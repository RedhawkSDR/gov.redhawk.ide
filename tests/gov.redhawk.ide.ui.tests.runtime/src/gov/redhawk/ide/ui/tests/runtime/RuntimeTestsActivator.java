package gov.redhawk.ide.ui.tests.runtime;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

public class RuntimeTestsActivator extends Plugin {
	
	public static final String ID = "gov.redhawk.ide.ui.tests.runtime";

	private static RuntimeTestsActivator instance;
	
	@Override
	public void start(BundleContext context) throws Exception {
		instance = this;
		super.start(context);
	}
	
	public static RuntimeTestsActivator getInstance() {
		return instance;
	}
	
	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		instance = null;
	}

}
