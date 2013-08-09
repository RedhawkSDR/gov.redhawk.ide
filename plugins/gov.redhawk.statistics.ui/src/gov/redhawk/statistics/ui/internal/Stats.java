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

import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math.stat.descriptive.UnivariateStatistic;
import org.apache.commons.math.stat.descriptive.rank.Median;

public class Stats extends DescriptiveStatistics {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9047657894722901933L;
	public static final String MINIMUM = "Min", MAXIMUM = "Max", MEDIAN = "Median", MEAN = "Mean", STD_DEV = "Std Dev", NUM = "Samples";

	private UnivariateStatistic medianImpl = new Median();

	private final int length;

	public Stats(double[] initialDoubleArray) {
		for (double d : initialDoubleArray) {
			addValue(d);
		}
		length = initialDoubleArray.length;
	}

	public double getMedian() {
		return apply(medianImpl);

	}

	public int getLength() {
		return length;
	}

	public Number getStat(String key) {
		if (MINIMUM.equals(key)) {
			return getMin();
		} else if (MAXIMUM.equals(key)) {
			return getMax();
		} else if (MEAN.equals(key)) {
			return getMean();
		} else if (MEDIAN.equals(key)) {
			return getMedian();
		} else if (STD_DEV.equals(key)) {
			return getStandardDeviation();
		} else if (NUM.equals(key)) {
			return getLength();
		}
		return null;
	}

}
