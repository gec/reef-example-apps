package org.totalgrid.samples.stateoptimizer;

import org.totalgrid.reef.api.ServiceIOException;
import org.totalgrid.reef.api.javaclient.IConnection;
import org.totalgrid.reef.api.javaclient.ISession;
import org.totalgrid.reef.api.request.AllScadaService;
import org.totalgrid.reef.api.request.impl.ReefScadaServiceImpl;
import org.totalgrid.reef.messaging.BrokerConnectionInfo;
import org.totalgrid.reef.messaging.javaclient.Connection;
import org.totalgrid.reef.proto.ReefServicesList;

/**
 * logs into reef, initializes and authorizes a session and passes it to the StateOptimizerManager
 */
public class EntryPoint {

    public static void main(String[] args) {
        BrokerConnectionInfo info = getConnectionInfo();

        // configure the connection with list of services
        IConnection connection = new Connection(info, ReefServicesList.getInstance(), 5000);
        try {
            connection.connect(5000);
            System.out.println("Connected to Reef");

            // get a session for the algorithm to use and create the all services wrapper
            ISession session = connection.newSession();
            AllScadaService services = new ReefScadaServiceImpl(session);

            // get an AuthToken and attach it to parent
            String token = services.createNewAuthorizationToken("core", "core");
            session.getDefaultEnv().setAuthToken(token);

            // create the switching algorithm we will use
            IStateOptimizer algorithm = new CapacitorSwitchingAlgorithm();

            // manager sets up timer to call alogrithm.optimize on a timer
            StateOptimizerManager manager = new StateOptimizerManager(services, algorithm, 5000);

            // Run until we are done
            manager.run();
        } catch (Exception e) {
            System.out.println("Error connecting or logging in: " + e.getMessage() + ". check that Reef server is running");
            e.printStackTrace();
        } finally {
            try {
                connection.disconnect(5000);
            } catch (ServiceIOException e) {
                // swallow any errors disconnecting
            }
            System.out.println("Disconnected from Reef");
        }
    }

    /**
     * gets the ip of the reef server, defaults to 127.0.0.1 but can be override with java property
     * -Dorg.totalgrid.reef.amqp.host=192.168.100.10
     */
    private static BrokerConnectionInfo getConnectionInfo() {
        String reef_ip = System.getProperty("org.totalgrid.reef.amqp.host");
        if (reef_ip == null) reef_ip = "127.0.0.1";
        String reef_port = System.getProperty("org.totalgrid.reef.amqp.port");
        if (reef_port == null) reef_port = "5672";
        String user = System.getProperty("org.totalgrid.reef.amqp.user");
        if (user == null) user = "guest";
        String password = System.getProperty("org.totalgrid.reef.amqp.password");
        if (password == null) password = "guest";
        String virtualHost = System.getProperty("org.totalgrid.reef.amqp.virtualHost");
        if (virtualHost == null) virtualHost = "test";

        return new BrokerConnectionInfo(reef_ip, Integer.parseInt(reef_port), user, password, virtualHost);
    }

}
