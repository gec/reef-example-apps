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
package org.totalgrid.reef.examples.service.basic.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.ClientOperations;
import org.totalgrid.reef.examples.service.basic.client.SampleService;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample;

/**
 * Implementation of SampleService interface.
 *
 * Uses a Client interface to translate RPC calls into REST
 * service calls.
 *
 */
public class SampleServiceImpl implements SampleService {
    
    private final Client client;
    
    public SampleServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * Send sample message request, implemented using "GET" verb
     *
     * @param request
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Sample.SampleMessage sendRequest(Sample.SampleMessage request) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        return operations.getOne(request);
    }
}
