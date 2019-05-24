package qmqp.delay.queue;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述:
 *
 * @author junxi.chen
 * @create 2019-01-10 14:50
 */
@Slf4j
public class AmqpConsumer {

    private static String queue_name = "test.queue";

    public static void main(String[] args) throws IOException, InterruptedException {
        //打开连接和创建频道，与发送端一样
        ConnectionFactory factory = new ConnectionFactory();
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
        // 声明队列
        channel.queueDeclare(queue_name, true, false, false, null);
        // 绑定路由
        channel.queueBind(queue_name, "delay.exchange", "delay.message");

        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 指定消费队列
        channel.basicConsume(queue_name, true, consumer);
        while (true) {
            // nextDelivery是一个阻塞方法（内部实现其实是阻塞队列的take方法）
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            String message = new String(delivery.getBody());
            System.out.println("received message:" + message + ",date:" + System.currentTimeMillis());
        }
    }
}
