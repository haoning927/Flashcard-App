package com.example.flashcard;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;

public class AlarmSchedules extends AppCompatActivity {
    public final static String TAG = "AlarmSchedules";
    private LinearLayout container;
    private AlertDialog.Builder builder;
    private HashSet<Integer> canceled;
    private SharedPreferences ssp;
    private String[] alarms;
    private int num_alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_schedules);
        builder = new AlertDialog.Builder(this);
        container = (LinearLayout) findViewById(R.id.wholeview);
        num_alarms = 0;

        // add present alarms to listview
        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        String filename = "flashCardSP"+Integer.toString(sp.getInt("current_user", 0));
        ssp = getSharedPreferences(filename, MODE_PRIVATE);
        String present_alarm = ssp.getString("present_alarm", "");
        showListView(present_alarm);
    }

    private void showListView(String present_alarm) {
        Log.i(TAG, present_alarm);
        alarms = present_alarm.split(",");
        num_alarms = alarms.length;
//        Resources res = getResources();
        // TODO: need better design
        for (int i=0; i<alarms.length; i++) {
            if (alarms[i].length()==0) continue;
            Log.i(TAG, alarms[i]);
            LinearLayout row = new LinearLayout(this);
            String[] curr = alarms[i].split("-");
            row.setId(Integer.valueOf(curr[0]));

            TextView tv = new TextView(this);
            tv.setText("   "+curr[1]);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(36);
            row.addView(tv);

            TextView tv2 = new TextView(this);
            tv2.setText("   "+curr[2]);
            tv2.setTextColor(Color.BLACK);
            tv2.setTextSize(16);
            row.addView(tv2);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, Integer.toString(v.getId()));

                    //Setting message manually and performing action on button click
                    builder.setMessage("Do you want to cancel this alarm?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    container.removeView(v);
                                    Alarm.cancelAlarm(AlarmSchedules.this, row.getId());
                                    if (canceled==null) canceled=new HashSet<>();
                                    canceled.add(row.getId());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Cancel the alarm");
                    alert.show();
                }
            });

            container.addView(row);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (canceled==null) return;
        String new_alarm = "";
        for (int i=0; i<alarms.length; i++) {
            String curr = alarms[i];
            int idx = curr.indexOf("-");
            if (idx==-1) continue;
            int this_id = Integer.valueOf(curr.substring(0, idx));
            if (canceled.contains(this_id)) continue;
            new_alarm += curr;
            new_alarm += ",";
        }
        final SharedPreferences.Editor ssp_editor = ssp.edit();
        ssp_editor.putString("present_alarm", new_alarm);
        ssp_editor.commit();
    }

    public void goToCreateAlarm(View view) {
        Intent intent = new Intent(this, CreateNewAlarm.class);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        alarms = ssp.getString("present_alarm", "").split(",");
        if (alarms.length == num_alarms) return;
        // new alarms detected
        for (int i=num_alarms; i<alarms.length; i++) {
            if (alarms[i].length()==0) continue;
            Log.i(TAG, alarms[i]);
            LinearLayout row = new LinearLayout(this);
            String[] curr = alarms[i].split("-");
            row.setId(Integer.valueOf(curr[0]));

            TextView tv = new TextView(this);
            tv.setText("   "+curr[1]);
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(36);
            row.addView(tv);

            TextView tv2 = new TextView(this);
            tv2.setText("   "+curr[2]);
            tv2.setTextColor(Color.BLACK);
            tv2.setTextSize(16);
            row.addView(tv2);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, Integer.toString(v.getId()));

                    //Setting message manually and performing action on button click
                    builder.setMessage("Do you want to cancel this alarm?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    container.removeView(v);
                                    Alarm.cancelAlarm(AlarmSchedules.this, row.getId());
                                    if (canceled==null) canceled=new HashSet<>();
                                    canceled.add(row.getId());
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    return;
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Cancel the alarm");
                    alert.show();
                }
            });

            container.addView(row);
        }
        num_alarms = alarms.length;
    }
}
