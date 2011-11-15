package org.totalgrid.reef.examples.commands;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.CommandService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Commands.UserCommandRequest;
import org.totalgrid.reef.proto.Commands.CommandAccess;
import org.totalgrid.reef.proto.Commands.CommandStatus;
import org.totalgrid.reef.proto.Model;
import org.totalgrid.reef.proto.Model.Command;

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
        CommandService commandService = client.getRpcInterface(CommandService.class);

        // Get full list of Command objects in the system
        List<Command> commandList = commandService.getCommands();

        // Inspect the first Command object
        Command command = commandList.get(0);

        // Display properties of the Command object
        System.out.println("Command");
        System.out.println("-----------");
        System.out.println("Uuid: " + command.getUuid().getUuid());
        System.out.println("Name: " + command.getName());
        System.out.println("Display name: " + command.getDisplayName());
        System.out.println("Type: " + command.getType());
        System.out.println("Logical node: " + command.getLogicalNode().getName());
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
     * Execution locks are CommandAccess objects with the mode "ALLOWED"
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void executionLock(Client client) throws ReefServiceException {

        System.out.print("\n=== Execution Lock ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getRpcInterface(CommandService.class);

        // Get a single Command object
        Command command = commandService.getCommands().get(0);

        // Create a command execution lock for the Command object
        // CommandAccess objects describe executions locks
        CommandAccess commandAccess = commandService.createCommandExecutionLock(command);

        // Display the properties of the CommandAccess object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Uid: " + commandAccess.getUid());
        System.out.println("User: " + commandAccess.getUser());
        System.out.println("Access: " + commandAccess.getAccess());
        System.out.println("Expire Time: " + new Date(commandAccess.getExpireTime()));

        for (String cmd : commandAccess.getCommandsList()) {
            System.out.println("Command: " + cmd);
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandAccess);
    }

    /**
     * Multiple Execution Lock
     *
     * Locks may be acquired for multiple Commands to ensure exclusive access during operations
     * across different objects. This example acquires a lock for three Commands.
     *
     * Execution locks are CommandAccess objects with the mode "ALLOWED"
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void multipleExecutionLock(Client client) throws ReefServiceException {

        System.out.print("\n=== Multiple Execution Lock ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getRpcInterface(CommandService.class);

        // Get three Command objects
        List<Command> commandList = commandService.getCommands().subList(0, 3);

        // Create a command execution lock for the Command objects
        // CommandAccess objects describe executions locks
        CommandAccess commandAccess = commandService.createCommandExecutionLock(commandList);

        System.out.print("Locking commands: ");
        for (Command cmd: commandList) {
            System.out.print(cmd.getName() + " ");
        }
        System.out.print("\n\n");

        // Display the properties of the CommandAccess object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Uid: " + commandAccess.getUid());
        System.out.println("User: " + commandAccess.getUser());
        System.out.println("Access: " + commandAccess.getAccess());
        System.out.println("Expire Time: " + new Date(commandAccess.getExpireTime()));

        for (String cmd : commandAccess.getCommandsList()) {
            System.out.println("Command: " + cmd);
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandAccess);
    }

    /**
     * Command Blocking
     *
     * Command denial locks prevent all command execution. This examples acquires a denial lock
     * for three Commands.
     *
     * Execution locks are CommandAccess objects with the mode "BLOCKED"
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void commandBlocking(Client client) throws ReefServiceException {

        System.out.print("\n=== Execution Denial Lock ===\n\n");

        // Get service interface for commands
        CommandService commandService = client.getRpcInterface(CommandService.class);

        // Get three Command objects
        List<Command> commandList = commandService.getCommands().subList(0, 3);

        // Create a command denial lock for the Command objects
        // CommandAccess objects describe executions locks
        CommandAccess commandAccess = commandService.createCommandDenialLock(commandList);

        System.out.print("Locking commands: ");
        for (Command cmd: commandList) {
            System.out.print(cmd.getName() + " ");
        }
        System.out.print("\n\n");

        // Display the properties of the CommandAccess object
        System.out.println("Command Access");
        System.out.println("-----------");
        System.out.println("Uid: " + commandAccess.getUid());
        System.out.println("User: " + commandAccess.getUser());
        System.out.println("Access: " + commandAccess.getAccess());
        System.out.println("Expire Time: " + new Date(commandAccess.getExpireTime()));

        for (String cmd : commandAccess.getCommandsList()) {
            System.out.println("Command: " + cmd);
        }
        System.out.println("-----------\n");

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandAccess);
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
        CommandService commandService = client.getRpcInterface(CommandService.class);

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
        CommandAccess commandAccess = commandService.createCommandExecutionLock(control);

        System.out.println("Command access: " + commandAccess.getAccess());

        // Execute the control. The CommandStatus enumeration describes the result of the
        // execution ("SUCCESS" if successful)
        CommandStatus commandStatus = commandService.executeCommandAsControl(control);

        System.out.println("Command result: " + commandStatus);

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandAccess);

        // Get the history of command executions (UserCommandRequests) for the Command
        List<UserCommandRequest> commandRequestList = commandService.getCommandHistory(control);

        // Inspect the most recent command request
        UserCommandRequest lastRequest = commandRequestList.get(commandRequestList.size() - 1);

        // Display the properties of the control we executed
        System.out.println("\nUserCommandRequest");
        System.out.println("-----------");
        System.out.println("Uid: " + lastRequest.getUid());
        System.out.println("Status: " + lastRequest.getStatus());
        System.out.println("Name: " + lastRequest.getCommandRequest().getName());
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
        CommandService commandService = client.getRpcInterface(CommandService.class);

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
        CommandAccess commandAccess = commandService.createCommandExecutionLock(setpoint);

        System.out.println("Command access: " + commandAccess.getAccess());

        // Execute the control. The CommandStatus enumeration describes the result of the
        // execution ("SUCCESS" if successful)
        CommandStatus commandStatus = commandService.executeCommandAsSetpoint(setpoint, 35.323);

        System.out.println("Command result: " + commandStatus);

        // Remove the command lock from the system, cleaning up
        commandService.deleteCommandLock(commandAccess);

        // Get the history of command executions (UserCommandRequests) for the Command
        List<UserCommandRequest> commandRequestList = commandService.getCommandHistory(setpoint);

        // Inspect the most recent command request
        UserCommandRequest lastRequest = commandRequestList.get(commandRequestList.size() - 1);

        // Display the properties of the setpoint we executed
        System.out.println("\nUserCommandRequest");
        System.out.println("-----------");
        System.out.println("Uid: " + lastRequest.getUid());
        System.out.println("Status: " + lastRequest.getStatus());
        System.out.println("Name: " + lastRequest.getCommandRequest().getName());
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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);

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
