package com.lrq.springbootalimq.controller;

import com.aliyun.openservices.ons.api.Message;
import com.lrq.springbootalimq.config.MqConfig;
import com.lrq.springbootalimq.mq.MqProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/msg")
public class MsgController {
    @Autowired
    private MqProducer mqProducer;
    @Autowired
    private MqConfig mqConfig;

    @PostMapping("/send")
    public Message msg(String msgbody){
        Message message = new Message(
                // Message 所属的 Topic
                mqConfig.getTopic(),
                // Message Tag,
                // 可理解为 Gmail 中的标签，对消息进行再归类，方便 Consumer 指定过滤条件在 MQ 服务器过滤
                mqConfig.getTag(),
                // Message Body
                // 任何二进制形式的数据，MQ 不做任何干预，需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                msgbody.getBytes());
        mqProducer.sendMessage(message);
        return message;
    }
}
