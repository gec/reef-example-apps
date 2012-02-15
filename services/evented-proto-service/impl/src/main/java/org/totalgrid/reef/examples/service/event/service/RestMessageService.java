package org.totalgrid.reef.examples.service.event.service;

import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.proto.Envelope;
import org.totalgrid.reef.client.registration.EventPublisher;
import org.totalgrid.reef.client.registration.Service;
import org.totalgrid.reef.client.registration.ServiceResponseCallback;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.RestMessage;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RestMessageService implements Service {

    private final ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();

    private final EventPublisher publisher;

    public RestMessageService(EventPublisher publisher) {
        this.publisher = publisher;
    }

    private void doGet(RestMessage restMessage, String id, ServiceResponseCallback callback) {
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
        b.setId(id);

        if (!restMessage.hasKey()) {

            b.setStatus(Envelope.Status.BAD_REQUEST);
            b.setErrorMessage("Must include key in get request");

        } else if (restMessage.getKey().equals("*")) {
            
            for (Map.Entry<String, String> entry : map.entrySet()) {
                RestMessage msg = RestMessage.newBuilder().setKey(entry.getKey()).setValue(entry.getValue()).build();
                b.addPayload(msg.toByteString());
            }
            b.setStatus(Envelope.Status.OK);

        } else {

            String value = map.get(restMessage.getKey());

            if (value != null) {
                RestMessage msg = RestMessage.newBuilder().setKey(restMessage.getKey()).setValue(value).build();
                b.addPayload(msg.toByteString());
            }
            b.setStatus(Envelope.Status.OK);
        }

        callback.onResponse(b.build());
    }

    private void doPut(RestMessage restMessage, String id, ServiceResponseCallback callback) {
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
        b.setId(id);

        if (!restMessage.hasKey() || !restMessage.hasValue()) {
            b.setStatus(Envelope.Status.BAD_REQUEST);
            b.setErrorMessage("Must include key and value in put request");
        } else {

            String previous = map.put(restMessage.getKey(), restMessage.getValue());
            
            RestMessage msg = RestMessage.newBuilder().setKey(restMessage.getKey()).setValue(restMessage.getValue()).build();

            b.addPayload(msg.toByteString());

            if (previous == null) {
                b.setStatus(Envelope.Status.CREATED);
                publisher.publishEvent(Envelope.SubscriptionEventType.ADDED, msg, msg.getKey());
            } else {
                b.setStatus(Envelope.Status.UPDATED);
                publisher.publishEvent(Envelope.SubscriptionEventType.MODIFIED, msg, msg.getKey());
            }

        }

        callback.onResponse(b.build());
    }

    private void doPost(RestMessage restMessage, String id, ServiceResponseCallback callback) {
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
        b.setId(id);
        b.setStatus(Envelope.Status.BAD_REQUEST);
        b.setErrorMessage("post verb not implemented");
        callback.onResponse(b.build());
    }

    private void doDelete(RestMessage restMessage, String id, ServiceResponseCallback callback) {
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
        b.setId(id);

        if (!restMessage.hasKey()) {

            b.setStatus(Envelope.Status.BAD_REQUEST);
            b.setErrorMessage("Must include key in delete request");

        } else if (restMessage.getKey().equals("*")) {

            for (Map.Entry<String, String> entry : map.entrySet()) {
                RestMessage msg = RestMessage.newBuilder().setKey(entry.getKey()).setValue(entry.getValue()).build();
                b.addPayload(msg.toByteString());
                publisher.publishEvent(Envelope.SubscriptionEventType.REMOVED, msg, msg.getKey());
            }
            b.setStatus(Envelope.Status.DELETED);
            map.clear();

        } else {
            String value = map.remove(restMessage.getKey());

            if (value != null) {
                RestMessage msg = RestMessage.newBuilder().setKey(restMessage.getKey()).setValue(value).build();
                b.addPayload(msg.toByteString());
                b.setStatus(Envelope.Status.DELETED);
                publisher.publishEvent(Envelope.SubscriptionEventType.REMOVED, msg, msg.getKey());
            } else {

                b.setStatus(Envelope.Status.BAD_REQUEST);
                b.setErrorMessage("Cannot delete nonexisting entry");
            }
        }

        callback.onResponse(b.build());
    }


    private void handleSubscription(RestMessage message, Map<String, List<String>> headers) {

        List<String> queueList = headers.get("SUB_QUEUE_NAME");
        if (queueList == null || queueList.size() < 1) {
            return;
        }
        
        String subQueue = queueList.get(0);

        publisher.bindQueueByClass(subQueue, "*", RestMessage.class);
    }

    @Override
    public void respond(Envelope.ServiceRequest request, Map<String, List<String>> headers, ServiceResponseCallback callback) {

        try {
            RestMessage message = RestMessage.parseFrom(request.getPayload());

            handleSubscription(message, headers);

            if (request.getVerb() == Envelope.Verb.GET) {
                doGet(message, request.getId(), callback);
            } else if (request.getVerb() == Envelope.Verb.PUT) {
                doPut(message, request.getId(), callback);
            } else if (request.getVerb() == Envelope.Verb.POST) {
                doPost(message, request.getId(), callback);
            } else if (request.getVerb() == Envelope.Verb.DELETE) {
                doDelete(message, request.getId(), callback);
            }

        } catch (Exception ex) {
            Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
            b.setId(request.getId());
            b.setStatus(Envelope.Status.INTERNAL_ERROR);
            b.setErrorMessage(ex.toString());
            callback.onResponse(b.build());
        }
    }
    

}
