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
package org.totalgrid.reef.examples.applications;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.ApplicationService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Application.ApplicationConfig;

import java.util.List;

/**
 * Example: Applications
 *
 *
 */
public class ApplicationsExample {

    /**
     * Get Applications
     *
     * Retrieves a list of applications registered in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getApplications(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Applications ===\n\n");

        // Get service interface for applications
        ApplicationService applicationService = client.getService(ApplicationService.class);

        // Get list of registered applications
        List<ApplicationConfig> applicationConfigList = applicationService.getApplications();

        // Inspect first ApplicationConfig object
        ApplicationConfig applicationConfig = applicationConfigList.get(0);

        // Display properties of ApplicationConfig
        System.out.println("ApplicationConfig");
        System.out.println("-----------");
        System.out.println("Uuid: " + applicationConfig.getUuid().getValue());
        System.out.println("Instance Name: " + applicationConfig.getInstanceName());
        System.out.println("Capabilities: " + applicationConfig.getCapabilitesList());
        System.out.println("Location: " + applicationConfig.getLocation());
        System.out.println("Network: " + applicationConfig.getNetwork());
        System.out.println("-----------\n");

        // Display list of ApplicationConfig objects
        for (ApplicationConfig appConfig : applicationConfigList) {
            System.out.println("Application: " + appConfig.getInstanceName());
        }
    }
}
