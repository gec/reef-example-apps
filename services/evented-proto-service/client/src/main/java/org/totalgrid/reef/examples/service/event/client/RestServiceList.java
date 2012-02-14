package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.ServiceProviderInfo;
import org.totalgrid.reef.client.ServicesList;
import org.totalgrid.reef.client.registration.BasicServiceProviderInfo;
import org.totalgrid.reef.client.registration.BasicServiceTypeInformation;
import org.totalgrid.reef.client.types.ServiceTypeInformation;
import org.totalgrid.reef.examples.service.event.client.impl.RestServiceFactory;

import java.util.ArrayList;
import java.util.List;

public class RestServiceList implements ServicesList {

    @Override
    public List<ServiceTypeInformation<?, ?>> getServiceTypeInformation() {
        List<ServiceTypeInformation<?, ?>> typeList = new ArrayList<ServiceTypeInformation<?, ?>>();

        typeList.add(new BasicServiceTypeInformation(new RestMessageDescriptor(), new RestMessageDescriptor()));

        return typeList;
    }

    @Override
    public List<ServiceProviderInfo> getServiceProviders() {
        List<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

        list.add(new BasicServiceProviderInfo(new RestServiceFactory(), RestService.class));

        return list;
    }
}
