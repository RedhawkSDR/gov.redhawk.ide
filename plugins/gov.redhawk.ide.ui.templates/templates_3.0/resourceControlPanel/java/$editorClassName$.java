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
package $packageName$;

import gov.redhawk.sca.ui.editors.AbstractScaContentEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

/**
 * An example showing how to create a control panel.
 */
public class $editorClassName$ extends AbstractScaContentEditor<$resourceClassNameNoGeneric$> {
	private $compositeName$ composite;

	@Override
	public void createPartControl(final Composite main) {
		this.composite = new $compositeName$(main, SWT.None);
		this.composite.setInput(getInput());
	}

	@Override
	public void setFocus() {
		if (this.composite != null) {
			composite.setFocus();
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		// init override necessary for WindowBuilder support
		super.init(site, input);
	}

	@Override
	protected Class<$resourceClassNameNoGeneric$> getInputType() {
		return $resourceClassNameNoGeneric$.class;
	}
}
