package org.totalgrid.reef.examples.stateoptimizer;


import org.totalgrid.reef.client.exception.ExpectationException;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.CommandService;
import org.totalgrid.reef.client.service.proto.Commands;
import org.totalgrid.reef.client.service.proto.Model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * issues Commands on behalf of the client code. Selects all commands in the system before issuing command.
 * <p/>
 * toString is overloaded to display all commands in the system
 */
public class CommandIssuer {

    /**
     * service we use to get Select and issue command
     */
    private CommandService commandService;

    /**
     * map of all commands we may operate on.
     */
    private Map<String, Model.Command> commandsToLock = new HashMap<String, Model.Command>();

    public CommandIssuer(CommandService commandService) throws ReefServiceException {
        this.commandService = commandService;

        // get list of all commands in system
        List<Model.Command> commands = commandService.getCommands();
        // TODO: filter system commands for only commands we are using
        for (Model.Command cmd : commands) {
            commandsToLock.put(cmd.getName(), cmd);
        }
    }

    /**
     * Issue a command request for the command.
     *
     * @param commandName name of the command
     * @throws ReefServiceException     if there is an issue executing commands in the system
     * @throws IllegalArgumentException if we attempt to execute an unknown command
     */
    public void issueCommand(String commandName) throws ReefServiceException, IllegalArgumentException {

        Model.Command cmd = commandsToLock.get(commandName);
        if (cmd == null)
            throw new IllegalArgumentException("No command with commandName: " + commandName + ". Known commands are :" + commandsToLock.keySet());

        // lock all commands in the system
        Commands.CommandLock select = commandService.createCommandExecutionLock(new LinkedList<Model.Command>(commandsToLock.values()));
        try {
            Commands.CommandStatus result = commandService.executeCommandAsControl(cmd);
            System.out.println("Executed command: " + commandName + " result: " + result);
            if (result != Commands.CommandStatus.SUCCESS) {
                throw new ExpectationException("Command: " + commandName + " didn't result in SUCCESS. ( " + result + ")");
            }
        } finally {
            try {
                commandService.deleteCommandLock(select);
            } catch (ReefServiceException e) {
                System.out.println("Error deleting select: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * @return names of all the commands in the system
     */
    public List<String> getCommandNames() {
        return new LinkedList<String>(commandsToLock.keySet());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Model.Command c : commandsToLock.values()) {
            sb.append(c.getName());
            sb.append("\n");
        }
        return sb.toString();
    }
}
