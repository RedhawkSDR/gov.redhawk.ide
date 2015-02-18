#!/usr/bin/env python
#
# AUTO-GENERATED CODE.  DO NOT MODIFY!
#
# Source: sim_RX_DIGITIZER.spd.xml
from ossie.cf import CF, CF__POA
from ossie.utils import uuid

#from ossie.device import Device
from ossie.properties import simple_property
from ossie.properties import struct_property
from ossie.properties import structseq_property
#from ossie.properties import props_to_dict
#from ossie.properties import props_from_dict
from ossie.properties import struct_to_props

#import Queue, copy
import time, threading
#from ossie.resource import usesport, providesport
import bulkio
#from redhawk.frontendInterfaces import FRONTEND__POA

from redhawk.frontendInterfaces import FRONTEND
import frontend
from frontend import FrontendTunerDevice, FrontendTunerAllocation, FrontendListenerAllocation
#from frontend import InDigitalTunerPort, InRFInfoPort

NOOP = -1
NORMAL = 0
FINISH = 1
class ProcessThread(threading.Thread):
    def __init__(self, target, pause=0.0125):
        threading.Thread.__init__(self)
        self.setDaemon(True)
        self.target = target
        self.pause = pause
        self.stop_signal = threading.Event()

    def stop(self):
        self.stop_signal.set()

    def updatePause(self, pause):
        self.pause = pause

    def run(self):
        state = NORMAL
        while (state != FINISH) and (not self.stop_signal.isSet()):
            state = self.target()
            delay = 1e-6
            if (state == NOOP):
                # If there was no data to process sleep to avoid spinning
                delay = self.pause
            time.sleep(delay)

