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
package gov.redhawk.ide.spd.internal.ui;

import gov.redhawk.ui.util.EMFEmptyStringToNullUpdateValueStrategy;
import mil.jpeojtrs.sca.spd.Descriptor;
import mil.jpeojtrs.sca.spd.LocalFile;
import mil.jpeojtrs.sca.spd.PropertyFile;
import mil.jpeojtrs.sca.spd.SpdFactory;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.emf.databinding.EMFUpdateValueStrategy;

public class UpdateValueStrategyFactory {

	private UpdateValueStrategyFactory() {
	};

	/**
	 * @deprecated use EMFEditProperties.value(getEditingDomain(),
	 *             FeaturePath.fromList(SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
	 *             SpdPackage.Literals.PROPERTY_FILE__LOCAL_FILE, SpdPackage.Literals.LOCAL_FILE__NAME)).observe(model),
	 *             instead
	 * 
	 * @return
	 */
	@Deprecated
	public static UpdateValueStrategy createPrfModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(PropertyFile.class, String.class) {

			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return "";
				}
				final PropertyFile file = (PropertyFile) fromObject;
				final LocalFile lFile = file.getLocalFile();
				if (lFile == null) {
					return "";
				}
				return lFile.getName();
			}

		});
		return strategy;
	}

	/**
	 * @deprecated use EMFEditProperties.value(getEditingDomain(),
	 *             FeaturePath.fromList(SpdPackage.Literals.SOFT_PKG__PROPERTY_FILE,
	 *             SpdPackage.Literals.PROPERTY_FILE__LOCAL_FILE, SpdPackage.Literals.LOCAL_FILE__NAME)).observe(model),
	 *             instead
	 * 
	 * @return
	 */
	@Deprecated
	public static UpdateValueStrategy createPrfTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, PropertyFile.class) {

			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final PropertyFile file = SpdFactory.eINSTANCE.createPropertyFile();
				file.setType("PRF");
				final LocalFile lFile = SpdFactory.eINSTANCE.createLocalFile();
				lFile.setName(fromObject.toString());
				file.setLocalFile(lFile);
				return file;
			}

		});
		return strategy;
	}

	/**
	 * @deprecated use EMFEditProperties.value(getEditingDomain(),
	 *             FeaturePath.fromList(SpdPackage.Literals.SOFT_PKG__DESCRIPTOR,
	 *             SpdPackage.Literals.DESCRIPTOR__LOCALFILE, , SpdPackage.Literals.LOCAL_FILE__NAME)).observe(model),
	 *             instead
	 * 
	 * @return
	 */
	@Deprecated
	public static UpdateValueStrategy createScdModelToTarget() {
		final EMFUpdateValueStrategy strategy = new EMFUpdateValueStrategy();
		strategy.setConverter(new Converter(Descriptor.class, String.class) {

			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return "";
				}
				final Descriptor file = (Descriptor) fromObject;
				final LocalFile lFile = file.getLocalfile();
				if (lFile == null) {
					return "";
				}
				return lFile.getName();
			}

		});
		return strategy;
	}

	/**
	 * @deprecated use EMFEditProperties.value(getEditingDomain(),
	 *             FeaturePath.fromList(SpdPackage.Literals.SOFT_PKG__DESCRIPTOR,
	 *             SpdPackage.Literals.DESCRIPTOR__LOCALFILE, , SpdPackage.Literals.LOCAL_FILE__NAME)).observe(model),
	 *             instead
	 * 
	 * @return
	 */
	@Deprecated
	public static UpdateValueStrategy createScdTargetToModel() {
		final EMFEmptyStringToNullUpdateValueStrategy strategy = new EMFEmptyStringToNullUpdateValueStrategy();
		strategy.setConverter(new Converter(String.class, Descriptor.class) {

			public Object convert(final Object fromObject) {
				if (fromObject == null) {
					return null;
				}
				final Descriptor file = SpdFactory.eINSTANCE.createDescriptor();
				final LocalFile lFile = SpdFactory.eINSTANCE.createLocalFile();
				lFile.setName(fromObject.toString());
				file.setLocalfile(lFile);
				return file;
			}

		});
		return strategy;
	}
}
