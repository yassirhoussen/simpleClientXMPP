package com.lyos.simpleclientxmpp.model;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by lyos2210 on 28/07/14.
 */
public class ContactAccount {

    private String name = "";
    private String status ;
    private String JID = "";
    private Presence.Mode mode;

    public ContactAccount() {

    }

    public ContactAccount(String name, String status, String JID, Presence.Mode mode) {
        this.name   = name;
        this.status = status;
        this.JID    = JID;
        this.mode   = mode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJID() {
        return JID;
    }

    public void setJID(String JID) {
        this.JID = JID;
    }


    public Presence.Mode getMode() {
        return mode;
    }

    public void setMode(Presence.Mode mode) {
        this.mode = mode;
    }

}
