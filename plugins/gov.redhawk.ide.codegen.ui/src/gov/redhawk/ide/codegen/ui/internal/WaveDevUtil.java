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
package gov.redhawk.ide.codegen.ui.internal;

import gov.redhawk.ide.codegen.CodegenFactory;
import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.ICodeGeneratorDescriptor;
import gov.redhawk.ide.codegen.IPropertyDescriptor;
import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.Property;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.ui.RedhawkIDEUiPlugin;
import gov.redhawk.ide.ui.wizard.IRedhawkImportProjectWizardAssist;
import gov.redhawk.model.sca.commands.ScaModelCommand;

import java.io.IOException;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class WaveDevUtil {

	private WaveDevUtil() {
	}

	/**
	 * Re-creates a missing .wavedev file for a {@link SoftPkg} using some assumptions.
	 * @param softPkg
	 * @return The newly created {@link WaveDevSettings}
	 * @throws CoreException
	 */
	public static WaveDevSettings generateWaveDev(SoftPkg softPkg) throws CoreException {
		WaveDevSettings waveDev = CodegenFactory.eINSTANCE.createWaveDevSettings();

		// Recreate the basic settings for each implementation
		// This makes assumptions that the defaults are selected for everything
		for (final Implementation impl : softPkg.getImplementation()) {
			generateImplSettings(waveDev, impl);
		}

		// Create the URI to the .wavedev file
		final org.eclipse.emf.common.util.URI uri = org.eclipse.emf.common.util.URI.createPlatformResourceURI(
			softPkg.getName() + "/." + softPkg.getName() + ".wavedev", false);
		final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
		final Resource res = set.createResource(uri);

		// Add the WaveDevSettings to the resource and save to disk to persist the newly created WaveDevSettings
		res.getContents().add(waveDev);
		try {
			res.save(null);
		} catch (final IOException e) {
			RedhawkCodegenUiActivator.logError(e.getMessage(), e);
		}

		return waveDev;
	}

	/**
	 * Creates {@link ImplementationSettings} for an {@link Implementation} using some assumptions.
	 * @param waveDev The wavedev file to add settings to
	 * @param impl The implementation for which to generate settings
	 * @throws CoreException
	 */
	public static ImplementationSettings generateImplSettings(final WaveDevSettings waveDev, final Implementation impl) throws CoreException {
		final ImplementationSettings settings = CodegenFactory.eINSTANCE.createImplementationSettings();
		final String lang = impl.getProgrammingLanguage().getName();
		// Find the code generator if specified, otherwise pick the first
		// one returned by the registry
		ICodeGeneratorDescriptor codeGenDesc = null;
		final ICodeGeneratorDescriptor[] codeGens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(lang);
		if (codeGens.length > 0) {
			codeGenDesc = codeGens[0];
		}

		if (codeGenDesc != null) {
			final IScaComponentCodegen generator = codeGenDesc.getGenerator();

			// Assume that there is <name>[/].+<other> format for the entry point
			// Pick out <name> for both the output directory and settings name
			final String lf = impl.getCode().getEntryPoint();

			// Set the generator, settings name and output directory
			settings.setGeneratorId(generator.getClass().getCanonicalName());
			settings.setOutputDir(lf.substring(0, lf.lastIndexOf('/')));

			// pick the first selectable and defaultable template returned by the registry
			ITemplateDesc templateDesc = null;
			final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(settings.getGeneratorId());
			for (final ITemplateDesc itd : templates) {
				if (itd.isSelectable() && !itd.notDefaultableGenerator()) {
					templateDesc = itd;
					break;
				}
			}
			// If we found the template, use it
			if (templateDesc != null) {
				// Set the properties to their default values
				for (final IPropertyDescriptor prop : templateDesc.getPropertyDescriptors()) {
					final Property p = CodegenFactory.eINSTANCE.createProperty();
					p.setId(prop.getKey());
					p.setValue(prop.getDefaultValue());
					settings.getProperties().add(p);
				}
				// Set the template
				settings.setTemplate(templateDesc.getId());
				for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
					if (assistant.handlesLanguage(lang)) {
						settings.setTemplate(assistant.getDefaultTemplate());
						break;
					}
				}
			}
		}

		for (IRedhawkImportProjectWizardAssist assistant : RedhawkIDEUiPlugin.getDefault().getRedhawkImportWizardAssistants()) {
			if (assistant.handlesLanguage(lang)) {
				assistant.setupWaveDev(impl.getSoftPkg().getName(), settings);
				break;
			}
		}

		ScaModelCommand.execute(waveDev, new ScaModelCommand() {

			@Override
			public void execute() {
				waveDev.getImplSettings().put(impl.getId(), settings);
			}
		});

		return settings;
	}

	/**
	 * Gets the implementation's settings from the .wavedev file
	 * @param impl
	 * @return
	 */
	public static ImplementationSettings getImplSettings(Implementation impl) {
		final WaveDevSettings waveDev = CodegenUtil.loadWaveDevSettings((SoftPkg) impl.eContainer());
		final EMap<String, ImplementationSettings> implSet = waveDev.getImplSettings();
		final ImplementationSettings settings = implSet.get(impl.getId());
		return settings;
	}
}
