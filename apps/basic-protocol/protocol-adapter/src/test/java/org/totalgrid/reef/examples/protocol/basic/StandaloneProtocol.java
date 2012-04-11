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
package org.totalgrid.reef.examples.protocol.basic;


import org.totalgrid.reef.client.settings.util.PropertyReader;
import org.totalgrid.reef.examples.protocol.basic.adapter.ProtocolAdapter;
import org.totalgrid.reef.frontend.StandaloneProtocolAdapter;
import org.totalgrid.reef.protocol.api.ProtocolManager;

import java.util.Arrays;
import java.util.Properties;

/**
 * Runs just this example protocol against a running server.
 *
 * @see StandaloneProtocolAdapter
 */
public class StandaloneProtocol {
    public static void main(String[] args) throws Exception {

        // Load configuration files from the working directory if args aren't present
        if(args.length == 0){
            args = new String[]{"org.totalgrid.reef.amqp.cfg", "org.totalgrid.reef.user.cfg", "org.totalgrid.reef.node.cfg"};
        }

        // Read in configuration
        Properties properties = PropertyReader.readFromFiles(Arrays.asList(args));

        // Create protocol adapter instance
        ProtocolManager manager = new ProtocolAdapter();

        // StandaloneProtocolAdapter allows us to create the "plumbing" necessary to host a protocol without
        // being deployed directly in the Reef runtime
        StandaloneProtocolAdapter standalone = new StandaloneProtocolAdapter(properties, "ExternalProtocol", manager);

        // Run the protocol until the program is terminated
        standalone.run();
    }
}
