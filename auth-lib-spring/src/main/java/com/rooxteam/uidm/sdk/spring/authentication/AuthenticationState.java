package com.rooxteam.uidm.sdk.spring.authentication;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;

import java.util.*;

/**
 * Authentication state that is populated during authentication flow. Login beans should populate this object and set isAuthenticated flag
 */
@Getter
@Setter(AccessLevel.PACKAGE)
public class AuthenticationState extends AbstractAuthenticationToken {
    private String principal;
    private String module;
    private String chain;
    private Integer authLevel;
    private Object credentials;
    private AuthenticationType authenticationType = AuthenticationType.storedToken;

    private Collection<GrantedAuthority> authorities = AuthorityUtils.NO_AUTHORITIES;

    /**
     * Токен, который подтверджает аутентификацию в IdP
     */
    private String idpSystemToken;
    /**
     * Дата окончания времени жизни токена в IdP
     */
    private Date idpSystemTokenExpiration;
    /**
     * IdP, через который аутентифицировались в процессе аутентификации
     */
    private String idpSystem;

    /**
     * Дополнительные атрибуты токена
     */
    private Map<String, Object> attributes = new HashMap<String, Object>();

    /**
     * Авторизовавшаяся система
     */
    private String clientSystem;

    /**
     * Данные об авторизации пользователя
     */
    private Authentication userAuthentication;

    /**
     * Данные об авторизации системы
     */
    private Authentication systemAuthentication;

    /**
     * Realm
     */
    private String realm;

    /**
     * Impersonator
     */
    private String impersonator;

    /**
     * Создать новый анонимный state
     */
    public AuthenticationState() {
        super(AuthorityUtils.NO_AUTHORITIES);
    }

    public AuthenticationState(Collection<? extends GrantedAuthority> authority) {
        super(authority);
        this.authorities = new ArrayList<GrantedAuthority>(authority);
    }

    public AuthenticationState(GrantedAuthority... authority) {
        super(Arrays.asList(authority));
        this.authorities = Arrays.asList(authority);
    }

    public AuthenticationState(Authentication userAuthentication) {
        super(AuthorityUtils.NO_AUTHORITIES);
        setUserAuthentication(userAuthentication);
    }

    public Set<String> getAuthoritySet() {
        return AuthorityUtils.authorityListToSet(getAuthorities());
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void clearSystemAuthentication() {
        this.clientSystem = null;
        this.systemAuthentication = null;
    }

    public void setSystemAuthentication(Authentication systemAuthentication) {
        if (systemAuthentication == null) {
            clearSystemAuthentication();
            return;
        }
        this.systemAuthentication = systemAuthentication;
        if (systemAuthentication.getPrincipal() != null) {
            this.clientSystem = systemAuthentication.getPrincipal().toString();
        } else {
            this.clientSystem = null;
        }
    }

    public void clearUserAuthentication() {
        userAuthentication = null;
        systemAuthentication = null;
        setAuthenticated(false);
        setPrincipal(null);
        setCredentials(null);
        setDetails(null);
        setAuthorities(AuthorityUtils.NO_AUTHORITIES);
    }

    public void setUserAuthentication(Authentication userAuthentication) {
        this.userAuthentication = userAuthentication;
        setAuthenticated(userAuthentication.isAuthenticated());
        setPrincipal((userAuthentication.getPrincipal() != null) ? userAuthentication.getPrincipal().toString() : null);
        setCredentials(userAuthentication.getCredentials());
        setDetails(userAuthentication.getDetails());
        setAuthorities(userAuthentication.getAuthorities());
        if (userAuthentication instanceof AuthenticationState) {
            AuthenticationState userAuthenticationState = (AuthenticationState) userAuthentication;
            setAuthLevel(userAuthenticationState.getAuthLevel());
            setAttributes(userAuthenticationState.getAttributes());
            setChain(userAuthenticationState.getChain());
            setClientSystem(userAuthenticationState.getClientSystem());
            setIdpSystem(userAuthenticationState.getIdpSystem());
            setIdpSystemToken(userAuthenticationState.getIdpSystemToken());
            setIdpSystemTokenExpiration(userAuthenticationState.getIdpSystemTokenExpiration());
            setModule(userAuthenticationState.getModule());
            setRealm(userAuthenticationState.getRealm());
            setAuthenticationType(userAuthenticationState.getAuthenticationType());
            setImpersonator(userAuthenticationState.getImpersonator());
        }
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        if (this.authorities == AuthorityUtils.NO_AUTHORITIES) {
            this.authorities = new ArrayList<GrantedAuthority>();
        }
        this.authorities.addAll(authorities);
    }

    public boolean isUserDev() {
        return isDev(getUserAuthentication());
    }

    protected static boolean isDev(Authentication auth) {
        return auth instanceof AuthenticationState
                && ((AuthenticationState) auth).isUserDev();
    }

    public boolean isImpersonated() {
        String impersonator = getImpersonator();
        return impersonator != null && !impersonator.isEmpty();
    }
}
