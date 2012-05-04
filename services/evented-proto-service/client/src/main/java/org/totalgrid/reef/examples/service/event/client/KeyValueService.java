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
package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.Promise;
import org.totalgrid.reef.client.SubscriptionResult;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.List;

/**
 * Provides an Java-idiom, RPC-like method for clients to make service calls.
 */
public interface KeyValueService {

    /**
     * Get a single value by name
     *
     * @param key Key of the key-value pair
     * @return The key-value pair
     * @throws ReefServiceException
     */
    Promise<KeyValue> getValue(String key) throws ReefServiceException;

    /**
     * Get all key-value pairs
     *
     * @return The list of key-value pairs
     * @throws ReefServiceException
     */
    Promise<List<KeyValue>> getAllValues() throws ReefServiceException;

    /**
     * Put a key-value pair, adding if one doesn't exist already, modifying otherwise
     *
     * @param key Key of the key-value pair
     * @param value Value of the key-value pair
     * @return The key-value pair that was put by the service
     * @throws ReefServiceException
     */
    Promise<KeyValue> putValue(String key, String value) throws ReefServiceException;

    /**
     * Delete a specific key-value pair by specifying the key
     *
     * @param key Key of the key-value pair
     * @throws ReefServiceException
     */
    Promise<KeyValue> deleteValue(String key) throws ReefServiceException;

    /**
     * Delete all entries in the system
     *
     * @throws ReefServiceException
     */
    Promise<List<KeyValue>> deleteAllValues() throws ReefServiceException;

    /**
     * Subscribe to all subscription events associated with all KeyValue service objects
     *
     * @return Contains immediate results to the query as well as subscription management object
     * @throws ReefServiceException
     */
    Promise<SubscriptionResult<List<KeyValue>, KeyValue>> subscribeToAllKeyValues() throws ReefServiceException;

    /**
     * Subscribe to subscription events associated with a specific KeyValue service object (by key)
     *
     * @param key Key of the key-value pair
     * @return Contains immediate results to the query as well as subscription management object
     * @throws ReefServiceException
     */
    Promise<SubscriptionResult<KeyValue, KeyValue>> subscribeToKeyValues(String key) throws ReefServiceException;

}
