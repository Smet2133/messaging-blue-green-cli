import com.rabbitmq.client.*;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Producer {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    private static final String QUEUE1_NAME = "q1";
    private static final String QUEUE2_NAME = "q2";
    private static final String ROUTING_INFO = "some.key.info";
    private static final String EXCHANGE_NAME = "aggr_exchange";
    //private static final String VERSION = "v3";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String appVersion = Utils.getAppVersion(argv);
        publish(EXCHANGE_NAME, ROUTING_INFO, "some-message", appVersion, channel);

        channel.close();
        connection.close();
    }

    static void publish(String exchangeName, String initialRoutingKey, String message, String appVersion, Channel channel) throws Exception {

        Map<String, Object> headers = new HashMap();
        headers.put("Version", appVersion);
        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.headers(headers);
        while (true){
            String routingKey = getRandomQueue(initialRoutingKey);
            routingKey += ".bg-" + appVersion;
            //routingKey = initialRoutingKey + "." + QUEUE2_NAME + ".bg-" + VERSION;

            String messageToSend = message + "-" + Calendar.getInstance().getTime();
            channel.basicPublish(exchangeName, routingKey, builder.build(), messageToSend.getBytes("UTF-8"));
            System.out.println(" [x] Sent '" + routingKey + "':'" + messageToSend + "'");
            Thread.sleep(1000);
        }

    }

    private static String getRandomQueue(String routingKey) {
        if (new Random().nextInt(2) == 0) {
            return routingKey + "." + QUEUE1_NAME;
        } else {
            return routingKey + "." + QUEUE2_NAME;
        }
    }


    private static String getRouteToVersion(String[] strings) {
        return null;
    }

}
