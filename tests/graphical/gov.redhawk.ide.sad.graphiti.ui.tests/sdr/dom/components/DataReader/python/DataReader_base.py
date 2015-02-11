#!/usr/bin/env python
#
# This file is protected by Copyright. Please refer to the COPYRIGHT file distributed with this 
# source distribution.
# 
# This file is part of REDHAWK Basic Components DataReader.
# 
# REDHAWK Basic Components DataReader is free software: you can redistribute it and/or modify it under the terms of 
# the GNU Lesser General Public License as published by the Free Software Foundation, either 
# version 3 of the License, or (at your option) any later version.
# 
# REDHAWK Basic Components DataReader is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
# PURPOSE.  See the GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public License along with this 
# program.  If not, see http://www.gnu.org/licenses/.
#
#
# AUTO-GENERATED
#
# Source: DataReader.spd.xml
from ossie.cf import CF, CF__POA
from ossie.utils import uuid

from ossie.resource import Resource
from ossie.threadedcomponent import *
from ossie.properties import simple_property

import Queue, copy, time, threading
from ossie.resource import usesport, providesport
import bulkio

class DataReader_base(CF__POA.Resource, Resource, ThreadedComponent):
        # These values can be altered in the __init__ of your derived class

        PAUSE = 0.0125 # The amount of time to sleep if process return NOOP
        TIMEOUT = 5.0 # The amount of time to wait for the process thread to die when stop() is called
        DEFAULT_QUEUE_SIZE = 100 # The number of BulkIO packets that can be in the queue before pushPacket will block

        def __init__(self, identifier, execparams):
            loggerName = (execparams['NAME_BINDING'].replace('/', '.')).rsplit("_", 1)[0]
            Resource.__init__(self, identifier, execparams, loggerName=loggerName)
            ThreadedComponent.__init__(self)

            # self.auto_start is deprecated and is only kept for API compatibility
            # with 1.7.X and 1.8.0 components.  This variable may be removed
            # in future releases
            self.auto_start = False
            # Instantiate the default implementations for all ports on this component
            self.port_dataFloatOut = bulkio.OutFloatPort("dataFloatOut")

        def start(self):
            Resource.start(self)
            ThreadedComponent.startThread(self, pause=self.PAUSE)

        def stop(self):
            if not ThreadedComponent.stopThread(self, self.TIMEOUT):
                raise CF.Resource.StopError(CF.CF_NOTSET, "Processing thread did not die")
            Resource.stop(self)

        def releaseObject(self):
            try:
                self.stop()
            except Exception:
                self._log.exception("Error stopping")
            Resource.releaseObject(self)

        ######################################################################
        # PORTS
        # 
        # DO NOT ADD NEW PORTS HERE.  You can add ports in your derived class, in the SCD xml file, 
        # or via the IDE.

        port_dataFloatOut = usesport(name="dataFloatOut",
                                     repid="IDL:BULKIO/dataFloat:1.0",
                                     type_="data")

        ######################################################################
        # PROPERTIES
        # 
        # DO NOT ADD NEW PROPERTIES HERE.  You can add properties in your derived class, in the PRF xml file
        # or by using the IDE.
        SampleRate = simple_property(id_="SampleRate",
                                     type_="double",
                                     defvalue=336000.0,
                                     mode="readwrite",
                                     action="external",
                                     kinds=("configure",),
                                     description="""Sample rate for output data""")
        
        StreamID = simple_property(id_="StreamID",
                                   type_="string",
                                   defvalue="dataPlayerStream",
                                   mode="readwrite",
                                   action="external",
                                   kinds=("configure",),
                                   description="""bulkio streamID associated with this data""")
        
        FrontendRF = simple_property(id_="FrontendRF",
                                     type_="long",
                                     defvalue=0,
                                     mode="readwrite",
                                     action="external",
                                     kinds=("configure",),
                                     description="""Radio frequency associated with this data.  This data is transmitted as a bulkio keyword""")
        
        InputFile = simple_property(id_="InputFile",
                                    type_="string",
                                    defvalue="/the/path/to/my/file",
                                    mode="readwrite",
                                    action="external",
                                    kinds=("configure",),
                                    description="""Path to the binary data file to read from""")
        
        SpeedFactor = simple_property(id_="SpeedFactor",
                                      type_="float",
                                      defvalue=1.0,
                                      mode="readwrite",
                                      action="external",
                                      kinds=("configure",),
                                      description="""Ratio of "real time" to play data out the bulkio port.
                                      1.0:  real time.
                                      > 1.0:  faster than real time
                                      < 1.0: slower then real time
                                      < 0: no sleeping - go as fast as possible""")
        
        Play = simple_property(id_="Play",
                               type_="boolean",
                               defvalue=False,
                               mode="readwrite",
                               action="external",
                               kinds=("configure",),
                               description="""If play is false data playback is paused.  When play is set to true we resume playback from the same point in the file""")
        
        ydelta = simple_property(id_="ydelta",
                                 type_="double",
                                 defvalue=0.0,
                                 mode="readwrite",
                                 action="external",
                                 kinds=("configure",),
                                 description="""The ydelta associated with the bulkio SRI.  This is only used for framed data (subsize > 0)""")
        
        subsize = simple_property(id_="subsize",
                                  type_="long",
                                  defvalue=0,
                                  mode="readwrite",
                                  action="external",
                                  kinds=("configure",),
                                  description="""The frame size if the data is framed.  This is used for the bulkio SRI.""")
        
        complex = simple_property(id_="complex",
                                  type_="boolean",
                                  defvalue=True,
                                  mode="readwrite",
                                  action="external",
                                  kinds=("configure",),
                                  description="""Flag to indicate data is complex.  If true, data values assumed to be alternating real and complex float values.  """)
        
        Loop = simple_property(id_="Loop",
                               type_="boolean",
                               defvalue=False,
                               mode="readwrite",
                               action="external",
                               kinds=("execparam",),
                               description="""Continue to replay and loop over the input file when we are done or not""")
        
        blocking = simple_property(id_="blocking",
                                   type_="boolean",
                                   defvalue=True,
                                   mode="readwrite",
                                   action="external",
                                   kinds=("configure",),
                                   description="""Set the blocking flag in the bulkio sri.  If this is not set to true you risk packet drops if processing can't keep up with data playback rate.""")
        

