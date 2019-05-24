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
public class AmqpAsyncProduce {

    public static void main(String[] args) throws IOException {
        Thread thread = new Thread(new Runnable() {

            public void run() {

                try {
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
                    channel.confirmSelect();

                    //往队列中发出一条消息
                    //发送持久化消息
                    for(int i = 0;i < 100;i++)
                    {
                        //第一个参数是exchangeName(默认情况下代理服务器端是存在一个""名字的exchange的,
                        //因此如果不创建exchange的话我们可以直接将该参数设置成"",如果创建了exchange的话
                        //我们需要将该参数设置成创建的exchange的名字),第二个参数是路由键
                        channel.basicPublish("exchange.gs.user", "gs.user.uagg.station.detail.browse", null, message.getBytes());
                    }

                    System.out.println("Sent Message：'" + message + "'");
                    // 异步机制
                    channel.addConfirmListener(new ConfirmListener() {
                        // 确认ok
                        public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                            System.out.println("ack: deliveryTag = "+deliveryTag+" multiple: "+multiple);
                        }

                        // 失败重发
                        public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                            System.out.println("nack: deliveryTag = "+deliveryTag+" multiple: "+multiple);
                        }
                    });

                    //关闭频道和连接
                    //channel.close();
                    //connection.close();
                } catch (Exception ex) {

                }

                while (true){}

            }


        });

        thread.start();
    }
}
