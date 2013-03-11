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
package gov.redhawk.ide.spd.internal.ui.editor.wizard;

import mil.jpeojtrs.sca.spd.Author;

import org.eclipse.jface.wizard.Wizard;

/**
 * 
 */
public class AuthorWizard extends Wizard {

	private final AuthorWizardPage authorPage = new AuthorWizardPage();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addPages() {
		this.addPage(this.authorPage);
		super.addPages();
	}

	/**
	 * Gets the author.
	 * 
	 * @return the author
	 */
	public Author getAuthor() {
		return this.authorPage.getAuthor();
	}

	/**
	 * Sets the author.
	 * 
	 * @param author the new author
	 */
	public void setAuthor(final Author author) {
		this.authorPage.setAuthor(author);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean performFinish() {
		return true;
	}

}
