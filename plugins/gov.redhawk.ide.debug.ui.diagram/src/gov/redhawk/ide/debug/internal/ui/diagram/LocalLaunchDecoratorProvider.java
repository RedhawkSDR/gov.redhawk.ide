/*******************************************************************************
 * This file is protected by Copyright. 
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under 
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package gov.redhawk.ide.debug.internal.ui.diagram;

import gov.redhawk.ide.debug.LocalLaunch;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ui.diagram.LocalScaDiagramPlugin;
import gov.redhawk.model.sca.ScaComponent;
import gov.redhawk.sca.util.PluginUtil;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.diagram.part.SadVisualIDRegistry;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.draw2d.ImageFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RelativeLocator;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.diagram.ui.editparts.GraphicalEditPart;
import org.eclipse.gmf.runtime.diagram.ui.parts.DiagramEditDomain;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.AbstractDecorator;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.CreateDecoratorsOperation;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoration;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorProvider;
import org.eclipse.gmf.runtime.diagram.ui.services.decorator.IDecoratorTarget;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.IMapMode;
import org.eclipse.gmf.runtime.draw2d.ui.mapmode.MapModeUtil;
import org.eclipse.gmf.runtime.notation.Edge;
import org.eclipse.gmf.runtime.notation.View;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class LocalLaunchDecoratorProvider extends AbstractProvider implements IDecoratorProvider {

	private static class LocalLaunchDecorator extends AbstractDecorator {

		private final Label toolTip;

		public LocalLaunchDecorator(final IDecoratorTarget decoratorTarget) {
			super(decoratorTarget);
			this.toolTip = new Label("Component launched in debug mode.");
		}

		public void activate() {

		}

		public void refresh() {
			removeDecoration();
			final View view = (View) getDecoratorTarget().getAdapter(View.class);
			if (view == null || view.eResource() == null) {
				return;
			}
			final EditPart editPart = (EditPart) getDecoratorTarget().getAdapter(EditPart.class);
			if (editPart == null || editPart.getViewer() == null) {
				return;
			}
			if (!(view instanceof Edge) && !view.isSetElement()) {
				return;
			} else if (view.getElement() instanceof SadComponentInstantiation) {
				final ScaComponent comp = PluginUtil.adapt(ScaComponent.class, view.getElement());

				if (comp instanceof LocalLaunch) {
					final LocalLaunch launch = (LocalLaunch) comp;
					if (launch.getLaunch() == null) {
						launch.eAdapters().add(new AdapterImpl() {
							@Override
							public void notifyChanged(final Notification msg) {
								switch (msg.getFeatureID(LocalLaunch.class)) {
								case ScaDebugPackage.LOCAL_LAUNCH__LAUNCH:
									launch.eAdapters().remove(this);
									refresh();
									break;
								default:
									break;
								}
							}
						});
					} else if (ILaunchManager.DEBUG_MODE.equals(launch.getLaunch().getLaunchMode())) {
						final Image image = LocalScaDiagramPlugin.getImage(LocalScaDiagramPlugin.IMG_DEBUG);
						final IMapMode mm = MapModeUtil.getMapMode(((GraphicalEditPart) editPart).getFigure());
						final ImageFigure figure = new ImageFigure(image);
						figure.setSize(mm.DPtoLP(image.getBounds().width), mm.DPtoLP(image.getBounds().height));

						final IDecoration newValue = getDecoratorTarget().addDecoration(figure,
						        new RelativeLocator(((GraphicalEditPart) editPart).getFigure(), 0.88, 0.1),
						        false);

						newValue.setIgnoreParentVisibility(false);
						setDecoration(newValue);
						getDecoration().setToolTip(this.toolTip);

						final Point point = getDecoration().getLocation();
						point.x = point.x - 100;
						getDecoration().setLocation(point);
					}
				}
			}

		}
	}

	private static final String KEY = "launchMode";

	public boolean provides(final IOperation operation) {
		if (!(operation instanceof CreateDecoratorsOperation)) {
			return false;
		}
		final IDecoratorTarget decoratorTarget = ((CreateDecoratorsOperation) operation).getDecoratorTarget();
		final View view = (View) decoratorTarget.getAdapter(View.class);
		return view != null && mil.jpeojtrs.sca.sad.diagram.edit.parts.SadComponentInstantiationEditPart.VISUAL_ID == SadVisualIDRegistry.getVisualID(view);
	}

	public void createDecorators(final IDecoratorTarget decoratorTarget) {
		final EditPart editPart = (EditPart) decoratorTarget.getAdapter(EditPart.class);
		if (editPart instanceof GraphicalEditPart || editPart instanceof AbstractConnectionEditPart) {
			final Object model = editPart.getModel();
			if (model instanceof View) {
				final View view = (View) model;
				if (!(view instanceof Edge) && !view.isSetElement()) {
					return;
				} else if (view.getElement() instanceof SadComponentInstantiation) {
					final EditDomain editDomain = editPart.getViewer().getEditDomain();
					if (((DiagramEditDomain) editDomain).getEditorPart() instanceof SandboxDiagramEditor) {
						decoratorTarget.installDecorator(LocalLaunchDecoratorProvider.KEY, new LocalLaunchDecorator(decoratorTarget));
					}
				}
			}
		}

	}

}
