/*******************************************************************************
 * Copyright (c) 2011, 2012 Red Hat, Inc.
 *  All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 *
 * @author Ivar Meikas
 ******************************************************************************/
package gov.redhawk.ide.sad.graphiti.ui.diagram.features.connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.bpmn2.AdHocSubProcess;
import org.eclipse.bpmn2.BaseElement;
import org.eclipse.bpmn2.Bpmn2Package;
import org.eclipse.bpmn2.Choreography;
import org.eclipse.bpmn2.Collaboration;
import org.eclipse.bpmn2.Definitions;
import org.eclipse.bpmn2.DocumentRoot;
import org.eclipse.bpmn2.Event;
import org.eclipse.bpmn2.EventDefinition;
import org.eclipse.bpmn2.ExtensionAttributeValue;
import org.eclipse.bpmn2.FormalExpression;
import org.eclipse.bpmn2.Participant;
import org.eclipse.bpmn2.Process;
import org.eclipse.bpmn2.RootElement;
import org.eclipse.bpmn2.SubChoreography;
import org.eclipse.bpmn2.SubProcess;
import org.eclipse.bpmn2.Transaction;
import org.eclipse.bpmn2.di.BPMNDiagram;
import org.eclipse.bpmn2.di.BPMNPlane;
import org.eclipse.bpmn2.di.BpmnDiPackage;
import org.eclipse.bpmn2.modeler.core.Activator;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterRegistry;
import org.eclipse.bpmn2.modeler.core.adapters.AdapterUtil;
import org.eclipse.bpmn2.modeler.core.adapters.ExtendedPropertiesAdapter;
import org.eclipse.bpmn2.modeler.core.adapters.INamespaceMap;
import org.eclipse.bpmn2.modeler.core.adapters.InsertionAdapter;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerFactory;
import org.eclipse.bpmn2.modeler.core.model.Bpmn2ModelerResourceSetImpl;
import org.eclipse.bpmn2.util.Bpmn2Resource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dd.dc.DcPackage;
import org.eclipse.dd.di.DiPackage;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.impl.DynamicEObjectImpl;
import org.eclipse.emf.ecore.impl.EAttributeImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.BasicFeatureMap;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.ecore.util.FeatureMap;
import org.eclipse.emf.ecore.util.FeatureMap.Entry;
import org.eclipse.emf.ecore.util.FeatureMapUtil;
import org.eclipse.emf.ecore.xml.type.XMLTypePackage;
import org.eclipse.emf.transaction.RecordingCommand;
import org.eclipse.emf.transaction.TransactionalEditingDomain;
import org.eclipse.emf.transaction.util.TransactionUtil;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.platform.IDiagramContainer;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.ui.editor.DiagramEditor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.xsd.XSDAttributeDeclaration;
import org.eclipse.xsd.XSDElementDeclaration;

public class ModelUtil {

	// TODO: need to determine whether IDs need to be unique within a Resource or ResourceSet - see getKey()
	
	// Map of EMF resource sets to ID mapping tables. The ID mapping tables map a BPMN2 element ID string to the EObject.
	// The EObject is not used anywhere (yet!) just a placeholder to allow use of a HashMap for fast lookups of the ID string.
	// The ID strings are composed from the BPMN2 element description name and a sequence number (starting at 1).
	// When a new ID is requested, generateID() simply increments the sequence number until an ID is found that isn't
	// already in the table.
	public static HashMap<Object, Hashtable<String, EObject>> ids = new  HashMap<Object, Hashtable<String, EObject>>();
	// Map of ID strings and sequential counters for each BPMN2 element description.
	public static HashMap<String, Integer> defaultIds = new HashMap<String, Integer>();

	/**
	 * Clear the IDs hashmap for the given EMF Resource. This should be called
	 * when the editor is disposed to avoid unnecessary growth of the IDs table.
	 * 
	 * @param res - the EMF Resource that was used to generate the ID strings.
	 */
	public static void clearIDs(Resource res, boolean all) {
		ids.remove( getKey(res) );
		if (all) {
			defaultIds.clear();
		}
	}

	/**
	 * Construct the first part of the ID string using the BPMN2 element description name.
	 * If the object is a DI element, concatenate the BPMN2 element description name.
	 * 
	 * @param obj - the BPMN2 object
	 * @return name string
	 */
	private static String getObjectName(EObject obj) {
		String name;
		EStructuralFeature feature = ((EObject)obj).eClass().getEStructuralFeature("bpmnElement"); //$NON-NLS-1$
		if (feature!=null && obj.eGet(feature)!=null) {
			EObject bpmnElement = (EObject) obj.eGet(feature);
			name = obj.eClass().getName() + "_" + bpmnElement.eClass().getName(); //$NON-NLS-1$
		}
		else {
			name = obj.eClass().getName();
		}
		return name;
	}
	
	private static Object getKey(EObject obj) {
		Resource resource = getResource(obj);
		if (resource==null) {
//			System.out.println("The object type "+obj.getClass().getName()+" is not contained in a Resource");
			return null;
		}
		Assert.isTrue(obj!=null);
		return getKey(resource);
	}
	
	private static Object getKey(Resource res) {
		Assert.isTrue(res!=null);
		return res.getResourceSet();
	}
	
	/**
	 * If an EObject has not yet been added to a Resource (e.g. during construction)
	 * generate an ID string using a different strategy (basically same ID prefixed with an underscore).
	 * The "defaultIds" table is used to track the next sequential ID value for a given element description.
	 * 
	 * @param obj - the BPMN2 object
	 * @return the ID string
	 */
	private static String generateDefaultID(EObject obj, String name) {
		if (name==null)
			name = getObjectName(obj);
		Integer value = defaultIds.get(name);
		if (value==null)
			value = Integer.valueOf(1);
		value = Integer.valueOf( value.intValue() + 1 );
		defaultIds.put(name, Integer.valueOf(value));
		
		return "_" + name + "_" + value; //$NON-NLS-1$ //$NON-NLS-2$
	}

	/**
	 * Generate an ID string for a given BPMN2 object that will (eventually!) be added to the given Resource.
	 * 
	 * CAUTION: IDs for objects that have already been deleted WILL be reused.
	 * 
	 * @param obj - the BPMN2 object
	 * @param res - the Resource to which the object will be added
	 * @return the ID string
	 */
	private static String generateID(EObject obj, Resource res) {
		return generateID(obj, res, null);
	}

	public static String generateID(EObject obj, Resource res, String name) {
		Object key = (res==null ? getKey(obj) : getKey(res));
		if (key!=null) {
			Hashtable<String, EObject> tab = ids.get(key);
			if (tab==null) {
				tab = new Hashtable<String, EObject>();
				ids.put(key, tab);
			}
			
			String id = name;
			if (name==null) {
				name = getObjectName(obj);
				id = name + "_" + 1; //$NON-NLS-1$
			}
			
			for (int i=1;; ++i) {
				if (tab.get(id)==null) {
					tab.put(id, obj);
					return id;
				}
				id = name + "_" + i; //$NON-NLS-1$
			}
		}
		return generateDefaultID(obj, name);
	}
	
