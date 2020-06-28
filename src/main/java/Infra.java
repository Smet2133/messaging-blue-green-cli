import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.util.Calendar;


public class Infra {

    //private static final String APPROACH = "aggr"; //master or aggr


    //private static final String MASTER_EXCHANGE_NAME = "master_exchange";
    private static final String EXCHANGE_NAME = "aggr_exchange";
    private static final String Q1 = "q1_v3";
    private static final String Q2 = "q2_v3";
    private static final String Q3 = "q1_v4";
    private static final String Q4 = "q2_v4";


    public static void main(String[] argv) throws Exception {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channel.queueDeclare(Q1, false, false, false, null);
        channel.queueDeclare(Q2, false, false, false, null);
        channel.queueDeclare(Q3, false, false, false, null);
        channel.queueDeclare(Q4, false, false, false, null);


        channel.queueBind(Q1, EXCHANGE_NAME, "#.q1.#.bg-v3.#");
        channel.queueBind(Q2, EXCHANGE_NAME, "#.q2.#.bg-v3.#");
        channel.queueBind(Q3, EXCHANGE_NAME, "#.q1.#.bg-v4.#");
        channel.queueBind(Q4, EXCHANGE_NAME, "#.q2.#.bg-v4.#");

        //channel.queueBind(Q1, EXCHANGE_NAME, "#.black.white.#.bg-v3.#");
        //channel.queueBind(Q2, EXCHANGE_NAME, "#.black.#.bg-v3.#");


        System.in.read();

        channel.close();
        connection.close();
    }



}
