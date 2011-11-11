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

public class CommandsExample {

    public static void getCommands(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Commands ===\n\n");

        CommandService commandService = client.getRpcInterface(CommandService.class);

        List<Command> commandList = commandService.getCommands();

        Command command = commandList.get(0);

        System.out.println("Command");
        System.out.println("-----------");
        System.out.println("Uuid: " + command.getUuid().getUuid());
        System.out.println("Name: " + command.getName());
        System.out.println("Display name: " + command.getDisplayName());
        System.out.println("Type: " + command.getType());
        System.out.println("Logical node: " + command.getLogicalNode().getName());
        System.out.println("Entity: " + command.getEntity().getName());
        System.out.println("-----------\n");

        for (Command cmd : commandList) {
            System.out.println("Command: " + cmd.getName());
        }
    }


    public static void executionLock(Client client) throws ReefServiceException {

        System.out.print("\n=== Execution Lock ===\n\n");

        CommandService commandService = client.getRpcInterface(CommandService.class);

        Command command = commandService.getCommands().get(0);

        CommandAccess commandAccess = commandService.createCommandExecutionLock(command);

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

        commandService.deleteCommandLock(commandAccess);
    }


    public static void multipleExecutionLock(Client client) throws ReefServiceException {

        System.out.print("\n=== Multiple Execution Lock ===\n\n");

        CommandService commandService = client.getRpcInterface(CommandService.class);

        List<Command> commandList = commandService.getCommands().subList(0, 3);

        CommandAccess commandAccess = commandService.createCommandExecutionLock(commandList);

        System.out.print("Locking commands: ");
        for (Command cmd: commandList) {
            System.out.print(cmd.getName() + " ");
        }
        System.out.print("\n\n");

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

        commandService.deleteCommandLock(commandAccess);
    }


    public static void commandBlocking(Client client) throws ReefServiceException {

        System.out.print("\n=== Execution Denial Lock ===\n\n");

        CommandService commandService = client.getRpcInterface(CommandService.class);

        List<Command> commandList = commandService.getCommands().subList(0, 3);

        CommandAccess commandAccess = commandService.createCommandDenialLock(commandList);

        System.out.print("Locking commands: ");
        for (Command cmd: commandList) {
            System.out.print(cmd.getName() + " ");
        }
        System.out.print("\n\n");

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

        commandService.deleteCommandLock(commandAccess);
    }


    public static void executeControl(Client client) throws ReefServiceException {

        System.out.print("\n=== Execute Control ===\n\n");

        CommandService commandService = client.getRpcInterface(CommandService.class);

        List<Command> commandList = commandService.getCommands();

        Command control = null;
        for (Command cmd : commandList ) {
            if (cmd.getType() == Model.CommandType.CONTROL) {
                control = cmd;
                break;
            }
        }

        System.out.println("Command to execute: " + control.getName());

        CommandAccess commandAccess = commandService.createCommandExecutionLock(control);

        System.out.println("Command access: " + commandAccess.getAccess());

        CommandStatus commandStatus = commandService.executeCommandAsControl(control);

        System.out.println("Command result: " + commandStatus);

        commandService.deleteCommandLock(commandAccess);

        List<UserCommandRequest> commandRequestList = commandService.getCommandHistory(control);

        UserCommandRequest lastRequest = commandRequestList.get(commandRequestList.size() - 1);

        System.out.println("\nUserCommandRequest");
        System.out.println("-----------");
        System.out.println("Uid: " + lastRequest.getUid());
        System.out.println("Status: " + lastRequest.getStatus());
        System.out.println("Name: " + lastRequest.getCommandRequest().getName());
        System.out.println("User: " + lastRequest.getUser());
        System.out.println("Type: " + lastRequest.getCommandRequest().getType());
        System.out.println("-----------");
    }


    public static void executeSetpoint(Client client) throws ReefServiceException {

        System.out.print("\n=== Execute Setpoint ===\n\n");

        CommandService commandService = client.getRpcInterface(CommandService.class);

        List<Command> commandList = commandService.getCommands();

        Command control = null;
        for (Command cmd : commandList ) {
            if (cmd.getType() == Model.CommandType.SETPOINT_DOUBLE) {
                control = cmd;
                break;
            }
        }

        System.out.println("Command to execute: " + control.getName());

        CommandAccess commandAccess = commandService.createCommandExecutionLock(control);

        System.out.println("Command access: " + commandAccess.getAccess());

        CommandStatus commandStatus = commandService.executeCommandAsSetpoint(control, 35.323);

        System.out.println("Command result: " + commandStatus);

        commandService.deleteCommandLock(commandAccess);

        List<UserCommandRequest> commandRequestList = commandService.getCommandHistory(control);

        UserCommandRequest lastRequest = commandRequestList.get(commandRequestList.size() - 1);

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

            getCommands(client);

            executionLock(client);

            multipleExecutionLock(client);

            commandBlocking(client);

            executeControl(client);

            executeSetpoint(client);

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
