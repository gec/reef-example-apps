package org.totalgrid.reef.examples.service.basic.client;

import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample.SampleMessage;

import java.io.IOException;

/**
 * SampleMessage type descriptor, tells clients/services how to serialize, deserialize and route
 * SampleMessage messages
 */
public class SampleMessageDescriptor implements TypeDescriptor<SampleMessage> {

    /**
     * Serialize the proto object to a byte array
     *
     * @param sampleMessage
     * @return
     */
    @Override
    public byte[] serialize(SampleMessage sampleMessage) {
        return sampleMessage.toByteArray();
    }

    /**
     * Deserialize a byte array into a proto object
     *
     * @param bytes
     * @return
     * @throws IOException
     */
    @Override
    public SampleMessage deserialize(byte[] bytes) throws IOException {
        return SampleMessage.parseFrom(bytes);
    }

    /**
     * Class of the SampleMessage proto message
     *
     * @return
     */
    @Override
    public Class<SampleMessage> getKlass() {
        return SampleMessage.class;
    }

    /**
     * Used to declare the AMQP exchange for request/responses
     *
     * @return
     */
    @Override
    public String id() {
        return "sample_message";
    }
}
