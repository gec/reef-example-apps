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
package org.totalgrid.reef.examples.events.publishing;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.EventPublishingService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Events.Event;
import org.totalgrid.reef.client.service.proto.Utils.Attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Examples: Event Publishing
 *
 */
public class EventPublishingExample {

    /**
     * Publish Event
     *
     * Publish a user login event.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void publishEvent(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Event ===\n\n");

        // Get service interface for publishing events
        EventPublishingService eventCreationService = client.getService(EventPublishingService.class);

        // Set event type to user login
        String eventType = "System.UserLogin";

        // Set subsystem to generic "system"
        String subsystem = "system";

        // Publish event by specifying type and subsystem
        Event published = eventCreationService.publishEvent(eventType, subsystem);

        // Display properties of published Event
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getId());
        System.out.println("User: " + published.getUserId());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

    /**
     * Publish Event
     *
     * Publish a user login event with arguments. Arguments are used to provide event-specific
     * details. Their structure is determined by the event type.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void publishEventWithArguments(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Event With Arguments ===\n\n");

        // Get service interface for publishing events
        EventPublishingService eventCreationService = client.getService(EventPublishingService.class);

        // Set event type to user login
        String eventType = "System.UserLogin";

        // Set subsystem to generic "system"
        String subsystem = "system";

        // The "System.UserLogin" type has two arguments: "status" and "reason"

        // Create attribute for "status"
        Attribute status = Attribute.newBuilder().setName("status").setValueString("StatusArg").setVtype(Attribute.Type.STRING).build();

        // Create attribute for "reason"
        Attribute reason = Attribute.newBuilder().setName("reason").setValueString("ReasonArg").setVtype(Attribute.Type.STRING).build();

        // Create list of attributes
        List<Attribute> attributeList = new ArrayList<Attribute>();

        attributeList.add(status);

        attributeList.add(reason);

        // Publish event, including attributes
        Event published = eventCreationService.publishEvent(eventType, subsystem, attributeList);

        // Display properties of published Event, message will include status/reason
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getId());
        System.out.println("User: " + published.getUserId());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

}
