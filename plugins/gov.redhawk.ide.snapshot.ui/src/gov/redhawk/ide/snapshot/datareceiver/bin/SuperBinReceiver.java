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
package gov.redhawk.ide.snapshot.datareceiver.bin;

import gov.redhawk.bulkio.util.AbstractBulkIOPort;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.StreamSRIUtil;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.CFDataType;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Model;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataFactory;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.SnapshotMetadataPackage;
import gov.redhawk.ide.snapshot.internal.ui.SnapshotMetaData.Value;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.concurrent.CancellationException;

import mil.jpeojtrs.sca.util.AnyUtils;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import BULKIO.StreamSRI;
import CF.DataType;
import CF.DataTypeHelper;

public class SuperBinReceiver extends AbstractBulkIOPort {
	private FileChannel channel;
	/**the string representation of the format to print the start and end times of files*/
	private final String timeFormat = "yyyy-MM-dd HH:mm:ss:SSS z";
	/**the number of samples that have been captured so far*/
	private long currentSamples;
	/**the BulkIO data type pushed by the port to capture from*/
	private BulkIOType type;
	/**the Number of file sets that have been created*/
	private int fileNumber = 1;
	private IOException writeException;
	/** EMF model for the metadata for bin snapshots*/
	private Model metaInfo;
	/**boolean for whether or not an end of stream has occurred*/
	//protected boolean eos;

	private RandomAccessFile aFile;
	private File startFile;
	/** the sri of the current file */
	private StreamSRI currentSri = null;
	/** the number of files that have been saved*/
	private SnapshotMetadataFactory modelFactory;

	public SuperBinReceiver(File file, BulkIOType type) throws IOException {
		super(type);
		aFile = new RandomAccessFile(file, "rw");
		startFile = file;
		channel = aFile.getChannel();
		this.type = type;
		SnapshotMetadataPackage.eINSTANCE.eClass();
		modelFactory = SnapshotMetadataFactory.eINSTANCE;
		metaInfo = modelFactory.createModel();
		metaInfo.setNumberOfSamples(0);
		metaInfo.setTime(modelFactory.createTime());
		metaInfo.setStreamSRI(modelFactory.createSRI());

	}

	/**
	 * This method generates the file names created by the data receiver.
	 * The first row contains the absolute path to the parent directory.
	 * The subsequent rows contains the names of each set of files created.
	 * For example, outputFiles[1] = {"[filename].bin", "[filename].xml"}
	 * @return the files written to by the data receiver
	 */
	public String[][] getOutputFiles() {
		if (fileNumber == 0) {
			return null;
		} else {
			String[][] files = new String[fileNumber + 1][2];
			files[0][0] = startFile.getParentFile().getAbsolutePath();
			files[0][1] = startFile.getParentFile().getAbsolutePath();
			files[1][0] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + ".bin";
			files[1][1] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + ".xml";
			for (int i = 2; i < files.length; i++) {
				files[i][0] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + i + ".bin";
				files[i][1] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + i + ".xml";
			}
			return files;
		}
	}

	/**
	 * Saves the EMF model to an xml
	 * @throws IOException
	 */
	protected void saveXML() throws IOException {
		String filePath;
		filePath = startFile.getAbsolutePath();
		filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ((fileNumber > 1) ? fileNumber : "") + ".xml";

		Resource resource = new XMLResourceFactoryImpl().createResource(URI.createFileURI(filePath));
		resource.getContents().add(metaInfo);
		resource.save(null);
	}

