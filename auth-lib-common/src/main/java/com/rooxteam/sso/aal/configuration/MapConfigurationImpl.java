package com.rooxteam.sso.aal.configuration;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
final class MapConfigurationImpl implements Configuration {

    private final Map<String, Object> configuration;

    MapConfigurationImpl(Map<String, Object> configuration) {
        this.configuration = configuration;
    }

    @Override
    public String getString(String property, String defaultValue) {
        Object value = configuration.get(property);
        if (value == null) {
            return defaultValue;
        }
        return value.toString();
    }

    @Override
    public String getString(String property) {
        Object value = configuration.get(property);
        if (value == null) return null;
        return value.toString();
    }

    @Override
    public boolean getBoolean(String property, boolean defaultValue) {
        Object value = configuration.get(property);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(String.valueOf(value));
    }

    @Override
    public int getInt(String property, int defaultValue) {
        Object value = configuration.get(property);
        if (value == null) {
            return defaultValue;
        }
        if (value instanceof Byte) {
            return (Byte) value;
        }
        if (value instanceof Short) {
            return (Short) value;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        if (value instanceof Long) {
            return (int)(long)((Long) value);
        }
        if (value instanceof BigInteger) {
            return ((BigInteger)value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    @Override
    public List getList(String property) {
        return (List) configuration.get(property);
    }

    @Override
    public String[] getStringArray(String property) {
        return (String[]) configuration.get(property);
    }
}
