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

import gov.redhawk.ide.sad.internal.ui.properties.model.SadProperty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mil.jpeojtrs.sca.prf.AbstractProperty;
import mil.jpeojtrs.sca.prf.ConfigurationKind;
import mil.jpeojtrs.sca.prf.Enumeration;
import mil.jpeojtrs.sca.prf.Kind;
import mil.jpeojtrs.sca.prf.Simple;
import mil.jpeojtrs.sca.prf.SimpleSequence;
import mil.jpeojtrs.sca.prf.provider.PrfItemProviderAdapterFactory;
import mil.jpeojtrs.sca.sad.SadComponentInstantiation;
import mil.jpeojtrs.sca.sad.provider.SadItemProviderAdapterFactory;

import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerLabelProvider;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;

public class PropertiesViewerLabelProvider extends XViewerLabelProvider {

	private ComposedAdapterFactory adapterFactory;
	private AdapterFactoryLabelProvider labelProvider;

	public PropertiesViewerLabelProvider(XViewer viewer) {
		super(viewer);
		adapterFactory = new ComposedAdapterFactory();
		adapterFactory.addAdapterFactory(new SadItemProviderAdapterFactory());
		adapterFactory.addAdapterFactory(new PrfItemProviderAdapterFactory());
		labelProvider = new AdapterFactoryLabelProvider(adapterFactory);
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// do nothing
	}

	@Override
	public void dispose() {
		adapterFactory.dispose();
	}

	@Override
	public Image getColumnImage(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		if (columnIndex == 0) {
			if (element instanceof SadProperty) {
				SadProperty prop = (SadProperty) element;
				return labelProvider.getImage(prop.getDefinition());
			} else {
				return labelProvider.getImage(element);
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
		String retVal = internalGetColumnText(element, xCol, columnIndex);
		if (retVal == null) {
			return "";
		}
		return retVal;
	}

	private String internalGetColumnText(Object element, XViewerColumn xCol, int columnIndex) throws Exception {
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
		} else if (xCol.equals(PropertiesViewerFactory.ACTION)) {
			return getAction(element);
		} else if (xCol.equals(PropertiesViewerFactory.ENUMERATIONS)) {
			return getEnumerations(element);
		} else if (xCol.equals(PropertiesViewerFactory.RANGE)) {
			return getRange(element);
		} else if (xCol.equals(PropertiesViewerFactory.UNITS)) {
			return getUnits(element);
		}
		return "";
	}

	private String getAction(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			return getAction(prop.getDefinition());
		} else if (element instanceof Simple) {
			Simple simple = (Simple) element;
			if (simple.getAction() != null && simple.getAction().getType() != null) {
				return simple.getAction().getType().getLiteral();
			}
		} else if (element instanceof SimpleSequence) {
			SimpleSequence seq = (SimpleSequence) element;
			if (seq.getAction() != null && seq.getAction().getType() != null) {
				return seq.getAction().getType().getLiteral();
			}
		}
		return "";
	}

	public String getEnumerations(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			return getEnumerations(prop.getDefinition());
		} else if (element instanceof Simple) {
			Simple simple = (Simple) element;
			if (simple.getEnumerations() != null) {
				List<String> retVal = new ArrayList<String>();
				for (Enumeration en : simple.getEnumerations().getEnumeration()) {
					retVal.add(en.getLabel() + "=" + en.getValue());
				}
				return retVal.toString();
			} else {
				return "";
			}
		}
		return "";
	}

	public String getRange(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			return getRange(prop.getDefinition());
		} else if (element instanceof Simple) {
			Simple simple = (Simple) element;
			if (simple.getRange() != null) {
				return "[" + simple.getRange().getMin() + ", " + simple.getRange().getMax() + "]";
			} else {
				return "";
			}
		} else if (element instanceof SimpleSequence) {
			SimpleSequence seq = (SimpleSequence) element;
			if (seq.getRange() != null) {
				return "[" + seq.getRange().getMin() + ", " + seq.getRange().getMax() + "]";
			} else {
				return "";
			}
		}
		return "";
	}

	public String getUnits(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			return getUnits(prop.getDefinition());
		} else if (element instanceof Simple) {
			Simple simple = (Simple) element;
			return simple.getUnits();
		} else if (element instanceof SimpleSequence) {
			SimpleSequence seq = (SimpleSequence) element;
			return seq.getUnits();
		}
		return "";
	}

	public String getExternalValue(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			if (prop.isAssemblyControllerProperty()) {
				String value = prop.getExternalID();
				if (value != null) {
					return value;
				}
				return prop.getID();
			}
			return prop.getExternalID();
		}
		return "";
	}

	@Override
	public Color getForeground(Object element, XViewerColumn xCol, int columnIndex) {
		if (xCol.equals(PropertiesViewerFactory.EXTERNAL)) {
			if (element instanceof SadProperty) {
				SadProperty prop = (SadProperty) element;
				if (prop.isAssemblyControllerProperty() && prop.getExternalID() == null) {
					return PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GRAY);
				}
			}
		}
		return super.getForeground(element, xCol, columnIndex);
	}

	public String getDescription(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			return prop.getDefinition().getDescription();
		}
		return "";
	}

	public String getType(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			AbstractProperty def = prop.getDefinition();
			if (def instanceof Simple) {
				Simple simple = (Simple) def;
				if (simple.isComplex()) {
					return "complex " + simple.getType().getLiteral();
				} else {
					return simple.getType().getLiteral();
				}
			} else if (def instanceof SimpleSequence) {
				SimpleSequence seq = (SimpleSequence) def;
				if (seq.isComplex()) {
					return "complex " + seq.getType().getLiteral();
				} else {
					return seq.getType().getLiteral();
				}
			}
		}
		return "";
	}

	public String getMode(Object element) {
		if (element instanceof SadProperty) {
			SadProperty prop = (SadProperty) element;
			return prop.getDefinition().getMode().getLiteral();
		}
		return null;
	}

	public String getKind(Object element) {
		if (element instanceof SadProperty) {
			final Collection< ? > kinds = ((SadProperty) element).getKinds();
			return toKindString(kinds);
		}
		return null;
	}

	private String toKindString(Collection< ? > kinds) {
		List<String> retVal = new ArrayList<String>(kinds.size());
		for (Object kind : kinds) {
			if (kind instanceof Kind) {
				retVal.add(((Kind) kind).getType().getLiteral());
			} else if (kind instanceof ConfigurationKind) {
				retVal.add(((ConfigurationKind) kind).getType().getLiteral());
			}
		}
		return Arrays.toString(retVal.toArray());
	}

	public String getID(Object element) {
		if (element instanceof SadComponentInstantiation) {
			return ((SadComponentInstantiation) element).getId();
		} else if (element instanceof SadProperty) {
			return ((SadProperty) element).getDefinition().getId();
		}
		return "";
	}

	public String getName(Object element) {
		if (element instanceof SadComponentInstantiation) {
			return ((SadComponentInstantiation) element).getUsageName();
		} else if (element instanceof SadProperty) {
			AbstractProperty prop = ((SadProperty) element).getDefinition();
			return (prop.getName() != null) ? prop.getName() : prop.getId();
		}
		return "";
	}

	public String getPrfValue(Object element) {
		if (element instanceof SadProperty) {
			SadProperty property = (SadProperty) element;
			Object value = property.getPrfValue();
			return property.getLabelProvider().getText(value);
		}
		return "";
	}

	public String getSadValue(Object element) {
		if (element instanceof SadProperty) {
			SadProperty property = (SadProperty) element;
			Object value = property.getSadValue();
			return property.getLabelProvider().getText(value);
		}
		return "";
	}

}
