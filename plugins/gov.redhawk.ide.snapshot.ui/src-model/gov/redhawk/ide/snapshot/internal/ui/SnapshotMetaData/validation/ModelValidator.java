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
// BEGIN GENERATED CODE
package gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.validation;

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Time;

import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * A sample validator interface for {@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface ModelValidator
{
	boolean validate();

	boolean validateMixed(FeatureMap value);
	boolean validateNumberOfSamples(long value);
	boolean validateDataByteOrder(String value);
	boolean validateTime(Time value);
	boolean validateBulkIOType(String value);
	boolean validateStreamSRI(SRI value);
}
