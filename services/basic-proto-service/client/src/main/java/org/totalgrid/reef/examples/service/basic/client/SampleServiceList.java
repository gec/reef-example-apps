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

/**
 * ServiceList implementation for sample service messages.
 *
 * Provides service clients SampleMessageDescriptors and factories to build implementations
 * of the SampleService interface.
 */
public class SampleServiceList implements ServicesList {

    /**
     * Exposes ServiceTypeInformation for the sample service message
     *
     * @return ServiceTypeInformation used to route service requests
     */
    @Override
    public List<ServiceTypeInformation<?, ?>> getServiceTypeInformation() {
        List<ServiceTypeInformation<?, ?>> typeList = new ArrayList<ServiceTypeInformation<?, ?>>();

        // Build type information with SampleMessageDescriptor for both request/response and subscription event types
        // (They might theoretically be different, i.e. MeasurementSnapshot -> Measurement)
        typeList.add(new BasicServiceTypeInformation(new SampleMessageDescriptor(), new SampleMessageDescriptor()));

        return typeList;
    }

    /**
     * Provides a factory to build impls of sample service client interfaces.
     *
     * @return
     */
    @Override
    public List<ServiceProviderInfo> getServiceProviders() {
        List<ServiceProviderInfo> list = new ArrayList<ServiceProviderInfo>();

        // Build provider info with SampleServiceFactory, specify that it builds the SampleService class
        list.add(new BasicServiceProviderInfo(new SampleServiceFactory(), SampleService.class));

        return list;
    }
}