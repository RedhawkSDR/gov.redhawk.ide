/**
 */
package gov.redhawk.ide.sad.graphiti.ext;

import gov.redhawk.ide.graphiti.ext.RHContainerShape;
import gov.redhawk.ide.sad.graphiti.ui.diagram.patterns.ComponentPattern;
import org.eclipse.graphiti.features.context.IAddContext;
import org.eclipse.graphiti.features.context.IUpdateContext;
import org.eclipse.graphiti.features.impl.Reason;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Component Shape</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape#isStarted <em>Started</em>}</li>
 *   <li>{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape#getEvent <em>Event</em>}</li>
 * </ul>
 * </p>
 *
 * @see gov.redhawk.ide.sad.graphiti.ext.RHSadGxPackage#getComponentShape()
 * @model
 * @generated
 */
public interface ComponentShape extends RHContainerShape {
	/**
	 * Returns the value of the '<em><b>Started</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Started</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Started</em>' attribute.
	 * @see #setStarted(boolean)
	 * @see gov.redhawk.ide.sad.graphiti.ext.RHSadGxPackage#getComponentShape_Started()
	 * @model unique="false"
	 * @generated
	 */
	boolean isStarted();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape#isStarted <em>Started</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Started</em>' attribute.
	 * @see #isStarted()
	 * @generated
	 */
	void setStarted(boolean value);

	/**
	 * Returns the value of the '<em><b>Event</b></em>' attribute.
	 * The literals are from the enumeration {@link gov.redhawk.ide.sad.graphiti.ext.Event}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Event</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Event</em>' attribute.
	 * @see gov.redhawk.ide.sad.graphiti.ext.Event
	 * @see #setEvent(Event)
	 * @see gov.redhawk.ide.sad.graphiti.ext.RHSadGxPackage#getComponentShape_Event()
	 * @model unique="false"
	 * @generated
	 */
	Event getEvent();

	/**
	 * Sets the value of the '{@link gov.redhawk.ide.sad.graphiti.ext.ComponentShape#getEvent <em>Event</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Event</em>' attribute.
	 * @see gov.redhawk.ide.sad.graphiti.ext.Event
	 * @see #getEvent()
	 * @generated
	 */
	void setEvent(Event value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model contextDataType="gov.redhawk.ide.sad.graphiti.ext.IAddContext" contextUnique="false" patternDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentPattern" patternUnique="false"
	 * @generated
	 */
	void init(IAddContext context, ComponentPattern pattern);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * performs a layout on the contents of this shape
	 * <!-- end-model-doc -->
	 * @model
	 * @generated
	 */
	void layout();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Updates the shape with supplied values
	 * <!-- end-model-doc -->
	 * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" contextDataType="gov.redhawk.ide.sad.graphiti.ext.IUpdateContext" contextUnique="false" patternDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentPattern" patternUnique="false"
	 * @generated
	 */
	Reason update(IUpdateContext context, ComponentPattern pattern);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * <!-- begin-model-doc -->
	 * Checks if shape requires an update.
	 * If update required returns Reason with true
	 * boolean value and message describing what needs to be updated
	 * <!-- end-model-doc -->
	 * @model dataType="gov.redhawk.ide.sad.graphiti.ext.Reason" unique="false" contextDataType="gov.redhawk.ide.sad.graphiti.ext.IUpdateContext" contextUnique="false" patternDataType="gov.redhawk.ide.sad.graphiti.ext.ComponentPattern" patternUnique="false"
	 * @generated
	 */
	Reason updateNeeded(IUpdateContext context, ComponentPattern pattern);

} // ComponentShape
