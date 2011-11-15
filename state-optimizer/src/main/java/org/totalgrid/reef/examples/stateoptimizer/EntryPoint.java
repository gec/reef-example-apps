package org.totalgrid.reef.examples.stateoptimizer;


import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.AllScadaService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * logs into reef, initializes and authorizes a session and passes it to the StateOptimizerManager
 */
public class EntryPoint {


    public static void startup(AmqpSettings amqp, UserSettings user) {
        ConnectionFactory factory = new ReefConnectionFactory(amqp);
        Connection conn = null;
        try {
            conn = factory.connect();
            Client client = conn.login(user);
            AllScadaService services = client.getRpcInterface(AllScadaService.class);
            IStateOptimizer algorithm = new CapacitorSwitchingAlgorithm();

            // manager sets up timer to call algorithm.optimize on a timer
            StateOptimizerManager manager = new StateOptimizerManager(services, algorithm, 5000);

            // Run until we are done
            manager.run();
        }
        catch(ReefServiceException rse) {
            System.out.println("Error connecting or logging in: " + rse.getMessage() + ". check that Reef server is running.");
            rse.printStackTrace();
        }
        finally {
            if(conn != null) conn.disconnect();
            factory.terminate();
        }

    }

    public static void main(String[] args) throws Exception {

        if (args.length == 2) {
            AmqpSettings amqp = new AmqpSettings(args[0]);
            UserSettings user = new UserSettings(args[1]);
            startup(amqp, user);

        } else {
            System.out.println("Usage: <broker configuration file> <user settings file>");
            System.exit(1);
        }
    }

}