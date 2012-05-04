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
package org.totalgrid.reef.examples.service.event;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.totalgrid.reef.client.AnyNodeDestination;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.Subscription;
import org.totalgrid.reef.client.SubscriptionEvent;
import org.totalgrid.reef.client.SubscriptionEventAcceptor;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.registration.Service;
import org.totalgrid.reef.client.registration.ServiceRegistration;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.util.PropertyReader;
import org.totalgrid.reef.examples.service.event.client.KeyValueDescriptor;
import org.totalgrid.reef.examples.service.event.client.KeyValueService;
import org.totalgrid.reef.examples.service.event.client.KeyValueServiceList;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;
import org.totalgrid.reef.examples.service.event.service.KeyValueServiceImplementor;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static org.junit.Assert.*;

public class IntegrationTest {

    static ConnectionFactory connectionFactory = null;
    static Connection connection = null;
    static KeyValueService service = null;

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
        connection.addServicesList(new KeyValueServiceList());

        // Obtain the service registration interface to perform service provider duties
        ServiceRegistration registration = connection.getServiceRegistration();

        Service serviceImplementation = new KeyValueServiceImplementor(registration.getEventPublisher());

        // Bind sample service, routing sample messages to the service implementation
        registration.bindService(serviceImplementation, new KeyValueDescriptor(), new AnyNodeDestination(), true);

        // create a client and pretend we logged in by setting a fake auth token
        Client client = connection.createClient("FAKE_AUTH_TOKEN");

        // get a sample service binding implementation
        service = client.getService(KeyValueService.class);
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
     public void testSimplePutAndDelete() throws Exception{

        List<KeyValue> values = service.getAllValues().await();

        assertEquals(values.size(), 0);

        KeyValue inserted = service.putValue("Key", "Val").await();

        assertEquals("Key", inserted.getKey());
        assertEquals("Val", inserted.getValue());

        List<KeyValue> oneValue = service.getAllValues().await();

        assertEquals(1, oneValue.size());
        assertEquals(inserted, oneValue.get(0));

        KeyValue retrieved = service.getValue("Key").await();

        assertEquals(inserted, retrieved);

        List<KeyValue> deleteAll = service.deleteAllValues().await();

        assertEquals(oneValue, deleteAll);
    }

    @Test
    public void testBatchGets() throws Exception{

        service.deleteAllValues().await();

        service.putValue("Key1", "Val1").await();
        service.putValue("Key2", "Val2").await();
        service.putValue("Key3", "Val3").await();

        List<KeyValue> allThree = service.getValues(Arrays.asList("Key1", "Key2", "Key3")).await();
        assertEquals(3, allThree.size());
        assertEquals("Key1", allThree.get(0).getKey());
        assertEquals("Key2", allThree.get(1).getKey());
        assertEquals("Key3", allThree.get(2).getKey());

        List<KeyValue> justThreeAndTwo = service.getValues(Arrays.asList("Key3", "Key2")).await();
        assertEquals(2, justThreeAndTwo.size());
        assertEquals("Key3", justThreeAndTwo.get(0).getKey());
        assertEquals("Key2", justThreeAndTwo.get(1).getKey());

        try{
            // a batch request for a partially correct name will fail the whole batch
            service.getValues(Arrays.asList("Key3", "UnknownKey")).await();
            fail("Should have thrown exception for bad key");
        }catch(ReefServiceException rse){
            assertNotSame(-1, rse.getMessage().indexOf("UnknownKey"));
        }
    }

    class SubscriptionListener implements SubscriptionEventAcceptor<KeyValue>{

        BlockingQueue<SubscriptionEvent<KeyValue>> queue = new ArrayBlockingQueue<SubscriptionEvent<KeyValue>>(100);
        @Override
        public void onEvent(SubscriptionEvent<KeyValue> keyValueSubscriptionEvent) {
            queue.add(keyValueSubscriptionEvent);
        }
    }

    @Test
    public void testSubscription() throws Exception{

        service.deleteAllValues().await();

        SubscriptionListener allListener = new SubscriptionListener();
        SubscriptionListener key1Listener = new SubscriptionListener();

        service.putValue("Key1", "Val1").await();
        service.putValue("Key2", "Val2").await();

        Subscription<KeyValue> sub1 = service.subscribeToAllKeyValues().await().getSubscription().start(allListener);
        Subscription<KeyValue> sub2 = service.subscribeToKeyValues("Key1").await().getSubscription().start(key1Listener);

        service.putValue("Key1", "ChangedVal1").await();
        service.putValue("Key2", "ChangedVal2").await();

        assertEquals(2, allListener.queue.size());
        assertEquals(1, key1Listener.queue.size());

        service.deleteAllValues().await();

        assertEquals(4, allListener.queue.size());
        assertEquals(2, key1Listener.queue.size());

        sub1.cancel();
        sub2.cancel();
    }
}
