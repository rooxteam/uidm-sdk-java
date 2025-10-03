package com.rooxteam.sso.aal.exception;

/**
 * Contains known error subtypes. WARNING: this is not enumeration because error subtype in an open dictionary.
 * Aal clients should process only subtypes that are known to it and ignore other.
 * Error subtype may not be specified.
 */
public class ErrorSubtypes {

    /**
     * IP address is not contained in configured allowed IP pools
     */
    public static final String IP_NOT_IN_POOL = "ip_not_in_pool";

    /**
     * Response from PCRF contains no customer id field
     */
    public static final String CUSTOMER_ID_VALIDATION_FAILED = "customer_id_validation_failed";


    /**
     * Response from PCRF contains no billing id field
     */
    public static final String BILLING_ID_VALIDATION_FAILED = "billing_id_validation_failed";

    /**
     * Customer IP is not contained in list of clientIps.
     */
    public static final String IP_NOT_IN_CLIENT_IPS = "ip_not_in_client_ips";

    /**
     * PCRF communication error
     */
    public static final String GENERIC_PCRF_ERROR = "generic_pcrf_error";

    /**
     * JWT token has expired and thus authentication failed
     */
    public static final String TOKEN_EXPIRED = "token_expired";


}
