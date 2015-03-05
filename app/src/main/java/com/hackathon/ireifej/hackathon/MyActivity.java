package com.hackathon.ireifej.hackathon;


import java.util.Date;
import java.text.DateFormat;
import android.media.RingtoneManager;
import android.media.Ringtone;
import android.content.DialogInterface;
import android.app.AlertDialog;
import java.util.Calendar;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.Toast;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import java.util.List;
import android.widget.TextView;
import android.widget.EditText;
import android.content.Intent;
import android.net.Uri;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MyActivity extends ActionBarActivity {
    private Activity currentOnTopActivity;

    public void PromptMe() {
        new AlertDialog.Builder(this)
                .setTitle("Excuse me!")
                .setMessage("Do you promise that you are NOT driving a vehicle?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("I AM driving! Sorry!", new DialogInterface.OnClickListener() {

                    Context context = getApplicationContext();
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Toast.makeText(context, "It Can Wait!", Toast.LENGTH_SHORT).show();
                        sendEmail();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    public void sendEmail() {
        EditText myThing = (EditText)findViewById(R.id.editText1);
        String myEmail = myThing.getText().toString();

        String filelocation="/storage/emulated/0/Download/download.png";
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{myEmail});
        i.putExtra(Intent.EXTRA_SUBJECT, "It Can Wait");
        i.putExtra(Intent.EXTRA_TEXT   , "You were texting while driving! Please remember that It Can Wait!");
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse( "file://"+filelocation));

        try {
            startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MyActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (currentOnTopActivity!=null && !currentOnTopActivity.isFinishing()) {
            currentOnTopActivity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    String msg = "Some msg";
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(currentOnTopActivity);
                    AlertDialog invitationDialog = null;

                    // set title
                    alertDialogBuilder.setTitle("Title ");

                    // set dialog message
                    alertDialogBuilder.setMessage(msg).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do something
                        }
                    });

                    // create alert dialog
                    invitationDialog = alertDialogBuilder.create();

                    // show it on UI Thread
                    invitationDialog.show();
                }

            });
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this .getSystemService(Context.LOCATION_SERVICE);

            LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                location.getLatitude();
                Context context = getApplicationContext();
                float mySpeed = location.getSpeed();
                //TextView myText2 = (TextView)findViewById(R.id.textView2);
                Toast.makeText(context, "Current speed:" + mySpeed, Toast.LENGTH_SHORT).show();

                TextView myText = (TextView)findViewById(R.id.textView1);
                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                // textView is the TextView view that should display it
                myText.append(currentDateTimeString + " --> " + Float.toString(mySpeed) + "m/s\n");

                if (mySpeed > 0.0) {
                    ActivityManager activityManager = (ActivityManager) getSystemService( ACTIVITY_SERVICE );
                    List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
                    for(int i = 0; i < procInfos.size(); i++)
                    {
                        if(procInfos.get(i).processName.equals("com.android.email") || procInfos.get(i).processName.equals("com.android.mms"))
                        {
                            Toast.makeText(context, "Hey! You're going too fast while texting!", Toast.LENGTH_SHORT).show();
                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                            r.play();
                            PromptMe();
                            break;
                        }
                    }
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) { }

            public void onProviderEnabled(String provider) { }

            public void onProviderDisabled(String provider) { }
        };

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);
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
