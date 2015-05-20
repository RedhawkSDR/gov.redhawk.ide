#!/usr/bin/env python
#
#
# AUTO-GENERATED
#
# Source: testIDE1095.spd.xml
from ossie.resource import start_component
import logging

from testIDE1095_base import *

class testIDE1095_i(testIDE1095_base):
    """<DESCRIPTION GOES HERE>"""
    def constructor(self):
        """
        This is called by the framework immediately after your component registers with the system.
        
        In general, you should add customization here and not in the __init__ constructor.  If you have 
        a custom port implementation you can override the specific implementation here with a statement
        similar to the following:
          self.some_port = MyPortImplementation()
        """
        # TODO add customization here.
    
    def getPort(self, name):
        if name == 'a_in':
            print "Looks like a_in is being grabbed, throwing exception!"
            raise CF.PortSupplier.UnknownPort
        if name == 'b_in':
            print "Looks like b_in is being grabbed"
        if name == 'a_out':
            print "Looks like a_out is being grabbed"
        if name == 'b_out':
            print "Looks like b_out is being grabbed"
            
        super(testIDE1095_i, self).getPort(name)
    
    def process(self):
        # TODO fill in your code here
        self._log.debug("process() example log message")
        return NOOP

  
if __name__ == '__main__':
    logging.getLogger().setLevel(logging.INFO)
    logging.debug("Starting Component")
    start_component(testIDE1095_i)

