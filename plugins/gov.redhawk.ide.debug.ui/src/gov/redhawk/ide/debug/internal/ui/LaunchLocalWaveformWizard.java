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
import gov.redhawk.ide.sdr.WaveformsContainer;
import gov.redhawk.sca.launch.ScaLaunchConfigurationConstants;
import gov.redhawk.sca.launch.ScaLaunchConfigurationUtil;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.InvocationTargetException;

import mil.jpeojtrs.sca.sad.SoftwareAssembly;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.statushandlers.StatusManager;

/**
 * 
 */
public class LaunchLocalWaveformWizard extends Wizard {

	private PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	private SoftwareAssembly sad;
	private WaveformsContainer waveformsContainer;
	private SoftwareAssemblySelectionPage selectSadPage;
	private LocalWaveformPropertyEditWizardPage propertiesPage;
	private boolean autoStart;
	private int timeout = ScaDebugLaunchConstants.DEFAULT_ATT_LAUNCH_TIMEOUT;
	private WaveformCommonLaunchConfigurationWizardPage commonPage;
	
	public LaunchLocalWaveformWizard() {
		setNeedsProgressMonitor(true);
		setWindowTitle("Launch Waveform");
	}
	
	@Override
	public void addPages() {
		if (sad == null) {
			selectSadPage = new SoftwareAssemblySelectionPage(this);
			addPage(selectSadPage);
		}
		propertiesPage = new LocalWaveformPropertyEditWizardPage("propPage", this);
		addPage(propertiesPage);
		commonPage = new WaveformCommonLaunchConfigurationWizardPage(this);
		addPage(commonPage);
	}
	

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						ILaunchConfigurationWorkingCopy config = LaunchUtil.createLaunchConfiguration(sad);
						ScaLaunchConfigurationUtil.saveProperties(config, propertiesPage.getPropertyContainer());

						config.setAttribute(ScaLaunchConfigurationConstants.ATT_START, isAutoStart());
						config.setAttribute(ScaDebugLaunchConstants.ATT_LAUNCH_TIMEOUT, getTimeout());

//						TODO Allow the user to save the launch configuration
//						ILaunchConfiguration savedConfig = config.doSave();
						config.launch("run", monitor, false, true);
					} catch (CoreException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException e) {
			StatusManager.getManager().handle(new Status(Status.ERROR, ScaDebugUiPlugin.PLUGIN_ID, "Failed to launch waveform ", e.getCause()), StatusManager.SHOW | StatusManager.LOG);
			return false;
		} catch (InterruptedException e) {
			return false;
		}
		return true;
	}

	public void setSoftwareAssembly(SoftwareAssembly element) {
		SoftwareAssembly oldValue = this.sad;
		this.sad = element;
		pcs.firePropertyChange("softwareAssembly", oldValue, this.sad);
	}

	public SoftwareAssembly getSoftwareAssembly() {
		return sad;
	}
	
	public WaveformsContainer getWaveformsContainer() {
		return waveformsContainer;
	}
	
	public void setWaveformsContainer(WaveformsContainer waveformsContainer) {
		WaveformsContainer oldValue = this.waveformsContainer;
		this.waveformsContainer = waveformsContainer;
		pcs.firePropertyChange("waveformsContainer", oldValue, this.waveformsContainer);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		pcs.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		pcs.removePropertyChangeListener(listener);
	}
	
	public void setAutoStart(boolean autoStart) {
		boolean oldValue = this.autoStart;
		this.autoStart = autoStart;
		pcs.firePropertyChange("autoStart", oldValue, this.autoStart);
	}
	
	public boolean isAutoStart() {
		return this.autoStart;
	}
	
	public int getTimeout() {
		return timeout;
	}
	
	public void setTimeout(int timeout) {
		int oldValue = this.timeout;
		this.timeout = timeout;
		pcs.firePropertyChange("timeout", oldValue, this.timeout);
	}

}
