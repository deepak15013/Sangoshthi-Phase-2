package io.github.varunj.sangoshthi_ivr.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import java.util.Date;

import io.github.varunj.sangoshthi_ivr.R;
import io.github.varunj.sangoshthi_ivr.network.AMQPPublish;

/**
 * Created by Varun on 12-Mar-17.
 */

public class HomeActivity extends AppCompatActivity {

    public static final ArrayList<String> show_id = new ArrayList<>();
    public static final ArrayList<String> time_of_air = new ArrayList<>();
    public static final ArrayList<String> audio_name = new ArrayList<>();
    public static final ArrayList<String> ashalist = new ArrayList<>();

    public static final ArrayList<String> notifications_message = new ArrayList<>();
    public static final ArrayList<String> notifications_state = new ArrayList<>();
    public static final ArrayList<String> notifications_message_id = new ArrayList<>();

    private String senderPhoneNum;
    Thread subscribeThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        senderPhoneNum = pref.getString("phoneNum", "0000000000");

        final Button button_home_add_show = (Button) findViewById(R.id.home_add_show);
        button_home_add_show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AddShowActivity.class);
                startActivity(intent);
            }
        });

        final Button button_home_host_show = (Button) findViewById(R.id.home_host_show);
        button_home_host_show.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HostShowActivity.class);
                startActivity(intent);

            }
        });

        final Button button_home_notifications = (Button) findViewById(R.id.home_notifications);
        button_home_notifications.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {Intent intent = new Intent(getApplicationContext(), NotificationsActivity.class);
                startActivity(intent);
            }
        });

        final Button button_home_help = (Button) findViewById(R.id.home_help);
        button_home_help.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {Intent intent = new Intent(getApplicationContext(), TutorialsActivity.class);
                startActivity(intent);
            }
        });

        // AMQP stuff
        // TODO: 03-06-2017
        /*AMQPPublish.setupConnectionFactory();
        AMQPPublish.publishToAMQP();*/
        /*setupConnectionFactory();
        subscribe();*/

        // TODO: 03-06-2017
        /*try {
            final JSONObject jsonObject = new JSONObject();
            //primary key: <broadcaster, show_name>
            jsonObject.put("objective", "app_launch_notify");
            jsonObject.put("broadcaster_phoneno", senderPhoneNum);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.queue.putLast(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();

        show_id.clear();
        time_of_air.clear();
        audio_name.clear();
        ashalist.clear();
        notifications_message.clear();
        notifications_message_id.clear();
        notifications_state.clear();

        // TODO: 03-06-2017
       /* try {
            final JSONObject jsonObject = new JSONObject();
            //primary key: <broadcaster, show_name>
            jsonObject.put("objective", "get_notifications");
            jsonObject.put("broadcaster_phoneno", senderPhoneNum);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.queue.putLast(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            final JSONObject jsonObject = new JSONObject();
            //primary key: <broadcaster, show_name>
            jsonObject.put("objective", "get_show_list");
            jsonObject.put("broadcaster_phoneno", senderPhoneNum);
            jsonObject.put("timestamp", DateFormat.getDateTimeInstance().format(new Date()));
            AMQPPublish.queue.putLast(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        if (AMQPPublish.publishThread != null)
            AMQPPublish.publishThread.interrupt();
        if (subscribeThread != null)
            subscribeThread.interrupt();
        super.onBackPressed();
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

                        String queue_name = "server_to_broadcaster_ivr_" +  senderPhoneNum;
                        // xxx: read http://www.rabbitmq.com/tutorials/tutorial-three-python.html, http://stackoverflow.com/questions/10620976/rabbitmq-amqp-single-queue-multiple-consumers-for-same-message
                        channel.queueDeclare(queue_name, false, false, false, null);
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(queue_name, true, consumer);

                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            final JSONObject message = new JSONObject(new String(delivery.getBody()));
                            System.out.println("xxx:" + message.toString());

                            // populate hostShowList
                            if (message.getString("objective").equals("show_list_populate")) {
                                JSONArray jsonArr = message.getJSONArray("info");
                                if (jsonArr != null) {
                                    try {
                                        for (int i = 0; i < jsonArr.length(); i++) {
                                            JSONObject c = jsonArr.getJSONObject(i);
                                            show_id.add(c.getString("show_id"));
                                            time_of_air.add(c.getString("time_of_airing"));
                                            audio_name.add(c.getString("audio_name"));
                                            ashalist.add(c.getString("list_of_listeners"));
                                        }
                                    }
                                    catch (final JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
                            }

                            // notifications
                            if (message.getString("objective").equals("notify")) {
                                JSONArray jsonArr = message.getJSONArray("info");
                                if (jsonArr != null) {
                                    try {
                                        for (int i = 0; i < jsonArr.length(); i++) {
                                            JSONObject c = jsonArr.getJSONObject(i);
                                            notifications_message.add(c.getString("body"));
                                            notifications_state.add(c.getString("read_status"));
                                            notifications_message_id.add(c.getString("msg_id"));
                                        }
                                    }
                                    catch (final JSONException e) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                }
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
    }*/
    }
}
