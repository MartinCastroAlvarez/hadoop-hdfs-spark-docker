package com.martincastroalvarez.hadoop.exceptions;

/*
 * Custom exception class to handle invalid or empty topic strings.
 */
public class TopicException extends Exception {
    public TopicException(String message) {
        super(message);
    }
}
