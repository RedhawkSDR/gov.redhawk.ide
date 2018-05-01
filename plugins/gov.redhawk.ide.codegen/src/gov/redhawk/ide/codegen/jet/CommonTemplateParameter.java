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
package gov.redhawk.ide.codegen.jet;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

/* package */ abstract class CommonTemplateParameter< T extends EObject > {

	private T model;
	private String headerContent;
	private List<String> filesToInstall;

	protected CommonTemplateParameter(T model, String headerContent) {
		this.model = model;
		this.headerContent = headerContent;
		this.filesToInstall = new ArrayList<>();
	}

	public T getModel() {
		return model;
	}

	public String getHeaderContent() {
		return headerContent;
	}

	public List<String> getFilesToInstall() {
		return this.filesToInstall;
	}

	public void addFileToInstall(String fileName) {
		this.filesToInstall.add(fileName);
	}

}
