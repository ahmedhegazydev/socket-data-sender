package com.mungujn.sendtext;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainAct extends ActionBarActivity implements View.OnClickListener {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bluetoothservice!=null){bluetoothservice.stop();}
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    Button send;
    TextView tv1;
    EditText editText;
    ListView mConversationView;
    Button mSendButton;
    AdapterView.OnItemClickListener adpterlistener;
    /**
     * Array adapter for the conversation thread
     */
    private ArrayAdapter<String> mConversationArrayAdapter;
    /**
     * String buffer for outgoing messages
     */
    private StringBuffer mOutStringBuffer;


    BluetoothDevice device;
    BluetoothAdapter adapter;
    BluetoothService bluetoothservice=null;

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final int RECIEVED_MESSAGE=11;
    public static final int ATTEMPTING = 6;
    public static final int CONNECTED_TO = 7;
    public static final int CONNECT_THREAD = 8;
    public static final int CONNECTED_THREAD = 9;
    public static final int ERROR_DURING_WRITE= 10;
    SharedPreferences data;
    ClipboardManager cliper;
    public static String filename = "SharedString";
    public String ConnectedDeviceName="Null";
    boolean btDef,btDef2;
    public String pasteData= "";
    public String mostrecent="Status";



    public static final String DEVICE_NAME = "device_name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        adapter=BluetoothAdapter.getDefaultAdapter();
        cliper = (ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
        initializeVars();
        data = getSharedPreferences(filename,0);
        obtainPrefs();
        if (!adapter.isEnabled()/*&&btDef==true*/) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 24);
        }
        bluetoothservice = new BluetoothService(this,mHandler);
        loadDefDev();
        tv1.setText(mostrecent);
    }


    void obtainPrefs(){
        SharedPreferences getPrefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        btDef = getPrefs.getBoolean("checkbox",true);
        btDef2 = getPrefs.getBoolean("checkbox2",false);
    }
    void loadDefDev(){
        data = getSharedPreferences(filename,0);
        String address = data.getString("address","Null");
        if(!address.equalsIgnoreCase("Null")&&btDef2!=false&&adapter.isEnabled())
        {
            BluetoothDevice device = adapter.getRemoteDevice(address);
            bluetoothservice.connect(device);
        }

    }
    void initializeVars(){
        btDef=true;
        btDef2=false;
        bluetoothservice=null;

        mOutStringBuffer = new StringBuffer("");
        send = (Button) findViewById(R.id.button_send1);
        send.setOnClickListener(this);
        editText = (EditText)findViewById(R.id.edit_text_out);
        editText.setOnEditorActionListener(mWriteListener);
        mConversationView =(ListView)findViewById(R.id.in);
        // Initialize the array adapter for the conversation thread
        mConversationArrayAdapter = new ArrayAdapter<String>(MainAct.this, R.layout.message);

        mConversationView.setAdapter(mConversationArrayAdapter);
        mConversationView.setOnItemClickListener(mDeviceClickListener);


        device=null;
        tv1 = (TextView)findViewById(R.id.tv1);


    }
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, final int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();

            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            clipboard.setText(info);

            if(info.length()>9)
            Toast.makeText(getApplicationContext(),"Copied... " + info.substring(0,6)+"...",Toast.LENGTH_LONG).show();
            else Toast.makeText(getApplicationContext(),"Copied... ",Toast.LENGTH_LONG).show();

            /*
            AlertDialog.Builder alert=new AlertDialog.Builder(MainAct.this);
            alert.setTitle("What you want to do ?");
            alert.setIcon(R.drawable.ic_launcher);
            alert.setPositiveButton("Copy",new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            String info = ((TextView) v).getText().toString();
                            Toast.makeText(getApplicationContext(), info,Toast.LENGTH_LONG).show();

                        }
                    });*/

        }
    };
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_send1:
                String str = editText.getText().toString();
                sendMessage(str);
                break;

        }
    }
    /**
     * The action listener for the EditText widget, to listen for the return key
     */
    private TextView.OnEditorActionListener mWriteListener
            = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
            // If the action is a key-up event on the return key, send the message
            if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                String message = view.getText().toString();
                sendMessage(message);
            }
            return true;
        }
    };
    private void setStatus(CharSequence subtitle){
Activity activity = this;
        final ActionBar actionBar = activity.getActionBar();
        if (null == actionBar) {
            return;
        }
        actionBar.setSubtitle(subtitle);
    }
    public  void sendMessage(String message){
        // Check that we're actually connected before trying anything
        if (bluetoothservice.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            message = message.replace("\n","").replace("\r","")+"\n"+"\n";

            byte[] send = message.getBytes();
            bluetoothservice.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
            editText.setText(mOutStringBuffer);
        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == 2) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                //tv1.append(address);
                device = adapter.getRemoteDevice(address);
                bluetoothservice.connect(device);

            }
        }
        if(requestCode ==24){
            if(resultCode == RESULT_CANCELED) {}
        }
    }
    private final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_STATE_CHANGE:
                    switch (msg.arg1)
                    {
                        case ATTEMPTING:
                            tv1.setText(getString(R.string.Status));
                            setStatus(getString(R.string.Status));
                            tv1.append(getString(R.string.attemngtcnt));
                            break;
                        case CONNECT_THREAD:
                            tv1.setText(getString(R.string.stsfldtoconn));
                            break;
                    }
                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    ConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), getString(R.string.connctdto)
                            + ConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    tv1.setText(getString(R.string.stscnctto)+" "+ConnectedDeviceName);
                    break;
                case ERROR_DURING_WRITE:
                    Toast.makeText(getApplicationContext(), "Error during write"
                            , Toast.LENGTH_SHORT).show();
                    tv1.setText("Status: Error while attempting to send ");
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:\n" + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add("                              "+":"+ConnectedDeviceName + ":\n" + readMessage);
                    break;
                case RECIEVED_MESSAGE:
                    // save the connected device's name
                    String xcz = msg.getData().getString("RECV");
                    Toast.makeText(getApplicationContext(),"Text Recieved", Toast.LENGTH_SHORT).show();
                    //et2.setText("RECIEVED \n" +
                      //      " " +xcz);
                    break;
                case 200:
                    Toast.makeText(getApplicationContext(),"Sent", Toast.LENGTH_SHORT).show();
                    break;
                case 700:
                    switch (msg.arg1)
                    {
                        case 1:
                            tv1.setText("Staus: Error 1");
                            break;
                        case 2:
                            tv1.setText("Status: Waiting for devices");
                            break;
                        case 3:
                            tv1.setText("Status: Error 3");
                            break;
                        case 4:
                            tv1.setText("Status: Succesfully Connected to "+ConnectedDeviceName);
                            break;
                    }
                    break;
                case Constants.MESSAGE_TOAST:
                     {
                        Toast.makeText(getApplicationContext(), ConnectedDeviceName+" Disconnected",
                                Toast.LENGTH_SHORT).show();
                         tv1.setText("Status: Waiting for devices");

                         if (bluetoothservice != null&&adapter.isEnabled()) {
                              {
                                 bluetoothservice.start();
                             }
                         }
                     }
                    break;


            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (bluetoothservice != null&&adapter.isEnabled()) {
            if (bluetoothservice.getState() == bluetoothservice.STATE_NONE) {
                bluetoothservice.start();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent Intent = new Intent(this, settings.class);
            startActivity(Intent);
            return true;
        }
        if(id==R.id.wscan){
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent,2);
            return true;
        }
        if(id==R.id.wscan2){
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent,2);
            return true;
        }
        if(id==R.id.wscan3){
            // Launch the DeviceListActivity to see devices and do scan
            bluetoothservice.start();
            return true;
        }
        if(id==R.id.wscan4){
            if(cliper.hasPrimaryClip()){
                ClipData.Item item2 = cliper.getPrimaryClip().getItemAt(0);
                //pasteData = item.getText();
                //pasteData = item.getText().toString();
                CharSequence x = item2.getText();
                editText.setText(x);
                if(pasteData!=null){
                    return true;
                }else{
                    Uri paste = item2.getUri();
                    if(paste!=null){
                        paste.toString();
                    }else{
                        // et2.append("Nothing to paste");
                        return true;}
                }
            }
            return true;
        }

        if(id==R.id.zabout){
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, LinksInfo.class);
            startActivity(serverIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragmentmy, container, false);
            return rootView;
        }
    }

}

