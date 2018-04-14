/*
 * Copyright (c) 2017. Created by Deepak Sood
 */

package io.github.varunj.sangoshthi_ivr.network;

import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import org.json.JSONObject;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import io.github.varunj.sangoshthi_ivr.utilities.ConstantUtil;


public class AMQPPublish {

    private static final String TAG = AMQPPublish.class.getSimpleName();

    private static AMQPPublish instance;

    private AMQPPublish() {

    }

    public static synchronized AMQPPublish getInstance() {
        if(instance == null) {
            instance = new AMQPPublish();
        }
        return instance;
    }

    private static String EXCHANGE_NAME = "defaultExchangeName";
    private static String QUEUE_NAME = "broadcaster_to_server_ivr";

    public BlockingDeque<JSONObject> queue = new LinkedBlockingDeque<>();

    private ConnectionFactory factory = new ConnectionFactory();

    private Thread publishThread;
    private Thread subscribeThread;

    public void setupConnectionFactory() {
        try {
            factory.setAutomaticRecoveryEnabled(true);
            factory.setNetworkRecoveryInterval(10000);
            factory.setHost(ConstantUtil.IP_ADDR);
            factory.setPort(ConstantUtil.SERVER_PORT);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This function will declare and bind queue with the server
     */
    public void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.confirmSelect();

                        while (true) {
                            JSONObject message = queue.takeFirst();
                            try {
                                channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
                                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                                channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, QUEUE_NAME);

                                channel.basicPublish(EXCHANGE_NAME,
                                        QUEUE_NAME,
                                        new AMQP.BasicProperties.Builder().expiration("10000").build(),
                                        message.toString().getBytes());

                                Log.d(TAG, "[s] " + message);

                                channel.waitForConfirmsOrDie();
                            } catch (Exception e) {
                                Log.e(TAG,"[f] " + message);
                                queue.putFirst(message);
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        Log.e(TAG, "Interrupted Exception: " + e);
                        break;
                    } catch (Exception e) {
                        Log.d(TAG, "Connection broken: " + e.getClass().getName());
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e1) {
                            Log.e(TAG, "Interrupted Exception thrown: " + e1);
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();

    }

    public void publishMessage(JSONObject message) {
        try {
//            Log.d(TAG, "pushing message to queue - " + message);
            queue.putLast(message);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(final String senderPhoneNum) {

        subscribeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();

                        String queue_name = "server_to_broadcaster_ivr_" +  senderPhoneNum;
                        channel.queueDeclare(queue_name, false, false, false, null);
                        QueueingConsumer consumer = new QueueingConsumer(channel);
                        channel.basicConsume(queue_name, true, consumer);
                        Log.d(TAG, "listening to queue: " + queue_name);
                        while (true) {
                            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
                            final JSONObject message = new JSONObject(new String(delivery.getBody()));
                            Log.d(TAG, "[r] " + message.toString());

                            ResponseMessageHelper.getInstance().handle(message);

                        }
                    } catch (InterruptedException e) {
                        break;
                    } catch (Exception e1) {
                        Log.d("", "Connection broken: " + e1.getClass().getName());
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            }
        });
        subscribeThread.start();
    }

    public void interruptThreads() {
        if(publishThread != null)
            publishThread.interrupt();
        if(subscribeThread != null)
            subscribeThread.interrupt();
    }
}
