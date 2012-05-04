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
package org.totalgrid.reef.examples.service.basic;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.totalgrid.reef.client.AnyNodeDestination;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.Promise;
import org.totalgrid.reef.client.PromiseErrorTransform;
import org.totalgrid.reef.client.exception.BadRequestException;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.registration.ServiceRegistration;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.util.PropertyReader;
import org.totalgrid.reef.examples.service.basic.client.SampleMessageDescriptor;
import org.totalgrid.reef.examples.service.basic.client.SampleService;
import org.totalgrid.reef.examples.service.basic.client.SampleServiceList;
import org.totalgrid.reef.examples.service.basic.service.SampleServiceImplementor;

import java.util.Properties;

import static org.junit.Assert.assertEquals;

public class IntegrationTest{

    static ConnectionFactory connectionFactory = null;
    static Connection connection = null;
    static SampleService service = null;

    @BeforeClass
    public static void setupConnection() throws Exception{

        Properties props = PropertyReader.readFromFile("../../../org.totalgrid.reef.amqp.cfg");
        // Load broker settings from config file
        AmqpSettings amqp = new AmqpSettings(props);

        // Create a ConnectionFactory by passing the broker settings. The ConnectionFactory is
        // used to create a Connection to the Reef server
        connectionFactory = ReefConnectionFactory.buildFactory(amqp, new ReefServices());

        // Connect to the Reef server, may fail if can't connect
        connection = connectionFactory.connect();

        // Add the sample service list, which contains the type description of the sample service message
        connection.addServicesList(new SampleServiceList());

        // Obtain the service registration interface to perform service provider duties
        ServiceRegistration registration = connection.getServiceRegistration();

        // Bind sample service, routing sample messages to the service implementation
        registration.bindService(new SampleServiceImplementor(), new SampleMessageDescriptor(), new AnyNodeDestination(), true);

        // create a client and pretend we logged in by setting a fake auth token
        Client client = connection.createClient("FAKE_AUTH_TOKEN");

        // get a sample service binding implementation
        service = client.getService(SampleService.class);
    }

    @AfterClass
    public static void teardownConnection(){

        if(connection != null) {

            // Disconnect the Connection object, removes clients and subscriptions
            connection.disconnect();
        }

        if(connectionFactory != null){
            // Terminate the ConnectionFactory to close threading objects
            connectionFactory.terminate();
        }
    }


    @Test
     public void testSuccessfulRequests() throws Exception{


        String response = service.sendAndGetContent("test-string", false).await();

        assertEquals(response, "test-string");

        String response2 = service.sendAndGetContent("echo", false).await();

        assertEquals(response2, "echo");

    }

    @Test
    public void testFailedRequest() throws Exception{

        Promise<String> failure = service.sendAndGetContent("error string", true);
        try{
            failure.await();
        }catch(BadRequestException bre){
            assertEquals(bre.getMessage(), "error string");
        }

        Promise<String> transformedFailure = failure.transformError(new PromiseErrorTransform() {
            @Override
            public ReefServiceException transformError(ReefServiceException error) {
                error.addExtraInformation("ExtraInfo");
                return error;
            }
        });
        try{
            transformedFailure.await();
        }catch(BadRequestException bre){
            assertEquals(bre.getMessage(), "ExtraInfoerror string");
        }
    }
}