class sim_RX_DIGITIZER_base(FrontendTunerDevice):
        # These values can be altered in the __init__ of your derived class

        PAUSE = 0.0125 # The amount of time to sleep if process return NOOP
        TIMEOUT = 5.0 # The amount of time to wait for the process thread to die when stop() is called
        DEFAULT_QUEUE_SIZE = 100 # The number of BulkIO packets that can be in the queue before pushPacket will block

        def __init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams):
            FrontendTunerDevice.__init__(self, devmgr, uuid, label, softwareProfile, compositeDevice, execparams)
            self.threadControlLock = threading.RLock()
            self.process_thread = None
            # self.auto_start is deprecated and is only kept for API compatibility
            # with 1.7.X and 1.8.0 devices.  This variable may be removed
            # in future releases
            self.auto_start = False

        def initialize(self):
            FrontendTunerDevice.initialize(self)
            
            # Instantiate the default implementations for all ports on this device
            self.port_DigitalTuner_in = frontend.InDigitalTunerPort("DigitalTuner_in", logger=self._log)
            self.port_RFInfo_in = frontend.InRFInfoPort("RFInfo_in", logger=self._log)
            self.port_dataShort_out = bulkio.OutShortPort("dataShort_out", logger=self._log)
            

        def start(self):
            self.threadControlLock.acquire()
            try:
                FrontendTunerDevice.start(self)
                if self.process_thread == None:
                    self.process_thread = ProcessThread(target=self.process, pause=self.PAUSE)
                    self.process_thread.start()
            finally:
                self.threadControlLock.release()

        def process(self):
            """The process method should process a single "chunk" of data and then return.  This method will be called
            from the processing thread again, and again, and again until it returns FINISH or stop() is called on the
            device.  If no work is performed, then return NOOP"""
            raise NotImplementedError

        def stop(self):
            self.threadControlLock.acquire()
            try:
                process_thread = self.process_thread
                self.process_thread = None

                if process_thread != None:
                    process_thread.stop()
                    process_thread.join(self.TIMEOUT)
                    if process_thread.isAlive():
                        raise CF.Resource.StopError(CF.CF_NOTSET, "Processing thread did not die")
                FrontendTunerDevice.stop(self)
            finally:
                self.threadControlLock.release()

        def releaseObject(self):
            try:
                self.stop()
            except Exception:
                self._log.exception("Error stopping")
            self.threadControlLock.acquire()
            try:
                FrontendTunerDevice.releaseObject(self)
            finally:
                self.threadControlLock.release()

        # FE DigitalTuner callback functions -- these are generated in base class
        def fe_getTunerType(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.tuner_type
        
        def fe_getTunerDeviceControl(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            if alloc_id == self.tunerChannels[tuner_id].control_allocation_id:
                return True
            return False
        
        def fe_getTunerGroupId(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.group_id
        
        def fe_getTunerRfFlowId(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.rf_flow_id
        
        def fe_getTunerStatus(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return struct_to_props(self.tunerChannels[tuner_id].frontend_status)
        
        def fe_getTunerCenterFrequency(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.center_frequency
        
        def fe_setTunerCenterFrequency(self, alloc_id, freq):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            if alloc_id != self.tunerChannels[tuner_id].control_allocation_id:
                raise  FRONTEND.FrontendException("ERROR: ID: %s DOES NOT HAVE AUTHORIZATION TO MODIFY TUNER!"%(alloc_id))
        
            try:
                # If the freq has changed (change in stream) or the tuner is disabled, then set it as disabled
                isTunerEnabled = self.tunerChannels[tuner_id].frontend_status.enabled
                if not isTunerEnabled or self.tunerChannels[tuner_id].frontend_status.center_frequency != freq:
                    self.enableTuner(tuner_id, False) # TODO: is this a generic thing we can assume is desired when changing any tuner device?
                
                self.tunerChannels[tuner_id].lock.acquire()
                try:
                    if not self._valid_center_frequency(freq,tuner_id):
                        # TODO: add log message
                        raise  FRONTEND.BadParameterException("INVALID FREQUENCY")
                    try:
                        self._dev_set_center_frequency(freq,tuner_id)
                    except:
                        #TODO: add back log messages
                        raise  FRONTEND.FrontendException("WARNING: failed when configuring device hardware")
                    try:
                        self.tunerChannels[tuner_id].frontend_status.center_frequency = self._dev_get_center_frequency(tuner_id)
                    except:
                        #TODO: add back log messages
                        raise  FRONTEND.FrontendException("WARNING: failed when querying device hardware")
                finally:
                    self.tunerChannels[tuner_id].lock.release()
                    
                if isTunerEnabled:
                    self.enableTuner(tuner_id, True)
            except Exception, e:
                raise  FRONTEND.FrontendException("WARNING: %s"%(e))
        
        def fe_getTunerBandwidth(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.bandwidth
        
        def fe_setTunerBandwidth(self, alloc_id, bw):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            if alloc_id != self.tunerChannels[tuner_id].control_allocation_id:
                raise  FRONTEND.FrontendException("ERROR: ID: %s DOES NOT HAVE AUTHORIZATION TO MODIFY TUNER!"%(alloc_id))
        
            try:
                self.tunerChannels[tuner_id].lock.acquire()
                try:
                    if not self._valid_bandwidth(bw,tuner_id):
                        # TODO: add log message
                        raise  FRONTEND.BadParameterException("INVALID BANDWIDTH")
                    try:
                        self._dev_set_bandwidth(bw,tuner_id)
                    except:
                        #TODO: add back log messages
                        raise  FRONTEND.FrontendException("WARNING: failed when configuring device hardware")
                    try:
                        self.tunerChannels[tuner_id].frontend_status.bandwidth = self._dev_get_bandwidth(tuner_id)
                    except:
                        #TODO: add back log messages
                        raise  FRONTEND.FrontendException("WARNING: failed when querying device hardware")
                finally:
                    self.tunerChannels[tuner_id].lock.release()
            except Exception, e:
                raise  FRONTEND.FrontendException("WARNING: %s"%(e))
        
        def fe_getTunerAgcEnable(self, alloc_id):
            raise  FRONTEND.NotSupportedException("getTunerAgcEnable(const char* id) IS NOT CURRENTLY SUPPORTED")
        
        def fe_setTunerAgcEnable(self, alloc_id, enable):
            raise  FRONTEND.NotSupportedException("setTunerAgcEnable(const char* id, CORBA::Boolean enable) IS NOT CURRENTLY SUPPORTED")
        
        def fe_getTunerGain(self, alloc_id):
            raise  FRONTEND.NotSupportedException("getTunerGain(const char* id) IS NOT CURRENTLY SUPPORTED")
        
        def fe_setTunerGain(self, alloc_id, gain):
            raise  FRONTEND.NotSupportedException("setTunerGain(const char* id, CORBA::Float gain) IS NOT CURRENTLY SUPPORTED")
        
        def fe_getTunerReferenceSource(self, alloc_id):
            raise  FRONTEND.NotSupportedException("getTunerReferenceSource(const char* id) IS NOT CURRENTLY SUPPORTED")
        
        def fe_setTunerReferenceSource(self, alloc_id, source):
            raise  FRONTEND.NotSupportedException("setTunerReferenceSource(const char* id, CORBA::Long source) IS NOT CURRENTLY SUPPORTED")
        
        def fe_getTunerEnable(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.enabled
        
        def fe_setTunerEnable(self, alloc_id, enable):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            if alloc_id != self.tunerChannels[tuner_id].control_allocation_id:
                raise  FRONTEND.FrontendException("ERROR: ID: %s DOES NOT HAVE AUTHORIZATION TO MODIFY TUNER!"%(alloc_id))
            try:
                self.enableTuner(tuner_id, enable)
            except Exception, e:
                raise  FRONTEND.FrontendException("WARNING: Exception Caught during enableTuner: %s"%(e))
        
        def fe_getTunerOutputSampleRate(self, alloc_id):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            return self.tunerChannels[tuner_id].frontend_status.sample_rate
        
        def fe_setTunerOutputSampleRate(self, alloc_id, sr):
            tuner_id = self.getTunerMapping(alloc_id)
            if tuner_id < 0:
                raise  FRONTEND.FrontendException("ERROR: ID: %s IS NOT ASSOCIATED WITH ANY TUNER!"%(alloc_id))
            if  alloc_id != self.tunerChannels[tuner_id].control_allocation_id:
                raise  FRONTEND.FrontendException("ERROR: ID: %s DOES NOT HAVE AUTHORIZATION TO MODIFY TUNER!"%(alloc_id))
        
            try:
                self.tunerChannels[tuner_id].lock.acquire()
                try:
                    if not self._valid_sample_rate(sr,tuner_id):
                        # TODO: add log message
                        raise  FRONTEND.BadParameterException("INVALID SAMPLE RATE")
                    try:
                        self._dev_set_sample_rate(sr,tuner_id)
                    except:
                        #TODO: add back log messages
                        raise  FRONTEND.FrontendException("WARNING: failed when configuring device hardware")
                    try:
                        self.tunerChannels[tuner_id].frontend_status.sample_rate = self._dev_get_sample_rate(tuner_id)
                    except:
                        #TODO: add back log messages
                        raise  FRONTEND.FrontendException("WARNING: failed when querying device hardware")
                finally:
                    self.tunerChannels[tuner_id].lock.release()
            except Exception, e:
                raise  FRONTEND.FrontendException("WARNING: %s"%(e))

        # FE RFInfo callback functions -- these are generated in base class
        def fe_getRFFlowId(self):
            raise  FRONTEND.NotSupportedException("rf_flow_id() IS NOT CURRENTLY SUPPORTED")
        
        def fe_setRFFlowId(self, data):
            raise  FRONTEND.NotSupportedException("rf_flow_id(const char* data) IS NOT CURRENTLY SUPPORTED")
        
        def fe_getRFInfoPkt(self):
            raise  FRONTEND.NotSupportedException("rfinfo_pkt() IS NOT CURRENTLY SUPPORTED")
        
        def fe_setRFInfoPkt(self, data):
            raise  FRONTEND.NotSupportedException("rfinfo_pkt(const ::FRONTEND::RFInfoPkt& data) IS NOT CURRENTLY SUPPORTED")
            
                
        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        device_kind = simple_property(id_="DCE:cdc5ee18-7ceb-4ae6-bf4c-31f983179b4d",
                                      name="device_kind",
                                      type_="string",
                                      defvalue="FRONTEND::TUNER",
                                      mode="readonly",
                                      action="eq",
                                      kinds=("allocation","configure"),
                                      description="""This specifies the device kind"""
                                      )
        device_model = simple_property(id_="DCE:0f99b2e4-9903-4631-9846-ff349d18ecfb",
                                       name="device_model",
                                       type_="string",
                                       defvalue="RX_DIGITIZER simulator",
                                       mode="readonly",
                                       action="eq",
                                       kinds=("allocation","configure"),
                                       description=""" This specifies the specific device"""
                                       )
        
        frontend_tuner_allocation = struct_property(id_="FRONTEND::tuner_allocation",
                                                    name="frontend_tuner_allocation",
                                                    structdef=FrontendTunerAllocation,
                                                    configurationkind=("allocation",),
                                                    mode="readwrite",
                                                    description="""Frontend Interfaces v2.0 main allocation structure"""
                                                    )
        frontend_listener_allocation = struct_property(id_="FRONTEND::listener_allocation",
                                                       name="frontend_listener_allocation",
                                                       structdef=FrontendListenerAllocation,
                                                       configurationkind=("allocation",),
                                                       mode="readwrite",
                                                       description="""Allocates a listener (subscriber) based off a previous allocation """
                                                       )
        class SimTunerAllocation(object):
            action = simple_property(id_="SIM::tuner_allocation::action",
                                     name="action",
                                     type_="string",
                                     defvalue="NONE",
                                     )
            tuner_type = simple_property(id_="SIM::tuner_allocation::tuner_type",
                                         name="tuner_type",
                                         type_="string",
                                         defvalue="RX_DIGITIZER",
                                         )
            allocation_id = simple_property(id_="SIM::tuner_allocation::allocation_id",
                                            name="allocation_id",
                                            type_="string",
                                            )
            center_frequency = simple_property(id_="SIM::tuner_allocation::center_frequency",
                                               name="center_frequency",
                                               type_="double",
                                               )
            bandwidth = simple_property(id_="SIM::tuner_allocation::bandwidth",
                                        name="bandwidth",
                                        type_="double",
                                        )
            bandwidth_tolerance = simple_property(id_="SIM::tuner_allocation::bandwidth_tolerance",
                                                  name="bandwidth_tolerance",
                                                  type_="double",
                                                  defvalue=10.0,
                                                  )
            sample_rate = simple_property(id_="SIM::tuner_allocation::sample_rate",
                                          name="sample_rate",
                                          type_="double",
                                          )
            sample_rate_tolerance = simple_property(id_="SIM::tuner_allocation::sample_rate_tolerance",
                                                    name="sample_rate_tolerance",
                                                    type_="double",
                                                    defvalue=10.0,
                                                    )
            device_control = simple_property(id_="SIM::tuner_allocation::device_control",
                                             name="device_control",
                                             type_="boolean",
                                             defvalue=True,
                                             )
            group_id = simple_property(id_="SIM::tuner_allocation::group_id",
                                       name="group_id",
                                       type_="string",
                                       )
            rf_flow_id = simple_property(id_="SIM::tuner_allocation::rf_flow_id",
                                         name="rf_flow_id",
                                         type_="string",
                                         )
        
            def __init__(self, **kw):
                """Construct an initialized instance of this struct definition"""
                for attrname, classattr in type(self).__dict__.items():
                    if type(classattr) == simple_property:
                        classattr.initialize(self)
                for k,v in kw.items():
                    setattr(self,k,v)
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["action"] = self.action
                d["tuner_type"] = self.tuner_type
                d["allocation_id"] = self.allocation_id
                d["center_frequency"] = self.center_frequency
                d["bandwidth"] = self.bandwidth
                d["bandwidth_tolerance"] = self.bandwidth_tolerance
                d["sample_rate"] = self.sample_rate
                d["sample_rate_tolerance"] = self.sample_rate_tolerance
                d["device_control"] = self.device_control
                d["group_id"] = self.group_id
                d["rf_flow_id"] = self.rf_flow_id
                return str(d)
        
            def getId(self):
                return "SIM::tuner_allocation"
        
            def isStruct(self):
                return True
        
            def getMembers(self):
                return [("action",self.action),("tuner_type",self.tuner_type),("allocation_id",self.allocation_id),("center_frequency",self.center_frequency),("bandwidth",self.bandwidth),("bandwidth_tolerance",self.bandwidth_tolerance),("sample_rate",self.sample_rate),("sample_rate_tolerance",self.sample_rate_tolerance),("device_control",self.device_control),("group_id",self.group_id),("rf_flow_id",self.rf_flow_id)]

        sim_tuner_allocation = struct_property(id_="SIM::tuner_allocation",
                                               name="sim_tuner_allocation",
                                               structdef=SimTunerAllocation,
                                               configurationkind=("configure",),
                                               mode="readwrite",
                                               description="""Frontend Interfaces v2.0 main allocation structure"""
                                               )
        class SimListenerAllocation(object):
            action = simple_property(id_="SIM::listener_allocation::action",
                                     name="action",
                                     type_="string",
                                     defvalue="NONE",
                                     )
            existing_allocation_id = simple_property(id_="SIM::listener_allocation::existing_allocation_id",
                                                     name="existing_allocation_id",
                                                     type_="string",
                                                     )
            listener_allocation_id = simple_property(id_="SIM::listener_allocation::listener_allocation_id",
                                                     name="listener_allocation_id",
                                                     type_="string",
                                                     )
        
            def __init__(self, **kw):
                """Construct an initialized instance of this struct definition"""
                for attrname, classattr in type(self).__dict__.items():
                    if type(classattr) == simple_property:
                        classattr.initialize(self)
                for k,v in kw.items():
                    setattr(self,k,v)
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["action"] = self.action
                d["existing_allocation_id"] = self.existing_allocation_id
                d["listener_allocation_id"] = self.listener_allocation_id
                return str(d)
        
            def getId(self):
                return "SIM::listener_allocation"
        
            def isStruct(self):
                return True
        
            def getMembers(self):
                return [("action",self.action),("existing_allocation_id",self.existing_allocation_id),("listener_allocation_id",self.listener_allocation_id)]

        sim_listener_allocation = struct_property(id_="SIM::listener_allocation",
                                                  name="sim_listener_allocation",
                                                  structdef=SimListenerAllocation,
                                                  configurationkind=("configure",),
                                                  mode="readwrite",
                                                  description="""Allocates a listener (subscriber) based off a previous allocation """
                                                  )
        class FrontendTunerStatusStruct(object):
            tuner_type = simple_property(id_="FRONTEND::tuner_status::tuner_type",
                                         name="tuner_type",
                                         type_="string",
                                         )
            allocation_id_csv = simple_property(id_="FRONTEND::tuner_status::allocation_id_csv",
                                                name="allocation_id_csv",
                                                type_="string",
                                                )
            center_frequency = simple_property(id_="FRONTEND::tuner_status::center_frequency",
                                               name="center_frequency",
                                               type_="double",
                                               )
            bandwidth = simple_property(id_="FRONTEND::tuner_status::bandwidth",
                                        name="bandwidth",
                                        type_="double",
                                        )
            sample_rate = simple_property(id_="FRONTEND::tuner_status::sample_rate",
                                          name="sample_rate",
                                          type_="double",
                                          )
            group_id = simple_property(id_="FRONTEND::tuner_status::group_id",
                                       name="group_id",
                                       type_="string",
                                       )
            rf_flow_id = simple_property(id_="FRONTEND::tuner_status::rf_flow_id",
                                         name="rf_flow_id",
                                         type_="string",
                                         )
            enabled = simple_property(id_="FRONTEND::tuner_status::enabled",
                                      name="enabled",
                                      type_="boolean",
                                      )
            bandwidth_tolerance = simple_property(id_="FRONTEND::tuner_status::bandwidth_tolerance",
                                                  name="bandwidth_tolerance",
                                                  type_="double",
                                                  )
            sample_rate_tolerance = simple_property(id_="FRONTEND::tuner_status::sample_rate_tolerance",
                                                    name="sample_rate_tolerance",
                                                    type_="double",
                                                    )
            complex = simple_property(id_="FRONTEND::tuner_status::complex",
                                      name="complex",
                                      type_="boolean",
                                      )
            gain = simple_property(id_="FRONTEND::tuner_status::gain",
                                   name="gain",
                                   type_="double",
                                   )
            agc = simple_property(id_="FRONTEND::tuner_status::agc",
                                  name="agc",
                                  type_="boolean",
                                  )
            valid = simple_property(id_="FRONTEND::tuner_status::valid",
                                    name="valid",
                                    type_="boolean",
                                    )
            available_frequency = simple_property(id_="FRONTEND::tuner_status::available_frequency",
                                                  name="available_frequency",
                                                  type_="string",
                                                  )
            available_bandwidth = simple_property(id_="FRONTEND::tuner_status::available_bandwidth",
                                                  name="available_bandwidth",
                                                  type_="string",
                                                  )
            available_gain = simple_property(id_="FRONTEND::tuner_status::available_gain",
                                             name="available_gain",
                                             type_="string",
                                             )
            available_sample_rate = simple_property(id_="FRONTEND::tuner_status::available_sample_rate",
                                                    name="available_sample_rate",
                                                    type_="string",
                                                    )
            reference_source = simple_property(id_="FRONTEND::tuner_status::reference_source",
                                               name="reference_source",
                                               type_="long",
                                               )
            output_format = simple_property(id_="FRONTEND::tuner_status::output_format",
                                            name="output_format",
                                            type_="string",
                                            )
            output_multicast = simple_property(id_="FRONTEND::tuner_status::output_multicast",
                                               name="output_multicast",
                                               type_="string",
                                               )
            output_vlan = simple_property(id_="FRONTEND::tuner_status::output_vlan",
                                          name="output_vlan",
                                          type_="long",
                                          )
            output_port = simple_property(id_="FRONTEND::tuner_status::output_port",
                                          name="output_port",
                                          type_="long",
                                          )
            decimation = simple_property(id_="FRONTEND::tuner_status::decimation",
                                         name="decimation",
                                         type_="long",
                                         )
            tuner_number = simple_property(id_="FRONTEND::tuner_status::tuner_number",
                                           name="tuner_number",
                                           type_="short",
                                           )
        
            def __init__(self, tuner_type="", allocation_id_csv="", center_frequency=0.0, bandwidth=0.0, sample_rate=0.0, group_id="", rf_flow_id="", enabled=False, bandwidth_tolerance=0.0, sample_rate_tolerance=0.0, complex=False, gain=0.0, agc=False, valid=False, available_frequency="", available_bandwidth="", available_gain="", available_sample_rate="", reference_source=0, output_format="", output_multicast="", output_vlan=0, output_port=0, decimation=0, tuner_number=0):
                self.tuner_type = tuner_type
                self.allocation_id_csv = allocation_id_csv
                self.center_frequency = center_frequency
                self.bandwidth = bandwidth
                self.sample_rate = sample_rate
                self.group_id = group_id
                self.rf_flow_id = rf_flow_id
                self.enabled = enabled
                self.bandwidth_tolerance = bandwidth_tolerance
                self.sample_rate_tolerance = sample_rate_tolerance
                self.complex = complex
                self.gain = gain
                self.agc = agc
                self.valid = valid
                self.available_frequency = available_frequency
                self.available_bandwidth = available_bandwidth
                self.available_gain = available_gain
                self.available_sample_rate = available_sample_rate
                self.reference_source = reference_source
                self.output_format = output_format
                self.output_multicast = output_multicast
                self.output_vlan = output_vlan
                self.output_port = output_port
                self.decimation = decimation
                self.tuner_number = tuner_number
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["tuner_type"] = self.tuner_type
                d["allocation_id_csv"] = self.allocation_id_csv
                d["center_frequency"] = self.center_frequency
                d["bandwidth"] = self.bandwidth
                d["sample_rate"] = self.sample_rate
                d["group_id"] = self.group_id
                d["rf_flow_id"] = self.rf_flow_id
                d["enabled"] = self.enabled
                d["bandwidth_tolerance"] = self.bandwidth_tolerance
                d["sample_rate_tolerance"] = self.sample_rate_tolerance
                d["complex"] = self.complex
                d["gain"] = self.gain
                d["agc"] = self.agc
                d["valid"] = self.valid
                d["available_frequency"] = self.available_frequency
                d["available_bandwidth"] = self.available_bandwidth
                d["available_gain"] = self.available_gain
                d["available_sample_rate"] = self.available_sample_rate
                d["reference_source"] = self.reference_source
                d["output_format"] = self.output_format
                d["output_multicast"] = self.output_multicast
                d["output_vlan"] = self.output_vlan
                d["output_port"] = self.output_port
                d["decimation"] = self.decimation
                d["tuner_number"] = self.tuner_number
                return str(d)
        
            def getId(self):
                return "frontend_tuner_status_struct"
        
            def isStruct(self):
                return True
        
            def getMembers(self):
                return [("tuner_type",self.tuner_type),("allocation_id_csv",self.allocation_id_csv),("center_frequency",self.center_frequency),("bandwidth",self.bandwidth),("sample_rate",self.sample_rate),("group_id",self.group_id),("rf_flow_id",self.rf_flow_id),("enabled",self.enabled),("bandwidth_tolerance",self.bandwidth_tolerance),("sample_rate_tolerance",self.sample_rate_tolerance),("complex",self.complex),("gain",self.gain),("agc",self.agc),("valid",self.valid),("available_frequency",self.available_frequency),("available_bandwidth",self.available_bandwidth),("available_gain",self.available_gain),("available_sample_rate",self.available_sample_rate),("reference_source",self.reference_source),("output_format",self.output_format),("output_multicast",self.output_multicast),("output_vlan",self.output_vlan),("output_port",self.output_port),("decimation",self.decimation),("tuner_number",self.tuner_number)]

        frontend_tuner_status = structseq_property(id_="FRONTEND::tuner_status",
                                                   name="frontend_tuner_status",
                                                   structdef=FrontendTunerStatusStruct,
                                                   defvalue=[],
                                                   configurationkind=("configure",),
                                                   mode="readonly",
                                                   description="""Frontend Interfaces v2.0 status structure. One element for every frontend resource (receiver, transmitter) configured on this hardware"""
                                                   )
        class SimChannelStruct(object):
            tuner_type = simple_property(id_="sim::tuner_type",
                                         name="tuner_type",
                                         type_="string",
                                         defvalue="RX_DIGITIZER",
                                         )
            freq_min = simple_property(id_="sim::freq_min",
                                       name="freq_min",
                                       type_="double",
                                       )
            freq_max = simple_property(id_="sim::freq_max",
                                       name="freq_max",
                                       type_="double",
                                       )
            actual_freq = simple_property(id_="sim::actual_freq",
                                          name="actual_freq",
                                          type_="double",
                                          )
            bw_min = simple_property(id_="sim::bw_min",
                                     name="bw_min",
                                     type_="double",
                                     )
            bw_max = simple_property(id_="sim::bw_max",
                                     name="bw_max",
                                     type_="double",
                                     )
            bw_resolution = simple_property(id_="sim::bw_resolution",
                                            name="bw_resolution",
                                            type_="double",
                                            )
            actual_bw = simple_property(id_="sim::actual_bw",
                                        name="actual_bw",
                                        type_="double",
                                        )
            rate_min = simple_property(id_="sim::rate_min",
                                       name="rate_min",
                                       type_="double",
                                       )
            rate_max = simple_property(id_="sim::rate_max",
                                       name="rate_max",
                                       type_="double",
                                       )
            rate_resolution = simple_property(id_="sim::rate_resolution",
                                              name="rate_resolution",
                                              type_="double",
                                              )
            actual_rate = simple_property(id_="sim::actual_rate",
                                          name="actual_rate",
                                          type_="double",
                                          )
            gain_min = simple_property(id_="sim::sim::gain_min",
                                       name="gain_min",
                                       type_="double",
                                       )
            gain_max = simple_property(id_="sim::gain_max",
                                       name="gain_max",
                                       type_="double",
                                       )
            gain_resolution = simple_property(id_="sim::gain_resolution",
                                              name="gain_resolution",
                                              type_="double",
                                              )
            actual_gain = simple_property(id_="sim::actual_gain",
                                          name="actual_gain",
                                          type_="double",
                                          )
        
            def __init__(self, tuner_type="RX_DIGITIZER", freq_min=0.0, freq_max=0.0, actual_freq=0.0, bw_min=0.0, bw_max=0.0, bw_resolution=0.0, actual_bw=0.0, rate_min=0.0, rate_max=0.0, rate_resolution=0.0, actual_rate=0.0, gain_min=0.0, gain_max=0.0, gain_resolution=0.0, actual_gain=0.0):
                self.tuner_type = tuner_type
                self.freq_min = freq_min
                self.freq_max = freq_max
                self.actual_freq = actual_freq
                self.bw_min = bw_min
                self.bw_max = bw_max
                self.bw_resolution = bw_resolution
                self.actual_bw = actual_bw
                self.rate_min = rate_min
                self.rate_max = rate_max
                self.rate_resolution = rate_resolution
                self.actual_rate = actual_rate
                self.gain_min = gain_min
                self.gain_max = gain_max
                self.gain_resolution = gain_resolution
                self.actual_gain = actual_gain
        
            def __str__(self):
                """Return a string representation of this structure"""
                d = {}
                d["tuner_type"] = self.tuner_type
                d["freq_min"] = self.freq_min
                d["freq_max"] = self.freq_max
                d["actual_freq"] = self.actual_freq
                d["bw_min"] = self.bw_min
                d["bw_max"] = self.bw_max
                d["bw_resolution"] = self.bw_resolution
                d["actual_bw"] = self.actual_bw
                d["rate_min"] = self.rate_min
                d["rate_max"] = self.rate_max
                d["rate_resolution"] = self.rate_resolution
                d["actual_rate"] = self.actual_rate
                d["gain_min"] = self.gain_min
                d["gain_max"] = self.gain_max
                d["gain_resolution"] = self.gain_resolution
                d["actual_gain"] = self.actual_gain
                return str(d)
        
            def getId(self):
                return "sim_channel_struct"
        
            def isStruct(self):
                return True
        
            def getMembers(self):
                return [("tuner_type",self.tuner_type),("freq_min",self.freq_min),("freq_max",self.freq_max),("actual_freq",self.actual_freq),("bw_min",self.bw_min),("bw_max",self.bw_max),("bw_resolution",self.bw_resolution),("actual_bw",self.actual_bw),("rate_min",self.rate_min),("rate_max",self.rate_max),("rate_resolution",self.rate_resolution),("actual_rate",self.actual_rate),("gain_min",self.gain_min),("gain_max",self.gain_max),("gain_resolution",self.gain_resolution),("actual_gain",self.actual_gain)]

        simulated_channel_seq = structseq_property(id_="simulated_channel_seq",
                                                   name="simulated_channel_seq",
                                                   structdef=SimChannelStruct,
                                                   defvalue=[SimChannelStruct('RX_DIGITIZER',28750000.0,2240000000.0,0,4000000.0,40000000.0,0.0,0,1000000.0,100000000.0,0.0,0,0.0,38.0,0.0,0)],
                                                   configurationkind=("configure",),
                                                   mode="readwrite",
                                                   description="""Describes the channels found in this sim device"""
                                                   )
