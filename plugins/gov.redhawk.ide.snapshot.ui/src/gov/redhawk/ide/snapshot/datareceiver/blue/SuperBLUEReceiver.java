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
package gov.redhawk.ide.snapshot.datareceiver.blue;

import gov.redhawk.bulkio.util.AbstractBulkIOPort;
import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.datareceiver.DataReceiverUtils;

import java.io.File;
import java.io.IOException;
//import java.util.concurrent.CancellationException;

//import nxm.sys.lib.Convert;
import nxm.sys.lib.Data;
import nxm.sys.lib.DataFile;
import nxm.sys.lib.Midas;
import nxm.sys.lib.NeXtMidas;

//import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;

public class SuperBLUEReceiver extends AbstractBulkIOPort {

	/** number of samples to save to file */
	//protected final long samples;
	private long currentSamples;
	/** the Object used to write data to a Midas .BLUE file */
	private DataFile df;
	/** the format specification of a .BLUE file*/
	private Data dataFormat;
	/**the data type from the port*/
	private BulkIOType type;
	private IOException writeException;

	/** the sri data for the file being currently modified*/
	private StreamSRI currentSri = null;
	/**Instance of Midas for creating new data files*/
	private Midas m = NeXtMidas.getGlobalInstance().getMidasContext();
	/**number of files made*/
	private int fileNumber = 1;
	/**the file the receiver starts writing to*/
	private File startFile;

	public SuperBLUEReceiver(File file, BulkIOType type) throws IOException {
		super(type);
		this.df = new DataFile(m, file.getAbsolutePath());
		this.df.open(DataFile.OUTPUT);
		this.startFile = file;
		this.type = type;
		this.dataFormat = this.getDataType(type);
	}

	/**
	 * This method maps a BulkIOType to a Data
	 * @param t : the BulkIOType to map to the DataTypes type
	 * @return the equivalent Data Type
	 */
	protected Data getDataType(BulkIOType t) {
		Data data = new Data();
		switch (t) {
		case CHAR:
			data.setFormatType('I');
			break;
		case DOUBLE:
			//return 'D';
			data.setFormatType('D');
			break;
		case FLOAT:
			//return 'F';
			data.setFormatType('F');
			break;
		case LONG:
			//return 'L';
			data.setFormatType('L');
			break;
		case LONG_LONG:
			//return 'X';
			data.setFormatType('X');
			break;
		case SHORT:
			//return 'I';
			data.setFormatType('I');
			break;
		case OCTET:
			//return 'I';
			data.setFormatType(Data.INT);
			break;
		case ULONG:
			//return 'X';
			data.setFormatType(Data.XLONG);
			break;
		case ULONG_LONG:
			//return 'X';
			data.setFormatType(Data.XLONG);
			break;
		case USHORT:
			//return 'L';
			data.setFormatType(Data.LONG);
			break;
		default:
			throw new IllegalArgumentException("The BulkIOType was not a recognized Type");
		}
		return data;
	}

	@Override
	public void pushSRI(StreamSRI sri) {
		if (writeException != null) {
			return;
		}
		try {
			super.pushSRI(sri);

			if (DataReceiverUtils.isSRIChanged(sri, this.currentSri)) {
				if (this.currentSri != null) {
					df.close();
					fileNumber++;
					df = new DataFile(m, this.getNextFileName());
					df.open(DataFile.OUTPUT);
				}
				dataFormat.setFormatMode((sri.mode == 0) ? 'S' : 'C');
				df.setFormat(dataFormat.getFormat());
				double xdelta = (sri.xdelta == 0) ? 1.0 : sri.xdelta; // delta should NOT be zero
				double ydelta = (sri.ydelta == 0) ? 1.0 : sri.ydelta; // delta should NOT be zero
				df.setXStart(sri.xstart);
				df.setXDelta(xdelta);
				df.setXUnits(sri.xunits);
				df.setYStart(sri.ystart);
				df.setYDelta(ydelta);
				df.setYUnits(sri.yunits);
				df.setFrameSize(sri.subsize);
				df.getKeywordsObject().put("hversion", sri.hversion + "");
				df.getKeywordsObject().put("streamID", sri.streamID + "");
				df.getKeywordsObject().put("blocking", sri.blocking + "");
				this.currentSri = sri;
			}
		} catch (Exception e) {
			this.writeException(new IOException(e));
		}

	}

	/**
	 * This method generates the file names created by the data receiver.
	 * The first row contains the absolute path to the parent directory.
	 * The subsequent rows contains the names of each set of files created.
	 * For example, outputFiles[1] = {"[filename].tmp"}
	 * @return the files written to by the data receiver
	 */
	public String[][] getOutputFiles() {
		if (fileNumber == 0) {
			return null;
		} else {
			String[][] files = new String[fileNumber + 1][1];
			files[0][0] = startFile.getParentFile().getAbsolutePath();
			files[1][0] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".tmp")) + ".tmp";
			for (int i = 2; i < files.length; i++) {
				files[i][0] = startFile.getName().substring(0, startFile.getName().lastIndexOf(".tmp")) + i + ".tmp";
			}
			return files;
		}
	}

	/**
	 * This method determines the next file name to use based on the base file name
	 * stored in startFile an the counter fileNumber
	 * @return a String of the path to the next file
	 */
	protected String getNextFileName() {
		String filePath;
		filePath = startFile.getAbsolutePath();
		filePath = filePath.substring(0, filePath.lastIndexOf(".")) + fileNumber + ".tmp";
		return filePath;

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
		if (df != null) {
			df.close();
			df = null;
		}
	}

	protected IOException getWriteException() {
		return this.writeException;
	}

	protected long getCurrentSamples() {
		return this.currentSamples;
	}

	protected BulkIOType getType() {
		return this.type;
	}

	protected DataFile getDataFile() {
		return this.df;
	}

	protected Data getDataFormat() {
		return this.dataFormat;
	}

	protected StreamSRI getSRI() {
		return currentSri;
	}

}
