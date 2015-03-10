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
package gov.redhawk.ide.debug.internal.variables;

import gov.redhawk.ide.debug.ILauncherVariableDesc;
import gov.redhawk.ide.debug.ScaDebugPlugin;
import gov.redhawk.ide.debug.variables.AbstractLauncherResolver;
import gov.redhawk.ide.debug.variables.LaunchVariables;
import gov.redhawk.sca.util.ORBUtil;
import gov.redhawk.sca.util.OrbSession;
import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SoftPkg;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.InvalidName;
import org.omg.CosNaming.NamingContextPackage.NotFound;

/**
 * Provides a unique name binding within a naming context for a {@link SoftPkg} being launched.
 */
public class NameBindingResolver extends AbstractLauncherResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String resolveValue(String arg, final ILaunch launch, final ILaunchConfiguration config, final SoftPkg spd, final Implementation impl)
		throws CoreException {
		final ILauncherVariableDesc desc = ScaDebugPlugin.getInstance().getLauncherVariableRegistry().getDesc(LaunchVariables.NAMING_CONTEXT_IOR);
		return getUniqueName(spd, desc.resolveValue(null, spd, launch, config));
	}

	private String getUniqueName(final SoftPkg spd, final String namingContextIOR) {
		OrbSession session = OrbSession.createSession();
		NamingContextExt namingContext = null;
		try {
			namingContext = NamingContextExtHelper.narrow(session.getOrb().string_to_object(namingContextIOR));
			String tmpName = spd.getName();
			int lastDot = tmpName.lastIndexOf('.');
			if (lastDot > -1) {
				tmpName = tmpName.substring(lastDot + 1);
			}
			final String name = tmpName;
			String retVal = name;
			for (int i = 1; true; i++) {
				org.omg.CORBA.Object ref = null;
				try {
					ref = namingContext.resolve_str(retVal);
					retVal = name + "_" + i;
				} catch (final NotFound e) {
					return retVal;
				} catch (final CannotProceed e) {
					throw new IllegalStateException(e);
				} catch (final InvalidName e) {
					throw new IllegalStateException(e);
				} finally {
					if (ref != null) {
						ORBUtil.release(ref);
						ref = null;
					}
				}
			}
		} finally {
			if (namingContext != null) {
				ORBUtil.release(namingContext);
				namingContext = null;
			}
			session.dispose();
		}
	}

}
