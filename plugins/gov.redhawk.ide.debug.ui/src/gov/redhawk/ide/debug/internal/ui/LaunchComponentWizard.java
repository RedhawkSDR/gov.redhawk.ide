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

import gov.redhawk.ide.debug.ScaDebugLaunchConstants;
import gov.redhawk.ide.debug.ui.LaunchUtil;
import gov.redhawk.ide.debug.ui.ScaDebugUiPlugin;
import gov.redhawk.ide.sdr.SoftPkgRegistry;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

public class LaunchComponentWizard extends Wizard {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private Implementation implementation;
	private SoftPkg softPkg;
	private SoftPkgRegistry spdContainer;
	private SoftPkgSelectionPage spdPage;
	private ImplementationSelectionPage implPage;
	private boolean autoStart;
	private int timeout = ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT;
	private String debugLevel = "Default";
	private LocalComponentPropertyEditWizardPage propertiesPage;
	private ComponentCommonLaunchConfigurationWizardPage commonPage;
	private boolean saveRunConfiguration;
	private boolean showAutoStart = true;

	public LaunchComponentWizard() {
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		if (this.implementation == null) {
			if (this.softPkg == null) {
				spdPage = new SoftPkgSelectionPage(this);
				addPage(spdPage);
			}
			implPage = new ImplementationSelectionPage(this);
			addPage(implPage);
		}
		propertiesPage = new LocalComponentPropertyEditWizardPage(this);
		addPage(propertiesPage);
		commonPage = new ComponentCommonLaunchConfigurationWizardPage(this, showAutoStart);
		addPage(commonPage);
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					ILaunchConfigurationWorkingCopy config;
					try {
						config = LaunchUtil.createLaunchConfiguration(implementation);
						ScaLaunchConfigurationUtil.saveProperties(config, propertiesPage.getPropertyContainer());
						config.setAttribute(ScaLaunchConfigurationConstants.ATT_START, isAutoStart());
						config.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, getTimeout());
						config.setAttribute(ScaDebugLaunchConstants.ATT_DEBUG_LEVEL, getDebugLevel());
						
						ILaunchConfiguration finalConfig = config;
						if (isSaveRunConfiguration()) {
							finalConfig = config.doSave();	
						} 
						
						finalConfig.launch("run", monitor, false, true);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e1) {
			StatusManager.getManager().handle(new Status(Status.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to launch", e1.getCause()),
				StatusManager.SHOW | StatusManager.LOG);
			return false;
		} catch (InterruptedException e1) {
			return false;
		}
		return true;
	}

	@Override
	public IWizardPage getPreviousPage(IWizardPage page) {
		if (page == propertiesPage) {
			if (softPkg != null && softPkg.getImplementation().size() == 1) {
				return spdPage;
			} else {
				return super.getPreviousPage(page);
			}
		}
		return super.getPreviousPage(page);
	}

	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		if (page == spdPage) {
			if (softPkg != null && softPkg.getImplementation().size() == 1) {
				return propertiesPage;
			} else {
				return super.getNextPage(page);
			}
		}
		return super.getNextPage(page);
	}

	public void setSoftPkg(SoftPkg element) {
		if (element == this.softPkg) {
			return;
		}
		SoftPkg oldValue = this.softPkg;
		this.softPkg = element;
		pcs.firePropertyChange("softPkg", oldValue, softPkg);
		
		// Only pre-choose implementation if there is exactly one to choose
		if (softPkg != null && softPkg.getImplementation().size() == 1) {
			setImplementation(this.softPkg.getImplementation().get(0));
		} else {
			setImplementation(null);
		}
	}

	public SoftPkg getSoftPkg() {
		return softPkg;
	}

	public void setImplementation(Implementation element) {
		if (element == this.implementation) {
			return;
		}
		if (element != null) {
			setSoftPkg(element.getSoftPkg());
		}
		Implementation oldValue = this.implementation;
		this.implementation = element;
		pcs.firePropertyChange("implementation", oldValue, implementation);
	}

	public Implementation getImplementation() {
		return implementation;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}

	public SoftPkgRegistry getSpdContainer() {
		return spdContainer;
	}

	public void setSpdContainer(SoftPkgRegistry spdContainer) {
		if (spdContainer == this.spdContainer) {
			return;
		}
		SoftPkgRegistry oldValue = this.spdContainer;
		this.spdContainer = spdContainer;
		pcs.firePropertyChange("spdContainer", oldValue, this.spdContainer);
	}
	
	public void setAutoStart(boolean autoStart) {
		boolean oldValue = this.autoStart;
		this.autoStart = autoStart;
		pcs.firePropertyChange("autoStart", oldValue, this.autoStart);
	}
	
	public boolean isAutoStart() {
		return autoStart;
	}
	
	public void hideAutoStartControl() {
		showAutoStart = false;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		int oldValue = this.timeout;
		this.timeout = timeout;
		pcs.firePropertyChange("timeout", oldValue, this.timeout);
	}
	
	public String getDebugLevel() {
		return debugLevel;
	}
	
	public void setDebugLevel(String debugLevel) {
		String oldValue = this.debugLevel;
		this.debugLevel = debugLevel;
		pcs.firePropertyChange("debugLevel", oldValue, this.debugLevel);
	}

	public boolean isSaveRunConfiguration() {
		return saveRunConfiguration;
	}

	public void setSaveRunConfiguration(boolean saveRunConfiguration) {
		boolean oldValue = this.saveRunConfiguration;
		this.saveRunConfiguration = saveRunConfiguration;
		pcs.firePropertyChange("saveRunConfiguration", oldValue, this.saveRunConfiguration);
	}
	

}