	public static void unsetID(EObject obj, Resource resource) {
		EStructuralFeature feature = ((EObject)obj).eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature!=null) {
			Object value = obj.eGet(feature);
			if (value instanceof String) {
				String id = (String)value;
				Object key = getKey(resource);
				if (key!=null) {
					Hashtable<String, EObject> tab = ids.get(key);
					if (tab!=null) {
						tab.remove(id);
					}
				}
			}
		}
	}
	
	/**
	 * Add an ID string to the ID mapping table(s). This must be used during model import
	 * to add existing BPMN2 element IDs to the table so we don't generate duplicates.
	 * 
	 * @param obj - the BPMN2 object
	 */
	public static void addID(EObject obj) {
		EStructuralFeature feature = ((EObject)obj).eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature!=null) {
			Object value = obj.eGet(feature);
			if (value!=null) {
				addID(obj,(String)value);
			}
			else {
				// TODO: what to do here if the BPMN2 element has an "id" attribute which is not set?
				// should we generate one and set it?
				// yup
				setID(obj);
			}
		}
		
	}
	
	/**
	 * Add an ID string to the ID mapping table(s). This must be used during model import
	 * to add existing BPMN2 element IDs to the table so we don't generate duplicates.
	 * 
	 * @param obj - the BPMN2 object
	 * @param id - the object's ID string
	 */
	public static void addID(EObject obj, String id) {
		Object key = getKey(obj);
		String name = getObjectName(obj);
		if (key==null || id.startsWith("_" + name + "_")) { //$NON-NLS-1$ //$NON-NLS-2$
			int newValue = 0;
			try {
				int i = id.lastIndexOf('_') + 1;
				if (i<id.length())
					newValue = Integer.parseInt(id.substring(i));
			} catch (Exception e) {
			}
			Integer oldValue = defaultIds.get(name);
			if (oldValue==null || newValue > oldValue.intValue())
				defaultIds.put(name, Integer.valueOf(newValue));
		}
		else {	
			Hashtable<String, EObject> tab = ids.get(key);
			if (tab==null) {
				tab = new Hashtable<String, EObject>();
				ids.put(key, tab);
			}
			tab.put(id, obj);
		}
	}

	/**
	 * Generate a unique ID for the given BPMN2 element and set it.
	 * This should only be used during object construction AFTER an object has
	 * already been added to a Resource.
	 * 
	 * @param obj - the BPMN2 object
	 */
	public static String setID(EObject obj) {
		return setID(obj,getResource(obj));
	}

	/**
	 * Generate a unique ID for the given BPMN2 element and set it.
	 * This should be used during object construction if the object has NOT YET
	 * been added to a Resource.
	 * 
	 * @param obj - the BPMN2 object
	 * @param res - the Resource to which the object will be added
	 */
	public static String setID(EObject obj, Resource res) {
		String id = null;
		EStructuralFeature feature = ((EObject)obj).eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature!=null) {
			if (obj.eGet(feature)==null) {
				id = generateID(obj,res);
				obj.eSet(feature, id);
			}
		}
		return id;
	}
	
	public static String getID(EObject obj) {
		EStructuralFeature feature = ((EObject)obj).eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature!=null) {
			return (String)obj.eGet(feature);
		}
		return null;
	}
	
	public static String generateUndefinedID(String base) {
		String name = "undefined"; //$NON-NLS-1$
		if (base.contains("_")) { //$NON-NLS-1$
			return "<" + name + "_" + base.replaceFirst(".*_", "") + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}
		
		Integer value = defaultIds.get(name);
		if (value==null)
			value = Integer.valueOf(1);
		value = Integer.valueOf( value.intValue() + 1 );
		defaultIds.put(name, Integer.valueOf(value));
		
		return "<" + name + "_" + value + ">"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	public static int getIDNumber(String id) {
		try {
			int i = id.lastIndexOf("_"); //$NON-NLS-1$
			return Integer.parseInt(id.substring(i+1));
		}
		catch (Exception e) {
			return -1;
		}
	}

	public static String getName(BaseElement element) {
		if (element != null) {
			EStructuralFeature feature = element.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
			if (feature==null)
				feature = getAnyAttribute(element,"name"); //$NON-NLS-1$
			if (feature!=null && element.eGet(feature) instanceof String)
				return (String) element.eGet(feature);
		}
		return null;
	}

	public static boolean hasName(BaseElement obj) {
		EStructuralFeature feature = obj.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature==null)
			feature = getAnyAttribute(obj,"name"); //$NON-NLS-1$
		return feature!=null;
	}
