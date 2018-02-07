/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.datalist.ui.views;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;

import BULKIO.PrecisionUTCTime;
import gov.redhawk.bulkio.util.BulkIOFormatter;
import gov.redhawk.datalist.ui.DataCollectionSettings;
import gov.redhawk.datalist.ui.DataListPlugin;
import gov.redhawk.datalist.ui.internal.DataCourier;
import gov.redhawk.datalist.ui.internal.DataCourierReceiver;
import gov.redhawk.datalist.ui.internal.IDataCourierListener;
import gov.redhawk.datalist.ui.internal.TableToolTipSupport;
import gov.redhawk.ide.snapshot.ui.SnapshotJob;
import gov.redhawk.ide.snapshot.ui.SnapshotWizard;
import gov.redhawk.model.sca.ScaAbstractComponent;
import gov.redhawk.model.sca.ScaUsesPort;
import gov.redhawk.model.sca.ScaWaveform;
import gov.redhawk.sca.ui.MultiOutConnectionWizard;

/**
 * @since 2.0
 */
public class DataListView extends ViewPart {
	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "gov.redhawk.datalist.ui.views.DataListView";

	public static final String REAL = "Real Value", IMAGINARY = "Imaginary Value", COMPLEX = "Complex";

	private final DataCourier dataCourier;

	private OptionsComposite input;
	private Table table;
	private Button chartButton, snapshotButton;
	private ProgressBar loading;
	private Composite tableComposite;
	private NumberFormat indexFormatter;
	private int prevCols;

	/**
	 * This listener waits for the data courier to notify it of its completion, and then sets the number of data rows
	 * on the table.
	 */
	private IDataCourierListener listener = new IDataCourierListener() {
		private WorkbenchJob completeJob = new WorkbenchJob("Refresh Viewer...") {

			{
				setUser(false);
				setSystem(true);
			}

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (DataListView.this.table != null && !DataListView.this.table.isDisposed()) {
					stopAcquire();
					table.setItemCount(dataCourier.getSize());
				}
				return Status.OK_STATUS;
			}
		};

		@Override
		public void dataChanged() {
		}

