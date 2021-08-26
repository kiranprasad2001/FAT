// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.Closeable;
import java.util.zip.Checksum;
import java.util.zip.CRC32;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PushbackInputStream;
import org.apache.poi.EmptyFileException;
import java.io.InputStream;

public final class IOUtils
{
    private static final POILogger logger;
    
    private IOUtils() {
    }
    
    public static byte[] peekFirst8Bytes(final InputStream stream) throws IOException, EmptyFileException {
        stream.mark(8);
        final byte[] header = new byte[8];
        final int read = readFully(stream, header);
        if (read < 1) {
            throw new EmptyFileException();
        }
        if (stream instanceof PushbackInputStream) {
            final PushbackInputStream pin = (PushbackInputStream)stream;
            pin.unread(header);
        }
        else {
            stream.reset();
        }
        return header;
    }
    
    public static byte[] toByteArray(final InputStream stream) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final byte[] buffer = new byte[4096];
        int read = 0;
        while (read != -1) {
            read = stream.read(buffer);
            if (read > 0) {
                baos.write(buffer, 0, read);
            }
        }
        return baos.toByteArray();
    }
    
    public static byte[] toByteArray(final ByteBuffer buffer, final int length) {
        if (buffer.hasArray() && buffer.arrayOffset() == 0) {
            return buffer.array();
        }
        final byte[] data = new byte[length];
        buffer.get(data);
        return data;
    }
    
    public static int readFully(final InputStream in, final byte[] b) throws IOException {
        return readFully(in, b, 0, b.length);
    }
    
    public static int readFully(final InputStream in, final byte[] b, final int off, final int len) throws IOException {
        int total = 0;
        while (true) {
            final int got = in.read(b, off + total, len - total);
            if (got < 0) {
                return (total == 0) ? -1 : total;
            }
            total += got;
            if (total == len) {
                return total;
            }
        }
    }
    
    public static int readFully(final ReadableByteChannel channel, final ByteBuffer b) throws IOException {
        int total = 0;
        while (true) {
            final int got = channel.read(b);
            if (got < 0) {
                return (total == 0) ? -1 : total;
            }
            total += got;
            if (total == b.capacity() || b.position() == b.capacity()) {
                return total;
            }
        }
    }
    
    public static void copy(final InputStream inp, final OutputStream out) throws IOException {
        final byte[] buff = new byte[4096];
        int count;
        while ((count = inp.read(buff)) != -1) {
            if (count > 0) {
                out.write(buff, 0, count);
            }
        }
    }
    
    public static long calculateChecksum(final byte[] data) {
        final Checksum sum = new CRC32();
        sum.update(data, 0, data.length);
        return sum.getValue();
    }
    
    public static void closeQuietly(final Closeable closeable) {
        try {
            closeable.close();
        }
        catch (Exception exc) {
            IOUtils.logger.log(7, "Unable to close resource: " + exc, exc);
        }
    }
    
    static {
        logger = POILogFactory.getLogger(IOUtils.class);
    }
}
