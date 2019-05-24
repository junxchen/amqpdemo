package qmqp.rpc;

import java.security.MessageDigest;

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
 * @create 2019-01-14 21:19
 */

@Slf4j
public class RPCServer {

    private static final String RPC_QUEUE_NAME = "rpc_queue";
    public static void main(String[] args) throws Exception {
        // 先建立连接、通道，并声明队列
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("127.0.0.1");
        factory.setUsername("gs-test");
        factory.setPassword("gs-test");
        factory.setVirtualHost("bd");
        factory.setPort(AMQP.PROTOCOL.PORT);
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);
        // 可以运行多个服务器进程。通过channel.basicQos设置prefetchCount属性可将负载平均分配到多台服务器上。
        channel.basicQos(1);
        QueueingConsumer consumer = new QueueingConsumer(channel);
        // 打开应答机制autoAck=false
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);
        log.info(" [x] Awaiting RPC requests");
        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();
            AMQP.BasicProperties props = delivery.getProperties();
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties.Builder()
                    .correlationId(props.getCorrelationId()).build();
            String message = new String(delivery.getBody());
            log.info(" [.] getMd5String(" + message + ")");
            String response = getMd5String(message);
            //返回处理结果队列
            channel.basicPublish("", props.getReplyTo(), replyProps,
                    response.getBytes());
            //发送应答
            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

    /**
     * 模拟RPC方法 获取MD5字符串
     * @param str
     * @return
     */
    public static String getMd5String(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++){
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
