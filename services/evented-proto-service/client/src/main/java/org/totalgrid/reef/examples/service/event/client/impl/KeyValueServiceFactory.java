package org.totalgrid.reef.examples.service.event.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.ServiceProviderFactory;

public class KeyValueServiceFactory implements ServiceProviderFactory {

    @Override
    public Object createRpcProvider(Client client) {
        return new KeyValueServiceImpl(client);
    }
}
