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
package gov.redhawk.ide.sdr.internal.util;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import mil.jpeojtrs.sca.spd.Implementation;
import mil.jpeojtrs.sca.spd.SpdPackage;
import mil.jpeojtrs.sca.util.ScaEcoreUtils;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.emf.common.util.URI;

public class PythonEnvMap extends AbstractEnvMap {
	

	@Override
	public boolean handles(Implementation impl) {
		String language = getImplProgrammingLanguage(impl);
		return "python".equalsIgnoreCase(language);
	}

	@Override
	public void initEnv(Implementation impl, Map<String, String> envMap) throws CoreException {
		envMap.put("PYTHONPATH", getPythonPath(impl));
	}

	public String getPythonPath(Implementation impl) throws CoreException {
		// Python lib is always in the "lib" sub directory
		final Set<String> pythonPath = new LinkedHashSet<String>();
		pythonPath.add("${env_var:PYTHONPATH}");
		pythonPath.add("${OssieHome}/lib/python");
		if (impl != null) {
			addToPath(pythonPath, impl);
		}

		StringBuilder pythonPathString = new StringBuilder();
		Iterator<String> i = pythonPath.iterator();
		String s = i.next();
		pythonPathString.append(s);
		// Insert in reverse order
		for (; i.hasNext();) {
			s = i.next();
			pythonPathString.insert(0, s + File.pathSeparatorChar);
		}
		return pythonPathString.toString();
	}

	@Override
	protected boolean addToPath(Set<String> path, Implementation impl) throws CoreException {
		boolean retVal = super.addToPath(path, impl);

		if (retVal) {
			String relativeCodePath = ScaEcoreUtils.getFeature(impl, SpdPackage.Literals.IMPLEMENTATION__CODE, SpdPackage.Literals.CODE__LOCAL_FILE,
			        SpdPackage.Literals.LOCAL_FILE__NAME);
			String[] dir = URI.createFileURI(relativeCodePath).segments();
			if (impl.eResource() != null && impl.eResource().getURI() != null) {
				URI pathUri = impl.eResource().getURI().trimSegments(1).appendSegments(dir);
				path.add(getAbsolutePath(pathUri));
			}
		}
		
		return retVal;
	}

}
