package com.lyos.simpleclientxmpp.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lyos.simpleclientxmpp.R;
import com.lyos.simpleclientxmpp.Services.XMPPService;
import com.lyos.simpleclientxmpp.model.User;


public class Connection extends Activity implements View.OnClickListener {

    private static User user         = null;

    private EditText host     = null,
                     service  = null,
                     port     = null,
                     username = null,
                     password = null;

    private Button   validate   = null;


    private class StartServiceTask extends AsyncTask<Intent, Void, String> {

        private final ProgressDialog dialog = new ProgressDialog(Connection.this);
        private final static String TAG ="StartServiceTask";
        boolean checkInternet = false;


        protected void onPreExecute() {
                this.dialog.setMessage(Connection.this.getString(R.string.connection_in_progress));
                this.dialog.show();
        }
        // automatically done on worker thread (separate from UI thread)
        protected String doInBackground(final Intent... intents) {
            checkInternet = this.checkInternetConnection();
            if (checkInternet) {
                startService(intents[0]);
                return null;
            }
            return null;
        }

        protected void onPostExecute(final String result) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
        }

        private boolean checkInternetConnection() {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            // test for connection
            if (cm.getActiveNetworkInfo() != null
                    && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected()) {
                return true;
            } else {
                Log.d(TAG, "Internet Connection Not Present");
                return false;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        this.host    = (EditText) findViewById(R.id.hostEdit);
        this.service = (EditText) findViewById(R.id.serviceEdit);
        this.port    = (EditText) findViewById(R.id.portEdit);
        this.username = (EditText) findViewById(R.id.usernameEdit);
        this.password = (EditText) findViewById(R.id.passwordEdit);

        this.validate = (Button) findViewById(R.id.connect);
        this.validate.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.connection, menu);
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
    public void onClick(View v) {
        if (v.getId() == R.id.connect) {
            String s_host    = this.host.getText().toString();
            String s_service = this.service.getText().toString();
            int s_port       = Integer.parseInt(this.port.getText().toString());
            String s_user    = this.username.getText().toString();
            String s_pwd     = this.password.getText().toString();
            if (!checkIfEmptyContent(s_host) &&
                    !checkIfEmptyContent(s_service) &&
                    !checkIfEmptyContent(s_port + "") &&
                    !checkIfEmptyContent(s_user)    &&
                    !checkIfEmptyContent(s_pwd)) {
                Connection.user = new User(s_host, s_user, s_pwd);
                Intent newIntent = new Intent(this, XMPPService.class);
                new StartServiceTask().execute(newIntent);
            }
            else {
                Toast.makeText(this," Connection failed due to some empty area",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean checkIfEmptyContent(String value) {
        if (value.isEmpty() || value.equals(null) || value.equals(" "))
            return true;
        else
            return false;
    }

    // getters and setters

    public static User getUser() {
        return Connection.user;
    }

    public static void setUser(User user) {
        Connection.user = user;
    }
}
