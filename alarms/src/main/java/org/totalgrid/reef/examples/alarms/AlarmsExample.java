package org.totalgrid.reef.examples.alarms;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.AlarmService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Alarms.Alarm;
import org.totalgrid.reef.proto.Events.Event;

import java.util.Date;
import java.util.List;

public class AlarmsExample {


    public static void getActiveAlarms(Client client) throws ReefServiceException {

        System.out.print("\n=== Active Alarms ===\n\n");

        AlarmService alarmService = client.getRpcInterface(AlarmService.class);

        List<Alarm> alarmList = alarmService.getActiveAlarms(5);

        Alarm firstAlarm = alarmList.get(0);

        Event firstEvent = firstAlarm.getEvent();

        System.out.println("Alarm");
        System.out.println("-----------");
        System.out.println("Alarm Uid: " + firstAlarm.getUid());
        System.out.println("State: " + firstAlarm.getState());
        System.out.println("Alarm Message: " + firstAlarm.getRendered());
        System.out.println("Event Uid: " + firstEvent.getUid());
        System.out.println("User: " + firstEvent.getUserId());
        System.out.println("Type: " + firstEvent.getEventType());
        System.out.println("Severity: " + firstEvent.getSeverity());
        System.out.println("Subsystem: " + firstEvent.getSubsystem());
        System.out.println("Event Message: " + firstEvent.getRendered());
        System.out.println("Is Alarm: " + firstEvent.getAlarm());
        System.out.println("Time: " + new Date(firstEvent.getTime()));
        System.out.println("-----------\n");

        for (Alarm alarm : alarmList) {
            System.out.println("Alarm: " + alarm.getState() + ", " + alarm.getEvent().getRendered() + ", " + new Date(alarm.getEvent().getTime()).toString());
        }
    }

    public static void alarmLifecycle(Client client) throws ReefServiceException {

        System.out.print("\n=== Alarm Lifecycle ===\n\n");

        AlarmService alarmService = client.getRpcInterface(AlarmService.class);

        Alarm alarm = alarmService.getActiveAlarms(1).get(0);

        System.out.println("Original: ");
        System.out.println("Alarm: " + alarm.getState() + ", " + alarm.getEvent().getRendered() + ", " + new Date(alarm.getEvent().getTime()).toString() + "\n");

        Alarm acked = alarmService.acknowledgeAlarm(alarm);

        System.out.println("Acknowledged: ");
        System.out.println("Alarm: " + acked.getState() + ", " + acked.getEvent().getRendered() + ", " + new Date(acked.getEvent().getTime()).toString() + "\n");

        Alarm removed = alarmService.removeAlarm(acked);

        System.out.println("Removed: ");
        System.out.println("Alarm: " + removed.getState() + ", " + removed.getEvent().getRendered() + ", " + new Date(removed.getEvent().getTime()).toString() + "\n");
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

            getActiveAlarms(client);

            alarmLifecycle(client);

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
