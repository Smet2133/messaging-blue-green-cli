import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.Calendar;


public class Consumer {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    //private static final String QUEUE_NAME = "q1";
    //private static final String VERSION = "v3";

    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String appVersion = Utils.getAppVersion(argv);
        String queueName = Utils.getQueueName(argv);
        consume(queueName, appVersion, channel);

        channel.close();
        connection.close();
    }

    static void consume(String queueName, String appVersion, Channel channel) throws Exception {
        System.out.println(" [*] Waiting for messages.");
        String fullQueueName = queueName + "-" + appVersion;
        System.out.println("fullqueuename: " + fullQueueName);
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            System.out.println(String.format(" I'm Consumer of version '%s'", appVersion));
            System.out.println(" [x] Headers: " + delivery.getProperties().getHeaders());
            System.out.println(" [x] Received '" +
                    delivery.getEnvelope().getRoutingKey() + "':'" + message + "'");

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        while (true){
            channel.basicConsume(fullQueueName, true, deliverCallback, consumerTag -> { });
        }

    }

}
