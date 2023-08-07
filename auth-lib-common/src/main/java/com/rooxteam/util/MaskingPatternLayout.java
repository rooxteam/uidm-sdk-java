package com.rooxteam.util;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class MaskingPatternLayout extends PatternLayout {

    private final List<Pattern> maskPatterns = new ArrayList<>();

    public void addMaskPattern(String maskPattern) {
        maskPatterns.add(Pattern.compile(maskPattern));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessage(super.doLayout(event));
    }

    String maskMessage(String message) {
        StringBuilder sb = new StringBuilder(message);
        for (Pattern pattern : maskPatterns) {
            maskDataWithAsterisks(sb, pattern.matcher(message));
        }
        return sb.toString();
    }

    private void maskDataWithAsterisks(StringBuilder message, Matcher matcher) {
        AtomicInteger lastCheckedIndex = new AtomicInteger(0);
        while (matcher.find(lastCheckedIndex.get())) {
            IntStream.rangeClosed(1, matcher.groupCount()).forEach(group -> {
                if (matcher.group(group) != null) {
                    IntStream.range(matcher.start(group), matcher.end(group)).forEach(i -> message.setCharAt(i, '*'));
                    lastCheckedIndex.set(matcher.end());
                    matcher.reset(message);
                }
            });
        }
    }
}
