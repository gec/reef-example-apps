package org.totalgrid.reef.examples.service.basic.service;

import org.totalgrid.reef.client.proto.Envelope;
import org.totalgrid.reef.client.registration.Service;
import org.totalgrid.reef.client.registration.ServiceResponseCallback;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample;

import java.util.List;
import java.util.Map;

public class SampleService implements Service {

    @Override
    public void respond(Envelope.ServiceRequest request, Map<String, List<String>> headers, ServiceResponseCallback callback) {

        Sample.SampleMessage message = Sample.SampleMessage.newBuilder().setContent("response").build();
        
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
        b.setId(request.getId());
        b.setStatus(Envelope.Status.OK);
        b.addPayload(message.toByteString());
        
        callback.onResponse(b.build());
    }
}
