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
package gov.redhawk.ide.dcd.internal.ui.editor.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.dcd.CompositePartOfDevice;
import mil.jpeojtrs.sca.dcd.DcdComponentPlacement;
import mil.jpeojtrs.sca.dcd.DcdFactory;
import mil.jpeojtrs.sca.dcd.DcdPackage;
import mil.jpeojtrs.sca.dcd.DcdPartitioning;
import mil.jpeojtrs.sca.dcd.provider.DcdComponentPlacementItemProvider;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.command.CompoundCommand;
import org.eclipse.emf.common.command.IdentityCommand;
import org.eclipse.emf.common.command.UnexecutableCommand;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EStructuralFeature.Setting;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.edit.command.CommandParameter;
import org.eclipse.emf.edit.command.DragAndDropCommand;
import org.eclipse.emf.edit.command.DragAndDropCommand.Detail;
import org.eclipse.emf.edit.command.SetCommand;
import org.eclipse.emf.edit.domain.EditingDomain;

/**
 * The Class ImplementationItemProvider.
 */
public class DevicesSectionComponentPlacementItemProvider extends DcdComponentPlacementItemProvider {
	private static final float DND_INSERT_BEGIN = 0.2f;
	private static final float DND_INSERT_END = 0.8f;

	/**
	 * The Constructor.
	 * 
	 * @param adapterFactory the adapter factory
	 * @param page the page
	 */
	public DevicesSectionComponentPlacementItemProvider(final AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getText(final Object object) {
		final DcdComponentPlacement comp = (DcdComponentPlacement) object;
		if (!comp.getComponentInstantiation().isEmpty()) {
			return comp.getComponentInstantiation().get(0).getId();
		}
		final String id = comp.getComponentFileRef().getRefid();
		if (id != null && id.length() > 0) {
			return id;
		}

		return super.getText(object);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection< ? extends EStructuralFeature> getChildrenFeatures(final Object object) {
		if (this.childrenFeatures == null) {
			this.childrenFeatures = new ArrayList<EStructuralFeature>();
		}
		return this.childrenFeatures;
	}

	private List<DcdComponentPlacement> getChildComponent(final DcdComponentPlacement cp) {
		final Collection<Setting> references = EcoreUtil.UsageCrossReferencer.find(cp, cp.eResource());
		final List<DcdComponentPlacement> children = new ArrayList<DcdComponentPlacement>();
		for (final Setting setting : references) {
			if (setting.getEObject() instanceof DcdComponentPlacement) {
				children.add((DcdComponentPlacement) setting.getEObject());
			}
		}
		return children;
	}

	@Override
	public Command createCommand(final Object object, final EditingDomain domain, final Class< ? extends Command> commandClass,
	        final CommandParameter commandParameter) {

		if (commandClass == DragAndDropCommand.class && !commandParameter.getCollection().isEmpty()) {
			if (!DcdComponentPlacement.Util.isAggregateDevice((DcdComponentPlacement) commandParameter.getOwner())) {
				return UnexecutableCommand.INSTANCE;
			}
			final DragAndDropCommand.Detail det = (Detail) commandParameter.getFeature();
			return new DragAndDropCommand(domain, commandParameter.getOwner(), det.location, det.operations, det.operation, commandParameter.getCollection(),
			        false) {
				@Override
				protected Collection< ? > getChildren(final Object object) {
					// If this is a component placement, return the children for the placement, otherwise delegate
					if (object instanceof DcdComponentPlacement) {
						final DcdComponentPlacement cp = (DcdComponentPlacement) object;
						return getChildComponent(cp);
					}
					return super.getChildren(object);
				}

				@Override
				protected Object getParent(final Object object) {
					// If this is a component placement, return the parent for the placement(Partitioning or Parent device), otherwise delegate
					if (object instanceof DcdComponentPlacement) {
						final DcdComponentPlacement place = (DcdComponentPlacement) object;
						if (place.getParentDevice() != null) {
							return place.getParentDevice();
						}
						return place.eContainer();
					}
					return super.getParent(object);
				}

				@Override
				protected boolean prepareDropMoveOn() {
					// If this should be an insert instead of a move, bypass
					if ((this.location > DevicesSectionComponentPlacementItemProvider.DND_INSERT_BEGIN)
					        && (this.location < DevicesSectionComponentPlacementItemProvider.DND_INSERT_END)) {
						// Here, we need to set the parent
						final DcdComponentPlacement parentId = ((DcdComponentPlacement) this.owner);
						return doIt(parentId);
					}
					this.dragCommand = IdentityCommand.INSTANCE;
					this.dropCommand = UnexecutableCommand.INSTANCE;
					return false;
				}

				@Override
				protected boolean prepareDropMoveInsert(final Object parent, final java.util.Collection< ? > children, final int index) {
					// If we're dropping on a Partitioning, remove the CompositePartOfDevice
					if (parent instanceof DcdPartitioning) {
						final boolean result = super.prepareDropMoveInsert(parent, children, index);
						if (result) {
							final CompoundCommand drop = new CompoundCommand();
							drop.append(this.dropCommand);
							for (final Object place : this.collection) {
								if (place instanceof DcdComponentPlacement) {
									drop.append(SetCommand.create(this.domain, place, DcdPackage.Literals.DCD_COMPONENT_PLACEMENT__COMPOSITE_PART_OF_DEVICE,
									        SetCommand.UNSET_VALUE));
								}
							}
							this.dropCommand = drop;
						}
						return result;
					}
					// Otherwise, get the new parentId from the parent
					final DcdComponentPlacement parentId = ((DcdComponentPlacement) parent);
					return doIt(parentId);
				}

				private boolean doIt(final DcdComponentPlacement parent) {
					if (isCrossDomain()) {
						this.dragCommand = IdentityCommand.INSTANCE;
						this.dropCommand = UnexecutableCommand.INSTANCE;
					} else {
						final CompoundCommand drop = new CompoundCommand();
						// Loop through everything being dropped
						for (final Object o : this.collection) {
							// If it's a ComponentPlacement, set the parentId to the passed in one(of the object being dropped on)
							if (o instanceof DcdComponentPlacement) {
								// Check for recursive reference
								if (!EcoreUtil.UsageCrossReferencer.find(parent, (EObject) o).isEmpty()) {
									this.dragCommand = UnexecutableCommand.INSTANCE;
									this.dropCommand = UnexecutableCommand.INSTANCE;
									return false;
								}
								final CompositePartOfDevice cpod = DcdFactory.eINSTANCE.createCompositePartOfDevice();
								cpod.setRefID(parent.getComponentInstantiation().get(0).getId());
								drop.append(SetCommand.create(this.domain, o, DcdPackage.Literals.DCD_COMPONENT_PLACEMENT__COMPOSITE_PART_OF_DEVICE, cpod));
							}
						}
						// If we processed something, set the drop command
						if (!drop.isEmpty()) {
							this.dropCommand = drop;
							if (analyzeForNonContainment(this.dropCommand)) {
								this.dropCommand.dispose();
								this.dropCommand = UnexecutableCommand.INSTANCE;
							}
						} else {
							// Otherwise we can't do anything
							this.dropCommand = UnexecutableCommand.INSTANCE;
						}
						this.dragCommand = IdentityCommand.INSTANCE;
					}

					final boolean result = this.dragCommand.canExecute() && this.dropCommand.canExecute();
					return result;
				}

			};
		} else if (commandClass == SetCommand.class) {
			final SetCommand cmd = new SetCommand(domain, commandParameter.getEOwner(), commandParameter.getEStructuralFeature(), commandParameter.getValue()) {
				@Override
				public boolean doCanExecute() {
					if (commandParameter.getValue() instanceof CompositePartOfDevice) {
						final DcdComponentPlacement draggedObject = (DcdComponentPlacement) this.owner;
						final CompositePartOfDevice cpod = ((CompositePartOfDevice) commandParameter.getValue());
						final String newParentId = cpod.getRefID();
						final DcdComponentPlacement newParent = getPlacement((DcdPartitioning) draggedObject.eContainer(), newParentId);

						if (!(DcdComponentPlacement.Util.isAggregateDevice(newParent))) {
							return false;
						}

						if (findParent(newParent, getChildComponent(draggedObject))) {
							return false;
						}
					}
					return super.doCanExecute();
				}

				@Override
				public boolean doCanUndo() {
					return true;
				}

				@Override
				public void doRedo() {
					final DcdComponentPlacement place = (DcdComponentPlacement) this.getOwner();
					if (this.getValue() != SetCommand.UNSET_VALUE) {
						place.setCompositePartOfDevice((CompositePartOfDevice) this.getValue());
					} else {
						place.setCompositePartOfDevice(null);
					}
				}

				@Override
				public void doUndo() {
					final DcdComponentPlacement place = (DcdComponentPlacement) this.getOwner();
					if (this.getOldValue() != SetCommand.UNSET_VALUE) {
						place.setCompositePartOfDevice((CompositePartOfDevice) this.getOldValue());
					} else {
						place.setCompositePartOfDevice(null);
					}
				}

				private boolean findParent(final DcdComponentPlacement newParent, final List<DcdComponentPlacement> children) {
					for (final DcdComponentPlacement place : children) {
						if ((place == newParent) || findParent(newParent, getChildComponent(place))) {
							return true;
						}
					}
					return false;
				}

				private DcdComponentPlacement getPlacement(final DcdPartitioning parent, final String instId) {
					for (final DcdComponentPlacement place : parent.getComponentPlacement()) {
						if (instId.equals(place.getComponentInstantiation().get(0).getId())) {
							return place;
						}
					}
					return null;
				}
			};
			return cmd;
		}
		return super.createCommand(object, domain, commandClass, commandParameter);
	}

}
