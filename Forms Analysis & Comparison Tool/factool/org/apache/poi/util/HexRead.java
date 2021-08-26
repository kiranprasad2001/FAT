// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;

public class HexRead
{
    public static byte[] readData(final String filename) throws IOException {
        final File file = new File(filename);
        final FileInputStream stream = new FileInputStream(file);
        try {
            return readData(stream, -1);
        }
        finally {
            stream.close();
        }
    }
    
    public static byte[] readData(final InputStream stream, final String section) throws IOException {
        try {
            StringBuffer sectionText = new StringBuffer();
            boolean inSection = false;
            for (int c = stream.read(); c != -1; c = stream.read()) {
                switch (c) {
                    case 91: {
                        inSection = true;
                        break;
                    }
                    case 10:
                    case 13: {
                        inSection = false;
                        sectionText = new StringBuffer();
                        break;
                    }
                    case 93: {
                        inSection = false;
                        if (sectionText.toString().equals(section)) {
                            return readData(stream, 91);
                        }
                        sectionText = new StringBuffer();
                        break;
                    }
                    default: {
                        if (inSection) {
                            sectionText.append((char)c);
                            break;
                        }
                        break;
                    }
                }
            }
        }
        finally {
            stream.close();
        }
        throw new IOException("Section '" + section + "' not found");
    }
    
    public static byte[] readData(final String filename, final String section) throws IOException {
        final File file = new File(filename);
        final FileInputStream stream = new FileInputStream(file);
        return readData(stream, section);
    }
    
    public static byte[] readData(final InputStream stream, final int eofChar) throws IOException {
        int characterCount = 0;
        byte b = 0;
        final List<Byte> bytes = new ArrayList<Byte>();
        boolean done = false;
        while (!done) {
            final int count = stream.read();
            char baseChar = 'a';
            if (count == eofChar) {
                break;
            }
            switch (count) {
                case 35: {
                    readToEOL(stream);
                    continue;
                }
                case 48:
                case 49:
                case 50:
                case 51:
                case 52:
                case 53:
                case 54:
                case 55:
                case 56:
                case 57: {
                    b <<= 4;
                    b += (byte)(count - 48);
                    if (++characterCount == 2) {
                        bytes.add(b);
                        characterCount = 0;
                        b = 0;
                        continue;
                    }
                    continue;
                }
                case 65:
                case 66:
                case 67:
                case 68:
                case 69:
                case 70: {
                    baseChar = 'A';
                }
                case 97:
                case 98:
                case 99:
                case 100:
                case 101:
                case 102: {
                    b <<= 4;
                    b += (byte)(count + 10 - baseChar);
                    if (++characterCount == 2) {
                        bytes.add(b);
                        characterCount = 0;
                        b = 0;
                        continue;
                    }
                    continue;
                }
                case -1: {
                    done = true;
                    continue;
                }
            }
        }
        final Byte[] polished = bytes.toArray(new Byte[0]);
        final byte[] rval = new byte[polished.length];
        for (int j = 0; j < polished.length; ++j) {
            rval[j] = polished[j];
        }
        return rval;
    }
    
    public static byte[] readFromString(final String data) {
        try {
            return readData(new ByteArrayInputStream(data.getBytes()), -1);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static void readToEOL(final InputStream stream) throws IOException {
        for (int c = stream.read(); c != -1 && c != 10 && c != 13; c = stream.read()) {}
    }
}
