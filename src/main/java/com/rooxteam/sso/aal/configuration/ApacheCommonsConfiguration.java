package com.rooxteam.sso.aal.configuration;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * Адаптер конфигурации, принимающий Apache Commons Configuration
 */
@SuppressWarnings("unused")
final class ApacheCommonsConfiguration implements Configuration {

    private org.apache.commons.configuration.Configuration configuration;

    public ApacheCommonsConfiguration(org.apache.commons.configuration.Configuration configuration) {
        this.configuration = configuration;
    }

    public org.apache.commons.configuration.Configuration subset(String prefix) {
        return configuration.subset(prefix);
    }

    public boolean isEmpty() {
        return configuration.isEmpty();
    }

    public boolean containsKey(String key) {
        return configuration.containsKey(key);
    }

    public void addProperty(String key, Object value) {
        configuration.addProperty(key, value);
    }

    public void setProperty(String key, Object value) {
        configuration.setProperty(key, value);
    }

    public void clearProperty(String key) {
        configuration.clearProperty(key);
    }

    public void clear() {
        configuration.clear();
    }

    public Object getProperty(String key) {
        return configuration.getProperty(key);
    }

    public Iterator<String> getKeys(String prefix) {
        return configuration.getKeys(prefix);
    }

    public Iterator<String> getKeys() {
        return configuration.getKeys();
    }

    public Properties getProperties(String key) {
        return configuration.getProperties(key);
    }

    public boolean getBoolean(String key) {
        return configuration.getBoolean(key);
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return configuration.getBoolean(key, defaultValue);
    }

    public Boolean getBoolean(String key, Boolean defaultValue) {
        return configuration.getBoolean(key, defaultValue);
    }

    public byte getByte(String key) {
        return configuration.getByte(key);
    }

    public byte getByte(String key, byte defaultValue) {
        return configuration.getByte(key, defaultValue);
    }

    public Byte getByte(String key, Byte defaultValue) {
        return configuration.getByte(key, defaultValue);
    }

    public double getDouble(String key) {
        return configuration.getDouble(key);
    }

    public double getDouble(String key, double defaultValue) {
        return configuration.getDouble(key, defaultValue);
    }

    public Double getDouble(String key, Double defaultValue) {
        return configuration.getDouble(key, defaultValue);
    }

    public float getFloat(String key) {
        return configuration.getFloat(key);
    }

    public float getFloat(String key, float defaultValue) {
        return configuration.getFloat(key, defaultValue);
    }

    public Float getFloat(String key, Float defaultValue) {
        return configuration.getFloat(key, defaultValue);
    }

    public int getInt(String key) {
        return configuration.getInt(key);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return configuration.getInt(key, defaultValue);
    }

    public Integer getInteger(String key, Integer defaultValue) {
        return configuration.getInteger(key, defaultValue);
    }

    public long getLong(String key) {
        return configuration.getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return configuration.getLong(key, defaultValue);
    }

    public Long getLong(String key, Long defaultValue) {
        return configuration.getLong(key, defaultValue);
    }

    public short getShort(String key) {
        return configuration.getShort(key);
    }

    public short getShort(String key, short defaultValue) {
        return configuration.getShort(key, defaultValue);
    }

    public Short getShort(String key, Short defaultValue) {
        return configuration.getShort(key, defaultValue);
    }

    public BigDecimal getBigDecimal(String key) {
        return configuration.getBigDecimal(key);
    }

    public BigDecimal getBigDecimal(String key, BigDecimal defaultValue) {
        return configuration.getBigDecimal(key, defaultValue);
    }

    public BigInteger getBigInteger(String key) {
        return configuration.getBigInteger(key);
    }

    public BigInteger getBigInteger(String key, BigInteger defaultValue) {
        return configuration.getBigInteger(key, defaultValue);
    }

    @Override
    public String getString(String key) {
        return configuration.getString(key);
    }

    @Override
    public String getString(String key, String defaultValue) {
        return configuration.getString(key, defaultValue);
    }

    public String[] getStringArray(String key) {
        return configuration.getStringArray(key);
    }

    @Override
    public List<Object> getList(String key) {
        return configuration.getList(key);
    }

    public List<Object> getList(String key, List<?> defaultValue) {
        return configuration.getList(key, defaultValue);
    }
}
