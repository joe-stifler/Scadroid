package com.scadroid.interfaces;

import com.scadroid.webservice.request.ServerStatus;

/**
 * Created by joe on 9/1/15.
 */
public interface StatusServerInterface {
    // catch informations from server
    public void receiveStatus(ServerStatus serverStatus);
}
