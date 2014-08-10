package com.lyos.simpleclientxmpp.model;

import java.util.List;

/**
 * Created by lyos2210 on 25/07/14.
 */
public class User {

   private String        host;
   private String        service;
   private int           port = 5222; // default value
   private String        username;
   private String        password;
   private AccountMode   mode = AccountMode.AVAILABLE;
   private String        ressource = "Android";
   private int           priority = 42; // default value

   private List<ContactAccount> contactList = null;

   public User(){

   }

   public User(String host, String service, int port, String username, String password) {
       this.host     = host;
       this.service  = service;
       this.port     = port;
       this.username = username;
       this.password = password;
   }

    public User(String host, String username, String password) {
        this.host     = host;
        this.service  = host;
        this.username = username;
        this.password = password;
    }

    public ContactAccount searchContact(String value) {
            for ( ContactAccount account : this.contactList ) {
                if (account.getJID().equals(value) || account.getName().equals(value)) {
                    return account;
                }
            }
            return null;
    }

    //sort object by their attribute accountStatus
    //Online, Tchat, away,dnd, unavailable, offline
    public List<ContactAccount> SortListAccount(AccountMode status) {
       return null;
    }

    public void addContactToList(ContactAccount contact) {

    }

    public void removeContactFromList(ContactAccount contact) {

    }



    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountMode getMode() {
        return mode;
    }

    public void setMode(AccountMode status) {
        this.mode = status;
    }

    public String getRessource() {
        return ressource;
    }

    public void setRessource(String ressource) {
        this.ressource = ressource;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    public List<ContactAccount> getContactList() {
        return contactList;
    }

    public void setContactList(List<ContactAccount> contactList) {
        this.contactList = contactList;
    }


}
