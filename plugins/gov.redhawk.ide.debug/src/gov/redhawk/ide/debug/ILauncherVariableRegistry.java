/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;

/**
 * @since 2.0
 * 
 */
public interface ILauncherVariableRegistry {

	public ILauncherVariableDesc getDesc(String id);

	ILauncherVariableDesc[] getDescriptors();
}