/*	
	public static String getLabel(EObject object) {
		if (object==null)
			return "";
		return toDisplayName(object.eClass().getName());
	}
*/	
	public static String toDisplayName(String anyName) {
		// get rid of the "Impl" java suffix
		anyName = anyName.replaceAll("Impl$", ""); //$NON-NLS-1$ //$NON-NLS-2$
		String displayName = ""; //$NON-NLS-1$
		boolean first = true;
		char[] chars = anyName.toCharArray();
		for (int i=0; i<chars.length; ++i) {
			char c = chars[i];
			if (Character.isUpperCase(c)) {
				if (displayName.length()>0 && i+1<chars.length && !Character.isUpperCase(chars[i+1]))
					displayName += " "; //$NON-NLS-1$
			}
			if (first) {
				c = Character.toUpperCase(c);
				first = false;
			}
			if (c=='_')
				c = ' ';
			displayName += c;
		}
		return displayName.trim();
	}

	@SuppressWarnings("unchecked")
	public static List<EventDefinition> getEventDefinitions(Event event) {
		if (event!=null) {
			EStructuralFeature feature = event.eClass().getEStructuralFeature("eventDefinitions"); //$NON-NLS-1$
			if (feature!=null) {
				return (List<EventDefinition>) event.eGet(feature);
			}
		}
		return new ArrayList<EventDefinition>();
	}
	
	/**
	 * Checks if an event has a specific event definition type defined
	 * 
	 * @param event the event to be checked
	 * @param clazz the class of the event definition to 
	 * @return true if the event definition is defined for this event instance, false otherwise
	 */
	public static boolean hasEventDefinition (Event event, Class<?> clazz) {
		for (EventDefinition def : getEventDefinitions(event)) {
			if (clazz.isInstance(def)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Get the first event definition for an given event and given type
	 * 
	 * @param event the event 
	 * @param clazz the event definition class
	 * @return the first event definition definied for this event instance
	 */
	public static EventDefinition getEventDefinition (Event event, Class<?> clazz) {
		for (EventDefinition def : getEventDefinitions(event)) {
			if (clazz.isInstance(def)) {
				return def;
			}
		}
		return null;
	}

	/**
	 * This is a slightly hacked resource set that we will be using for to solve
	 * the problem of loading the right resources from URLs that betray no
	 * information on the type of the resource.
	 * 
	 * @param resourceSet
	 * 
	 * @return the BPMN2ResourceSetImpl that walks around the problem indicated.
	 * 
	 */

	public static Bpmn2ModelerResourceSetImpl slightlyHackedResourceSet(
			ResourceSet resourceSet) {

		if (resourceSet instanceof Bpmn2ModelerResourceSetImpl) {
			return (Bpmn2ModelerResourceSetImpl) resourceSet;
		}

		Map<Object, Object> map = resourceSet.getLoadOptions();
		Bpmn2ModelerResourceSetImpl result = (Bpmn2ModelerResourceSetImpl) map
				.get(Bpmn2ModelerResourceSetImpl.SLIGHTLY_HACKED_KEY);
		if (result == null) {
			result = new Bpmn2ModelerResourceSetImpl();
			map.put(Bpmn2ModelerResourceSetImpl.SLIGHTLY_HACKED_KEY, result);
		}
		return result;
	}

	/**
	 * Return the resource set that we should be using to load "specific" type
	 * of resources. The "slightlyHacked" resource set is kept in the load
	 * options map.
	 * 
	 * @param eObj
	 * @return the slightly hacked resource set.
	 * 
	 */
	public static Bpmn2ModelerResourceSetImpl slightlyHackedResourceSet(EObject eObj) {
		return slightlyHackedResourceSet(eObj.eResource().getResourceSet());
	}
	
	public static Object resolveXSDObject(Object xsdObject) {
		if (xsdObject instanceof XSDElementDeclaration) {
			XSDElementDeclaration resolvedElement = ((XSDElementDeclaration)xsdObject).getResolvedElementDeclaration();
			if (resolvedElement != null) xsdObject = resolvedElement;
		} else if (xsdObject instanceof XSDAttributeDeclaration) {
			XSDAttributeDeclaration resolvedAttribute = ((XSDAttributeDeclaration)xsdObject).getResolvedAttributeDeclaration();
			if (resolvedAttribute != null) xsdObject = resolvedAttribute;
		}
		return xsdObject;
	}

	/**
	 * @param eObject
	 * @return the namespace map for the given object.
	 */

	@SuppressWarnings("unchecked")
	static public INamespaceMap<String, String> getNamespaceMap(EObject eObject) {

		if (eObject == null) {
			throw new NullPointerException(
					"eObject cannot be null in getNamespaceMap()"); //$NON-NLS-1$
		}

		INamespaceMap<String, String> nsMap = null;
    	// Bug 120110 - this eObject may not have a namespace map, but its
		// ancestors might, so keep searching until we find one or until
		// we run out of ancestors.
		while (nsMap==null && eObject!=null) {
			nsMap = AdapterRegistry.INSTANCE.adapt(
				eObject, INamespaceMap.class);
			if (nsMap==null)
				eObject = eObject.eContainer();
		}
		
		if (nsMap == null) {
			throw new IllegalStateException(
					"INamespaceMap cannot be attached to an eObject"); //$NON-NLS-1$
		}

		return nsMap;
	}

	public static String getNamespacePrefix(EObject eObject, String namespace) {

		for (EObject context = eObject; context != null; context = context
				.eContainer()) {
			List<String> pfxList = getNamespaceMap(context).getReverse(
					namespace);
			if (pfxList.size() > 0) {
				return pfxList.get(0);
			}
		}
		return null;
	}
	
	public enum Bpmn2DiagramType {
		NONE("None"), //$NON-NLS-1$
		PROCESS("Process"), //$NON-NLS-1$
		CHOREOGRAPHY("Choreography"), //$NON-NLS-1$
		COLLABORATION("Collaboration"), //$NON-NLS-1$
		CONVERSATION("Conversation"); //$NON-NLS-1$
		String value;
		Bpmn2DiagramType(String value) {
			this.value = value;
		}

		public static Bpmn2DiagramType fromString(String value) {
			if (value != null) {
				for (Bpmn2DiagramType type : Bpmn2DiagramType.values()) {
					if (value.equalsIgnoreCase(type.value)) {
						return type;
					}
				}
			}
			return null;
		}

		@Override
		public String toString() {
			return value;
		}
	}

	public static Bpmn2DiagramType getDiagramType(String name) {
		for (Bpmn2DiagramType t : Bpmn2DiagramType.values()) {
			if (t.toString().equalsIgnoreCase(name))
				return t;
		}
		return Bpmn2DiagramType.NONE;
	}

	public static DiagramEditor getDiagramEditor(EObject object) {
		return getDiagramEditor(getResource(object));
	}
	
	public static DiagramEditor getDiagramEditor(Resource res) {
		if (res != null) {
			for (Adapter a : res.getResourceSet().eAdapters()) {
				if (a instanceof DiagramEditorAdapter) {
					return ((DiagramEditorAdapter)a).getDiagramEditor();
				}
			}
		}
		return null;
	}
	
	public static Bpmn2DiagramType getDiagramType(EObject object) {
		if (object instanceof Diagram) {
			object = BusinessObjectUtil.getBusinessObjectForPictogramElement((Diagram)object);
		}
		if (object instanceof BPMNDiagram)
			return getDiagramType((BPMNDiagram)object);
		DiagramEditor editor = getDiagramEditor(object);
		return getDiagramType(editor);
	}
	
	public static Bpmn2DiagramType getDiagramType(DiagramEditor editor) {
		if (editor!=null) {
			Diagram diagram = editor.getDiagramTypeProvider().getDiagram();
			if (diagram!=null) {
				EObject object = Graphiti.getLinkService().getBusinessObjectForLinkedPictogramElement(diagram);
				if (object instanceof BPMNDiagram)
					return getDiagramType((BPMNDiagram)object);
			}
		}
		return Bpmn2DiagramType.NONE;
	}
	
	public static Bpmn2DiagramType getDiagramType(BPMNDiagram diagram) {
		if (diagram!=null && getResource(diagram)!=null) {
			BPMNPlane plane = diagram.getPlane();
			if (plane!=null) {
				BaseElement be = plane.getBpmnElement();
				if (be==null)
					be = getDefaultBPMNPlaneReference(diagram);
				if (be instanceof Choreography)
					return Bpmn2DiagramType.CHOREOGRAPHY;
				else if (be instanceof Collaboration)
					return Bpmn2DiagramType.COLLABORATION;
				else
					// everything else (like SubProcess, etc.) belongs to a Process diagram
					return Bpmn2DiagramType.PROCESS;
			}
		}
		return Bpmn2DiagramType.NONE;
	}
	
	/**
	 * Return the first Process, SubProcess, AdHocSubProcess, Transaction, Collaboration,
	 * Choreography or SubChoreography defined in this document.
	 * 
	 * @param object
	 * @return
	 */
	public static BaseElement getDefaultBPMNPlaneReference(EObject object) {
		Definitions definitions = getDefinitions(object);
		if (definitions!=null) {
			for (RootElement re : definitions.getRootElements()) {
				if (	re instanceof Process ||
						re instanceof SubProcess ||
						re instanceof AdHocSubProcess ||
						re instanceof Transaction ||
						re instanceof Collaboration ||
						re instanceof Choreography ||
						re instanceof SubChoreography
				) {
					return re;
				}
			}

		}
		return null;
	}
	
	public static String getDiagramTypeName(BPMNDiagram object) {
		Bpmn2DiagramType type = getDiagramType((BPMNDiagram)object); 
		if (type == Bpmn2DiagramType.CHOREOGRAPHY) {
			return Messages.ModelUtil_Choreograpy_Diagram;
		}
		else if (type == Bpmn2DiagramType.COLLABORATION) {
			return Messages.ModelUtil_Collaboration_Diagram;
		}
		else if (type == Bpmn2DiagramType.PROCESS) {
			return Messages.ModelUtil_Process_Diagram;
		}
		return Messages.ModelUtil_Unknown_Diagram_Type;
	}
	
	public static List<EStructuralFeature> getAnyAttributes(EObject object) {
		List<EStructuralFeature> list = new ArrayList<EStructuralFeature>();
		EStructuralFeature anyAttribute = ((EObject)object).eClass().getEStructuralFeature("anyAttribute"); //$NON-NLS-1$
		if (anyAttribute!=null && object.eGet(anyAttribute) instanceof BasicFeatureMap) {
			BasicFeatureMap map = (BasicFeatureMap)object.eGet(anyAttribute);
			for (Entry entry : map) {
				EStructuralFeature feature = entry.getEStructuralFeature();
				list.add(feature);
			}
		}
		return list;
	}
	
	public static EStructuralFeature getAnyAttribute(EObject object, String name) {
		EStructuralFeature anyAttribute = ((EObject)object).eClass().getEStructuralFeature("anyAttribute"); //$NON-NLS-1$
		if (anyAttribute!=null && object.eGet(anyAttribute) instanceof BasicFeatureMap) {
			BasicFeatureMap map = (BasicFeatureMap)object.eGet(anyAttribute);
			for (Entry entry : map) {
				EStructuralFeature feature = entry.getEStructuralFeature();
				if (feature.getName().equals(name))
					return feature;
			}
		}
		return null;
	}
	
	/**
	 * Removed "deprecated" annotation: ModelExtensionDescriptor.populateObject() needs this  
	 */
	public static EStructuralFeature addAnyAttribute(EObject childObject, String name, Object value) {
		return addAnyAttribute(childObject, childObject.eClass().getEPackage().getNsURI(), name, value);
	}
	
	/**
	 * Removed "deprecated" annotation: ModelExtensionDescriptor.populateObject() needs this  
	 */
	@SuppressWarnings("unchecked")
	public static EStructuralFeature addAnyAttribute(EObject childObject, String namespace, String name, Object value) {
		EStructuralFeature attr = null;
		EStructuralFeature anyAttribute = childObject.eClass().getEStructuralFeature(Bpmn2Package.BASE_ELEMENT__ANY_ATTRIBUTE);
		List<BasicFeatureMap.Entry> anyMap = (List<BasicFeatureMap.Entry>)childObject.eGet(anyAttribute);
		if (anyMap==null)
			return null;
		for (BasicFeatureMap.Entry fe : anyMap) {
			if (fe.getEStructuralFeature() instanceof EAttributeImpl) {
				EAttributeImpl a = (EAttributeImpl) fe.getEStructuralFeature();
				if (namespace.equals(a.getExtendedMetaData().getNamespace()) && name.equals(a.getName())) {
					attr = a;
					break;
				}
			}
		}
		
		// this featuremap can only hold attributes, not elements
		String type = "E" + value.getClass().getSimpleName(); //$NON-NLS-1$
		EDataType eDataType = (EDataType)EcorePackage.eINSTANCE.getEClassifier(type);
		if (eDataType!=null) {
			if (attr==null) {
				attr = ExtendedMetaData.INSTANCE.demandFeature(namespace, name, false);
				attr.setEType(eDataType);
				anyMap.add( FeatureMapUtil.createEntry(attr, value) );
			}
			else {
				EClassifier dt = attr.getEType();
				if (dt==null || !eDataType.getInstanceClass().isAssignableFrom(dt.getInstanceClass()))
					throw new IllegalArgumentException(
						NLS.bind(
							Messages.ModelUtil_Illegal_Value,
							new Object[] {
								childObject.eClass().getName(),
								attr.getName(),
								attr.getEType().getName(),
								value.toString()
							}
						)
					);
				anyMap.add( FeatureMapUtil.createEntry(attr, value) );
			}
		}
		else if (attr==null) {
			attr = ExtendedMetaData.INSTANCE.demandFeature(namespace, name, false);
			anyMap.add( FeatureMapUtil.createEntry(attr, value) );
		}
		else {
			anyMap.add( FeatureMapUtil.createEntry(attr, value) );
		}
		return attr;
	}

	public static boolean isBpmnPackage(EPackage pkg) {
		return pkg == Bpmn2Package.eINSTANCE || pkg == BpmnDiPackage.eINSTANCE || pkg == DcPackage.eINSTANCE || pkg == DiPackage.eINSTANCE;
	}
	
	public static EAttribute createDynamicAttribute(EPackage pkg, EObject object, String name, String type) {
		if (isBpmnPackage(pkg)) {
			throw new IllegalArgumentException(NLS.bind(Messages.ModelUtil_Illegal_EPackage_For_Attribute, pkg.getName()));
		}
		EClass eClass = object instanceof EClass ? (EClass)object : object.eClass(); 
		EAttribute attr = null;
		EClass docRoot = (EClass)pkg.getEClassifier("DocumentRoot"); //$NON-NLS-1$
		if (docRoot==null) {
			ExtendedMetaData.INSTANCE.demandPackage(pkg.getNsURI());
			docRoot = ExtendedMetaData.INSTANCE.getDocumentRoot(pkg);
		}
		if (docRoot!=null) {
			for (EStructuralFeature f : docRoot.getEStructuralFeatures()) {
				if (f.getName().equals(name)) {
					if (f instanceof EAttribute) {
						attr = (EAttribute)f;
						break;
					}
					return null;
				}
			}
		}
		
		if (type==null)
			type = "EString"; //$NON-NLS-1$
		
		EClassifier eClassifier = null;
		if (type!=null) {
			eClassifier = getEClassifierFromString(pkg,type);
			if (eClassifier==null || !(eClassifier instanceof EDataType)) {
				String message =
					NLS.bind(
						Messages.ModelUtil_Unknown_Attribute_Data_Type,
						new Object[] {
							name,
							eClass.getName(),
							type
						}
					);
	
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						Messages.ModelUtil_Internal_Error,
						message);
				throw new IllegalArgumentException(message);
			}
		}
		if (attr==null) {
			attr = EcorePackage.eINSTANCE.getEcoreFactory().createEAttribute();
			attr.setName(name);
			attr.setEType(eClassifier);
			ExtendedMetaData.INSTANCE.setFeatureKind(attr,ExtendedMetaData.ATTRIBUTE_FEATURE);
			
			docRoot.getEStructuralFeatures().add(attr);
			ExtendedMetaData.INSTANCE.setNamespace(attr, pkg.getNsURI());
			ExtendedMetaData.INSTANCE.setDocumentRoot(docRoot);
		}
		else if (eClassifier!=null)
			attr.setEType(eClassifier);
		
		// force this feature to be serialized regardless of whether its value is the default value
		attr.setUnsettable(true);
		
		return attr;
	}
	
	public static EReference createDynamicReference(EPackage pkg, EObject object, String name, String type) {
		if (isBpmnPackage(pkg)) {
			throw new IllegalArgumentException(NLS.bind(Messages.ModelUtil_Illegal_EPackage_For_Reference,pkg.getName()));
		}
		EClass eClass = object instanceof EClass ? (EClass)object : object.eClass(); 
		EReference ref = null;
		EClass docRoot = ExtendedMetaData.INSTANCE.getDocumentRoot(pkg);
		if (docRoot==null) {
			ExtendedMetaData.INSTANCE.demandPackage(pkg.getNsURI());
			docRoot = ExtendedMetaData.INSTANCE.getDocumentRoot(pkg);
			if (docRoot==null) {
				EClassifier e = pkg.getEClassifier("DocumentRoot"); //$NON-NLS-1$
				if (e instanceof EClass) {
					docRoot = (EClass)e;
				}
			}
		}
		if (docRoot!=null) {
			for (EStructuralFeature f : docRoot.getEStructuralFeatures()) {
				if (f.getName().equals(name)) {
					if (f instanceof EReference) {
						ref = (EReference)f;
						break;
					}
					return null;
				}
			}
		}

		EClassifier eClassifier = null;
		if (type!=null) {
			eClassifier = getEClassifierFromString(pkg,type);
			if (eClassifier==null || !(eClassifier instanceof EClass)) {
				String message =
					NLS.bind(
						Messages.ModelUtil_Unknown_Reference_Object_Type,
						new Object[] {
							name,
							eClass.getName(),
							type
						}
					);
	
				MessageDialog.openError(Display.getDefault().getActiveShell(),
						Messages.ModelUtil_Internal_Error,
						message);
				throw new IllegalArgumentException(message);
			}
		}
		if (ref==null) {
			ref = EcorePackage.eINSTANCE.getEcoreFactory().createEReference();
			ref.setName(name);
			ref.setEType(eClassifier);
			ExtendedMetaData.INSTANCE.setFeatureKind(ref,ExtendedMetaData.ATTRIBUTE_FEATURE);
			
			docRoot.getEStructuralFeatures().add(ref);
			ExtendedMetaData.INSTANCE.setNamespace(ref, pkg.getNsURI());
			ExtendedMetaData.INSTANCE.setDocumentRoot(docRoot);
		}
		else if (eClassifier!=null)
			ref.setEType(eClassifier);
		
		return ref;
	}
	
	public static boolean removeDynamicFeature(EPackage pkg, EObject object, String name) {
		if (isBpmnPackage(pkg)) {
			throw new IllegalArgumentException("Can not remove dynamic feature from "+pkg.getName()); //$NON-NLS-1$
		}
		
		EStructuralFeature anyAttribute = ((EObject)object).eClass().getEStructuralFeature("anyAttribute"); //$NON-NLS-1$
		if (anyAttribute!=null && object.eGet(anyAttribute) instanceof BasicFeatureMap) {
			BasicFeatureMap map = (BasicFeatureMap)object.eGet(anyAttribute);
			for (Entry entry : map) {
				EStructuralFeature feature = entry.getEStructuralFeature();
				if (feature.getName().equals(name)) {
					map.remove(entry);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static EClassifier getEClassifierFromString(EPackage pkg, String type) {
		EClassifier eClassifier = null;
		if (type==null) {
			return EcorePackage.eINSTANCE.getEObject();
		}
		if (pkg!=null) {
			eClassifier = pkg.getEClassifier(type);
			if (eClassifier!=null)
				return eClassifier;
		}
		
		eClassifier = EcorePackage.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		eClassifier = Bpmn2Package.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		eClassifier = BpmnDiPackage.eINSTANCE.getEClassifier(type);
		if (eClassifier!=null)
			return eClassifier;
		
		return null;
	}
	
	public static EObject createStringWrapper(String value) {
		DynamicEObjectImpl de = new DynamicEObjectImpl() {
			// prevent owners from trying to resolve this thing - it's just a string!
			public boolean eIsProxy() {
				return false;
			}

			@Override
			public boolean equals(Object object) {
				if (object instanceof DynamicEObjectImpl) {
					DynamicEObjectImpl that = (DynamicEObjectImpl) object;
					if (eProxyURI()==null) {
						return that.eProxyURI()==null;
					}
					String thisString = eProxyURI().toString();
					String thatString = that.eProxyURI() == null ? null : that.eProxyURI().toString();
					return thisString.equals(thatString);
				}
				else if (object instanceof String) {
					String thisString = eProxyURI().toString();
					return thisString.equals(object);
				}
				return super.equals(object);
			}
			
		};
		de.eSetClass(EcorePackage.eINSTANCE.getEObject());
		de.eSetProxyURI(URI.createURI(value));
		return de;
	}
	
	public static String getStringWrapperValue(Object wrapper) {
		if (wrapper instanceof DynamicEObjectImpl) {
			DynamicEObjectImpl de = (DynamicEObjectImpl)wrapper;
			URI uri = de.eProxyURI();
			return uri.toString();
		}
		else if (wrapper instanceof EObject) {
			return EcoreUtil.getURI((EObject)wrapper).toString();
		}
		return null;
	}
	
	public static boolean setStringWrapperValue(Object wrapper, String value) {
		if (isStringWrapper(wrapper)) {
			DynamicEObjectImpl de = (DynamicEObjectImpl)wrapper;
			de.eSetProxyURI(URI.createURI(value));
			return true;
		}
		return false;
	}
	
	public static boolean isStringWrapper(Object wrapper) {
		return wrapper instanceof DynamicEObjectImpl;
	}
	
	public static boolean isElementSelected(IDiagramContainer editor, PictogramElement element) {
		for (PictogramElement search : editor.getSelectedPictogramElements()) {
			if (search.equals(element)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Given an EObject always returns the BPMN2 Resource that is associated with that object.
	 * This may involve searching for all Resources in the ResourceSet that the EObject belongs to.
	 * This also searches for a Resource in the object's InsertionAdapter if the object is not yet
	 * contained in any Resource.
	 * 
	 * @param object
	 * @return
	 */
	public static Resource getResource(EObject object) {
		Resource resource = null;
		if (object!=null) {
			resource = object.eResource();
			if (resource!=null) {
				ResourceSet rs = resource.getResourceSet();
				if (rs!=null) {
					for (Resource r : rs.getResources()) {
						if (r instanceof Bpmn2Resource) {
							return r;
						}
					}
				}
			}
			if (resource==null) {
				InsertionAdapter insertionAdapter = AdapterUtil.adapt(object, InsertionAdapter.class);
				if (insertionAdapter!=null)
					resource = insertionAdapter.getResource();
				// TODO: can we use any of the referenced objects to find a Resource?
			}
		}
		return resource;
	}

	public static Resource getResource(DiagramEditor editor) {
		if (editor!=null)
			return getResource(editor.getDiagramTypeProvider().getDiagram());
		return null;
	}
	
	public static EObject getContainer(EObject object) {
		EObject container = null;
		if (object!=null) {
			container = object.eContainer();
			if (container==null) {
				InsertionAdapter insertionAdapter = AdapterUtil.adapt(object, InsertionAdapter.class);
				if (insertionAdapter!=null)
					container = insertionAdapter.getObject();
			}
		}
		return container;
	}

	public static Definitions getDefinitions(EObject object) {
		Resource resource = getResource(object);
		return getDefinitions(resource);
	}
	
	public static Definitions getDefinitions(Resource resource) {
		if (resource!=null && !resource.getContents().isEmpty() && !resource.getContents().get(0).eContents().isEmpty()) {
			Object defs = resource.getContents().get(0).eContents().get(0);
			if (defs instanceof Definitions)
				return (Definitions)defs;
		}
		return null;
	}
	
	public static DocumentRoot getDocumentRoot(EObject object) {
		Resource resource = getResource(object);
		if (resource!=null) {
			EList<EObject> contents = resource.getContents();
			if (!contents.isEmpty() && contents.get(0) instanceof DocumentRoot)
				return (DocumentRoot)contents.get(0);
		}
		return null;
	}
	
	public static List<EObject> getAllReachableObjects(EObject object, EStructuralFeature feature) {
		ArrayList<EObject> list = null;
		if (object!=null && feature.getEType() instanceof EClass) {
			Resource resource = getResource(object);
			if (resource!=null) {
				EClass eClass = (EClass)feature.getEType();
				if (eClass != EcorePackage.eINSTANCE.getEObject()) {
					list = new ArrayList<EObject>();
					TreeIterator<EObject> contents = resource.getAllContents();
					while (contents.hasNext()) {
						Object item = contents.next();
						if (eClass.isInstance(item)) {
							list.add((EObject)item);
						}
					}
				}
			}
		}
		return list;
	}
	
	public static List<EObject> getAllReachableObjects(EObject object, EClass eClass) {
		ArrayList<EObject> list = null;
		Resource resource = getResource(object);
		if (resource!=null) {
			list = new ArrayList<EObject>();
			if (eClass != EcorePackage.eINSTANCE.getEObject()) {
				TreeIterator<EObject> contents = resource.getAllContents();
				while (contents.hasNext()) {
					Object item = contents.next();
					if (eClass.isInstance(item)) {
						list.add((EObject)item);
					}
				}
			}
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllRootElements(Definitions definitions, final Class<T> class1) {
		ArrayList<T> list = new ArrayList<T>();
		if (definitions!=null) {
			for (RootElement re : definitions.getRootElements()) {
				if (class1.isInstance(re)) {
					list.add((T) re);
				}
			}
		}
		return list;
	}
	
	public static boolean compare(Object v1, Object v2) {
		if (v1==null) {
			if (v2!=null)
				return false;
		}
		else if (v2==null) {
			if (v1!=null)
				return false;
		}
		return v1.equals(v2);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static EObject findNearestAncestor(EObject object, Class[] types) {
		EObject ancestor = null;
		if (object!=null) {
			ancestor = getContainer(object);
			while (ancestor!=null) {
				Class type = ancestor.getClass();
				for (Class t : types) {
					if (t.isAssignableFrom(type))
						return ancestor;
				}
				ancestor = getContainer(ancestor);
			}
		}
		return ancestor;
	}
	
	@SuppressWarnings("rawtypes")
	public static List<EObject> collectAncestorObjects(EObject object, String featureName, Class[] ancestorTypes) {
		return collectAncestorObjects(object, featureName, ancestorTypes, null);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<EObject> collectAncestorObjects(EObject object, String featureName, Class[] ancestorTypes, Class[] objectTypes) {
		List<EObject> values = new ArrayList<EObject>();
		EObject ancestor = ModelUtil.findNearestAncestor(object, ancestorTypes);
		while (ancestor!=null) {
			EStructuralFeature feature = ancestor.eClass().getEStructuralFeature(featureName);
			if (feature!=null && ancestor.eGet(feature) instanceof List) {
				List<EObject> objects = (List<EObject>) ancestor.eGet(feature);
				if (objectTypes==null) {
					values.addAll(objects);
				}
				else {
					for (EObject item : objects) {
						for (Class t : objectTypes) {
							if (t.isAssignableFrom(item.getClass()))
								values.add(item);
						}
					}
				}
			}
			ancestor = ModelUtil.findNearestAncestor(ancestor, ancestorTypes);
		}
		return values;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllExtensionAttributeValues(EObject object, Class<T> clazz) {
		List<T> results = new ArrayList<T>();
		
		if (object!=null) {
			EStructuralFeature evf = object.eClass().getEStructuralFeature("extensionValues"); //$NON-NLS-1$
			EList<ExtensionAttributeValue> list = (EList<ExtensionAttributeValue>)object.eGet(evf);
			for (ExtensionAttributeValue eav : list) {
				FeatureMap fm = eav.getValue();
				for (Entry e : fm) {
					if (clazz.isInstance(e.getValue())) {
						results.add((T)e.getValue());
					}
				}
			}
		}
		return results;
	}
	
	public static List<ExtensionAttributeValue> getExtensionAttributeValues(EObject be) {
		if (be instanceof Participant) {
			final Participant participant = (Participant) be;
			if (participant.getProcessRef() == null) {
				if (participant.eContainer() instanceof Collaboration) {
					Collaboration collab = (Collaboration) participant.eContainer();
					if (collab.eContainer() instanceof Definitions) {
						final Definitions definitions = getDefinitions(collab);
						
						TransactionalEditingDomain domain = TransactionUtil.getEditingDomain(definitions.eResource());
						
						domain.getCommandStack().execute(new RecordingCommand(domain) {
							@Override
							protected void doExecute() {
								Process process = Bpmn2ModelerFactory.create(Process.class);
								participant.setProcessRef(process);
								definitions.getRootElements().add(process);
								ModelUtil.setID(process);
							}
							
						});
						
					}
				}
			}
			return participant.getProcessRef().getExtensionValues();
		}
		if (be instanceof BPMNDiagram) {
			BPMNDiagram diagram = (BPMNDiagram) be;
			BaseElement bpmnElement = diagram.getPlane().getBpmnElement();
			if (bpmnElement instanceof org.eclipse.bpmn2.Process) {
				return bpmnElement.getExtensionValues();
			}
		}
		if (be instanceof BaseElement) {
			return ((BaseElement) be).getExtensionValues();
		}

		return new ArrayList<ExtensionAttributeValue>();
	}
	
	@SuppressWarnings("unchecked")
	public static void addExtensionAttributeValue(EObject object, EStructuralFeature feature, Object value) {
		EStructuralFeature evf = object.eClass().getEStructuralFeature("extensionValues"); //$NON-NLS-1$
		EList<EObject> list = (EList<EObject>)object.eGet(evf);
		
		if (list.size()==0) {
			ExtensionAttributeValue newItem = Bpmn2ModelerFactory.create(ExtensionAttributeValue.class);
			FeatureMap map = newItem.getValue();
			map.add(feature, value);
			list.add(newItem);
			ModelUtil.setID(newItem);
		}
		else {
			ExtensionAttributeValue oldItem = (ExtensionAttributeValue) list.get(0);
			FeatureMap map = oldItem.getValue();
			map.add(feature, value);
		}
	}

	/*
	 * Various model object and feature UI property methods
	 */
	@SuppressWarnings("rawtypes")
	public static String getLabel(Object object) {
		String label = ""; //$NON-NLS-1$
		if (object instanceof EObject) {
			EObject eObject = (EObject)object;
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eObject);
			if (adapter!=null)
				label = adapter.getObjectDescriptor().getLabel(eObject);
			else
				label = toDisplayName( eObject.eClass().getName() );
		}
		else
			label = object.toString();
		label = label.replaceAll(" Ref$", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return label;
	}

	@SuppressWarnings("rawtypes")
	public static void setLabel(EObject object, EStructuralFeature feature, String label) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null)
			adapter.getFeatureDescriptor(feature).setLabel(label);
	}

	@SuppressWarnings("rawtypes")
	public static String getLabel(EObject object, EStructuralFeature feature) {
		String label = ""; //$NON-NLS-1$
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null)
			label = adapter.getFeatureDescriptor(feature).getLabel(object);
		else
			label = toDisplayName( feature.getName() );
		label = label.replaceAll(" Ref$", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return label;
	}

	@SuppressWarnings("rawtypes")
	public static String getDisplayName(Object object) {
		if (object instanceof EObject) {
			EObject eObject = (EObject)object;
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(eObject);
			if (adapter!=null) {
				String text = adapter.getObjectDescriptor().getDisplayName(eObject);
				if (text!=null && !text.isEmpty()) {
					return text;
				}
			}
			return getLongDisplayName(eObject);
		}
		return object==null ? null : object.toString();
	}

	@SuppressWarnings("rawtypes")
	public static String getDisplayName(EObject object, EStructuralFeature feature) {
		if (feature==null)
			return getDisplayName(object);
		
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null)
			return adapter.getFeatureDescriptor(feature).getDisplayName(object);
		return getLongDisplayName(object, feature);
	}

	@SuppressWarnings("rawtypes")
	public static boolean setMultiLine(EObject object, EStructuralFeature feature, boolean multiLine) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null) {
			adapter.getFeatureDescriptor(feature).setMultiLine(multiLine);
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isMultiLine(EObject object, EStructuralFeature feature) {
		if (feature==null)
			return false;
		
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null)
			return adapter.getFeatureDescriptor(feature).isMultiLine(object);
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Hashtable<String, Object> getChoiceOfValues(EObject object, EStructuralFeature feature) {
		if (feature==null)
			return null;
		
		if (feature.getEType() instanceof EEnum) {
			EEnum en = (EEnum)feature.getEType();
			Hashtable<String,Object> choices = new Hashtable<String,Object>();
			for (EEnumLiteral el : en.getELiterals()) {
				choices.put(el.getLiteral(), el.getInstance());
			}
			return choices;
		}
		
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null)
			return adapter.getFeatureDescriptor(feature).getChoiceOfValues(object);
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static boolean setValue(TransactionalEditingDomain domain, final EObject object, final EStructuralFeature feature, Object value) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);

		try {
			InsertionAdapter.executeIfNeeded(object);
			if (value instanceof EObject) {
				// make sure the new object is added to its control first
				// so that it inherits the control's Resource and EditingDomain
				// before we try to change its value.
				InsertionAdapter.executeIfNeeded((EObject)value);
			}
			if (value instanceof String && ((String) value).isEmpty()) {
				if (!(feature.getDefaultValue() instanceof String))
					value = null;
			}
			
			if (adapter!=null) {
				if (!adapter.getFeatureDescriptor(feature).equals(value)) {
					adapter.getFeatureDescriptor(feature).setValue(value);
				}
			}
			else if (domain!=null) {
				final Object v = value;
				domain.getCommandStack().execute(new RecordingCommand(domain) {
					@Override
					protected void doExecute() {
						if (object.eGet(feature) instanceof List) {
							((List)object.eGet(feature)).add(v);
						}
						else
							object.eSet(feature, v);
					}
				});
			}
			else {
				if (object.eGet(feature) instanceof List) {
					((List)object.eGet(feature)).add(value);
				}
				else
					object.eSet(feature, value);
			}
		} catch (Exception e) {
			ErrorUtils.showErrorMessage(e.getMessage());
			return false;
		}
		return true;
	}

	@SuppressWarnings("rawtypes")
	public static Object getValue(final EObject object, final EStructuralFeature feature) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		Object value = adapter==null ? object.eGet(feature) : adapter.getFeatureDescriptor(feature).getValue();
		return value;
	}

	public static boolean compare(EObject object1, EObject object2, boolean similar) {
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object1, null);
		if (adapter!=null)
			return adapter.getObjectDescriptor().compare(object1, object2, similar);
		return object1.equals(object2);
	}
	
	@SuppressWarnings("rawtypes")
	public static boolean canEdit(EObject object, EStructuralFeature feature) {
		if (feature!=null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter!=null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_EDIT);
				if (result instanceof Boolean)
					return ((Boolean)result);
			}
			if (feature instanceof EReference) {
				if (((EReference)feature).isContainment())
					return true;
				if (Bpmn2Package.eINSTANCE.getRootElement().isSuperTypeOf((EClass)feature.getEType()))
					return true;
				if (feature.isMany())
					return true;
				return false;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean canCreateNew(EObject object, EStructuralFeature feature) {
		if (feature!=null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter!=null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_CREATE_NEW);
				if (result instanceof Boolean)
					return ((Boolean)result);
			}
			if (feature instanceof EReference) {
				if (((EReference)feature).isContainment())
					return true;
				if (Bpmn2Package.eINSTANCE.getRootElement().isSuperTypeOf((EClass)feature.getEType()))
					return true;
				return false;
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean canEditInline(EObject object, EStructuralFeature feature) {
		if (feature!=null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter!=null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_EDIT_INLINE);
				if (result instanceof Boolean)
					return ((Boolean)result);
			}
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean canSetNull(EObject object, EStructuralFeature feature) {
		if (feature!=null && feature.getEType() instanceof EClass) {
			ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
			if (adapter!=null) {
				Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_CAN_SET_NULL);
				if (result instanceof Boolean)
					return ((Boolean)result);
			}
			return true;
		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	public static boolean isMultiChoice(EObject object, EStructuralFeature feature) {
		if (feature==null) {
			return false;
		}
		if (feature.getEType() instanceof EEnum) {
			return true;
		}
		
		ExtendedPropertiesAdapter adapter = ExtendedPropertiesAdapter.adapt(object, feature);
		if (adapter!=null) {
			Object result = adapter.getProperty(feature, ExtendedPropertiesAdapter.UI_IS_MULTI_CHOICE);
			if (result instanceof Boolean)
				return ((Boolean)result);
		}

		return getChoiceOfValues(object,feature) != null;
	}

	/*
	 * Fallbacks in case a property provider does not exist
	 */
	public static String getLongDisplayName(EObject object) {
		String objName = null;
		if (object instanceof BPMNDiagram) {
			Bpmn2DiagramType type = getDiagramType((BPMNDiagram)object); 
			if (type == Bpmn2DiagramType.CHOREOGRAPHY) {
				objName = Messages.ModelUtil_Choreography_Diagram;
			}
			else if (type == Bpmn2DiagramType.COLLABORATION) {
				objName = Messages.ModelUtil_Collaboration_Diagram;
			}
			else if (type == Bpmn2DiagramType.PROCESS) {
				objName = Messages.ModelUtil_Process_Diagram;
			}
		}
		if (objName==null){
			objName = toDisplayName( object.eClass().getName() );
		}
		EStructuralFeature feature = object.eClass().getEStructuralFeature("name"); //$NON-NLS-1$
		if (feature!=null) {
			String name = (String)object.eGet(feature);
			if (name==null || name.isEmpty())
				name = NLS.bind(Messages.ModelUtil_Unnamed_Object, objName);
			else
				name = objName + " \"" + name + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			return name;
		}
		feature = object.eClass().getEStructuralFeature("id"); //$NON-NLS-1$
		if (feature!=null) {
			String id = (String)object.eGet(feature);
			if (id==null || id.isEmpty())
				id = Messages.ModelUtil_Unknown_Object + objName;
			else
				id = objName + " \"" + id + "\""; //$NON-NLS-1$ //$NON-NLS-2$
			return id;
		}
		feature = object.eClass().getEStructuralFeature("qName"); //$NON-NLS-1$
		if (feature!=null) {
			Object qName = object.eGet(feature);
			if (qName!=null) {
				return qName.toString();
			}
		}
		return objName;
	}

	public static String getLongDisplayName(EObject object, EStructuralFeature feature) {
		Object value = object.eGet(feature);
		if (value==null)
			return ""; //$NON-NLS-1$
		return value.toString();
	}

	public static boolean isEmpty(Object result) {
		if (result == null)
			return true;
		if (result instanceof String)
			return ((String) result).isEmpty();
		return false;
	}

	public static void disposeChildWidgets(Composite parent) {
		int i = 0;
		Control[] kids = parent.getChildren();
		for (Control k : kids) {
			if (k instanceof Composite) {
				disposeChildWidgets((Composite)k);
			}
			k.dispose();
			++i;
		}
		kids = parent.getChildren();
	}

	/**
	 * Ugly hack to force layout of the entire widget tree of the property sheet page.
	 * @param parent
	 */
	public static void recursivelayout(Composite parent) {
		Control[] kids = parent.getChildren();
		for (Control k : kids) {
			if (k.isDisposed())
				Activator.logError(new SWTException("Widget is disposed.")); //$NON-NLS-1$
			if (k instanceof Composite) {
				recursivelayout((Composite)k);
				((Composite)k).layout(true);
			}
		}
		parent.layout(true);
	}

	public static DiagramEditor getEditor(EObject object) {
		Resource resource = InsertionAdapter.getResource(object);
		if(resource!=null)
			return getEditor(resource.getResourceSet());
		return null;
	}

	public static DiagramEditor getEditor(Resource resource) {
		if(resource!=null)
			return getEditor(resource.getResourceSet());
		return null;
	}
	
	public static DiagramEditor getEditor(ResourceSet resourceSet) {
	    Iterator<Adapter> it = resourceSet.eAdapters().iterator();
	    while (it.hasNext()) {
	        Object next = it.next();
	        if (next instanceof DiagramEditorAdapter) {
	            return ((DiagramEditorAdapter)next).getDiagramEditor();
	        }
	    }
	    return null;
	}

	public static EPackage getEPackage(EStructuralFeature feature) {
		EObject o = feature;
		while ( o.eContainer()!=null ) {
			o = o.eContainer();
			if (o instanceof EPackage) {
				return (EPackage)o;
			}
		}
		return null;
	}
	
	/**
	 * This is a workaround to deal with FormalExpressions: if the "body" of an expression
	 * is null, the default FormalExpression.getBody() method returns the string "null"
	 * which is not exactly what we want! We need to know if the body is actually null,
	 * or if it contains the string "null".
	 * 
	 * @param expression
	 * @return
	 */
	public static String getExpressionBody(FormalExpression expression) {
		String body = null;
        if (expression.getMixed() != null && !expression.getMixed().isEmpty()) {
            StringBuilder result = new StringBuilder();
            boolean isNull = true;
            for (FeatureMap.Entry cur : expression.getMixed()) {
                switch (cur.getEStructuralFeature().getFeatureID()) {
                case XMLTypePackage.XML_TYPE_DOCUMENT_ROOT__CDATA:
                case XMLTypePackage.XML_TYPE_DOCUMENT_ROOT__TEXT:
                	if (cur.getValue()!=null) {
                		isNull = false;
                		result.append(cur.getValue());
                	}
                    break;

                default:
                    break;
                }
            }
            if (!isNull)
            	body = result.toString();
        }
        return body;
    }

	public static List<Tuple<EObject,EObject>> findDuplicateIds(Resource resource) {
		List<Tuple<EObject,EObject>> list = new ArrayList<Tuple<EObject,EObject>>();
		Definitions definitions = ModelUtil.getDefinitions(resource);
		TreeIterator<EObject> iter1 = definitions.eAllContents();
		HashSet<EObject> map = new HashSet<EObject>();
		while (iter1.hasNext()) {
			EObject o1 = iter1.next();
			EStructuralFeature id1Feature = o1.eClass().getEIDAttribute();
			if (id1Feature!=null && !map.contains(o1)) {
				TreeIterator<EObject> iter2 = definitions.eAllContents();
				map.add(o1);
				String id1 = (String)o1.eGet(id1Feature);
				
				while (iter2.hasNext()) {
					EObject o2 = iter2.next();
					EStructuralFeature id2Feature = o2.eClass().getEIDAttribute();
					if (id2Feature!=null && o1!=o2 && !map.contains(o2)) {
						String id2 = (String)o2.eGet(id2Feature);
						if (id1!=null && !id1.isEmpty() && id2!=null && !id2.isEmpty()) {
							if (id1.equals(id2)) {
								list.add( new Tuple<EObject,EObject>(o1,o2) );
							}
						}
					}
				}
			}
		}
		
		return list;
	}
	
}
