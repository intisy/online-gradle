package io.github.intisy.gradle.online;

import org.gradle.api.model.ObjectFactory;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class UsesExtension {

    private final List<String> strings;

    @Inject
    public UsesExtension(ObjectFactory objects) {
        this.strings = objects.listProperty(String.class).get();
    }

    public List<String> getStrings() {
        return strings;
    }
}