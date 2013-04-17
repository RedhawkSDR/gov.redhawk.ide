/** 
 * REDHAWK HEADER
 *
 * Identification: $Revision: 9208 $
 */
package gov.redhawk.ide.debug;


/**
 * @since 3.0
 */
public interface ILauncherVariableDesc extends ILauncherVariableResolver {
	String getName();

	boolean prependName();
	
	String getDescription();
}
