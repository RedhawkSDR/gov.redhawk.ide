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
package gov.redhawk.ide.codegen.ui.action;

import gov.redhawk.ide.codegen.CodegenUtil;
import gov.redhawk.ide.codegen.WaveDevSettings;
import gov.redhawk.ide.codegen.ui.GenerateCode;
import gov.redhawk.ide.codegen.ui.RedhawkCodegenUiActivator;
import gov.redhawk.ide.codegen.ui.internal.CodegenPluginImages;
import gov.redhawk.model.sca.util.ModelUtil;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISaveableFilter;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.Saveable;

public class GenerateCodeAction extends Action {
	private Resource spdResource = null;
	private GenerateCode codeGenerator = null;

	public GenerateCodeAction(final String compOrDevice) {
		this.codeGenerator = new GenerateCode();
		setImageDescriptor(CodegenPluginImages.GENERATE_CODE);
		setToolTipText("Generate All " + compOrDevice + " Implementations");
	}

	@Override
	public void run() {
		if (this.spdResource != null) {
			SoftPkg softPkg = null;
			softPkg = SoftPkg.Util.getSoftPkg(this.spdResource);

			if (softPkg != null) {
				if (!softPkg.getImplementation().isEmpty()) {
					
					if (savedRelatedEditors(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), ModelUtil.getResource(this.spdResource))) {
						return;
					} 
					this.codeGenerator.generate(softPkg.getImplementation());
				} else {
					RedhawkCodegenUiActivator.logError("Unable to perform code generation without implementation(s)", null);
				}
			}
		}
	}

	/**
	 * @param spdResource the spdResource to set
	 */
	public void setSpdResource(final Resource spdResource) {
		this.spdResource = spdResource;
	}

	/**
	 * @return the spdResource
	 */
	public Resource getSpdResource() {
		return this.spdResource;
	}

	@Override
	public boolean isEnabled() {
		WaveDevSettings waveSettings = null;

		if (this.spdResource != null) {
			final SoftPkg softPkg = SoftPkg.Util.getSoftPkg(this.spdResource);

			if (softPkg != null) {
				waveSettings = CodegenUtil.loadWaveDevSettings(softPkg);
			}
		}

		return (waveSettings != null);
	}
	
	private boolean savedRelatedEditors(final Shell shell, final IFile editorFile) {
	    return !PlatformUI.getWorkbench().saveAll(new IShellProvider() {
	    	
	    	public Shell getShell() {
	    		return shell;
	    	}
	    }, PlatformUI.getWorkbench().getActiveWorkbenchWindow(), new ISaveableFilter() {
	    	
	    	public boolean select(Saveable saveable, IWorkbenchPart[] containingParts) {
	    		for (IWorkbenchPart part : containingParts) {
	    			if (part instanceof IEditorPart && ((IEditorPart) part).getEditorInput() instanceof IFileEditorInput) {
	    				IFileEditorInput input = (IFileEditorInput) ((IEditorPart) part).getEditorInput();
	    				if (input.getFile().getProject().equals(editorFile.getProject())) {
	    					return true;
	    				}
	    			}
	    		}
	    		return false;
	    	}
	    }, true);
    }
	
}
