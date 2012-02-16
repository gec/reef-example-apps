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
public class SampleService implements Service {

    @Override
    public void respond(Envelope.ServiceRequest request, Map<String, List<String>> headers, ServiceResponseCallback callback) {

        // Build a canned response message
        Sample.SampleMessage message = Sample.SampleMessage.newBuilder().setContent("response").build();

        // Build the ServiceReponse envelope
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();

        // Response id must match request id
        b.setId(request.getId());

        // Return an "OK" status
        b.setStatus(Envelope.Status.OK);

        // Add message payload to response envelope
        b.addPayload(message.toByteString());

        // Send the response
        callback.onResponse(b.build());
    }
}
