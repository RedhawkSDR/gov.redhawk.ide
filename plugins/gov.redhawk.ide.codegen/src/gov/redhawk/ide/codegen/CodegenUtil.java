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
package gov.redhawk.ide.codegen;

import gov.redhawk.ide.codegen.builders.TopLevelBuildScript;
import gov.redhawk.ide.codegen.builders.TopLevelRPMSpec;
import gov.redhawk.ide.natures.ScaComponentProjectNature;
import gov.redhawk.sca.util.Debug;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.ScaResourceFactoryUtil;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.BasicEMap;
import org.eclipse.emf.common.util.BasicEMap.Entry;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

public class CodegenUtil {
	private static final Debug DEBUG = new Debug(RedhawkCodegenActivator.PLUGIN_ID, "codgenUtil");
	/**
	 * @since 9.0
	 */
	public static final String JAVA_PACKAGE = "java_package";
	/**
	 * @since 7.0
	 */
	public static final String CPP = "C++";
	/**
	 * @since 7.0
	 */
	public static final String JAVA = "Java";
	/**
	 * @since 7.0
	 */
	public static final String PYTHON = "Python";

	private CodegenUtil() {

	}

	/**
	 * Gets the wave dev settings.
	 * 
	 * @param waveDevResource the wave dev resource
	 * 
	 * @return the wave dev settings
	 */
	public static WaveDevSettings getWaveDevSettings(final Resource waveDevResource) {
		if (waveDevResource == null) {
			return null;
		}
		return (WaveDevSettings) waveDevResource.getEObject("/");
	}

	/**
	 * Gets the wavedev uri. There is a 1-1 relationship for SPD file to wavedev
	 * settings
	 * 
	 * @param spdResourceUri the SPD resource URI
	 * 
	 * @return the wavedev settings uri
	 */
	public static URI getWaveDevSettingsURI(final URI spdResourceUri) {
		URI uri = spdResourceUri;
		String name = uri.lastSegment();
		name = name.substring(0, name.length() - 7);
		uri = uri.trimSegments(1);
		uri = uri.appendSegment("." + name + "wavedev");
		return uri;
	}

	/**
	 * @since 9.0
	 */
	public static Command createAddImplementationSettingsCommand(final EditingDomain domain, final String implId, final ImplementationSettings settings,
	        final WaveDevSettings waveDevSettings) {
		// XXX Is this right?!?
		@SuppressWarnings("unchecked")
		final BasicEMap.Entry<String, ImplementationSettings> entry = (Entry<String, ImplementationSettings>) EcoreUtil
		        .create(CodegenPackage.Literals.IMPL_ID_TO_SETTINGS_MAP);
		entry.setKey(implId);
		entry.setValue(settings);
		return AddCommand.create(domain, waveDevSettings, CodegenPackage.Literals.WAVE_DEV_SETTINGS__IMPL_SETTINGS, Collections.singleton(entry));
	}

	/**
	 * Gets the settings URI
	 * 
	 * @param softpkg the SPD resource
	 * 
	 * @return the wavedev settings uri
	 * @since 9.0
	 */
	public static URI getSettingsURI(final SoftPkg softpkg) {
		if ((softpkg == null) || (softpkg.eResource() == null)) {
			return null;
		}
		URI uri = softpkg.eResource().getURI();
		final String[] segments = uri.segments();
		String name = segments[segments.length - 1];
		name = name.substring(0, name.length() - 7);
		uri = uri.trimSegments(1);
		uri = uri.appendSegment("." + name + "wavedev").appendFragment("/");
		return uri;
	}

	/**
	 * 
	 * @param impl The implementation to get Implementation Settings from
	 * @return implSettings Return the Implementation Settings associated with the Implementation that was passed in
	 */
	public static ImplementationSettings getImplementationSettings(final Implementation impl) {
		final WaveDevSettings waveSettings = CodegenUtil.getWaveDevSettings(impl);

		if (waveSettings != null) {
			return waveSettings.getImplSettings().get(impl.getId());
		}

		return null;
	}

