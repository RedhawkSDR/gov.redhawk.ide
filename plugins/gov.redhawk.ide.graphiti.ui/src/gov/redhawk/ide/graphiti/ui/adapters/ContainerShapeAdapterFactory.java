package gov.redhawk.ide.graphiti.ui.adapters;

import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.graphiti.ui.diagram.util.DUtil;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.model.sca.ScaDevice;
import gov.redhawk.model.sca.ScaDeviceManager;
import gov.redhawk.model.sca.ScaModelPlugin;
import gov.redhawk.model.sca.ScaWaveform;
import mil.jpeojtrs.sca.dcd.DcdComponentInstantiation;
import mil.jpeojtrs.sca.partitioning.ComponentInstantiation;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

/**
 * Adapts an {@link AbstractGraphicalEditPart} from a waveform / node diagram to an {@link ScaComponent} or
 * an {@link ScaDevice}.
 */
public class ContainerShapeAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { ScaComponent.class, ScaDevice.class };

	@Override
	@SuppressWarnings("unchecked")
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		// Must be an AbstractGraphicalEditPart whose model object is a RHContainerShape
		if (!(adaptableObject instanceof AbstractGraphicalEditPart)) {
			return null;
		}
		Object model = ((AbstractGraphicalEditPart) adaptableObject).getModel();
		if (!(model instanceof RHContainerShape)) {
			return null;
		}

		// Go from the Graphiti model object to the Redhawk model object
		ComponentInstantiation ci = (ComponentInstantiation) DUtil.getBusinessObject((RHContainerShape) model);
		if (ci == null || ci.eResource() == null) {
			return null;
		}

		final String myId = ci.getId();
		final URI uri = ci.eResource().getURI();
		final Map<String, String> query = QueryParser.parseQuery(uri.query());
		final String ref = query.get(ScaFileSystemConstants.QUERY_PARAM_WF);

		// Check the type of the instantiation as well as the adapter type to determine the conversion
		if (ci instanceof SadComponentInstantiation && adapterType.isAssignableFrom(ScaComponent.class)) {
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, ref);
			if (waveform != null) {
				for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
					final String scaComponentId = component.identifier();
					if (scaComponentId.startsWith(myId)) {
						return (T) component;
					}
				}
			}
		} else if (ci instanceof DcdComponentInstantiation && adapterType.isAssignableFrom(ScaDevice.class)) {
			final ScaDeviceManager devMgr = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, ref);
			if (devMgr != null) {
				for (final ScaDevice< ? > dev : GraphitiAdapterUtil.safeFetchComponents(devMgr)) {
					final String scaComponentId = dev.getIdentifier();
					if (scaComponentId.startsWith(myId)) {
						return (T) dev;
					}
				}
			}
		}

		return null;
	}

	@Override
	public Class< ? >[] getAdapterList() {
		return ADAPTER_TYPES;
	}

}
