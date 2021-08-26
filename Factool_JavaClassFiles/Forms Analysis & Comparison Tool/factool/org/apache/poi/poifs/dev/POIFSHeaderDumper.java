// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.dev;

import java.lang.reflect.Field;
import org.apache.poi.util.IntList;
import java.lang.reflect.Method;
import org.apache.poi.util.HexDump;
import org.apache.poi.poifs.storage.ListManagedBlock;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import java.io.InputStream;
import org.apache.poi.poifs.storage.SmallBlockTableReader;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.poifs.storage.BlockList;
import org.apache.poi.poifs.storage.BlockAllocationTableReader;
import org.apache.poi.poifs.storage.RawDataBlockList;
import org.apache.poi.poifs.storage.HeaderBlock;
import java.io.FileInputStream;

public class POIFSHeaderDumper
{
    public static void main(final String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Must specify at least one file to view");
            System.exit(1);
        }
        for (int j = 0; j < args.length; ++j) {
            viewFile(args[j]);
        }
    }
    
    public static void viewFile(final String filename) throws Exception {
        final InputStream inp = new FileInputStream(filename);
        final HeaderBlock header_block = new HeaderBlock(inp);
        displayHeader(header_block);
        final POIFSBigBlockSize bigBlockSize = header_block.getBigBlockSize();
        final RawDataBlockList data_blocks = new RawDataBlockList(inp, bigBlockSize);
        displayRawBlocksSummary(data_blocks);
        final BlockAllocationTableReader batReader = new BlockAllocationTableReader(header_block.getBigBlockSize(), header_block.getBATCount(), header_block.getBATArray(), header_block.getXBATCount(), header_block.getXBATIndex(), data_blocks);
        displayBATReader(batReader);
        final PropertyTable properties = new PropertyTable(header_block, data_blocks);
        final BlockList sbat = SmallBlockTableReader.getSmallDocumentBlocks(bigBlockSize, data_blocks, properties.getRoot(), header_block.getSBATStart());
    }
    
    public static void displayHeader(final HeaderBlock header_block) throws Exception {
        System.out.println("Header Details:");
        System.out.println(" Block size: " + header_block.getBigBlockSize().getBigBlockSize());
        System.out.println(" BAT (FAT) header blocks: " + header_block.getBATArray().length);
        System.out.println(" BAT (FAT) block count: " + header_block.getBATCount());
        System.out.println(" XBAT (FAT) block count: " + header_block.getXBATCount());
        System.out.println(" XBAT (FAT) block 1 at: " + header_block.getXBATIndex());
        System.out.println(" SBAT (MiniFAT) block count: " + header_block.getSBATCount());
        System.out.println(" SBAT (MiniFAT) block 1 at: " + header_block.getSBATStart());
        System.out.println(" Property table at: " + header_block.getPropertyStart());
        System.out.println("");
    }
    
    public static void displayRawBlocksSummary(final RawDataBlockList data_blocks) throws Exception {
        System.out.println("Raw Blocks Details:");
        System.out.println(" Number of blocks: " + data_blocks.blockCount());
        final Method gbm = data_blocks.getClass().getSuperclass().getDeclaredMethod("get", Integer.TYPE);
        gbm.setAccessible(true);
        for (int i = 0; i < Math.min(16, data_blocks.blockCount()); ++i) {
            final ListManagedBlock block = (ListManagedBlock)gbm.invoke(data_blocks, i);
            final byte[] data = new byte[Math.min(48, block.getData().length)];
            System.arraycopy(block.getData(), 0, data, 0, data.length);
            System.out.println(" Block #" + i + ":");
            System.out.println(HexDump.dump(data, 0L, 0));
        }
        System.out.println("");
    }
    
    public static void displayBATReader(final BlockAllocationTableReader batReader) throws Exception {
        System.out.println("Sectors, as referenced from the FAT:");
        final Field entriesF = batReader.getClass().getDeclaredField("_entries");
        entriesF.setAccessible(true);
        final IntList entries = (IntList)entriesF.get(batReader);
        for (int i = 0; i < entries.size(); ++i) {
            final int bn = entries.get(i);
            String bnS = Integer.toString(bn);
            if (bn == -2) {
                bnS = "End Of Chain";
            }
            else if (bn == -4) {
                bnS = "DI Fat Block";
            }
            else if (bn == -3) {
                bnS = "Normal Fat Block";
            }
            else if (bn == -1) {
                bnS = "Block Not Used (Free)";
            }
            System.out.println("  Block  # " + i + " -> " + bnS);
        }
        System.out.println("");
    }
}
