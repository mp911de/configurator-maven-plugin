/*
 * The MIT License (MIT)
 * Copyright (c) 2013 Mark Paluch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished
 * to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package biz.paluch.maven.configurator;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author <a href="mailto:mpaluch@paluch.biz">Mark Paluch</a>
 */
public class PropertyReplacingInputStream extends InputStream {


    public static int MAX_SCAN_LENGTH = 512;


    private InputStream parent;

    private Properties properties;
    private String tokenStart;
    private String tokenEnd;

    private byte[] startBytes;
    private byte[] endBytes;

    private Queue<Integer> buffer = new ArrayDeque<Integer>(MAX_SCAN_LENGTH);


    public PropertyReplacingInputStream(InputStream parent, Properties properties, String tokenStart, String tokenEnd) {
        this.parent = parent;
        this.properties = properties;
        this.tokenStart = tokenStart;
        this.tokenEnd = tokenEnd;

        startBytes = tokenStart.getBytes();
        endBytes = tokenEnd.getBytes();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {

        if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return 0;
        }

        int c = read();
        if (c == -1) {
            return -1;
        }
        b[off] = (byte) c;

        int i = 1;
        for (; i < len; i++) {
            c = read();
            if (c == -1) {
                break;
            }
            b[off + i] = (byte) c;
        }
        return i;
    }

    @Override
    public int read() throws IOException {

        if (!buffer.isEmpty()) {
            return buffer.poll();
        }

        int theByte = parent.read();
        int scanPosition = 0;
        boolean repeat = false;

        if (theByte == startBytes[0]) {
            buffer.add(theByte);
            scanPosition++;
            boolean startMatched = false;


            while (parent.available() > 0 && !bytesStartWith(buffer, startBytes)) {
                if (scanPosition > MAX_SCAN_LENGTH) {
                    return buffer.poll();
                }
                scanPosition++;
                buffer.add(parent.read());
            }

            if (bytesStartWith(buffer, startBytes)) {
                startMatched = true;
            } else {
                return buffer.poll();
            }

            if (startMatched) {

                while (parent.available() > 0 && !bytesEndWith(buffer, endBytes)) {
                    if (scanPosition > MAX_SCAN_LENGTH) {
                        return buffer.poll();
                    }

                    scanPosition++;
                    theByte = parent.read();
                    buffer.add(theByte);
                }

                if (bytesEndWith(buffer, endBytes)) {
                    byte[] theBytes = stripStartEnd(startBytes, endBytes, buffer);

                    String key = new String(theBytes);
                    if (properties.containsKey(key)) {
                        buffer.clear();
                        buffer.addAll(toIntegerList(properties.getProperty(key)));
                        return read();
                    } else {
                        throw new IOException("Cannot resolve key " + tokenStart + key + tokenEnd + " to a value.");
                    }
                }
            }
        }


        return theByte;
    }

    private Collection<? extends Integer> toIntegerList(String property) {
        List<Integer> result = new ArrayList<Integer>(property.length());
        byte[] bytes = property.getBytes();
        for (byte theByte : bytes) {
            result.add(Byte.valueOf(theByte).intValue());
        }

        return result;

    }

    private byte[] stripStartEnd(byte[] startBytes, byte[] endBytes, Queue<Integer> buffer) {

        byte[] result = new byte[buffer.size() - startBytes.length - endBytes.length];
        Integer array[] = buffer.toArray(new Integer[buffer.size()]);
        for (int i = startBytes.length; i < buffer.size() - endBytes.length; i++) {
            result[i - startBytes.length] = array[i].byteValue();
        }
        return result;
    }

    private boolean bytesStartWith(Queue<Integer> buffer, byte[] startBytes) {

        if (buffer.size() >= startBytes.length) {
            boolean match;
            int position = 0;
            Integer array[] = buffer.toArray(new Integer[buffer.size()]);
            do {
                int bufferByte = array[position];
                match = bufferByte == startBytes[position];
            } while (match && (++position < startBytes.length));
            return match;
        }

        return false;
    }

    private boolean bytesEndWith(Queue<Integer> buffer, byte[] endBytes) {

        if (buffer.size() > endBytes.length) {
            boolean match;
            int position = 0;
            Integer array[] = buffer.toArray(new Integer[buffer.size()]);
            do {
                int bufferByte = array[array.length - endBytes.length + position];
                match = bufferByte == endBytes[position];
            } while (match && (++position < endBytes.length));
            return match;
        }

        return false;
    }

    @Override
    public int available() throws IOException {

        if (!buffer.isEmpty()) {
            return buffer.size();
        }

        return parent.available();
    }
}
