/**
 * This file is protected by Copyright.
 * Please refer to the COPYRIGHT file distributed with this source distribution.
 *
 * This file is part of REDHAWK IDE.
 *
 * All rights reserved.  This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html.
 */
package gov.redhawk.ide.snapshot.internal.writer;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.StreamXMLSRIUtil;
import gov.redhawk.bulkio.util.StreamSRIMetaData.StreamSRIDocumentRoot;
import gov.redhawk.bulkio.util.StreamSRIMetaData.StreamSRIMetaDataFactory;
import gov.redhawk.bulkio.util.StreamSRIMetaData.StreamSRIMetaDataPackage;
import gov.redhawk.bulkio.util.StreamSRIMetaData.StreamSRIModel;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

public class BinXMLDataWriter extends BinDataWriter {

	/** string representation of the format to print the start and end times of files. */
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS z";

	static final String SNAPSHOT_FILE_EXENSION = ".bin";
	private static final String METADATA_FILE_EXENSION = "xml";

	@Override
	protected void saveMetaData() throws IOException {
		StreamSRIModel metaInfo = StreamSRIMetaDataFactory.eINSTANCE.createStreamSRIModel();
		int divisor = (getSRI().mode == 1) ? 2 : 1;
		metaInfo.setNumberOfSamples(getNumSamples() / divisor);
		metaInfo.setDataByteOrder("" + getByteOrder());
		metaInfo.setTime(StreamSRIMetaDataFactory.eINSTANCE.createTime());
		metaInfo.getTime().setStartTime(new SimpleDateFormat(TIME_FORMAT).format(new Date()));
		StreamXMLSRIUtil.setStreamSRI(getSRI(), metaInfo);
		BulkIOType type = getSettings().getType();
		metaInfo.setBulkIOType(type.name());
		metaInfo.getTime().setEndTime(new SimpleDateFormat(TIME_FORMAT).format(new Date()));

		// save to XML file using EMF model
		File metadataFile = getMetaDataFile();

		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(URI.createFileURI(metadataFile.getAbsolutePath()), StreamSRIMetaDataPackage.eCONTENT_TYPE);
		StreamSRIDocumentRoot root = StreamSRIMetaDataFactory.eINSTANCE.createStreamSRIDocumentRoot();
		root.setSri(metaInfo);
		resource.getContents().add(root);
		resource.save(null);
	}

	@Override
	protected String getMetaDataFileExtension() {
		return METADATA_FILE_EXENSION;
	}

	@Override
	public List<File> getOutputFileList() {
		return Arrays.asList(new File[] { getFileDestination(), getMetaDataFile() });
	}
}
