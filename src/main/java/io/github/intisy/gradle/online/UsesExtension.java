package io.github.intisy.gradle.online;

import java.util.List;

@SuppressWarnings("unused")
public class UsesExtension {
    private List<String> urls;
    private List<String> presets;
    private boolean debug;
    private boolean autoUpdate;
    private int updateDelay;

    public void setUpdateDelay(int updateDelay) {
        this.updateDelay = updateDelay;
    }

    public int getUpdateDelay() {
        return updateDelay;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getPresets() {
        return presets;
    }

    public void setPresets(List<String> presets) {
        this.presets = presets;
    }
}