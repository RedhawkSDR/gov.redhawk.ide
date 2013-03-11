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
package gov.redhawk.ide.spd.internal.ui.editor.provider;

import mil.jpeojtrs.sca.spd.provider.SpdItemProviderAdapterFactory;

import org.eclipse.emf.common.notify.Adapter;

/**
 * The Class ProcessorAdapterFactoryItemProvider.
 */
public class SpdItemProviderAdapterFactoryAdapter extends SpdItemProviderAdapterFactory {

	private Adapter authorAdapter;
	private Adapter codeAdapter;
	private Adapter compilerAdapter;
	private Adapter dependencyAdapter;
	private Adapter descriptorAdapter;
	private Adapter documentRootAdapter;
	private Adapter humanLanguageAdapter;
	private Adapter implementationAdapter;
	private Adapter implRefAdapter;
	private Adapter localFileAdapter;
	private Adapter osAdapter;
	private Adapter processorAdapter;
	private Adapter programmingLanguageAdapter;
	private Adapter propertyFileAdapter;
	private Adapter runtimeAdapter;
	private Adapter softPkgAdapter;
	private Adapter usesDeviceAdapter;
	private Adapter propertyRefAdapter;
	private Adapter softPkgRefAdapter;

	/**
	 * @return the propertyRefAdapter
	 */
	public Adapter getPropertyRefAdapter() {
		return this.propertyRefAdapter;
	}

	/**
	 * @param propertyRefAdapter the propertyRefAdapter to set
	 */
	public void setPropertyRefAdapter(final Adapter propertyRefAdapter) {
		this.propertyRefAdapter = propertyRefAdapter;
	}

	/**
	 * Sets the soft pkg item provider.
	 * 
	 * @param softPkgAdapter the soft pkg adapter
	 */
	public void setSoftPkgAdapter(final Adapter softPkgAdapter) {
		this.softPkgAdapter = softPkgAdapter;
	}

	/**
	 * Gets the soft pkg adapter.
	 * 
	 * @return the softPkgItemProvider
	 */
	public Adapter getSoftPkgAdapter() {
		return this.softPkgAdapter;
	}

	/**
	 * @return the authorAdapter
	 */
	public Adapter getAuthorAdapter() {
		return this.authorAdapter;
	}

	/**
	 * @param authorAdapter the authorAdapter to set
	 */
	public void setAuthorAdapter(final Adapter authorAdapter) {
		this.authorAdapter = authorAdapter;
	}

	/**
	 * @return the codeAdapter
	 */
	public Adapter getCodeAdapter() {
		return this.codeAdapter;
	}

	/**
	 * @param codeAdapter the codeAdapter to set
	 */
	public void setCodeAdapter(final Adapter codeAdapter) {
		this.codeAdapter = codeAdapter;
	}

	/**
	 * @return the compilerAdapter
	 */
	public Adapter getCompilerAdapter() {
		return this.compilerAdapter;
	}

	/**
	 * @param compilerAdapter the compilerAdapter to set
	 */
	public void setCompilerAdapter(final Adapter compilerAdapter) {
		this.compilerAdapter = compilerAdapter;
	}

	/**
	 * @return the dependencyAdapter
	 */
	public Adapter getDependencyAdapter() {
		return this.dependencyAdapter;
	}

	/**
	 * @param dependencyAdapter the dependencyAdapter to set
	 */
	public void setDependencyAdapter(final Adapter dependencyAdapter) {
		this.dependencyAdapter = dependencyAdapter;
	}

	/**
	 * @return the descriptorAdapter
	 */
	public Adapter getDescriptorAdapter() {
		return this.descriptorAdapter;
	}

	/**
	 * @param descriptorAdapter the descriptorAdapter to set
	 */
	public void setDescriptorAdapter(final Adapter descriptorAdapter) {
		this.descriptorAdapter = descriptorAdapter;
	}

	/**
	 * @return the documentRootAdapter
	 */
	public Adapter getDocumentRootAdapter() {
		return this.documentRootAdapter;
	}

	/**
	 * @param documentRootAdapter the documentRootAdapter to set
	 */
	public void setDocumentRootAdapter(final Adapter documentRootAdapter) {
		this.documentRootAdapter = documentRootAdapter;
	}

	/**
	 * @return the humanLanguageAdapter
	 */
	public Adapter getHumanLanguageAdapter() {
		return this.humanLanguageAdapter;
	}

	/**
	 * @param humanLanguageAdapter the humanLanguageAdapter to set
	 */
	public void setHumanLanguageAdapter(final Adapter humanLanguageAdapter) {
		this.humanLanguageAdapter = humanLanguageAdapter;
	}

	/**
	 * @return the implementationAdapter
	 */
	public Adapter getImplementationAdapter() {
		return this.implementationAdapter;
	}

	/**
	 * @param implementationAdapter the implementationAdapter to set
	 */
	public void setImplementationAdapter(final Adapter implementationAdapter) {
		this.implementationAdapter = implementationAdapter;
	}

	/**
	 * @return the implRefAdapter
	 */
	public Adapter getImplRefAdapter() {
		return this.implRefAdapter;
	}

	/**
	 * @param implRefAdapter the implRefAdapter to set
	 */
	public void setImplRefAdapter(final Adapter implRefAdapter) {
		this.implRefAdapter = implRefAdapter;
	}

