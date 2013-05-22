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
package gov.redhawk.ide.debug.impl;

import gov.redhawk.ide.debug.NotifyingNamingContext;
import gov.redhawk.ide.debug.ScaDebugPackage;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.internal.NamingContextExtPOATie;
import gov.redhawk.model.sca.IDisposable;
import gov.redhawk.model.sca.ScaPackage;
import gov.redhawk.model.sca.commands.ScaModelCommand;
import gov.redhawk.model.sca.commands.ScaModelCommandWithResult;
import gov.redhawk.sca.util.Debug;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EObjectContainmentWithInverseEList;
import org.eclipse.emf.ecore.util.EcoreEMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.InternalEList;
import org.jacorb.naming.BindingIteratorImpl;
import org.jacorb.naming.Name;
import org.omg.CORBA.INTERNAL;
import org.omg.CosNaming.Binding;
import org.omg.CosNaming.BindingIteratorHelper;
import org.omg.CosNaming.BindingIteratorHolder;
import org.omg.CosNaming.BindingListHolder;
import org.omg.CosNaming.BindingType;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContext;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextExtPackage.InvalidAddress;
import org.omg.CosNaming.NamingContextPackage.AlreadyBound;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.CosNaming.NamingContextPackage.NotFoundReason;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Notifying Naming Context</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#isDisposed <em>Disposed</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getObjectMap <em>Object Map</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getContextMap <em>Context Map</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getNamingContext <em>Naming Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getSubContexts <em>Sub Contexts</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getParentContext <em>Parent Context</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getPoa <em>Poa</em>}</li>
 *   <li>{@link gov.redhawk.ide.debug.impl.NotifyingNamingContextImpl#getName <em>Name</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class NotifyingNamingContextImpl extends EObjectImpl implements NotifyingNamingContext {
	/**
	 * The default value of the '{@link #isDisposed() <em>Disposed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDisposed()
	 * @generated
	 * @ordered
	 */
	protected static final boolean DISPOSED_EDEFAULT = false;
	/**
	 * The cached value of the '{@link #isDisposed() <em>Disposed</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isDisposed()
	 * @generated
	 * @ordered
	 */
	protected boolean disposed = DISPOSED_EDEFAULT;
	/**
	 * The cached value of the '{@link #getObjectMap() <em>Object Map</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getObjectMap()
	 * @generated
	 * @ordered
	 */
	protected EMap<Name, org.omg.CORBA.Object> objectMap;
	/**
	 * The cached value of the '{@link #getContextMap() <em>Context Map</em>}' map.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getContextMap()
	 * @generated
	 * @ordered
	 */
	protected EMap<Name, NamingContext> contextMap;
	/**
	 * The default value of the '{@link #getNamingContext() <em>Naming Context</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamingContext()
	 * @generated
	 * @ordered
	 */
	protected static final NamingContextExt NAMING_CONTEXT_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getNamingContext() <em>Naming Context</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getNamingContext()
	 * @generated
	 * @ordered
	 */
	protected NamingContextExt namingContext = NAMING_CONTEXT_EDEFAULT;
	/**
	 * The cached value of the '{@link #getSubContexts() <em>Sub Contexts</em>}' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getSubContexts()
	 * @generated
	 * @ordered
	 */
	protected EList<NotifyingNamingContext> subContexts;
	/**
	 * The default value of the '{@link #getPoa() <em>Poa</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPoa()
	 * @generated
	 * @ordered
	 */
	protected static final POA POA_EDEFAULT = null;
	/**
	 * The cached value of the '{@link #getPoa() <em>Poa</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPoa()
	 * @generated
	 * @ordered
	 */
	protected POA poa = POA_EDEFAULT;
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected NotifyingNamingContextImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ScaDebugPackage.Literals.NOTIFYING_NAMING_CONTEXT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isDisposed() {
		return disposed;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<Name, org.omg.CORBA.Object> getObjectMap() {
		if (objectMap == null) {
			objectMap = new EcoreEMap<Name,org.omg.CORBA.Object>(ScaDebugPackage.Literals.NAME_TO_OBJECT_ENTRY, NameToObjectEntryImpl.class, this, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP);
		}
		return objectMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EMap<Name, NamingContext> getContextMap() {
		if (contextMap == null) {
			contextMap = new EcoreEMap<Name,NamingContext>(ScaDebugPackage.Literals.NAME_TO_NAMING_CONTEXT_ENTRY, NameToNamingContextEntryImpl.class, this, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP);
		}
		return contextMap;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NamingContextExt getNamingContext() {
		return namingContext;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setNamingContext(NamingContextExt newNamingContext) {
		NamingContextExt oldNamingContext = namingContext;
		namingContext = newNamingContext;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT, oldNamingContext, namingContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<NotifyingNamingContext> getSubContexts() {
		if (subContexts == null) {
			subContexts = new EObjectContainmentWithInverseEList<NotifyingNamingContext>(NotifyingNamingContext.class, this, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT);
		}
		return subContexts;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotifyingNamingContext getParentContext() {
		if (eContainerFeatureID() != ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT) return null;
		return (NotifyingNamingContext)eContainer();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public NotificationChain basicSetParentContext(NotifyingNamingContext newParentContext, NotificationChain msgs) {
		msgs = eBasicSetContainer((InternalEObject)newParentContext, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT, msgs);
		return msgs;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setParentContext(NotifyingNamingContext newParentContext) {
		if (newParentContext != eInternalContainer() || (eContainerFeatureID() != ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT && newParentContext != null)) {
			if (EcoreUtil.isAncestor(this, newParentContext))
				throw new IllegalArgumentException("Recursive containment not allowed for " + toString());
			NotificationChain msgs = null;
			if (eInternalContainer() != null)
				msgs = eBasicRemoveFromContainer(msgs);
			if (newParentContext != null)
				msgs = ((InternalEObject)newParentContext).eInverseAdd(this, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS, NotifyingNamingContext.class, msgs);
			msgs = basicSetParentContext(newParentContext, msgs);
			if (msgs != null) msgs.dispatch();
		}
		else if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT, newParentContext, newParentContext));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public POA getPoa() {
		return poa;
	}

	private static final Debug DEBUG = new Debug(ScaDebugPlugin.ID, "context");
	private boolean destroyed = false;
	private final Adapter adapter = new AdapterImpl() {
		{
			eAdapters().add(this);
		}
		@Override
        public void notifyChanged(final Notification msg) {
			switch(msg.getFeatureID(NotifyingNamingContext.class)) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				switch(msg.getEventType()) {
				case Notification.REMOVE:
					if (msg.getOldValue() instanceof NotifyingNamingContext) {
						final NotifyingNamingContext context = (NotifyingNamingContext) msg.getOldValue();
						removeReferences((NotifyingNamingContextImpl) context);
					}
					break;
				case Notification.REMOVE_MANY:
					for (final Object obj : (Collection<?>)msg.getOldValue()) {
						if (obj instanceof NotifyingNamingContext) {
							final NotifyingNamingContext context = (NotifyingNamingContext) obj;
							removeReferences((NotifyingNamingContextImpl) context);
						}
					}
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @since 3.0
	 */
	protected void removeReferences(final NotifyingNamingContextImpl context) {
		getContextMap().remove(context.name);
    }

	private Name name;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void setPoa(final POA newPoa) {
		setPoaGen(newPoa);
		if (newPoa == null) {
			setNamingContext(null);
			return;
		}
		try {
	        setNamingContext(NamingContextExtHelper.narrow(this.poa.servant_to_reference(new NamingContextExtPOATie(this))));
        } catch (final ServantNotActive e) {
	        setNamingContext(null);
        } catch (final WrongPolicy e) {
        	setNamingContext(null);
        }
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPoaGen(POA newPoa) {
		POA oldPoa = poa;
		poa = newPoa;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__POA, oldPoa, poa));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getName() {
		// END GENERATED CODE
		if (this.name != null) {
			return name.toString();
		} 
		return "";
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public NameComponent[] getName(final URI uri) {
		// END GENERATED CODE
		if (uri == null) {
			return null;
		}
		try {
			final String encodedURI = URLEncoder.encode(uri.toString(), "UTF-8");
			final String escapedURI = encodedURI.replaceAll("\\.", "%2E");
			return to_name(escapedURI);
		} catch (final InvalidName e) {
			throw new IllegalStateException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public URI getURI(final NameComponent[] name) {
		// END GENERATED CODE
		for (int i = name.length - 1; i >= 0; i--) {
			final NameComponent c = name[i];
			try {
				final String uriStr = URLDecoder.decode(c.id, "UTF-8");
				final URI uri = URI.createURI(uriStr);
				if (uri.scheme() != null) {
					return uri;
				}
			} catch (final Exception e) {
				// PASS
			}
		}
		return null;
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public String getFullName() {
		// END GENERATED CODE
		try {
			if (name != null) {
				return name.fullName().toString();
			}
		} catch (InvalidName e) {
			//PASS			
		}
		return "";
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public NotifyingNamingContext getResourceContext(final URI uri) {
		// END GENERATED CODE
		if (uri == null) {
			return null;
		}
		Name newName;
		try {
			newName = new Name(getName(uri));
		} catch (final org.omg.CosNaming.NamingContextPackage.InvalidName e1) {
			throw new IllegalStateException(e1);
		}
		NamingContext context = getContextMap().get(newName);
		if (context == null) {
			try {
				context = bind_new_context(newName.components());
			} catch (final NotFound e) {
				// PASS
			} catch (final AlreadyBound e) {
				// PASS
			} catch (final CannotProceed e) {
				throw new IllegalStateException(e);
			} catch (final org.omg.CosNaming.NamingContextPackage.InvalidName e) {
				throw new IllegalStateException(e);
			}
		}
		return findContext(context);
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public NotifyingNamingContext findContext(final NamingContext context) {
		// END GENERATED CODE
		if (context == null) {
			return null;
		}
		for (final TreeIterator<EObject> iterator = eAllContents(); iterator.hasNext();) {
			final EObject obj = iterator.next();
			if (obj instanceof NotifyingNamingContext) {
				final NotifyingNamingContext nc = (NotifyingNamingContext) obj;
				if (context._is_equivalent(nc.getNamingContext())) {
					return nc;
				}
			}
		}
		return null;
		// BEGIN GENERATED CODE
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public void dispose() {
		getObjectMap().clear();
		getContextMap().clear();
		getSubContexts().clear();
		EcoreUtil.delete(this);
		this.destroyed = true;
		this.namingContext = null;
		this.poa = null;
		this.disposed = true;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public NotificationChain eInverseAdd(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				return ((InternalEList<InternalEObject>)(InternalEList<?>)getSubContexts()).basicAdd(otherEnd, msgs);
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				if (eInternalContainer() != null)
					msgs = eBasicRemoveFromContainer(msgs);
				return basicSetParentContext((NotifyingNamingContext)otherEnd, msgs);
		}
		return super.eInverseAdd(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs) {
		switch (featureID) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP:
				return ((InternalEList<?>)getObjectMap()).basicRemove(otherEnd, msgs);
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP:
				return ((InternalEList<?>)getContextMap()).basicRemove(otherEnd, msgs);
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				return ((InternalEList<?>)getSubContexts()).basicRemove(otherEnd, msgs);
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				return basicSetParentContext(null, msgs);
		}
		return super.eInverseRemove(otherEnd, featureID, msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public NotificationChain eBasicRemoveFromContainerFeature(NotificationChain msgs) {
		switch (eContainerFeatureID()) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				return eInternalContainer().eInverseRemove(this, ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS, NotifyingNamingContext.class, msgs);
		}
		return super.eBasicRemoveFromContainerFeature(msgs);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__DISPOSED:
				return isDisposed();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP:
				if (coreType) return getObjectMap();
				else return getObjectMap().map();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP:
				if (coreType) return getContextMap();
				else return getContextMap().map();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT:
				return getNamingContext();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				return getSubContexts();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				return getParentContext();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__POA:
				return getPoa();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAME:
				return getName();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP:
				((EStructuralFeature.Setting)getObjectMap()).set(newValue);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP:
				((EStructuralFeature.Setting)getContextMap()).set(newValue);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT:
				setNamingContext((NamingContextExt)newValue);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				getSubContexts().clear();
				getSubContexts().addAll((Collection<? extends NotifyingNamingContext>)newValue);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				setParentContext((NotifyingNamingContext)newValue);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__POA:
				setPoa((POA)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP:
				getObjectMap().clear();
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP:
				getContextMap().clear();
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT:
				setNamingContext(NAMING_CONTEXT_EDEFAULT);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				getSubContexts().clear();
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				setParentContext((NotifyingNamingContext)null);
				return;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__POA:
				setPoa(POA_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__DISPOSED:
				return disposed != DISPOSED_EDEFAULT;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__OBJECT_MAP:
				return objectMap != null && !objectMap.isEmpty();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__CONTEXT_MAP:
				return contextMap != null && !contextMap.isEmpty();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAMING_CONTEXT:
				return NAMING_CONTEXT_EDEFAULT == null ? namingContext != null : !NAMING_CONTEXT_EDEFAULT.equals(namingContext);
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__SUB_CONTEXTS:
				return subContexts != null && !subContexts.isEmpty();
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__PARENT_CONTEXT:
				return getParentContext() != null;
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__POA:
				return POA_EDEFAULT == null ? poa != null : !POA_EDEFAULT.equals(poa);
			case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__NAME:
				return NAME_EDEFAULT == null ? getName() != null : !NAME_EDEFAULT.equals(getName());
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eBaseStructuralFeatureID(int derivedFeatureID, Class<?> baseClass) {
		if (baseClass == IDisposable.class) {
			switch (derivedFeatureID) {
				case ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__DISPOSED: return ScaPackage.IDISPOSABLE__DISPOSED;
				default: return -1;
			}
		}
		return super.eBaseStructuralFeatureID(derivedFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public int eDerivedStructuralFeatureID(int baseFeatureID, Class<?> baseClass) {
		if (baseClass == IDisposable.class) {
			switch (baseFeatureID) {
				case ScaPackage.IDISPOSABLE__DISPOSED: return ScaDebugPackage.NOTIFYING_NAMING_CONTEXT__DISPOSED;
				default: return -1;
			}
		}
		return super.eDerivedStructuralFeatureID(baseFeatureID, baseClass);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (disposed: ");
		result.append(disposed);
		result.append(", namingContext: ");
		result.append(namingContext);
		result.append(", poa: ");
		result.append(poa);
		result.append(')');
		return result.toString();
	}

	/**
	 *  bind a name (an array of name components) to an object
	 */

	public void bind(final NameComponent[] nc, final org.omg.CORBA.Object obj) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		if (nc == null || nc.length == 0) {
			throw new InvalidName();
		}

		if (obj == null) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		final Name n = new Name(nc);
		final Name ctx = n.ctxName();
		final NameComponent nb = n.baseNameComponent();
		if (ctx == null) {
			if (getObjectMap().containsKey(n)) {
				// if the name is still in use, try to ping the object
				final org.omg.CORBA.Object s = getObjectMap().get(n);
				if (isDead(s)) {
					rebind(n.components(), obj);
					return;
				}
				throw new AlreadyBound();
			} else if (getContextMap().containsKey(n)) {
				// if the name is still in use, try to ping the object
				final org.omg.CORBA.Object s = getContextMap().get(n);
				if (isDead(s)) {
					unbind(n.components());
				}
				throw new AlreadyBound();
			}

			final Object putResult = ScaModelCommandWithResult.execute(this, new ScaModelCommandWithResult<Object>() {

				public void execute() {
					setResult(getObjectMap().put(n, obj));
				}
			});

			if (putResult != null) {
				throw new CannotProceed(getNamingContext(), n.components());
			}

			if (NotifyingNamingContextImpl.DEBUG.enabled) {
				NotifyingNamingContextImpl.DEBUG.message("Bound name: " + n.toString());
			}
		} else {
			final NameComponent[] ncx = new NameComponent[1];
			ncx[0] = nb;
			NamingContextExtHelper.narrow(resolve(ctx.components())).bind(ncx, obj);
		}
	}

	/**
	 * Bind an object to a name that's already in use, i.e. rebind the name
	 */

	public void rebind(final NameComponent[] nc, final org.omg.CORBA.Object obj) throws NotFound, CannotProceed, InvalidName {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		if (nc == null || nc.length == 0) {
			throw new InvalidName();
		}

		if (obj == null) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		final Name n = new Name(nc);
		final Name ctx = n.ctxName();
		final NameComponent nb = n.baseNameComponent();

		// the name is bound, but it is bound to a context,
		// the client should have been using rebind_context!

		if (getContextMap().containsKey(n)) {
			throw new NotFound(NotFoundReason.not_object, new NameComponent[] {
				nb
			});
		}

		// try remove an existing binding

		final org.omg.CORBA.Object _o = ScaModelCommandWithResult.execute(this, new ScaModelCommandWithResult<org.omg.CORBA.Object>() {

			public void execute() {
				setResult(getObjectMap().removeKey(n));
			}
		});

		if (ctx == null) {
			// do the rebinding in this context

			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					getObjectMap().put(n, obj);
				}
			});
			if (NotifyingNamingContextImpl.DEBUG.enabled) {
				NotifyingNamingContextImpl.DEBUG.message("re-Bound name: " + n.toString());
			}
		} else {
			// rebind in the correct context

			final NameComponent[] ncx = new NameComponent[1];
			ncx[0] = nb;
			final NamingContextExt nce = NamingContextExtHelper.narrow(resolve(ctx.components()));
			if (nce == null) {
				throw new CannotProceed();
			}
			nce.rebind(ncx, obj);
		}
	}

	/**
	 * Bind an context to a name that's already in use, i.e. rebind the name
	 */

	public void rebind_context(final NameComponent[] nc, final NamingContext obj) throws NotFound, CannotProceed, InvalidName {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		if (nc == null || nc.length == 0) {
			throw new InvalidName();
		}

		if (obj == null) {
			throw new org.omg.CORBA.BAD_PARAM();
		}

		final Name n = new Name(nc);
		final Name ctx = n.ctxName();
		final NameComponent nb = n.baseNameComponent();

		// the name is bound, but it is bound to an object,
		// the client should have been using rebind() !

		if (getObjectMap().containsKey(n)) {
			throw new NotFound(NotFoundReason.not_context, new NameComponent[] {
				nb
			});
		}

		// try to remove an existing context binding

		final org.omg.CORBA.Object _o = ScaModelCommandWithResult.execute(this, new ScaModelCommandWithResult<org.omg.CORBA.Object>() {

			public void execute() {
				setResult(getContextMap().removeKey(n));
			}
		});

		if (ctx == null) {
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					getContextMap().put(n, obj);
				}
			});
			if (NotifyingNamingContextImpl.DEBUG.enabled) {
				NotifyingNamingContextImpl.DEBUG.message("Re-Bound context: " + n.baseNameComponent().id);
			}
		}
	}
	/**
	 * Bind a context to a name
	 */	
	public void bind_context(final NameComponent[] nc, final NamingContext obj) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		bind_context(new Name(nc), obj);
	}

	private void bind_context(final Name n, final NamingContext obj) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		final Name ctx = n.ctxName();
		final NameComponent nb = n.baseNameComponent();

		if (ctx == null) {
			if (getObjectMap().containsKey(n)) {
				// if the name is still in use, try to ping the object
				final org.omg.CORBA.Object s = getObjectMap().get(n);
				if (isDead(s)) {
					unbind(n.components());
				} else {
					throw new AlreadyBound();
				}
			} else if (getContextMap().containsKey(n)) {
				// if the name is still in use, try to ping the object
				final org.omg.CORBA.Object s = getContextMap().get(n);
				if (isDead(s)) {
					rebind_context(n.components(), obj);
					return;
				}
				throw new AlreadyBound();
			}
			final Object putResult = ScaModelCommandWithResult.execute(this, new ScaModelCommandWithResult<Object>() {

				public void execute() {
					setResult(getContextMap().put(n, obj));
				}
			});
			if (putResult != null) {
				throw new CannotProceed(getNamingContext(), n.components());
			}
			ScaModelCommand.execute(this, new ScaModelCommand() {

				public void execute() {
					getContextMap().put(n, obj);
				}
			});

			if (NotifyingNamingContextImpl.DEBUG.enabled) {
				NotifyingNamingContextImpl.DEBUG.message("Bound context: " + n.toString());
			}
		} else {
			final NameComponent[] ncx = new NameComponent[1];
			ncx[0] = nb;
			NamingContextExtHelper.narrow(resolve(ctx.components())).bind_context(ncx, obj);
		}
	}

	public NamingContext bind_new_context(final NameComponent[] nc) throws NotFound, CannotProceed, InvalidName, AlreadyBound {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		if (nc == null || nc.length == 0) {
			throw new InvalidName();
		}

		final NotifyingNamingContextImpl impl = new NotifyingNamingContextImpl();
		impl.setPoa(getPoa());
		impl.name = new Name(nc);
		final NamingContextExt ns = NamingContextExtHelper.narrow(impl.getNamingContext());
		bind_context(impl.name, ns);
		ScaModelCommand.execute(this, new ScaModelCommand() {
			
			public void execute() {
				getSubContexts().add(impl);
			}
		});

		if (ns == null) {
			throw new CannotProceed();
		}
		return ns;
	}

	/**
	 *  cleanup bindings, i.e. ping every object and remove bindings to
	 *  non-existent objects
	 */

	private void cleanup() {
		final List<Name> itemsToRemoveNames = new ArrayList<Name>();
		for (final Iterator<Entry<Name, org.omg.CORBA.Object>> iterator = getObjectMap().entrySet().iterator(); iterator.hasNext();) {
			final Entry<Name, org.omg.CORBA.Object> entry = iterator.next();
			if (isDead(entry.getValue())) {
				if (NotifyingNamingContextImpl.DEBUG.enabled) {
					NotifyingNamingContextImpl.DEBUG.message("Removing name " + entry.getKey().baseNameComponent().id);
				}
				itemsToRemoveNames.add(entry.getKey());
			}
		}

		final List<Name> itemsToRemoveContexts = new ArrayList<Name>();
		/* ping contexts */
		for (final Iterator<Entry<Name, NamingContext>> iterator = getContextMap().entrySet().iterator(); iterator.hasNext();) {
			final Entry<Name, NamingContext> entry = iterator.next();
			if (isDead(entry.getValue())) {
				if (NotifyingNamingContextImpl.DEBUG.enabled) {
					NotifyingNamingContextImpl.DEBUG.message("Removing context " + entry.getKey().baseNameComponent().id);
				}
				itemsToRemoveContexts.add(entry.getKey());
			}
		}

		ScaModelCommand.execute(this, new ScaModelCommand() {

			public void execute() {
				for (final Name name : itemsToRemoveNames) {
					getObjectMap().removeKey(name);
				}
				for (final Name name : itemsToRemoveContexts) {
					getContextMap().removeKey(name);
				}
			}

		});

	}

	public void destroy() throws NotEmpty {
		ScaModelCommand.execute(this, new ScaModelCommand() {

			public void execute() {
				NotifyingNamingContextImpl.this.dispose();
			}
		});
	}

	/**
	 *  @return numer of bindings in this context
	 */

	public int how_many() {
		if (this.destroyed) {
			return 0;
		}
		return getObjectMap().size() + getContextMap().size();
	}

	/**
	 *  list all bindings
	 */

	public void list(final int how_many, final BindingListHolder bl, final BindingIteratorHolder bi) {
		if (this.destroyed) {
			return;
		}

		Binding[] result;

		cleanup();

		int size = how_many();

		final Iterator<Entry<Name, org.omg.CORBA.Object>> n = getObjectMap().entrySet().iterator();
		final Iterator<Entry<Name, NamingContext>> c = getContextMap().entrySet().iterator();

		if (how_many < size) {
			// counter for copies
			int how_many_ctr = how_many;

			// set up an array with "how_many" bindings

			result = new Binding[how_many];
			for (; n.hasNext() && how_many_ctr > 0; how_many_ctr--) {
				result[how_many_ctr - 1] = new Binding((n.next().getKey()).components(), BindingType.nobject);
			}

			for (; c.hasNext() && how_many_ctr > 0; how_many_ctr--) {
				result[how_many_ctr - 1] = new Binding((c.next().getKey()).components(), BindingType.ncontext);
			}

			// create a new BindingIterator for the remaining arrays

			size -= how_many;
			final Binding[] rest = new Binding[size];
			for (; n.hasNext() && size > 0; size--) {
				rest[size - 1] = new Binding((n.next().getKey()).components(), BindingType.nobject);
			}

			for (; c.hasNext() && size > 0; size--) {
				rest[size - 1] = new Binding((c.next().getKey()).components(), BindingType.ncontext);
			}

			org.omg.CORBA.Object o = null;
			try {
				// Iterators are activated with the RootPOA (transient)
				// TODO Should be in root poa?
				final byte[] oid = getPoa().activate_object(new BindingIteratorImpl(rest));
				o = getPoa().id_to_reference(oid);
			} catch (final Exception e) {
				NotifyingNamingContextImpl.DEBUG.catching("unexpected exception", e);
				throw new INTERNAL(e.toString());
			}

			bi.value = BindingIteratorHelper.narrow(o);
		} else {
			result = new Binding[size];
			for (; n.hasNext() && size > 0; size--) {
				result[size - 1] = new Binding((n.next().getKey()).components(), BindingType.nobject);
			}

			for (; c.hasNext() && size > 0; size--) {
				result[size - 1] = new Binding((c.next().getKey()).components(), BindingType.ncontext);
			}
		}

		bl.value = result;
	}

	public NamingContext new_context() {
		final NotifyingNamingContextImpl impl = new NotifyingNamingContextImpl();
		impl.setPoa(getPoa());
		return impl.getNamingContext();
	}

	/**
	 * resolve a name
	 */

	public org.omg.CORBA.Object resolve(final NameComponent[] nc) throws NotFound, CannotProceed, InvalidName {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		if (nc == null || nc.length == 0) {
			throw new InvalidName();
		}

		final Name n = new Name(nc[0]);
		if (nc.length > 1) {
			final NamingContextExt next_context = NamingContextExtHelper.narrow(getContextMap().get(n));

			if ((next_context == null) || (isDead(next_context))) {
				throw new NotFound(NotFoundReason.missing_node, nc);
			}

			final NameComponent[] nc_prime = new NameComponent[nc.length - 1];

			for (int i = 1; i < nc.length; i++) {
				nc_prime[i - 1] = nc[i];
			}

			return next_context.resolve(nc_prime);
		} else {
			org.omg.CORBA.Object result = null;

			result = getContextMap().get(n);

			if (result == null) {
				result = getObjectMap().get(n);
			}

			if (result == null) {
				throw NotifyingNamingContextImpl.DEBUG.throwing(new NotFound(NotFoundReason.missing_node, n.components()));
			}

			if (isDead(result)) {
				throw NotifyingNamingContextImpl.DEBUG.throwing(new NotFound(NotFoundReason.missing_node, n.components()));
			}

			return result;
		}
	}

	/**
	 * unbind a name
	 */

	public void unbind(final NameComponent[] nc) throws NotFound, CannotProceed, InvalidName {
		if (this.destroyed) {
			throw new CannotProceed();
		}

		if (nc == null || nc.length == 0) {
			throw new InvalidName();
		}

		final Name n = new Name(nc);
		final Name ctx = n.ctxName();
		final NameComponent nb = n.baseNameComponent();

		if (ctx == null) {
			if (getObjectMap().containsKey(n)) {
				final org.omg.CORBA.Object o = ScaModelCommandWithResult.execute(this, new ScaModelCommandWithResult<org.omg.CORBA.Object>() {

					public void execute() {
						setResult(getObjectMap().removeKey(n));
					}
				});
				if (NotifyingNamingContextImpl.DEBUG.enabled) {
					NotifyingNamingContextImpl.DEBUG.message("Unbound: " + n.toString());
				}
			} else if (getContextMap().containsKey(n)) {
				final org.omg.CORBA.Object o = ScaModelCommandWithResult.execute(this, new ScaModelCommandWithResult<org.omg.CORBA.Object>() {

					public void execute() {
						setResult(getContextMap().removeKey(n));
					}
				});

				if (NotifyingNamingContextImpl.DEBUG.enabled) {
					NotifyingNamingContextImpl.DEBUG.message("Unbound: " + n.toString());
				}
			} else {
				if (NotifyingNamingContextImpl.DEBUG.enabled) {
					NotifyingNamingContextImpl.DEBUG.message("Unbind failed for " + n.toString());
				}
				throw NotifyingNamingContextImpl.DEBUG.throwing(new NotFound(NotFoundReason.not_context, n.components()));
			}
		} else {
			final NameComponent[] ncx = new NameComponent[1];
			ncx[0] = nb;
			NamingContextExtHelper.narrow(resolve(ctx.components())).unbind(ncx);
		}
	}

	/* NamingContextExt */

	/**
	 * convert a name into its string representation
	 */

	public String to_string(final NameComponent[] n) throws InvalidName {
		return Name.toString(n);
	}

	/**
	 * convert a string into name
	 * @throws InvalidName
	 */

	public NameComponent[] to_name(final String sn) throws InvalidName {
		return Name.toName(sn);
	}

	/**
	 *
	 */

	public String to_url(final String addr, final String sn) throws InvalidAddress, InvalidName {
		throw new UnsupportedOperationException();
	}

	/**
	 *
	 */

	public org.omg.CORBA.Object resolve_str(final String n) throws NotFound, CannotProceed, InvalidName {
		return resolve(to_name(n));
	}

	/**
	 * determine if non_existent
	 */

	private boolean isDead(final org.omg.CORBA.Object o) {
		boolean non_exist = true;
		try {
			non_exist = o._non_existent();
		} catch (final org.omg.CORBA.SystemException e) {
			non_exist = true;
		}
		return non_exist;
	}

} //NotifyingNamingContextImpl
