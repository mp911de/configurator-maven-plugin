package de.paluch.maven.configurator;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 * @since 08.04.13 12:43
 */
public class LimitedInputStream extends InputStream {

    private long position = 0;
    private long limit = 0;

    private InputStream parent;

    public LimitedInputStream(long limit, InputStream parent) {
        this.limit = limit;
        this.parent = parent;
    }

    @Override
    public int available() throws IOException {

        long result = limit - position;
        return (int) result;
    }

    @Override
    public int read() throws IOException {
        position++;
        return parent.read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        int read = parent.read(b);
        position += read;
        return read;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int read = parent.read(b, off, len);
        position += read;
        return read;
    }

    @Override
    public long skip(long n) throws IOException {
        long skipped = super.skip(n);
        position += skipped;
        return skipped;
    }

    @Override
    public void close() throws IOException {
    }
}
