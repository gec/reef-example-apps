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
