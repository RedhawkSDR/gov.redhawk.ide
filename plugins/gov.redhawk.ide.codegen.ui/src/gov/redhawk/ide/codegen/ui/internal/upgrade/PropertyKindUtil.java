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

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.transaction.RunnableWithResult;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import gov.redhawk.ide.codegen.ui.internal.SaveXmlUtils;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;
import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.AccessType;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.PrfFactory;
import mil.jpeojtrs.sca.prf.PrfPackage;
import mil.jpeojtrs.sca.prf.Properties;
import mil.jpeojtrs.sca.prf.PropertyConfigurationType;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructPropertyConfigurationType;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.util.collections.FeatureMapList;

public class PropertyKindUtil {

	private PropertyKindUtil() {
	}

	/**
	 * Check if the properties associated with the implementation(s) need upgrading. The user will be prompted to
	 * upgrade the properties if necessary, or codegen will be aborted.
	 * @param shell
	 * @param parentProject
	 * @param impls
	 * @throws CoreException
	 * @throws OperationCanceledException
	 */
	public static void checkProperties(Shell shell, IProject parentProject, List<Implementation> impls) throws CoreException, OperationCanceledException {
		if (impls == null || impls.isEmpty()) {
			return;
		}

		// Nothing to do if there isn't an associated PRF
		final SoftPkg spd = (SoftPkg) impls.get(0).eContainer();
		if (spd.getPropertyFile() == null) {
			return;
		}
		final Properties prf = spd.getPropertyFile().getProperties();

		// Find if there are configure, execparam and property kinds in the properties file
		final Set<PropertyConfigurationType> kindTypes = ScaModelCommandWithResult.runExclusive(prf, () -> {
			Set<PropertyConfigurationType> retVal = new HashSet<PropertyConfigurationType>();
			FeatureMap props = prf.getProperties();
			for (FeatureMap.Entry propEntry : props) {
				Object propObj = propEntry.getValue();
				if (propObj instanceof Simple) {
					Simple prop = (Simple) propObj;
					findKinds(prop.getKind(), retVal);
				} else if (propObj instanceof SimpleSequence) {
					SimpleSequence prop = (SimpleSequence) propObj;
					findKinds(prop.getKind(), retVal);
				} else if (propObj instanceof Struct) {
					Struct prop = (Struct) propObj;
					findConfigurationKinds(prop.getConfigurationKind(), retVal);
				} else if (propObj instanceof StructSequence) {
					StructSequence prop = (StructSequence) propObj;
					findConfigurationKinds(prop.getConfigurationKind(), retVal);
				}
			}
			return retVal;
		});
		if (kindTypes == null) {
			throw new OperationCanceledException();
		}

		// Offer upgrade if using deprecated property kinds with a newer codegen
		if (kindTypes.contains(PropertyConfigurationType.CONFIGURE) || kindTypes.contains(PropertyConfigurationType.EXECPARAM)
			|| kindTypes.contains(PropertyConfigurationType.EVENT)) {
			String[] buttons = new String[] { IDialogConstants.CANCEL_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.YES_LABEL };
			MessageDialog dialog = new MessageDialog(shell, Messages.DeprecatedProps_Title, null, Messages.DeprecatedProps_Message, MessageDialog.QUESTION,
				buttons, 2);
			int result = dialog.open();
			if (result == 2) {
				// We make the changes in the UI thread because otherwise there are asynchronous changes that have
				// to propagate to the UI thread (so that the EMF changes can update the XML editor's page).
				RunnableWithResult<CoreException> runnable = new RunnableWithResult.Impl<CoreException>() {
					@Override
					public void run() {
						upgradeProperties(prf);
						try {
							SaveXmlUtils.save(spd, prf, null);
						} catch (CoreException e) {
							setResult(e);
						}
					}
				};
				shell.getDisplay().syncExec(runnable);

				// Re-throw the exception if any
				if (runnable.getResult() != null) {
					throw runnable.getResult();
				}
			} else if (result != 1) {
				throw new OperationCanceledException();
			}
		}
	}

	/**
	 * Finds all unique kind types explicitly specified and adds them to the Set
	 * @param kinds The list of kinds to look through
	 * @param kindTypes Any property kind types present are added to the Set
	 */
	private static void findKinds(EList<Kind> kinds, Set<PropertyConfigurationType> kindTypes) {
		if (kinds == null) {
			return;
		}
		for (Kind kind : kinds) {
			kindTypes.add(kind.getType());
		}
	}

	/**
	 * Finds all unique configuration kind types explicitly specified and adds their kind type equivalent to the Set
	 * @param kinds The list of kinds to look through
	 * @param kindTypes Any property kind types present are added to the Set
	 */
	private static void findConfigurationKinds(EList<ConfigurationKind> kinds, Set<PropertyConfigurationType> kindTypes) {
		if (kinds == null) {
			return;
		}
		for (ConfigurationKind kind : kinds) {
			kindTypes.add(kind.getType().getPropertyConfigurationType());
		}
	}

