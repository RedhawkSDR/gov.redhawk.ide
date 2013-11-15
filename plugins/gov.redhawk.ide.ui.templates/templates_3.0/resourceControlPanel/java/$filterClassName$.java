/**
 * 
 */
package $packageName$;

import gov.redhawk.sca.util.PluginUtil;
import org.eclipse.jface.viewers.IFilter;

/**
 * An example showing how to create a property section.
 */
public class $filterClassName$ implements IFilter {

	public boolean select(Object toTest) {
		$resourceClassName$ component = PluginUtil.adapt($resourceClassName$.class, toTest);
		if (component != null) {
			return component.getProfileObj().getId().equals("$contentTypeProfileId$");
		}
		return false;
	}

}
