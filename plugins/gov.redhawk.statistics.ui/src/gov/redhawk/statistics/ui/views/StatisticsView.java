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
package gov.redhawk.statistics.ui.views;

import gov.redhawk.statistics.ui.internal.CustomAction;
import gov.redhawk.statistics.ui.internal.DatalistDataset;
import gov.redhawk.statistics.ui.internal.SettingsDialog;
import gov.redhawk.statistics.ui.internal.Stats;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.WorkbenchJob;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.ClusteredXYBarRenderer;
import org.jfree.chart.renderer.xy.StandardXYBarPainter;
import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.xy.XYDataset;

public class StatisticsView extends ViewPart {

	public static final String ID = "gov.redhawk.statistics.ui.views.StatisticsView";

	private static final String[] STAT_PROPS = { Stats.MINIMUM, Stats.MAXIMUM, Stats.MEDIAN, Stats.MEAN, Stats.STD_DEV, Stats.NUM };

	private Label[] labels = new Label[STAT_PROPS.length];

	private Number[][] datalist;

	private JFreeChart chart;

	private Stats[] stats;

	private Stats magnitudeStats;

	private Composite parent;

	private Section section;

	private DatalistDataset dataSet = new DatalistDataset();

	private int curIndex = -1;

	private int numBars = 4;

	private final List<DisposeListener> listeners = Collections.synchronizedList(new ArrayList<DisposeListener>());

