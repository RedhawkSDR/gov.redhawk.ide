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
package gov.redhawk.ide.codegen.ui.internal.upgrade;

import gov.redhawk.ide.codegen.IScaComponentCodegen;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.GeneratorUtil;
import gov.redhawk.ide.codegen.ui.internal.WaveDevUtil;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructPropertyConfigurationType;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.ide.ResourceUtil;
import org.osgi.framework.Version;

public class PropertyKindUtil {

	private PropertyKindUtil() {
	}

	private static class PropertyTypes {
		private boolean hasConfigOrExecParamKind = false;
		private boolean hasPropertyKind = false;

		public boolean hasConfigOrExecParamKind() {
			return hasConfigOrExecParamKind;
		}

		public boolean hasPropertyKind() {
			return hasPropertyKind;
		}

		public void setHasConfigOrExecParamKind() {
			hasConfigOrExecParamKind = true;
		}

		public void setHasPropertyKind() {
			hasPropertyKind = true;
		}
	}

	/**
	 * Check if the properties associated with the implementation(s) need upgrading. The user will be prompted to
	 * upgrade the properties if necessary, or codegen will be aborted.
	 * @param shell
	 * @param parentProject
	 * @param impls
	 * @throws CoreException
	 * @throws OperationCanceledException The user aborts an operation, or codegen is not allowed to proceed
	 */
	public static void checkProperties(Shell shell, IProject parentProject, List<Implementation> impls) throws CoreException, OperationCanceledException {
		if (impls == null || impls.size() == 0) {
			return;
		}

		// Nothing to do if there isn't an associated PRF
		final SoftPkg spd = (SoftPkg) impls.get(0).eContainer();
		if (spd.getPropertyFile() == null) {
			return;
		}
		final Properties prf = spd.getPropertyFile().getProperties();

		// Get code generator version(s)
		List<Version> codegenVersions = new ArrayList<Version>();
		for (Implementation impl : impls) {
			ImplementationSettings implSettings = WaveDevUtil.getImplSettings(impl);
			if (implSettings == null) {
				continue;
			}
			IScaComponentCodegen generator = GeneratorUtil.getGenerator(implSettings);
			if (generator == null) {
				continue;
			}
			codegenVersions.add(generator.getCodegenVersion());
		}

		// Find if there are configure, execparam and property kinds in the properties file
		PropertyTypes propTypes;
		try {
			propTypes = ScaModelCommandWithResult.runExclusive(prf, new RunnableWithResult.Impl<PropertyTypes>() {
				@Override
				public void run() {
					PropertyTypes propTypes = new PropertyTypes();
					FeatureMap props = prf.getProperties();
					for (FeatureMap.Entry propEntry : props) {
						Object propObj = propEntry.getValue();
						if (propObj instanceof Simple) {
							Simple prop = (Simple) propObj;
							findKinds(prop.getKind(), propTypes);
						} else if (propObj instanceof SimpleSequence) {
							SimpleSequence prop = (SimpleSequence) propObj;
							findKinds(prop.getKind(), propTypes);
						} else if (propObj instanceof Struct) {
							Struct prop = (Struct) propObj;
							findConfigurationKinds(prop.getConfigurationKind(), propTypes);
						} else if (propObj instanceof StructSequence) {
							StructSequence prop = (StructSequence) propObj;
							findConfigurationKinds(prop.getConfigurationKind(), propTypes);
						}
					}
					setResult(propTypes);
				}
			});
		} catch (InterruptedException e) {
			throw new OperationCanceledException();
		}

		// Don't allow the user to proceed if they're using an old codegen with the new 'property' kind
		for (Version codegenVersion : codegenVersions) {
			if (codegenVersion.compareTo(new Version(2, 0, 0)) < 0 && propTypes.hasPropertyKind()) {
				String message = "The code generator you are attempting to use is older than 2.0.0 and does not support the 'property' kind for properties";
				MessageDialog dialog = new MessageDialog(shell, "Older code generator", null, message, MessageDialog.ERROR, new String[] { "Ok" }, 0);
				dialog.open();
				throw new OperationCanceledException();
			}
		}

		// Force upgrade if using deprecated property kinds with a newer codegen
		for (Version codegenVersion : codegenVersions) {
			if (codegenVersion.compareTo(new Version(2, 0, 0)) >= 0 && propTypes.hasConfigOrExecParamKind()) {
				String message = "Your properties file contains properties deprecated in REDHAWK 2.0 ('configure' or 'execparam' property kinds). They will be upgraded to the new 'property' kind if you proceed.";
				boolean result = MessageDialog.openConfirm(shell, "Deprecated property kinds", message);
				if (result) {
					upgradeProperties(prf);
					save(parentProject, spd, prf);
					break;
				} else {
					throw new OperationCanceledException();
				}
			}
		}
	}

	/**
	 * Examines the list of kinds to see if configure, execparam, or property are explicitly (not implicitly) present.
	 * @param kinds The list of kinds to look through
	 * @param propTypes Flags are adjusted to true in this structure if the properties are present
	 */
	private static void findKinds(EList<Kind> kinds, PropertyTypes propTypes) {
		if (kinds == null) {
			return;
		}
		for (Kind kind : kinds) {
			if (kind.isSetType()) {
				switch (kind.getType()) {
				case CONFIGURE:
				case EXECPARAM:
					propTypes.setHasConfigOrExecParamKind();
					break;
				case PROPERTY:
					propTypes.setHasPropertyKind();
					break;
				default:
				}
			}
		}
	}

