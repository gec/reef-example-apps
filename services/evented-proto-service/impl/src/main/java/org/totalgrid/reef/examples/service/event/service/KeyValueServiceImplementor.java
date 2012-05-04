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
package org.totalgrid.reef.examples.service.event.service;

import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.proto.Envelope;
import org.totalgrid.reef.client.registration.EventPublisher;
import org.totalgrid.reef.client.registration.Service;
import org.totalgrid.reef.client.registration.ServiceResponseCallback;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service implementation for a simple key-value store. An in-memory map is used instead of
 * a database.
 *
 * The service is implemented in terms of the RESTful get/put/post/delete verbs, and changes to
 * key value objects are published as service subscription events.
 */
public class KeyValueServiceImplementor implements Service {

    private final ConcurrentMap<String, String> map = new ConcurrentHashMap<String, String>();

    private final EventPublisher publisher;

    /**
     * @param publisher Interface for publishing/binding subscriptions for events.
     */
    public KeyValueServiceImplementor(EventPublisher publisher) {
        this.publisher = publisher;
    }

    /**
     * Implements the "GET" verb. Returns a specific key-value pair or all of them.
     *
     * @param message Request message.
     * @param id ID that correlates request and response message.
     * @param callback Used to respond to the request.
     */
    private void doGet(KeyValue message, String id, ServiceResponseCallback callback) {

        // Create a ServiceResponse to send when finished processing
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();

        // Set the correlation id on the response to the same from the request
        b.setId(id);

        if (!message.hasKey()) {

            // Must have a key; a BAD_REQUEST error with an appropriate message
            b.setStatus(Envelope.Status.BAD_REQUEST);
            b.setErrorMessage("Must include key in get request");

        } else if (message.getKey().equals("*")) {

            // Got the special key "*", go through all entries in the map and collect them
            for (Map.Entry<String, String> entry : map.entrySet()) {

                // Build the message from the map entry
                KeyValue msg = KeyValue.newBuilder().setKey(entry.getKey()).setValue(entry.getValue()).build();

                // Add to the list of messages returned by the request
                b.addPayload(msg.toByteString());
            }

            // Return "OK" response status
            b.setStatus(Envelope.Status.OK);

        } else {

            // Get (possible) entry out of the map using the specified key
            String value = map.get(message.getKey());

            // If the value exists, we return it
            if (value != null) {

                // Build the message from the map entry
                KeyValue msg = KeyValue.newBuilder().setKey(message.getKey()).setValue(value).build();

                // Add to the list of messages returned by the request
                b.addPayload(msg.toByteString());
            }

            // Return "OK" response status even if nothing is found
            b.setStatus(Envelope.Status.OK);
        }

        // Send the response message
        callback.onResponse(b.build());
    }

    /**
     * Implements the "PUT" verb. If key doesn't exist, creates a new entry and sends an ADDED event. If
     * key already exists, replaces the value and sends a MODIFIED event.
     *
     * @param message Request message.
     * @param id ID that correlates request and response message.
     * @param callback Used to respond to the request.
     */
    private void doPut(KeyValue message, String id, ServiceResponseCallback callback) {

        // Create a ServiceResponse to send when finished processing
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();

        // Set the correlation id on the response to the same from the request
        b.setId(id);

        if (!message.hasKey() || !message.hasValue()) {

            // Must have a key; a BAD_REQUEST error with an appropriate message
            b.setStatus(Envelope.Status.BAD_REQUEST);
            b.setErrorMessage("Must include key and value in put request");

        } else {

            // Put key-value pair into map, getting the (possible) previous value
            String previous = map.put(message.getKey(), message.getValue());

            // Build the message from the map entry
            KeyValue msg = KeyValue.newBuilder().setKey(message.getKey()).setValue(message.getValue()).build();

            // Add to the list of messages returned by the request
            b.addPayload(msg.toByteString());

            if (previous == null) {

                // No previous message, return "CREATED" response status
                b.setStatus(Envelope.Status.CREATED);

                // Publish a "ADDED" event to notify subscribers the object is created
                publisher.publishEvent(Envelope.SubscriptionEventType.ADDED, msg, msg.getKey());

            } else {

                // The was a previous message, return "UPDATED" response status
                b.setStatus(Envelope.Status.UPDATED);

                // Publish a "MODIFIED" event to notify subscribers the object is created
                publisher.publishEvent(Envelope.SubscriptionEventType.MODIFIED, msg, msg.getKey());
            }

        }

        // Send the response message
        callback.onResponse(b.build());
    }

