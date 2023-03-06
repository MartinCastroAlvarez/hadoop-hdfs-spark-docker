package com.martincastroalvarez.hadoop;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.log4j.BasicConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martincastroalvarez.hadoop.exceptions.MessageException;
import com.martincastroalvarez.hadoop.exceptions.HostException;
import com.martincastroalvarez.hadoop.exceptions.PortException;
import com.martincastroalvarez.hadoop.exceptions.TopicException;
import com.martincastroalvarez.hadoop.exceptions.SerializationException;
import com.martincastroalvarez.hadoop.Message;

public class Producer {

    private static final Logger log = LoggerFactory.getLogger(Producer.class);

    private String host = "";
    private String port = "";
    private String topic = "";
    private KafkaProducer<String, String> producer;

    /*
     * String serializer.
     * */
    public String toString() {
        return "<Producer: " + this.getHost() + ">";
    }

    /*
     * Main method. Opens a connection with Kafka, sends one message and closes the connection.
     * */
    public static void main(String[] args) throws MessageException, PortException, HostException, TopicException, SerializationException {
        BasicConfigurator.configure();
        Producer producer = new Producer();
        producer.setHost(System.getProperty("host"));
        producer.setTopic(System.getProperty("topic"));
        producer.setPort(System.getProperty("port"));
        Message message = new Message();
        message.setText(System.getProperty("message"));
        message.setTitle(System.getProperty("title"));
        producer.connect();
        try {
            producer.send(message);
        } finally {
            producer.disconnect();
        }
    }

    /*
     * Topic getter & setter.
     * */
    public String getTopic() {
        return this.topic;
    }
    public void setTopic(String topic) throws TopicException {
        log.info("Topic: " + topic);
        if (topic == null || topic.trim().isEmpty()) {
            throw new TopicException("Topic string cannot be null or empty.");
        }
        this.topic = topic;
    }

    /*
     * Host getter & setter.
     * */
    public String getHost() {
        return this.host;
    }
    public void setHost(String host) throws HostException {
        log.info("Host: " + host);
        if (host == null || host.trim().isEmpty()) {
            throw new HostException("Host string cannot be null or empty.");
        }
        this.host = host;
    }

    /*
     * Port getter & setter.
     * */
    public String getPort() {
        return this.port;
    }
    public void setPort(String port) throws PortException {
        log.info("Port: " + port);
        if (port == null || port.trim().isEmpty()) {
            throw new PortException("Port number cannot be null or empty.");
        }
        if (Integer.parseInt(port) < 1024) {
            throw new PortException("The port can not be empty.");
        }
        this.port = port;
    }

    /*
     * Method responsible for producing a message into the Kafka topic.
     * */
    public void send(Message message) throws SerializationException {
        log.info("Sending message to the Kafka server...");
        ProducerRecord<String, String> producerRecord = new ProducerRecord<>(this.getTopic(), message.serialize());
        producer.send(producerRecord);
        log.info("Message sent successfuly!");
    }

    /*
     * Method responsible for opening the connection with Kafka.
     * */
    public void connect() {
        log.info("Connecting with Kafka.");
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, this.getHost() + ":" + this.getPort());
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(properties);
        log.info("Connected with Kafka!");
    }

    /*
     * Method responsible for closing the connection with Kafka.
     * */
    public void disconnect() {
        log.info("Closing connection.");
        this.producer.flush();
        this.producer.close();
        log.info("Connection closed successfully!");
    }
}
