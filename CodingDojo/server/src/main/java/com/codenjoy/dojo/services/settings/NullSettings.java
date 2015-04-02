package com.codenjoy.dojo.services.settings;

import java.util.LinkedList;
import java.util.List;

/**
* User: sanja
* Date: 21.11.13
* Time: 13:33
*/
public class NullSettings implements Settings {

    public static final Settings INSTANCE = new NullSettings();

    private NullSettings() {
        // do nothing
    }

    @Override
    public List<Parameter<?>> getParameters() {
        return new LinkedList<Parameter<?>>();
    }

    @Override
    public Parameter<?> addEditBox(String name) {
        return null;
    }

    @Override
    public Parameter<?> addSelect(String name, List<Object> strings) {
        return null;
    }

    @Override
    public Parameter<Boolean> addCheckBox(String name) {
        return null;
    }

    @Override
    public Parameter<?> getParameter(String name) {
        return null;
    }
}
