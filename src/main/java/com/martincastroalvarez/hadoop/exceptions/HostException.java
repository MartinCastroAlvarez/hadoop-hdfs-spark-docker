package com.martincastroalvarez.hadoop.exceptions;

/*
 * Custom exception class to handle invalid or empty hostname strings.
 */
public class HostException extends Exception {
    public HostException(String message) {
        super(message);
    }
}