		@Override
		public void complete() {
			completeJob.schedule(100);
		}
	};


	public DataListView() {
		dataCourier = new DataCourier();
		indexFormatter = NumberFormat.getIntegerInstance();
	}

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

		this.tableComposite = new Composite(parent, SWT.NONE);
		this.tableComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());
		createTable(1);

		Composite bottom = new Composite(parent, SWT.NONE);
		bottom.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		bottom.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());

		loading = new ProgressBar(bottom, SWT.HORIZONTAL | SWT.INDETERMINATE);
		loading.setVisible(false);
		loading.setLayoutData(GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.CENTER).grab(true, false).create());

		snapshotButton = new Button(bottom, SWT.NONE);
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

		chartButton = new Button(bottom, SWT.NONE);
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
		// Dispose the previous table, if any
		if (this.table != null) {
			this.table.dispose();
			this.table = null;
		}

		TableColumnLayout tableLayout = new TableColumnLayout();
		this.tableComposite.setLayout(tableLayout);

		// We use a SWT.VIRTUAL table so we can populate the rows in the table as they're displayed
		this.table = new Table(this.tableComposite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.VIRTUAL | SWT.FULL_SELECTION);
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		// Index column
		final TableColumn indexColumn = new TableColumn(this.table, SWT.CENTER);
		indexColumn.setText("Sample ID");
		indexColumn.setResizable(true);
		indexColumn.setMoveable(false);
		tableLayout.setColumnData(indexColumn, new ColumnPixelData(100, true));

		// Data column(s)
		for (int i = 0; i < numColumns; i++) {
			final TableColumn dataColumn = new TableColumn(this.table, SWT.CENTER);
			if (numColumns == 2) {
				String text = (i == 0) ? REAL : IMAGINARY;
				dataColumn.setText(text);
			} else {
				dataColumn.setText("Value [" + i + "]");
			}
			dataColumn.setResizable(true);
			dataColumn.setMoveable(false);
			tableLayout.setColumnData(dataColumn, new ColumnWeightData(100, 75, true));
		}

		// Provide labels, tooltips for a SWT.VIRTUAL table
		this.table.addListener(SWT.SetData, event -> {
			TableItem item = (TableItem) event.item;

			// Sample ID
			item.setText(0, indexFormatter.format(event.index));

			// Data column(s)
			Object[] sample = dataCourier.getBuffer().getSample(event.index);
			for (int i = 0; i < numColumns; i++) {
				item.setText(i + 1, sample[i].toString());
			}
		});
		TableToolTipSupport.enableFor(table, element -> {
			String textIndex = ((TableItem) element).getText();
			if (textIndex.isEmpty()) {
				return "";
			}
			int index = Integer.valueOf(textIndex);

			PrecisionUTCTime time = dataCourier.getBuffer().getSampleTime(index);
			StringBuilder sb = new StringBuilder("Sample: ");
			sb.append(indexFormatter.format(index));
			sb.append('\n');
			if (time != null) {
				sb.append("Sample time: ");
				sb.append(BulkIOFormatter.toISO8601(time));
				sb.append('\n');
			}
			return sb.toString();
		});

		this.table.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).span(6, 1).create());
		dataCourier.addListener(listener);
		tableComposite.layout(true);
	}

	protected void startAcquire(DataCollectionSettings settings) {
		ScaUsesPort source = dataCourier.getSource();
		if (source == null || source.isDisposed()) {
			MessageDialog.openError(getSite().getShell(), "Resource Not Valid", "The resouce is no longer valid.");
			return;
		}

		// Check if multi-out port, and if so get a connection ID from the user
		String connectionId = null;
		Map<String, Boolean> connectionIDTable = ScaUsesPort.Util.getConnectionIds(source);
		if (connectionIDTable.size() > 0) {
			connectionId = getConnectionId(connectionIDTable);
			if (connectionId == null) {
				return;
			}
		}

		EObject container = source.eContainer();
		Boolean started = null;
		if (container instanceof ScaAbstractComponent< ? >) {
			ScaAbstractComponent< ? > comp = (ScaAbstractComponent< ? >) container;
			started = comp.getStarted();
		} else if (container instanceof ScaWaveform) {
			ScaWaveform waveform = (ScaWaveform) container;
			started = waveform.getStarted();
		}
		if (started == null || !started) {
			if (!MessageDialog.openQuestion(getSite().getShell(), "Resource Not Started",
				"The resource is not started, and will not produce output data. \n\nDo you wish to continue?")) {
				return;
			}
		}

		// Re-create the table if the number of columns has changed; empty the data
		int columns = settings.getDimensions();
		if (prevCols != columns) {
			createTable(columns);
			prevCols = columns;
		}
		table.setItemCount(0);

		dataCourier.acquire(settings, connectionId);

		setButtons(true);
	}

	private void stopAcquire() {
		dataCourier.stop();

		setButtons(false);
	}

	public void setButtons(boolean running) {
		chartButton.setEnabled(!running && dataCourier.getSize() > 0);
		snapshotButton.setEnabled(!running && dataCourier.getSize() > 0);
		loading.setVisible(running);
		input.buttonsEnable(running);
	}

	@Override
	public void dispose() {
		super.dispose();
		this.dataCourier.dispose();
	}

	public void setInput(final ScaUsesPort port) {
		try {
			dataCourier.setSource(port);
		} catch (final Exception e) { // SUPPRESS CHECKSTYLE Logged Catch all exception
			setContentDescription(e.getMessage());
			if (this.table != null) {
				this.table.setEnabled(false);
			}
		}
	}

	@Override
	public void setFocus() {
		input.setFocus();
	}

	/**
	 * Checks if the port is a multi-out port and if so gives the user the option to define the connection ID
	 * @return False if the data acquire operation was canceled
	 */
	private String getConnectionId(Map<String, Boolean> connectionIds) {
		// Check if port is a multi-out port, and if it has an available connection ID
		Entry<String, Boolean> firstEntry = connectionIds.entrySet().iterator().next();
		String connectionId = null;
		if (connectionIds.size() == 1 && firstEntry.getValue()) {
			connectionId = firstEntry.getKey();
		} else {
			MultiOutConnectionWizard dialog = new MultiOutConnectionWizard(Display.getDefault().getActiveShell(), connectionIds);
			if (Window.CANCEL == dialog.open() || dialog.getSelectedId() == null) {
				return null;
			}
			connectionId = dialog.getSelectedId();
		}

		return connectionId;
	}
}
