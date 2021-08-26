// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.nio;

import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.channels.ReadableByteChannel;
import org.apache.poi.util.IOUtils;
import java.nio.ByteBuffer;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileBackedDataSource extends DataSource
{
    private FileChannel channel;
    private boolean writable;
    private RandomAccessFile srcFile;
    
    public FileBackedDataSource(final File file) throws FileNotFoundException {
        this(newSrcFile(file, "r"), true);
    }
    
    public FileBackedDataSource(final File file, final boolean readOnly) throws FileNotFoundException {
        this(newSrcFile(file, readOnly ? "r" : "rw"), readOnly);
    }
    
    public FileBackedDataSource(final RandomAccessFile srcFile, final boolean readOnly) {
        this(srcFile.getChannel(), readOnly);
        this.srcFile = srcFile;
    }
    
    public FileBackedDataSource(final FileChannel channel, final boolean readOnly) {
        this.channel = channel;
        this.writable = !readOnly;
    }
    
    public boolean isWriteable() {
        return this.writable;
    }
    
    public FileChannel getChannel() {
        return this.channel;
    }
    
    @Override
    public ByteBuffer read(final int length, final long position) throws IOException {
        if (position >= this.size()) {
            throw new IllegalArgumentException("Position " + position + " past the end of the file");
        }
        int worked = -1;
        ByteBuffer dst;
        if (this.writable) {
            dst = this.channel.map(FileChannel.MapMode.READ_WRITE, position, length);
            worked = 0;
        }
        else {
            this.channel.position(position);
            dst = ByteBuffer.allocate(length);
            worked = IOUtils.readFully(this.channel, dst);
        }
        if (worked == -1) {
            throw new IllegalArgumentException("Position " + position + " past the end of the file");
        }
        dst.position(0);
        return dst;
    }
    
    @Override
    public void write(final ByteBuffer src, final long position) throws IOException {
        this.channel.write(src, position);
    }
    
    @Override
    public void copyTo(final OutputStream stream) throws IOException {
        final WritableByteChannel out = Channels.newChannel(stream);
        this.channel.transferTo(0L, this.channel.size(), out);
    }
    
    @Override
    public long size() throws IOException {
        return this.channel.size();
    }
    
    @Override
    public void close() throws IOException {
        if (this.srcFile != null) {
            this.srcFile.close();
        }
        else {
            this.channel.close();
        }
    }
    
    private static RandomAccessFile newSrcFile(final File file, final String mode) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException(file.toString());
        }
        return new RandomAccessFile(file, mode);
    }
}
