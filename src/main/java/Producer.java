import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class Producer {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    private static final String MAAS_QUEUE_NAME = "maas_queue";
    private static final String QUEUE1_NAME = "q1";
    private static final String QUEUE2_NAME = "q2";
    private static final String ROUTING_INFO = "some.key.info";
    private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    //private static final String VERSION = "v3";

    //static List<String> verList = new CopyOnWriteArrayList<>();
    //static List verList = Collections.synchronizedList(new ArrayList());
    static String activeVersion;

    public static void main(String[] argv) throws Exception {

        //verList.add("v1");
        //verList.add("v3");
        //verList.add("v4");
        activeVersion = "v1";

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String appVersion = Utils.getAppVersion(argv);
        String routeVersion = Utils.getRouteVersion(argv, appVersion);
        //setBasicVersions();
        //subscribeToMaasVersions(channel);

        System.out.println(String.format("Producer of version %s is intended to route message to queue of version %s", appVersion, routeVersion));

        while (true){
            publish(MASTER_EXCHANGE_NAME, ROUTING_INFO, "some-message", routeVersion, appVersion, channel);
        }

        //channel.close();
        //connection.close();
    }



    static void publish(String exchangeName, String initialRoutingKey, String message, String routeVersion, String appVersion, Channel channel) throws Exception {

/*
        String queueVersion;
        Boolean useActive = false;
        if (verList.contains(routeVersion)){  //v1 v3 v4
            queueVersion = routeVersion;
            System.out.println(String.format("Version %s exists, routing will be done to it.", routeVersion));
        } else {
            queueVersion = activeVersion; //v1
            useActive = true;
            System.out.println(String.format("Version %s doesn't exist, routing will be done to active version %s.", routeVersion, queueVersion));
        }
*/

        Map<String, Object> headers = new HashMap();
        headers.put("App version", appVersion);
        headers.put("Route version", routeVersion);
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.headers(headers);
        //while (true){
        String routingKey = getRandomQueue(initialRoutingKey);
        routingKey += ".bg-" + routeVersion;
        //routingKey = initialRoutingKey + "." + QUEUE2_NAME + ".bg-" + VERSION;

        String messageToSend = message + "-" + Calendar.getInstance().getTime();
        channel.basicPublish(exchangeName, routingKey, builder.build(), messageToSend.getBytes("UTF-8"));
        System.out.println(String.format(" [x] Sent from appVersion '%s' with route to '%s' with routing key '%s' message '%s'", appVersion, routeVersion, routingKey, messageToSend));
        Thread.sleep(3000);
        //}

    }

/*    private static List<String> getExistingVersions() {
        return verList;
    }*/

/*    private static String getActiveVersion() {
        return activeVersion;
    }*/

/*    private static void setBasicVersions() {
        verList.add("v1");
        verList.add("v3");
        verList.add("v4");
        activeVersion = "v1";
    }*/

/*
    private static void subscribeToMaasVersions(Channel channel) throws IOException {

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(" >>>>>> [x] Received '" + message + "'");

            String version = message.split(" ")[1];
            if (message.startsWith("promote")){
                verList.add(version);
            } else if (message.startsWith("rollback")) {
                verList.remove(version);
            } else if (message.startsWith("active")) {
                activeVersion = version;
            }
        };
        channel.basicConsume(MAAS_QUEUE_NAME, true, deliverCallback, consumerTag -> { });
    }
*/



    private static String getRandomQueue(String routingKey) {
        if (new Random().nextInt(2) == 0) {
            return routingKey + "." + QUEUE1_NAME;
        } else {
            return routingKey + "." + QUEUE2_NAME;
        }
    }

}
