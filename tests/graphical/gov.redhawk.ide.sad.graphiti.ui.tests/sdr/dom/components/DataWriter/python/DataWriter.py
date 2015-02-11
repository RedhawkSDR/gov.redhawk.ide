#!/usr/bin/env python
#
# This file is protected by Copyright. Please refer to the COPYRIGHT file distributed with this 
# source distribution.
# 
# This file is part of REDHAWK Basic Components DataWriter.
# 
# REDHAWK Basic Components DataWriter is free software: you can redistribute it and/or modify it under the terms of 
# the GNU Lesser General Public License as published by the Free Software Foundation, either 
# version 3 of the License, or (at your option) any later version.
# 
# REDHAWK Basic Components DataWriter is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
# PURPOSE.  See the GNU Lesser General Public License for more details.
# 
# You should have received a copy of the GNU Lesser General Public License along with this 
# program.  If not, see http://www.gnu.org/licenses/.
#
#
# AUTO-GENERATED
#
# Source: DataWriter.spd.xml
from ossie.resource import Resource, start_component
import logging

from DataWriter_base import *

import os
import struct
import ConfigParser

class DataWriter_i(DataWriter_base):
    """<DESCRIPTION GOES HERE>"""
    def initialize(self):
        """
        This is called by the framework immediately after your component registers with the NameService.
        
        In general, you should add customization here and not in the __init__ constructor.  If you have 
        a custom port implementation you can override the specific implementation here with a statement
        similar to the following:
          self.some_port = MyPortImplementation()
        """
        DataWriter_base.initialize(self)
        self._log.warn('"DataWriter" has been depreciated and will cease being distributed and supported in future releases.  Please replace instances of this component with "FileWriter"')
        self.complex_data = None
        self._file = None
        self._metadata_config = None
        self._metadata_filename = None
        self._filename_base = None
        
        self._direction = "<" # Little endian
        self._format = 'f' # float
        self.addPropertyChangeListener("filename", self.propChange_filename)
        self.addPropertyChangeListener("write", self.propChange_write)
        self.addPropertyChangeListener("endian", self.propChange_endian)
    def stop(self):
        self._close_file()
        DataWriter_base.stop(self)

    def process(self):
        data, T, EOS, stream_id, sri, sri_changed, input_queue_flushed = self.port_dataFloat.getPacket()
        if input_queue_flushed:
            self._log.warning("input queue flushed - data has been thrown on the floor")
        if data is None:
            return NOOP
        
        if self.write:
            self._open_file()
            self._log.debug("Writing out %d values" % len(data))
            
            self._fmt = self._direction + str(len(data))+ self._format
            byte_data = struct.pack(self._fmt, *data)
            
            self._file.write(byte_data)
            self._file.flush()
            os.fsync(self._file.fileno())
            
            self._process_metadata(sri, sri_changed, T)
        if EOS:
            self._log.info("Got EOS")
            self._close_file(clear_metadata=False)
        return NORMAL

    def _process_metadata(self, sri, sri_changed, T):
        sri_section = MetadataSectionNames.SRI
        if sri_changed or not self._metadata_config.has_section(sri_section):
            self._log.debug("Got new SRI: %s" % sri)
            if self._metadata_config.has_section(sri_section):
                self._metadata_config.remove_section(sri_section)
            self._metadata_config.add_section(sri_section)
            self._metadata_config.set(sri_section, "hversion", sri.hversion)
            self._metadata_config.set(sri_section, "xstart", sri.xstart)
            self._metadata_config.set(sri_section, "xdelta", sri.xdelta)
            self._metadata_config.set(sri_section, "xunits", sri.xunits)
            self._metadata_config.set(sri_section, "subsize", sri.subsize)
            self._metadata_config.set(sri_section, "ystart", sri.ystart)
            self._metadata_config.set(sri_section, "ydelta", sri.ydelta)
            self._metadata_config.set(sri_section, "yunits", sri.yunits)
            self._metadata_config.set(sri_section, "mode", sri.mode)
            self._metadata_config.set(sri_section, "streamID", sri.streamID)
            self._metadata_config.set(sri_section, "blocking", sri.blocking)
            
            keyword_section = MetadataSectionNames.KEYWORDS
            if self._metadata_config.has_section(keyword_section):
                self._metadata_config.remove_section(keyword_section) # Clears out any previous keywords
            self._metadata_config.add_section(keyword_section)
            for key in sri.keywords:
                self._metadata_config.set(keyword_section, key.id, str(key.value.value()))
        
        sections = self._metadata_config.sections()
        for time_section in MetadataSectionNames.TIME_SECTIONS:
            if time_section not in sections:
                self._metadata_config.add_section(time_section)
                break
        
        self._metadata_config.set(time_section, "tcmode", T.tcmode)
        self._metadata_config.set(time_section, "tcstatus", T.tcstatus)
        self._metadata_config.set(time_section, "toff", T.toff)
        self._metadata_config.set(time_section, "twsec", T.twsec)
        self._metadata_config.set(time_section, "tfsec", T.tfsec)
        
    def propChange_filename(self, id, oldval, newval):
        self._log.debug("propChange_filename: %s, %s" % (oldval, newval))
        #self.filename = newval
        if self.filename != oldval:
            # New filename
            self._filename_base = newval
            self._close_file()
            self._open_file()
    
    def propChange_write(self, id, oldval, newval):
        self._log.debug("propChange_write: %s, %s" % (oldval, newval))
        if self.write != oldval:
        # Toggled write
            if not self.write:
                self._close_file(clear_metadata=False)
            else:
                self._open_file()
    
    def propChange_endian(self, id, oldval, newval):
        self._log.debug("propChange_endian: %s, %s" % (oldval, newval))
        if self.endian == "big":
            #self.endian = "big"
            self._direction = ">"
        else:
            #self.endian = "little"
            self._direction = "<"
    
    def _close_file(self, clear_metadata=True):
        if self._file is not None:
            self._file.close()
            self._log.info("Closed data file: %s" % self._file.name)
            self._file = None
            f = open(self._metadata_filename, "wb")
            self._metadata_config.write(f)
            f.close()
            self._log.info("Wrote metadata to file: %s" % f.name)
            self._reset_metadata(clear_metadata)
    
    def _open_file(self):
        if self._file is None and self.write and self._started:
            self._check_filename_avail()
            self._file = open(self.filename, "wb")
            self._log.info("Opened data file: %s" % self._file.name)
            self._metadata_filename = self.filename + ".sri"
            if self._metadata_config is None:
                self._metadata_config = ConfigParser.ConfigParser()
                self._metadata_config.read(self.filename + ".sri")
    
    def _reset_metadata(self, full_reset):
        if full_reset:
            self._metadata_config = None
        else:
            # For when a stream is active, but the 'write' property is toggled, potentially causing
            # multiple files to be created, but the input source will not send a new sri's
            # Also for when an EOS is sent, but data resumes without a new sri
            for time_section in MetadataSectionNames.TIME_SECTIONS:
                if self._metadata_config.has_section(time_section):
                    self._metadata_config.remove_section(time_section)
    
    def _check_filename_avail(self):
        if not self.overwrite:
            filename = self._filename_base
            self.filename = self._filename_base
            x = 0
            while True:
                if not os.path.exists(filename):
                    break # File does not exist, good to go
                x += 1
                filename = self._filename_base + ".%d" % x
            if filename != self.filename:
                self._log.warning("%s already exists, writing to %s instead" % (self.filename, filename) )
                self.filename = filename


class MetadataSectionNames(object):
    SRI = "SRI"
    KEYWORDS = "SRI Keywords"
    TIME_SECTIONS = ["First Packet Time", "Last Packet Time"]
  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.WARN)
    logging.debug("Starting Component")
    start_component(DataWriter_i)

