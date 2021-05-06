package com.example.message;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.qpid.client.AMQAnyDestination;
import org.apache.qpid.client.AMQConnectionFactory;
import org.apache.qpid.client.AMQTopic;
import org.apache.qpid.client.message.JMSBytesMessage;
import org.apache.qpid.url.URLSyntaxException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.net.URISyntaxException;
import java.util.Map;

@Slf4j
public class QpidHelper {
    private static String url = "amqp://guest:guest@test/?brokerlist='tcp://172.16.75.171:5672'";
    private static String queueName = "Service.MessageResend";
    public static final String X_APP_ID = "x-amqp-0-10.app-id";
    private String address;
    private final JmsTemplate jmsTemplate;
    private static QpidHelper helper = null;

    public QpidHelper() throws URLSyntaxException {
        ConnectionFactory connectionFactory = new AMQConnectionFactory(this.url);
        JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory);
        address = String.format("ADDR:%s;{create:always, node:{type:queue}}", queueName);
        jmsTemplate.setDefaultDestinationName(queueName);
        jmsTemplate.setDeliveryPersistent(true);
        this.jmsTemplate = jmsTemplate;
    }

    public static synchronized QpidHelper getInstance() throws URLSyntaxException {
        if(helper == null) {
            helper = new QpidHelper();
        }
        return helper;
    }

    private static JMSBytesMessage buildMsg(final Session session, final String correlationId, final Map<String, Object> map) throws JMSException {
        JMSBytesMessage jmsBytesMessage = (JMSBytesMessage) session.createBytesMessage();
        jmsBytesMessage.setContentType("fix/map");
        jmsBytesMessage.setJMSCorrelationID(correlationId);
        jmsBytesMessage.setJMSRedelivered(true);
        jmsBytesMessage.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
        jmsBytesMessage.setStringProperty(X_APP_ID, IdUtil.fastSimpleUUID());
        if (map != null) {
            jmsBytesMessage.writeBytes(MsgBuild.build(map));
        }
        return jmsBytesMessage;
    }

    public void sendString(final String string) throws URISyntaxException {
        Destination destination = new AMQAnyDestination(this.address);
        this.jmsTemplate.send(destination, new DefaultMessageCreator(string));
        log.info("send" + string);
    }

    public void publish(final String topic, final String correlationId, final Map<String, Object> map) throws URISyntaxException {
        try {
            jmsTemplate.setPubSubDomain(true);
            Destination dest = new AMQTopic(topic);
            jmsTemplate.send(dest, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    JMSBytesMessage msg = (JMSBytesMessage) session.createBytesMessage();
                    msg.setContentType("fix/map");
                    msg.setJMSCorrelationID(correlationId);
                    msg.setJMSRedelivered(true);
                    msg.setJMSDeliveryMode(DeliveryMode.PERSISTENT);
                    msg.setStringProperty(X_APP_ID, IdUtil.fastSimpleUUID());
                    if (map != null) {
                        msg.writeBytes(MsgBuild.build(map));
                    }
                    return msg;
                }
            });
            log.info("publish" + map.toString());
        } catch (URISyntaxException e) {
            log.error("error");
        } finally {
            jmsTemplate.setPubSubDomain(false);
        }
    }

    public static class DefaultMessageCreator implements MessageCreator {
        String correlationId;
        Map<String, Object> map;

        public DefaultMessageCreator(String correlationId) {
            super();
            this.correlationId = correlationId;
        }

        public DefaultMessageCreator(String correlationId, Map<String, Object> map) {
            super();
            this.correlationId = correlationId;
            this.map = map;
        }

        @Override
        public Message createMessage(Session session) throws JMSException {
            return buildMsg(session, correlationId, map);
        }
    }
}
