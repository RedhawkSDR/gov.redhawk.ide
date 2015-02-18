#!/usr/bin/env python
#
#
import copy, logging
import bulkio
from ossie.cf.CF import Port

# ----------------------------------------------------------------------------------------
# OutShortPort_customized declaration
# ----------------------------------------------------------------------------------------


class OutShortPort_customized(bulkio.OutShortPort):
    def __init__(self, name, parent, logger=None ):
        bulkio.OutShortPort.__init__(self,name,logger)
        self.parent = parent

    def pushSRI_on_connection(self, connection_name):
        #curSRI = self._get_activeSRIs()
        #for sri in curSRI:
        #    if  self.parent.is_connectionID_valid_for_streamID(sri.streamID, connection_name):
        #        self.pushSRI(sri)
        for streamID in self.sriDict:
            if  self.parent.is_connectionID_valid_for_streamID(streamID, connection_name):
                self.pushSRI(self.sriDict[streamID])
    
    def pushSRI(self, H):
        if self.logger:
            self.logger.trace('OutShortPort_customized pushSRI ENTER ')
        self.port_lock.acquire()
        self.sriDict[H.streamID] = copy.deepcopy(H)
        try:
            for connId, port in self.outConnections.items():
                try:
                    if not self.parent.is_connectionID_valid_for_streamID(H.streamID, connId):
                        continue
                    if port != None:
                        port.pushSRI(H)
                except Exception:
                    if self.logger:
                        self.logger.error("The call to pushSRI failed on port %s connection %s instance %s", self.name, connId, port)
        finally:
            self.refreshSRI = False
            self.port_lock.release()

        if self.logger:
            self.logger.trace('OutShortPort_customized  pushSRI EXIT ')    
    
    #def pushPacket(self, data, length, T, EOS, streamID):
    
    def pushPacket(self, data, T, EOS, streamID):
        if self.logger:
            self.logger.trace('OutShortPort_customized  pushPacket ENTER ')

        if self.refreshSRI:
            if self.sriDict.has_key(streamID): 
                self.pushSRI(self.sriDict[streamID])

        self.port_lock.acquire()

        try:
            for connId, port in self.outConnections.items():
                try:
                    if not self.parent.is_connectionID_valid_for_streamID(streamID, connId):
                        continue
                    if port != None:
                        port.pushPacket(data, T, EOS, streamID)
                        self.stats.update(len(data), 0, EOS, streamID, connId)
                except Exception:
                    if self.logger:
                        self.logger.error("The call to pushPacket failed on port %s connection %s instance %s", self.name, connId, port)
            if EOS==True:
                if self.sriDict.has_key(streamID):
                    tmp = self.sriDict.pop(streamID)
        finally:
            self.port_lock.release()
 
        if self.logger:
            self.logger.trace('OutShortPort_customized  pushPacket EXIT ')
    
    def push_EOS_on_listener(self, streamID, connection_name):
        if self.logger:
            self.logger.trace('OutShortPort_customized  push_EOS_on_listener ENTER ')
            
        self.port_lock.acquire()
        try:
            if not self.parent.is_connectionID_valid_for_streamID(streamID, connection_name):
                if self.logger:
                    self.logger.error("Call to push_EOS_on_listener with invalid streamID:connectionID pair (%s,%s)", streamID, connection_name)
                return

            for connId, port in self.outConnections.items():
                try:
                    if connId != connection_name:
                        continue
                    if port != None:
                        port.pushPacket([], bulkio.timestamp.now(), True, streamID)
                        self.stats.update(0, 0, True, streamID, connection_name)
                except Exception:
                    if self.logger:
                        self.logger.error("The call to push_EOS_on_listener failed on port %s connection %s instance %s", self.name, connId, port)
            if self.sriDict.has_key(streamID):
                tmp = self.sriDict.pop(streamID)
        finally:
            self.port_lock.release()
 
        if self.logger:
            self.logger.trace('OutShortPort_customized  push_EOS_on_listener EXIT ')
    
    def connectPort(self, connection, connectionId):
        if self.logger:
            self.logger.trace('OutShortPort_customized:  connectPort ENTER ')

        self.port_lock.acquire()
        try:
            try:
                port = connection._narrow(self.PortType)
                self.outConnections[str(connectionId)] = port
                self.refreshSRI = True

                if self.logger:
                    self.logger.debug('OutShortPort_customized:  CONNECT PORT:' + str(self.name) + ' CONNECTION:' + str(connectionId) )
              
            except:
                if self.logger:
                    self.logger.error('OutShortPort_customized:  CONNECT PORT:' + str(self.name) + ' PORT FAILED NARROW')
                raise Port.InvalidPort(1, "Invalid Port for Connection ID:" + str(connectionId) )
        finally:
            self.port_lock.release()

        self.pushSRI_on_connection(connectionId)
        
        if self.logger:
            self.logger.trace('OutShortPort_customized:  connectPort EXIT ')
