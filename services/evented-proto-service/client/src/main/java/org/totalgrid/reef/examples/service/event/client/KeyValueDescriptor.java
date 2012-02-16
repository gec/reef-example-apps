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

import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.io.IOException;

/**
 * KeyValue type descriptor, tells clients/services how to serialize, deserialize and route
 * KeyValue messages
 */
public class KeyValueDescriptor implements TypeDescriptor<KeyValue> {

    /**
     * Serialize the proto object to a byte array
     *
     * @param value
     * @return
     */
    @Override
    public byte[] serialize(KeyValue value) {
        return value.toByteArray();
    }

    /**
     * Deserialize a byte array into a proto object
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    @Override
    public KeyValue deserialize(byte[] bytes) throws IOException {
        return KeyValue.parseFrom(bytes);
    }

    /**
     * Class of the KeyValue proto message
     *
     * @return
     */
    @Override
    public Class<KeyValue> getKlass() {
        return KeyValue.class;
    }

    /**
     * Used to declare the AMQP exchange for request/responses
     *
     * @return
     */
    @Override
    public String id() {
        return "key_value";
    }
}
