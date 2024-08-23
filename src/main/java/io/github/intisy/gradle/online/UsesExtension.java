package io.github.intisy.gradle.online;

import java.util.ArrayList;
import java.util.List;

public class UsesExtension {
    private final List<String> messages = new ArrayList<>();
    private ActionCallback callback;

    public List<String> getMessages() {
        return messages;
    }

    public void setMessage(String message) {
        messages.add(message);
        if (callback != null) {
            callback.execute(message);
        }
    }

    public void setCallback(ActionCallback callback) {
        this.callback = callback;
    }

    interface ActionCallback {
        void execute(String message);
    }
}