	@Override
	public void pushSRI(StreamSRI sri) {
		if (writeException != null) {
			return;
		}
		try {
			super.pushSRI(sri);
			if (!StreamSRIUtil.equals(sri, this.currentSri)) {
				if (this.currentSri != null) {
					try {
						this.saveXML();
						this.dispose();
						fileNumber++;
						aFile = new RandomAccessFile(this.getNextFile(), "rw");
					} catch (IOException e) {
						this.writeException(e);
					}
					channel = aFile.getChannel();
					metaInfo.getTime().setStartTime(new SimpleDateFormat(this.timeFormat).format(new Date()));
				}
				metaInfo.setBulkIOType(type.name());
				metaInfo.getStreamSRI().setHversion(sri.hversion);
				metaInfo.getStreamSRI().setXstart(sri.xstart);
				metaInfo.getStreamSRI().setXdelta((sri.xdelta == 0) ? 1.0 : sri.xdelta);
				metaInfo.getStreamSRI().setXunits(sri.xunits);
				metaInfo.getStreamSRI().setYstart(sri.ystart);
				metaInfo.getStreamSRI().setYdelta((sri.ydelta == 0) ? 1.0 : sri.ydelta);
				metaInfo.getStreamSRI().setYunits(sri.yunits);
				metaInfo.getStreamSRI().setSubsize(sri.subsize);
				metaInfo.getStreamSRI().setMode(sri.mode);
				metaInfo.getStreamSRI().setStreamID(sri.streamID);
				metaInfo.getStreamSRI().setBlocking(sri.blocking);
				metaInfo.getStreamSRI().setKeywords(modelFactory.createKeywordsType());
				for (int i = 0; i < sri.keywords.length; i++) {
					CFDataType keyword = readCFDataType(modelFactory.createCFDataType(), sri.keywords[i]);
					metaInfo.getStreamSRI().getKeywords().getCFDataType().add(keyword);
				}
				currentSri = sri;
			}
		} catch (Exception e) {
			this.writeException(new IOException(e));
		}
	}

	/**
	 * This method collects the information in the CF.DataType and stores it 
	 * in the EMF model CFDataType
	 * @param cfType : the EMF model for the CF.DataType
	 * @param data	:a CF.DataType to be processed
	 * @return the EMF model containing the data from the CF.DataType
	 */
	protected CFDataType readCFDataType(CFDataType cfType, DataType data) {
		Object temp;
		if (DataTypeHelper.type().equivalent(data.value.type())) {
			temp = DataTypeHelper.extract(data.value);
		} else {
			temp = AnyUtils.convertAny(data.value);
		}
		cfType.setId(data.id);
		if (temp instanceof DataType) {
			cfType.setValue(readCFDataType(modelFactory.createCFDataType(), (DataType) temp));
		} else {
			Value value = modelFactory.createValue();
			value.setValue(temp.toString());
			//value.setJavaType(DataReceiverUtils.getAnyTypeDataType(temp));
			value.setJavaType(temp.getClass().getName());
			cfType.setValue(value);
		}
		return cfType;
	}

	/**
	 * This method determines the next file name to use based on the base file name
	 * stored in startFile an the counter fileNumber
	 * @return a File of the next File to save to
	 */
	protected File getNextFile() {
		String filePath;
		filePath = startFile.getAbsolutePath();
		filePath = filePath.substring(0, filePath.lastIndexOf(".")) + fileNumber + ".bin";
		return new File(filePath);
	}

	protected synchronized void writeException(IOException e) {
		this.writeException = e;
		notifyAll();
	}

	protected long deriveNumberOfSamples(long value) {
		int divideBy = 1;
		if (this.currentSri.subsize > 0 && this.currentSri.mode == 1) {
			divideBy = 4;
		} else if (this.currentSri.subsize > 0 || this.currentSri.mode == 1) {
			divideBy = 2;
		}
		return (long) (value / divideBy);
	}

	protected synchronized void incrementSamples(long value) {
		currentSamples += value;
		notifyAll();
	}

	public void dispose() throws IOException {
		if (channel != null) {
			channel.close();
			channel = null;
		}
		if (aFile != null) {
			aFile.close();
			aFile = null;
		}

	}

	protected FileChannel getChannel() {
		return channel;
	}

	protected String getTimeFormat() {
		return timeFormat;
	}

	protected BulkIOType getType() {
		return type;
	}

	protected IOException getWriteException() {
		return writeException;
	}

	protected long getCurrentSamples() {
		return currentSamples;
	}

	protected Model getMetaDataModel() {
		return metaInfo;
	}

	protected StreamSRI getSRI() {
		return this.currentSri;
	}

	protected int getFileNumber() {
		return this.fileNumber;
	}

	protected synchronized void incrementFileNumber(int value) {
		this.fileNumber += value;
		notifyAll();
	}

	protected synchronized void setRandomAccessFile(RandomAccessFile aFile) {
		this.aFile = aFile;
		notifyAll();
	}

	protected synchronized void setMetaDataModel(Model metadata) {
		this.metaInfo = metadata;
		notifyAll();
	}

	protected synchronized void setChannel(FileChannel channel) {
		this.channel = channel;
		notifyAll();
	}

	protected synchronized void setSRI(StreamSRI sri) {
		this.currentSri = sri;
		notifyAll();
	}
}
