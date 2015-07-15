package gov.redhawk.ide.graphiti.ui.adapters;

import java.util.Map;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import gov.redhawk.ide.debug.LocalLaunch;
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
 * Can adapt either:
 * <ul>
 * <li>{@link AbstractGraphicalEditPart} (Graphiti UI part)</li>
 * <li>{@link RHContainerShape} (our Graphiti model object)</li>
 * </ul>
 * from the diagrams to the following types (and a few of their super types):
 * <ul>
 * <li>{@link ScaComponent}</li>
 * <li>{@link ScaDevice}</li>
 * <li>{@link LocalLaunch}</li>
 * </ul>
 */
public class ContainerShapeAdapterFactory implements IAdapterFactory {

	private static final Class< ? >[] ADAPTER_TYPES = new Class< ? >[] { ScaComponent.class, ScaDevice.class, LocalLaunch.class };

	@Override
	@SuppressWarnings("unchecked")
	public < T > T getAdapter(Object adaptableObject, Class<T> adapterType) {
		// We convert the Graphiti UI part -> Graphiti model object, if not already done for us
		Object model;
		if (adaptableObject instanceof AbstractGraphicalEditPart) {
			model = ((AbstractGraphicalEditPart) adaptableObject).getModel();
		} else {
			model = adaptableObject;
		}
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
		if (ci instanceof SadComponentInstantiation) {
			final ScaWaveform waveform = ScaModelPlugin.getDefault().findEObject(ScaWaveform.class, ref);
			if (waveform != null) {
				for (final ScaComponent component : GraphitiAdapterUtil.safeFetchComponents(waveform)) {
					final String scaComponentId = component.identifier();
					if (scaComponentId.startsWith(myId)) {
						if (adapterType.isAssignableFrom(ScaComponent.class) ||
								(adapterType.isAssignableFrom(LocalLaunch.class) && component instanceof LocalLaunch)) {
							return (T) component;
						} else {
							return null;
						}
					}
				}
			}
		} else if (ci instanceof DcdComponentInstantiation) {
			final ScaDeviceManager devMgr = ScaModelPlugin.getDefault().findEObject(ScaDeviceManager.class, ref);
			if (devMgr != null) {
				for (final ScaDevice< ? > dev : GraphitiAdapterUtil.safeFetchComponents(devMgr)) {
					final String scaComponentId = dev.getIdentifier();
					if (scaComponentId.startsWith(myId)) {
						if (adapterType.isAssignableFrom(ScaDevice.class) ||
								(adapterType.isAssignableFrom(LocalLaunch.class) && dev instanceof LocalLaunch)) {
							return (T) dev;
						} else {
							return null;
						}
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
