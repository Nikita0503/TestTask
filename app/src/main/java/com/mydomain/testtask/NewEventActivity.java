package com.mydomain.testtask;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class NewEventActivity extends AppCompatActivity {

    public static final int DIALOG_TIME = 1;
    private int mMyHour;
    private int mMyMinute;
    private String mMyDescription = null;
    private TextView mTimeTextView;
    private EditText mDescriptionEditText;
    private Button mChooseTimeButton;
    private Button mSaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        mTimeTextView = (TextView) findViewById(R.id.textViewTime);
        mDescriptionEditText = (EditText) findViewById(R.id.editText);
        mChooseTimeButton = (Button) findViewById(R.id.button);
        mSaveButton = (Button) findViewById(R.id.button2);
        mChooseTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DIALOG_TIME);
            }
        });
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMyDescription = mDescriptionEditText.getText().toString();
                if(mTimeTextView.getText().toString().equals("Not selected")){
                    Toast.makeText(getApplicationContext(), "Choose time, please", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mMyDescription==null || mMyDescription.equals("")){
                    Toast.makeText(getApplicationContext(), "Set description, please", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent();

                intent.putExtra("hour", mMyHour);
                intent.putExtra("min", mMyMinute);
                intent.putExtra("description", mMyDescription);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_TIME) {
            TimePickerDialog tpd = new TimePickerDialog(this, myCallBack, 0, 0, true);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mMyHour = hourOfDay;
            mMyMinute = minute;
            //Toast.makeText(getApplicationContext(), "Time is " + myHour + " hours " + myMinute + " minutes", Toast.LENGTH_SHORT).show();
            mTimeTextView.setText("Time is " + mMyHour + " hours " + mMyMinute + " minutes");

        }
    };
}
