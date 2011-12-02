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
import org.totalgrid.reef.proto.Alarms.Alarm;
import org.totalgrid.reef.proto.Events.Event;

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

    /**
     * Java entry-point for running examples.
     *
     * Starts a client connection to Reef, logs in, and executes example code.
     *
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Parse command line arguments
        if (args.length < 2) {
            System.out.println("Usage: <broker settings> <user settings>");
            System.exit(-1);
        }

        int result = 0;

        // Load broker settings from config file
        AmqpSettings amqp = new AmqpSettings(args[0]);

        // Load user settings (login credentials) from config file
        UserSettings user = new UserSettings(args[1]);

        // Create a ConnectionFactory by passing the broker settings. The ConnectionFactory is
        // used to create a Connection to the Reef server
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, ReefServices.getInstance());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getActiveAlarms(client);

            alarmLifecycle(client);

        } catch(ReefServiceException rse) {

            // Handle ReefServiceException, potentially caused by connection, login, or service request errors
            System.out.println("Reef service error: " + rse.getMessage() + ". check that Reef server is running.");
            rse.printStackTrace();
            result = -1;

        } finally {

            if(connection != null) {

                // Disconnect the Connection object, removes clients and subscriptions
                connection.disconnect();
            }

            // Terminate the ConnectionFactory to close threading objects
            connectionFactory.terminate();
        }

        System.exit(result);
    }

}
