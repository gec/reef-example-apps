package org.totalgrid.reef.examples.login;


import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;

public class LoginConnector {


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

        } catch(ReefServiceException rse) {

            System.out.println("Error connecting or logging in: " + rse.getMessage() + ". check that Reef server is running.");
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
