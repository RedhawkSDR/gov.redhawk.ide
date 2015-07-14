package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Collection;

import mil.jpeojtrs.sca.prf.AbstractProperty;

public interface SadProperty {

	AbstractProperty getDefinition();

	boolean isAssemblyControllerProperty();

	String getExternalID();

	boolean canSetExternalId();

	void setExternalID(String newExternalID);

	Object getSadValue();

	void setSadValue(Object value);

	String getPrfValue();

	String getID();

	Collection< ? > getKinds();

}
