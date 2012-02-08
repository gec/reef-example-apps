package org.totalgrid.reef.examples.service.basic.client;

import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample.SampleMessage;

import java.io.IOException;

public class SampleMessageDescriptor implements TypeDescriptor<SampleMessage> {

    @Override
    public byte[] serialize(SampleMessage sampleMessage) {
        return sampleMessage.toByteArray();
    }

    @Override
    public SampleMessage deserialize(byte[] bytes) throws IOException {
        return SampleMessage.parseFrom(bytes);
    }

    @Override
    public Class<SampleMessage> getKlass() {
        return SampleMessage.class;
    }

    @Override
    public String id() {
        return "sample_message";
    }
}
