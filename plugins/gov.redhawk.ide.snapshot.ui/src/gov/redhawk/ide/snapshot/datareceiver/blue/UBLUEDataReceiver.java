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
import mil.jpeojtrs.sca.util.UnsignedUtils;

import nxm.sys.lib.Convert;
import nxm.sys.lib.Data;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.ArrayUtils;
import org.eclipse.core.runtime.IProgressMonitor;

import BULKIO.PrecisionUTCTime;
import BULKIO.StreamSRI;
import BULKIO.dataOctetOperations;
import BULKIO.dataUlongLongOperations;
import BULKIO.dataUlongOperations;
import BULKIO.dataUshortOperations;
//import CF.DataType;

public class UBLUEDataReceiver extends SuperBLUEReceiver implements dataOctetOperations, 
				dataUlongLongOperations, dataUlongOperations, dataUshortOperations, IDataReceiver {

	/**The total number of samples to capture*/
	private long totalNumSamples;
	/**the total time for capturing samples*/
	private final double totalTime;
	/**the time that has elapsed in sample time*/
	private double currentTimeDuration;
	/**the number of samples that have been captured so far*/
	private double currentSampleDelta;
	/**the Selected capture method, only supports NUMBER and SAMPLE_TIME*/
	private IDataReceiver.CaptureMethod captureMethod;
    /**boolean for whether or not an end of stream has occurred*/
    private boolean eos = false;

    public UBLUEDataReceiver(File file, long numSamples, double sampleDurationTime,
    		BulkIOType type, IDataReceiver.CaptureMethod method) throws IOException {
    	super(file, type);
    	this.currentSampleDelta = 1;
        switch (method) {
	        case NUMBER:
	        	this.totalNumSamples = numSamples;
	            this.totalTime = numSamples * this.currentSampleDelta;
	            break;
	        case SAMPLE_TIME:
	        	this.totalTime = sampleDurationTime;
	        	this.totalNumSamples = (long) (this.totalTime / this.currentSampleDelta + .5);
	        	break;
        	default:
        		throw new IllegalArgumentException("Unsupported Capture Type");
        }
        this.currentTimeDuration = 0;
        this.captureMethod = method;
    }
    
    @Override
    public void pushSRI(StreamSRI sri) {
    	if (totalNumSamples > super.getCurrentSamples()  && !this.eos) {
    		if (this.captureMethod == CaptureMethod.SAMPLE_TIME) {
        		this.currentSampleDelta = (sri.xdelta != 0) ? sri.xdelta : 1;
        		totalNumSamples = ((long) (((this.totalTime - this.currentTimeDuration) 
        				/ currentSampleDelta) + .5)) + super.getCurrentSamples();
        	}
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
    		throw new IllegalArgumentException("The BulkIOType was not a recognized unsigned Type");
    	}
    	return data;
    }
    

    //@Override
    //public void pushSRI(StreamSRI sri) {
	    // TODO Auto-generated method stub
	    //super.pushSRI(sri);
	    //metaInfo.setSRI(sri);
//	    if (!corbareceiver.isSRIChanged(sri, this.currentSri)) {
	    //String xmlFile = 
//	    df.setFormat(dataFormat);
//	    DOUBLE XDELTA = (SRI.XDELTA == 0) ? 1.0 : SRI.XDELTA; // DELTA SHOULD NOT BE ZERO
//	    DOUBLE YDELTA = (SRI.YDELTA == 0) ? 1.0 : SRI.YDELTA; // DELTA SHOULD NOT BE ZERO
//	    .SETXSTART(SRI.XSTART);
//	    DF.SETXDELTA(XDELTA); 
//	    DF.SETXUNITS(SRI.XUNITS);
//	    DF.SETYSTART(SRI.YSTART);
//	    DF.SETYDELTA(YDELTA);
//	    DF.SETYUNITS(SRI.YUNITS);
// 
    //}

    @Override
    public void pushPacket(byte[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (this.totalNumSamples <= super.getCurrentSamples()  || super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
        try {
	        short[] signedData = UnsignedUtils.toSigned(data);
	        int byteBufferLen = BulkIOType.SHORT.getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        Convert.ja2bb(signedData, super.getDataFormat().getFormatType(), byteBuffer, 
	        		super.getDataFile().dataType, data.length); // convert byte(octet)[] to byte[]
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
        } catch (Exception e) {
         	super.writeException(new IOException(e));
        }
        super.incrementSamples(super.deriveNumberOfSamples(length));
        this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
    }

    
    @Override
    public void pushPacket(short[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (this.totalNumSamples <= super.getCurrentSamples()  || super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
        try {
	        int[] signedData = UnsignedUtils.toSigned(data);
	        int byteBufferLen = BulkIOType.LONG.getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        Convert.ja2bb(signedData, super.getDataFormat().getFormatType(), byteBuffer, 
	        		super.getDataFile().dataType, data.length); // convert byte(octet)[] to byte[]
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
        } catch (Exception e) {
         	super.writeException(new IOException(e));
        }
        super.incrementSamples(super.deriveNumberOfSamples(length));
        this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
    }

    @Override
    public void pushPacket(int[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (this.totalNumSamples <= super.getCurrentSamples()  || super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
        try {
	    	long[] signedData = UnsignedUtils.toSigned(data);
	        int byteBufferLen = BulkIOType.LONG_LONG.getBytePerAtom() * length; 
	        byte[] byteBuffer = new byte[byteBufferLen];
	        Convert.ja2bb(signedData, super.getDataFormat().getFormatType(), byteBuffer, 
	        		super.getDataFile().dataType, data.length); // convert byte(octet)[] to byte[]
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
        } catch (Exception e) {
         	super.writeException(new IOException(e));
        }
        super.incrementSamples(super.deriveNumberOfSamples(length));
        this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
    }

    @Override
    public void pushPacket(long[] data, PrecisionUTCTime time, boolean eos, String streamID) {
    	if (this.totalNumSamples <= super.getCurrentSamples()  || super.getWriteException() != null || this.eos) {
            return;
        }
        if (eos) {
        	this.eos = true;
        }
        int length = (int) Math.min(data.length, this.totalNumSamples - super.getCurrentSamples());
        try {
    		long[] signedData = new long[data.length];
			for (int i = 0; i < signedData.length; i++) {
				if (data[i] < 0) {
					signedData[i] = Long.MAX_VALUE;
				} else {
					signedData[i] = data[i];
				}
			}
		    int byteBufferLen = BulkIOType.LONG_LONG.getBytePerAtom() * length; 
		    byte[] byteBuffer = new byte[byteBufferLen];
		    Convert.ja2bb(signedData, super.getDataFormat().getFormatType(), byteBuffer, 
		    		super.getDataFile().dataType, data.length); // convert byte(octet)[] to byte[]
	        super.getDataFile().write(byteBuffer, 0, byteBuffer.length);
        } catch (Exception e) {
         	super.writeException(new IOException(e));
        }
        super.incrementSamples(super.deriveNumberOfSamples(length));
        this.incrementTime(super.deriveNumberOfSamples(length) * this.currentSampleDelta);
    }
    

    // increament increment asdfa
    private synchronized void incrementTime(double time) {
    	this.currentTimeDuration += time;
    }
    
    public void processSamples(IProgressMonitor monitor) 
    		throws InterruptedException, IOException {
    	int work;
        if (captureMethod == CaptureMethod.NUMBER) {
	        if (totalNumSamples > Integer.MAX_VALUE) {
	            work = IProgressMonitor.UNKNOWN;
	        } else {
	            work = (int) totalNumSamples;
	        }
        } else {
        	//work = IProgressMonitor.UNKNOWN;
        	if (this.totalTime > Integer.MAX_VALUE) {
        		work = IProgressMonitor.UNKNOWN;
        	} else {
        		work = (int) (this.totalTime + .5);
        	}
        }
        monitor.beginTask("Acquiring samples...", work);
        try {
	        long lastWorked = super.getCurrentSamples();
	        double lastTime = this.currentTimeDuration;
	        double deltaProgress;
	        if (captureMethod == CaptureMethod.NUMBER) {
	        	deltaProgress = super.getCurrentSamples();
	        } else {
	        	deltaProgress = this.currentTimeDuration;
	        }
	        double workedProgress = 0;
	        while (totalNumSamples > super.getCurrentSamples()) {
	            if (super.getWriteException() != null) {
	                throw super.getWriteException();
	            }
	            workedProgress += deltaProgress;
	            int worked = (int) workedProgress; 
	            if (worked > 0) {
	                monitor.worked(worked);
	                workedProgress -= worked;
	            }
	            synchronized (this) {
	                wait(500);
	                if (monitor.isCanceled()) {
	                    //throw new CancellationException();
	                	this.eos = true;
	                	break;
	                }
	                if (captureMethod == CaptureMethod.NUMBER) {
	                	deltaProgress = super.getCurrentSamples()  - lastWorked;
	                    lastWorked = super.getCurrentSamples();
	                } else {
	                	deltaProgress = this.currentTimeDuration - lastTime;
	                	lastTime = this.currentTimeDuration;
	                }
	                
	            }
	            if (this.eos) {
	            	break;
	            }
	        }
        } finally {
            //TODO truncate if desired
            monitor.done();
        }
    }
    
	@Override
    public void writeFile(Object[] data, StreamSRI sri) throws IOException {
		this.pushSRI(sri);
    	String [] typeMismatch = {" does not correspond the port type ",
		", it does correspond to the port type "};
    	//try {
    	 if (data instanceof Byte []) {
				if (super.getType() == BulkIOType.OCTET) {
					this.pushPacket(ArrayUtils.toPrimitive((Byte []) data), 
							new PrecisionUTCTime(), false, sri.streamID);
				} else {
					throw new IllegalArgumentException("A Byte array" + typeMismatch[0]
							+ super.getType() +  typeMismatch[1] + BulkIOType.OCTET.name());
				}
		} else if (data instanceof Short[]) {
			if (super.getType() == BulkIOType.SHORT) {
				this.pushPacket(ArrayUtils.toPrimitive((Short []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Short array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.USHORT.name());
			}
		} else if (data instanceof Integer[]) {
			if (super.getType() == BulkIOType.LONG) {
				this.pushPacket(ArrayUtils.toPrimitive((Integer []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("An Integer array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.ULONG.name());
			}
		} else if (data instanceof Long[]) {
			if (super.getType() == BulkIOType.LONG_LONG) {
				this.pushPacket(ArrayUtils.toPrimitive((Long []) data), 
						new PrecisionUTCTime(), false, sri.streamID);
			} else {
				throw new IllegalArgumentException("A Long array" + typeMismatch[0]
						+ super.getType() +  typeMismatch[1] + BulkIOType.ULONG_LONG.name());
			}
		} else {
			throw new IllegalArgumentException("Data type not supported by this receiver");
		}
    	/*} finally {
	    	try {
	            channel.truncate(super.getCurrentSamples()  * type.getBytePerAtom());
	        } catch (IOException e) {
	            // PASS
	        }
    	}*/
	    
    }

}
