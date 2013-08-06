/**
 * 
 */
package $packageName$;

import gov.redhawk.sca.util.PluginUtil;
import gov.redhawk.model.sca.ScaComponent;

import org.eclipse.jface.viewers.IFilter;

/**
 * An example showing how to create a property section.
 */
public class $filterClassName$ implements IFilter {

	public boolean select(Object toTest) {
		ScaComponent component = PluginUtil.adapt(ScaComponent.class, toTest);
		if (component != null) {
			return component.getProfileObj().getId().equals("$contentTypeProfileId$");
		}
		return false;
	}

}
