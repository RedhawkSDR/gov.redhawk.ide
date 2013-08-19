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

import gov.redhawk.bulkio.util.BulkIOType;
import gov.redhawk.ide.snapshot.datareceiver.BaseDataReceiver;
import gov.redhawk.ide.snapshot.datareceiver.CaptureMethod;
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import nxm.sys.lib.Data;
import nxm.sys.lib.DataFile;
import nxm.sys.lib.Midas;
import nxm.sys.lib.NeXtMidas;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import BULKIO.StreamSRI;

/**
 * 
 */
public class BlueDataReceiver extends BaseDataReceiver {

	private static final String[] FILE_EXTENSIONS = new String[] { ".tmp" };

	/** the Object used to write data to a Midas .BLUE file */
	private DataFile df;
	/** the format specification of a .BLUE file*/
	private Data dataFormat;
	/**the data type from the port*/

	/**Instance of Midas for creating new data files*/
	private Midas m = NeXtMidas.getGlobalInstance().getMidasContext();

	public BlueDataReceiver(BulkIOType type, File file, CaptureMethod method, long numberSamples, double durationTime) throws IOException {
		super(type, file, method, numberSamples, durationTime);

		this.df = new DataFile(m, file.getAbsolutePath());
		this.df.open(DataFile.OUTPUT);
		this.dataFormat = this.getDataType(type);
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiverAttributes#getReceiverName()
	 */
	@Override
	public String getReceiverName() {
		return "Midas BLUE file (.tmp)";
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiverAttributes#getReceiverFilenameExtensions()
	 */
	@Override
	public String[] getReceiverFilenameExtensions() {
		return FILE_EXTENSIONS;
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiverAttributes#newInstance(java.io.File, long, double, gov.redhawk.bulkio.util.BulkIOType, boolean, gov.redhawk.ide.snapshot.datareceiver.IDataReceiver.CaptureMethod)
	 */
	@Override
	public IDataReceiver newInstance(File file, long samples, double time, BulkIOType type, boolean upcastUnsigned, CaptureMethod method) throws IOException {
		return new BlueDataReceiver(type, file, method, samples, time);
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiver#dispose()
	 */
	@Override
	public void dispose() throws IOException {
		if (df != null) {
			df.close();
			df = null;
		}
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.IDataReceiver#writeFile(java.lang.Object[], BULKIO.StreamSRI)
	 */
	@Override
	public void writeFile(Object[] data, StreamSRI sri) throws IOException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.BaseDataReceiver#saveMetadata()
	 */
	@Override
	@Nullable
	protected String saveMetadata() throws IOException {
		return null; // none - metadata is stored in BLUE file
	}

	/* (non-Javadoc)
	 * @see gov.redhawk.ide.snapshot.datareceiver.BaseDataReceiver#startNewFile(java.lang.String, BULKIO.StreamSRI)
	 */
	@Override
	@NonNull
	protected String startNewFile(String nextFileName, StreamSRI sri) throws IOException {
		df = new DataFile(m, this.getNextFileName());
		df.open(DataFile.OUTPUT);

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

		return null;
	}

	/**
	 * This method maps a BulkIOType to a Data
	 * @param t : the BulkIOType to map to the DataTypes type
	 * @return the equivalent Data Type
	 */
	private Data getDataType(BulkIOType t) {
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
	public String[][] getOutputFiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doTaskBeforeAquireData(Date startTime) {
		// TODO Auto-generated method stub

	}

}
