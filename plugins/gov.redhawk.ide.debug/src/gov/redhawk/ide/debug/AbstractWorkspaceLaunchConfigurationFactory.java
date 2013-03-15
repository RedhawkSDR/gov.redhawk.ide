/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;

import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * @since 2.0
 */
public abstract class AbstractWorkspaceLaunchConfigurationFactory extends AbstractLaunchConfigurationFactory {

	@Override
	public ILaunchConfigurationWorkingCopy createLaunchConfiguration(final String name, final String implId, final SoftPkg spd) throws CoreException {
		final ILaunchConfigurationWorkingCopy retVal = super.createLaunchConfiguration(name, implId, spd);

		final IFile resource = getResource(spd.eResource().getURI());
		retVal.setMappedResources(new IResource[] {
			resource.getProject()
		});

		return retVal;
	}

	public boolean supports(final SoftPkg spd, final String implId) {
		final URI uri = EcoreUtil.getURI(spd);
		return uri.isPlatformResource();
	}

	@Override
	protected String getProfile(final SoftPkg spd) {
		return spd.eResource().getURI().toPlatformString(true);
	}

	protected IFile getResource(final URI uri) {
		final String path = uri.toPlatformString(true);
		return ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
	}
}
