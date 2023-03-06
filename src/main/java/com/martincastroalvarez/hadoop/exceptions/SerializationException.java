package com.martincastroalvarez.hadoop.exceptions;

/*
 * Custom exception class to handle invalid serialization objects.
 */
public class SerializationException extends Exception {
    public SerializationException(String message) {
        super(message);
    }
}
