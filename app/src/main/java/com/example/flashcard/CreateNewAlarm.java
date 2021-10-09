package com.example.flashcard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

public class CreateNewAlarm extends AppCompatActivity implements View.OnClickListener {

    public final static String TAG = "CreateNewAlarm";
    private TimePicker timePicker;
    private CheckBox mon, tue, wed, thu, fri, sat, sun;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_alarm);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        timePicker = findViewById(R.id.time_picker);
        mon = findViewById(R.id.on_mon);
        tue = findViewById(R.id.on_tue);
        wed = findViewById(R.id.on_wed);
        thu = findViewById(R.id.on_thu);
        fri = findViewById(R.id.on_fri);
        sat = findViewById(R.id.on_sat);
        sun = findViewById(R.id.on_sun);
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "Confirming");
    }

    public void onConfirm(View v) {
        Alarm alarm = new Alarm(
                timePicker.getHour(),
                timePicker.getMinute(),
                mon.isChecked(),
                tue.isChecked(),
                wed.isChecked(),
                thu.isChecked(),
                fri.isChecked(),
                sat.isChecked(),
                sun.isChecked()
        );
        int alarm_id = alarm.schedule(this);

        final SharedPreferences sp = getSharedPreferences("flashCardSP", MODE_PRIVATE);
        String filename = "flashCardSP"+sp.getInt("current_user", 0);
        SharedPreferences ssp = getSharedPreferences(filename, MODE_PRIVATE);
        String present_alarm = ssp.getString("present_alarm", "");
        final SharedPreferences.Editor editor = ssp.edit();
        String new_alarm = alarm_id+"-";
        String temp = Integer.toString(timePicker.getHour());
        if (temp.length()==1) temp="0"+temp;
        new_alarm += temp+":";
        temp = Integer.toString(timePicker.getMinute());
        if (temp.length()==1) temp="0"+temp;
        new_alarm += temp+"-";
        if (mon.isChecked()) new_alarm+="Mo ";
        else new_alarm+="";
        if (tue.isChecked()) new_alarm+="Tu ";
        else new_alarm+="";
        if (wed.isChecked()) new_alarm+="We ";
        else new_alarm+="";
        if (thu.isChecked()) new_alarm+="Th ";
        else new_alarm+="";
        if (fri.isChecked()) new_alarm+="Fr ";
        else new_alarm+="";
        if (sat.isChecked()) new_alarm+="Sa ";
        else new_alarm+="";
        if (sun.isChecked()) new_alarm+="Su ";
        else new_alarm+="";
//        editor.putString("present_alarm", new_alarm);
        editor.putString("present_alarm", present_alarm+","+new_alarm);
        editor.commit();
        super.finish();
    }

}
