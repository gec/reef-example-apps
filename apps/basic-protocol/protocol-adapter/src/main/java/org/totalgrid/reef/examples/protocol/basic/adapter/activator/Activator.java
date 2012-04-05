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
package org.totalgrid.reef.examples.protocol.basic.adapter.activator;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.totalgrid.reef.examples.protocol.basic.adapter.ProtocolAdapter;
import org.totalgrid.reef.protocol.api.ProtocolManager;

import java.util.Properties;

public class Activator implements BundleActivator {

    private ServiceRegistration registration;

    @Override
    public void start(BundleContext bundleContext) throws Exception {

        ProtocolAdapter protocolManager = new ProtocolAdapter();
        
        Properties properties = new Properties();
        properties.put("protocol", "ExternalProtocol");

        registration = bundleContext.registerService(ProtocolManager.class.getName(), protocolManager, properties);
    }

    @Override
    public void stop(BundleContext bundleContext) throws Exception {

        if (registration != null) {
            registration.unregister();
        }
    }
}
