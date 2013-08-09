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
package gov.redhawk.statistics.ui.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.general.DatasetChangeEvent;
import org.jfree.data.statistics.HistogramBin;
import org.jfree.data.statistics.HistogramType;
import org.jfree.data.xy.AbstractIntervalXYDataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.util.ObjectUtilities;
import org.jfree.util.PublicCloneable;

public class DatalistDataset extends AbstractIntervalXYDataset implements IntervalXYDataset, Cloneable, PublicCloneable, Serializable {
	private static final long serialVersionUID = 2344821917159844514L;

	/** A list of maps. */
	private List<Map< ? , ? >> list;

	/** The histogram type. */
	private HistogramType type;

	/**
	 * Creates a new (empty) dataset with a default type of
	 * {@link HistogramType}.FREQUENCY.
	 */
	public DatalistDataset() {
		this.list = new ArrayList<Map< ? , ? >>();
		this.type = HistogramType.FREQUENCY;
	}

	/**
	 * Returns the histogram type.
	 *
	 * @return The type (never <code>null</code>).
	 */
	public HistogramType getType() {
		return this.type;
	}

	/**
	 * Sets the histogram type and sends a {@link DatasetChangeEvent} to all
	 * registered listeners.
	 *
	 * @param type  the type (<code>null</code> not permitted).
	 */
	public void setType(HistogramType type) {
		if (type == null) {
			throw new IllegalArgumentException("Null 'type' argument");
		}
		this.type = type;
		notifyListeners(new DatasetChangeEvent(this, this));
	}

	/**
	 * Adds a series to the dataset, using the specified number of bins.
	 *
	 * @param key  the series key (<code>null</code> not permitted).
	 * @param values the values (<code>null</code> not permitted).
	 * @param bins  the number of bins (must be at least 1).
	 */
	public void addSeries(Comparable< ? > key, double[] values, int bins) {
		// defer argument checking...
		double minimum = getMinimum(values);
		double maximum = getMaximum(values);
		addSeries(key, values, bins, minimum, maximum);
	}

	/**
	 * Adds a series to the dataset. Any data value less than minimum will be
	 * assigned to the first bin, and any data value greater than maximum will
	 * be assigned to the last bin.  Values falling on the boundary of
	 * adjacent bins will be assigned to the higher indexed bin.
	 *
	 * @param key  the series key (<code>null</code> not permitted).
	 * @param values  the raw observations.
	 * @param bins  the number of bins (must be at least 1).
	 * @param minimum  the lower bound of the bin range.
	 * @param maximum  the upper bound of the bin range.
	 */
	public void addSeries(Comparable< ? > key, double[] values, int bins, double minimum, double maximum) {

		if (key == null) {
			throw new IllegalArgumentException("Null 'key' argument.");
		}
		if (values == null) {
			throw new IllegalArgumentException("Null 'values' argument.");
		} else if (bins < 1) {
			throw new IllegalArgumentException("The 'bins' value must be at least 1.");
		}
		double binWidth = (maximum - minimum) / bins;

		double lower = minimum;
		double upper;
		List<HistogramBin> binList = new ArrayList<HistogramBin>(bins);
		for (int i = 0; i < bins; i++) {
			HistogramBin bin;
			// make sure bins[bins.length]'s upper boundary ends at maximum
			// to avoid the rounding issue. the bins[0] lower boundary is
			// guaranteed start from min
			if (i == bins - 1) {
				bin = new HistogramBin(lower, maximum);
			} else {
				upper = minimum + (i + 1) * binWidth;
				bin = new HistogramBin(lower, upper);
				lower = upper;
			}
			binList.add(bin);
		}
		// fill the bins
		for (int i = 0; i < values.length; i++) {
			int binIndex = bins - 1;
			if (values[i] < maximum) {
				double fraction = (values[i] - minimum) / (maximum - minimum);
				if (fraction < 0.0) {
					fraction = 0.0;
				}
				binIndex = (int) (fraction * bins);
				// rounding could result in binIndex being equal to bins
				// which will cause an IndexOutOfBoundsException - see bug
				// report 1553088
				if (binIndex >= bins) {
					binIndex = bins - 1;
				}
			}
			HistogramBin bin = binList.get(binIndex);
			bin.incrementCount();
		}
		// generic map for each series
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key", key);
		map.put("bins", binList);
		map.put("values.length", new Integer(values.length));
		map.put("bin width", new Double(binWidth));
		this.list.add(map);

		notifyListeners(new DatasetChangeEvent(this, this));
	}

	public void removeAllSeries() {
		this.list.clear();
	}

