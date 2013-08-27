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
package gov.redhawk.ide.snapshot.writer.internal;

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataFactory;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import BULKIO.StreamSRI;

/**
 * 
 */
public class BinXMLDataWriter extends BinDataWriter {

	/** string representation of the format to print the start and end times of files. */
	public static final String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS z";

	static final String SNAPSHOT_FILE_EXENSION = ".bin";
	private static final String METADATA_FILE_EXENSION = "xml";

	protected void saveMetaData() throws IOException {
		Model metaInfo = SnapshotMetadataFactory.eINSTANCE.createModel();
		int divisor = (getSRI().mode == 1) ? 2 : 1;
		metaInfo.setNumberOfSamples(getNumSamples() / divisor);
		metaInfo.setTime(SnapshotMetadataFactory.eINSTANCE.createTime());
		metaInfo.setStreamSRI(SnapshotMetadataFactory.eINSTANCE.createSRI());
		
		BulkIOType type = getSettings().getType();
		StreamSRI localSRI = getSRI();
		metaInfo.getTime().setStartTime(new SimpleDateFormat(TIME_FORMAT).format(new Date()));
		metaInfo.setBulkIOType(type.name());
		metaInfo.getStreamSRI().setHversion(localSRI.hversion);
		metaInfo.getStreamSRI().setXstart(localSRI.xstart);
		metaInfo.getStreamSRI().setXdelta((localSRI.xdelta == 0) ? 1.0 : localSRI.xdelta);
		metaInfo.getStreamSRI().setXunits(localSRI.xunits);
		metaInfo.getStreamSRI().setYstart(localSRI.ystart);
		metaInfo.getStreamSRI().setYdelta((localSRI.ydelta == 0) ? 1.0 : localSRI.ydelta);
		metaInfo.getStreamSRI().setYunits(localSRI.yunits);
		metaInfo.getStreamSRI().setSubsize(localSRI.subsize);
		metaInfo.getStreamSRI().setMode(localSRI.mode);
		metaInfo.getStreamSRI().setStreamID(localSRI.streamID);
		metaInfo.getStreamSRI().setBlocking(localSRI.blocking);
		metaInfo.getStreamSRI().setKeywords(SnapshotMetadataFactory.eINSTANCE.createKeywordsType());
		for (int i = 0; i < localSRI.keywords.length; i++) {
			CFDataType keyword = readCFDataType(SnapshotMetadataFactory.eINSTANCE.createCFDataType(), localSRI.keywords[i]);
			metaInfo.getStreamSRI().getKeywords().getCFDataType().add(keyword);
		}
		
		metaInfo.getTime().setEndTime(new SimpleDateFormat(TIME_FORMAT).format(new Date()));

		// save to XML file using EMF model
		File metadataFile = getMetaDataFile();

		Resource resource = new XMLResourceFactoryImpl().createResource(URI.createFileURI(metadataFile.getAbsolutePath()));
		resource.getContents().add(metaInfo);
		resource.save(null);
	}
	
	@Override
	protected String getMetaDataFileExtension() {
		return METADATA_FILE_EXENSION;
	}

	@Override
	public List<File> getOutputFileList() {
		return Arrays.asList(new File[] {
			getFileDestination(),
			getMetaDataFile()
		});
	}
}
