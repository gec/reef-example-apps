/**
 * Copyright 2011 Green Energy Corp.
 *
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Green Energy
 * Corp licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.totalgrid.reef.examples.protocol.basic.adapter;


import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.AllScadaService;
import org.totalgrid.reef.client.service.MeasurementService;
import org.totalgrid.reef.client.service.command.CommandRequestHandler;
import org.totalgrid.reef.client.service.proto.FEP;
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Model.ConfigFile;
import org.totalgrid.reef.examples.protocol.basic.library.ExternalProtocol;
import org.totalgrid.reef.protocol.api.ProtocolManager;
import org.totalgrid.reef.protocol.api.ProtocolResources;
import org.totalgrid.reef.protocol.api.ProtocolResourcesFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ProtocolAdapter implements ProtocolManager {



    Map<String, ExternalProtocol> endpointMap = new HashMap<String, ExternalProtocol>();

    @Override
    public CommandRequestHandler addEndpoint(Client client, FEP.EndpointConnection endpointConnection) {

        String endpointName = endpointConnection.getEndpoint().getName();

        System.out.println("Adding endpoint: " + endpointName);

        ExternalProtocol protocol = new ExternalProtocol();

        AllScadaService service = client.getService(AllScadaService.class);

        String routingKey = endpointConnection.getRouting().getServiceRoutingKey();

        UpdateAdapter updateAdapter = new UpdateAdapter(service, routingKey);

        CommandAdapter commandAdapter = new CommandAdapter(protocol);

        endpointMap.put(endpointName, protocol);

        protocol.connect(updateAdapter);

        //service.alterCommunicationChannelState()
        try {
            service.alterEndpointConnectionState(endpointConnection.getId(), FEP.EndpointConnection.State.COMMS_UP);
        } catch (ReefServiceException ex) {
            System.out.println("Couldn't update endpoint connection state. " + ex);
        }

        return commandAdapter;
    }

    @Override
    public void removeEndpoint(FEP.EndpointConnection endpointConnection) {

        String endpointName = endpointConnection.getEndpoint().getName();

        System.out.println("Removing endpoint: " + endpointName);

        ExternalProtocol protocol = endpointMap.get(endpointName);

        protocol.disconnect();

        endpointMap.remove(endpointName);
    }

}
