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
package gov.redhawk.ide.debug.internal.ui;

import gov.redhawk.ide.debug.ui.LaunchUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.actions.LaunchAction;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.IWorkbenchContribution;
import org.eclipse.ui.services.IServiceLocator;

public class LaunchContributionItem extends CompoundContributionItem implements IWorkbenchContribution {

	private IServiceLocator locator;

	public LaunchContributionItem() {
	}

	public LaunchContributionItem(String id) {
		super(id);
	}

	@Override
	protected IContributionItem[] getContributionItems() {
		ISelectionService selectionService = (ISelectionService) locator.getService(ISelectionService.class);

		ISelection selection = selectionService.getSelection();
		List<IContributionItem> items = new ArrayList<IContributionItem>();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection ss = (IStructuredSelection) selection;
			Object element = ss.getFirstElement();
			if (element instanceof SoftPkg) {
				SoftPkg spd = (SoftPkg) element;
				for (Implementation impl : spd.getImplementation()) {
					ILaunchConfigurationWorkingCopy config;
					try {
						config = LaunchUtil.createLaunchConfiguration(impl);
						if (config != null) {
							config = config.copy(impl.getId());
							LaunchAction action = new LaunchAction(config, ILaunchManager.RUN_MODE);
							items.add(new ActionContributionItem(action));
						}
					} catch (CoreException e) {
						// PASS
					}
				}
			}
		}
		Collections.sort(items, new Comparator<IContributionItem>() {

			@Override
			public int compare(IContributionItem o1, IContributionItem o2) {
				String s1 = ((ActionContributionItem) o1).getAction().getText();
				String s2 = ((ActionContributionItem) o2).getAction().getText();
				return s1.compareTo(s2);
			}

		});
		return items.toArray(new IContributionItem[items.size()]);
	}

	public void initialize(IServiceLocator serviceLocator) {
		this.locator = serviceLocator;

	}

}