	private WorkbenchJob refreshJob = new WorkbenchJob("Refreshing Data and Composite") {
		{
			setSystem(true);
			setUser(false);
		}

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			refresh();
			return Status.OK_STATUS;
		}
	};

	/**
	 * the constructor
	 */
	public StatisticsView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize it.
	 */
	@Override
	public void createPartControl(Composite comp) {

		parent = comp;
		parent.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).numColumns(1).create());

		// Custom Action for the View's Menu
		CustomAction customAction = new CustomAction() {

			@Override
			public void run() {
				SettingsDialog dialog = new SettingsDialog(parent.getShell(), datalist.length, curIndex, numBars);
				dialog.create();
				if (dialog.open() == Window.OK) {
					numBars = dialog.getNumBars();
					curIndex = dialog.getSelectedIndex();
					refreshJob.schedule();
				}
			}
		};
		customAction.setText("Settings");
		getViewSite().getActionBars().getMenuManager().add(customAction);

		// creation of chart composite and selection of associated options
		Composite chartComposite = new Composite(parent, SWT.EMBEDDED);
		chartComposite.setLayoutData(GridDataFactory.fillDefaults().grab(true, true).create());

		chart = ChartFactory.createXYBarChart(null, null, false, null, dataSet, PlotOrientation.VERTICAL, false, true, false);

		org.eclipse.swt.graphics.Color backgroundColor = chartComposite.getBackground();
		chart.setBackgroundPaint(new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue()));
		chart.getXYPlot().setBackgroundPaint(ChartColor.WHITE);

		Frame chartFrame = SWT_AWT.new_Frame(chartComposite);
		chartFrame.setBackground(new Color(backgroundColor.getRed(), backgroundColor.getGreen(), backgroundColor.getBlue()));
		chartFrame.setLayout(new GridLayout());

		ChartPanel jFreeChartPanel = new ChartPanel(chart);
		chartFrame.add(jFreeChartPanel);

		ClusteredXYBarRenderer renderer = new ClusteredXYBarRenderer();
		renderer.setBarPainter(new StandardXYBarPainter());
		renderer.setMargin(0.05);
		renderer.setShadowVisible(false);
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelGenerator(new XYItemLabelGenerator() {
			@Override
			public String generateLabel(XYDataset dataset, int series, int item) {
				return String.valueOf((int) (dataset.getYValue(series, item)));
			}
		});
		renderer.setBasePaint(new Color(139, 0, 0));
		renderer.setLegendItemLabelGenerator(new XYSeriesLabelGenerator() {

			@Override
			public String generateLabel(XYDataset ds, int i) {
				if (ds.getSeriesCount() == 2) {
					if (i == 0) {
						return "Real";
					} else if (i == 1) {
						return "Imaginary";
					} else {
						return "Complex";
					}
				} else if (ds.getSeriesCount() > 1) {
					return "Dimension " + i;
				}

				return null;
			}
		});
		chart.getXYPlot().setRenderer(renderer);

		dataSet.addChangeListener(new DatasetChangeListener() {

			@Override
			public void datasetChanged(DatasetChangeEvent event) {
				chart.getPlot().datasetChanged(event);

			}
		});

		// creation of the statistics composite
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		section = toolkit.createSection(parent, Section.DESCRIPTION | Section.NO_TITLE | Section.CLIENT_INDENT);
		section.setBackground(parent.getBackground());
		section.setDescription("");
		section.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create()); // layout within parent

		// Composite for storing the data
		Composite composite = toolkit.createComposite(section, SWT.WRAP);
		composite.setBackground(parent.getBackground());
		composite.setLayout(GridLayoutFactory.fillDefaults().margins(10, 10).numColumns(4).create());
		composite.setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create()); // layout within parent
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		for (int j = 0; j < STAT_PROPS.length; j++) {
			Label label = new Label(composite, SWT.None);
			label.setText(STAT_PROPS[j] + ":");
			labels[j] = new Label(composite, SWT.None);
			labels[j].setLayoutData(GridDataFactory.fillDefaults().grab(true, false).create());
		}

	}

	private void refresh() {
			Job job = new Job("Update Chart..") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					createStatsArray();
					PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

						@Override
						public void run() {
							updateStatsLabels(curIndex);
						}
						
					});
					
					setAllCategories();
					
					return Status.OK_STATUS;
				}
				
			};
			job.schedule();
		}

	private void createStatsArray() {
		stats = new Stats[datalist.length];
		for (int i = 0; i < stats.length; i++) {
			stats[i] = new Stats(datalist[i]);
		}
		magnitudeStats = new Stats(magArray());
	}

	private void updateStatsLabels(int i) {
		int showIndex = i;
		if (datalist.length == 1) {
			showIndex = 0;
		}
		Stats s;
		if (showIndex < 0) {
			s = magnitudeStats;
		} else {
			s = stats[showIndex];
		}

		for (int j = 0; j < STAT_PROPS.length; j++) {
			DecimalFormat form;
			double value = s.getStat(STAT_PROPS[j]).doubleValue();
			if (STAT_PROPS[j].equals(Stats.NUM)) {
				form = new DecimalFormat();
			} else if (value != 0 && (Math.abs(value) * 10 < 1 || Math.abs(value) / 10 > 99)) {
				form = new DecimalFormat("0.0#E0");
			} else {
				form = new DecimalFormat();
				form.setMaximumFractionDigits(3);
				form.setMaximumIntegerDigits(2);
			}
			labels[j].setText(form.format(value));
		}
		section.setDescription(getCategoryName(showIndex));
	}

	private void setAllCategories() {
		dataSet.removeAllSeries();
		if (curIndex >= 0) {
			dataSet.addSeries(getCategoryName(curIndex), doubleArray(curIndex), numBars);
		} else {
			for (int i = 0; i < datalist.length; i++) {
				dataSet.addSeries(getCategoryName(i), doubleArray(i), numBars);
			}
		}
	}

	private String getCategoryName(int i) {
		if (i < 0) {
			return "Complex (statistics calculated using magnitude)";
		} else if (datalist.length == 2) {
			if (i == 0) {
				return "Real";
			} else {
				return "Imaginary";
			}
		} else if (datalist.length == 1) {
			return "";
		}
		return "Dimension " + i;

	}

	/**
	 * @since 2.0
	 */
	public void setInput(Number[][] datalist) {
		this.datalist = datalist;
		refreshJob.schedule();
	}

	private double[] doubleArray(int index) {
		Number[] series = datalist[index];

		double[] array = new double[series.length];
		for (int i = 0; i < array.length; i++) {
			array[i] = series[i].doubleValue();
		}
		return array;
	}

	private Number[] magArray() {
		Number[] array = new Number[datalist.length];
		for (int i = 0; i < datalist.length; i++) {
			array[i] = findMagnitude(datalist[i]);
		}
		return array;
	}

	private Number findMagnitude(Number[] nums) {
		double n = 0;
		for (int i = 0; i < nums.length; i++) {
			n += Math.pow(nums[i].doubleValue(), 2);
		}
		return Math.sqrt(n);
	}

	public void setIndex(int i) {
		this.curIndex = i;
		refreshJob.schedule();
	}

	public void setNumBars(int i) {
		this.numBars = i;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
	}

	@Override
	public void dispose() {
		super.dispose();
		for (DisposeListener listener : listeners) {
			listener.widgetDisposed(null);
		}
	}

	public void addDisposeListener(DisposeListener listener) {
		if (listeners.contains(listener)) {
			return;
		}
		listeners.add(listener);
	}

	public void removeDisposeListener(DisposeListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}
}
