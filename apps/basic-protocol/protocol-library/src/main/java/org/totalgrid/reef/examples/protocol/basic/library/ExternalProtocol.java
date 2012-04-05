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
package org.totalgrid.reef.examples.protocol.basic.library;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ExternalProtocol implements ExternalCommandAcceptor {

    private Timer timer = null;

    static class UpdateTask extends TimerTask {
        private final ExternalUpdateAcceptor updateAcceptor;

        UpdateTask(ExternalUpdateAcceptor updateAcceptor) {
            this.updateAcceptor = updateAcceptor;
        }

        @Override
        public void run() {
            long time = System.currentTimeMillis();
            Random rand = new Random(time);
            updateAcceptor.handleUpdate("ExternalDevice.Point01", rand.nextDouble(), time);
        }
    }

    public boolean connect(ExternalUpdateAcceptor acceptor) {
        if (timer == null) {
            UpdateTask task = new UpdateTask(acceptor);
            timer = new Timer();
            timer.schedule(task, 1000, 1000);
            return true;
        } else {
            return false;
        }
    }

    public void disconnect() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public boolean handleCommand(String name) {
        System.out.println("Handled command: " + name);
        return true;
    }
}
