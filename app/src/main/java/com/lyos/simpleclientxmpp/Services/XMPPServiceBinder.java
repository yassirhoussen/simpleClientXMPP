package com.lyos.simpleclientxmpp.Services;

import android.os.Binder;

/**
 * Created by lyos2210 on 28/07/14.
 */
public class XMPPServiceBinder extends Binder {

    private XMPPService service;

    public XMPPServiceBinder(){

    }

    public XMPPServiceBinder(XMPPService service) {
        this.service = service;
    }

    public XMPPService getService() {
        return service;
    }

    public void setService(XMPPService service) {
        this.service = service;
    }
}
