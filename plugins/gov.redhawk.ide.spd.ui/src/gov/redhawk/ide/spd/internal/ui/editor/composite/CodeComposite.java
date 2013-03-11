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
package gov.redhawk.ide.spd.internal.ui.editor.composite;

import gov.redhawk.common.ui.editor.FormLayoutFactory;
import gov.redhawk.common.ui.parts.FormEntry;
import gov.redhawk.ide.codegen.ITemplateDesc;
import gov.redhawk.ide.codegen.ImplementationSettings;
import gov.redhawk.ide.codegen.RedhawkCodegenActivator;
import gov.redhawk.ide.codegen.provider.CodegenItemProviderAdapterFactory;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionImplementationItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionOsItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.ImplementationDetailsSectionProcessorItemProvider;
import gov.redhawk.ide.spd.internal.ui.editor.provider.SpdItemProviderAdapterFactoryAdapter;
import gov.redhawk.ide.ui.doc.IdeHelpConstants;
import gov.redhawk.ui.doc.HelpUtil;
import gov.redhawk.ui.editor.IScaComposite;
import mil.jpeojtrs.sca.spd.CodeFileType;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * 
 */
public class CodeComposite extends Composite implements IScaComposite {
	private static final int NUM_COLUMNS = 3;

	private final FormToolkit toolkit;
	private FormEntry entryPoint;
	private FormEntry localFile;
	private FormEntry codePriority;
	private ComboViewer codeTypeViewer;
	private ComposedAdapterFactory adapterFactory;
	private FormEntry codeStackSize;

	/**
	 * @param parent
	 * @param style
	 */
	public CodeComposite(final Composite parent, final int style, final FormToolkit toolkit) {
		super(parent, style);
		this.toolkit = toolkit;

		setLayout(FormLayoutFactory.createSectionClientGridLayout(false, CodeComposite.NUM_COLUMNS));

		createCodeEntryPointEntry();

		createCodePriorityEntry();

		createCodeLocalFileEntry();

		createCodeStackSizeEntry();

		createCodeTypeEntry();

		toolkit.paintBordersFor(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void dispose() {
		if (this.adapterFactory != null) {
			this.adapterFactory.dispose();
			this.adapterFactory = null;
		}
		super.dispose();
	}

	/**
	 * Creates the code entry point entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createCodeEntryPointEntry() {
		this.entryPoint = new FormEntry(this, this.toolkit, "Entry Point:", SWT.SINGLE);
		HelpUtil.assignTooltip(this.entryPoint.getText(), IdeHelpConstants.spd_implementation_code_entryPoint);
	}

	/**
	 * Creates the code local file entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createCodeLocalFileEntry() {
		this.localFile = new FormEntry(this, this.toolkit, "File*:", SWT.SINGLE);
		HelpUtil.assignTooltip(this.localFile.getText(), IdeHelpConstants.spd_implementation_code_file);
	}

	/**
	 * Creates the code priority enry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createCodePriorityEntry() {
		this.codePriority = new FormEntry(this, this.toolkit, "Priority:", SWT.SINGLE);
		HelpUtil.assignTooltip(this.codePriority.getText(), IdeHelpConstants.spd_implementation_code_priority);
	}

	/**
	 * Creates the code stack size entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createCodeStackSizeEntry() {
		this.codeStackSize = new FormEntry(this, this.toolkit, "Stack Size:", SWT.SINGLE);
		HelpUtil.assignTooltip(this.codeStackSize.getText(), IdeHelpConstants.spd_implementation_code_stackSize);
	}

	/**
	 * Creates the code type entry.
	 * 
	 * @param client the client
	 * @param toolkit the toolkit
	 * @param actionBars the action bars
	 */
	private void createCodeTypeEntry() {
		final Label label = this.toolkit.createLabel(this, "Type:");
		label.setForeground(this.toolkit.getColors().getColor(IFormColors.TITLE));
		this.codeTypeViewer = new ComboViewer(this, SWT.READ_ONLY | SWT.SINGLE | SWT.DROP_DOWN);
		this.codeTypeViewer.getControl().setLayoutData(GridDataFactory.fillDefaults().grab(true, false).span(2, 1).create());
		this.codeTypeViewer.setContentProvider(new ArrayContentProvider());
		this.codeTypeViewer.setLabelProvider(new AdapterFactoryLabelProvider(getAdapterFactory()));
		this.codeTypeViewer.setInput(CodeFileType.VALUES);
		HelpUtil.assignTooltip(this.codeTypeViewer.getControl(), IdeHelpConstants.spd_implementation_code_type);
	}

	/**
	 * Gets the adapter factory.
	 * 
	 * @return the adapter factory
	 */
	protected AdapterFactory getAdapterFactory() {
		if (this.adapterFactory == null) {
			this.adapterFactory = new ComposedAdapterFactory(ComposedAdapterFactory.Descriptor.Registry.INSTANCE);

			this.adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
			final SpdItemProviderAdapterFactoryAdapter spdFactory = new SpdItemProviderAdapterFactoryAdapter();
			spdFactory.setImplementationAdapter(new ImplementationDetailsSectionImplementationItemProvider(spdFactory));
			spdFactory.setOsAdapter(new ImplementationDetailsSectionOsItemProvider(spdFactory));
			spdFactory.setProcessorAdapter(new ImplementationDetailsSectionProcessorItemProvider(spdFactory));
			this.adapterFactory.addAdapterFactory(spdFactory);
			this.adapterFactory.addAdapterFactory(new CodegenItemProviderAdapterFactory());
			this.adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());
		}
		return this.adapterFactory;
	}

	/**
	 * @return the entryPoint
	 */
	public FormEntry getEntryPoint() {
		return this.entryPoint;
	}

	/**
	 * @return the codeFile
	 */
	public FormEntry getLocalFile() {
		return this.localFile;
	}

	/**
	 * @return the codePriority
	 */
	public FormEntry getCodePriority() {
		return this.codePriority;
	}

	/**
	 * @return the codeTypeViewer
	 */
	public ComboViewer getCodeTypeViewer() {
		return this.codeTypeViewer;
	}

	/**
	 * @return the codeStackSize
	 */
	public FormEntry getCodeStackSize() {
		return this.codeStackSize;
	}

	public void setFieldsEditable(final ImplementationSettings implSettings) {
		boolean editable = true;

		// The fields shouldn't be editable if the generator generates code.
		// We determine this by whether or not the generator has settings
		// (no settings, nothing has been defaulted)
		if (implSettings != null) {
			final ITemplateDesc template = RedhawkCodegenActivator.getCodeGeneratorTemplatesRegistry().findTemplate(implSettings.getTemplate());
			editable = (template == null) || (!template.hasSettings());
		}
		this.localFile.getText().setEnabled(editable);
		this.entryPoint.getText().setEnabled(editable);
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEditable(final boolean canEdit) {
		this.localFile.setEditable(canEdit);
		this.entryPoint.setEditable(canEdit);
		this.codePriority.setEditable(canEdit);
		this.codeStackSize.setEditable(canEdit);
		this.codeTypeViewer.getCombo().setEnabled(canEdit);
	}

}
