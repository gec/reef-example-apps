package org.totalgrid.samples.stateoptimizer;


import org.totalgrid.reef.japi.request.AllScadaService;
import org.totalgrid.reef.japi.request.impl.AllScadaServicePooledWrapper;
import org.totalgrid.reef.japi.request.impl.AuthTokenServicePooledWrapper;
import org.totalgrid.reef.japi.ServiceIOException;
import org.totalgrid.reef.japi.client.AMQPConnectionSettings;
import org.totalgrid.reef.japi.client.Connection;
import org.totalgrid.reef.japi.client.SessionExecutionPool;
import org.totalgrid.reef.messaging.javaclient.AMQPConnection;
import org.totalgrid.reef.proto.ReefServicesList;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * logs into reef, initializes and authorizes a session and passes it to the StateOptimizerManager
 */
public class EntryPoint {

    public static void main(String[] args) {
        AMQPConnectionSettings info = null;
        if (args.length == 0 ) {
            info = getConnectionInfo("org.totalgrid.reef.amqp.cfg");
        } else if (args.length == 1) {
            info = getConnectionInfo(args[0]);
        } else {
            System.out.println("Error: too many arguments.");
            System.exit(-1);
        }

        // configure the connection with list of services and address
        Connection connection = new AMQPConnection(info, ReefServicesList.getInstance(), 5000);
        try {
            connection.connect(5000);
            System.out.println("Connected to Reef");

            SessionExecutionPool pool = connection.newSessionPool();
            String authToken = new AuthTokenServicePooledWrapper(pool).createNewAuthorizationToken("core", "core");

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

    /**
     * Loads broker configuration from Reef settings file
     * @return settings to connect to the broker
     */
    private static AMQPConnectionSettings getConnectionInfo(String configFile) {
        Properties props = new Properties();
        try {
            FileInputStream fis = new FileInputStream(configFile);
            props.load(fis);
            fis.close();
        } catch (IOException ex) {
            System.out.println("Could not load config file: " + ex.toString());
            System.exit(-1);
        }

        String reefIp = loadProperty("org.totalgrid.reef.amqp.host", props);
        String port = loadProperty("org.totalgrid.reef.amqp.port", props);
        String user = loadProperty("org.totalgrid.reef.amqp.user", props);
        String password = loadProperty("org.totalgrid.reef.amqp.password", props);
        String virtualHost = loadProperty("org.totalgrid.reef.amqp.virtualHost", props);

        return new AMQPConnectionSettings(reefIp, Integer.parseInt(port), user, password, virtualHost);
    }

    private static String loadProperty(String id, Properties props) {
        String prop = props.getProperty(id);
        if (prop == null) {
            loadFailure(id);
        }
        return prop;
    }

    private static void loadFailure(String missing) {
        System.out.println("Could not load configuration. Missing: " + missing);
        System.exit(-1);
    }

}
