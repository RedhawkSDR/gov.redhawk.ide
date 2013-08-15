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
package gov.redhawk.ide.snapshot.datareceiver.bin.sri;

import gov.redhawk.bulkio.util.AbstractBulkIOPort;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.bulkio.util.StreamSRIUtil;
import gov.redhawk.ide.snapshot.datareceiver.DataReceiverUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import nxm.sys.lib.Table;
import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public class SuperBinSriReceiver extends AbstractBulkIOPort {
	protected static final String GENERAL_HEADER = "General Information";
	protected static final String FIRST_PACKET_HEADER = "First Packet Time";
	protected static final String LAST_PACKET_HEADER = "Last Packet Time";
	protected static final String SRI_HEADER = "SRI";
	protected static final String KEYWORDS_HEADER = "SRI Keywords";

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
	/**boolean for whether or not an end of stream has occurred*/
	//protected boolean eos;

	private RandomAccessFile aFile;
	private File startFile;
	/** the sri of the current file */
	private StreamSRI currentSri = null;

	private Table sriTable;
	private PrecisionUTCTime lastPushPacket;

	public SuperBinSriReceiver(File file, BulkIOType type) throws IOException {
		super(type);
		aFile = new RandomAccessFile(file, "rw");
		startFile = file;
		channel = aFile.getChannel();
		this.type = type;
		sriTable = new Table();
		sriTable.addTable(GENERAL_HEADER);
		sriTable.addTable(FIRST_PACKET_HEADER);
		Table subTable = sriTable.getTable(GENERAL_HEADER);
		subTable.addIfNotPresent("BulkIOType", type.name());
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
			files[1][1] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + ".sri";
			for (int i = 2; i < files.length; i++) {
				files[i][0] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + i + ".bin";
				files[i][1] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".bin")) + i + ".sri";
			}
			return files;
		}
	}

	/**
	 * Saves the EMF model to an xml
	 * @throws IOException
	 */
	protected void saveSRI() throws IOException {
		if (this.lastPushPacket != null) {
			sriTable.addTable(LAST_PACKET_HEADER);
			Table lastPacket = sriTable.getTable(LAST_PACKET_HEADER);
			lastPacket.addIfNotPresent("tcmode", this.lastPushPacket.tcmode);
			lastPacket.addIfNotPresent("tfsec", this.lastPushPacket.tfsec);
			lastPacket.addIfNotPresent("twsec", this.lastPushPacket.twsec);
			lastPacket.addIfNotPresent("tcstatus", this.lastPushPacket.tcstatus);
			lastPacket.addIfNotPresent("toff", this.lastPushPacket.toff);
		} else {
			sriTable.remove(FIRST_PACKET_HEADER);
		}
		this.setEndTime();
		String filePath;
		filePath = startFile.getAbsolutePath();
		filePath = filePath.substring(0, filePath.lastIndexOf(".")) + ((fileNumber > 1) ? fileNumber : "") + ".sri";

		List<String> list = sriTable.toConfigFile();

		FileWriter sriIOFile = new FileWriter(filePath);
		for (int i = 0; i < list.size(); i++) {
			sriIOFile.append(list.get(i) + "\n");
		}
		sriIOFile.close();
	}

	protected void setTimestamp(PrecisionUTCTime time) {
		this.lastPushPacket = time;
		if (!sriTable.getTable("First Packet Time").containsKey("tcmode")) {
			Table firstPacket = sriTable.getTable("First Packet Time");
			firstPacket.addIfNotPresent("tcmode", time.tcmode);
			firstPacket.addIfNotPresent("tfsec", time.tfsec);
			firstPacket.addIfNotPresent("twsec", time.twsec);
			firstPacket.addIfNotPresent("tcstatus", time.tcstatus);
			firstPacket.addIfNotPresent("toff", time.toff);
		}
	}

	@Override
	public void pushSRI(StreamSRI sri) {
		if (writeException != null) {
			return;
		}
		try {
			super.pushSRI(sri);
			if (!StreamSRIUtil.equals(sri, this.currentSri)) {
				Table sriSubTable;
				if (this.currentSri != null) {
					try {
						this.saveSRI();
						this.dispose();
						fileNumber++;
						aFile = new RandomAccessFile(this.getNextFile(), "rw");
						sriTable.clear();
						sriTable.addTable(GENERAL_HEADER);
						sriSubTable = sriTable.getTable(GENERAL_HEADER);
						sriSubTable.addIfNotPresent("BulkIOType", type.name());
						sriTable.addTable(FIRST_PACKET_HEADER);
						this.lastPushPacket = null;
						this.setStartTime();
					} catch (IOException e) {
						this.writeException(e);
					}
					channel = aFile.getChannel();
				}
				sriTable.addTable(SRI_HEADER);
				sriSubTable = sriTable.getTable(SRI_HEADER);
				sriSubTable.addIfNotPresent("hversion", "" + sri.hversion);
				sriSubTable.addIfNotPresent("xunits", "" + sri.xunits);
				sriSubTable.addIfNotPresent("xdelta", "" + sri.xdelta);
				sriSubTable.addIfNotPresent("xstart", "" + sri.xstart);
				sriSubTable.addIfNotPresent("subsize", "" + sri.subsize);
				sriSubTable.addIfNotPresent("yunits", "" + sri.yunits);
				sriSubTable.addIfNotPresent("ydelta", "" + sri.ydelta);
				sriSubTable.addIfNotPresent("ystart", "" + sri.ystart);
				sriSubTable.addIfNotPresent("mode", "" + sri.mode);
				sriSubTable.addIfNotPresent("blocking", "" + sri.blocking);
				sriSubTable.addIfNotPresent("streamid", sri.streamID);
				sriTable.addTable(KEYWORDS_HEADER);
				sriSubTable = sriTable.getTable(KEYWORDS_HEADER);
				for (int i = 0; i < sri.keywords.length; i++) {
					String[] keyword = DataReceiverUtils.readCorbaAny(sri.keywords[i], null);
					sriSubTable.addIfNotPresent(keyword[0], keyword[1]);
				}
				currentSri = sri;
			}
		} catch (Exception e) {
			this.writeException(new IOException(e));
		}
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

	protected BulkIOType getType() {
		return type;
	}

	protected IOException getWriteException() {
		return writeException;
	}

	protected long getCurrentSamples() {
		return currentSamples;
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

	protected synchronized void setChannel(FileChannel channel) {
		this.channel = channel;
		notifyAll();
	}

	protected synchronized void setSRI(StreamSRI sri) {
		this.currentSri = sri;
		notifyAll();
	}

	protected synchronized void setPrintedSamples(long samples) {
		Table subTable = sriTable.getTable(GENERAL_HEADER);
		if (!subTable.addIfNotPresent("Number of Samples", samples)) {
			subTable.remove("Number of Samples");
			subTable.addIfNotPresent("Number of Samples", samples);
		}
	}

	protected synchronized void setStartTime() {
		String time = new SimpleDateFormat(this.timeFormat).format(new Date());
		Table subTable = sriTable.getTable(GENERAL_HEADER);
		if (!subTable.addIfNotPresent("Start Time", time)) {
			subTable.remove("Start Time");
			subTable.addIfNotPresent("Start Time", time);
		}
	}

	protected synchronized void setEndTime() {
		String time = new SimpleDateFormat(this.timeFormat).format(new Date());
		Table subTable = sriTable.getTable(GENERAL_HEADER);
		if (!subTable.addIfNotPresent("End Time", time)) {
			subTable.remove("End Time");
			subTable.addIfNotPresent("End Time", time);
		}
	}
}
