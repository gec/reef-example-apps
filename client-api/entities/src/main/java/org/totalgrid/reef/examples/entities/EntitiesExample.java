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
package org.totalgrid.reef.examples.entities;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.EntityService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Model.Relationship;
import org.totalgrid.reef.client.service.proto.Model.Entity;
import org.totalgrid.reef.client.service.proto.Model.ReefUUID;

import java.util.List;

/**
 * Example: Entities
 *
 */
public class EntitiesExample {

    /**
     * Get Entities
     *
     * Retrieves the list of Entity objects in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getEntities(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Entities ===\n\n");

        // Get service interface for entities
        EntityService entityService = client.getService(EntityService.class);

        // Retrieve list of all entities in the system
        List<Entity> entityList = entityService.getEntities();

        // Inspect a single Entity object
        Entity first = entityList.get(0);

        // Display properties of Entity object
        System.out.println("Entity");
        System.out.println("-----------");
        System.out.println("Uuid: " + first.getUuid().getValue());
        System.out.println("Name: " + first.getName());
        System.out.println("Types: " + first.getTypesList());
        System.out.println("-----------\n");

        System.out.println("Entity count: " + entityList.size());

        // Display list of (first 10) Entity objects
        for (Entity entity : entityList.subList(0, 10)) {
            System.out.println("Entity: " + entity.getName());
        }

        System.out.println("...");
    }

    /**
     * Get By Type
     *
     * Retrieves Entity objects of a certain type.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getByType(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Entities With Type ===\n\n");

        // Get service interface for entities
        EntityService entityService = client.getService(EntityService.class);

        // Get Entity objects with the type "Breaker"
        List<Entity> entityList = entityService.getEntitiesWithType("Breaker");

        System.out.println("Entity count: " + entityList.size());

        // Display list of "Breaker" entities
        for (Entity entity : entityList) {
            System.out.println("Entity: " + entity.getName() + ", Types: " + entity.getTypesList());
        }

    }

    /**
     * Get Immediate Children
     *
     * Finds the immediate children of an Entity.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getImmediateChildren(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Immediate Children ===\n\n");

        // Get service interface for entities
        EntityService entityService = client.getService(EntityService.class);

        // Select an Entity of type "Equipment"
        Entity equipment = entityService.getEntitiesWithType("Equipment").get(0);

        System.out.println("Parent: " + equipment.getName() + ", Types: " + equipment.getTypesList());

        // Get UUID of equipment entity
        ReefUUID equipUuid = equipment.getUuid();

        // Get immediate children (relationship "owns") of equipment entity
        List<Entity> children = entityService.getEntityImmediateChildren(equipUuid, "owns");

        // Display list of the children of the equipment entity
        for (Entity entity : children) {
            System.out.println("Children: " + entity.getName() + ", Types: " + entity.getTypesList());
        }

    }

    /**
     * Get Children
     *
     * Finds the children of an Entity, at multiple depths.
     *
     * Returns a tree of entities (represented by a root node) that can be
     * traversed to examine the relationships and sub-entities.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getChildren(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Children ===\n\n");

        // Get service interface for entities
        EntityService entityService = client.getService(EntityService.class);

        // Select an Entity of type "EquipmentGroup"
        Entity equipmentGroup = entityService.getEntitiesWithType("EquipmentGroup").get(0);

        ReefUUID equipUuid = equipmentGroup.getUuid();

        // Get children of the equipment group to a depth of 2
        Entity rootNode = entityService.getEntityChildren(equipUuid, "owns", 2);

        // Display root node (the equipment group)
        System.out.println("Root: " + rootNode.getName() + ", Types: " + rootNode.getTypesList());

        List<Relationship> subRelations = rootNode.getRelationsList();

        // Display relationships at depth 1
        for (Relationship relationship: subRelations) {
            System.out.println("+ Relationship: " + relationship.getRelationship() + ", Descendant: " + relationship.getDescendantOf() + ", Distance: " + relationship.getDistance());

            // Display (first 3) entities of at depth 1
            for (Entity entity : relationship.getEntitiesList().subList(0, 2)) {
                System.out.println("  + Children: " + entity.getName() + ", Types: " + entity.getTypesList());

                // Display relationships at depth 2
                for (Relationship rel2: entity.getRelationsList()) {
                    System.out.println("    + Relationship: " + rel2.getRelationship() + ", Descendant: " + rel2.getDescendantOf() + ", Distance: " + rel2.getDistance());

                    // Display entities at depth 2
                    for (Entity entity2: rel2.getEntitiesList().subList(0, 2)) {
                        System.out.println("      + Children: " + entity2.getName() + ", Types: " + entity2.getTypesList());
                    }
                    System.out.println("        ...");
                }
            }
            System.out.println("  ...");
        }

    }

    /**
     * Entity Tree
     *
     * Searches for entities by creating a search tree of abstract entities. Starting
     * from the root of the tree, the entity service finds any relationships/entities
     * that the tree describes.
     *
     * In particular, this example starts with a Point entity, and searched back "up"
     * the equipment hierarchy to find the EquipmentGroup the point is a part of.
     *
     * Returns a tree of entities (represented by a root node) that can be
     * traversed to examine the relationships and sub-entities.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void entityTree(Client client) throws ReefServiceException {

        System.out.print("\n=== Entity Tree ===\n\n");

        // Get service interface for entities
        EntityService entityService = client.getService(EntityService.class);

        // Select a single Entity of type "Point"
        Entity point = entityService.getEntitiesWithType("Point").get(0);

        // Create the root of the abstract search tree, set it to the point entity
        Entity.Builder treeRootBuilder = Entity.newBuilder();
        treeRootBuilder.setUuid(point.getUuid());

        // Set the first relationship to be "owns", to go back "up" the tree
        // (descendant = false), and be an immediate relative (distance = 1)
        Relationship.Builder relationship = Relationship.newBuilder();
        relationship.setRelationship("owns");
        relationship.setDescendantOf(false);
        relationship.setDistance(1);

        // Find immediate relatives of type "Equipment"
        Entity.Builder firstEntity = Entity.newBuilder();
        firstEntity.addTypes("Equipment");

        // Set the first relationship to be "owns", to go back "up" the tree
        // (descendant = false), and be an immediate relative (distance = 1)
        Relationship.Builder higherRelationship = Relationship.newBuilder();
        higherRelationship.setRelationship("owns");
        higherRelationship.setDescendantOf(false);
        higherRelationship.setDistance(1);

        // The second-depth relative should be of type "EquipmentGroup"
        Entity.Builder secondEntity = Entity.newBuilder();
        secondEntity.addTypes("EquipmentGroup");

        // Add equipment group entity to "higher" relationship
        higherRelationship.addEntities(secondEntity);

        // Add "higher" relationship to the equipment entity
        firstEntity.addRelations(higherRelationship);

        // Add equipment entity to the "lower" (immediate) relationship
        relationship.addEntities(firstEntity);

        // Add immediate relationship to the root
        treeRootBuilder.addRelations(relationship);

        // Construct the root entity
        Entity treeRoot = treeRootBuilder.build();

        // Get a result tree from the search tree
        Entity result = entityService.searchForEntityTree(treeRoot);

        // Display the root node (point entity)
        System.out.println("Root: " + result.getName() + ", Types: " + result.getTypesList());

        // Get immediate relationship
        Relationship lowerRel = result.getRelations(0);

        System.out.println("+ Relationship: " + lowerRel.getRelationship() + ", Descendant: " + lowerRel.getDescendantOf() + ", Distance: " + lowerRel.getDistance());

        // Display the immediate child entity ("Equipment")
        Entity middle = lowerRel.getEntities(0);

        System.out.println("  + Child: " + middle.getName() + ", Types: " + middle.getTypesList());

        // Get second-depth relationship
        Relationship higherRel = middle.getRelations(0);

        System.out.println("    + Relationship: " + higherRel.getRelationship() + ", Descendant: " + higherRel.getDescendantOf() + ", Distance: " + higherRel.getDistance());

        // Display the second child entity ("EquipmentGroup")
        Entity high = higherRel.getEntities(0);

        System.out.println("      + Child: " + high.getName() + ", Types: " + high.getTypesList());

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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, new ReefServices());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getEntities(client);

            getByType(client);

            getImmediateChildren(client);

            getChildren(client);

            entityTree(client);

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
