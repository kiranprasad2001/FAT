// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.POILogFactory;
import java.util.Collections;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.DirectoryProperty;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import org.apache.poi.poifs.storage.BATBlock;
import java.util.Iterator;
import org.apache.poi.poifs.storage.BlockWritable;
import org.apache.poi.poifs.storage.HeaderBlockWriter;
import java.util.Collection;
import org.apache.poi.poifs.storage.BlockAllocationTableWriter;
import org.apache.poi.poifs.storage.SmallBlockTableWriter;
import java.io.OutputStream;
import org.apache.poi.util.LongField;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.poi.poifs.storage.SmallBlockTableReader;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.RawDataBlockList;
import java.util.ArrayList;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.util.CloseIgnoringInputStream;
import java.io.InputStream;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import java.util.List;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.util.POILogger;
import org.apache.poi.poifs.dev.POIFSViewable;

public class POIFSFileSystem implements POIFSViewable
{
    private static final POILogger _logger;
    private PropertyTable _property_table;
    private List<POIFSViewable> _documents;
    private DirectoryNode _root;
    private POIFSBigBlockSize bigBlockSize;
    
    public static InputStream createNonClosingInputStream(final InputStream is) {
        return new CloseIgnoringInputStream(is);
    }
    
    public POIFSFileSystem() {
        this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        final HeaderBlock header_block = new HeaderBlock(this.bigBlockSize);
        this._property_table = new PropertyTable(header_block);
        this._documents = new ArrayList<POIFSViewable>();
        this._root = null;
    }
    
    public POIFSFileSystem(final InputStream stream) throws IOException {
        this();
        boolean success = false;
        HeaderBlock header_block;
        RawDataBlockList data_blocks;
        try {
            header_block = new HeaderBlock(stream);
            this.bigBlockSize = header_block.getBigBlockSize();
            data_blocks = new RawDataBlockList(stream, this.bigBlockSize);
            success = true;
        }
        finally {
            this.closeInputStream(stream, success);
        }
        new BlockAllocationTableReader(header_block.getBigBlockSize(), header_block.getBATCount(), header_block.getBATArray(), header_block.getXBATCount(), header_block.getXBATIndex(), data_blocks);
        final PropertyTable properties = new PropertyTable(header_block, data_blocks);
        this.processProperties(SmallBlockTableReader.getSmallDocumentBlocks(this.bigBlockSize, data_blocks, properties.getRoot(), header_block.getSBATStart()), data_blocks, properties.getRoot().getChildren(), null, header_block.getPropertyStart());
        this.getRoot().setStorageClsid(properties.getRoot().getStorageClsid());
    }
    
    private void closeInputStream(final InputStream stream, final boolean success) {
        if (stream.markSupported() && !(stream instanceof ByteArrayInputStream)) {
            final String msg = "POIFS is closing the supplied input stream of type (" + stream.getClass().getName() + ") which supports mark/reset.  " + "This will be a problem for the caller if the stream will still be used.  " + "If that is the case the caller should wrap the input stream to avoid this close logic.  " + "This warning is only temporary and will not be present in future versions of POI.";
            POIFSFileSystem._logger.log(5, msg);
        }
        try {
            stream.close();
        }
        catch (IOException e) {
            if (success) {
                throw new RuntimeException(e);
            }
            e.printStackTrace();
        }
    }
    
    public static boolean hasPOIFSHeader(final InputStream inp) throws IOException {
        final byte[] header = IOUtils.peekFirst8Bytes(inp);
        return hasPOIFSHeader(header);
    }
    
    public static boolean hasPOIFSHeader(final byte[] header8Bytes) {
        final LongField signature = new LongField(0, header8Bytes);
        return signature.get() == -2226271756974174256L;
    }
    
    public DocumentEntry createDocument(final InputStream stream, final String name) throws IOException {
        return this.getRoot().createDocument(name, stream);
    }
    
    public DocumentEntry createDocument(final String name, final int size, final POIFSWriterListener writer) throws IOException {
        return this.getRoot().createDocument(name, size, writer);
    }
    
    public DirectoryEntry createDirectory(final String name) throws IOException {
        return this.getRoot().createDirectory(name);
    }
    
