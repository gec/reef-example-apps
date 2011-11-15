package org.totalgrid.reef.examples.entities;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.EntityService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Model.Relationship;
import org.totalgrid.reef.proto.Model.Entity;
import org.totalgrid.reef.proto.Model.ReefUUID;

import java.util.List;

public class EntitiesExample {


    public static void getEntities(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Entities ===\n\n");

        EntityService entityService = client.getRpcInterface(EntityService.class);

        List<Entity> entityList = entityService.getAllEntities();

        Entity first = entityList.get(0);


        System.out.println("Entity");
        System.out.println("-----------");
        System.out.println("Uuid: " + first.getUuid().getUuid());
        System.out.println("Name: " + first.getName());
        System.out.println("Types: " + first.getTypesList());
        System.out.println("-----------\n");

        System.out.println("Entity count: " + entityList.size());

        for (Entity entity : entityList.subList(0, 10)) {
            System.out.println("Entity: " + entity.getName());
        }

        System.out.println("...");
    }

    public static void getByType(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Entities With Type ===\n\n");

        EntityService entityService = client.getRpcInterface(EntityService.class);

        List<Entity> entityList = entityService.getAllEntitiesWithType("Breaker");

        System.out.println("Entity count: " + entityList.size());

        for (Entity entity : entityList) {
            System.out.println("Entity: " + entity.getName() + ", Types: " + entity.getTypesList());
        }

    }

    public static void getImmediateChildren(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Immediate Children ===\n\n");

        EntityService entityService = client.getRpcInterface(EntityService.class);

        Entity equipment = entityService.getAllEntitiesWithType("Equipment").get(0);

        System.out.println("Parent: " + equipment.getName() + ", Types: " + equipment.getTypesList());

        ReefUUID equipUuid = equipment.getUuid();

        List<Entity> children = entityService.getEntityImmediateChildren(equipUuid, "owns");

        for (Entity entity : children) {
            System.out.println("Children: " + entity.getName() + ", Types: " + entity.getTypesList());
        }

    }

    public static void getChildren(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Children ===\n\n");

        EntityService entityService = client.getRpcInterface(EntityService.class);

        Entity equipment = entityService.getAllEntitiesWithType("EquipmentGroup").get(0);

        ReefUUID equipUuid = equipment.getUuid();

        Entity rootNode = entityService.getEntityChildren(equipUuid, "owns", 2);

        System.out.println("Root: " + rootNode.getName() + ", Types: " + rootNode.getTypesList());

        List<Relationship> subRelations = rootNode.getRelationsList();

        for (Relationship relationship: subRelations) {
            System.out.println("+ Relationship: " + relationship.getRelationship() + ", Descendant: " + relationship.getDescendantOf() + ", Distance: " + relationship.getDistance());

            for (Entity entity : relationship.getEntitiesList().subList(0, 3)) {
                System.out.println("  + Children: " + entity.getName() + ", Types: " + entity.getTypesList());

                for (Relationship rel2: entity.getRelationsList()) {
                    System.out.println("    + Relationship: " + rel2.getRelationship() + ", Descendant: " + rel2.getDescendantOf() + ", Distance: " + rel2.getDistance());

                    for (Entity entity2: rel2.getEntitiesList().subList(0, 4)) {
                        System.out.println("      + Children: " + entity2.getName() + ", Types: " + entity2.getTypesList());
                    }
                    System.out.println("        ...");
                }
            }
            System.out.println("  ...");
        }

    }

    public static void entityTree(Client client) throws ReefServiceException {

        System.out.print("\n=== Entity Tree ===\n\n");

        EntityService entityService = client.getRpcInterface(EntityService.class);

        Entity point = entityService.getAllEntitiesWithType("Point").get(0);

        Entity.Builder treeRootBuilder = Entity.newBuilder();
        treeRootBuilder.setUuid(point.getUuid());

        Relationship.Builder relationship = Relationship.newBuilder();
        relationship.setRelationship("owns");
        relationship.setDescendantOf(false);
        relationship.setDistance(1);

        Entity.Builder firstEntity = Entity.newBuilder();
        firstEntity.addTypes("Equipment");

        Relationship.Builder higherRelationship = Relationship.newBuilder();
        higherRelationship.setRelationship("owns");
        higherRelationship.setDescendantOf(false);
        higherRelationship.setDistance(1);

        Entity.Builder secondEntity = Entity.newBuilder();
        secondEntity.addTypes("EquipmentGroup");

        higherRelationship.addEntities(secondEntity);

        firstEntity.addRelations(higherRelationship);

        relationship.addEntities(firstEntity);

        treeRootBuilder.addRelations(relationship);

        Entity treeRoot = treeRootBuilder.build();

        Entity result = entityService.getEntityTree(treeRoot);

        System.out.println("Root: " + result.getName() + ", Types: " + result.getTypesList());

        Relationship lowerRel = result.getRelations(0);

        System.out.println("+ Relationship: " + lowerRel.getRelationship() + ", Descendant: " + lowerRel.getDescendantOf() + ", Distance: " + lowerRel.getDistance());

        Entity middle = lowerRel.getEntities(0);

        System.out.println("  + Child: " + middle.getName() + ", Types: " + middle.getTypesList());

        Relationship higherRel = middle.getRelations(0);

        System.out.println("    + Relationship: " + higherRel.getRelationship() + ", Descendant: " + higherRel.getDescendantOf() + ", Distance: " + higherRel.getDistance());

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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);

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
