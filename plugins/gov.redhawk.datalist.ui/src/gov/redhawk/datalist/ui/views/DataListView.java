/******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package gov.redhawk.datalist.ui.views;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.internal.DataBuffer;
import gov.redhawk.datalist.ui.internal.DataCollectionSettings;
import gov.redhawk.datalist.ui.internal.DataCourierReceiver;
import gov.redhawk.datalist.ui.internal.IDataBufferListener;
import gov.redhawk.datalist.ui.internal.IFullListener;
import gov.redhawk.datalist.ui.internal.Sample;
import gov.redhawk.ide.snapshot.ui.SnapshotJob;
import gov.redhawk.ide.snapshot.ui.SnapshotWizard;
import gov.redhawk.model.sca.ScaUsesPort;

import java.lang.reflect.Array;
import java.text.DecimalFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

import BULKIO.PrecisionUTCTime;

public class DataListView extends ViewPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.redhawk.datalist.ui.views.DataListView";

	public static final String REAL = "Real", IMAGINARY = "Imaginary", COMPLEX = "Complex";

	private final DataCourier dataCourier;

	private OptionsComposite input;

	private TableViewer viewer;

	private BulkIOType type;

	private DataBuffer buffer;

	private Button chartButton, snapshotButton;

	private ProgressBar loading;

	private Composite tableComposite;

	private int prevCols;

	private final WorkbenchJob refreshJob = new WorkbenchJob("Refreshing View") {
		{
			setSystem(true);
			setUser(false);
		}

		@Override
		public IStatus runInUIThread(final IProgressMonitor monitor) {
			if (DataListView.this.viewer != null && !DataListView.this.viewer.getControl().isDisposed()) {
				DataListView.this.viewer.refresh();
				for (Sample s : buffer.getList()) {
					dataCourier.addToList(s);
				}
			}
			return Status.OK_STATUS;
		}
	};

	private final IDataBufferListener listener = new IDataBufferListener() {
		@Override
		public void dataBufferChanged(final DataBuffer d) {
			DataListView.this.refreshJob.schedule();

		}
	};

	static class ViewContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(final Object parent) {
			if (parent instanceof DataCourier) {
				return ((DataCourier) parent).getList().toArray();
			}
			return ((DataBuffer) parent).getDataBuffer().toArray();
		}

	}

	abstract static class PrecisionTimeTooltipProvider extends ColumnLabelProvider {
		@Override
		public int getToolTipDisplayDelayTime(final Object object) {
			return 50;
		}

		@Override
		public int getToolTipTimeDisplayed(final Object object) {
			return 5000;
		}

		@Override
		public String getToolTipText(final Object element) {
			if (element instanceof Sample) {
				final Sample s = (Sample) element;
				final PrecisionUTCTime time = s.getTime();
				return "TC Mode: " + time.tcmode + "\n" + "TC Status: " + time.tcstatus + "\n" + "TF Sec: " + time.tfsec + "\n" + "T Off: " + time.toff + "\n"
					+ "TW Sec: " + time.twsec;
			}
			return super.getToolTipText(element);
		}
	}

	static class IndexColumnLabelProvider extends PrecisionTimeTooltipProvider {

		@Override
		public String getText(final Object element) {
			if (element instanceof Sample) {
				final Sample s = (Sample) element;
				return String.valueOf(s.getIndex());
			}
			return super.getText(element);
		}

	}

	private class ValueColumnLabelProvider extends PrecisionTimeTooltipProvider {

		private final int index;

		public ValueColumnLabelProvider(final int index) {
			this.index = index;
		}

		@Override
		public String getText(final Object element) {
			if (element instanceof Sample) {
				final Sample s = (Sample) element;
				final Object data = s.getData();

				Object value = null;

				if (data.getClass().isArray()) {
					if (Array.getLength(data) > this.index) {
						value = Array.get(data, this.index);
					} else {
						return "";
					}
				} else {
					value = data;
				}
				DecimalFormat form;
				if (value != null && value instanceof Number) {
					double doubleValue = ((Number) value).doubleValue();
					if (doubleValue != 0 && (Math.abs(doubleValue) * 10 < 1 || Math.abs(doubleValue) / 10 > 99)) {
						form = new DecimalFormat("0.0#E0");
					} else {
						form = new DecimalFormat();
						form.setMaximumFractionDigits(3);
						form.setMaximumIntegerDigits(2);
					}
					return form.format(value);
				}
			}
			return super.getText(element);
		}
	}

	/**
	 * The constructor.
	 */
	public DataListView() {
		dataCourier = new DataCourier();
		dataCourier.addFullListener(new IFullListener() {

			@Override
			public void fireIsFull(DataCourier courier) {
				setButtons(false); // is not collecting
				viewer.setItemCount(courier.getSize());
			}
		});
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(final Composite parent) {
		parent.setLayout(GridLayoutFactory.fillDefaults().margins(5, 10).numColumns(1).create());

		input = new OptionsComposite(parent) {
			@Override
			public void startAcquire() {
				DataListView.this.startAcquire(this.getSettings());
			}

			@Override
			public void stopAcquire() {
				DataListView.this.stopAcquire();

			}
		};

		this.tableComposite = new Composite(parent, SWT.None);
		this.tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		createTable(0);

		Composite bottom = new Composite(parent, SWT.None);
		bottom.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		bottom.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		loading = new ProgressBar(bottom, SWT.HORIZONTAL | SWT.INDETERMINATE);
		loading.setVisible(false);
		loading.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).create());

		snapshotButton = new Button(bottom, SWT.None);
		snapshotButton.setText("Save");
		snapshotButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).create());
		snapshotButton.setImage(DataListPlugin.imageDescriptorFromPlugin(DataListPlugin.PLUGIN_ID, "icons/save.gif").createImage());
		snapshotButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SnapshotWizard wizard = new SnapshotWizard();

				WizardDialog dialog = new WizardDialog(parent.getShell(), wizard);
				dialog.open();
				DataCourierReceiver receiver = new DataCourierReceiver(dataCourier);
				receiver.setDataWriter(wizard.getDataWriter());
				SnapshotJob job = new SnapshotJob("Data list snapshot", receiver);
				job.schedule();
			}
		});

		chartButton = new Button(bottom, SWT.None);
		chartButton.setImage(DataListPlugin.imageDescriptorFromPlugin(DataListPlugin.PLUGIN_ID, "icons/chart.gif").createImage());
		chartButton.setText("Chart");
		chartButton.setToolTipText("Chart Data List");
		chartButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataCourier.openStatisticsView(String.valueOf(hashCode()));
			}
		});
		chartButton.setLayoutData(GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).create());

		chartButton.setEnabled(false);
		snapshotButton.setEnabled(false);
	}

	protected void createTable(final int numColumns) {
		if (this.viewer != null) {
			this.viewer.getTable().dispose();
			this.viewer = null;
		}

		final TableColumnLayout layout = new TableColumnLayout();
		this.tableComposite.setLayout(layout);
		this.viewer = new TableViewer(this.tableComposite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		this.viewer.setContentProvider(new ViewContentProvider());
		this.viewer.getTable().setHeaderVisible(true);
		this.viewer.getTable().setLinesVisible(true);
		ColumnViewerToolTipSupport.enableFor(this.viewer);

		final TableViewerColumn indexColumn = new TableViewerColumn(this.viewer, SWT.CENTER);
		indexColumn.getColumn().setResizable(true);
		indexColumn.getColumn().setMoveable(false);
		indexColumn.getColumn().setWidth(50);

		indexColumn.getColumn().addSelectionListener(new SelectionListener() {

			/**
			 * Called when the index column's header is selected.
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				dataCourier.setSelectedIndex(DataCourier.ALL);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		indexColumn.setLabelProvider(new IndexColumnLabelProvider());
		layout.setColumnData(indexColumn.getColumn(), new ColumnPixelData(50, true));

		for (int i = 0; i < numColumns; i++) {
			final TableViewerColumn dataColumn = new TableViewerColumn(this.viewer, SWT.CENTER);
			dataColumn.getColumn().setResizable(true);
			dataColumn.getColumn().setMoveable(false);
			dataColumn.getColumn().setWidth(75);
			dataColumn.getColumn().setData(i); // i is the dimension of the data w/in this column 

			if (numColumns == 2) {
				if (i == 0) {
					dataColumn.getColumn().setText(REAL);
				} else {
					dataColumn.getColumn().setText(IMAGINARY);
				}
			}

			dataColumn.getColumn().addSelectionListener(new SelectionListener() {
				/**
				 * Called when the column header is selected.
				 */
				@Override
				public void widgetSelected(SelectionEvent e) {
					dataCourier.setSelectedIndex((Integer) dataColumn.getColumn().getData());
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
				}
			});

			dataColumn.setLabelProvider(new ValueColumnLabelProvider(i));
			layout.setColumnData(dataColumn.getColumn(), new ColumnWeightData(100, 75, true));
		}

		this.viewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(6, 1).create());
		if (this.buffer != null) {
			this.viewer.setInput(this.buffer);
		}

	}

	protected void startAcquire(DataCollectionSettings settings) {
		int columns = settings.getDimensions();
		double samples = settings.getSamples();

		if (dataCourier != null) {
			dataCourier.clear();
		}
		if (this.buffer != null) {
			this.viewer.setInput(this.buffer);
		}

		this.viewer.getTable().clearAll();
		if (prevCols != columns) {
			createTable(columns);
			prevCols = columns;
		}
		switch (OptionsComposite.CaptureMethod.stringToValue(settings.getProcessType())) {
		case NUMBER:
			dataCourier.setProperties(columns, (int) samples);
			this.viewer.getTable().setItemCount((int) samples);
			this.viewer.getTable().setData(false); //sample-number dependent
			break;
		default:
			dataCourier.setProperties(columns, 0);
			this.viewer.getTable().setItemCount(1);
			this.viewer.getTable().setData(true); //not sample-number dependent; increment when adding
			break;
		}

		this.viewer.refresh();

		this.buffer.clear();
		this.buffer.setDimension(columns);
		this.buffer.acquire(settings);

		final Point size = this.tableComposite.getSize();
		this.tableComposite.setSize(size.x + 1, size.y + 1);
		this.tableComposite.setSize(size);

		setButtons(true); //is running
		refreshJob.schedule();
	}

	private void stopAcquire() {
		this.buffer.disconnect();
		Sample[] elements = dataCourier.getList().toArray(new Sample[0]);
		dataCourier.setSize(elements.length);
		dataCourier.fireListIsFull();

		input.showSamples(elements.length);
		input.getSettings().setSamples(elements.length);
		viewer.getTable().removeAll();
		viewer.setInput(dataCourier);
		viewer.refresh();
		setButtons(false); // is no longer running
	}

	public void setButtons(boolean running) {
		chartButton.setEnabled(!running);
		snapshotButton.setEnabled(!running);
		loading.setVisible(running);
		if (input != null) {
			input.buttonsEnable(running);
		}

	}

	@Override
	public void dispose() {
		super.dispose();
		if (this.buffer != null) {
			this.buffer.dispose();
		}
	}

	public void setInput(final ScaUsesPort port) {
		if (this.buffer != null) {
			this.buffer.dispose();
			this.buffer = null;
		}
		try {
			type = BulkIOType.getType(port.getRepid());
			this.buffer = new DataBuffer(port, type);
			this.buffer.addDataBufferListener(this.listener);

			this.dataCourier.setType(type);
			if (this.viewer != null) {
				this.viewer.setInput(this.buffer);
			}
		} catch (final Exception e) {
			setContentDescription(e.getMessage());
			if (this.viewer != null) {
				this.viewer.getControl().setEnabled(false);
			}
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		if (this.input != null) {
			this.input.setFocus();
		}
	}
}