	/**
	 * Returns the minimum value in an array of values.
	 *
	 * @param values  the values (<code>null</code> not permitted and
	 *                zero-length array not permitted).
	 *
	 * @return The minimum value.
	 */
	private double getMinimum(double[] values) {
		if (values == null || values.length < 1) {
			throw new IllegalArgumentException("Null or zero length 'values' argument.");
		}
		double min = Double.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] < min) {
				min = values[i];
			}
		}
		return min;
	}

	/**
	 * Returns the maximum value in an array of values.
	 *
	 * @param values  the values (<code>null</code> not permitted and
	 *                zero-length array not permitted).
	 *
	 * @return The maximum value.
	 */
	private double getMaximum(double[] values) {
		if (values == null || values.length < 1) {
			throw new IllegalArgumentException("Null or zero length 'values' argument.");
		}
		double max = -Double.MAX_VALUE;
		for (int i = 0; i < values.length; i++) {
			if (values[i] > max) {
				max = values[i];
			}
		}
		return max;
	}

	/**
	 * Returns the bins for a series.
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 *
	 * @return A list of bins.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	List< ? > getBins(int series) {
		Map< ? , ? > map = this.list.get(series);
		return (List< ? >) map.get("bins");
	}

	/**
	 * Returns the total number of observations for a series.
	 *
	 * @param series  the series index.
	 *
	 * @return The total.
	 */
	private int getTotal(int series) {
		Map< ? , ? > map = this.list.get(series);
		return ((Integer) map.get("values.length")).intValue();
	}

	/**
	 * Returns the bin width for a series.
	 *
	 * @param series  the series index (zero based).
	 *
	 * @return The bin width.
	 */
	private double getBinWidth(int series) {
		Map< ? , ? > map = this.list.get(series);
		return ((Double) map.get("bin width")).doubleValue();
	}

	/**
	 * Returns the number of series in the dataset.
	 *
	 * @return The series count.
	 */
	public int getSeriesCount() {
		return this.list.size();
	}

	/**
	 * Returns the key for a series.
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 *
	 * @return The series key.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Comparable< ? > getSeriesKey(int series) {
		Map< ? , ? > map = this.list.get(series);
		return (Comparable< ? >) map.get("key");
	}

	/**
	 * Returns the number of data items for a series.
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 *
	 * @return The item count.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public int getItemCount(int series) {
		return getBins(series).size();
	}

	/**
	 * Returns the X value for a bin.  This value won't be used for plotting
	 * histograms, since the renderer will ignore it.  But other renderers can
	 * use it (for example, you could use the dataset to create a line
	 * chart).
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 * @param item  the item index (zero based).
	 *
	 * @return The start value.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Number getX(int series, int item) {
		List< ? > bins = getBins(series);
		HistogramBin bin = (HistogramBin) bins.get(item);
		double x = (bin.getStartBoundary() + bin.getEndBoundary()) / 2.;
		return new Double(x);
	}

	/**
	 * Returns the y-value for a bin (calculated to take into account the
	 * histogram type).
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 * @param item  the item index (zero based).
	 *
	 * @return The y-value.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Number getY(int series, int item) {
		List< ? > bins = getBins(series);
		HistogramBin bin = (HistogramBin) bins.get(item);
		double total = getTotal(series);
		double binWidth = getBinWidth(series);

		if (this.type == HistogramType.FREQUENCY) {
			return new Double(bin.getCount());
		} else if (this.type == HistogramType.RELATIVE_FREQUENCY) {
			return new Double(bin.getCount() / total);
		} else if (this.type == HistogramType.SCALE_AREA_TO_1) {
			return new Double(bin.getCount() / (binWidth * total));
		} else { // pretty sure this shouldn't ever happen
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the start value for a bin.
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 * @param item  the item index (zero based).
	 *
	 * @return The start value.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Number getStartX(int series, int item) {
		List< ? > bins = getBins(series);
		HistogramBin bin = (HistogramBin) bins.get(item);
		return new Double(bin.getStartBoundary());
	}

	/**
	 * Returns the end value for a bin.
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 * @param item  the item index (zero based).
	 *
	 * @return The end value.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Number getEndX(int series, int item) {
		List< ? > bins = getBins(series);
		HistogramBin bin = (HistogramBin) bins.get(item);
		return new Double(bin.getEndBoundary());
	}

	/**
	 * Returns the start y-value for a bin (which is the same as the y-value,
	 * this method exists only to support the general form of the
	 * {@link IntervalXYDataset} interface).
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 * @param item  the item index (zero based).
	 *
	 * @return The y-value.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Number getStartY(int series, int item) {
		return getY(series, item);
	}

	/**
	 * Returns the end y-value for a bin (which is the same as the y-value,
	 * this method exists only to support the general form of the
	 * {@link IntervalXYDataset} interface).
	 *
	 * @param series  the series index (in the range <code>0</code> to
	 *     <code>getSeriesCount() - 1</code>).
	 * @param item  the item index (zero based).
	 *
	 * @return The Y value.
	 *
	 * @throws IndexOutOfBoundsException if <code>series</code> is outside the
	 *     specified range.
	 */
	public Number getEndY(int series, int item) {
		return getY(series, item);
	}

	/**
	 * Tests this dataset for equality with an arbitrary object.
	 *
	 * @param obj  the object to test against (<code>null</code> permitted).
	 *
	 * @return A boolean.
	 */
	public boolean equalTo(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DatalistDataset)) {
			return false;
		}
		DatalistDataset that = (DatalistDataset) obj;
		if (!ObjectUtilities.equal(this.type, that.type) || !ObjectUtilities.equal(this.list, that.list)) {
			return false;
		}
		return true;
	}

	/**
	 * Returns a clone of the dataset.
	 *
	 * @return A clone of the dataset.
	 *
	 * @throws CloneNotSupportedException if the object cannot be cloned.
	 */
	public Object clone() throws CloneNotSupportedException {
		DatalistDataset clone = (DatalistDataset) super.clone();
		int seriesCount = getSeriesCount();
		clone.list = new ArrayList<Map< ? , ? >>(seriesCount);
		for (int i = 0; i < seriesCount; i++) {
			clone.list.add(new HashMap<Object, Object>(this.list.get(i)));
		}
		return clone;
	}

}
