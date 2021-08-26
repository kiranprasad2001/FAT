// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.eventfilesystem;

import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.POIFSDocument;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import org.apache.poi.poifs.storage.SmallBlockTableReader;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.HeaderBlock;
import java.io.InputStream;

public class POIFSReader
{
    private POIFSReaderRegistry registry;
    private boolean registryClosed;
    
    public POIFSReader() {
        this.registry = new POIFSReaderRegistry();
        this.registryClosed = false;
    }
    
    public void read(final InputStream stream) throws IOException {
        this.registryClosed = true;
        final HeaderBlock header_block = new HeaderBlock(stream);
        final RawDataBlockList data_blocks = new RawDataBlockList(stream, header_block.getBigBlockSize());
        new BlockAllocationTableReader(header_block.getBigBlockSize(), header_block.getBATCount(), header_block.getBATArray(), header_block.getXBATCount(), header_block.getXBATIndex(), data_blocks);
        final PropertyTable properties = new PropertyTable(header_block, data_blocks);
        this.processProperties(SmallBlockTableReader.getSmallDocumentBlocks(header_block.getBigBlockSize(), data_blocks, properties.getRoot(), header_block.getSBATStart()), data_blocks, properties.getRoot().getChildren(), new POIFSDocumentPath());
    }
    
    public void registerListener(final POIFSReaderListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        if (this.registryClosed) {
            throw new IllegalStateException();
        }
        this.registry.registerListener(listener);
    }
    
    public void registerListener(final POIFSReaderListener listener, final String name) {
        this.registerListener(listener, null, name);
    }
    
    public void registerListener(final POIFSReaderListener listener, final POIFSDocumentPath path, final String name) {
        if (listener == null || name == null || name.length() == 0) {
            throw new NullPointerException();
        }
        if (this.registryClosed) {
            throw new IllegalStateException();
        }
        this.registry.registerListener(listener, (path == null) ? new POIFSDocumentPath() : path, name);
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("at least one argument required: input filename(s)");
            System.exit(1);
        }
        for (int j = 0; j < args.length; ++j) {
            final POIFSReader reader = new POIFSReader();
            final POIFSReaderListener listener = new SampleListener();
            reader.registerListener(listener);
            System.out.println("reading " + args[j]);
            final FileInputStream istream = new FileInputStream(args[j]);
            reader.read(istream);
            istream.close();
        }
    }
    
    private void processProperties(final BlockList small_blocks, final BlockList big_blocks, final Iterator properties, final POIFSDocumentPath path) throws IOException {
        while (properties.hasNext()) {
            final Property property = properties.next();
            final String name = property.getName();
            if (property.isDirectory()) {
                final POIFSDocumentPath new_path = new POIFSDocumentPath(path, new String[] { name });
                this.processProperties(small_blocks, big_blocks, ((DirectoryProperty)property).getChildren(), new_path);
            }
            else {
                final int startBlock = property.getStartBlock();
                final Iterator listeners = this.registry.getListeners(path, name);
                if (listeners.hasNext()) {
                    final int size = property.getSize();
                    POIFSDocument document = null;
                    if (property.shouldUseSmallBlocks()) {
                        document = new POIFSDocument(name, small_blocks.fetchBlocks(startBlock, -1), size);
                    }
                    else {
                        document = new POIFSDocument(name, big_blocks.fetchBlocks(startBlock, -1), size);
                    }
                    while (listeners.hasNext()) {
                        final POIFSReaderListener listener = listeners.next();
                        listener.processPOIFSReaderEvent(new POIFSReaderEvent(new DocumentInputStream(document), path, name));
                    }
                }
                else if (property.shouldUseSmallBlocks()) {
                    small_blocks.fetchBlocks(startBlock, -1);
                }
                else {
                    big_blocks.fetchBlocks(startBlock, -1);
                }
            }
        }
    }
    
    private static class SampleListener implements POIFSReaderListener
    {
        SampleListener() {
        }
        
        @Override
        public void processPOIFSReaderEvent(final POIFSReaderEvent event) {
            final DocumentInputStream istream = event.getStream();
            final POIFSDocumentPath path = event.getPath();
            final String name = event.getName();
            try {
                final byte[] data = new byte[istream.available()];
                istream.read(data);
                for (int pathLength = path.length(), k = 0; k < pathLength; ++k) {
                    System.out.print("/" + path.getComponent(k));
                }
                System.out.println("/" + name + ": " + data.length + " bytes read");
            }
            catch (IOException ex) {}
        }
    }
}
