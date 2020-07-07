import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class MaaS {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    //private static final String MAAS_QUEUE_NAME = "maas_queue";
    private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    private static final String ALTERNATE_EXCHANGE_NAME = "alternate_exchange";

    //static List<String> verList = new CopyOnWriteArrayList<>();
    static String activeVersion;
    static List<String> queueVersionsList = new ArrayList<>();


    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //channel.queueDeclare(MAAS_QUEUE_NAME, false, false, false, null);

        Map<String, Object> args = new HashMap<>();
        args.put("alternate-exchange", ALTERNATE_EXCHANGE_NAME);
        channel.exchangeDeclare(MASTER_EXCHANGE_NAME, "topic", false, false, args);
        channel.exchangeDeclare(ALTERNATE_EXCHANGE_NAME, "topic");

        String startVersion = "v1";
        List<String> queueList = new ArrayList<>();
        queueList.add("q1");
        queueList.add("q2");

        //verList.add("v1");
        createQueuesForVersion(MASTER_EXCHANGE_NAME, queueList, startVersion, channel);
        changeActiveVersion(ALTERNATE_EXCHANGE_NAME, startVersion, channel);



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
                createQueuesForVersion(MASTER_EXCHANGE_NAME, queueList, version, channel);
            } else if (line.startsWith("rollback")) {
                deleteQueueForVersion(version, channel);
            } else if (line.startsWith("active")) {
                changeActiveVersion(ALTERNATE_EXCHANGE_NAME, version, channel);
            }

            //channel.basicPublish("", MAAS_QUEUE_NAME, null, line.getBytes());
        }

        for (String queue : queueVersionsList) {
            channel.queueDelete(queue);
            System.out.println(String.format("Queue with name %s was deleted", queue));
        }
        channel.exchangeDelete(MASTER_EXCHANGE_NAME);
        channel.exchangeDelete(ALTERNATE_EXCHANGE_NAME);
        channel.close();
        connection.close();
    }

    private static void changeActiveVersion(String alternateExchangeName, String version, Channel channel) throws IOException {


        if (activeVersion != null) {
            for (String queue: queueVersionsList){
                if (queue.endsWith(activeVersion)) {
                    String queueName = queue.substring(0, 2);
                    channel.queueUnbind(queue, alternateExchangeName, "#." + queueName + ".#");
                    System.out.println(String.format("Queue '%s' was unbinded to exchange '%s' with routing key '%s'", queue, alternateExchangeName, "#." + queueName + ".#"));
                }
            }
        }

        for (String queue: queueVersionsList){
            if (queue.endsWith(version)) {
                String queueName = queue.substring(0, 2);
                channel.queueBind(queue, alternateExchangeName, "#." + queueName + ".#");
                System.out.println(String.format("Queue '%s' was binded to exchange '%s' with routing key '%s'", queue, alternateExchangeName, "#." + queueName + ".#"));
            }
        }
        activeVersion = version;
        System.out.println(String.format("Active version is '%s' now", activeVersion));
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
            System.out.println(String.format("Queue '%s' was created", queueVersionName));

            String routingKey = "#." + queue + ".#.bg-" + version + ".#";
            channel.queueBind(queueVersionName, exchangeName, routingKey);
            System.out.println(String.format("Queue '%s' was binded to exchange '%s' with routing key: '%s'", queueVersionName, MASTER_EXCHANGE_NAME, routingKey));

        }
    }


}
