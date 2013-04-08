package de.paluch.maven.configurator.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:40
 */
public class Container extends Entry {

    private List<Entry> entries = new ArrayList<Entry>();

    public Container(String name) {
        super(name);
    }

    public List<Entry> getEntries() {
        return entries;
    }
}
