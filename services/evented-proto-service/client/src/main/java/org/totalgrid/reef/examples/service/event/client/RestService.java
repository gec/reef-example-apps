package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.RestMessage;

import java.util.List;

public interface RestService {

    RestMessage getMessage(String key) throws ReefServiceException;

    List<RestMessage> getAllMessages() throws ReefServiceException;

    RestMessage putMessage(String key, String value) throws ReefServiceException;

    void deleteMessage(String key) throws ReefServiceException;

    void deleteAllMessages() throws ReefServiceException;

}
