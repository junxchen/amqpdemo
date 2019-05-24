package qmqp.confirm;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
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
public class AmqpSyncProduce {

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
        //指定一个队列
        channel.queueDeclare("queue.gs.user.uagg.station.detail.browse-test", true, false, false, null);
        channel.queueBind("queue.gs.user.uagg.station.detail.browse-test","exchange.gs.user","gs.user.uagg.station.detail.browse");
        //发送的消息
        String message = "hello world!";
        //往队列中发出一条消息
        channel.basicPublish("exchange.gs.user", "gs.user.uagg.station.detail.browse", null, message.getBytes());
        System.out.println("Sent Message：'" + message + "'");
        // 同步机制
        try {
            // 确认ok
            if (channel.waitForConfirms()) {
                System.out.println("确认ok");
            // 失败从发
            } else {

                System.out.println("失败从发");
            }
        } catch (Exception ex) {

        }
        //关闭频道和连接
        channel.close();
        connection.close();
    }
}
