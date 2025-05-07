package com.sky.task;

import com.sky.websocket.WebSocketServer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Class name: WebSockwtTask
 * Package: com.sky.task
 * Description:
 *
 * @Create: 2025/5/7 13:46
 * @Author: jay
 * @Version: 1.0
 */
@Component
public class WebSockwtTask {

    @Resource
    private WebSocketServer webSocketServer;

    public void sendMessageToClient(){
        webSocketServer.sendToAllClient("这里是来自服务端的消息:" + DateTimeFormatter
                .ofPattern("HH:mm:ss")
                .format(LocalDateTime.now()));
    }
}

