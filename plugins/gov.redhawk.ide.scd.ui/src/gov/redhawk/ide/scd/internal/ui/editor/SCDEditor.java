package gov.redhawk.ide.scd.internal.ui.editor;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.statushandlers.StatusManager;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import CF.PortSupplierHelper;
import gov.redhawk.ide.scd.ui.ScdUiPlugin;
import gov.redhawk.ide.scd.ui.editor.page.PortsFormPage;
import gov.redhawk.ide.scd.ui.provider.PortsEditorScdItemProviderAdapterFactory;
import gov.redhawk.ui.editor.SCAFormEditor;
import mil.jpeojtrs.sca.scd.ComponentType;
import mil.jpeojtrs.sca.scd.Interface;
import mil.jpeojtrs.sca.scd.Ports;
import mil.jpeojtrs.sca.scd.ScdFactory;
import mil.jpeojtrs.sca.scd.ScdPackage;
import mil.jpeojtrs.sca.scd.SoftwareComponent;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

/**
 * The editor for SCD files. Shows both a form page for editing ports as well as an XML editor page.
 */
public class SCDEditor extends SCAFormEditor {

	private PortsFormPage portsFormPage;
	private TextEditor textEditor;

	public SCDEditor() {
	}

	@Override
	protected void addPages() {
		try {
			addPortsFormPage();
			addXmlEditorPage();
		} catch (PartInitException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ScdUiPlugin.PLUGIN_ID, "Failed to add pages", e));
		}
	}

	/**
	 * Adds the port editing form page.
	 * @throws PartInitException
	 */
	private void addPortsFormPage() throws PartInitException {
		// If resource is a Service, only show the Ports page if PortSupplier is in the inheritance path
		boolean showPortsPage = true;
		SoftwareComponent scd = SoftwareComponent.Util.getSoftwareComponent(this.getMainResource());
		ComponentType componentType = SoftwareComponent.Util.getWellKnownComponentType(scd);
		if (componentType == ComponentType.SERVICE) {
			showPortsPage = false;
			Interface tmpInterface = ScdFactory.eINSTANCE.createInterface();
			tmpInterface.setRepid(PortSupplierHelper.id());
			for (Interface serviceInterface : scd.getInterfaces().getInterface()) {
				if (serviceInterface.isInstance(tmpInterface)) {
					showPortsPage = true;
					break;
				}
			}
		}
		if (!showPortsPage) {
			return;
		}

		this.portsFormPage = new PortsFormPage(this);
		addPage(this.portsFormPage);
		this.portsFormPage.setInput(getMainResource());
	}

	/**
	 * Adds the XML source page.
	 * @throws PartInitException
	 */
	private void addXmlEditorPage() throws PartInitException {
		this.textEditor = createTextEditor(getEditorInput());
		final int newPage = addPage(this.textEditor, getEditorInput(), getMainResource());
		this.setPageText(newPage, getEditorInput().getName());
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
		super.init(site, input);
		this.setPartName(getEditorInput().getName());
	}

	@Override
	public String getEditingDomainId() {
		return "gov.redhawk.scd.editingDomain";
	}

	@Override
	protected AdapterFactory getSpecificAdapterFactory() {
		return new PortsEditorScdItemProviderAdapterFactory();
	}

	@Override
	protected IContentOutlinePage createContentOutline() {
		return new SCDFormOutlinePage(this);
	}

	@Override
	public List< ? > getOutlineItems() {
		// Return just the 'ports' object
		SoftwareComponent scd = SoftwareComponent.Util.getSoftwareComponent(getMainResource());
		Ports ports = ScaEcoreUtils.getFeature(scd, ScdPackage.Literals.SOFTWARE_COMPONENT__COMPONENT_FEATURES, ScdPackage.Literals.COMPONENT_FEATURES__PORTS);
		return (ports == null) ? Collections.EMPTY_LIST : Collections.singletonList(ports);
	}

}
