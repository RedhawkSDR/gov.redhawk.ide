package gov.redhawk.ide.tests.ui.stubs;

import org.eclipse.emf.common.util.URI;
import org.ossie.component.Device;

import CF.DataType;
import CF.ExecutableDeviceOperations;
import CF.FileSystem;
import CF.InvalidFileName;
import CF.DevicePackage.InvalidState;
import CF.ExecutableDevicePackage.ExecuteFail;
import CF.ExecutableDevicePackage.InvalidFunction;
import CF.ExecutableDevicePackage.InvalidOptions;
import CF.ExecutableDevicePackage.InvalidParameters;
import CF.ExecutableDevicePackage.InvalidProcess;
import CF.LoadableDevicePackage.InvalidLoadKind;
import CF.LoadableDevicePackage.LoadFail;
import CF.LoadableDevicePackage.LoadType;

public class AnalogDevice extends Device implements ExecutableDeviceOperations{

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load(FileSystem fs, String fileName, LoadType loadKind) throws InvalidState, InvalidLoadKind, InvalidFileName, LoadFail {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unload(String fileName) throws InvalidState, InvalidFileName {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void terminate(int processId) throws InvalidProcess, InvalidState {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int execute(String name, DataType[] options, DataType[] parameters) throws InvalidState, InvalidFunction, InvalidParameters, InvalidOptions,
		InvalidFileName, ExecuteFail {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public String softwareProfile() {
		URI uri = URI.createPlatformPluginURI("gov.redhawk.ide.tests.ui/resources/analogDevice/analogDevice.spd.xml", true);
		return uri.toString();
	}
	
	@Override
	public String identifier() {
		return "analogDevice";
	}
	
	@Override
	public String label() {
		return "analogDevice";
	}

}
