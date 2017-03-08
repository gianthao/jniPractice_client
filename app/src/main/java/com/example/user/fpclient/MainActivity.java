package com.example.user.fpclient;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.example.user.app_jni.IFpApi;
import com.example.user.app_jni.Person;

public class MainActivity extends AppCompatActivity {

    private Button button = null;
    private boolean buttonText = false;
    private CheckBox checkBox1 = null;
    private CheckBox checkBox2 = null;
    private CheckBox checkBox3 = null;
    private CheckBox checkBox4 = null;
    private CheckBox checkBox5 = null;
    private IFpApi fpApi;
    private Person person;

    private ServiceConnection serviceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("FpService connected");
            fpApi = IFpApi.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            fpApi =null;
            System.out.println("FpService disconnected");
        }
    };
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();
        int count=0;
        String name="***";
        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.CHECKBOX1:
                if (checked) {
                    // Put some meat on the sandwich
                    if(fpApi!=null) {
                        try {
                            count = fpApi.getCount();
                            person =fpApi.getPerson();
                            person.setName("xxhhh");
                            name = person.getName();
                        }catch (Exception e)
                        {
                            System.out.println("quto_server_method getCount() fail");
                        }
                        Toast.makeText(getApplicationContext(), "count=" + count+", person="+name, Toast.LENGTH_SHORT).show();
                    }
                    else
                        Toast.makeText(getApplicationContext(), "service is null, retry", Toast.LENGTH_SHORT).show();
                }
                //else
                // Remove the meat
                    //Toast.makeText(getApplicationContext(), "fingerprint1 off", Toast.LENGTH_SHORT).show();
                break;
            // TODO: Veggie sandwich
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button)findViewById(R.id.BUTTON);
        checkBox1 = (CheckBox)findViewById(R.id.CHECKBOX1);
        checkBox2 = (CheckBox)findViewById(R.id.CHECKBOX2);
        checkBox3 = (CheckBox)findViewById(R.id.CHECKBOX3);
        checkBox4 = (CheckBox)findViewById(R.id.CHECKBOX4);
        checkBox5 = (CheckBox)findViewById(R.id.CHECKBOX5);

       // final Intent intent = new Intent();
       // intent.setAction("com.example.user.app_jni.FP_SERVICE");

        /*Bind service ,for android 5, need to specify package name*/
        final Intent intent = new Intent("com.example.user.app_jni.FP_SERVICE")
                .setPackage("com.example.user.app_jni");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                buttonText= !buttonText;

                if(buttonText) {
                   // Toast.makeText(getApplicationContext(), "fingerprint disconned",
                     //       Toast.LENGTH_SHORT).show();
                    try {
                        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
                    }catch (Exception e)
                    {
                        System.out.println("FpService failed");
                    }
                    button.setText("turn off service");

                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                    checkBox4.setChecked(false);
                    checkBox5.setChecked(false);

                }
                else
                {
                    unbindService(serviceConnection);
                    button.setText("turn_on_service");
                }
            }
        });

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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
