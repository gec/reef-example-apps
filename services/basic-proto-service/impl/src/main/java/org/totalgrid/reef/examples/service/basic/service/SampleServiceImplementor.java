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
package org.totalgrid.reef.examples.service.basic.service;

import org.totalgrid.reef.client.proto.Envelope;
import org.totalgrid.reef.client.registration.Service;
import org.totalgrid.reef.client.registration.ServiceResponseCallback;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample;

import java.util.List;
import java.util.Map;

/**
 * Sample service implementation, returns a canned response message just to demonstrate connectivity.
 */
public class SampleServiceImplementor implements Service {

    @Override
    public void respond(Envelope.ServiceRequest request, Map<String, List<String>> headers, ServiceResponseCallback callback) {

        // Build the ServiceReponse envelope
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();

        // Response id must match request id
        b.setId(request.getId());

        prepareResponse(request, b);

        // Send the response
        callback.onResponse(b.build());
    }

    /**
     * parse the request object out of the envelope and construct the response
     */
    private void prepareResponse(Envelope.ServiceRequest request, Envelope.ServiceResponse.Builder b) {
        try{
            Sample.SampleMessage messageRequest = Sample.SampleMessage.parseFrom(request.getPayload());
            // if they have asked for an error, send them the content string as an error
            if(messageRequest.hasReturnError() && messageRequest.getReturnError()){
                b.setStatus(Envelope.Status.BAD_REQUEST);
                b.setErrorMessage(messageRequest.getContent());
            }else{
                // if not a
                Sample.SampleMessage responsePayload = Sample.SampleMessage.newBuilder().setContent(messageRequest.getContent()).build();
                // Return an "OK" status
                b.setStatus(Envelope.Status.OK);
                // Add message payload to response envelope
                b.addPayload(responsePayload.toByteString());
            }
        }catch(Exception e){
            b.setStatus(Envelope.Status.INTERNAL_ERROR);
            b.setErrorMessage(e.getMessage());
        }
    }
}
