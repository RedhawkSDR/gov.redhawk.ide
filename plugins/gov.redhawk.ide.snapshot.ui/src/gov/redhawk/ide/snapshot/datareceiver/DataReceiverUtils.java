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
package gov.redhawk.ide.snapshot.datareceiver;


import mil.jpeojtrs.sca.util.AnyUtils;
import BULKIO.StreamSRI;
import CF.DataType;
import CF.DataTypeHelper;

public final class DataReceiverUtils {

	private DataReceiverUtils() {
	}
	
	/**
     * This method checks to see if all of the values in the StreamSRIs match
     * @param sri1
     * @param sri2
     * @return true if they match, false if not
     */
    public static boolean isSRIChanged(StreamSRI sri1, StreamSRI sri2) {
    	boolean match = true;
    	if (sri1 == null || sri2 == null) {
    		if ((sri1 == null && sri2 != null) || (sri2 == null && sri1 != null)) {
    			match = false;
    		}
    	} else if (sri1.xstart != sri2.xstart || sri1.xdelta != sri2.xdelta 
    			|| sri1.xunits != sri2.xunits) {
    		match = false;
    	} else if (sri1.ystart != sri2.ystart || sri1.ydelta != sri2.ydelta 
    			|| sri1.yunits != sri2.yunits) {
    		match = false;
    	} else if (sri1.hversion != sri2.hversion || sri1.subsize != sri2.subsize 
    			|| sri1.mode != sri2.mode) {
    		match = false;
    	} else if (sri1.blocking != sri2.blocking || !sri1.streamID.equals(sri2.streamID) 
    			|| sri1.keywords.length != sri2.keywords.length) {
    		match = false;
    	} else {
    		int i = 0;
    		while (i < sri1.keywords.length && match) {
    			if (!sri1.keywords[i].id.equals(sri2.keywords[i].id) 
    					|| !sri1.keywords[i].value.equals(sri2.keywords[i].value)) {
    				match = false;
    			}
    			i++;
    		}
    	}
    	return !match;
    }
	
	public static String [] readCorbaAny(DataType keyword, String [] valueString) {
    	Object temp;
    	if (valueString == null || valueString.length < 2) {
    		valueString = new String [2];
    		valueString[0] = "";
    		valueString[1] = "";
    	}
    	if (!valueString[0].equals("")) {
			valueString[0] += "::";
		}
    	valueString[0] += keyword.id;
    	if (DataTypeHelper.type().equivalent(keyword.value.type())) {
    		temp = DataTypeHelper.extract(keyword.value);
    	} else {
    		temp = AnyUtils.convertAny(keyword.value);
    	}
    	if (temp instanceof DataType) {
    		valueString = readCorbaAny((DataType) (temp), valueString);
    	} else {
    		//valueString[1] = getAnyTypeDataType(temp) + " " + temp.toString();
    		valueString[1] = temp.getClass().getName() + " " + temp.toString();
    	}
    	return valueString;
    }
	
}
