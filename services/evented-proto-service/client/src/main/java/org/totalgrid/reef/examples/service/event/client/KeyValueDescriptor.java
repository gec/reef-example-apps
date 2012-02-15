package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.io.IOException;

public class KeyValueDescriptor implements TypeDescriptor<KeyValue> {

    @Override
    public byte[] serialize(KeyValue value) {
        return value.toByteArray();
    }

    @Override
    public KeyValue deserialize(byte[] bytes) throws IOException {
        return KeyValue.parseFrom(bytes);
    }

    @Override
    public Class<KeyValue> getKlass() {
        return KeyValue.class;
    }

    @Override
    public String id() {
        return "key_value";
    }
}
