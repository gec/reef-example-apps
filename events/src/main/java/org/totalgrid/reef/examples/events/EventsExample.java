package org.totalgrid.reef.examples.events;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.EventService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Events.EventSelect;
import org.totalgrid.reef.proto.Events.Event;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventsExample {


    public static void getRecentEvents(Client client) throws ReefServiceException {

        System.out.print("\n=== Recent Events ===\n\n");

        EventService eventService = client.getRpcInterface(EventService.class);

        List<Event> eventList = eventService.getRecentEvents(5);

        Event firstEvent = eventList.get(0);

        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + firstEvent.getUid());
        System.out.println("User: " + firstEvent.getUserId());
        System.out.println("Type: " + firstEvent.getEventType());
        System.out.println("Severity: " + firstEvent.getSeverity());
        System.out.println("Subsystem: " + firstEvent.getSubsystem());
        System.out.println("Message: " + firstEvent.getRendered());
        System.out.println("Is Alarm: " + firstEvent.getAlarm());
        System.out.println("Time: " + new Date(firstEvent.getTime()));
        System.out.println("-----------\n");

        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    public static void getRecentEventsByType(Client client) throws ReefServiceException {

        System.out.print("\n=== Recent Events By Type ===\n\n");

        EventService eventService = client.getRpcInterface(EventService.class);

        List<String> typeList = new ArrayList<String>();
        typeList.add("System.UserLogin");

        List<Event> eventList = eventService.getRecentEvents(typeList, 5);

        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    public static void searchForEventsBySeverity(Client client) throws ReefServiceException {

        System.out.print("\n=== Search For Events By Severity ===\n\n");

        EventService eventService = client.getRpcInterface(EventService.class);

        EventSelect.Builder builder = EventSelect.newBuilder();

        builder.setSeverityOrHigher(5);

        builder.setLimit(5);

        EventSelect eventSelect = builder.build();

        List<Event> eventList = eventService.getEvents(eventSelect);

        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
    }

    public static void searchForEventsByInterval(Client client) throws ReefServiceException {

        System.out.print("\n=== Search For Events By Interval ===\n\n");

        EventService eventService = client.getRpcInterface(EventService.class);

        EventSelect.Builder builder = EventSelect.newBuilder();

        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);

        builder.setTimeFrom(twentyMinutesAgo);

        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        builder.setTimeTo(fiveMinutesAgo);

        builder.setLimit(5);

        EventSelect eventSelect = builder.build();

        List<Event> eventList = eventService.getEvents(eventSelect);

        for (Event event : eventList) {
            System.out.println("Event: " + event.getRendered() + ", " + new Date(event.getTime()).toString());
        }
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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getRecentEvents(client);

            getRecentEventsByType(client);

            searchForEventsBySeverity(client);

            searchForEventsByInterval(client);

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
