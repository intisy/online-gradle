package io.github.intisy.gradle.online;

import java.util.List;

/**
 * Represents an extension for configuring various settings related to URL usage, presets, debugging, and auto-updating.
 * @author Finn Birich
 */
@SuppressWarnings("unused")
public class UsesExtension {
    private List<String> actions;
    private List<String> presets;
    private boolean debug;
    private boolean autoUpdate;
    private int updateDelay;

    /**
     * Sets the update delay.
     *
     * @param updateDelay The delay in milliseconds between updates.
     */
    public void setUpdateDelay(int updateDelay) {
        this.updateDelay = updateDelay;
    }

    /**
     * Gets the current update delay.
     *
     * @return The delay in milliseconds between updates.
     */
    public int getUpdateDelay() {
        return updateDelay;
    }

    /**
     * Checks if auto-update is enabled.
     *
     * @return true if auto-update is enabled, false otherwise.
     */
    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    /**
     * Sets the auto-update feature.
     *
     * @param autoUpdate true to enable auto-update, false to disable it.
     */
    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    /**
     * Checks if debug mode is enabled.
     *
     * @return true if debug mode is enabled, false otherwise.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Sets the debug mode.
     *
     * @param debug true to enable debug mode, false to disable it.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Gets the list of URLs.
     *
     * @return A List of Strings containing the URLs.
     */
    public List<String> getActions() {
        return actions;
    }

    /**
     * Sets the list of URLs.
     *
     * @param actions A List of Strings containing the URLs to be set.
     */
    public void setActions(List<String> actions) {
        this.actions = actions;
    }

    /**
     * Gets the list of presets.
     *
     * @return A List of Strings containing the presets.
     */
    public List<String> getPresets() {
        return presets;
    }

    /**
     * Sets the list of presets.
     *
     * @param presets A List of Strings containing the presets to be set.
     */
    public void setPresets(List<String> presets) {
        this.presets = presets;
    }
}