package com.martincastroalvarez.hadoop.exceptions;

/*
 * Custom exception class to handle invalid or empty port numbers.
 */
public class PortException extends Exception {
    public PortException(String message) {
        super(message);
    }
}
