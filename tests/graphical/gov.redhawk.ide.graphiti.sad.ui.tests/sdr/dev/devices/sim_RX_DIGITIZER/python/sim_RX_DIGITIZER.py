#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: sim_RX_DIGITIZER.spd.xml
from ossie.device import start_device
from ossie.properties import struct_to_props
import sys, math, copy, logging

from sim_RX_DIGITIZER_base import *

from port_impl_customized import OutShortPort_customized



############################
##        #DEFINES        ##
############################
TUNER_BUFFER_SIZE_BYTES=2048000


############################
##  STRUCTURE DEFINITION  ##
############################

''' Device Individual Tuner. This structure contains stream specific data for channel/tuner to include:
 *         - Data buffers
 *         - Additional stream metadata (sri & timestamps)
 *         - Control information (allocation id's)
 *         - Reference to associated frontend_tuner_status property where additional information is held. Note: frontend_tuner_status structure is required by frontend interfaces v2.0
'''

class dev_indivTuner:
    tuner_channel = None
    outputBuffer = []
    outputBufferTime = None # BULKIO::PrecisionUTCTime
    timeUp = None           # BULKIO::PrecisionUTCTime
    timeDown = None         # BULKIO::PrecisionUTCTime
    
    def __init__(self):
        #self.tuner_channel = frontend.indivTuner()
        pass

    def reset(self):
        if self.tuner_channel != None:
            self.tuner_channel.reset()
        self.outputBuffer = [] # range(TUNER_BUFFER_SIZE_BYTES / 2 # short = 2 bytes
        self.outputBufferTime = bulkio.timestamp.create(0, 0)
        self.timeUp = bulkio.timestamp.create(0, 0)
        self.timeDown = bulkio.timestamp.create(0, 0)
        #BIO_HELPER::zeroTime(&outputBufferTime)
        #BIO_HELPER::zeroTime(&timeUp)
        #BIO_HELPER::zeroTime(&timeDown)
        

