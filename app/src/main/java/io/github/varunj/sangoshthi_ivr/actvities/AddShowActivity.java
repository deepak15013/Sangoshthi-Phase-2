package io.github.varunj.sangoshthi_ivr.actvities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.utils.AMQPPublish;

/**
 * Created by Varun on 12-Mar-17.
 */

public class AddShowActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_PICK_AUDIO = 0;
    private String senderPhoneNum;
    private String showPath;
    private String showTime;
    private String showDate;

    private Button createShowTime;
    private Button createShowDate;
    private Button createShowAudioFile;
    private Button createShowList;
    private Button createShowOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_show);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setTitle(getResources().getString(R.string.home_add_show));
        }

        createShowTime = (Button) findViewById(R.id.create_show_time);
        createShowDate = (Button) findViewById(R.id.create_show_date);
        createShowAudioFile = (Button) findViewById(R.id.create_show_audio_file);
        createShowList = (Button) findViewById(R.id.create_show_list);
        createShowOk = (Button) findViewById(R.id.create_show_ok);

        if(createShowTime != null)
            createShowTime.setOnClickListener(this);

        if(createShowDate != null)
            createShowDate.setOnClickListener(this);

        if(createShowAudioFile != null)
            createShowAudioFile.setOnClickListener(this);

        if(createShowList != null)
            createShowList.setOnClickListener(this);

        if(createShowOk != null)
            createShowOk.setOnClickListener(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        senderPhoneNum = pref.getString("phoneNum", "0000000000");

        // AMQP stuff
        AMQPPublish.setupConnectionFactory();
        AMQPPublish.publishToAMQP();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_show_time:
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(AddShowActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        showTime = "" + selectedHour + ":" + selectedMinute + ":00" ;
                        createShowTime.setText(getResources().getString(R.string.create_show_time) + " " + showTime);
                    }
                }, 15 , 0 , false);
                mTimePicker.setTitle("Select Time For the Show");
                mTimePicker.show();
                break;

            case R.id.create_show_date:
                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                class mDateSetListener implements DatePickerDialog.OnDateSetListener {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        showDate = "" + dayOfMonth + "/" + (monthOfYear+1) + "/" + year;
                        createShowDate.setText(getResources().getString(R.string.create_show_date) + " " + showDate);
                    }
                }
                DatePickerDialog dialog = new DatePickerDialog(AddShowActivity.this, new mDateSetListener(), mYear, mMonth, mDay);
                dialog.show();
                break;

            case R.id.create_show_audio_file:
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Audio File"), REQUEST_PICK_AUDIO);
                break;

            case R.id.create_show_list:
                break;

            case R.id.create_show_ok:
               /* if (create_show_list.getText().toString().trim().length() > 0
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
                }*/
                Toast.makeText(AddShowActivity.this, "Show Created! Press Back Now.", Toast.LENGTH_SHORT).show();
                break;
        }
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
            createShowAudioFile.setText(getResources().getString(R.string.create_show_audio_file) + " " + showPath);
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
