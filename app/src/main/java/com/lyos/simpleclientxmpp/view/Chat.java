package com.lyos.simpleclientxmpp.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lyos.simpleclientxmpp.R;
import com.lyos.simpleclientxmpp.Services.XMPPService;
import com.lyos.simpleclientxmpp.Services.XMPPServiceBinder;
import com.lyos.simpleclientxmpp.model.ChatMessage;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.util.StringUtils;

public class Chat extends Activity implements ServiceConnection {

    private static final String TAG         = "chat_activity";
    private XMPPService xmppService         = null;
    private ChatMessageList chatMessageList = null;
    private Handler myHandler               = null;
    private ArrayAdapter<ChatMessage> chatMessageAdapter;

    private static final String MESSAGES    = "messages";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // attach service to activity
        Intent connectionService = new Intent(this, XMPPService.class);
        bindService(connectionService, this, Context.BIND_AUTO_CREATE);

        // get args from intent as user
        // the user value will contains the receipient
        Intent intent = getIntent();
        CharSequence recipientContact = intent.getCharSequenceExtra("user");

        this.myHandler = new Handler();

        final TextView recipient = (TextView) this.findViewById(R.id.recipient);
        recipient.setText(recipientContact);
        final EditText message = (EditText) this.findViewById(R.id.embedded_text_editor);
        ListView list = (ListView) this.findViewById(R.id.thread);


        this.chatMessageList = (ChatMessageList) (savedInstanceState != null ? savedInstanceState.getParcelable(MESSAGES) : null);
        if ( this.chatMessageList == null ) {
            this.chatMessageList = new ChatMessageList();
        }

        chatMessageAdapter = new MessageAdapter(this, R.layout.multi_line_list_item, this.chatMessageList);
        list.setAdapter(chatMessageAdapter);


        Button send = (Button) this.findViewById(R.id.send_button);
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String to = recipient.getText().toString();
                String text = message.getText().toString();
                Toast.makeText(Chat.this, "to : " + to + "text : " + text, Toast.LENGTH_SHORT).show();
                Chat.this.xmppService.sendMessage(to, text);
                ChatMessage chatMessage = new ChatMessage(to, text);
                Chat.this.chatMessageList.getMessages().add(chatMessage);
                Chat.this.chatMessageAdapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.i(TAG, "Chat service connected!");
        this.xmppService = ((XMPPServiceBinder) service).getService();

        PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
        xmppService.getConnection().addPacketListener( new PacketListener() {
            public void processPacket(Packet packet) {
                Message message = (Message) packet;
                if (message.getBody() != null) {
                    ChatMessage chatMessage = new ChatMessage(message.getFrom(), message.getBody());
                    Chat.this.chatMessageList.getMessages().add(chatMessage);
                    Chat.this.myHandler.post(new Runnable() {
                        public void run() {
                            Chat.this.chatMessageAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        }, filter);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i(TAG, "Chat service disConnected!");
    }

    private class MessageAdapter extends ArrayAdapter<ChatMessage> {

        Context context;

        MessageAdapter(Context context, int resource, ChatMessageList objects) {
            super(context, resource, objects.getMessages());
            this.context=context;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View row = View.inflate(context, R.layout.multi_line_list_item, null);

            if ( position < chatMessageList.getMessages().size() ) {

                ChatMessage mess = chatMessageList.getMessages().get(position);
                TextView message=(TextView)row.findViewById(R.id.message);
                message.setText(mess.getBody());

                TextView from=(TextView)row.findViewById(R.id.from);
                if ( mess.getFrom() != null ) {
                    String fromName = StringUtils.parseBareAddress(mess.getFrom());
                    from.setText(fromName +" :");
                } else {
                    from.setText("me :");
                }

                ImageView icon=(ImageView)row.findViewById(R.id.icon);
                if ( position % 2 == 0) {
                    icon.setImageResource(R.drawable.bebe_gnu_small);
                } else {
                    icon.setImageResource(R.drawable.bebe_tux_small);
                }
            }
            return(row);
        }
    }
}
