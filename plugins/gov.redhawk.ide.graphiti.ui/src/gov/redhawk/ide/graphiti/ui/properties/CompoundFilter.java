/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.graphiti.ui.properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IFilter;

public class CompoundFilter implements IFilter {

	public enum BooleanOperator {FILTER_AND, FILTER_OR};
	private List<IFilter> subFilters;
	private BooleanOperator myOperator;
	
	public CompoundFilter() {
		this(BooleanOperator.FILTER_OR);
	}
	
	public CompoundFilter(BooleanOperator op) {
		this.subFilters = new ArrayList<IFilter>();
		this.myOperator = op;
	}

	@Override
	public boolean select(Object toTest) {
		switch (this.myOperator) {
		case FILTER_AND:
			for (IFilter filter: this.subFilters) {
				if (!filter.select(toTest)) {
					return false;
				}
			}
			return true;
		case FILTER_OR:
			for (IFilter filter: this.subFilters) {
				if (filter.select(toTest)) {
					return true;
				}
			}
			return false;
		default:
			return false;
		}
	}

	public void addFilter(IFilter filter) {
		this.subFilters.add(filter);
	}
	
	public void clearFilters() {
		this.subFilters.clear();
	}
	
}
