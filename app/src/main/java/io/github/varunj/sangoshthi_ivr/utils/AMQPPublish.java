package io.github.varunj.sangoshthi_ivr.utils;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.json.JSONObject;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import io.github.varunj.sangoshthi_ivr.actvities.LoginActivity;

/**
 * Created by Varun on 22-Mar-17.
 */

public class AMQPPublish {

    public static Thread publishThread;
    public static JSONObject messagePresent;
    public static String QUEUE_NAME = "broadcaster_to_server_ivr";
    public static BlockingDeque<JSONObject> queue = new LinkedBlockingDeque<>();

    public static  ConnectionFactory factory = new ConnectionFactory();
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

    public static void publishToAMQP() {
        publishThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Connection connection = factory.newConnection();
                        Channel channel = connection.createChannel();
                        channel.confirmSelect();
                        while (true) {
                            messagePresent = queue.takeFirst();
                            try {
                                // xxx: read http://www.rabbitmq.com/api-guide.html. Set QueueName=RoutingKey to send message to only 1 queue
                                channel.exchangeDeclare("defaultExchangeName", "direct", true);
                                channel.queueDeclare(QUEUE_NAME, false, false, false, null);
                                channel.queueBind(QUEUE_NAME, "defaultExchangeName", QUEUE_NAME);
                                channel.basicPublish("defaultExchangeName", QUEUE_NAME, null, messagePresent.toString().getBytes());
                                System.out.println("xxx:" + messagePresent.toString());
                                channel.waitForConfirmsOrDie();
                            } catch (Exception e) {
                                queue.putFirst(messagePresent);
                                e.printStackTrace();
                                throw e;
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            Thread.sleep(5000); //sleep and then try again
                        } catch (InterruptedException e1) {
                            e1.printStackTrace();
                            break;
                        }
                    }
                }
            }
        });
        publishThread.start();
    }
}
