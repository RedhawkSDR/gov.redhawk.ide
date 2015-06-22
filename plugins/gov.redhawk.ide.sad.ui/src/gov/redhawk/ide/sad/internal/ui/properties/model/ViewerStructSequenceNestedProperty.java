package gov.redhawk.ide.sad.internal.ui.properties.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructSequenceRef;
import mil.jpeojtrs.sca.prf.StructValue;

public abstract class ViewerStructSequenceNestedProperty<E extends AbstractProperty> extends ViewerProperty<E> {

	public ViewerStructSequenceNestedProperty(E def, Object parent) {
		super(def, parent);
	}

	@Override
	public ViewerStructSequenceProperty getParent() {
		return (ViewerStructSequenceProperty) super.getParent();
	}

	protected abstract List< ? > getRefValues(List<StructValue> values);

	@Override
	public String getPrfValue() {
		StructSequence seq = getParent().getDefinition();
		List<?> retVal = getRefValues(seq.getStructValue());
		return Arrays.toString(retVal.toArray());
	}

	@Override
	public Object getValue() {
		StructSequenceRef structSequenceRef = getParent().getRef();
		if (structSequenceRef != null) {
			List< ? > values = getRefValues(structSequenceRef.getStructValue());
			return Arrays.toString(values.toArray());
		}
		return null;
	}

	@Override
	protected Collection< ? > getKindTypes() {
		return null;
	}
}
