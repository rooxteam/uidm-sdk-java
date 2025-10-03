package com.rooxteam.sso.aal;

/**
 * Represents cache key that can be stored in a hash map. Requires to override default equals and hashcode methods.
 *
 * @see com.rooxteam.sso.aal.PrincipalKey
 * @see com.rooxteam.sso.aal.PolicyDecisionKey
 */
interface AalCacheKey {
    boolean equals(Object obj);

    int hashCode();
}
