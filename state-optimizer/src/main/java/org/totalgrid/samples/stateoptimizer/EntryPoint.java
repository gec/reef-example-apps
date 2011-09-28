package org.totalgrid.samples.stateoptimizer;


import org.totalgrid.reef.broker.BrokerConnectionInfo;
import org.totalgrid.reef.japi.client.*;
import org.totalgrid.reef.japi.request.AllScadaService;
import org.totalgrid.reef.japi.request.impl.AllScadaServicePooledWrapper;
import org.totalgrid.reef.japi.request.impl.AuthTokenServicePooledWrapper;
import org.totalgrid.reef.japi.ServiceIOException;
import org.totalgrid.reef.messaging.javaclient.AMQPConnection;
import org.totalgrid.reef.proto.ReefServicesList;
import org.totalgrid.reef.util.ConfigReader;
import org.totalgrid.reef.util.FileConfigReader;
import sun.security.krb5.Config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * logs into reef, initializes and authorizes a session and passes it to the StateOptimizerManager
 */
public class EntryPoint {

    private static AMQPConnectionSettings getConnectionSettings(String filePath) throws IOException {
        FileInputStream stream = new FileInputStream(filePath);
        Properties props = new Properties();
        props.load(stream);
        return new AMQPConnectionSettingImpl(props);
    }

    private static UserSettings getUserSettings(String filePath) throws IOException {
        FileInputStream stream = new FileInputStream(filePath);
        Properties props = new Properties();
        props.load(stream);
        return new UserSettings(props);
    }

    public static void main(String[] args) throws Exception {
        AMQPConnectionSettings info = null;
        UserSettings user = null;
        if (args.length == 2) {
            info = getConnectionSettings(args[0]);
            user = getUserSettings(args[1]);

        } else {
            System.out.println("Usage: <broker configuration file> <user settings file>");
            System.exit(1);
        }

        // configure the connection with list of services and address
        Connection connection = new AMQPConnection(info, ReefServicesList.getInstance(), 5000);
        try {
            connection.connect(5000);
            System.out.println("Connected to Reef");

            SessionExecutionPool pool = connection.newSessionPool();
            String authToken = new AuthTokenServicePooledWrapper(pool).createNewAuthorizationToken(user.getUserName(), user.getUserPassword());

            AllScadaService services = new AllScadaServicePooledWrapper(pool, authToken);

            // create the switching algorithm we will use
            IStateOptimizer algorithm = new CapacitorSwitchingAlgorithm();

            // manager sets up timer to call alogrithm.optimize on a timer
            StateOptimizerManager manager = new StateOptimizerManager(services, algorithm, 5000);

            // Run until we are done
            manager.run();
        } catch (Exception e) {
            System.out.println("Error connecting or logging in: " + e.getMessage() + ". check that Reef server is running.");
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect(5000);
            } catch (ServiceIOException e) {
                System.out.println("Error disconnecting: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("Disconnected from Reef");
        }
    }

}
