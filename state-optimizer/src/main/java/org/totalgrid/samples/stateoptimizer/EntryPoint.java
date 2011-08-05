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

    public static void main(String[] args) throws Exception {
        AMQPConnectionSettings info = null;
        if (args.length == 1) {
            info = AMQPConnectionSettings.loadFromFile(args[0]);
        } else {
            System.out.println("Usage: <broker configuration file>");
            System.exit(1);
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

}
