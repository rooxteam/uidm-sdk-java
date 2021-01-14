package com.rooxteam.uidm.sdk.spring.policy;

import com.rooxteam.sso.aal.Principal;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;
import java.util.Set;

/**
 * Service scans Spring Context for methods with @PreAuthorize annotation and returns methods which allowed for the user.
 */
public interface PermissionsEvaluationService {

    /**
     * Returns all available methods for the current user,
     *
     * @param authentication user's authentication
     * @return map of available endpoints and methods
     */
    Map<String, Set<RequestMethod>> evaluate(Authentication authentication);

    /**
     * Returns all available methods for the given user.
     *
     * @param authentication user's authentication
     * @param principal user's principal
     * @return map of available endpoints and methods
     */
    Map<String, Set<RequestMethod>> evaluate(Authentication authentication, Principal principal);

}
