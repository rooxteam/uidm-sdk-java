package com.rooxteam.sso.aal.realms.impl;

import com.rooxteam.sso.aal.configuration.Configuration;
import com.rooxteam.sso.aal.utils.StringUtils;
import com.rooxteam.util.RealmFormatValidator;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.function.Supplier;

import static com.rooxteam.sso.aal.configuration.AalConfigLogger.LOG;


/**
 * @author sergey.syroezhkin
 * @since 04.08.2025
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RealmBasedConfiguration implements Configuration {
    private static final String AAL_CONFIG_PREFIX = "com.rooxteam.aal";

    private final Configuration delegate;
    private final Supplier<String> prefixSupplier;

    public static Configuration of(Configuration delegate, Supplier<String> realmSupplier) {
        return new RealmBasedConfiguration(delegate, () -> {
            String realm = realmSupplier.get();
            validateRealmFormat(realm);
            return StringUtils.isEmpty(realm)
                    ? AAL_CONFIG_PREFIX
                    : AAL_CONFIG_PREFIX.concat(".").concat(realm);
        });
    }

    String processPropertyKey(String property) {
        if (property != null && property.startsWith(AAL_CONFIG_PREFIX)) {
            String realmBasedPrefix = prefixSupplier.get();
            String newProperty = realmBasedPrefix.concat(property.substring(AAL_CONFIG_PREFIX.length()));
            LOG.debugPropertyReplaced(property, newProperty);
            return newProperty;
        }
        LOG.debugPropertyNoPrefix(property);
        return null;
    }

    @Override
    public String getString(String property, String defaultValue) {
        String realmProperty = processPropertyKey(property);
        if (realmProperty != null) {
            String value = delegate.getString(realmProperty, null);
            if (value != null) {
                LOG.debugReturnReplacedPropertyValue(realmProperty);
                return value;
            } else {
                LOG.debugReplacedPropertyValueAbsent(property);
            }
        }
        LOG.debugReturnOriginalPropertyValue(property);
        return delegate.getString(property, defaultValue);
    }

    @Override
    public String getString(String property) {
        return getString(property, null);
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        String realmProperty = processPropertyKey(property);
        if (realmProperty != null) {
            String value = delegate.getString(realmProperty, null);
            if (value != null) {
                LOG.debugReturnReplacedPropertyValue(realmProperty);
                return delegate.getBoolean(realmProperty, defaultValue);
            } else {
                LOG.debugReplacedPropertyValueAbsent(property);
            }
        }
        LOG.debugReturnOriginalPropertyValue(property);
        return delegate.getBoolean(property, defaultValue);
    }

    @Override
    public int getInt(String property, int defaultValue) {
        String realmProperty = processPropertyKey(property);
        if (realmProperty != null) {
            String value = delegate.getString(realmProperty, null);
            if (value != null) {
                LOG.debugReturnReplacedPropertyValue(realmProperty);
                return delegate.getInt(realmProperty, defaultValue);
            } else {
                LOG.debugReplacedPropertyValueAbsent(property);
            }
        }
        LOG.debugReturnOriginalPropertyValue(property);
        return delegate.getInt(property, defaultValue);
    }

    @Override
    public List getList(String property) {
        String realmProperty = processPropertyKey(property);
        if (realmProperty != null) {
            String value = delegate.getString(realmProperty, null);
            if (value != null) {
                LOG.debugReturnReplacedPropertyValue(realmProperty);
                return delegate.getList(realmProperty);
            } else {
                LOG.debugReplacedPropertyValueAbsent(property);
            }
        }
        LOG.debugReturnOriginalPropertyValue(property);
        return delegate.getList(property);
    }

    @Override
    public String[] getStringArray(String property) {
        String realmProperty = processPropertyKey(property);
        if (realmProperty != null) {
            String value = delegate.getString(realmProperty, null);
            if (value != null) {
                LOG.debugReturnReplacedPropertyValue(realmProperty);
                return delegate.getStringArray(realmProperty);
            } else {
                LOG.debugReplacedPropertyValueAbsent(property);
            }
        }
        LOG.debugReturnOriginalPropertyValue(property);
        return delegate.getStringArray(property);
    }

    private static void validateRealmFormat(String realm) {
        if (!RealmFormatValidator.isRealmFormatValid(realm)) {
            LOG.errorRealmConfigFormat(realm);
            throw new IllegalArgumentException();
        }
    }
}
