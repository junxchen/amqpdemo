package qmqp.header;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

/**
 * 描述:
 *
 * @author junxi.chen
 * @create 2019-05-24 10:33
 */
public class Producer {
    private final static String EXCHANGE_NAME = "header-exchange";

    @SuppressWarnings("deprecation")
    public static void main(String[] args) throws Exception {
        // 创建连接和频道
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        // 指定用户 密码
        factory.setUsername("gs-test");
        factory.setPassword("gs-test");
        factory.setVirtualHost("bd");
        // 指定端口
        factory.setPort(AMQP.PROTOCOL.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        //声明转发器和类型headers
        channel.exchangeDeclare(EXCHANGE_NAME, "headers",false,true,null);
        String message = new Date().toLocaleString() + " : log something";

        Map<String,Object> headers =  new Hashtable<String, Object>();
        headers.put("aaa", "01234");
        AMQP.BasicProperties.Builder properties = new AMQP.BasicProperties.Builder();
        properties.headers(headers);

        // 指定消息发送到的转发器,绑定键值对headers键值对
        channel.basicPublish(EXCHANGE_NAME, "",properties.build(),message.getBytes());

        System.out.println("Sent message :'" + message + "'");
        channel.close();
        connection.close();
    }
}
