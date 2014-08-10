package com.lyos.simpleclientxmpp.view;

import android.app.Dialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyos.simpleclientxmpp.R;
import com.lyos.simpleclientxmpp.Services.XMPPService;
import com.lyos.simpleclientxmpp.Services.XMPPServiceBinder;
import com.lyos.simpleclientxmpp.model.ContactAccount;
import com.lyos.simpleclientxmpp.model.User;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.jivesoftware.smack.util.StringUtils.parseBareAddress;

/**
 * Created by lyos2210 on 28/07/14.
 */


public class Contact extends Fragment implements ServiceConnection {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private static final String TAG                = "ContactFragment";

    private List<ContactAccount> myListContact = null;
    private ListViewAdapter listViewAdapter = null;
    private Handler myHandler                  = null;

    private User currentUser  = null;
    private boolean isContactDeleted = false;

    // constructor
    public Contact() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Contact newInstance(int sectionNumber) {
        Contact fragment = new Contact();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contact_fragment, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listView);

        // bind service to current fragment
        Intent connectionService = new Intent(getActivity().getApplicationContext(), XMPPService.class);
        getActivity().bindService(connectionService, this, Context.BIND_AUTO_CREATE);

        this.myHandler = new Handler();
        myListContact = new ArrayList<ContactAccount>();

        this.currentUser = Main.getUser();

        // init the list Adapter
        this.listViewAdapter = new ListViewAdapter(
                getActivity().getApplicationContext(),
                R.layout.account_line_list_item,
                this.myListContact
        );

        // connect the arrayAdapter to the listview
        listView.setAdapter(this.listViewAdapter);


