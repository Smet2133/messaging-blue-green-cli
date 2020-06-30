import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;


public class MaaS {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    private static final String MAAS_QUEUE_NAME = "maas_queue";
    private static final String EXCHANGE_NAME = "aggr_exchange";

    static List<String> verList = new CopyOnWriteArrayList<>();
    static String activeVersion;
    static List<String> queueVersionsList = new ArrayList<>();


    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.queueDeclare(MAAS_QUEUE_NAME, false, false, false, null);

        verList.add("v1");
        activeVersion = "v1";
        List<String> queueList = new ArrayList<>();
        queueList.add("q1");
        queueList.add("q2");

        createQueuesForVersion(EXCHANGE_NAME, queueList, activeVersion, channel);

        System.out.println("Commands: promote v6, rollback v6, active v6");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>>");
            String line = scanner.nextLine();
            if (line.equals("exit")) {
                break;
            }

            if (line.split(" ").length != 2 ) {
                continue;
            }
            String version = line.split(" ")[1];
            if (line.startsWith("promote")){
                verList.add(version);
                createQueuesForVersion(EXCHANGE_NAME, queueList, version, channel);
            } else if (line.startsWith("rollback")) {
                verList.remove(version);
                deleteQueueForVersion(version, channel);
//                System.out.println(String.format("Queues with version %s were deleted", version));
            } else if (line.startsWith("active")) {
                activeVersion = version;
                System.out.println(String.format("Active version now is '%s' now", activeVersion));
            }

            channel.basicPublish("", MAAS_QUEUE_NAME, null, line.getBytes());
        }

        for (String queue : queueVersionsList) {
            channel.queueDelete(queue);
            System.out.println(String.format("Queue with name %s was deleted", queue));
        }
        channel.exchangeDelete(EXCHANGE_NAME);
        channel.close();
        connection.close();
    }

    private static void deleteQueueForVersion(String version, Channel channel) throws IOException {
        for (String queue: queueVersionsList){
            if (queue.endsWith(version)) {
                channel.queueDelete(queue);
                System.out.println(String.format("Queue with name %s was deleted", queue));
            }
        }
        queueVersionsList.removeIf(s -> s.endsWith(version));
    }

    private static void createQueuesForVersion(String exchangeName, List<String> queueList, String version, Channel channel) throws IOException {
        for (String queue : queueList){
            String queueVersionName = queue + "_" + version;
            queueVersionsList.add(queueVersionName);
            channel.queueDeclare(queueVersionName, false, false, false, null);
            System.out.println(String.format("Queue with name %s was created", queueVersionName));

            String routingKey = "#." + queue + ".#.bg-" + version + ".#";
            channel.queueBind(queueVersionName, exchangeName, routingKey);
            System.out.println(String.format("Queue with name %s was binded to exchange with name %s with routing key: %s", queueVersionName, EXCHANGE_NAME, routingKey));

        }
    }


}
