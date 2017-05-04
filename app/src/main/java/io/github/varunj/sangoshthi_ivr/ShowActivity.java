package io.github.varunj.sangoshthi_ivr;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Varun on 12-Mar-17.
 */

public class ShowActivity extends AppCompatActivity {

    private String senderPhoneNum;
    private String show_id, time_of_air, audio_name, ashalist;
    private int poll = 0;
    ArrayList<String> ashaListNames;
    ArrayList<Integer> ashaListQuery;
    ArrayList<Integer> ashaListOnline;
    ArrayList<Integer> ashaListMute;
    ArrayList<String> ashaListPoll;
    Thread subscribeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        senderPhoneNum = pref.getString("phoneNum", "0000000000");

        // AMQP stuff
        AMQPPublish.setupConnectionFactory();
        AMQPPublish.publishToAMQP();
        setupConnectionFactory();
        subscribe();

        // get showName and senderPhoneNumber and videoname
        Intent i = getIntent();
        show_id = i.getStringExtra("show_id");
        time_of_air = i.getStringExtra("time_of_air");
        audio_name = "/" + i.getStringExtra("audio_name");
        ashalist = ""+i.getStringExtra("ashalist").toString();

        // build list
        final String[] temp1 = ashalist.replace("[","").replace("]","").replace("\"","").replace("\"","").split(",");
        ashaListNames = new ArrayList<>(Arrays.asList(temp1));
        ashaListOnline = new ArrayList<>(Collections.nCopies(temp1.length, R.drawable.red));
        ashaListQuery = new ArrayList<>(Collections.nCopies(temp1.length, 0));
        ashaListMute = new ArrayList<>(Collections.nCopies(temp1.length, R.drawable.speakernot));
        ashaListPoll = new ArrayList<>(Collections.nCopies(temp1.length, "-"));
        populateAshaList(ashaListNames);

