package org.totalgrid.reef.examples.service.basic.client;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.ServiceProviderFactory;
import org.totalgrid.reef.client.ServiceProviderInfo;
import org.totalgrid.reef.client.ServicesList;
import org.totalgrid.reef.client.registration.BasicServiceProviderInfo;
import org.totalgrid.reef.client.registration.BasicServiceTypeInformation;
import org.totalgrid.reef.client.types.ServiceTypeInformation;
import org.totalgrid.reef.client.types.TypeDescriptor;
import org.totalgrid.reef.examples.service.basic.client.impl.SampleServiceFactory;
import org.totalgrid.reef.examples.service.basic.client.impl.SampleServiceImpl;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample.SampleMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SampleServiceList implements ServicesList {

    @Override
    public List<ServiceTypeInformation<?, ?>> getServiceTypeInformation() {
        List<ServiceTypeInformation<?, ?>> typeList = new ArrayList<ServiceTypeInformation<?, ?>>();

        typeList.add(new BasicServiceTypeInformation(new SampleMessageDescriptor(), new SampleMessageDescriptor()));

        return typeList;
    }

    @Override
    public List<ServiceProviderInfo> getServiceProviders() {
        List<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

        list.add(new BasicServiceProviderInfo(new SampleServiceFactory(), SampleService.class));

        return list;
    }
}