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

/**
 * Protocol adapter that implements the ProtocolManager interface to provide the Reef FEP
 * subsystem with a wrapper for an external protocol implementation.
 */
public class ProtocolAdapter implements ProtocolManager {

    private final Map<String, ProtocolInstance> endpointMap = new HashMap<String, ProtocolInstance>();

    /**
     * Called by the FEP to notify the protocol manager that it should make a protocol connection to the
     * specified endpoint.
     *
     * @param client Client logged in with protocol permissions
     * @param endpointConnection Description of the endpoint connection, also includes endpoint configuration and config files
     * @return
     */
    @Override
    public CommandRequestHandler addEndpoint(Client client, FEP.EndpointConnection endpointConnection) {

        // Acquire a utility class for protocol-related operations
        ProtocolResources resources = ProtocolResourcesFactory.buildResources(client, endpointConnection);

        String endpointName = resources.getEndpointName();

        System.out.println("Adding endpoint: " + endpointName);

        // Load the configuration for the protocol from the EndpointConnection's associated ConfigFile object
        String measurementName = loadMeasurementName(resources);

        // Create a protocol implementation
        ExternalProtocol protocol = new ExternalProtocol(measurementName);

        // Create the adapter that publishes measurements
        UpdateAdapter updateAdapter = new UpdateAdapter(resources);

        // Create the adapter that handles command requests
        CommandAdapter commandAdapter = new CommandAdapter(protocol);

        // Store the protocol and configuration to handle removes
        endpointMap.put(endpointName, new ProtocolInstance(protocol, resources));

        // Attempt a connection (begin publishing measurements)
        protocol.connect(updateAdapter);

        // Inform the system that this endpoint is now online
        try {
            resources.setCommsState(FEP.EndpointConnection.State.COMMS_UP);
        } catch (ReefServiceException ex) {
            System.out.println("Couldn't update endpoint connection state. " + ex);
        }

        // Provide the FEP with a callback for issuing commands
        return commandAdapter;
    }

    /**
     * Called by the FEP to notify the protocol manager it needs to shut down the endpoint.
     *
     * @param endpointConnection Description of the endpoint connection, also includes endpoint configuration and config files
     */
    @Override
    public void removeEndpoint(FEP.EndpointConnection endpointConnection) {

        String endpointName = endpointConnection.getEndpoint().getName();

        System.out.println("Removing endpoint: " + endpointName);

        // Retrieves previously added protocol implementation
        ProtocolInstance instance = endpointMap.get(endpointName);

        ExternalProtocol protocol = instance.getProtocol();

        // Disconnects protocol (stops publishing measurements)
        protocol.disconnect();

        // Inform the system that this endpoint is now offline
        try {
            instance.getResources().setCommsState(FEP.EndpointConnection.State.COMMS_DOWN);
        } catch (ReefServiceException ex) {
            System.out.println("Couldn't update endpoint connection state. " + ex);
        }

        // Removes protocol from list
        endpointMap.remove(endpointName);
    }

    private String loadMeasurementName(ProtocolResources resources) {
        String name;
        try {
            // Find config file specified in the XML configuration file
            ConfigFile config = resources.getConfigFile("text/properties");

            // We're looking for content that looks like a=b, b is the measurement name
            if (config != null) {
                String raw = config.getFile().toStringUtf8().trim();
                name = raw.split("=")[1].trim();
            } else {
                name = "unknown";
            }

        } catch (ReefServiceException ex) {
            name = "unknown";
            System.out.println("Could not load measurement name: " + ex);
        }
        return name;
    }


    /**
     * Helper class for holding protocol/resource pairs
     */
    static class ProtocolInstance {
        private final ExternalProtocol protocol;
        private final ProtocolResources resources;

        ProtocolInstance(ExternalProtocol protocol, ProtocolResources resources) {
            this.protocol = protocol;
            this.resources = resources;
        }

        public ExternalProtocol getProtocol() {
            return protocol;
        }

        public ProtocolResources getResources() {
            return resources;
        }
    }


}
