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

public class ConfigFileExample {


    public static void getConfigFile(Client client) throws ReefServiceException, UnsupportedEncodingException {

        System.out.print("\n=== Get Config Files ===\n\n");

        ConfigFileService configFileService = client.getRpcInterface(ConfigFileService.class);

        List<ConfigFile> configFileList = configFileService.getAllConfigFiles();

        ConfigFile first = configFileList.get(0);

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

        for (ConfigFile configFile : configFileList) {
            System.out.println("ConfigFile: " + configFile.getName());
        }
    }

    public static void createUpdateRemove(Client client) throws ReefServiceException, UnsupportedEncodingException {

        System.out.print("\n=== Create / Update / Remove File ===\n\n");

        ConfigFileService configFileService = client.getRpcInterface(ConfigFileService.class);

        String name = "ExampleFile01";
        String mimeType = "text/plain";
        String initialData = "Example Config File Data";

        ConfigFile created = configFileService.createConfigFile(name, mimeType, initialData.getBytes("UTF-8"));

        System.out.println("Created - Name: " + created.getName() + ", Mime-type: " + created.getMimeType() + ", Data: \"" + created.getFile().toStringUtf8() + "\", UUID: " + created.getUuid());

        String updatedData = "Second Config File Data";

        ConfigFile updated = configFileService.updateConfigFile(created, updatedData.getBytes("UTF-8"));

        System.out.println("Updated - Name: " + updated.getName() + ", Mime-type: " + updated.getMimeType() + ", Data: \"" + updated.getFile().toStringUtf8() + "\", UUID: " + updated.getUuid());

        ConfigFile deleted = configFileService.deleteConfigFile(updated);

        System.out.println("Deleted - Name: " + deleted.getName() + ", Mime-type: " + deleted.getMimeType() + ", Data: \"" + deleted.getFile().toStringUtf8() + "\", UUID: " + deleted.getUuid());

        boolean wasDeleted = configFileService.findConfigFileByName(name) == null;

        System.out.println("Was deleted: " + wasDeleted);

    }

    public static void entityAssociation(Client client) throws ReefServiceException {

        System.out.print("\n=== Entity Association ===\n\n");

        ConfigFileService configFileService = client.getRpcInterface(ConfigFileService.class);

        EntityService entityService = client.getRpcInterface(EntityService.class);

        Entity entity = entityService.getAllEntities().get(0);

        System.out.println("Entity: " + entity.getName() + ", " + entity.getUuid().getUuid() + "\n");

        ConfigFile file1 = configFileService.createConfigFile("File1", "text/plain", "data1".getBytes());

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

        ConfigFile associated = configFileService.addConfigFileUsedByEntity(file1, entity.getUuid());

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

        configFileService.deleteConfigFile(associated);

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

            getConfigFile(client);

            createUpdateRemove(client);

            entityAssociation(client);


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
