package de.paluch.maven.configurator.model;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:38
 */
public enum PackagingType {
    EAR("ear"), JAR("jar"), WAR("war"), RAR("rar");

    private String type;

    private PackagingType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