	/**
	 * Upgrade 'configure' and 'execparam' properties to 'property' properties, or remove if superfluous.
	 * @param prf The PRF model object to upgrade
	 * @throws CoreException
	 */
	private static void upgradeProperties(final Properties prf) {
		// Upgrade each property
		ScaModelCommand.execute(prf, new ScaModelCommand() {
			@Override
			public void execute() {
				FeatureMapList<AbstractProperty> props = new FeatureMapList<AbstractProperty>(prf.getProperties(), AbstractProperty.class);
				for (AbstractProperty absProp : props) {
					switch (absProp.eClass().getClassifierID()) {
					case PrfPackage.SIMPLE:
					case PrfPackage.SIMPLE_SEQUENCE:
						upgradeKindProperties(absProp);
						break;
					case PrfPackage.STRUCT:
					case PrfPackage.STRUCT_SEQUENCE:
						upgradeConfigurationKindProperties(absProp);
						break;
					default:
						throw new IllegalArgumentException();
					}
				}
			}
		});
	}

	/**
	 * Upgrade properties with the "kind" XML type (simple / simple sequence)
	 * @param prop
	 */
	private static void upgradeKindProperties(AbstractProperty prop) {
		List<Kind> kinds;
		switch (prop.eClass().getClassifierID()) {
		case PrfPackage.SIMPLE:
			kinds = ((Simple) prop).getKind();
			break;
		case PrfPackage.SIMPLE_SEQUENCE:
			kinds = ((SimpleSequence) prop).getKind();
			break;
		default:
			throw new IllegalArgumentException();
		}

		boolean hasProperty = false;
		boolean hadConfigure = false;
		boolean hadExecParam = false;
		ListIterator<Kind> iterator = kinds.listIterator();
		while (iterator.hasNext()) {
			Kind kind = iterator.next();
			switch (kind.getType()) {
			case PROPERTY:
				hasProperty = true;
				break;
			case CONFIGURE:
				iterator.remove();
				hadConfigure = true;
				break;
			case EXECPARAM:
				iterator.remove();
				hadExecParam = true;
				break;
			case EVENT:
				iterator.remove();
				break;
			default:
				break;
			}
		}
		if (hadExecParam && prop.eClass().getClassifierID() == PrfPackage.SIMPLE) {
			// read-write execparms that aren't configurable -> read-only command line property
			if (!hadConfigure && !hasProperty && prop.getMode() == AccessType.READWRITE) {
				prop.setMode(AccessType.READONLY);
			}
			if (!hasProperty) {
				Kind newKind = PrfFactory.eINSTANCE.createKind();
				newKind.setType(PropertyConfigurationType.PROPERTY);
				kinds.add(newKind);
			}
			((Simple) prop).setCommandline(true);
		} else if (hadConfigure && !hasProperty) {
			Kind newKind = PrfFactory.eINSTANCE.createKind();
			newKind.setType(PropertyConfigurationType.PROPERTY);
			kinds.add(newKind);
		}
	}

	/**
	 * Upgrade properties with the "configurationkind" XML type (struct / struct sequence)
	 * @param prop
	 */
	private static void upgradeConfigurationKindProperties(AbstractProperty prop) {
		List<ConfigurationKind> kinds;
		switch (prop.eClass().getClassifierID()) {
		case PrfPackage.STRUCT:
			kinds = ((Struct) prop).getConfigurationKind();
			break;
		case PrfPackage.STRUCT_SEQUENCE:
			kinds = ((StructSequence) prop).getConfigurationKind();
			break;
		default:
			throw new IllegalArgumentException();
		}

		boolean hasProperty = false;
		boolean hadConfigure = false;
		ListIterator<ConfigurationKind> iterator = kinds.listIterator();
		while (iterator.hasNext()) {
			ConfigurationKind kind = iterator.next();
			switch (kind.getType()) {
			case PROPERTY:
				hasProperty = true;
				break;
			case CONFIGURE:
				iterator.remove();
				hadConfigure = true;
				break;
			case EVENT:
				iterator.remove();
				break;
			default:
				break;
			}
		}
		if (hadConfigure && !hasProperty) {
			ConfigurationKind newKind = PrfFactory.eINSTANCE.createConfigurationKind();
			newKind.setType(StructPropertyConfigurationType.PROPERTY);
			kinds.add(newKind);
		}

		// Drop nested kinds
		switch (prop.eClass().getClassifierID()) {
		case PrfPackage.STRUCT:
			Struct struct = (Struct) prop;
			for (Simple simple : struct.getSimple()) {
				simple.getKind().clear();
			}
			for (SimpleSequence simpleSequence : struct.getSimpleSequence()) {
				simpleSequence.getKind().clear();
			}
			break;
		case PrfPackage.STRUCT_SEQUENCE:
			StructSequence structSequence = (StructSequence) prop;
			for (Simple simple : structSequence.getStruct().getSimple()) {
				simple.getKind().clear();
			}
			for (SimpleSequence simpleSequence : structSequence.getStruct().getSimpleSequence()) {
				simpleSequence.getKind().clear();
			}
			break;
		default:
			throw new IllegalArgumentException();
		}
	}
}
