package com.lyos.simpleclientxmpp.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.lyos.simpleclientxmpp.model.AccountMode;
import com.lyos.simpleclientxmpp.model.User;
import com.lyos.simpleclientxmpp.view.Connection;
import com.lyos.simpleclientxmpp.view.Main;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;

import java.io.IOException;
import java.util.Collection;

public class XMPPService extends Service {

    private final static String TAG = "XMPPSERVICE";
    private static User currentUser = null;
    private XMPPServiceBinder binder = null;

    private XMPPConnection xmppConnection = null;

    public XMPPService() {
    }

    @Override
    public void onCreate(){
        super.onCreate();
        StrictMode.ThreadPolicy policy = new StrictMode.
                ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        this.currentUser = Connection.getUser();
        binder = new XMPPServiceBinder(this);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }


    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            this.startConnection();
            Toast.makeText(this,"success Connection",Toast.LENGTH_SHORT).show();
            Intent openIntent = new Intent(getBaseContext(), Main.class);
            openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getApplication().startActivity(openIntent);
            return START_NOT_STICKY;
    }

    public void startConnection() {
        try {
            ConnectionConfiguration configuration = null;
            if (!currentUser.getService().isEmpty())
                configuration = new ConnectionConfiguration(currentUser.getHost(), currentUser.getPort(), currentUser.getService());
            else
                configuration = new ConnectionConfiguration(currentUser.getHost());
            configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            this.xmppConnection = new XMPPTCPConnection(configuration);
            this.xmppConnection.connect();
            this.xmppConnection.login(
                    this.currentUser.getUsername(),
                    this.currentUser.getPassword(),
                    this.currentUser.getRessource()
            );
            this.xmppConnection.sendPacket(new Presence(AccountMode.getPresenceTypeFromStatus(this.currentUser.getMode())));
        } catch (XMPPException e) {
            Log.d(TAG, "startConnection XMPPException: " + e.getMessage().toString());
        } catch (SmackException.NotConnectedException e) {
            Log.d(TAG, "startConnection SmackException: " + e.getMessage().toString());
        } catch (SaslException e) {
            Log.d(TAG, "startConnection SaslException: " + e.getMessage().toString());
        } catch (SmackException e) {
            Log.d(TAG, "startConnection SmackException: " + e.getMessage().toString());
        } catch (IOException e) {
            Log.d(TAG, "startConnection IOException: " + e.getMessage().toString());
        }
    }

    public void closeConection() {
        this.xmppConnection = null;
        this.closeConection();
    }

    public void updateStatus(Presence presence) {
        try {
            if (this.xmppConnection != null && this.xmppConnection.isConnected()){
                this.xmppConnection.sendPacket(presence);
            }
        }catch (Exception e ) {
            Log.d(TAG, "error updateStatus" + e.getMessage().toString());
        }
    }

    public void sendMessage(String to, String s_message) {
        Message newMessage = new Message(to, Message.Type.chat);
        newMessage.setBody(s_message);
        if (this.xmppConnection != null && this.xmppConnection.isConnected()){
            try {
                this.xmppConnection.sendPacket(newMessage);
            } catch (Exception e) {
                Log.d(TAG, "error sendMessage" + e.getMessage().toString());
            }
        }
    }

    // get all Roster Entries from Server
    public Collection<RosterEntry> getContactList() {
        Collection<RosterEntry> collectionRoster = null;
        if (this.xmppConnection != null && this.xmppConnection.isConnected()){
            collectionRoster = this.xmppConnection.getRoster().getEntries();
        }
        return collectionRoster;
    }

    // get Rooster from Server
    public Roster getRooster() {
        Roster rooster = null;
        if (this.xmppConnection != null && this.xmppConnection.isConnected()){
            rooster = this.xmppConnection.getRoster();
        }
        return rooster;
    }

    public XMPPConnection getConnection () {
        return this.xmppConnection;
    }


    public void AddNewContactToList() {

    }

    public void removeContactToList() {

    }
}
