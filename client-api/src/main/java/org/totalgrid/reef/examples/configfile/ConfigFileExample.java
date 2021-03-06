/**
 * Copyright 2011 Green Energy Corp.
 *
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Green Energy
 * Corp licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.totalgrid.reef.examples.configfile;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.ConfigFileService;
import org.totalgrid.reef.client.service.EntityService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Model.ConfigFile;
import org.totalgrid.reef.client.service.proto.Model.Entity;

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
        ConfigFileService configFileService = client.getService(ConfigFileService.class);

        // Get full list of ConfigFiles in the system
        List<ConfigFile> configFileList = configFileService.getConfigFiles();

        // Inspect the first ConfigFile object
        ConfigFile first = configFileList.get(0);

        // Display properties of the ConfigFile object
        System.out.println("ConfigFile");
        System.out.println("-----------");
        System.out.println("Name: " + first.getName());
        System.out.println("Mime-type: " + first.getMimeType());
        System.out.println("UUID: " + first.getUuid().getValue());
        System.out.println("Data: \"" + first.getFile().toStringUtf8() + "...\"");

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
        ConfigFileService configFileService = client.getService(ConfigFileService.class);

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
        ConfigFileService configFileService = client.getService(ConfigFileService.class);

        // Get service interface for Entity objects
        EntityService entityService = client.getService(EntityService.class);

        // Get the a single Entity in the system
        Entity entity = entityService.getEntities().get(0);

        System.out.println("Entity: " + entity.getName() + ", " + entity.getUuid().getValue() + "\n");

        // Create a ConfigFile (not yet associated with an Entity)
        ConfigFile file1 = configFileService.createConfigFile("File1", "text/plain", "data1".getBytes());

        // Display properties of the ConfigFile object before the association
        System.out.println("Before:");
        System.out.println("-----------");
        System.out.println("Name: " + file1.getName());
        System.out.println("Mime-type: " + file1.getMimeType());
        System.out.println("UUID: " + file1.getUuid().getValue());
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
        System.out.println("UUID: " + associated.getUuid().getValue());
        System.out.println("Data: \"" + associated.getFile().toStringUtf8());

        for (Entity used : associated.getEntitiesList()) {
            System.out.println("Entity: " + used.getName());
        }
        System.out.println("-----------\n");

        // Delete the ConfigFile to clean-up
        configFileService.deleteConfigFile(associated);

    }
}
