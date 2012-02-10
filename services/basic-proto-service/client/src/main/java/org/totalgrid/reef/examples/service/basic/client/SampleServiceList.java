package org.totalgrid.reef.examples.service.basic.client;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.ServiceProviderFactory;
import org.totalgrid.reef.client.ServiceProviderInfo;
import org.totalgrid.reef.client.ServicesList;
import org.totalgrid.reef.client.types.ServiceTypeInformation;
import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.basic.client.impl.SampleServiceImpl;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample.SampleMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleServiceList implements ServicesList {

    @Override
    public List<ServiceTypeInformation<?, ?>> getServiceTypeInformation() {
        List<ServiceTypeInformation<?, ?>> typeList = new ArrayList<ServiceTypeInformation<?, ?>>();

        typeList.add(new ServiceTypeInformation<SampleMessage, SampleMessage>() {
            @Override
            public TypeDescriptor<SampleMessage> getDescriptor() {
                return new SampleMessageDescriptor();
            }

            @Override
            public TypeDescriptor<SampleMessage> getSubscriptionDescriptor() {
                return new SampleMessageDescriptor();
            }

            @Override
            public String getEventExchange() {
                return "sample_message_events";
            }
        });

        return typeList;
    }

    @Override
    public List<ServiceProviderInfo> getServiceProviders() {
        List<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

        list.add(new ServiceProviderInfo() {
            @Override
            public ServiceProviderFactory getFactory() {
                return new ServiceProviderFactory() {
                    @Override
                    public Object createRpcProvider(Client client) {
                        return new SampleServiceImpl(client);
                    }
                };
            }

            @Override
            public List<Class<?>> getInterfacesImplemented() {
                List<Class<?>> list = new ArrayList<Class<?>>();
                list.add(SampleService.class);
                return list;
            }
        });

        return list;
    }
}