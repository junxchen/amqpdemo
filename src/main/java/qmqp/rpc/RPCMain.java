package qmqp.rpc;

import lombok.extern.slf4j.Slf4j;

/**
 * 描述:
 *
 * @author junxi.chen
 * @create 2019-01-14 21:31
 */
@Slf4j
public class RPCMain {

    public static void main(String[] args) throws Exception {
        RPCClient rpcClient = new RPCClient();
        log.info(" [x] Requesting getMd5String(abc)");
        String response = rpcClient.call("abc");
        log.info(" [.] Got '" + response + "'");
        rpcClient.close();
    }
}
