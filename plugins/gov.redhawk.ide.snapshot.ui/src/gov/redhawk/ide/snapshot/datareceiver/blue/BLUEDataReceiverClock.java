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
import gov.redhawk.ide.snapshot.datareceiver.IDataReceiver;

import nxm.sys.lib.Convert;
import nxm.sys.lib.Data;
import nxm.sys.lib.Time;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.dataCharOperations;
import BULKIO.dataDoubleOperations;
import BULKIO.dataFloatOperations;
import BULKIO.dataLongLongOperations;
import BULKIO.dataLongOperations;
import BULKIO.dataShortOperations;



public class BLUEDataReceiverClock extends SuperBLUEReceiver implements dataDoubleOperations, 
		dataFloatOperations, dataLongLongOperations, dataLongOperations, dataShortOperations, 
		dataCharOperations, IDataReceiver {

	/**The total time to capture samples in clock time*/
	private double totalTime;
	/**The time samples started being captured*/
	private Time startTime;
	/**The current Time*/
	private Time currentTime;
    /**boolean for whether or not an end of stream has occurred*/
    private boolean eos = false;
 

    public BLUEDataReceiverClock(File file, double time, BulkIOType type, 
    		IDataReceiver.CaptureMethod method) throws IOException {
    	super(file, type);
        switch (method) {
	        case CLOCK_TIME:
	        	totalTime = time;
	        	break;
	        case INDEFINITELY:
	        	totalTime = Double.POSITIVE_INFINITY;
	        	break;
        	default:
        		throw new IllegalArgumentException("Unsupported Processing Type");
        }
    }
    

    @Override
    public void pushSRI(StreamSRI sri) {
    	if (Time.toTime(new Date()).diff(startTime) < this.totalTime && !this.eos) {
            super.pushSRI(sri);
        }
    }
    
    /**
     * This method maps a BulkIOType to a Data
     * @param t : the BulkIOType to map to the DataTypes type
     * @return the equivalent Data Type
     */
    @Override
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
    	default:
    		throw new IllegalArgumentException("The BulkIOType was not a recognized signed type");
    	}
    	return data;
    }

	@Override
    public void pushPacket(double[] data, PrecisionUTCTime time, boolean eos, String streamID) {
		if (Time.toTime(new Date()).diff(startTime) >= this.totalTime 
    			|| super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) data.length;
        try {
	        int byteBufferLen = super.getType().getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        // convert double[] to byte[]
	        Convert.ja2bb(data, Data.DOUBLE, byteBuffer, super.getDataFile().dataType, data.length); 
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
        } catch (Exception e) {
        	super.writeException(new IOException(e));
        }
        super.incrementSamples(super.deriveNumberOfSamples(length));
    }

    @Override
    public void pushPacket(char[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (Time.toTime(new Date()).diff(startTime) >= this.totalTime 
    			|| super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) data.length;
        /*ByteBuffer bBuffer = ByteBuffer.allocateDirect(type.getBytePerAtom() * length);
        CharBuffer tBuff = bBuffer.asCharBuffer();
        tBuff.put(data, 0, length);
        try {
            channel.write(bBuffer);
        } catch (IOException e) {
            writeException(e);
            return;
        }*/
        /*Data dataFile = df.getDataBuffer(data.length, Data.INT);
        dataFile.uncast((short[])data, true);
        df.write(dataFile);*/
        try {
	        int byteBufferLen = super.getType().getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        // convert int(char)[] to byte[]
	        Convert.ja2bb(data, Data.INT, byteBuffer, super.getDataFile().dataType, data.length); 
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
	    } catch (Exception e) {
	    	super.writeException(new IOException(e));
	    }
        super.incrementSamples(super.deriveNumberOfSamples(length));
    }

    @Override
    public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (Time.toTime(new Date()).diff(startTime) >= this.totalTime 
    			|| super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) data.length;
        try {
	        int byteBufferLen = super.getType().getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        // convert int(short)[] to byte[]
	        Convert.ja2bb(data, Data.INT, byteBuffer, super.getDataFile().dataType, data.length); 
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
	    } catch (Exception e) {
	    	super.writeException(new IOException(e));
	    }
        super.incrementSamples(super.deriveNumberOfSamples(length));
    }

    @Override
    public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (Time.toTime(new Date()).diff(startTime) >= this.totalTime 
    			|| super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) data.length;
    	try {
	        int byteBufferLen = super.getType().getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        // convert long(int)[] to byte[]
	        Convert.ja2bb(data, Data.LONG, byteBuffer, super.getDataFile().dataType, data.length); 
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
	    } catch (Exception e) {
	    	super.writeException(new IOException(e));
	    }
        super.incrementSamples(super.deriveNumberOfSamples(length));
    }

    @Override
    public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (Time.toTime(new Date()).diff(startTime) >= this.totalTime 
    			|| super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) data.length;
    	try {
	        int byteBufferLen = super.getType().getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        // convert XLong(long)[] to byte[]
	        Convert.ja2bb(data, Data.XLONG, byteBuffer, super.getDataFile().dataType, data.length);
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
	    } catch (Exception e) {
	    	super.writeException(new IOException(e));
	    }
        super.incrementSamples(super.deriveNumberOfSamples(length));
    }

    @Override
    public void pushPacket(float[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (Time.toTime(new Date()).diff(startTime) >= this.totalTime 
    			|| super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) data.length;
    	try {
	        int byteBufferLen = super.getType().getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        // convert float[] to byte[]
	        Convert.ja2bb(data, Data.FLOAT, byteBuffer, super.getDataFile().dataType, data.length); 
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
    	} catch (Exception e) {
	    	super.writeException(new IOException(e));
    	}
        super.incrementSamples(super.deriveNumberOfSamples(length));
    }

	@Override
    public void writeFile(Object[] data, StreamSRI sri) throws IOException {
    	this.pushSRI(sri);
    	String [] typeMismatch = {" does not correspond the port type ",
    			", it does correspond to the port type "};
    	//try {
    	if (data instanceof Double[]) {
			if (super.getType() == BulkIOType.DOUBLE) { 
				this.pushPacket(ArrayUtils.toPrimitive((Double []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Double array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.DOUBLE.name());
			}
		} else if (data instanceof Float[]) {
			if (super.getType() == BulkIOType.FLOAT) {
				this.pushPacket(ArrayUtils.toPrimitive((Float []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Float array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.FLOAT.name());
			}
		} else if (data instanceof Character[]) {
			if (super.getType() == BulkIOType.CHAR) {
				this.pushPacket(ArrayUtils.toPrimitive((Character []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Character array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.CHAR.name());
			}
		} else if (data instanceof Short[]) {
			if (super.getType() == BulkIOType.SHORT) {
				this.pushPacket(ArrayUtils.toPrimitive((Short []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Short array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.SHORT.name());
			}
		} else if (data instanceof Integer[]) {
			if (super.getType() == BulkIOType.LONG) {
				this.pushPacket(ArrayUtils.toPrimitive((Integer []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("An Integer array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.LONG.name());
			}
		} else if (data instanceof Long[]) {
			if (super.getType() == BulkIOType.LONG_LONG) {
				this.pushPacket(ArrayUtils.toPrimitive((Long []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Long array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.LONG_LONG.name());
			}
		} else {
			throw new IllegalArgumentException("Data super.getType() not supported by this receiver");
		}
    	/*} finally {
	    	try {
	            channel.truncate(super.getCurrentSamples() * super.getType() .getBytePerAtom());
	        } catch (IOException e) {
	            // PASS
	        }
    	}*/
    }

    public void processSamples(IProgressMonitor monitor) throws InterruptedException, 
    																	IOException {
    	startTime = Time.toTime(new Date());
    	int work;
        if (this.totalTime > Integer.MAX_VALUE) {
            work = IProgressMonitor.UNKNOWN;
        } else {
            work = (int) this.totalTime;
        }
        monitor.beginTask("Acquiring samples...", work);
        try {
	        Time lastIncrement = startTime;
	        this.currentTime = startTime;
	        while (this.currentTime.diff(startTime) < this.totalTime) {
	            if (super.getWriteException() != null) {
	                throw super.getWriteException();
	            }
	            double diff = this.currentTime.diff(lastIncrement);
	            if ((int) diff > 0) {
	                monitor.worked((int) diff);
	                lastIncrement = this.currentTime;
	            }
	            synchronized (this) {
	                wait(500);
	                if (monitor.isCanceled()) {
	                    //throw new CancellationException();
	                	this.eos = true;
	                	break;
	                }
	            }
	            if (this.eos) {
	            	break;
	            }
	            this.currentTime = Time.toTime(new Date());
	        }
		} finally {
            //TODO truncate if desired
            monitor.done();
        }
    }
  
	
}
