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
// BEGIN GENERATED CODE
package gov.redhawk.ide.sdr.provider;

import gov.redhawk.eclipsecorba.library.LibraryFactory;
import gov.redhawk.ide.sdr.SdrFactory;
import gov.redhawk.ide.sdr.SdrPackage;
import gov.redhawk.ide.sdr.SdrRoot;

import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.util.QueryParser;
import mil.jpeojtrs.sca.util.ScaFileSystemConstants;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.ResourceLocator;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptor;
import org.eclipse.emf.edit.provider.ItemPropertyDescriptorDecorator;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;
import org.eclipse.emf.edit.provider.ViewerNotification;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * This is the item provider adapter for a {@link gov.redhawk.ide.sdr.SdrRoot} object.
 * <!-- begin-user-doc -->
 * <!-- end-user-doc -->
 * @generated
 */
public class SdrRootItemProvider extends ItemProviderAdapter implements IEditingDomainItemProvider, IStructuredItemContentProvider, ITreeItemContentProvider,
		IItemLabelProvider, IItemPropertySource {
	/**
	 * This constructs an instance from a factory and a notifier.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public SdrRootItemProvider(AdapterFactory adapterFactory) {
		super(adapterFactory);
	}

	/**
	 * This returns the property descriptors for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public List<IItemPropertyDescriptor> getPropertyDescriptors(Object object) {
		if (itemPropertyDescriptors == null) {
			super.getPropertyDescriptors(object);

			addLoadStatusPropertyDescriptor(object);
			addStatePropertyDescriptor(object);
			addDomainConfigurationPropertyDescriptor(object);
			addDevFileSystemRootPropertyDescriptor(object);
			addDomFileSystemRootPropertyDescriptor(object);
		}
		return itemPropertyDescriptors;
	}

	/**
	 * This adds a property descriptor for the Load Status feature.
	 * <!-- begin-user-doc -->
	 * @since 3.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected void addLoadStatusPropertyDescriptor(final Object object) {
		IStatus loadStatus = ((SdrRoot) object).getLoadStatus();
		if (loadStatus == null) {
			loadStatus = Status.OK_STATUS;
		}
		final ItemPropertyDescriptor propDescriptor = createItemPropertyDescriptor(((ComposeableAdapterFactory) this.adapterFactory).getRootAdapterFactory(),
			getResourceLocator(), getString("_UI_SdrRoot_loadStatus_feature"),
			getString("_UI_PropertyDescriptor_description", "_UI_SdrRoot_loadStatus_feature", "_UI_SdrRoot_type"), SdrPackage.Literals.SDR_ROOT__LOAD_STATUS,
			!loadStatus.isOK(), true, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
		final ItemPropertyDescriptorDecorator decorator = new ItemPropertyDescriptorDecorator(object, propDescriptor) {
			@Override
			public IItemLabelProvider getLabelProvider(final Object thisObject) {
				return new IItemLabelProvider() {

					@Override
					public String getText(final Object object) {
						final IStatus status = (IStatus) object;
						return status.getMessage();
					}

					@Override
					public Object getImage(final Object object) {
						IStatus status = (IStatus) object;
						final ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
						if (status == null) {
							status = Status.OK_STATUS;
						}
						switch (status.getSeverity()) {
						case IStatus.WARNING:
							return sharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_WARN_TSK);
						case IStatus.ERROR:
							return sharedImages.getImageDescriptor(ISharedImages.IMG_OBJS_ERROR_TSK);
						default:
							break;
						}
						return null;
					}
				};
			}

			@Override
			public void setPropertyValue(final Object thisObject, final Object value) {
				// PASS
			}

		};

		this.itemPropertyDescriptors.add(decorator);
	}

	/**
	 * This adds a property descriptor for the State feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addStatePropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
			getString("_UI_SdrRoot_state_feature"), getString("_UI_PropertyDescriptor_description", "_UI_SdrRoot_state_feature", "_UI_SdrRoot_type"),
			SdrPackage.Literals.SDR_ROOT__STATE, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null));
	}

	/**
	 * This adds a property descriptor for the Domain Configuration feature.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected void addDomainConfigurationPropertyDescriptor(Object object) {
		itemPropertyDescriptors.add(createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
			getString("_UI_SdrRoot_domainConfiguration_feature"),
			getString("_UI_PropertyDescriptor_description", "_UI_SdrRoot_domainConfiguration_feature", "_UI_SdrRoot_type"),
			SdrPackage.Literals.SDR_ROOT__DOMAIN_CONFIGURATION, false, false, true, null, null, null));
	}

	/**
	 * This adds a property descriptor for the Dev File System Root feature.
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected void addDevFileSystemRootPropertyDescriptor(Object object) {
		ItemPropertyDescriptor desc = createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
			getString("_UI_SdrRoot_devFileSystemRoot_feature"),
			getString("_UI_PropertyDescriptor_description", "_UI_SdrRoot_devFileSystemRoot_feature", "_UI_SdrRoot_type"),
			SdrPackage.Literals.SDR_ROOT__DEV_FILE_SYSTEM_ROOT, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
		ItemPropertyDescriptorDecorator decorator = new ItemPropertyDescriptorDecorator(object, desc) {
			@Override
			public IItemLabelProvider getLabelProvider(final Object thisObject) {
				return new IItemLabelProvider() {

					@Override
					public String getText(Object object) {
						SdrRoot root = (SdrRoot) thisObject;
						return QueryParser.parseQuery(root.getDevFileSystemRoot().query()).get(ScaFileSystemConstants.QUERY_PARAM_FS);
					}

					@Override
					public Object getImage(Object object) {
						return null;
					}
				};
			}
		};
		itemPropertyDescriptors.add(decorator);
	}

	/**
	 * This adds a property descriptor for the Dom File System Root feature.
	 * <!-- begin-user-doc -->
	 * @since 2.0
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	protected void addDomFileSystemRootPropertyDescriptor(Object object) {
		ItemPropertyDescriptor desc = createItemPropertyDescriptor(((ComposeableAdapterFactory) adapterFactory).getRootAdapterFactory(), getResourceLocator(),
			getString("_UI_SdrRoot_domFileSystemRoot_feature"),
			getString("_UI_PropertyDescriptor_description", "_UI_SdrRoot_domFileSystemRoot_feature", "_UI_SdrRoot_type"),
			SdrPackage.Literals.SDR_ROOT__DOM_FILE_SYSTEM_ROOT, false, false, false, ItemPropertyDescriptor.GENERIC_VALUE_IMAGE, null, null);
		ItemPropertyDescriptorDecorator decorator = new ItemPropertyDescriptorDecorator(object, desc) {
			@Override
			public IItemLabelProvider getLabelProvider(final Object thisObject) {
				return new IItemLabelProvider() {

					@Override
					public String getText(Object object) {
						SdrRoot root = (SdrRoot) thisObject;
						return QueryParser.parseQuery(root.getDomFileSystemRoot().query()).get(ScaFileSystemConstants.QUERY_PARAM_FS);
					}

					@Override
					public Object getImage(Object object) {
						return null;
					}
				};
			}
		};
		itemPropertyDescriptors.add(decorator);
	}

	/**
	 * This specifies how to implement {@link #getChildren} and is used to deduce an appropriate feature for an
	 * {@link org.eclipse.emf.edit.command.AddCommand}, {@link org.eclipse.emf.edit.command.RemoveCommand} or
	 * {@link org.eclipse.emf.edit.command.MoveCommand} in {@link #createCommand}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Collection< ? extends EStructuralFeature> getChildrenFeatures(Object object) {
		if (childrenFeatures == null) {
			super.getChildrenFeatures(object);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__COMPONENTS_CONTAINER);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__WAVEFORMS_CONTAINER);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__DEVICES_CONTAINER);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__SERVICES_CONTAINER);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__NODES_CONTAINER);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__SHARED_LIBRARIES_CONTAINER);
			childrenFeatures.add(SdrPackage.Literals.SDR_ROOT__IDL_LIBRARY);
		}
		return childrenFeatures;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EStructuralFeature getChildFeature(Object object, Object child) {
		// Check the type of the specified child object and return the proper feature to use for
		// adding (see {@link AddCommand}) it as a child.

		return super.getChildFeature(object, child);
	}

	/**
	 * This returns SdrRoot.gif.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object getImage(Object object) {
		return overlayImage(object, getResourceLocator().getImage("full/obj16/SdrRoot"));
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getTextGen(Object object) {
		IStatus labelValue = ((SdrRoot) object).getLoadStatus();
		String label = labelValue == null ? null : labelValue.toString();
		return label == null || label.length() == 0 ? getString("_UI_SdrRoot_type") : getString("_UI_SdrRoot_type") + " " + label;
	}

	/**
	 * This returns the label text for the adapted class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	@Override
	public String getText(final Object object) {
		// END GENERATED CODE
		return "Target SDR";
		// BEGIN GENERATED CODE
	}

	/**
	 * This handles model notifications by calling {@link #updateChildren} to update any cached
	 * children and by creating a viewer notification, which it passes to {@link #fireNotifyChanged}.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void notifyChanged(Notification notification) {
		updateChildren(notification);

		switch (notification.getFeatureID(SdrRoot.class)) {
		case SdrPackage.SDR_ROOT__LOAD_STATUS:
		case SdrPackage.SDR_ROOT__STATE:
		case SdrPackage.SDR_ROOT__DEV_FILE_SYSTEM_ROOT:
		case SdrPackage.SDR_ROOT__DOM_FILE_SYSTEM_ROOT:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), false, true));
			return;
		case SdrPackage.SDR_ROOT__COMPONENTS_CONTAINER:
		case SdrPackage.SDR_ROOT__WAVEFORMS_CONTAINER:
		case SdrPackage.SDR_ROOT__DEVICES_CONTAINER:
		case SdrPackage.SDR_ROOT__SERVICES_CONTAINER:
		case SdrPackage.SDR_ROOT__NODES_CONTAINER:
		case SdrPackage.SDR_ROOT__SHARED_LIBRARIES_CONTAINER:
		case SdrPackage.SDR_ROOT__IDL_LIBRARY:
			fireNotifyChanged(new ViewerNotification(notification, notification.getNotifier(), true, false));
			return;
		}
		super.notifyChanged(notification);
	}

	/**
	 * This adds {@link org.eclipse.emf.edit.command.CommandParameter}s describing the children
	 * that can be created under this object.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected void collectNewChildDescriptors(Collection<Object> newChildDescriptors, Object object) {
		super.collectNewChildDescriptors(newChildDescriptors, object);

		newChildDescriptors.add(createChildParameter(SdrPackage.Literals.SDR_ROOT__SERVICES_CONTAINER, SdrFactory.eINSTANCE.createServicesContainer()));

		newChildDescriptors.add(createChildParameter(SdrPackage.Literals.SDR_ROOT__SHARED_LIBRARIES_CONTAINER,
			SdrFactory.eINSTANCE.createSharedLibrariesContainer()));

		newChildDescriptors.add(createChildParameter(SdrPackage.Literals.SDR_ROOT__IDL_LIBRARY, LibraryFactory.eINSTANCE.createIdlLibrary()));
	}

	/**
	 * Return the resource locator for this item provider's resources.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public ResourceLocator getResourceLocator() {
		return SdrEditPlugin.INSTANCE;
	}

}
