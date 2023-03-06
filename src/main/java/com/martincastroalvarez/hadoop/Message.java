package com.martincastroalvarez.hadoop;

import java.io.File;
import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.martincastroalvarez.hadoop.exceptions.MessageException;
import com.martincastroalvarez.hadoop.exceptions.SerializationException;

public class Message {

    private static final String TITLE = "title";
    private static final String TEXT = "text";
    private static final String TIMESTAMP = "timestamp";
    private static final Logger log = LoggerFactory.getLogger(Producer.class);
    private static Schema schema = null;

    private Date timestamp = new Date();
    private String text = "";
    private String title = "";

    /*
     * String serializer.
     * */
    public String toString() {
        return "<Message: " + this.getTitle() + ">";
    }

    /*
     * Title getter & setter.
     * */
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) throws MessageException {
        log.info("Message Title: " + title);
        if (title == null || title.trim().isEmpty()) {
            throw new MessageException("Message title string cannot be null or empty: " + title);
        }
        this.title = title;
    }

    /*
     * Message getter & setter.
     * */
    public String getText() {
        return this.text;
    }
    public void setText(String text) throws MessageException {
        log.info("Message: " + text);
        if (text == null || text.trim().isEmpty()) {
            throw new MessageException("Message string cannot be null or empty: " + text);
        }
        this.text = text;
    }

    /*
     * Date getter & setter.
     * */
    public Date getTimestamp() {
        return this.timestamp;
    }
    public void setTimestamp(Date timestamp) {
        log.info("Timestamp: " + timestamp);
        this.timestamp = timestamp;
    }
    public void setTimestamp(String timestamp) {
        log.info("Timestamp: " + timestamp);
        String format = "EEE MMM dd HH:mm:ss zzz yyyy";
        DateFormat dateFormat = new SimpleDateFormat(format);
        Date parsedDate = null;
        try {
            this.timestamp = dateFormat.parse(format);
        } catch (ParseException error) {
            System.err.println("Failed to parse date: " + error.getMessage());
        }
    }

    /*
     * Method responsible for loading the AVRO schema configuration.
     * Implements the Singleton design pattern.
     * */
    private static Schema getSchema() throws SerializationException {
        if (Message.schema == null) {
            try {
                URL url = Message.class.getClassLoader().getResource("message.avsc");
                if (url == null) {
                    throw new SerializationException("AVRO file not found!");
                }
                File schemaFile = new File(url.toURI());
                Message.schema = new Schema.Parser().parse(schemaFile);
            } catch (URISyntaxException error) {
                throw new SerializationException("Malformed AVRO URI!");
            } catch (IOException error) {
                throw new SerializationException("AVRO schema file not found!");
            }
        }
        return Message.schema;
    }

    /*
     * Method responsible for serializing a message using AVRO format.
     * */
    public String serialize() throws SerializationException {
        log.info("Serializing message.");
        try {
            Schema schema = Message.getSchema();
            GenericRecord record = new GenericData.Record(schema);
            record.put(TIMESTAMP, this.getTimestamp().toString());
            record.put(TEXT, this.getText());
            record.put(TITLE, this.getTitle());
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(output, null);
            DatumWriter<GenericRecord> writer = new GenericDatumWriter<>(schema);
            writer.write(record, encoder);
            encoder.flush();
            output.close();
            log.info("Serialized message: " + output);
            return output.toString();
        } catch (IOException error) {
            throw new SerializationException("Failed to serialize object: " + this);
        }
    }

    /*
     * Method responsible for deserializing a message using AVRO format.
     * */
    public static Message deserialize(String serialized) throws SerializationException {
        log.info("Deserializing message: " + serialized);
        try {
            Schema schema = Message.getSchema();
            DatumReader<GenericRecord> reader = new GenericDatumReader<GenericRecord>(schema);
            Decoder decoder = DecoderFactory.get().binaryDecoder(serialized.getBytes(), null);
            GenericRecord record = reader.read(null, decoder);
            Message message = new Message();
            message.setTitle(record.get(TITLE).toString());
            message.setText(record.get(TEXT).toString());
            message.setTimestamp(record.get(TIMESTAMP).toString());
            return message;
        } catch (MessageException error) {
            throw new SerializationException("Failed to deserialize Avro message: " + serialized);
        } catch (IOException error) {
            throw new SerializationException("Failed to deserialize object: " + serialized);
        }
    }

}