	/**
	 * Examines the list of kinds to see if configure or property are explicitly (not implicitly) present.
	 * @param kinds The list of kinds to look through
	 * @param propTypes Flags are adjusted to true in this structure if the properties are present
	 */
	private static void findConfigurationKinds(EList<ConfigurationKind> kinds, PropertyTypes propTypes) {
		if (kinds == null) {
			return;
		}
		for (ConfigurationKind kind : kinds) {
			if (kind.isSetType()) {
				switch (kind.getType()) {
				case CONFIGURE:
					propTypes.setHasConfigOrExecParamKind();
					break;
				case PROPERTY:
					propTypes.setHasPropertyKind();
					break;
				default:
				}
			}
		}
	}

	/**
	 * Upgrade 'configure' and 'execparam' properties to 'property' properties.
	 * @param prf The PRF model object to upgrade
	 * @throws CoreException
	 */
	private static void upgradeProperties(final Properties prf) {
		// Upgrade each property
		ScaModelCommand.execute(prf, new ScaModelCommand() {
			@Override
			public void execute() {
				FeatureMap props = prf.getProperties();
				for (FeatureMap.Entry propEntry : props) {
					Object propObj = propEntry.getValue();
					if (propObj instanceof Simple) {
						Simple prop = (Simple) propObj;
						upgradeKinds(prop.getKind());
					} else if (propObj instanceof SimpleSequence) {
						SimpleSequence prop = (SimpleSequence) propObj;
						upgradeKinds(prop.getKind());
					} else if (propObj instanceof Struct) {
						Struct prop = (Struct) propObj;
						upgradeConfigurationKinds(prop.getConfigurationKind());
					} else if (propObj instanceof StructSequence) {
						StructSequence prop = (StructSequence) propObj;
						upgradeConfigurationKinds(prop.getConfigurationKind());
					}
				}
			}
		});
	}

	/**
	 * Upgrade the list of {@link Kind} (configure or execparam -> property).
	 * @param kinds
	 */
	private static void upgradeKinds(EList<Kind> kinds) {
		if (kinds == null) {
			return;
		}

		boolean hasConfigureOrExecParam = false;
		boolean hasPropertyKind = false;
		ListIterator<Kind> kindIterator = kinds.listIterator();
		while (kindIterator.hasNext()) {
			Kind kind = kindIterator.next();
			if (kind.isSetType()) {
				switch (kind.getType()) {
				case CONFIGURE:
				case EXECPARAM:
					kindIterator.remove();
					hasConfigureOrExecParam = true;
					break;
				case PROPERTY:
					hasPropertyKind = true;
					break;
				default:
					break;
				}
			}
		}
		if (hasConfigureOrExecParam && !hasPropertyKind) {
			Kind kind = PrfFactory.eINSTANCE.createKind();
			kind.setType(PropertyConfigurationType.PROPERTY);
			kinds.add(kind);
		}
	}

	/**
	 * Upgrade the list of {@link ConfigurationKind} (configure -> property).
	 * @param kinds
	 */
	private static void upgradeConfigurationKinds(EList<ConfigurationKind> kinds) {
		if (kinds == null) {
			return;
		}

		boolean hasConfigure = false;
		boolean hasPropertyKind = false;
		ListIterator<ConfigurationKind> kindIterator = kinds.listIterator();
		while (kindIterator.hasNext()) {
			ConfigurationKind kind = kindIterator.next();
			if (kind.isSetType()) {
				switch (kind.getType()) {
				case CONFIGURE:
					kindIterator.remove();
					hasConfigure = true;
					break;
				case PROPERTY:
					hasPropertyKind = true;
					break;
				default:
					break;
				}
			}
		}
		if (hasConfigure && !hasPropertyKind) {
			ConfigurationKind kind = PrfFactory.eINSTANCE.createConfigurationKind();
			kind.setType(StructPropertyConfigurationType.PROPERTY);
			kinds.add(kind);
		}
	}

	/**
	 * Saves the properties file to disk. This will be done via an open SPD / PRF editor if possible, otherwise the
	 * file will be saved directly and a change notification generated.
	 * @param project The parent project
	 * @param spd The SPD using the PRF
	 * @param prf The PRF file to be saved
	 * @throws CoreException
	 */
	private static void save(final IProject project, final SoftPkg spd, final Properties prf) throws CoreException {
		RunnableWithResult<Boolean> saveViaEditor = new RunnableWithResult.Impl<Boolean>() {
			@Override
			public void run() {
				// Look for a PRF editor
				IEditorPart editorPart = ResourceUtil.findEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
					project.getFile(prf.eResource().getURI().lastSegment()));

				// If no PRF editor, look for SPD editor
				if (editorPart == null) {
					editorPart = ResourceUtil.findEditor(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(),
						project.getFile(spd.eResource().getURI().lastSegment()));
				}

				if (editorPart != null) {
					editorPart.doSave(new NullProgressMonitor());
					setResult(true);
				}
			}
		};
		Display.getDefault().syncExec(saveViaEditor);

		if (saveViaEditor.getResult() == null) {
			try {
				new WorkspaceModifyDelegatingOperation(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							prf.eResource().save(null);
							IFile prfFile = project.getFile(prf.eResource().getURI().lastSegment());
							prfFile.refreshLocal(IResource.DEPTH_ZERO, new NullProgressMonitor());
						} catch (IOException e) {
							throw new InvocationTargetException(e);
						} catch (CoreException e) {
							throw new InvocationTargetException(e);
						}
					}
				}).run(new NullProgressMonitor());
			} catch (InvocationTargetException e) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Problem while saving PRF XML file", e.getCause()));
			} catch (InterruptedException e) {
				throw new CoreException(new Status(IStatus.ERROR, RedhawkCodegenUiActivator.PLUGIN_ID, "Problem while saving PRF XML file", e));
			}
		}
	}
}
