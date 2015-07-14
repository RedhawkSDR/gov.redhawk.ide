package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.ItemProvider;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;

public abstract class SadPropertiesStructSequenceNestedProperty<E extends AbstractProperty> extends ItemProvider implements SadProperty {

	protected final E definition;

	public SadPropertiesStructSequenceNestedProperty(AdapterFactory adapterFactory, E def, Object parent) {
		super(adapterFactory);
		this.parent = parent;
		this.definition = def;
	}

	@Override
	public SadPropertiesStructSequence getParent() {
		return (SadPropertiesStructSequence) super.getParent();
	}

	protected abstract List< ? > getRefValues(List<StructValue> values);

	@Override
	public String getPrfValue() {
		StructSequence seq = getParent().getDefinition();
		List<?> retVal = getRefValues(seq.getStructValue());
		return Arrays.toString(retVal.toArray());
	}

	@Override
	public Object getSadValue() {
		StructSequenceRef structSequenceRef = getParent().getValueRef();
		if (structSequenceRef != null) {
			List< ? > values = getRefValues(structSequenceRef.getStructValue());
			return Arrays.toString(values.toArray());
		}
		return null;
	}

	@Override
	public Collection< ? > getKinds() {
		return getParent().getKinds();
	}

	@Override
	public AbstractProperty getDefinition() {
		return definition;
	}

	@Override
	public boolean isAssemblyControllerProperty() {
		return getParent().isAssemblyControllerProperty();
	}

	@Override
	public String getExternalID() {
		return null;
	}

	@Override
	public boolean canSetExternalId() {
		return false;
	}

	@Override
	public void setExternalID(String newExternalID) {
	}

	@Override
	public void setSadValue(Object value) {
	}

	@Override
	public String getID() {
		return definition.getId();
	}
}
