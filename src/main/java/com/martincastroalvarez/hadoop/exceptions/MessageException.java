package com.martincastroalvarez.hadoop.exceptions;

/*
 * Custom exception class to handle invalid or empty message strings.
 */
public class MessageException extends Exception {
    public MessageException(String message) {
        super(message);
    }
}
