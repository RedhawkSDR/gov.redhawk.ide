package gov.redhawk.ide.debug.impl.commands;

import java.util.Map;

import CF.DeviceManagerPackage.ServiceType;
import gov.redhawk.ide.debug.ScaDebugFactory;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaService;
import gov.redhawk.model.sca.commands.MergeServicesCommand;

public class LocalMergeServicesCommand extends MergeServicesCommand {

	public LocalMergeServicesCommand(ScaDeviceManager provider, Map<String, ServiceType> newServices) {
	    super(provider, newServices);
    }

	
	@Override
	protected ScaService createScaService() {
	    return ScaDebugFactory.eINSTANCE.createLocalScaService();
	}
}
