package com.example.user.fpclient;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.user.app_jni.IFpApi;
import com.example.user.app_jni.IMessageCallback;
import com.example.user.app_jni.Person;

import android.os.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    public static final int MSG_AUTH_ERROR = 1;
    public static final int MSG_AUTH_HELP = 2;
    public static final int MSG_AUTH_SUCCESSED = 3;
    public static final int MSG_AUTH_FAILED = 4;
    public static final int MSG_AUTH_ACQUIRED = 5;


    private Button button = null;
    private boolean buttonText = false;
    private IFpApi fpApi_toService;
    private Person person;
    private int callback_received_cnt = 0;
    String tag = "FpClient";
    TextView textView = null;
    TextView Text_verifyStatus = null;
    ImageView image = null;
    Handler handler=null;
    int[] msg_send={34};
    CancellationSignal cancel = null;
    MyHandler myHandler =null;
    FingerprintManager.CryptoObject cryptoObject=null;
    FingerprintManager fingerprintManager=null;
    MyAuthenticationCallback myAuthCallback=null;
    CryptoObjectHelper cryptoObjectHelper=null;
    boolean lockout =false;
    EditText editText = null;
    int user_count=0;
    int total=0;
    int pass=0;
    AlertDialog.Builder dialog;
    TextView count_pass;
    TextView count_total;

    IMessageCallback.Stub messageCallback =new IMessageCallback.Stub() {
        @Override
        public void getMsg(int[] msg) throws RemoteException {
         //   Log.i(tag, "FpService message received,msg[0]="+msg[0]);
            if (msg[0]%3!=0) {
                Message message = new Message();
                message.what = msg[0];
                handler.sendMessage(message);
            }
        }
    };


    /*bind FpService*/
    private ServiceConnection serviceConnection =new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(tag,"FpService connected");
            fpApi_toService = IFpApi.Stub.asInterface(service);
            if(fpApi_toService!=null) {
                try {
                    fpApi_toService.registerCallback(messageCallback);
                    Log.i(tag,"FpService registerCallback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                fpApi_toService.getCount(msg_send);
                Log.i(tag,"test");
            }catch (Exception e){
                e.printStackTrace();
                Log.i(tag, "test fail");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

            Log.i(tag, "FpService disconnected");
            if(fpApi_toService!=null) {
                try {
                    fpApi_toService.unregisterCallback(messageCallback);
                    fpApi_toService =null;
                    Log.i(tag,"FpService unregisterCallback");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    };
        /*bind FpService end*/


    public void confirm(View view){
        //if(editText.getText()==null||editText==null)
       // editText.setText("0");
        user_count= Integer.parseInt(editText.getText().toString());
        Log.i(tag,"user_count="+user_count);
        total=0;
        pass=0;
        count_pass.setText("0");
        count_total.setText("0");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(tag, "onCreate");
        textView = (TextView) findViewById(R.id.serviceStatus);
        Text_verifyStatus = (TextView) findViewById(R.id.enroll_staus);
        image =(ImageView) findViewById(R.id.image);
        myHandler =new MyHandler(this.getMainLooper());
        editText = (EditText) findViewById(R.id.eidt_input);
        count_pass=(TextView) findViewById(R.id.count_pass);
        count_total= (TextView) findViewById(R.id.count_total);

        /*Bind service ,for android 5, need to specify package name*/
        /*
        final Intent intent = new Intent("com.example.user.app_jni.FP_SERVICE")
                .setPackage("com.example.user.app_jni");
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        */

        fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
        if(!fingerprintManager.isHardwareDetected()){
            dialog =new AlertDialog.Builder(this);
            dialog.setTitle(R.string.nofingerprint);
            dialog.setMessage(R.string.checkSensor);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }

        if(!fingerprintManager.hasEnrolledFingerprints()){
            dialog =new AlertDialog.Builder(this);
            dialog.setTitle(R.string.noenrolledfinger);
            dialog.setMessage(R.string.checksetting);
            dialog.setCancelable(false);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            dialog.show();
        }

        myAuthCallback = new MyAuthenticationCallback(myHandler);
        try{
            cryptoObjectHelper=new CryptoObjectHelper();
           cryptoObject= cryptoObjectHelper.buildCryptoObject();
        }catch (Exception e){
            Log.i(tag,"oncreat: unable to create cryptoObject");
        }
        cancel =new CancellationSignal();
        if(cryptoObject!=null)
        fingerprintManager.authenticate(cryptoObject,cancel,0,myAuthCallback,null);
    }

    private class MyHandler extends Handler{
        public MyHandler(Looper looper){
            super(looper);
        }

        @Override
        public void handleMessage(Message msg){
            Log.i(tag, "pass=" + pass + ";total=" + total + ";user_count=" + user_count);


            switch (msg.what){
                case MainActivity.MSG_AUTH_ACQUIRED:
                    Log.i(tag, "msg acquired");

                    //textView.setText("MSG_AUTH_ACQUIRED");
                    cancel = null;
                    handleAcquiredInfo(msg.arg1);
                    //delay_updateUI();
                    break;

                case MainActivity.MSG_AUTH_ERROR:
                    Log.i(tag, "msg error");
                    if(total<user_count||user_count==0) {
                        total++;
                        count_pass.setText(""+pass);
                        count_total.setText(""+total);
                    }
                    String errorString =(String) msg.obj;
                    if(msg.arg1!=FingerprintManager.FINGERPRINT_ERROR_CANCELED) {
                        Text_verifyStatus.setText(errorString);
                    }else
                    Log.i(tag,"FINGERPRINT_ERROR_CANCELED");
                    Log.i(tag, "lockout errorcode=" + msg.arg1);
                    if(msg.arg1!=FingerprintManager.FINGERPRINT_ERROR_LOCKOUT) {
                        Log.i(tag,"lockout code!=7");
                        delay_updateUI();
                    }
                    break;

                case MainActivity.MSG_AUTH_FAILED:
                    Log.i(tag, "msg failed");
                    if(total<user_count||user_count==0) {
                        total++;
                        count_pass.setText(""+pass);
                        count_total.setText(""+total);
                    }
                    textView.setText("MSG_AUTH_FAILED");
                    Text_verifyStatus.setText("Fingerprint not recognized.\nTry again.");
                    image.setImageResource(R.drawable.notreco);
                        delay_updateUI();
                        delay_updateText();
                    Log.i(tag,"2222 lockout="+lockout);
                    break;

                case MainActivity.MSG_AUTH_HELP:
                    Log.i(tag, "msg help");
                    if(total<user_count||user_count==0) {
                        //total++;
                        count_pass.setText(""+pass);
                        count_total.setText(""+total);
                    }
                    String strinMsg =(String) msg.obj;
                    textView.setText(strinMsg);
                    cancel = null;
                    delay_updateUI();

                    break;

                case MainActivity.MSG_AUTH_SUCCESSED:
                    Log.i(tag,"msg successed");
                    if(total<user_count||user_count==0) {
                        pass++;
                        total++;
                        count_pass.setText(""+pass);
                        count_total.setText(""+total);
                    }
                    image.setImageResource(R.drawable.success);
                    Text_verifyStatus.setText("Fingerprint recognized");
                    cancel = null;
                    if(cancel==null)
                        cancel =new CancellationSignal();
                    if(cryptoObject==null) {
                        try {
                            cryptoObject = cryptoObjectHelper.buildCryptoObject();
                        } catch (Exception e) {
                            Log.i(tag, "unable to create CryptoObject");
                        }
                    }
                    fingerprintManager.authenticate(cryptoObject, cancel, 0, myAuthCallback, null);
                    delay_updateUI();
                    delay_updateText();
                    break;
            }

        }
    }

    public void delay_updateUI(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                image.setImageResource(R.drawable.ic_fp_40px);
                textView.setText("");
            }
        },400);

    }
    public void delay_updateText(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Text_verifyStatus.setText("Touch Sensor");
                textView.setText("");
            }
        },1000);
    }

    public void handleAcquiredInfo(int code){
        switch (code){
            case FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY:
                textView.setText("FINGERPRINT_ACQUIRED_IMAGER_DIRTY");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_GOOD:
                textView.setText("FINGERPRINT_ACQUIRED_GOOD");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW:
                textView.setText("FINGERPRINT_ACQUIRED_TOO_SLOW");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT:
                textView.setText("FINGERPRINT_ACQUIRED_INSUFFICIENT");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL:
                textView.setText("FINGERPRINT_ACQUIRED_PARTIAL");
                break;
            case FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST:
                textView.setText("FINGERPRINT_ACQUIRED_TOO_FAST");
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.i(tag,"onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.i(tag, "onOptionsItemSelected");

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(tag, "lifeCycle onStop");
       // if (cancel!=null)
        //cancel.cancel();
       ///cancel=null;

    }

    protected void onPostResume() {
        super.onPostResume();
        Log.i(tag, "lifeCycle onPostResume");
    if(cancel==null){
        cancel =new CancellationSignal();
        if (cryptoObject != null) {
            Log.i(tag, "lifeCycle authenticate");
            fingerprintManager.authenticate(cryptoObject, cancel, 0, myAuthCallback, null);
        }
    }
    }


}
