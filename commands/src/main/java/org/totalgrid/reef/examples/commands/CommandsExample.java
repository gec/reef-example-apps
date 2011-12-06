package org.totalgrid.reef.examples.commands;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.CommandService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Commands.UserCommandRequest;
import org.totalgrid.reef.client.service.proto.Commands.CommandLock;
import org.totalgrid.reef.client.service.proto.Commands.CommandStatus;
import org.totalgrid.reef.client.service.proto.Model;
import org.totalgrid.reef.client.service.proto.Model.Command;

import java.util.Date;
import java.util.List;

/**
 * Example: Commands
 *
 *
 */
public class CommandsExample {

    /**
     * Get Commands
     *
     * Retrieves a list of commands configured in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getCommands(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Commands ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getService(CommandService.class);

        // Get full list of Command objects in the system
        List<Command> commandList = commandService.getCommands();

        // Inspect the first Command object
        Command command = commandList.get(0);

        // Display properties of the Command object
        System.out.println("Command");
        System.out.println("-----------");
        System.out.println("Uuid: " + command.getUuid().getValue());
        System.out.println("Name: " + command.getName());
        System.out.println("Display name: " + command.getDisplayName());
        System.out.println("Type: " + command.getType());
        System.out.println("Logical node: " + command.getEndpoint().getName());
        System.out.println("Entity: " + command.getEntity().getName());
        System.out.println("-----------\n");

        // List the Command objects
        for (Command cmd : commandList) {
            System.out.println("Command: " + cmd.getName());
        }
    }

    /**
     * Execution Lock
     *
     * Before executing a command, agents must acquire exclusive access to prevent
     * simultaneous executions from other agents. This example acquires a lock
     * for a single Command.
     *
     * Execution locks are CommandLock objects with the mode "ALLOWED"
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void executionLock(Client client) throws ReefServiceException {

        System.out.print("\n=== Execution Lock ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getService(CommandService.class);

        // Get a single Command object
        Command command = commandService.getCommands().get(0);

        // Create a command execution lock for the Command object
        // CommandLock objects describe executions locks
        CommandLock commandLock = commandService.createCommandExecutionLock(command);

        // Display the properties of the CommandLock object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Uid: " + commandLock.getId());
        System.out.println("User: " + commandLock.getUser());
        System.out.println("Access: " + commandLock.getAccess());
        System.out.println("Expire Time: " + new Date(commandLock.getExpireTime()));

        for (Command cmd : commandLock.getCommandsList()) {
            System.out.println("Command: " + cmd.getName());
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandLock);
    }

    /**
     * Multiple Execution Lock
     *
     * Locks may be acquired for multiple Commands to ensure exclusive access during operations
     * across different objects. This example acquires a lock for three Commands.
     *
     * Execution locks are CommandLock objects with the mode "ALLOWED"
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void multipleExecutionLock(Client client) throws ReefServiceException {

        System.out.print("\n=== Multiple Execution Lock ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getService(CommandService.class);

        // Get three Command objects
        List<Command> commandList = commandService.getCommands().subList(0, 3);

        // Create a command execution lock for the Command objects
        // CommandLock objects describe executions locks
        CommandLock commandLock = commandService.createCommandExecutionLock(commandList);

        System.out.print("Locking commands: ");
        for (Command cmd: commandList) {
            System.out.print(cmd.getName() + " ");
        }
        System.out.print("\n\n");

        // Display the properties of the CommandLock object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Uid: " + commandLock.getId());
        System.out.println("User: " + commandLock.getUser());
        System.out.println("Access: " + commandLock.getAccess());
        System.out.println("Expire Time: " + new Date(commandLock.getExpireTime()));

        for (Command cmd : commandLock.getCommandsList()) {
            System.out.println("Command: " + cmd.getName());
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandLock);
    }

    /**
     * Command Blocking
     *
     * Command denial locks prevent all command execution. This examples acquires a denial lock
     * for three Commands.
     *
     * Execution locks are CommandLock objects with the mode "BLOCKED"
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void commandBlocking(Client client) throws ReefServiceException {

        System.out.print("\n=== Execution Denial Lock ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getService(CommandService.class);

        // Get three Command objects
        List<Command> commandList = commandService.getCommands().subList(0, 3);

        // Create a command denial lock for the Command objects
        // CommandLock objects describe executions locks
        CommandLock commandLock = commandService.createCommandDenialLock(commandList);

        System.out.print("Locking commands: ");
        for (Command cmd: commandList) {
            System.out.print(cmd.getName() + " ");
        }
        System.out.print("\n\n");

        // Display the properties of the CommandLock object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Uid: " + commandLock.getId());
        System.out.println("User: " + commandLock.getUser());
        System.out.println("Access: " + commandLock.getAccess());
        System.out.println("Expire Time: " + new Date(commandLock.getExpireTime()));

        for (Command cmd : commandLock.getCommandsList()) {
            System.out.println("Command: " + cmd.getName());
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandLock);
    }

    /**
     * Execute Control
     *
     * Controls are Commands that do not have a value associated with them.
     * The procedure is to acquire an execution lock, then execute the control.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void executeControl(Client client) throws ReefServiceException {

        System.out.print("\n=== Execute Control ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getService(CommandService.class);

        // Get list of all Commands
        List<Command> commandList = commandService.getCommands();

        // Find a Command of type "CONTROL"
        Command control = null;
        for (Command cmd : commandList ) {
            if (cmd.getType() == Model.CommandType.CONTROL) {
                control = cmd;
                break;
            }
        }

        System.out.println("Command to execute: " + control.getName());

        // Get execution lock for selected control
        CommandLock commandLock = commandService.createCommandExecutionLock(control);

        System.out.println("Command access: " + commandLock.getAccess());

        // Execute the control. The CommandStatus enumeration describes the result of the
        // execution ("SUCCESS" if successful)
        CommandStatus commandStatus = commandService.executeCommandAsControl(control);

        System.out.println("Command result: " + commandStatus);

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandLock);

        // Get the history of command executions (UserCommandRequests) for the Command
        List<UserCommandRequest> commandRequestList = commandService.getCommandHistory(control);

        // Inspect the most recent command request
        UserCommandRequest lastRequest = commandRequestList.get(commandRequestList.size() - 1);

        // Display the properties of the control we executed
        System.out.println("\nUserCommandRequest");
        System.out.println("-----------");
        System.out.println("Uid: " + lastRequest.getId());
        System.out.println("Status: " + lastRequest.getStatus());
        System.out.println("Name: " + lastRequest.getCommandRequest().getCommand().getName());
        System.out.println("User: " + lastRequest.getUser());
        System.out.println("Type: " + lastRequest.getCommandRequest().getType());
        System.out.println("-----------");
    }

    /**
     * Execute Setpoint
     *
     * Setpoints are Commands that have a value associated with them.
     * The procedure is to acquire an execution lock, then execute the setpoint.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void executeSetpoint(Client client) throws ReefServiceException {

        System.out.print("\n=== Execute Setpoint ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getService(CommandService.class);

        // Get list of all Commands
        List<Command> commandList = commandService.getCommands();

        // Find a Command of type "SETPOINT_DOUBLE"
        Command setpoint = null;
        for (Command cmd : commandList ) {
            if (cmd.getType() == Model.CommandType.SETPOINT_DOUBLE) {
                setpoint = cmd;
                break;
            }
        }

        System.out.println("Command to execute: " + setpoint.getName());

        // Get execution lock for selected setpoint
        CommandLock commandLock = commandService.createCommandExecutionLock(setpoint);

        System.out.println("Command access: " + commandLock.getAccess());

        // Execute the control. The CommandStatus enumeration describes the result of the
        // execution ("SUCCESS" if successful)
        CommandStatus commandStatus = commandService.executeCommandAsSetpoint(setpoint, 35.323);

        System.out.println("Command result: " + commandStatus);

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandLock);

        // Get the history of command executions (UserCommandRequests) for the Command
        List<UserCommandRequest> commandRequestList = commandService.getCommandHistory(setpoint);

        // Inspect the most recent command request
        UserCommandRequest lastRequest = commandRequestList.get(commandRequestList.size() - 1);

        // Display the properties of the setpoint we executed
        System.out.println("\nUserCommandRequest");
        System.out.println("-----------");
        System.out.println("Uid: " + lastRequest.getId());
        System.out.println("Status: " + lastRequest.getStatus());
        System.out.println("Name: " + lastRequest.getCommandRequest().getCommand().getName());
        System.out.println("User: " + lastRequest.getUser());
        System.out.println("Type: " + lastRequest.getCommandRequest().getType());
        System.out.println("Value: " + lastRequest.getCommandRequest().getDoubleVal());
        System.out.println("-----------");
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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, ReefServices.getInstance());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getCommands(client);

            executionLock(client);

            multipleExecutionLock(client);

            commandBlocking(client);

            executeControl(client);

            executeSetpoint(client);

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
