package qmqp.delay.message;

import java.io.IOException;
import java.util.HashMap;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述:
 *
 * @author junxi.chen
 * @create 2019-01-10 12:23
 */
@Slf4j
public class AmqpProduce {

    private static String queue_name = "message_ttl_queue";

    public static void main(String[] args) throws IOException {

        /**
         * 创建连接连接到MabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        //设置MabbitMQ所在主机ip或者主机名
        factory.setHost("127.0.0.1");
        //指定用户 密码
        factory.setUsername("gs-test");
        factory.setPassword("gs-test");
        factory.setVirtualHost("bd");
        //指定端口
        factory.setPort(AMQP.PROTOCOL.PORT);
        //创建一个连接
        Connection connection = factory.newConnection();
        //创建一个频道
        Channel channel = connection.createChannel();

        HashMap<String, Object> arguments = new HashMap<String, Object>();
        arguments.put("x-dead-letter-exchange", "delay.message.exchange");
        arguments.put("x-dead-letter-routing-key", "message_ttl_routingKey");
        // 声明队列
        channel.queueDeclare("delay.message.queue", true, false, false, arguments);
        // 绑定路由
        channel.queueBind("delay.message.queue","delaysync.exchange", "message_ttl_routingKey");
        //发送的消息
        String message = "hello world!";
        try{
            channel.txSelect();
            //往队列中发出一条消息
            // 设置延时属性
            AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
            // 持久性 non-persistent (1) or persistent (2)
            AMQP.BasicProperties properties = builder.expiration("10000").deliveryMode(2).build();
            // routingKey =delay_queue 进行转发
            channel.basicPublish("delaysync.exchange", "message_ttl_routingKey", properties, message.getBytes());
            System.out.println("Sent Message：'" + message + "'");
            channel.txCommit();
        }catch (Exception ex){
            log.error(ex.getMessage(),ex);
            channel.txRollback();
        }

        //关闭频道和连接
        channel.close();
        connection.close();
    }
}
