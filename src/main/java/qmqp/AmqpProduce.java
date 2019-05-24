package qmqp;

import java.io.IOException;

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

    public static void main(String[] args) throws IOException {

        /**
         * 创建连接连接到MabbitMQ
         */
        ConnectionFactory factory = new ConnectionFactory();
        //设置MabbitMQ所在主机ip或者主机名
        factory.setHost("10.6.1.190");
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
        try{
            channel.txSelect();
            //往队列中发出一条消息
            channel.basicPublish("exchange.gs.user", "gs.user.uagg.station.detail.browse", null, message.getBytes());
            System.out.println("Sent Message：'" + message + "'");
             int i = 1/0;
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