	/**
	 * 
	 * @param impl The implementation to get Wave Dev Settings from
	 * @return waveDevSettings Return the Wave Dev Settings associated with the Implementation that was passed in
	 * @since 9.0
	 */
	public static WaveDevSettings getWaveDevSettings(final Implementation impl) {
		if (impl == null || impl.eResource() == null) {
			return null;
		}
		final ResourceSet resourceSet = impl.eResource().getResourceSet();
		if (resourceSet == null) {
			return null;
		}
		final URI uri = CodegenUtil.getWaveDevSettingsURI(impl.eResource().getURI());
		Resource waveDevResource = null;
		if (uri.isPlatform()) {
			final IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true)));
			if (file.exists()) {
				waveDevResource = resourceSet.getResource(uri, true);
			} else {
				final ResourceSet set = ScaResourceFactoryUtil.createResourceSet();
				waveDevResource = set.createResource(uri, CodegenPackage.eCONTENT_TYPE);
				WaveDevSettings retVal = CodegenFactory.eINSTANCE.createWaveDevSettings();
				waveDevResource.getContents().add(retVal);
				try {
					waveDevResource.save(null);
				} catch (final IOException e1) {
					if (DEBUG.enabled) {
						DEBUG.catching("Unable to save resource: " + file, e1);
					}
				}
				return retVal;
			}
		} else {
			try {
				waveDevResource = resourceSet.getResource(uri, true);
			} catch (Exception e) {
				if (DEBUG.enabled) {
					DEBUG.catching("Unable to load wave dev settings: " + uri, e);
				}
				return null;
			}
		}
		if (waveDevResource != null) {
			try {
				if (!waveDevResource.isLoaded()) {
					waveDevResource.load(null);
				}
			} catch (final Exception e) {
				if (DEBUG.enabled) {
					DEBUG.catching("Unable to load wave dev settings: " + uri, e);
				}
				return null;
			}
			final Object obj = waveDevResource.getEObject("/");
			if (obj instanceof WaveDevSettings) {
				final WaveDevSettings settings = (WaveDevSettings) obj;
				return settings;
			}
		}
		return null;
	}

	public static WaveDevSettings loadWaveDevSettings(final SoftPkg softpkg) {
		final URI settingsUri = CodegenUtil.getSettingsURI(softpkg);
		if ((settingsUri == null) || !settingsUri.isPlatform()) {
			return null;
		}
		final EObjectImpl proxy = (EObjectImpl) EcoreFactory.eINSTANCE.createEObject();
		proxy.eSetProxyURI(settingsUri);
		final EObject obj = EcoreUtil.resolve(proxy, softpkg);
		if (obj instanceof WaveDevSettings) {
			return (WaveDevSettings) obj;
		}
		return null;
	}

	/**
	 * @since 5.0
	 */
	public static WaveDevSettings getWaveDevSettings(final URI fileURI) {
		final ResourceSet resourceSet = ScaResourceFactoryUtil.createResourceSet();
		final Resource resource = resourceSet.getResource(fileURI, true);

		return CodegenUtil.getWaveDevSettings(resource);
	}

	/**
	 * @since 6.0
	 */
	public static String getValidName(final String input) {
		if (input == null) return null;
		final String name = input.replaceAll("[^A-Za-z0-9_]", "_");
		return name;
	}

	/**
	 * @since 7.0
	 */
	public static ITemplateDesc getTemplate(final String templateName, final String codeGenId) {
		ITemplateDesc temp = null;

		// First, check if the template with the name exists
		if (templateName != null) {
			temp = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(templateName);
		}

		// Otherwise, check if the template with the generators name exists
		if ((temp == null) && (codeGenId != null)) {
			temp = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(codeGenId);
		}

		return temp;
	}

	/**
	 * @since 7.0
	 */
	public static IPortTemplateDesc getPortTemplate(final String templateId, final String codeGenId) {
		IPortTemplateDesc temp = null;

		// First, check if the template with the name exists
		if (templateId != null) {
			temp = RedhawkCodegenActivator.getCodeGeneratorPortTemplatesRegistry().findTemplate(templateId);
		}

		// Otherwise, check if the template specified by the code generator exists (not required to be specified)
		if ((temp == null) && (codeGenId != null)) {
			temp = RedhawkCodegenActivator.getCodeGeneratorPortTemplatesRegistry().findTemplate(codeGenId);
		}

		return temp;
	}

	/**
	 * Adds the top level build script generator to an SCA project, if applicable for the project type.
	 * @param project The project to add the top level build script generator to
	 * @param progress the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be reported and that the
	 *  operation cannot be canceled.
	 * @throws CoreException A problem occurs adjusting the project description
	 * @since 7.0
	 * @deprecated Preserved for 1.8 codegen projects only. Does not apply to 1.9 and future.
	 */
	@Deprecated
	public static void addTopLevelBuildScriptBuilder(final IProject project, final IProgressMonitor progress) throws CoreException {
		if (project.hasNature(ScaComponentProjectNature.ID)) {
			final IProjectDescription desc = project.getDescription();
			final ICommand[] commands = desc.getBuildSpec();

			// If the builder is already added, we're done
			for (int i = 0; i < commands.length; ++i) {
				if (commands[i].getBuilderName().equals(TopLevelBuildScript.ID)) {
					return;
				}
			}

			// Add builder to project
			final ICommand command = desc.newCommand();
			command.setBuilderName(TopLevelBuildScript.ID);
			final ICommand[] newCommands = new ICommand[commands.length + 1];

			// Add it before other builders.
			System.arraycopy(commands, 0, newCommands, 1, commands.length);
			newCommands[0] = command;
			desc.setBuildSpec(newCommands);
			project.setDescription(desc, progress);
		}
	}

	/**
     * Removes the top-level build script builder.
     * @param project The project to add the top level build script generator to
     * @param progress the progress monitor to use for reporting progress to the user. It is the caller's responsibility
     *  to call done() on the given monitor. Accepts null, indicating that no progress should be reported and that the
     *  operation cannot be canceled.
     * @throws CoreException
     * @since 10.1
     */
    public static void removeTopLevelBuildScriptBuilder(final IProject project, final IProgressMonitor progress) throws CoreException {
            if (project.hasNature(ScaComponentProjectNature.ID)) {
                    final IProjectDescription desc = project.getDescription();
                    final ICommand[] oldCommands = desc.getBuildSpec();
                    final List<ICommand> newCommands = new ArrayList<ICommand>();

                    // Keep everything except the top-level build script builder
                    for (ICommand command : oldCommands) {
                            if (!TopLevelBuildScript.ID.equals(command.getBuilderName())) {
                                    newCommands.add(command);
                            }
                    }

                    // If we removed it, adjust the project description
                    if (oldCommands.length != newCommands.size()) {
                            desc.setBuildSpec(newCommands.toArray(new ICommand[newCommands.size()]));
                            project.setDescription(desc, progress);
                    }
            }
    }

	/**
	 * Adds the top level RPM spec file generator to a project.
	 * 
	 * @param project The project to add the top level RPM spec file generator to
	 * @param progress the progress monitor to use for reporting progress to the user. It is the caller's responsibility
	 *  to call done() on the given monitor. Accepts null, indicating that no progress should be reported and that the
	 *  operation cannot be canceled.
	 * @throws CoreException A problem occurs adjusting the project description
	 * @since 7.0
	 */
	public static void addTopLevelRPMSpecBuilder(final IProject project, final IProgressMonitor progress) throws CoreException {
		final IProjectDescription desc = project.getDescription();
		final ICommand[] commands = desc.getBuildSpec();

		// If the builder is already added, we're done
		for (int i = 0; i < commands.length; ++i) {
			if (commands[i].getBuilderName().equals(TopLevelRPMSpec.ID)) {
				return;
			}
		}

		// Add builder to project
		final ICommand command = desc.newCommand();
		command.setBuilderName(TopLevelRPMSpec.ID);
		final ICommand[] newCommands = new ICommand[commands.length + 1];

		// Add it before other builders.
		System.arraycopy(commands, 0, newCommands, 1, commands.length);
		newCommands[0] = command;
		desc.setBuildSpec(newCommands);
		project.setDescription(desc, progress);
	}

	/**
	 * This method returns true if the given programming language name can be
	 * set to a primary implementation.  This is currently only valid for C++.
	 * @param progLangName the name of the programming language to check
	 * @return true if the programming language can be set to a primary implementation
	 * @since 9.0
	 */
	public static boolean canPrimary(final String progLangName) {
		return CodegenUtil.CPP.equals(progLangName);
	}

	/**
	 * @since 9.0
	 */
	public static void recreateImplSettings(final SoftPkg softPkg, final WaveDevSettings waveSettings) {
		// Recreate the basic settings for each implementation
		// This makes assumptions that the defaults are selected for everything
		for (final Implementation impl : softPkg.getImplementation()) {
			ImplementationSettings settings = waveSettings.getImplSettings().get(impl.getId());
			if (settings == null) {
				settings = CodegenFactory.eINSTANCE.createImplementationSettings();
			}

			// Find the code generator if specified, otherwise pick the first one returned by the registry
			ICodeGeneratorDescriptor codeGenDesc = null;
			final String lang = impl.getProgrammingLanguage().getName();
			final ICodeGeneratorDescriptor[] codeGens = RedhawkCodegenActivator.getCodeGeneratorsRegistry().findCodegenByLanguage(lang);
			for (final ICodeGeneratorDescriptor codegen : codeGens) {
				if (!codegen.notDefaultableGenerator()) {
					codeGenDesc = codegen;
				}
			}

			// Proceed if we found one
			IScaComponentCodegen generator = null;
			if (codeGenDesc != null) {
				try {
					generator = codeGenDesc.getGenerator();
				} catch (final CoreException e) {
					// PASS We'll make the user choose a generator
				}
			}
			if (generator != null) {
				// Assume that there is <output dir>[/]<name> format for the entrypoint
				String name = null;
				final String ep = impl.getCode().getEntryPoint();
				final int idx = ep.lastIndexOf('/');
				if (idx > 0) {
					name = ep.substring(idx + 1);
				} else if (idx == 0) {
					name = softPkg.getName();
				} 
				
				if (name == null) {
					name = "";
				}

				String outputDir = ep.substring(0, idx);

				if (CodegenUtil.PYTHON.equals(lang)) {
					final int dotIdx = name.indexOf('.');
					name = name.substring(0, (dotIdx > 0) ? dotIdx : name.length()); // SUPPRESS CHECKSTYLE AvoidInline
					outputDir = impl.getCode().getLocalFile().getName();
				} else if (CodegenUtil.JAVA.equals(lang)) {
					final int slhIdx = ep.indexOf('/');
					name = ep.substring(0, (slhIdx > 0) ? slhIdx : name.length()); // SUPPRESS CHECKSTYLE AvoidInline
					outputDir = impl.getCode().getLocalFile().getName();
				}
				
				// Set the generator, name and output directory
				if (isEmpty(settings.getGeneratorId())) {
					settings.setGeneratorId(generator.getClass().getCanonicalName());
				}
				
				// TODO: Determine if there is a way to set the name of the new settings without causing issues
//				if (isEmpty(settings.getName())) {
//					settings.setName(name);
//				}
				
				if (isEmpty(settings.getOutputDir())) {
					settings.setOutputDir(outputDir);
				}

				if (isEmpty(settings.getTemplate())) {
					// Find the template if specified, otherwise pick the first selectable and defaultable one returned by the registry
					ITemplateDesc templateDesc = null;
					final String componentType = softPkg.getDescriptor().getComponent().getComponentType();
					final ITemplateDesc[] templates = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplatesByCodegen(settings.getGeneratorId(), componentType);
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
							if (CodegenUtil.JAVA.equals(lang) && CodegenUtil.JAVA_PACKAGE.equals(prop.getKey())) {
								p.setValue(name);
							} else {
								p.setValue(prop.getDefaultValue());
							}
							settings.getProperties().add(p);
						}
						// Set the template
						settings.setTemplate(templateDesc.getId());
					}
				}
			}

			// Save the created settings
			waveSettings.getImplSettings().put(impl.getId(), settings);
		}
	}

	private static boolean isEmpty(String str) {
	    return (str == null) || "".equals(str.trim());
    }
}