	/**
	 * @return the localFileAdapter
	 */
	public Adapter getLocalFileAdapter() {
		return this.localFileAdapter;
	}

	/**
	 * @param localFileAdapter the localFileAdapter to set
	 */
	public void setLocalFileAdapter(final Adapter localFileAdapter) {
		this.localFileAdapter = localFileAdapter;
	}

	/**
	 * @return the osAdapter
	 */
	public Adapter getOsAdapter() {
		return this.osAdapter;
	}

	/**
	 * @param osAdapter the osAdapter to set
	 */
	public void setOsAdapter(final Adapter osAdapter) {
		this.osAdapter = osAdapter;
	}

	/**
	 * @return the processAdapter
	 */
	public Adapter getProcessorAdapter() {
		return this.processorAdapter;
	}

	/**
	 * @param processAdapter the processAdapter to set
	 */
	public void setProcessorAdapter(final Adapter processAdapter) {
		this.processorAdapter = processAdapter;
	}

	/**
	 * @return the programmingAdapter
	 */
	public Adapter getProgrammingLanguageAdapter() {
		return this.programmingLanguageAdapter;
	}

	/**
	 * @param programmingAdapter the programmingAdapter to set
	 */
	public void setProgrammingLanguageAdapter(final Adapter programmingAdapter) {
		this.programmingLanguageAdapter = programmingAdapter;
	}

	/**
	 * @return the propertyAdapter
	 */
	public Adapter getPropertyFileAdapter() {
		return this.propertyFileAdapter;
	}

	/**
	 * @param propertyAdapter the propertyAdapter to set
	 */
	public void setPropertyFileAdapter(final Adapter propertyAdapter) {
		this.propertyFileAdapter = propertyAdapter;
	}

	/**
	 * @return the runtimeAdapter
	 */
	public Adapter getRuntimeAdapter() {
		return this.runtimeAdapter;
	}

	/**
	 * @param runtimeAdapter the runtimeAdapter to set
	 */
	public void setRuntimeAdapter(final Adapter runtimeAdapter) {
		this.runtimeAdapter = runtimeAdapter;
	}

	/**
	 * @return the usesDeviceAdapter
	 */
	public Adapter getUsesDeviceAdapter() {
		return this.usesDeviceAdapter;
	}

	/**
	 * @param usesDeviceAdapter the usesDeviceAdapter to set
	 */
	public void setUsesDeviceAdapter(final Adapter usesDeviceAdapter) {
		this.usesDeviceAdapter = usesDeviceAdapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createSoftPkgAdapter() {
		if (this.softPkgAdapter != null) {
			return this.softPkgAdapter;
		} else {
			return super.createSoftPkgAdapter();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createAuthorAdapter() {
		if (this.authorAdapter != null) {
			return this.authorAdapter;
		}
		return super.createAuthorAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createCodeAdapter() {
		if (this.codeAdapter != null) {
			return this.codeAdapter;
		}
		return super.createCodeAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createCompilerAdapter() {
		if (this.compilerAdapter != null) {
			return this.compilerAdapter;
		}
		return super.createCompilerAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createDependencyAdapter() {
		if (this.dependencyAdapter != null) {
			return this.dependencyAdapter;
		}
		return super.createDependencyAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createDescriptorAdapter() {
		if (this.descriptorAdapter != null) {
			return this.descriptorAdapter;
		}
		return super.createDescriptorAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createHumanLanguageAdapter() {
		if (this.humanLanguageAdapter != null) {
			return this.humanLanguageAdapter;
		}
		return super.createHumanLanguageAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createImplementationAdapter() {
		if (this.implementationAdapter != null) {
			return this.implementationAdapter;
		}
		return super.createImplementationAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createImplRefAdapter() {
		if (this.implRefAdapter != null) {
			return this.implRefAdapter;
		}
		return super.createImplRefAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createLocalFileAdapter() {
		if (this.localFileAdapter != null) {
			return this.localFileAdapter;
		}
		return super.createLocalFileAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createOsAdapter() {
		if (this.osAdapter != null) {
			return this.osAdapter;
		}
		return super.createOsAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createProcessorAdapter() {
		if (this.processorAdapter != null) {
			return this.processorAdapter;
		}
		return super.createProcessorAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createProgrammingLanguageAdapter() {
		if (this.programmingLanguageAdapter != null) {
			return this.programmingLanguageAdapter;
		}
		return super.createProgrammingLanguageAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createPropertyFileAdapter() {
		if (this.propertyFileAdapter != null) {
			return this.propertyFileAdapter;
		}
		return super.createPropertyFileAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createPropertyRefAdapter() {
		if (this.propertyRefAdapter != null) {
			return this.propertyRefAdapter;
		}
		return super.createPropertyRefAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createRuntimeAdapter() {
		if (this.runtimeAdapter != null) {
			return this.runtimeAdapter;
		}
		return super.createRuntimeAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createSoftPkgRefAdapter() {
		if (this.softPkgRefAdapter != null) {
			return this.softPkgRefAdapter;
		}
		return super.createSoftPkgRefAdapter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Adapter createUsesDeviceAdapter() {
		if (this.usesDeviceAdapter != null) {
			return this.usesDeviceAdapter;
		}
		return super.createUsesDeviceAdapter();
	}

}
