package com.mungujn.sendtext;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by Nickson on 27/01/2015.
 */
public class SendAsset extends AppCompatActivity implements View.OnClickListener {
    TextView textView;
    Button generate,send;
    ProgressBar progressBar;
    File toSend;

    String storageDir="/storage/sdcard0/Android/data/com.mungujn.sendtext/files/mungujn";
    @Override
    protected void onStart() {
        super.onStart();
        textView = (TextView)findViewById(R.id.textView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        generate = (Button)findViewById(R.id.generat);
        send = (Button)findViewById(R.id.send);
        generate.setOnClickListener(this);
        send.setOnClickListener(this);
        //send.setEnabled(false);
        //toSend = new File( Environment.getExternalStorageDirectory().toString() + "/mungujn/Simple VLC Remote Client.jar");

        // if(toSend.exists()&&toSend!=null){toSend.delete();}

        //get the application's resources
        resources = getResources();
    }

    //a handle to the application's resources
    private Resources resources;
    String fileName="mungujn_debug_btm.mp3";
    //a string to output the contents of the files to LogCat

    public File internal;
    public File temp;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generatejar);
        resources = getResources();
        internal = this.getFilesDir();
        textView = (TextView)findViewById(R.id.textView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        generate = (Button)findViewById(R.id.generat);
        send = (Button)findViewById(R.id.send);
        generate.setOnClickListener(this);
        send.setOnClickListener(this);
        temp= new File("/storage/sdcard0/Android/data/com.mungujn.sendtext/files/mungujn");
        //send.setEnabled(false);
        //toSend = new File( Environment.getExternalStorageDirectory().toString() + "/mungujn/Simple VLC Remote Client.jar");

        // if(toSend.exists()&&toSend!=null){toSend.delete();}

        //get the application's resources
        resources = getResources();

        if(isExternalStorageWritable()&&isExternalStorageReadable()&&!temp.exists()){
            temp = getFileStorageDir(this,"mungujn");
        }else if (!isExternalStorageWritable()||!isExternalStorageReadable());

    }
    @Override
    public void onClick(View v) {
        int x = v.getId();
        switch (x){
            case(R.id.generat):
                if(isExternalStorageReadable()&&isExternalStorageWritable()&&temp!=null) {
                    String pass = temp.getAbsolutePath();
                    new loadFileEx().execute(fileName,storageDir);
                    textView.setText(pass+"\n");
                }
                else {
                    Toast.makeText(getApplicationContext(), "No SD Card Access for this app on your phone", Toast.LENGTH_SHORT).show();
                }
                break;
            case(R.id.send):
                send();
                break;
        }


    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    private void send() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(toSend));
        startActivity(intent);

    }
    public File getFileStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(null), albumName);
        if (!file.mkdirs()) {
            textView.setText("Failed");
        }
        return file;
    }
    public class loadFileEx extends AsyncTask<String, Integer, String> {
        String state="Nothing Happened";
        File ex;
        protected void onPreExecute() {
            progressBar.setProgress(0);
            //internaldata.this is the context where we want our dialog to display

        }
        @Override
        protected String doInBackground(String[] params) {
            InputStream iS;
            byte[] buffer=null;
            for(int i=0;i<20;i++){
                publishProgress(5);
                try {
                    Thread.sleep(22);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            {
                //get the file as a stream
                try {
                    iS = resources.getAssets().open(fileName);
                    //create a buffer that has the same size as the InputStream
                    buffer = new byte[iS.available()];
                    //read the text file as a stream, into the buffer
                    iS.read(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }



            FileOutputStream stream = null;
            try {
                //stream = openFileOutput(temp.getName(), Context.MODE_WORLD_READABLE);
                ex=new File(storageDir+"/BTM Chat.exe");
                stream= new FileOutputStream(ex);
                assert buffer != null;
                stream.write(buffer);
                stream.flush();
                stream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressBar.incrementProgressBy(values[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(ex.exists()){
                state="success";
                toSend=ex;
            }
            if(state.equalsIgnoreCase("success")) {
                textView.append(state+"Core Operations" + "\n" + "Successfully generated client....." + "\n" + "You can now send it to your computer " );

            }else {
                send.setEnabled(false);
                textView.append("\n"+state);
            }
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

    }

}