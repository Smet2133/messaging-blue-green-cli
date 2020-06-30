import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.ArrayList;
import java.util.List;


public class Infra {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    private static final String EXCHANGE_NAME = "aggr_exchange";
/*    private static final String q1_v1 = "q1_v1";
    private static final String q2_v1 = "q2_v1";
    private static final String q1_v3 = "q1_v3";
    private static final String q2_v3 = "q2_v3";
    private static final String q1_v4 = "q1_v4";
    private static final String q2_v4 = "q2_v4";*/


    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        System.out.println(String.format("Exchange with name %s was created", EXCHANGE_NAME));

        List<String> verList = Utils.getInfraVersionsList(argv);
        List<String> queueList = new ArrayList<>();
        queueList.add("q1");
        queueList.add("q2");

        List<String> queueVersionsList = new ArrayList<>();
        for (String version : verList) {
            for (String queue : queueList){
                String queueVersionName = queue + "_" + version;
                queueVersionsList.add(queueVersionName);
                channel.queueDeclare(queueVersionName, false, false, false, null);
                System.out.println(String.format("Queue with name %s was created", queueVersionName));

                String routingKey = "#." + queue + ".#.bg-" + version + ".#";
                channel.queueBind(queueVersionName, EXCHANGE_NAME, routingKey);
                System.out.println(String.format("Queue with name %s was binded to exchange with name %s with routing key: %s", queueVersionName, EXCHANGE_NAME, routingKey));

            }
        }


/*        channel.queueDeclare(q1_v1, false, false, false, null);
        channel.queueDeclare(q2_v1, false, false, false, null);
        channel.queueDeclare(q1_v3, false, false, false, null);
        channel.queueDeclare(q2_v3, false, false, false, null);
        channel.queueDeclare(q1_v4, false, false, false, null);
        channel.queueDeclare(q2_v4, false, false, false, null);


        channel.queueBind(q1_v1, EXCHANGE_NAME, "#.q1.#.bg-v1.#");
        channel.queueBind(q2_v1, EXCHANGE_NAME, "#.q2.#.bg-v1.#");
        channel.queueBind(q1_v3, EXCHANGE_NAME, "#.q1.#.bg-v3.#");
        channel.queueBind(q2_v3, EXCHANGE_NAME, "#.q2.#.bg-v3.#");
        channel.queueBind(q1_v4, EXCHANGE_NAME, "#.q1.#.bg-v4.#");
        channel.queueBind(q2_v4, EXCHANGE_NAME, "#.q2.#.bg-v4.#");*/

        //channel.queueBind(Q1, EXCHANGE_NAME, "#.black.white.#.bg-v3.#");
        //channel.queueBind(Q2, EXCHANGE_NAME, "#.black.#.bg-v3.#");


        System.in.read();

        for (String queue : queueVersionsList) {
            channel.queueDelete(queue);
            System.out.println(String.format("Queue with name %s was deleted", queue));
        }
        channel.exchangeDelete(EXCHANGE_NAME);
        System.out.println(String.format("Exchange with name %s was deleted", EXCHANGE_NAME));


        channel.close();
        connection.close();
    }



}
