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
package gov.redhawk.ide.help;

import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.help.ITopic;
import org.eclipse.help.IUAElement;

public class RedhawkTopic implements ITopic {

	private String docPath;
	private String href;
	private String label;

	public RedhawkTopic(String docPath, String label) {
		this.docPath = docPath;
		this.href = "/" + RedhawkHelpPlugin.PLUGIN_ID + "/" + docPath;
		this.label = label;
	}

	@Override
	public boolean isEnabled(IEvaluationContext context) {
		return true;
	}

	@Override
	public IUAElement[] getChildren() {
		return getSubtopics();
	}

	@Override
	public String getHref() {
		return href;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public ITopic[] getSubtopics() {
		return new ITopic[0];
	}

	public String getDocPath() {
		return docPath;
	}

}
