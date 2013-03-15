/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9042 $
 */
package gov.redhawk.ide.debug.internal;

import gov.redhawk.ide.debug.ScaDebugPlugin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;

import ExtendedCF.Sandbox;

public class IdeRefDynamicVariableResolver implements IDynamicVariableResolver {

	public String resolveValue(IDynamicVariable variable, String argument) throws CoreException {
		Sandbox sandbox = ScaDebugPlugin.getInstance().getSandbox();
		if (sandbox != null) {
			return sandbox.toString();
		} else {
			return null;
		}
	}

}
