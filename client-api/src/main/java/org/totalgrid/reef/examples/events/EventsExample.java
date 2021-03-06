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
package org.totalgrid.reef.examples.events;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.EventService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Events.EventSelect;
import org.totalgrid.reef.client.service.proto.Events.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Examples: Events
 *
 *
 */
public class EventsExample {

    /**
     * Get Recent Events
     *
     * Gets a list of the most recent events.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getRecentEvents(Client client) throws ReefServiceException {

        System.out.print("\n=== Recent Events ===\n\n");

        // Get service interface for events
        EventService eventService = client.getService(EventService.class);

        // Get list of five most recent events
        List<Event> eventList = eventService.getRecentEvents(5);

        // Inspect a single event
        Event firstEvent = eventList.get(0);

        // Display properties of the event
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + firstEvent.getId());
        System.out.println("User: " + firstEvent.getUserId());
        System.out.println("Type: " + firstEvent.getEventType());
        System.out.println("Severity: " + firstEvent.getSeverity());
        System.out.println("Subsystem: " + firstEvent.getSubsystem());
        System.out.println("Message: " + firstEvent.getRendered());
        System.out.println("Is Alarm: " + firstEvent.getAlarm());
        System.out.println("Time: " + new Date(firstEvent.getTime()));
        System.out.println("-----------\n");

        // Display list of events
        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    /**
     * Get Recent Events by Type
     *
     * Narrows the recent events returned by a specific type.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getRecentEventsByType(Client client) throws ReefServiceException {

        System.out.print("\n=== Recent Events By Type ===\n\n");

        // Get service interface for events
        EventService eventService = client.getService(EventService.class);

        // Create a list of a single type, user login
        List<String> typeList = new ArrayList<String>();
        typeList.add("System.UserLogin");

        // Search for five most recent events of type user login
        List<Event> eventList = eventService.getRecentEvents(typeList, 5);

        // Display list of events
        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    /**
     * Search for Events by Severity
     *
     * Uses the EventSelect object to create an advanced event search. Searches for
     * events of severity five or higher.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void searchForEventsBySeverity(Client client) throws ReefServiceException {

        System.out.print("\n=== Search For Events By Severity ===\n\n");

        // Get service interface for events
        EventService eventService = client.getService(EventService.class);

        // Create EventSelect object to describe our search
        EventSelect.Builder builder = EventSelect.newBuilder();

        // Select events that have a severity of five or higher
        builder.setSeverityOrHigher(5);

        // It is almost always necessary/advisable to limit the number of results
        builder.setLimit(5);

        EventSelect eventSelect = builder.build();

        // Get events using the EventSelect
        List<Event> eventList = eventService.searchForEvents(eventSelect);

        // Display list of event results
        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    /**
     * Search for Events by Interval
     *
     * Uses the EventSelect object to create an advanced event search. Searches for
     * events which occurred between twenty minutes ago and five minutes ago.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void searchForEventsByInterval(Client client) throws ReefServiceException {

        System.out.print("\n=== Search For Events By Interval ===\n\n");

        // Get service interface for events
        EventService eventService = client.getService(EventService.class);

        // Create EventSelect object to describe our search
        EventSelect.Builder builder = EventSelect.newBuilder();

        // Set start time to twenty minutes ago
        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);

        builder.setTimeFrom(twentyMinutesAgo);

        // Set end time to five minutes ago
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        builder.setTimeTo(fiveMinutesAgo);

        // It is almost always necessary/advisable to limit the number of results
        builder.setLimit(5);

        EventSelect eventSelect = builder.build();

        // Get events using the EventSelect
        List<Event> eventList = eventService.searchForEvents(eventSelect);

        // Display list of event results
        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

}