        // add a click item listener on listview
        // on item click start new activity to tchat with selected rooster entry
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Dialog dialog = new Dialog(getActivity());

                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_custom);

                TextView name = (TextView)dialog.findViewById(R.id.nameText);
                TextView jid = (TextView) dialog.findViewById(R.id.jidText);
                TextView status = (TextView) dialog.findViewById(R.id.statusText);
                TextView presence = (TextView) dialog.findViewById(R.id.presenceText);

                final ContactAccount contactCurrent = Contact.this.currentUser.getContactList().get(position);
                name.setText(contactCurrent.getName());
                jid.setText(contactCurrent.getJID());
                status.setText(contactCurrent.getStatus());
                // si null
                String s_presence = (contactCurrent.getMode() == null) ? "Offline" : String.valueOf(contactCurrent.getMode());
                presence.setText(s_presence);

                Button buttonStart = (Button)dialog.findViewById(R.id.button_start);
                buttonStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!Contact.this.isContactDeleted) {
                            Intent chatIntent = new Intent(getActivity().getApplicationContext(), Chat.class);
                            chatIntent.putExtra("user", contactCurrent.getJID());
                            getActivity().startActivity(chatIntent);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Contact Deleted, cannot chat with it", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });

                    Button buttonDelete = (Button) dialog.findViewById(R.id.button_delete);
                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Contact.this.myListContact.remove(contactCurrent);
                        Contact.this.isContactDeleted = true;
                        Contact.this.listViewAdapter.notifyDataSetChanged();
                        Toast.makeText(getActivity().getApplicationContext(), "removed contact :" + contactCurrent.getJID(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                Contact.this.isContactDeleted = false;
                dialog.show();
//                ContactAccount contact = Contact.this.myListContact.get(position);
//                Log.d(TAG, "Click on item listener at " + position);
//                Intent chatIntent = new Intent(getActivity().getApplicationContext(), Chat.class);
//                chatIntent.putExtra("user", contact.getJID());
//                getActivity().startActivity(chatIntent);
            }
        });
        return rootView;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Toast.makeText(getActivity().getApplicationContext(),
                "OnServiceConnected",
                Toast.LENGTH_SHORT
        ).show();
        XMPPService xmppService = ((XMPPServiceBinder) service).getService();
        Roster roster = xmppService.getRooster();
        if (roster == null)
            Toast.makeText(getActivity().getApplicationContext(),"roster Empty",Toast.LENGTH_SHORT).show();
        // populate List<ContactListAccount> form the RosterEntry
        Collection<RosterEntry> entries  = xmppService.getContactList();
        if (!entries.isEmpty()) {
            for (RosterEntry entry : entries) {
                ContactAccount current = new ContactAccount();
                current.setName(entry.getName());
                current.setJID(entry.getUser());
                Presence presence = roster.getPresence(entry.getUser());
                current.setMode(presence.getMode());
                current.setStatus(presence.getStatus());
                this.myListContact.add(current);
            }
        }

        this.currentUser.setContactList(this.myListContact);
        // add to listener to the roster
        if (roster != null)
            roster.addRosterListener(new RosterListener() {
                @Override
                public void entriesAdded(Collection<String> strings) {
                    Log.i(TAG, "RosterListener EntriesAdded: " + strings.toString());
                }

                @Override
                public void entriesUpdated(Collection<String> strings) {
                    Log.i(TAG, "RosterListener EntriesUpdated: " + strings.toString());
                }

                @Override
                public void entriesDeleted(Collection<String> strings) {
                    Log.i(TAG, "RosterListener EntriesDeleted: " + strings.toString());
                }

                @Override
                public void presenceChanged(Presence presence) {
                    Log.i(TAG, "RosterListener Presence changed: "
                            + presence.getFrom() +
                            " " +
                            presence
                    );
                    if ( presence.getFrom() != null ) {
                        ContactAccount account;
                        account = Contact.this.currentUser.searchContact(
                                parseBareAddress(presence.getFrom())
                        );
                        if ( account != null ) {
                            if ( Presence.Type.available.equals(presence.getType()) ) {
                                if (presence.getMode() == null ) {
                                    account.setMode(Presence.Mode.xa);
                                } else {
                                    account.setMode(presence.getMode());
                                }
                                account.setStatus(presence.getStatus());
                            } else if (Presence.Type.unavailable.equals(presence.getType()) ) {
                                account.setMode(null);
                            }
                            // inform the adapter that element have changed
                            Contact.this.myHandler.post(new Runnable() {
                                public void run() {
                                    Contact.this.listViewAdapter.notifyDataSetChanged();
                                }
                            });
                        }
                    }
                }
            });
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(getActivity().getApplicationContext(),
                "OnServiceDisconnected",
                Toast.LENGTH_SHORT
        ).show();
    }

    // this private class populate the listview with value from the roster
    // all data from the roster are add to List<ContactListAccount>
    private class ListViewAdapter extends ArrayAdapter<ContactAccount> {

        private Context context;

        public ListViewAdapter(Context context, int resource, List<ContactAccount> list) {
            super(context, resource, list);
            this.context = context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View view = View.inflate(context, R.layout.account_line_list_item, null);
            if (position < Contact.this.myListContact.size()) {
                ContactAccount currentContact = Contact.this.myListContact.get(position);
                if (currentContact != null) {
                    if (currentContact.getName() != null) {
                        TextView login = (TextView) view.findViewById(R.id.name);
                        login.setText(currentContact.getName());
                    } else if (currentContact.getJID() != null) {
                        TextView login = (TextView) view.findViewById(R.id.name);
                        login.setText(currentContact.getJID());
                    }

                    if (currentContact.getStatus() != null) {
                        TextView message = (TextView) view.findViewById(R.id.message);
                        message.setText(currentContact.getStatus());
                    }

                    if (currentContact.getMode() != null) {
                        ImageView icon = (ImageView) view.findViewById(R.id.icon_presence);
                        switch (currentContact.getMode()) {
                            case chat:
                                icon.setImageResource(R.drawable.chat);
                                break;

                            case available:
                                icon.setImageResource(R.drawable.available);
                                break;

                            case away:
                                icon.setImageResource(R.drawable.away);
                                break;

                            case xa:
                                icon.setImageResource(R.drawable.offline);
                                break;
                            case dnd:
                                icon.setImageResource(R.drawable.busy);
                                break;
                        }
                    }
                }
            }
            return view;
        }
    } // fin class interne

}
