package de.paluch.maven.configurator.model;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 11:41
 */
public class CompressedContainer extends Container {
    private PackagingType packagingType;


    public CompressedContainer(String name) {
        super(name);
    }

    public PackagingType getPackagingType() {
        return packagingType;
    }

    public void setPackagingType(PackagingType packagingType) {
        this.packagingType = packagingType;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer();
        sb.append(getClass().getSimpleName());
        sb.append(" [name=").append(getName());
        sb.append(", packagingType=").append(packagingType);
        sb.append(']');
        return sb.toString();
    }
}
