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
package gov.redhawk.ide.sad.internal.ui.properties;

import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerComponent;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSequenceProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerSimpleProperty;
import gov.redhawk.ide.sad.internal.ui.properties.model.ViewerStructSequenceSimpleProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleRef;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.Struct;
import mil.jpeojtrs.sca.prf.StructSequence;
import mil.jpeojtrs.sca.prf.StructValue;
import mil.jpeojtrs.sca.prf.Values;
import mil.jpeojtrs.sca.prf.provider.PrfItemProviderAdapterFactory;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 */
public class PropertiesViewerLabelProvider extends XViewerLabelProvider {

	private ComposedAdapterFactory adapterFactory;
	private AdapterFactoryLabelProvider labelProvider;

	/**
	 * @param viewer
	 */
	public PropertiesViewerLabelProvider(XViewer viewer) {
		super(viewer);
		adapterFactory = new ComposedAdapterFactory();
		adapterFactory.addAdapterFactory(new SadItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new PrfItemProviderAdapterFactory());
		labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
	}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		adapterFactory.dispose();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getColumnImage(java.lang.Object, org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
	 */
	@Override
	public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		if (columnIndex == 0) {
			if (element instanceof ViewerComponent) {
				ViewerComponent component = ((ViewerComponent) element);
				return labelProvider.getImage(component.getComponentInstantiation());
			} else if (element instanceof ViewerProperty< ? >) {
				ViewerProperty< ? > prop = (ViewerProperty< ? >) element;
				return labelProvider.getImage(prop.getDefinition());
			}
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider#getColumnText(java.lang.Object, org.eclipse.nebula.widgets.xviewer.XViewerColumn, int)
	 */
	@Override
	public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		if (xCol.equals(PropertiesViewerFactory.ID)) {
			return getID(element);
		} else if (xCol.equals(PropertiesViewerFactory.NAME)) {
			return getName(element);
		} else if (xCol.equals(PropertiesViewerFactory.PRF_VALUE)) {
			return getPrfValue(element);
		} else if (xCol.equals(PropertiesViewerFactory.SAD_VALUE)) {
			return getSadValue(element);
		} else if (xCol.equals(PropertiesViewerFactory.EXTERNAL)) {
			return getExternalValue(element);
		} else if (xCol.equals(PropertiesViewerFactory.KIND)) {
			return getKind(element);
		} else if (xCol.equals(PropertiesViewerFactory.MODE)) {
			return getMode(element);
		} else if (xCol.equals(PropertiesViewerFactory.TYPE)) {
			return getType(element);
		} else if (xCol.equals(PropertiesViewerFactory.DESCRIPTION)) {
			return getDescription(element);
		}
		return "";
	}

	public String getExternalValue(Object element) {
		if (element instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > prop = (ViewerProperty< ? >) element;
			return prop.getExternalID();
		}
		return "";
	}

	public String getDescription(Object element) {
		if (element instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > prop = (ViewerProperty< ? >) element;
			return prop.getDefinition().getDescription();
		}
		return "";
	}

	public String getType(Object element) {
		if (element instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > prop = (ViewerProperty< ? >) element;
			AbstractProperty def = prop.getDefinition();
			if (def instanceof Simple) {
				Simple simple = (Simple) def;
				return simple.getType().getLiteral();
			} else if (def instanceof SimpleSequence) {
				SimpleSequence seq = (SimpleSequence) def;
				return seq.getType().getLiteral();
			}
		}
		return "";
	}

	public String getMode(Object element) {
		if (element instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > prop = (ViewerProperty< ? >) element;
			if (prop.getParent() instanceof ViewerComponent) {
				return prop.getDefinition().getMode().getLiteral();
			}
		}
		return null;
	}

	public String getKind(Object element) {
		if (element instanceof ViewerProperty< ? >) {
			ViewerProperty< ? > prop = (ViewerProperty< ? >) element;
			if (prop.getParent() instanceof ViewerComponent) {
				AbstractProperty def = prop.getDefinition();
				if (def instanceof Simple) {
					Simple simple = (Simple) def;
					return toKindString(simple.getKind());
				} else if (def instanceof SimpleSequence) {
					SimpleSequence seq = (SimpleSequence) def;
					return toKindString(seq.getKind());
				} else if (def instanceof Struct) {
					Struct struct = (Struct) def;
					return toConfigurationKindString(struct.getConfigurationKind());
				} else if (def instanceof StructSequence) {
					StructSequence seq = (StructSequence) def;
					return toConfigurationKindString(seq.getConfigurationKind());
				}
			}
		}
		return "";
	}

	private String toConfigurationKindString(EList<ConfigurationKind> configurationKind) {
		List<String> retVal = new ArrayList<String>(configurationKind.size());
		for (ConfigurationKind k : configurationKind) {
			retVal.add(k.getType().getLiteral());
		}
		return Arrays.toString(retVal.toArray());
	}

	private String toKindString(EList<Kind> kind) {
		List<String> retVal = new ArrayList<String>(kind.size());
		for (Kind k : kind) {
			retVal.add(k.getType().getLiteral());
		}
		return Arrays.toString(retVal.toArray());
	}

	public String getID(Object element) {
		if (element instanceof ViewerComponent) {
			return ((ViewerComponent) element).getComponentInstantiation().getId();
		} else if (element instanceof ViewerProperty< ? >) {
			return ((ViewerProperty< ? >) element).getDefinition().getId();
		}
		return "";
	}

	public String getName(Object element) {
		if (element instanceof ViewerComponent) {
			return ((ViewerComponent) element).getComponentInstantiation().getUsageName();
		} else if (element instanceof ViewerProperty< ? >) {
			return ((ViewerProperty< ? >) element).getDefinition().getName();
		}
		return "";
	}

	public String getPrfValue(Object element) {
		if (element instanceof ViewerSimpleProperty) {
			return ((ViewerSimpleProperty) element).getDefinition().getValue();
		} else if (element instanceof ViewerSequenceProperty) {
			Values values = ((ViewerSequenceProperty) element).getDefinition().getValues();
			if (values != null) {
				return Arrays.toString(values.getValue().toArray());
			}
		} else if (element instanceof ViewerStructSequenceSimpleProperty) {
			ViewerStructSequenceSimpleProperty prop = (ViewerStructSequenceSimpleProperty) element;
			StructSequence seq = prop.getParent().getDefinition();
			EList<StructValue> value = seq.getStructValue();
			List<String> retVal = new ArrayList<String>(value.size());
			for (StructValue v : value) {
				SimpleRef ref = v.getRef(prop.getDefinition().getId());
				retVal.add(ref.getValue());
			}
			return Arrays.toString(retVal.toArray());
		}
		return "";
	}

	public String getSadValue(Object element) {
		if (element instanceof ViewerSimpleProperty) {
			ViewerSimpleProperty prop = ((ViewerSimpleProperty) element);
			return prop.getValue();
		} else if (element instanceof ViewerSequenceProperty) {
			ViewerSequenceProperty prop = (ViewerSequenceProperty) element;
			return Arrays.toString(prop.getValues().toArray());
		} else if (element instanceof ViewerStructSequenceSimpleProperty) {
			ViewerStructSequenceSimpleProperty prop = (ViewerStructSequenceSimpleProperty) element;
			return Arrays.toString(prop.getValues().toArray());
		}
		return "";
	}

}
