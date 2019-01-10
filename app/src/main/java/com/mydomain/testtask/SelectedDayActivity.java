package com.mydomain.testtask;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SelectedDayActivity extends AppCompatActivity {

    private int mDay;
    private int mMonth;
    private int mYear;
    private ArrayList<EventData> mEventsList;
    private DBHelper mDBHelper;
    private SQLiteDatabase mDb;
    private TextView mTextViewDate;
    private FloatingActionButton mFloatingActionButton;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_day);
        mTextViewDate = (TextView) findViewById(R.id.textViewDate);
        mListView = (ListView) findViewById(R.id.listView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        mDay = intent.getIntExtra("day", 0);
        mMonth = intent.getIntExtra("month", 0);
        mYear = intent.getIntExtra("year", 0);
        mTextViewDate.setText(mDay + "." + mMonth + "." + mYear);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.fabHome);
        mFloatingActionButton.setImageResource(R.drawable.home_tab);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setImageResource(R.drawable.plus);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectedDayActivity.this, NewEventActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        mDBHelper = new DBHelper(this);
        mDb = mDBHelper.getWritableDatabase();

    }

    @Override
    protected void onStart() {
        super.onStart();
        String selection = "day = ?";
        String[] selectionArgs = new String[] { mDay + "." + mMonth + "." + mYear };
        Cursor c = mDb.query("events", null, selection, selectionArgs, null, null, null);
        mEventsList = new ArrayList<EventData>();
        ArrayList<String> eventsDescriptions = new ArrayList<String>();
        if (c.moveToFirst()) {
            int dateColIndex = c.getColumnIndex("date");
            int descriptionColIndex = c.getColumnIndex("description");
            do {
                eventsDescriptions.add(c.getString(descriptionColIndex) + ", " + new Date(c.getLong(dateColIndex)).getHours() + ":" + new Date(c.getLong(dateColIndex)).getMinutes());
                mEventsList.add(new EventData(c.getLong(dateColIndex), c.getString(descriptionColIndex)));
            } while (c.moveToNext());
        } else
            Log.d("Log", "0 rows");
        c.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, eventsDescriptions);
        mListView.setAdapter(adapter);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {return;}
        int hour = data.getIntExtra("hour", 0);
        int min = data.getIntExtra("min", 0);
        String description = data.getStringExtra("description");

        Calendar calendar1 = Calendar.getInstance();
        calendar1.set(mYear, mMonth, mDay, hour, min);

        Log.d("123", calendar1.getTime().toString());

        ContentValues cv = new ContentValues();
        cv.put("date", calendar1.getTimeInMillis());
        cv.put("description", description);
        cv.put("day", mDay + "." + mMonth + "." + mYear);
        long rowID = mDb.insert("events", null, cv);
        Log.d("LOG", "row inserted, ID = " + rowID);
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, TimeNotification.class);
        intent.putExtra("description", description);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT );
        am.cancel(pendingIntent);
        am.set(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), pendingIntent);
    }





    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            // конструктор суперкласса
            super(context, "myDB", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("TAG", "--- onCreate database ---");
            // создаем таблицу с полями
            db.execSQL("create table events ("
                    + "id integer primary key autoincrement,"
                    + "day text,"
                    + "date long,"
                    + "description text" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
