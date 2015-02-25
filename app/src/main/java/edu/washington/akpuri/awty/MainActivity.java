package edu.washington.akpuri.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    //Boolean value that keeps track of whether an alarmIntent is currently firing
    private boolean start = true;
    private Intent alarmIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btn = (Button) findViewById(R.id.start);
        alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        //If there is an alarm currently going, change the boolean to false
        if ((PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent,
                PendingIntent.FLAG_NO_CREATE)) != null){
            start = false;
            btn.setText("Stop");
        }
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText message = (EditText) findViewById(R.id.message);
                EditText phone = (EditText) findViewById(R.id.phone);
                EditText minutes = (EditText) findViewById(R.id.minutes);
                boolean messageSet = checkMessage(message.getText().toString());
                boolean phoneSet = checkPhone(phone.getText().toString());
                boolean minutesSet = checkMinutes(minutes.getText().toString());
                //If the button currently says start, we need to trigger the alarm
                if (start) {
                    TextView retry = (TextView) findViewById(R.id.retry);
                    if (messageSet && phoneSet && minutesSet) {
                        btn.setText("Stop");
                        String messageText = phoneFormat(phone.getText().toString());
                        messageText += ": " + message.getText().toString();
                        //Pass the formatted message to the AlarmReciever class
                        alarmIntent.putExtra("message", messageText);
                        //Keep interval as second for now, will multiply my 60 to change to minutes
                        int interval = Integer.parseInt(minutes.getText().toString()) * 1000 ;
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
                        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
                        Toast.makeText(MainActivity.this, "Alarm Has Been Set", Toast.LENGTH_SHORT).show();
                        retry.setText("");
                        start = false;
                    } else {
                        retry.setText("Incorrect Values entered, Try Again Please");
                    }
                } else {// (!start) meaning the button currently says stop
                    //We need to kill the alarm service
                    btn.setText("Start");
                    AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, 0);
                    manager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    Toast.makeText(MainActivity.this, "Alarm Has Been Canceled", Toast.LENGTH_SHORT).show();
                    start = true;
                }
            }
        });
    }

    public boolean checkMessage(String s) {
        return true;
    }

    public boolean checkPhone(String s) {
        if (s.length() == 0) {
            return false;
        } else {
            return (s.length() == 10 || s.length() == 7);
        }
    }

    public boolean checkMinutes(String s) {
        if (s.length() == 0) {
            return false;
        } else if (s.toLowerCase().equals("0")) {
            return false;
        } else if (Integer.parseInt(s) <= 0) {
            return false;
        }
        return true;
    }

    public String phoneFormat(String number) {
        String formatted = "";
        int start = 0;
        if (number.length() == 10) {
            formatted += "(" + number.substring(start, start + 3) + ") ";
            start = 3;
        }
        formatted += number.substring(start, start + 3) + "-";
        start = start + 3;
        formatted += number.substring(start);
        return formatted;
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
