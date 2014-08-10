package com.lyos.simpleclientxmpp.view;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lyos.simpleclientxmpp.R;
import com.lyos.simpleclientxmpp.Services.XMPPService;
import com.lyos.simpleclientxmpp.Services.XMPPServiceBinder;
import com.lyos.simpleclientxmpp.model.AccountMode;
import com.lyos.simpleclientxmpp.model.User;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by lyos2210 on 25/07/14.
 */
public class Profile extends Fragment implements ServiceConnection  {

    private User currentUser;
    private EditText mode = null,
                     username = null,
                     password = null,
                     host     = null,
                     service  = null,
                     port     = null,
                     ressources = null,
                     priority   = null;

    private Button update = null;

    private XMPPService xmppService =  null;

    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static Profile newInstance(int sectionNumber) {
        Profile fragment = new Profile();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public Profile() {
        this.currentUser = Main.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_info, container, false);

        // bind service to current fragment
        Intent connectionService = new Intent(getActivity().getApplicationContext(), XMPPService.class);
        getActivity().bindService(connectionService, this, Context.BIND_AUTO_CREATE);

        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mode     = (EditText) rootView.findViewById(R.id.statusEdit);
        username = (EditText) rootView.findViewById(R.id.usernameEdit);
        password = (EditText) rootView.findViewById(R.id.passwordEdit);
        host     = (EditText) rootView.findViewById(R.id.hostEdit);
        service  = (EditText) rootView.findViewById(R.id.serviceEdit);
        port     = (EditText) rootView.findViewById(R.id.portEdit);
        ressources = (EditText) rootView.findViewById(R.id.ressourceEdit);
        priority = (EditText) rootView.findViewById(R.id.priorityEdit);

        this.initEditTextContent();

        mode.setOnFocusChangeListener(this.focusChangeListener);
        mode.setFocusable(false);
        mode.setFocusableInTouchMode(true);
        priority.setOnFocusChangeListener(this.focusChangeListener);

        update   = (Button) rootView.findViewById(R.id.update);
        update.setOnClickListener(this.onClickListener);

        return rootView;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.update) {
                Profile.this.updateUser(Profile.this.currentUser);
                if (Profile.this.currentUser.getMode() != AccountMode.AVAILABLE)
                    Profile.this.xmppService.updateStatus(
                            new Presence(
                                    AccountMode.getPresenceTypeFromStatus(
                                            Profile.this.currentUser.getMode()
                                    )
                            )
                    );
            }
        }
    };

    View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                AlertDialog.Builder builder = null;
                AlertDialog dialog = null;

                switch (v.getId()) {
                    // update user and status editText
                    case R.id.statusEdit:
                        final CharSequence[] items = new CharSequence[]{"available",
                                "offline",
                                "online",
                                "do not disturb",
                                "away",
                                "invisible",
                                "tchat"
                        };

                        builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Account status : ")
                                .setCancelable(false)
                                        //                            .setMessage(R.string.dialog_mood)
                                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        dialog.cancel();
                                    }
                                })
                                .setSingleChoiceItems(items, -1,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {
                                                switch (item) {
                                                    case 0:
                                                        Profile.this.mode.setText(items[0].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[0].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[0], Toast.LENGTH_SHORT).show();

                                                        break;
                                                    case 1:
                                                        Profile.this.mode.setText(items[1].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[1].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[1], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 2:
                                                        Profile.this.mode.setText(items[2].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[2].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[2], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 3:
                                                        Profile.this.mode.setText(items[3].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[3].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[3], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 4:
                                                        Profile.this.mode.setText(items[4].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[4].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[4], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 5:
                                                        Profile.this.mode.setText(items[5].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[5].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[5], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 6:
                                                        Profile.this.mode.setText(items[6].toString());
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(items[6].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), items[6], Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                            }
                                        }
                                );
                        dialog = builder.create();
                        dialog.show();

                        break;
                    case R.id.priorityEdit:
                        final CharSequence[] p_items = new CharSequence[]{"low  - 1 ", "medium - 2", "high - 3"};
                        builder = null;
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("priority status : ")
                                .setCancelable(false)
                                        //                            .setMessage(R.string.dialog_mood)
                                .setPositiveButton("update", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        dialog.cancel();
                                    }
                                })
                                .setSingleChoiceItems(p_items, -1,
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int item) {
                                                switch (item) {
                                                    case 0:
                                                        Profile.this.priority.setText("low - 0 ");
                                                        Profile.this.currentUser.setPriority(40);
                                                        Toast.makeText(getActivity().getApplicationContext(), p_items[0], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 1:
                                                        Profile.this.priority.setText("medium - 1 ");
                                                        Profile.this.currentUser.setPriority(41);
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(p_items[1].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), p_items[1], Toast.LENGTH_SHORT).show();
                                                        break;
                                                    case 2:
                                                        Profile.this.priority.setText("hight - 2 ");
                                                        Profile.this.currentUser.setPriority(42);
                                                        Profile.this.currentUser.setMode(AccountMode.getAccountStatusFromString(p_items[2].toString()));
                                                        Toast.makeText(getActivity().getApplicationContext(), p_items[2], Toast.LENGTH_SHORT).show();
                                                        break;
                                                }
                                            }
                                        }
                                );
                        dialog = builder.create();
                        dialog.show();
                        break;
                }
            }
        }
    };

    public void updateUser(User user) {
        Main.setUser(this.currentUser);
    }

    private void initEditTextContent() {
        this.mode.setText(AccountMode.getStringAccountStatus(this.currentUser.getMode()));
        this.username.setText((this.currentUser.getUsername()));
        this.username.setEnabled(false);
        this.password.setText(this.currentUser.getPassword());
        this.password.setEnabled(false);
        this.service.setText(this.currentUser.getService());
        this.service.setEnabled(false);
        this.host.setText(this.currentUser.getHost());
        this.host.setEnabled(false);
        this.port.setText(Integer.toString(this.currentUser.getPort()));
        this.port.setEnabled(false);
        this.ressources.setText(this.currentUser.getRessource());
        switch (this.currentUser.getPriority()) {
            case 40 :
                this.priority.setText("low");
                break;
            case 41 :
                this.priority.setText("medium");
                break;
            case 42 :
                this.priority.setText("high");
                break;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        this.xmppService = ((XMPPServiceBinder) service).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Toast.makeText(getActivity().getApplicationContext(),
                "OnServiceDisconnected",
                Toast.LENGTH_SHORT
        ).show();
    }
}
