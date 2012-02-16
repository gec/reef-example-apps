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
package org.totalgrid.reef.examples.stateoptimizer;


import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.AllScadaService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * logs into reef, initializes and authorizes a session and passes it to the StateOptimizerManager
 */
public class EntryPoint {


    public static void startup(AmqpSettings amqp, UserSettings user) {
        ConnectionFactory factory = new ReefConnectionFactory(amqp, new ReefServices());
        Connection conn = null;
        try {
            conn = factory.connect();
            Client client = conn.login(user);
            AllScadaService services = client.getService(AllScadaService.class);
            IStateOptimizer algorithm = new CapacitorSwitchingAlgorithm();

            // manager sets up timer to call algorithm.optimize on a timer
            StateOptimizerManager manager = new StateOptimizerManager(services, algorithm, 5000);

            // Run until we are done
            manager.run();
        }
        catch(ReefServiceException rse) {
            System.out.println("Error connecting or logging in: " + rse.getMessage() + ". check that Reef server is running.");
            rse.printStackTrace();
        }
        finally {
            if(conn != null) conn.disconnect();
            factory.terminate();
        }

    }

    public static void main(String[] args) throws Exception {

        if (args.length == 2) {
            AmqpSettings amqp = new AmqpSettings(args[0]);
            UserSettings user = new UserSettings(args[1]);
            startup(amqp, user);

        } else {
            System.out.println("Usage: <broker configuration file> <user settings file>");
            System.exit(1);
        }
    }

}
