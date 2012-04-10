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
package org.totalgrid.reef.examples.alarms;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.AlarmService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Alarms.Alarm;
import org.totalgrid.reef.client.service.proto.Events.Event;

import java.util.Date;
import java.util.List;

/**
 *  Example: Alarms
 *
 *
 */
public class AlarmsExample {

    /**
     * Get Active Alarms
     *
     * Simple query for
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getActiveAlarms(Client client) throws ReefServiceException {

        System.out.print("\n=== Active Alarms ===\n\n");

        // Get service interface for alarms
        AlarmService alarmService = client.getService(AlarmService.class);

        // Limit the number of objects returned to a manageable amount
        int limit = 5;

        // Call the alarm service to get a list of active alarms
        List<Alarm> alarmList = alarmService.getActiveAlarms(limit);

        // Inspect the first Alarm
        Alarm firstAlarm = alarmList.get(0);

        // Alarms are associated with a single Event
        Event firstEvent = firstAlarm.getEvent();

        // Display the properties of the Alarm and Event objects
        System.out.println("Alarm");
        System.out.println("-----------");
        System.out.println("Alarm Uid: " + firstAlarm.getId());
        System.out.println("State: " + firstAlarm.getState());
        System.out.println("Alarm Message: " + firstAlarm.getRendered());
        System.out.println("Event Uid: " + firstEvent.getId());
        System.out.println("User: " + firstEvent.getUserId());
        System.out.println("Type: " + firstEvent.getEventType());
        System.out.println("Severity: " + firstEvent.getSeverity());
        System.out.println("Subsystem: " + firstEvent.getSubsystem());
        System.out.println("Event Message: " + firstEvent.getRendered());
        System.out.println("Is Alarm: " + firstEvent.getAlarm());
        System.out.println("Time: " + new Date(firstEvent.getTime()));
        System.out.println("-----------\n");

        // List active Alarms
        for (Alarm alarm : alarmList) {
            System.out.println("Alarm: " + alarm.getState() + ", " + alarm.getEvent().getRendered() + ", " + new Date(alarm.getEvent().getTime()).toString());
        }
    }

    /**
     * Alarm Lifecycle
     *
     * Demonstrates the lifecycle of an alarm. Alarms begin in the state UNACK_AUDIBLE or UNACK_SILENT,
     * are acknowledged by an operator and transition to the state ACKNOWLEDGED, and are removed to the
     * state REMOVED when no longer relevant.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void alarmLifecycle(Client client) throws ReefServiceException {

        System.out.print("\n=== Alarm Lifecycle ===\n\n");

        // Get service interface for alarms
        AlarmService alarmService = client.getService(AlarmService.class);

        // Get the first active alarm
        Alarm alarm = alarmService.getActiveAlarms(1).get(0);

        System.out.println("Original: ");
        System.out.println("Alarm: " + alarm.getState() + ", " + alarm.getEvent().getRendered() + ", " + new Date(alarm.getEvent().getTime()).toString() + "\n");

        // Acknowledges alarm, changing state from UNACK_* to ACKNOWLEDGED
        Alarm acked = alarmService.acknowledgeAlarm(alarm);

        System.out.println("Acknowledged: ");
        System.out.println("Alarm: " + acked.getState() + ", " + acked.getEvent().getRendered() + ", " + new Date(acked.getEvent().getTime()).toString() + "\n");

        // Removes alarm, changing state from ACKNOWLEDGED to REMOVED
        Alarm removed = alarmService.removeAlarm(acked);

        System.out.println("Removed: ");
        System.out.println("Alarm: " + removed.getState() + ", " + removed.getEvent().getRendered() + ", " + new Date(removed.getEvent().getTime()).toString() + "\n");
    }

}
