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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.LocalComponentProgramLaunchDelegate;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import mil.jpeojtrs.sca.sad.SadPackage;
import mil.jpeojtrs.sca.sad.SoftwareAssembly;
import mil.jpeojtrs.sca.spd.Code;
import mil.jpeojtrs.sca.spd.CodeFileType;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.DceUuidUtil;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class LaunchHandler extends AbstractHandler implements IHandler {

	/**
	 * {@inheritDoc}
	 */
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getActiveMenuSelection(event);
		if (sel == null) {
			sel = HandlerUtil.getCurrentSelection(event);
		}
		if (sel instanceof IStructuredSelection) {
			final IStructuredSelection ss = (IStructuredSelection) sel;
			for (final Object obj : ss.toList()) {
				try {
					handleLaunch(obj, event);
				} catch (final CoreException e) {
					StatusManager.getManager().handle(new Status(IStatus.ERROR, ScaDebugUiPlugin.PLUGIN_ID, e
							.getLocalizedMessage(), e), StatusManager.LOG | StatusManager.SHOW);
				}
			}
		}
		return null;
	}
	
	@Override
	public void setEnabled(Object evaluationContext) {
		final IEvaluationContext context = (IEvaluationContext) evaluationContext;
		final IWorkbenchWindow window = (IWorkbenchWindow) context.getVariable("activeWorkbenchWindow");
		if (window == null) {
			setBaseEnabled(false);
			return;
		}
		
		if (context.getVariable("activeMenuSelection") != null && context.getVariable("activeMenuSelection") instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) context.getVariable("activeMenuSelection");
			boolean enabled = true;
			out:for (final Object obj : ss.toList()) {
				if (obj instanceof SoftPkg) {
					SoftPkg spd = (SoftPkg) obj;
					for (Implementation impl : spd.getImplementation()) {
						enabled = checkImpl(impl);
						if (!enabled) {
							break out;
						}
					}
				} else if (obj instanceof Implementation) {
					Implementation impl = (Implementation) obj;
					enabled = checkImpl(impl);
					if (!enabled) {
						break out;
					}
				}
			}
			setBaseEnabled(enabled);
		} 
	}

	private boolean checkImpl(Implementation impl) {
	    if (impl.getCode() != null) {
	    	Code code = impl.getCode();
	    	CodeFileType type = code.getType();
	    	if (type != null) {
	    		switch(type) {
	    		case EXECUTABLE:
	    			return true;
	    		case DRIVER:
	    		case KERNEL_MODULE:
	    		case NODE_BOOTER:
	    		case SHARED_LIBRARY:
	    		default:
	    			return false;
	    		}
	    	}
	    }
	    return false;
    }

	private void handleLaunch(Object element, final ExecutionEvent event) throws CoreException {
		if (element instanceof IFile) {
			element = loadFile((IFile) element);
		}

		if (element instanceof SoftPkg) {
			handleLaunchSpd((SoftPkg) element, event);
		} else if (element instanceof SoftwareAssembly) {
			handleLaunchSad((SoftwareAssembly) element, event);
		} else if (element instanceof Implementation) {
			launchImplementation((Implementation) element, event);
		}
	}

	private Object loadFile(final IFile element) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		try {
			final Resource resource = resourceSet.getResource(URI.createURI(URIUtil.toURI(element.getLocation()).toString()), true);
			if (element.getName().endsWith(SpdPackage.FILE_EXTENSION)) {
				return SoftPkg.Util.getSoftPkg(resource);
			} else if (element.getName().endsWith(SadPackage.FILE_EXTENSION)) {
				return SoftwareAssembly.Util.getSoftwareAssembly(resource);
			}
		} catch (final Exception e) {
			// PASS
		}
		return element;
	}

	private void handleLaunchSad(final SoftwareAssembly element, final ExecutionEvent event) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	private void handleLaunchSpd(final SoftPkg spd, final ExecutionEvent event) throws CoreException {
		if (spd.getImplementation().isEmpty()) {
			return;
		}
		if (spd.getImplementation().size() == 1) {
			final Implementation impl = spd.getImplementation().get(0);
			launchImplementation(impl, event);
		} else {
			final ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);
			adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

			final DecoratingLabelProvider labelProvider = new DecoratingLabelProvider(new AdapterFactoryLabelProvider(adapterFactory),
			        PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator());

			final ElementListSelectionDialog dialog = new ElementListSelectionDialog(HandlerUtil.getActiveShell(event), labelProvider);
			dialog.setElements(spd.getImplementation().toArray());
			dialog.setTitle("Launch");
			dialog.setMessage("Select an implementation to run:");
			dialog.setMultipleSelection(false);
			final int result = dialog.open();
			labelProvider.dispose();
			adapterFactory.dispose();

			if (result == Window.OK) {
				launchImplementation((Implementation) dialog.getFirstResult(), event);
			}
		}

	}

	private void launchImplementation(final Implementation impl, final ExecutionEvent event) throws CoreException {
		final SoftPkg spd = impl.getSoftPkg();
//		final String instId = DceUuidUtil.createDceUUID() + ":" + spd.getId();
		final URI uri = impl.eResource().getURI();
		final ILaunchConfigurationWorkingCopy config = LocalComponentProgramLaunchDelegate.createLaunchConfiguration(spd.getName(),
		        null,
		        impl.getId(),
		        null,
		        uri);
		final Job job = new Job("Launching " + spd.getName()) {

			@Override
			protected IStatus run(final IProgressMonitor monitor) {
				try {
					config.launch(ILaunchManager.RUN_MODE, monitor, false);
				} catch (final CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();

	}
}