class sim_RX_DIGITIZER_i(sim_RX_DIGITIZER_base):
    """<DESCRIPTION GOES HERE>"""
    
    # global properties for all channels
    rf_flow_id = None
    group_id = None

    # needed for RX capability in addition to frontend::indivTuner
    devChannels = None #std::vector<dev_indivTuner> 

    # Ensures configure() and serviceFunction() are thread safe
    propLock = None
    
    def __init__(self):
        self.devChannels = []
        self.propLock = threading.Lock()
        #self.rf_flow_id = None
        #self.group_id = None
        self._constructor_()
    
    def initialize(self):
        """
        This is called by the framework immediately after your device registers with the NameService.
        
        In general, you should add customization here and not in the __init__ constructor.  If you have 
        a custom port implementation you can override the specific implementation here with a statement
        similar to the following:
          self.some_port = MyPortImplementation()
        """
        sim_RX_DIGITIZER_base.initialize(self)
        
        ## As of the REDHAWK 1.8.3 release, device are not started automatically by the node. Therefore
        ## the device must start itself. 
        self.init_tuner()
        self.start()
    
    # custom construction w/ custom ports, callbacks, etc.
    def _constructor_(self):
    
        # Create new port instances
        self.port_dataShort_out = OutShortPort_customized("dataShort_out", self)
    
        # add callbacks
        self.port_DigitalTuner_in.setTunerTypeGetterCB(self.fe_getTunerType)
        self.port_DigitalTuner_in.setTunerDeviceControlGetterCB(self.fe_getTunerDeviceControl)
        self.port_DigitalTuner_in.setTunerGroupIdGetterCB(self.fe_getTunerGroupId)
        self.port_DigitalTuner_in.setTunerRfFlowIdGetterCB(self.fe_getTunerRfFlowId)
        self.port_DigitalTuner_in.setTunerStatusGetterCB(self.fe_getTunerStatus)
        self.port_DigitalTuner_in.setTunerCenterFrequencyGetterCB(self.fe_getTunerCenterFrequency)
        self.port_DigitalTuner_in.setTunerCenterFrequencySetterCB(self.fe_setTunerCenterFrequency)
        self.port_DigitalTuner_in.setTunerBandwidthGetterCB(self.fe_getTunerBandwidth)
        self.port_DigitalTuner_in.setTunerBandwidthSetterCB(self.fe_setTunerBandwidth)
        self.port_DigitalTuner_in.setTunerAgcEnableGetterCB(self.fe_getTunerAgcEnable)
        self.port_DigitalTuner_in.setTunerAgcEnableSetterCB(self.fe_setTunerAgcEnable)
        self.port_DigitalTuner_in.setTunerGainGetterCB(self.fe_getTunerGain)
        self.port_DigitalTuner_in.setTunerGainSetterCB(self.fe_setTunerGain)
        self.port_DigitalTuner_in.setTunerReferenceSourceGetterCB(self.fe_getTunerReferenceSource)
        self.port_DigitalTuner_in.setTunerReferenceSourceSetterCB(self.fe_setTunerReferenceSource)
        self.port_DigitalTuner_in.setTunerEnableGetterCB(self.fe_getTunerEnable)
        self.port_DigitalTuner_in.setTunerEnableSetterCB(self.fe_setTunerEnable)
        self.port_DigitalTuner_in.setTunerOutputSampleRateGetterCB(self.fe_getTunerOutputSampleRate)
        self.port_DigitalTuner_in.setTunerOutputSampleRateSetterCB(self.fe_setTunerOutputSampleRate)
    
        # add callbacks for frontend ports that support callbacks
        self.port_RFInfo_in.setRFFlowIdGetterCB(self.fe_getRFFlowId)
        self.port_RFInfo_in.setRFFlowIdSetterCB(self.fe_setRFFlowId)
        self.port_RFInfo_in.setRFInfoPktGetterCB(self.fe_getRFInfoPkt)
        self.port_RFInfo_in.setRFInfoPktSetterCB(self.fe_setRFInfoPkt)

    def init_tuner(self):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
    
        #Initialize Data Members
        for tuner_id, simChannel in enumerate(self.simulated_channel_seq):
            devChannel = dev_indivTuner()
            tunerChannel = frontend.indivTuner()
            frontendStatus = self.FrontendTunerStatusStruct()
            devChannel.tuner_channel = tunerChannel
            devChannel.tuner_channel.frontend_status = frontendStatus
            
            self.frontend_tuner_status.append(devChannel)
            self.tunerChannels.append(tunerChannel)
            self.devChannels.append(frontendStatus)
        
            devChannel.reset()
            devChannel.tuner_channel.frontend_status.complex = devChannel.tuner_channel.complex = True
            if devChannel.tuner_channel.lock == None:
                devChannel.tuner_channel.lock = threading.Lock()
            devChannel.tuner_channel.frontend_status.tuner_type = simChannel.tuner_type
    
            devChannel.tuner_channel.frontend_status.output_format = "CI"
            devChannel.tuner_channel.frontend_status.output_multicast = "123.456.789."+str(tuner_id)
            devChannel.tuner_channel.frontend_status.output_port = 1000 + tuner_id
            devChannel.tuner_channel.frontend_status.output_vlan = 2000 + tuner_id
            devChannel.tuner_channel.frontend_status.tuner_number = tuner_id
            devChannel.tuner_channel.frontend_status.valid = True
    
            devChannel.tuner_channel.frontend_status.available_frequency = "%.2f-%.2f"%(simChannel.freq_min,simChannel.freq_max)
    
            if simChannel.gain_resolution > 0.0:
                cur = simChannel.gain_min
                gain_list = []
                while cur <= simChannel.gain_max:
                    gain_list.append(cur)
                    cur+=simChannel.gain_resolution
                devChannel.tuner_channel.frontend_status.available_gain = ','.join(gain_list)
            else:
                devChannel.tuner_channel.frontend_status.available_gain = "%.2f-%.2f"%(simChannel.gain_min,simChannel.gain_max)
    
            if simChannel.rate_resolution > 0.0:
                cur = simChannel.rate_min
                rate_list = []
                while cur <= simChannel.rate_max:
                    rate_list.append(cur)
                    cur+=simChannel.rate_resolution
                devChannel.tuner_channel.frontend_status.available_sample_rate = ','.join(rate_list)
            else:
                devChannel.tuner_channel.frontend_status.available_sample_rate = "%.2f-%.2f"%(simChannel.rate_min,simChannel.rate_max)
    
            if simChannel.bw_resolution > 0.0:
                cur = simChannel.bw_min
                bw_list = []
                while cur <= simChannel.bw_max:
                    bw_list.append(cur)
                    cur+=simChannel.bw_resolution
                devChannel.tuner_channel.frontend_status.available_bandwidth = ','.join(bw_list)
            else:
                devChannel.tuner_channel.frontend_status.available_bandwidth = "%.2f-%.2f"%(simChannel.bw_min,simChannel.bw_max)
        self.update_rf_flow_id("sim_RX_DIGITIZER::simulated_rf_input")
        self.update_group_id("sim_RX_DIGITIZER::simulated_group")

    def process(self):
        rx_data = False
        self.propLock.acquire()
        try:
            for devChannel in self.devChannels:
                #Check to see if channel is either not allocated, or the output is not enabled
                if not devChannel.tuner_channel.control_allocation_id or not devChannel.tuner_channel.frontend_status.enabled:
                    continue
    
                devChannel.tuner_channel.lock.acquire()
                try:
                    # calculate num_samps based on time elapsed since last push, sample rate, and buffer size
                    prev_time = devChannel.outputBufferTime
                    cur_time = bulkio.time.utils.now()
                    delta_time = (cur_time.twsec+cur_time.tfsec) - (prev_time.twsec+prev_time.tfsec)
                    samp_rate = devChannel.tuner_channel.frontend_status.sample_rate
                    num_samps = min(math.floor(samp_rate*delta_time),TUNER_BUFFER_SIZE_BYTES/2)
        
                    if num_samps > 0:
        
                        # fill buffer with junk data (num_samps worth)
                        devChannel.outputBuffer = [x/2 for x in range(num_samps*2)]
                        devChannel.outputBuffer[1::2] = range(0,-1*num_samps,-1)
                        #devChannel.outputBuffer[1::2] = [-1*x for x in devChannel.outputBuffer[1::2]]
                        #devChannel.outputBuffer = [x for y in zip(range(num_samps),range(0,-1*num_samps,-1)) for x in y]
                        
                        rx_data = True
        
                        # Update Timestamps
                        devChannel.outputBufferTime = cur_time
                        if devChannel.timeUp.twsec <= 0:
                            devChannel.timeUp = devChannel.outputBufferTime
                        devChannel.timeDown = devChannel.outputBufferTime
        
                        # Pushing Data
                        streamID = devChannel.tuner_channel.sri.streamID
                        self.dataShort_out.pushPacket(devChannel.outputBuffer, devChannel.outputBufferTime, False, streamID)
                finally:
                    devChannel.tuner_channel.lock.release()
        finally:
            self.propLock.release()
    
        if rx_data:
            return NORMAL
        return NOOP
    
    
    #############################
    ##  property configuration ##
    #############################
    
    def onconfigure_prop_simulated_channel_seq(self, oldval, newval):
        self._log.debug('%s :: ENTER'%(sys._getframe().f_code.co_name))
        self.prop_simulated_channel_seq = newval
        self.init_tuner()
    
    def onconfigure_prop_sim_tuner_allocation(self, oldval, newval):
        self._log.debug('%s :: ENTER'%(sys._getframe().f_code.co_name))
        self.prop_sim_tuner_allocation = newval
        self.prop_sim_tuner_allocation.action = "NONE"
        if newval.action == "ALLOCATE":
            self.allocate_frontend_tuner_allocation(newval)
        elif newval.action == "DEALLOCATE":
            self.deallocate_frontend_tuner_allocation(newval)
        '''
        if newval.action == "ALLOCATE" or newval.action == "DEALLOCATE":
            v = frontend.FrontendTunerAllocation()
            v.allocation_id = self.prop_sim_tuner_allocation.allocation_id
            v.bandwidth = self.prop_sim_tuner_allocation.bandwidth
            v.bandwidth_tolerance = self.prop_sim_tuner_allocation.bandwidth_tolerance
            v.center_frequency = self.prop_sim_tuner_allocation.center_frequency
            v.device_control = self.prop_sim_tuner_allocation.device_control
            v.group_id = self.prop_sim_tuner_allocation.group_id
            v.rf_flow_id = self.prop_sim_tuner_allocation.rf_flow_id
            v.sample_rate = self.prop_sim_tuner_allocation.sample_rate
            v.sample_rate_tolerance = self.prop_sim_tuner_allocation.sample_rate_tolerance
            v.tuner_type = self.prop_sim_tuner_allocation.tuner_type
            alloc = struct_to_props(v)
            if newval.action == "ALLOCATE":
                self.allocate_frontend_tuner_allocation(newval)
            else:
                self.deallocate_frontend_tuner_allocation(newval)
            '''
    
    def onconfigure_prop_sim_listener_allocation(self, oldval, newval):
        self._log.debug('%s :: ENTER'%(sys._getframe().f_code.co_name))
        self.prop_sim_listener_allocation = newval
        self.prop_sim_listener_allocation.action = "NONE"
        if newval.action == "ALLOCATE":
            self.allocate_frontend_listener_allocation(newval)
        elif newval.action == "DEALLOCATE":
            self.deallocate_frontend_listener_allocation(newval)
    
    
    ########################################
    ## Required device specific functions ## -- to be implemented by device developer
    ########################################

    # these are pure virtual, must be implemented here
    def push_EOS_on_listener(self, listener_allocation_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        tuner_id = self.getTunerMapping(listener_allocation_id)
        if tuner_id < 0:
            return False
        streamID = self.devChannels[tuner_id].tuner_channel.sri.streamID
        if not self.is_connectionID_listener_for_streamID(streamID,listener_allocation_id):
            return False
        self.dataShort_out.push_EOS_on_listener(streamID,listener_allocation_id)
        return True

    def _valid_tuner_type(self, tuner_type):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if tuner_type == "RX_DIGITIZER":
            return True
        return False
    
    def _valid_center_frequency(self, req_freq, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if req_freq < self.simulated_channel_seq[tuner_id].freq_min or req_freq > self.simulated_channel_seq[tuner_id].freq_max:
            return False
        return True
    
    def _valid_bandwidth(self, req_bw, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if req_bw < 0.0:
            return False
        if req_bw > self.simulated_channel_seq[tuner_id].bw_max:
            return False
        return True
    
    def _valid_sample_rate(self, req_sr, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if req_sr < 0.0:
            return False
        # double sample rate to convert from complex sample rate to Real sample rate
        if req_sr*2.0 > self.simulated_channel_seq[tuner_id].rate_max:
            return False
        return True

    def _dev_enable(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        # Start Streaming Now
        self.dataShort_out.pushSRI(self.devChannels[tuner_id].tuner_channel.sri)
        return True
    
    def _dev_disable(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        # Stop Streaming Now
        streamID = self.devChannels[tuner_id].tuner_channel.sri.streamID
        self.dataShort_out.pushSRI(self.devChannels[tuner_id].tuner_channel.sri)
        self.dataShort_out.pushPacket(self.devChannels[tuner_id].outputBuffer, self.devChannels[tuner_id].outputBufferTime, True, streamID)
        self.devChannels[tuner_id].outputBufferTime = bulkio.timestamp.create(0, 0)
        self.devChannels[tuner_id].timeUp = bulkio.timestamp.create(0, 0)
        self.devChannels[tuner_id].timeDown = bulkio.timestamp.create(0, 0)
        #BIO_HELPER::zeroTime(self.devChannels[tuner_id].outputBufferTime)
        #BIO_HELPER::zeroTime(self.devChannels[tuner_id].timeUp)
        #BIO_HELPER::zeroTime(self.devChannels[tuner_id].timeDown)
        return True

    def _dev_set_all(self, req_freq, req_bw, req_sr, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            self.simulated_channel_seq[tuner_id].actual_freq = req_freq
            opt_bw = self.optimize_rate(req_bw,self.simulated_channel_seq[tuner_id].bw_max,self.simulated_channel_seq[tuner_id].bw_min)
            self.simulated_channel_seq[tuner_id].actual_bw = opt_bw
            opt_sr = self.optimize_rate(req_sr,self.simulated_channel_seq[tuner_id].rate_max/2.0,self.simulated_channel_seq[tuner_id].rate_min/2.0)
            self.simulated_channel_seq[tuner_id].actual_rate = opt_sr
        else:
            raise FRONTEND.BadParameterException("_dev_set_all(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER")
        
        return True
    def _dev_set_center_frequency(self, req_freq, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            self.simulated_channel_seq[tuner_id].actual_freq = req_freq
        else:
            raise FRONTEND.BadParameterException("_dev_set_center_frequency(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        return True
    
    def _dev_set_bandwidth(self, req_bw, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            opt_bw = self.optimize_rate(req_bw,self.simulated_channel_seq[tuner_id].bw_max,self.simulated_channel_seq[tuner_id].bw_min)
            self.simulated_channel_seq[tuner_id].actual_bw = opt_bw
        else:
            raise FRONTEND.BadParameterException("_dev_set_bandwidth(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        return True
    
    def _dev_set_sample_rate(self, req_sr, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            opt_sr = self.optimize_rate(req_sr,self.simulated_channel_seq[tuner_id].rate_max/2.0,self.simulated_channel_seq[tuner_id].rate_min/2.0)
            self.simulated_channel_seq[tuner_id].actual_rate = opt_sr
        else:
            raise FRONTEND.BadParameterException("_dev_set_sample_rate(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        return True

    def _dev_get_all(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            freq = self.simulated_channel_seq[tuner_id].actual_freq
            bw = self.simulated_channel_seq[tuner_id].actual_bw
            sr = self.simulated_channel_seq[tuner_id].actual_rate
            return freq,bw,sr
        else:
            raise FRONTEND.BadParameterException("_dev_get_all(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        
    def _dev_get_center_frequency(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            return self.simulated_channel_seq[tuner_id].actual_freq
        else:
            raise FRONTEND.BadParameterException("_dev_get_center_frequency(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        
    def _dev_get_bandwidth(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            return self.simulated_channel_seq[tuner_id].actual_bw
        else:
            raise FRONTEND.BadParameterException("_dev_get_bandwidth(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        
    def _dev_get_sample_rate(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            return self.simulated_channel_seq[tuner_id].actual_rate
        else:
            raise FRONTEND.BadParameterException("_dev_get_sample_rate(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        
    
    

    ##########################################
    ## Additional device specific functions ## -- add below
    ##########################################

    def update_rf_flow_id(self, rfFlowId):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        self.rf_flow_id = rfFlowId
    
        for devChannel in self.devChannels:
            if devChannel.tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
                devChannel.tuner_channel.frontend_status.rf_flow_id = rfFlowId
            else:
                self._log.warn("update_rf_flow_id :: UNKNOWN TUNER TYPE: %s"%(devChannel.tuner_channel.frontend_status.tuner_type))
    
    def update_group_id(self, groupId):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        self.group_id = groupId
        for devChannel in self.devChannels:
            devChannel.tuner_channel.frontend_status.group_id = groupId

    def _valid_gain(self, req_gain, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if req_gain < self.simulated_channel_seq[tuner_id].gain_min:
            return False
        if req_gain > self.simulated_channel_seq[tuner_id].gain_max:
            return False
        return True
    
    def _dev_set_gain(self, req_gain, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            self.simulated_channel_seq[tuner_id].actual_gain = req_gain
        else:
            raise FRONTEND.BadParameterException("_dev_set_gain(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        return True
    
    def _dev_get_gain(self, tuner_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        if self.devChannels[tuner_id].tuner_channel.frontend_status.tuner_type == "RX_DIGITIZER":
            return self.simulated_channel_seq[tuner_id].actual_gain
        else:
            raise FRONTEND.BadParameterException("_dev_get_gain(): INVALID TUNER TYPE. MUST BE RX_DIGITIZER!")
        
    
    #######################
    ##  tuner management ## -- override frontend.FrontendTunerDevice class implementations if required
    #######################
    
    #bool enableTuner(self, tuner_id, enable): pass
    def removeTuner(self, tuner_id):
        # need to override to reset usrpChannels[tuner_id] in addition to tunerChannels[tuner_id]
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        sim_RX_DIGITIZER_base.removeTuner(self,tuner_id)
        self.self.devChannels[tuner_id].reset()
        return True
    
    
    ########################################
    ## FE DigitalTuner callback functions ## -- override base class implementations if required
    ########################################

    #def fe_getTunerType(self, alloc_id): pass
    #def fe_getTunerDeviceControl(self, alloc_id): pass
    #def fe_getTunerGroupId(self, alloc_id): pass
    #def fe_getTunerRfFlowId(self, alloc_id): pass
    #def fe_getTunerStatus(self, alloc_id): pass
    #def fe_getTunerCenterFrequency(self, alloc_id): pass
    #def fe_setTunerCenterFrequency(self, alloc_id, freq): pass
    #def fe_getTunerBandwidth(self, alloc_id): pass
    #def fe_setTunerBandwidth(self, alloc_id, bw): pass
    #def fe_getTunerAgcEnable(self, alloc_id): pass
    #def fe_setTunerAgcEnable(self, alloc_id, enable): pass
    
    def fe_getTunerGain(self, alloc_id):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        tuner_id = self.getTunerMapping(alloc_id)
        if tuner_id < 0:
            raise FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
        return self.devChannels[tuner_id].tuner_channel.frontend_status.gain
    
    def fe_setTunerGain(self, alloc_id, gain):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        tuner_id = self.getTunerMapping(alloc_id)
        if tuner_id < 0:
            raise FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
        if alloc_id != self.devChannels[tuner_id].tuner_channel.control_allocation_id:
            raise FRONTEND.FrontendException("ERROR:: ID: %s DOES NOT HAVE AUTHORIZATION TO MODIFY TUNER!"%(alloc_id))
    
        self.devChannels[tuner_id].tuner_channel.lock.acquire()
        try:
            if not self._valid_gain(gain,tuner_id):
                # TODO: add log message
                raise FRONTEND.BadParameterException("INVALID GAIN")
            try:
                self._dev_set_gain(gain,tuner_id)
            except:
                #TODO: add back log messages
                raise FRONTEND.FrontendException("WARNING: failed when configuring device hardware")
            try:
                self.devChannels[tuner_id].tuner_channel.frontend_status.gain = self._dev_get_gain(tuner_id)
            except:
                #TODO: add back log messages
                raise FRONTEND.FrontendException("WARNING: failed when querying device hardware")
        except Exception, e:
            raise FRONTEND.FrontendException("WARNING: %s"%(e))
        finally:
            self.devChannels[tuner_id].tuner_channel.lock.acquire()
    
    #def fe_getTunerReferenceSource(self, alloc_id): pass
    #def fe_setTunerReferenceSource(self, alloc_id, source): pass
    #def fe_getTunerEnable(self, alloc_id): pass
    #def fe_setTunerEnable(self, alloc_id, enable): pass
    #def fe_getTunerOutputSampleRate(self, alloc_id): pass
    #def fe_setTunerOutputSampleRate(self, alloc_id, sr): pass


    ##################################
    ## FE RFInfo callback functions ## -- override base class implementations if required
    ##################################
    
    def fe_getRFFlowId(self):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        #raise FRONTEND.NotSupportedException("getRFFlowId() IS NOT CURRENTLY SUPPORTED")
        return copy.deepcopy(self.rf_flow_id)
    
    def fe_setRFFlowId(self, data):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        #raise FRONTEND.NotSupportedException("setRFFlowId(data) IS NOT CURRENTLY SUPPORTED")
        self.update_rf_flow_id(data)
    
    #def fe_getRFInfoPkt(self): pass
    
    def fe_setRFInfoPkt(self, data):
        self._log.trace('%s :: ENTER'%(sys._getframe().f_code.co_name))
        #raise FRONTEND.NotSupportedException("setRFInfoPkt(data) IS NOT CURRENTLY SUPPORTED")
        try:
            self.update_rf_flow_id(data.rf_flow_id)
        except:
            raise FRONTEND.BadParameterException("INVALID RFInfoPkt object")
            
            
        
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.WARN)
    logging.debug("Starting Device")
    start_device(sim_RX_DIGITIZER_i)

