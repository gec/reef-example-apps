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


import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.proto.FEP;
import org.totalgrid.reef.client.service.proto.Model;
import org.totalgrid.reef.examples.protocol.basic.library.ExternalProtocol;
import org.totalgrid.reef.protocol.api.CommandHandler;
import org.totalgrid.reef.protocol.api.scada.ProtocolAdapter;
import org.totalgrid.reef.protocol.api.scada.Resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtocolManager implements ProtocolAdapter {

    Map<String, ExternalProtocol> endpointMap = new HashMap<String, ExternalProtocol>();

    @Override
    public String name() {
        return "ExternalProtocol";
    }

    @Override
    public CommandHandler addEndpoint(String endpoint, FEP.CommChannel channel, List<Model.ConfigFile> config, Resources resources) throws ReefServiceException {

        ExternalProtocol protocol = new ExternalProtocol();

        UpdateAdapter updateAdapter = new UpdateAdapter(resources.getMeasurementBatchPublisher());

        CommandAdapter commandAdapter = new CommandAdapter(protocol);
       
        protocol.connect(updateAdapter);

        endpointMap.put(endpoint, protocol);

        return commandAdapter;
    }

    @Override
    public void removeEndpoint(String endpoint) throws ReefServiceException {
        ExternalProtocol protocol = endpointMap.get(endpoint);
        protocol.disconnect();
        endpointMap.remove(endpoint);
    }
}
