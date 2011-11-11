package org.totalgrid.reef.examples.events.publishing;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.EventCreationService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Events.Event;
import org.totalgrid.reef.proto.Utils.Attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventPublishingExample {


    public static void publishEvent(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Event ===\n\n");

        EventCreationService eventCreationService = client.getRpcInterface(EventCreationService.class);

        String eventType = "System.UserLogin";

        String subsystem = "system";

        Event published = eventCreationService.publishEvent(eventType, subsystem);

        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getUid());
        System.out.println("User: " + published.getUserId());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

    public static void publishEventWithArguments(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Event With Arguments ===\n\n");

        EventCreationService eventCreationService = client.getRpcInterface(EventCreationService.class);

        String eventType = "System.UserLogin";

        String subsystem = "system";

        Attribute status = Attribute.newBuilder().setName("status").setValueString("StatusArg").setVtype(Attribute.Type.STRING).build();

        Attribute reason = Attribute.newBuilder().setName("reason").setValueString("ReasonArg").setVtype(Attribute.Type.STRING).build();

        List<Attribute> attributeList = new ArrayList<Attribute>();

        attributeList.add(status);

        attributeList.add(reason);

        Event published = eventCreationService.publishEvent(eventType, subsystem, attributeList);

        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getUid());
        System.out.println("User: " + published.getUserId());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: <broker settings> <user settings>");
            System.exit(-1);
        }

        int result = 0;
        AmqpSettings amqp = new AmqpSettings(args[0]);
        UserSettings user = new UserSettings(args[1]);

        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);
        Connection connection = null;

        try {

            connection = connectionFactory.connect();
            Client client = connection.login(user);

            publishEvent(client);

            publishEventWithArguments(client);

        } catch(ReefServiceException rse) {

            System.out.println("Reef service error: " + rse.getMessage() + ". check that Reef server is running.");
            rse.printStackTrace();
            result = -1;

        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            connectionFactory.terminate();
        }

        System.exit(result);
    }

}