    public void writeFilesystem(final OutputStream stream) throws IOException {
        this._property_table.preWrite();
        final SmallBlockTableWriter sbtw = new SmallBlockTableWriter(this.bigBlockSize, this._documents, this._property_table.getRoot());
        final BlockAllocationTableWriter bat = new BlockAllocationTableWriter(this.bigBlockSize);
        final List<Object> bm_objects = new ArrayList<Object>();
        bm_objects.addAll(this._documents);
        bm_objects.add(this._property_table);
        bm_objects.add(sbtw);
        bm_objects.add(sbtw.getSBAT());
        for (final BATManaged bmo : bm_objects) {
            final int block_count = bmo.countBlocks();
            if (block_count != 0) {
                bmo.setStartBlock(bat.allocateSpace(block_count));
            }
        }
        final int batStartBlock = bat.createBlocks();
        final HeaderBlockWriter header_block_writer = new HeaderBlockWriter(this.bigBlockSize);
        final BATBlock[] xbat_blocks = header_block_writer.setBATBlocks(bat.countBlocks(), batStartBlock);
        header_block_writer.setPropertyStart(this._property_table.getStartBlock());
        header_block_writer.setSBATStart(sbtw.getSBAT().getStartBlock());
        header_block_writer.setSBATBlockCount(sbtw.getSBATBlockCount());
        final List<Object> writers = new ArrayList<Object>();
        writers.add(header_block_writer);
        writers.addAll(this._documents);
        writers.add(this._property_table);
        writers.add(sbtw);
        writers.add(sbtw.getSBAT());
        writers.add(bat);
        for (int j = 0; j < xbat_blocks.length; ++j) {
            writers.add(xbat_blocks[j]);
        }
        for (final BlockWritable writer : writers) {
            writer.writeBlocks(stream);
        }
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("two arguments required: input filename and output filename");
            System.exit(1);
        }
        final FileInputStream istream = new FileInputStream(args[0]);
        final FileOutputStream ostream = new FileOutputStream(args[1]);
        new POIFSFileSystem(istream).writeFilesystem(ostream);
        istream.close();
        ostream.close();
    }
    
    public DirectoryNode getRoot() {
        if (this._root == null) {
            this._root = new DirectoryNode(this._property_table.getRoot(), this, null);
        }
        return this._root;
    }
    
    public DocumentInputStream createDocumentInputStream(final String documentName) throws IOException {
        return this.getRoot().createDocumentInputStream(documentName);
    }
    
    void addDocument(final POIFSDocument document) {
        this._documents.add(document);
        this._property_table.addProperty(document.getDocumentProperty());
    }
    
    void addDirectory(final DirectoryProperty directory) {
        this._property_table.addProperty(directory);
    }
    
    void remove(final EntryNode entry) {
        this._property_table.removeProperty(entry.getProperty());
        if (entry.isDocumentEntry()) {
            this._documents.remove(((DocumentNode)entry).getDocument());
        }
    }
    
    private void processProperties(final BlockList small_blocks, final BlockList big_blocks, final Iterator<Property> properties, final DirectoryNode dir, final int headerPropertiesStartAt) throws IOException {
        while (properties.hasNext()) {
            final Property property = properties.next();
            final String name = property.getName();
            final DirectoryNode parent = (dir == null) ? this.getRoot() : dir;
            if (property.isDirectory()) {
                final DirectoryNode new_dir = (DirectoryNode)parent.createDirectory(name);
                new_dir.setStorageClsid(property.getStorageClsid());
                this.processProperties(small_blocks, big_blocks, ((DirectoryProperty)property).getChildren(), new_dir, headerPropertiesStartAt);
            }
            else {
                final int startBlock = property.getStartBlock();
                final int size = property.getSize();
                POIFSDocument document = null;
                if (property.shouldUseSmallBlocks()) {
                    document = new POIFSDocument(name, small_blocks.fetchBlocks(startBlock, headerPropertiesStartAt), size);
                }
                else {
                    document = new POIFSDocument(name, big_blocks.fetchBlocks(startBlock, headerPropertiesStartAt), size);
                }
                parent.createDocument(document);
            }
        }
    }
    
    @Override
    public Object[] getViewableArray() {
        if (this.preferArray()) {
            return this.getRoot().getViewableArray();
        }
        return new Object[0];
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        if (!this.preferArray()) {
            return this.getRoot().getViewableIterator();
        }
        return Collections.emptyList().iterator();
    }
    
    @Override
    public boolean preferArray() {
        return this.getRoot().preferArray();
    }
    
    @Override
    public String getShortDescription() {
        return "POIFS FileSystem";
    }
    
    public int getBigBlockSize() {
        return this.bigBlockSize.getBigBlockSize();
    }
    
    public POIFSBigBlockSize getBigBlockSizeDetails() {
        return this.bigBlockSize;
    }
    
    static {
        _logger = POILogFactory.getLogger(POIFSFileSystem.class);
    }
}