        // call self
        final Button show_call_self = (Button) findViewById(R.id.show_call_self);
        show_call_self.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_red));
        show_call_self.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    //primary key: <, >
                    jsonObject.put("objective", "start_show");
                    jsonObject.put("show_id", show_id);
                    jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                    AMQPPublish.queue.putLast(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                show_call_self.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_green));
            }
        });

        // call everyone
        final Button show_call_else = (Button) findViewById(R.id.show_call_else);
        show_call_else.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_red));
        show_call_else.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    final JSONObject jsonObject = new JSONObject();
                    //primary key: <, >
                    jsonObject.put("objective", "dial_listeners");
                    jsonObject.put("show_id", show_id);
                    jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                    AMQPPublish.queue.putLast(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                show_call_else.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_green));
            }
        });

        // start poll
        final Button show_start_poll = (Button) findViewById(R.id.show_start_poll);
        show_start_poll.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_red));
        show_start_poll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // start poll
                if (poll%2 == 0) {
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        //primary key: <, >
                        jsonObject.put("objective", "start_polling");
                        jsonObject.put("show_id", show_id);
                        jsonObject.put("poll_id", poll/2);
                        jsonObject.put("no_options", "3");
                        jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                        AMQPPublish.queue.putLast(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    show_start_poll.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_green));
                    poll++;
                    // reset poll answers
                    ashaListPoll = new ArrayList<>(Collections.nCopies(temp1.length, "-"));
                    populateAshaList(ashaListNames);
                }
                // end poll
                else {
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        //primary key: <, >
                        jsonObject.put("objective", "stop_polling");
                        jsonObject.put("show_id", show_id);
                        jsonObject.put("poll_id", (poll-1)/2);
                        jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                        AMQPPublish.queue.putLast(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    show_start_poll.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.add_show_red));
                    poll++;
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (AMQPPublish.publishThread != null)
            AMQPPublish.publishThread.interrupt();
        if (subscribeThread != null)
            subscribeThread.interrupt();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Closing Activity")
                .setMessage("Sure you don't want to continue?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // send closing command for freeswitch
                        try {
                            final JSONObject jsonObject = new JSONObject();
                            //primary key: <, >
                            jsonObject.put("objective", "end_show");
                            jsonObject.put("show_id", show_id);
                            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                            AMQPPublish.queue.putLast(jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (AMQPPublish.publishThread != null)
                            AMQPPublish.publishThread.interrupt();
                        if (subscribeThread != null)
                            subscribeThread.interrupt();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    void populateAshaList(final ArrayList<String> ashaListNames) {
        ListView list = (ListView)findViewById(R.id.show_ashalist_master);
        ShowListAdapter adapter = new ShowListAdapter(this, ashaListNames, ashaListOnline, ashaListQuery, ashaListMute, ashaListPoll);
        adapter.setNotifyOnChange(true);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // mute unmute
                if (ashaListMute.get(position) == R.drawable.speakernot) {
                    ashaListMute.set(position, R.drawable.speaker);
                    populateAshaList(ashaListNames);
                    // send unmute command for freeswitch
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        //primary key: <, >
                        jsonObject.put("objective", "unmute");
                        jsonObject.put("show_id", show_id);
                        jsonObject.put("sender_phone_no", senderPhoneNum);
                        jsonObject.put("listener_phoneno", ashaListNames.get(position));
                        jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                        AMQPPublish.queue.putLast(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    ashaListMute.set(position, R.drawable.speakernot);
                    populateAshaList(ashaListNames);
                    // send mute command for freeswitch
                    try {
                        final JSONObject jsonObject = new JSONObject();
                        //primary key: <, >
                        jsonObject.put("objective", "mute");
                        jsonObject.put("show_id", show_id);
                        jsonObject.put("sender_phone_no", senderPhoneNum);
                        jsonObject.put("listener_phoneno", ashaListNames.get(position));
                        jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
                        AMQPPublish.queue.putLast(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    // subscribe to RabbitMQ
    public static ConnectionFactory factory = new ConnectionFactory();
    public static  void setupConnectionFactory() {
        try {
            factory.setUsername(LoginActivity.SERVER_USERNAME);
            factory.setPassword(LoginActivity.SERVER_PASS);
            factory.setHost(LoginActivity.IP_ADDR);
            factory.setPort(LoginActivity.SERVER_PORT);
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(10000);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    void subscribe() {
        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();

                        String queue_name = "server_to_broadcaster_ivr_show_" +  senderPhoneNum;
                        // xxx: read http://www.rabbitmq.com/tutorials/tutorial-three-python.html, http://stackoverflow.com/questions/10620976/rabbitmq-amqp-single-queue-multiple-consumers-for-same-message
                        channel.queueDeclare(queue_name, false, false, false, null);
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(queue_name, true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            final JSONObject message = new JSONObject(new String(delivery.getBody()));
                            System.out.println("xxx1:" + message.toString());

                            // asha query
                            if (message.getString("objective").equals("press_1_event") && message.getString("show_id").equals(show_id)) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            ashaListQuery.set(ashaListNames.indexOf(message.getString("phoneno")), R.drawable.query);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        populateAshaList(ashaListNames);
                                    }
                                });

                            }

                            // asha active
                            else if (message.getString("objective").equals("conf_member_status") && message.getString("show_id").equals(show_id) && !message.getString("phoneno").equals(senderPhoneNum)) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            if (message.getString("task").equals("online")) {
                                            ashaListOnline.set(ashaListNames.indexOf(message.getString("phoneno")), R.drawable.green);
                                            }
                                            if (message.getString("task").equals("offline")) {
                                                System.out.println("xxx:111");
                                                ashaListOnline.set(ashaListNames.indexOf(message.getString("phoneno")), R.drawable.red);
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        populateAshaList(ashaListNames);
                                    }
                                });
                            }

                            // asha poll result
                            if (message.getString("objective").equals("get_polling_result_response") && message.getString("show_id").equals(show_id)) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        try {
                                            ashaListPoll.set(ashaListNames.indexOf(message.getString("phoneno")), message.getString("response"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        populateAshaList(ashaListNames);

                                        // update text field
                                        final TextView show_start_poll_result_cumm = (TextView) findViewById(R.id.show_start_poll_result_cumm);
                                        Map<String, Integer> map = new HashMap<String, Integer>();
                                        for (String x : ashaListPoll){
                                            if (map.containsKey(x)) {
                                                map.put(x, map.get(x) + 1);
                                            }
                                            else {
                                                map.put(x, 0);
                                            }
                                        }
                                        show_start_poll_result_cumm.setText(map.toString());
                                    }
                                });

                            }
                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        try {
                            Thread.sleep(4000); //sleep and then try again
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }

}
