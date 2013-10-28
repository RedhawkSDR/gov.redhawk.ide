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

import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.KeywordsType;

import mil.jpeojtrs.sca.prf.Properties;
import org.eclipse.emf.ecore.util.FeatureMap;

/**
 * A sample validator interface for {@link gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SRI}.
 * This doesn't really do anything, and it's not a real EMF artifact.
 * It was generated by the org.eclipse.emf.examples.generator.validator plug-in to illustrate how EMF's code generator can be extended.
 * This can be disabled with -vmargs -Dorg.eclipse.emf.examples.generator.validator=false.
 */
public interface SRIValidator
{
	boolean validate();

	boolean validateMixed(FeatureMap value);
	boolean validateHversion(int value);
	boolean validateXstart(double value);
	boolean validateXdelta(double value);
	boolean validateXunits(short value);
	boolean validateSubsize(double value);
	boolean validateYstart(double value);
	boolean validateYdelta(double value);
	boolean validateYunits(short value);
	boolean validateMode(short value);
	boolean validateStreamID(String value);
	boolean validateBlocking(boolean value);
	boolean validateKeywords(Properties value);

	boolean validateKeywords(KeywordsType value);
}
