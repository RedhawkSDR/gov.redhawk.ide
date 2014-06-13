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
package gov.redhawk.mfile.parser.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class Function {
	private List<String> inputs = new ArrayList<String>();
	private Map<String, Number> inputDefaultValues = new HashMap<String, Number>();
	private List<String> outputs = new ArrayList<String>();
	private String name;
	
	public List<String> getInputs() {
		return inputs;
	}
	
	public List<String> getOutputs() {
		return outputs;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Map<String, Number> getInputDefaultValues() {
		return inputDefaultValues;
	}
}
