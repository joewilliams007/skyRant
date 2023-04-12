package com.dev.engineerrant.adapters;

public class ChangelogItem {
    private final long timestamp;
    private final String version;
    private final String build;
    private final String text;

    public long getTimestamp() {
        return timestamp;
    }

    public String getVersion() {
        return version;
    }

    public String getBuild() {
        return build;
    }

    public String getText() {
        return text;
    }

    public ChangelogItem(long timestamp, String version, String build, String text) {
        this.text = text;
        this.timestamp = timestamp;
        this.version = version;
        this.build = build;
    }
}
