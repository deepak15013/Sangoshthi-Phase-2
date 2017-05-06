package io.github.varunj.sangoshthi_ivr;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Varun on 12-Mar-17.
 */

public class AddShowActivity extends AppCompatActivity {

    private static int REQUEST_PICK_AUDIO = 0;
    EditText create_show_list;
    private String senderPhoneNum;
    public String showTime = "-1", showDate = "-1", showPath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        senderPhoneNum = pref.getString("phoneNum", "0000000000");

        // AMQP stuff
        AMQPPublish.setupConnectionFactory();
        AMQPPublish.publishToAMQP();

        // initialise screen elements
        create_show_list = (EditText)findViewById(R.id.create_show_list);

        final Button create_show_time = (Button) findViewById(R.id.create_show_time);
        create_show_time.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_red));
        create_show_time.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddShowActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        showTime = "" + selectedHour + ":" + selectedMinute + ":00" ;
                        create_show_time.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_green));
                    }
                }, 15 , 0 , false);
                mTimePicker.setTitle("Select Time For the Show");
                mTimePicker.show();
            }
        });

        final Button create_show_date = (Button) findViewById(R.id.create_show_date);
        create_show_date.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_red));
        create_show_date.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                class mDateSetListener implements DatePickerDialog.OnDateSetListener {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showDate = "" + dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        create_show_date.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_green));
                    }
                }
                DatePickerDialog dialog = new DatePickerDialog(AddShowActivity.this, new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();
            }
        });

        final Button create_show_audio_file = (Button) findViewById(R.id.create_show_audio_file);
         create_show_audio_file.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), REQUEST_PICK_AUDIO);
                create_show_audio_file.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_green));
            }
        });

        final Button create_show_ok = (Button) findViewById(R.id.create_show_ok);
        create_show_ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (create_show_list.getText().toString().trim().length() > 0
                        &&  !showDate.equals("-1") && !showTime.equals("-1")) {
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        //primary key: <broadcaster, show_name>
                        jsonObject.put("objective", "create_show");
                        jsonObject.put("time_of_airing", showDate + " " + showTime);
                        jsonObject.put("broadcaster_phoneno", senderPhoneNum);
                        jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                        jsonObject.put("audio_name", showPath);
                        jsonObject.put("show_hosting_status", 0);
                        ArrayList<String> temp= new ArrayList<>();
                        for (String x: create_show_list.getText().toString().trim().split(",")) {
                            temp.add(x.trim());
                        }
                        jsonObject.put("list_of_listeners", new JSONArray(temp));
                        AMQPPublish.queue.putLast(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(AddShowActivity.this, "Enter Valid Data!", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(AddShowActivity.this, "Show Created! Press Back Now.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (AMQPPublish.publishThread != null)
            AMQPPublish.publishThread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (AMQPPublish.publishThread != null)
            AMQPPublish.publishThread.interrupt();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_PICK_AUDIO && resultCode == RESULT_OK) {
            Uri selectedAudioURI = intent.getData();
            File audioFile = new File(getRealPathFromURI(selectedAudioURI));
            showPath = audioFile.getName();
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }
}
