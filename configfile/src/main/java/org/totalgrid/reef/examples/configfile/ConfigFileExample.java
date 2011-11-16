package org.totalgrid.reef.examples.configfile;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.ConfigFileService;
import org.totalgrid.reef.client.rpc.EntityService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Model.ConfigFile;
import org.totalgrid.reef.proto.Model.Entity;
import sun.security.krb5.Config;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Example: ConfigFile
 *
 * ConfigFiles are used to store arbitrary data (byte arrays) used by protocols and
 * applications.
 */
public class ConfigFileExample {

    /**
     * Get ConfigFile
     *
     * Retrieves the list of ConfigFiles in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getConfigFile(Client client) throws ReefServiceException, UnsupportedEncodingException {

        System.out.print("\n=== Get Config Files ===\n\n");

        // Get service interface for ConfigFiles
        ConfigFileService configFileService = client.getRpcInterface(ConfigFileService.class);

        // Get full list of ConfigFiles in the system
        List<ConfigFile> configFileList = configFileService.getAllConfigFiles();

        // Inspect the first ConfigFile object
        ConfigFile first = configFileList.get(0);

        // Display properties of the ConfigFile object
        System.out.println("ConfigFile");
        System.out.println("-----------");
        System.out.println("Name: " + first.getName());
        System.out.println("Mime-type: " + first.getMimeType());
        System.out.println("UUID: " + first.getUuid().getUuid());
        System.out.println("Data: \"" + first.getFile().toStringUtf8().substring(0, 30) + "...\"");

        for (Entity entity : first.getEntitiesList()) {
            System.out.println("Entity: " + entity.getName());
        }
        System.out.println("-----------\n");

        // List ConfigFiles in the system
        for (ConfigFile configFile : configFileList) {
            System.out.println("ConfigFile: " + configFile.getName());
        }
    }

    /**
     * Create/Update/Remove
     *
     * Runs through the lifecycle of ConfigFile objects. Creates a new ConfigFile,
     * updates it to change the data payload, and finally deletes it from the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void createUpdateRemove(Client client) throws ReefServiceException, UnsupportedEncodingException {

        System.out.print("\n=== Create / Update / Remove File ===\n\n");

        // Get service interface for ConfigFiles
        ConfigFileService configFileService = client.getRpcInterface(ConfigFileService.class);

        // Specify the name, mime-type, and data payload for a ConfigFile
        String name = "ExampleFile01";
        String mimeType = "text/plain";
        String initialData = "Example Config File Data";

        // Create the ConfigFile
        ConfigFile created = configFileService.createConfigFile(name, mimeType, initialData.getBytes("UTF-8"));

        System.out.println("Created - Name: " + created.getName() + ", Mime-type: " + created.getMimeType() + ", Data: \"" + created.getFile().toStringUtf8() + "\", UUID: " + created.getUuid());

        // Specify a new data payload
        String updatedData = "Second Config File Data";

        // Update the ConfigFile with the new data payload
        ConfigFile updated = configFileService.updateConfigFile(created, updatedData.getBytes("UTF-8"));

        System.out.println("Updated - Name: " + updated.getName() + ", Mime-type: " + updated.getMimeType() + ", Data: \"" + updated.getFile().toStringUtf8() + "\", UUID: " + updated.getUuid());

        // Delete the ConfigFile from the system
        ConfigFile deleted = configFileService.deleteConfigFile(updated);

        System.out.println("Deleted - Name: " + deleted.getName() + ", Mime-type: " + deleted.getMimeType() + ", Data: \"" + deleted.getFile().toStringUtf8() + "\", UUID: " + deleted.getUuid());

        // Verify the ConfigFile no long exists by searching for it
        boolean wasDeleted = configFileService.findConfigFileByName(name) == null;

        System.out.println("Was deleted: " + wasDeleted);

    }

    /**
     * Entity Association
     *
     * Demonstrates associating ConfigFiles with entities.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void entityAssociation(Client client) throws ReefServiceException {

        System.out.print("\n=== Entity Association ===\n\n");

        // Get service interface for ConfigFiles
        ConfigFileService configFileService = client.getRpcInterface(ConfigFileService.class);

        // Get service interface for Entity objects
        EntityService entityService = client.getRpcInterface(EntityService.class);

        // Get the a single Entity in the system
        Entity entity = entityService.getAllEntities().get(0);

        System.out.println("Entity: " + entity.getName() + ", " + entity.getUuid().getUuid() + "\n");

        // Create a ConfigFile (not yet associated with an Entity)
        ConfigFile file1 = configFileService.createConfigFile("File1", "text/plain", "data1".getBytes());

        // Display properties of the ConfigFile object before the association
        System.out.println("Before:");
        System.out.println("-----------");
        System.out.println("Name: " + file1.getName());
        System.out.println("Mime-type: " + file1.getMimeType());
        System.out.println("UUID: " + file1.getUuid().getUuid());
        System.out.println("Data: \"" + file1.getFile().toStringUtf8());

        for (Entity used : file1.getEntitiesList()) {
            System.out.println("Entity: " + used.getName());
        }
        System.out.println("-----------\n");

        // Associate the ConfigFile with the selected Entity
        ConfigFile associated = configFileService.addConfigFileUsedByEntity(file1, entity.getUuid());

        // Display properties of the ConfigFile object after the association
        System.out.println("After:");
        System.out.println("-----------");
        System.out.println("Name: " + associated.getName());
        System.out.println("Mime-type: " + associated.getMimeType());
        System.out.println("UUID: " + associated.getUuid().getUuid());
        System.out.println("Data: \"" + associated.getFile().toStringUtf8());

        for (Entity used : associated.getEntitiesList()) {
            System.out.println("Entity: " + used.getName());
        }
        System.out.println("-----------\n");

        // Delete the ConfigFile to clean-up
        configFileService.deleteConfigFile(associated);

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

            getConfigFile(client);

            createUpdateRemove(client);

            entityAssociation(client);

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
