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
