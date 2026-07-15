package com.wada.ola.common.exception;

/**
 * Thrown when an authenticated user attempts to access or manage a resource
 * outside their authorized command scope (e.g. a Zone Manager touching a
 * member of another zone). Maps to HTTP 403 Forbidden.
 */
public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException(String message) {
        super(message);
    }
}
