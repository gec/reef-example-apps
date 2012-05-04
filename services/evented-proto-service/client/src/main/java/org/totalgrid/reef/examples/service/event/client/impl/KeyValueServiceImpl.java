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
package org.totalgrid.reef.examples.service.event.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Promise;
import org.totalgrid.reef.client.SubscriptionBinding;
import org.totalgrid.reef.client.SubscriptionResult;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.operations.BasicRequest;
import org.totalgrid.reef.client.operations.CommonResponseTransformations;
import org.totalgrid.reef.client.operations.RestOperations;
import org.totalgrid.reef.client.operations.SubscriptionBindingRequest;
import org.totalgrid.reef.examples.service.event.client.KeyValueDescriptor;
import org.totalgrid.reef.examples.service.event.client.KeyValueService;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the KeyValueService interface.
 *
 * Uses a Client interface to translate RPC calls into REST
 * service calls.
 *
 */
public class KeyValueServiceImpl implements KeyValueService {

    private final Client client;

    public KeyValueServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * Get a particular key-value, implemented with the "GET" verb
     *
     * @param key Key of the key-value pair
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Promise<KeyValue> getValue(final String key) throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<KeyValue>() {
            @Override
            public String errorMessage() {
                return "Cannot get value with key: " + key;
            }

            @Override
            public Promise<KeyValue> execute(RestOperations operations) {
                // Request is a KeyValue with the key filled in as the special "*" character
                KeyValue request = KeyValue.newBuilder().setKey(key).build();

                return CommonResponseTransformations.one(operations.get(request));
            }
        });
    }

    /**
     * Get all key-value pairs, implemented with the "GET" verb
     *
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Promise<List<KeyValue>> getAllValues() throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<List<KeyValue>>() {
            @Override
            public String errorMessage() {
                return "Cannot get all values";
            }

            @Override
            public Promise<List<KeyValue>> execute(RestOperations operations) {
                // Request is a KeyValue with the key filled in as the special "*" character
                KeyValue request = KeyValue.newBuilder().setKey("*").build();

                return CommonResponseTransformations.many(operations.get(request));
            }
        });
    }

    /**
     * Get a specific set of key-value pairs, implemented as a "scatter gather" query
     */
    @Override
    public Promise<List<KeyValue>> getValues(final List<String> keys) throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<List<KeyValue>>() {
            @Override
            public String errorMessage() {
                return "Cannot get all " + keys.toString();
            }

            @Override
            public Promise<List<KeyValue>> execute(RestOperations operations) {

                List<Promise<KeyValue>> individualPromises = new ArrayList<Promise<KeyValue>>(keys.size());

                for(String key : keys){
                    // Request is a KeyValue with the key filled in as the special "*" character
                    KeyValue request = KeyValue.newBuilder().setKey(key).build();
                    Promise<KeyValue> subPromise = CommonResponseTransformations.one(operations.get(request));
                    individualPromises.add(subPromise);
                }

                return CommonResponseTransformations.collatePromises(client.getInternal().getExecutor(), individualPromises);
            }
        });
    }

    /**
     * Add/modify a key/value pair, implemented with the "PUT" verb
     *
     * @param key Key of the key-value pair
     * @param value Value of the key-value pair
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Promise<KeyValue> putValue(final String key, final String value) throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<KeyValue>() {
            @Override
            public String errorMessage() {
                return "Cannot put key: " + key + " value: " + value;
            }

            @Override
            public Promise<KeyValue> execute(RestOperations operations) {
                // Request is a KeyValue with both fields filled in
                KeyValue request = KeyValue.newBuilder().setKey(key).setValue(value).build();

                return CommonResponseTransformations.one(operations.put(request));
            }
        });
    }

    /**
     * Delete a key/value pair, implemeted with "DELETE" verb
     *
     * @param key Key of the key-value pair
     * @throws ReefServiceException
     */
    @Override
    public Promise<KeyValue> deleteValue(final String key) throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<KeyValue>() {
            @Override
            public String errorMessage() {
                return "Cannot get value with key: " + key;
            }

            @Override
            public Promise<KeyValue> execute(RestOperations operations) {
                // Request is a KeyValue with the key filled in as the special "*" character
                KeyValue request = KeyValue.newBuilder().setKey(key).build();

                return CommonResponseTransformations.one(operations.delete(request));
            }
        });
    }

    /**
     * Delete all key/value pairs, implemeted with "DELETE" verb
     *
     * @throws ReefServiceException
     */
    @Override
    public Promise<List<KeyValue>> deleteAllValues() throws ReefServiceException {

        return client.getServiceOperations().request(new BasicRequest<List<KeyValue>>() {
            @Override
            public String errorMessage() {
                return "Cannot delete all values";
            }

            @Override
            public Promise<List<KeyValue>> execute(RestOperations operations) {
                // Request is a KeyValue with the key filled in as the special "*" character
                KeyValue request = KeyValue.newBuilder().setKey("*").build();

                return CommonResponseTransformations.many(operations.delete(request));
            }
        });
    }

    /**
     * Subscribe to all key/value pair updates
     *
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Promise<SubscriptionResult<List<KeyValue>, KeyValue>> subscribeToAllKeyValues() throws ReefServiceException {

        return client.getServiceOperations().subscriptionRequest(new KeyValueDescriptor(), new SubscriptionBindingRequest<List<KeyValue>>() {
            @Override
            public String errorMessage() {
                return "Cannot subscribe to all values";
            }

            @Override
            public Promise<List<KeyValue>> execute(SubscriptionBinding subscription, RestOperations operations) {
                // Request is a KeyValue with the key filled in as the special "*" character
                KeyValue request = KeyValue.newBuilder().setKey("*").build();

                return CommonResponseTransformations.many(operations.get(request, subscription));
            }
        });
    }

    /**
     * Subscribe to key/value pair updates with a particular key
     *
     * @param key Key of the key-value pair
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Promise<SubscriptionResult<KeyValue, KeyValue>> subscribeToKeyValues(final String key) throws ReefServiceException {

        return client.getServiceOperations().subscriptionRequest(new KeyValueDescriptor(), new SubscriptionBindingRequest<KeyValue>() {
            @Override
            public String errorMessage() {
                return "Cannot subscribe to key: " + key;
            }

            @Override
            public Promise<KeyValue> execute(SubscriptionBinding subscription, RestOperations operations) {
                // Request is a KeyValue with the key filled in
                KeyValue request = KeyValue.newBuilder().setKey(key).build();

                return CommonResponseTransformations.one(operations.get(request, subscription));
            }
        });
    }
}