    /**
     * Stub implementation of "POST" verb.
     *
     * @param message Request message.
     * @param id ID that correlates request and response message.
     * @param callback Used to respond to the request.
     */
    private void doPost(KeyValue message, String id, ServiceResponseCallback callback) {

        // Create a ServiceResponse to send when finished processing
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();

        // Set the correlation id on the response to the same from the request
        b.setId(id);

        // Not implementing this verb, return bad request and an error message
        b.setStatus(Envelope.Status.BAD_REQUEST);
        b.setErrorMessage("post verb not implemented");

        // Send the response message
        callback.onResponse(b.build());
    }

    /**
     * Implements the "DELETE" verb. Deletes a specific key-value pair or all of them. All
     * deleted pairs will be published as REMOVED events.
     *
     * @param message Request message.
     * @param id ID that correlates request and response message.
     * @param callback Used to respond to the request.
     */
    private void doDelete(KeyValue message, String id, ServiceResponseCallback callback) {

        // Create a ServiceResponse to send when finished processing
        Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();

        // Set the correlation id on the response to the same from the request
        b.setId(id);

        if (!message.hasKey()) {

            // Must have a key; a BAD_REQUEST error with an appropriate message
            b.setStatus(Envelope.Status.BAD_REQUEST);
            b.setErrorMessage("Must include key in delete request");

        } else if (message.getKey().equals("*")) {

            // Got the special key "*", go through all entries in the map and collect them
            for (Map.Entry<String, String> entry : map.entrySet()) {

                // Build the message from the map entry
                KeyValue msg = KeyValue.newBuilder().setKey(entry.getKey()).setValue(entry.getValue()).build();

                // Add to the list of messages returned by the request
                b.addPayload(msg.toByteString());

                // Publish a "REMOVED" event to notify subscribers the object is deleted
                publisher.publishEvent(Envelope.SubscriptionEventType.REMOVED, msg, msg.getKey());

            }

            // Return the "DELETED" status along with the list of deleted entries
            b.setStatus(Envelope.Status.DELETED);

            // Clear the actual set of entries
            map.clear();

        } else {

            // Remove the specific map entry
            String value = map.remove(message.getKey());

            if (value != null) {

                // Build the message from the map entry
                KeyValue msg = KeyValue.newBuilder().setKey(message.getKey()).setValue(value).build();

                // Add the message returned by the request
                b.addPayload(msg.toByteString());

                // Return the "DELETED" status along with the deleted entry
                b.setStatus(Envelope.Status.DELETED);

                // Publish a "REMOVED" event to notify subscribers the object is deleted
                publisher.publishEvent(Envelope.SubscriptionEventType.REMOVED, msg, msg.getKey());

            } else {

                // Error - cannot find key, send BAD_REQUEST error with an appropriate message
                b.setStatus(Envelope.Status.BAD_REQUEST);
                b.setErrorMessage("Cannot delete nonexisting entry");
            }
        }

        // Send the response message
        callback.onResponse(b.build());
    }


    /**
     * Checks whether requests also include a subscription, binds the subscription queue to the
     * event stream.
     *
     * @param message Request message, used to determine what event filters to subscribe to.
     * @param headers Headers, used most importantly to get the subscription queue name.
     */
    private void handleSubscription(KeyValue message, Map<String, List<String>> headers) {

        // "SUB_QUEUE_NAME" is used by clients to specify their subscription queue
        List<String> queueList = headers.get("SUB_QUEUE_NAME");

        // If no subscription queue is specified, do nothing
        if (queueList == null || queueList.size() < 1) {
            return;
        }
        
        String subQueue = queueList.get(0);

        // Use event interface to bind client's queue to event stream
        publisher.bindQueueByClass(subQueue, message.getKey(), KeyValue.class);
    }

    /**
     * Entry point for request handling.
     *
     * @param request Service request, which includes verb, headers, payload, and correlation id.
     * @param headers Service headers for extra request information.
     * @param callback Used to respond to requests asynchronously.
     */
    @Override
    public void respond(Envelope.ServiceRequest request, Map<String, List<String>> headers, ServiceResponseCallback callback) {

        // Catch any exceptions and respond with an error
        try {

            // Parse the request message payload into a KeyValue proto object
            KeyValue message = KeyValue.parseFrom(request.getPayload());

            // Handle a subscription request (if present)
            handleSubscription(message, headers);

            // Handle the request depending on which verb was used
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

            // Build a ServiceResponse with an error message
            Envelope.ServiceResponse.Builder b = Envelope.ServiceResponse.newBuilder();
            b.setId(request.getId());
            b.setStatus(Envelope.Status.INTERNAL_ERROR);
            b.setErrorMessage(ex.toString());
            callback.onResponse(b.build());
        }
    }
    

}
