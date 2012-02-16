package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.ServiceProviderInfo;
import org.totalgrid.reef.client.ServicesList;
import org.totalgrid.reef.client.registration.BasicServiceProviderInfo;
import org.totalgrid.reef.client.registration.BasicServiceTypeInformation;
import org.totalgrid.reef.client.types.ServiceTypeInformation;
import org.totalgrid.reef.examples.service.event.client.impl.KeyValueServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * ServiceList implementation for KeyValue service messages.
 *
 * Provides service clients KeyValueDescriptors and factories to build implementations
 * of the KeyValueService interface.
 */
public class KeyValueServiceList implements ServicesList {

    /**
     * Exposes ServiceTypeInformation for KeyValue service message
     *
     * @return ServiceTypeInformation used to route service requests
     */
    @Override
    public List<ServiceTypeInformation<?, ?>> getServiceTypeInformation() {
        List<ServiceTypeInformation<?, ?>> typeList = new ArrayList<ServiceTypeInformation<?, ?>>();

        // Build type information with KeyValueDescriptor for both request/response and subscription event types
        // (They might theoretically be different, i.e. MeasurementSnapshot -> Measurement)
        typeList.add(new BasicServiceTypeInformation(new KeyValueDescriptor(), new KeyValueDescriptor()));

        return typeList;
    }

    /**
     * Provides a factory to build impls of KeyValueService client interfaces.
     *
     * @return
     */
    @Override
    public List<ServiceProviderInfo> getServiceProviders() {
        List<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

        // Build provider info with KeyValueServiceFactory, specify that it builds the KeyValueService class
        list.add(new BasicServiceProviderInfo(new KeyValueServiceFactory(), KeyValueService.class));

        return list;
    }
}
