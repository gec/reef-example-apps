package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.RestMessage;

import java.io.IOException;

public class RestMessageDescriptor implements TypeDescriptor<RestMessage> {

    @Override
    public byte[] serialize(RestMessage value) {
        return value.toByteArray();
    }

    @Override
    public RestMessage deserialize(byte[] bytes) throws IOException {
        return RestMessage.parseFrom(bytes);
    }

    @Override
    public Class<RestMessage> getKlass() {
        return RestMessage.class;
    }

    @Override
    public String id() {
        return "rest_message";
    }
}
