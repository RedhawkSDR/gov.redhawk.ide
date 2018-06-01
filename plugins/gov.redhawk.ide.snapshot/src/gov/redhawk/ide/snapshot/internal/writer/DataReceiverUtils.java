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
package gov.redhawk.ide.snapshot.internal.writer;

import mil.jpeojtrs.sca.util.AnyUtils;
import CF.DataType;
import CF.DataTypeHelper;

public final class DataReceiverUtils {

	private DataReceiverUtils() {
	}

	public static String[] readCorbaAny(DataType keyword, String[] valueString) {
		Object temp;
		if (valueString == null || valueString.length < 2) {
			valueString = new String[2];
			valueString[0] = "";
			valueString[1] = "";
		}
		if (!valueString[0].isEmpty()) {
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
			valueString[1] = temp.getClass().getName() + " " + temp.toString();
		}
		return valueString;
	}

}
