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
import org.totalgrid.reef.client.Promise;
import org.totalgrid.reef.client.PromiseTransform;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.operations.BasicRequest;
import org.totalgrid.reef.client.operations.CommonResponseTransformations;
import org.totalgrid.reef.client.operations.RestOperations;
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
     * Send sample message request, implemented using "GET" verb. Only a single response is expected
     */
    @Override
    public Promise<Sample.SampleMessage> sendRequest(final Sample.SampleMessage request) throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<Sample.SampleMessage>() {
            @Override
            public Promise<Sample.SampleMessage> execute(RestOperations operations) {
                return CommonResponseTransformations.one(operations.get(request));
            }

            @Override
            public String errorMessage() {
                return "Couldn't get sample message";
            }
        });
    }

    /**
     * Send sample message request and directly extract the content string (showing utility of promise transforms)
     */
    @Override
    public Promise<String> sendAndGetContent(String initialString, boolean causeError) throws ReefServiceException {

        final Sample.SampleMessage request = Sample.SampleMessage.newBuilder().setContent(initialString).build();

        return sendRequest(request).transform(new PromiseTransform<Sample.SampleMessage, String>() {
            @Override
            public String transform(Sample.SampleMessage value) throws ReefServiceException {
                return value.getContent();
            }
        });
    }
}
