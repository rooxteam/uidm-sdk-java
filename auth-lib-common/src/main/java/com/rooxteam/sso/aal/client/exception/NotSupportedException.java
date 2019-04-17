package com.rooxteam.sso.aal.client.exception;

/**
 * The NotSupportedException exception indicates that an operation is not
 * supported.
 */
public class NotSupportedException extends RuntimeException {

    /**
     * Creates a new <code>NotSupportedException</code> without a
     * detail message.
     */
    public NotSupportedException() {
    }

    /**
     * Constructs an <code>NotSupportedException</code> with the specified
     * detail message.
     *
     * @param msg the detail message.
     */
    public NotSupportedException(String msg) {
        super(msg);
    }
}
