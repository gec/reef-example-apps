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
package org.totalgrid.reef.examples.protocol.basic.adapter;

import org.totalgrid.reef.client.service.proto.Commands;
import org.totalgrid.reef.examples.protocol.basic.library.ExternalCommandAcceptor;
import org.totalgrid.reef.protocol.api.CommandHandler;
import org.totalgrid.reef.protocol.api.Publisher;

public class CommandAdapter implements CommandHandler {

    private final ExternalCommandAcceptor acceptor;

    public CommandAdapter(ExternalCommandAcceptor acceptor) {
        this.acceptor = acceptor;
    }

    @Override
    public void issue(Commands.CommandRequest command, Publisher<Commands.CommandStatus> responsePublisher) {
        String name = command.getCommand().getName();

        boolean status = acceptor.handleCommand(name);
        
        if (status) {
            responsePublisher.publish(Commands.CommandStatus.SUCCESS);
        } else {
            responsePublisher.publish(Commands.CommandStatus.TIMEOUT);
        }
    }
}
