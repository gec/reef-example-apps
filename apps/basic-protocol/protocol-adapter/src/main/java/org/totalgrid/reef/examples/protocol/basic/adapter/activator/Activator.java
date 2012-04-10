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

/**
 * Bundle activators are called by OSGi to initialize the component. The BundleContext
 * provides an interface to the OSGi service registry, which we will use to register
 * our protocol as a service. This informs the FEP subsystem that we are providing an
 * implementation for the given protocol name.
 */
public class Activator implements BundleActivator {

    private ServiceRegistration registration;

    /**
     * Called when the bundle is started; used to instantiate a protocol implementation
     * and expose it as an OSGi service.
     *
     * @param bundleContext Context interface provided by OSGi
     * @throws Exception
     */
    @Override
    public void start(BundleContext bundleContext) throws Exception {

        // Create protocol implementation
        ProtocolAdapter protocolManager = new ProtocolAdapter();

        // Identify our protocol by name
        Properties properties = new Properties();
        properties.put("protocol", "ExternalProtocol");

        // Register service; FEP will be watching for services of the interface ProtocolManager
        registration = bundleContext.registerService(ProtocolManager.class.getName(), protocolManager, properties);
    }

    /**
     * Called when the bundle is stopped/uninstalled; can be used to perform any cleanup/termination logic
     * for the protocol.
     *
     * @param bundleContext Context interface provided by OSGi
     * @throws Exception
     */
    @Override
    public void stop(BundleContext bundleContext) throws Exception {

        if (registration != null) {
            registration.unregister();
        }
    }
}
