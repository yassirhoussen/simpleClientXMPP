package com.lyos.simpleclientxmpp.model;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by lyos2210 on 25/07/14.
 */
public enum AccountMode {
    /**
     * actually in tchatting with someone.
     */
    TCHAT,

    /**
     * Available to tchat and communicate
     */
    AVAILABLE,

    /**
     * offline. user is disconnected
     */
    OFFLINE,

    /**
     * online, user is connected. Set as Default value
     */
    ONLINE,

    /**
     * user is connected but not in front of his screen.
     */
    AWAY,

    /**
     * user is connected, but not available
     */
    DND,
    /*
     * user is unavailable for anything, it's more than DND
     */
    UNAVAILABLE;

    public static AccountMode getAccountStatusFromString(String text) {
        if (text != null) {
            for (AccountMode b : AccountMode.values()) {
                if (text.equalsIgnoreCase(b.toString())) {
                    return b;
                }
            }
        }
        return null;
    }

    public static String getStringAccountStatus(AccountMode status) {
        if (status != null) {
            return status.name();
        }
        return null;
    }

    public static Presence.Mode getPresenceModeFromStatus(AccountMode status) {
        switch (status) {
            case  TCHAT:
                return Presence.Mode.chat;
            case AVAILABLE:
                return Presence.Mode.available;
            case OFFLINE:
                return Presence.Mode.xa;
            case ONLINE:
                return Presence.Mode.available;
            case AWAY:
                return Presence.Mode.away;
            case DND:
                return Presence.Mode.dnd;
            case UNAVAILABLE:
                return Presence.Mode.xa;
            default:
                return null;
        }
    }

    public static Presence.Type getPresenceTypeFromStatus(AccountMode status) {
        switch (status) {
            case  TCHAT:
                return Presence.Type.available;
            case AVAILABLE:
                return Presence.Type.available;
            case OFFLINE:
                return Presence.Type.unavailable;
            case ONLINE:
                return Presence.Type.available;
            case AWAY:
                return Presence.Type.unavailable;
            case DND:
                return Presence.Type.unavailable;
            case UNAVAILABLE:
                return Presence.Type.unavailable;
            default:
                return null;
        }
    }

    public static AccountMode getStatusFromPresence(Presence userPresence) {
        if(userPresence.getType().equals(Presence.Type.unavailable))
            return AccountMode.OFFLINE;
        else {
            Presence.Mode userMode = userPresence.getMode();
            if(userMode == null)
                return AccountMode.OFFLINE;
            else
                switch (userMode){
                    case available:
                        return AccountMode.AVAILABLE;
                    case away:
                        return AccountMode.AWAY;
                    case dnd:
                        return AccountMode.DND;
                    case xa:
                        return AccountMode.UNAVAILABLE;
                    case chat:
                        return AccountMode.TCHAT;
                    default:
                        return AccountMode.OFFLINE;
                }
        }
    }
}
