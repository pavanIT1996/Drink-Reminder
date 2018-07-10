package com.example.drinkreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity{

    private static final String TAG = "MainActivity";

    private ListView mListView;
    DatabaseHelper myDb;
    String hour,mminute;
    int broadcastCode=0;
    AlarmManager alarmManager;
    ArrayAdapter<String> mAdapter;
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDb=new DatabaseHelper(this);

        mListView=(ListView) findViewById(R.id.list);
        loadReminderList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_add_time:
                Log.d(TAG, "Add a new Reminder");
                setTime();
                return true;
            case R.id.action_delete_time:
                Log.d(TAG, "Delete Reminder");
                DeleteTime();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void DeleteTime(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Reminder From ID");

        final EditText input = new EditText(this);

        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_Text = input.getText().toString();
                myDb.deleteData(m_Text);
                int rc = Integer.valueOf(m_Text);
                cancel_Alarm(rc);
                loadReminderList();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void setTime(){
        Calendar cal=Calendar.getInstance();
        TimePickerDialog timePickerDialog=new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                hour= String.valueOf(hourOfDay);
                mminute= String.valueOf(minute);
                startAlarm();
            }
        },cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }

    public void startAlarm(){
        broadcastCode++;
        AddData(broadcastCode,hour,mminute);
        Intent intent=new Intent(this,MyBroadcastReceiver.class);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this.getApplicationContext(),broadcastCode,intent,0);
        alarmManager=(AlarmManager) getSystemService(ALARM_SERVICE);
        Calendar cal_alarm=Calendar.getInstance();
        cal_alarm.set(Calendar.HOUR_OF_DAY,Integer.valueOf(hour));
        cal_alarm.set(Calendar.MINUTE,Integer.valueOf(mminute));
        cal_alarm.set(Calendar.SECOND,00);


        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(this,"Alarm > KITKAT & Alarm Set For: "+hour+" : "+mminute,Toast.LENGTH_SHORT).show();
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
            alarmManager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),pendingIntent);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);
            Toast.makeText(this,"Alarm < KITKAT & Alarm Set For: "+hour+" : "+mminute,Toast.LENGTH_SHORT).show();
        }


//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),AlarmManager.INTERVAL_DAY,pendingIntent);
//            Toast.makeText(this,"Alarm Set For: "+hour+" : "+mminute,Toast.LENGTH_SHORT).show();
//        }
//        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
//            alarmManager.set(AlarmManager.RTC_WAKEUP,cal_alarm.getTimeInMillis(),pendingIntent);
//            Toast.makeText(this,"Alarm < KITKAT & Alarm Set For: "+hour+" : "+mminute,Toast.LENGTH_SHORT).show();
//        }

    }

    public void cancel_Alarm(int value){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(getApplicationContext(), MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), value, myIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this,"Alarm Cancelled",Toast.LENGTH_SHORT).show();
    }

    public void AddData(int id,String hour,String minute){
        boolean isInserted=myDb.insertData(id,hour,minute);
        if(isInserted==true){
            Toast.makeText(MainActivity.this,"Data Inserted",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(MainActivity.this,"Data Not Inserted",Toast.LENGTH_LONG).show();
        }
        loadReminderList();
    }


    private void loadReminderList(){
        ArrayList<String> reminderList = myDb.getData();
        if(mAdapter==null){
            mAdapter=new ArrayAdapter<String>(this,R.layout.raw,R.id.textView,reminderList);
            mListView.setAdapter(mAdapter);

        }else{
            mAdapter.clear();
            mAdapter.addAll(reminderList);
            mAdapter.notifyDataSetChanged();
        }
    }

}




