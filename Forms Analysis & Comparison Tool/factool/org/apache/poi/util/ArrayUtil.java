// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class ArrayUtil
{
    public static void arraycopy(final byte[] src, final int src_position, final byte[] dst, final int dst_position, final int length) {
        if (src_position < 0) {
            throw new IllegalArgumentException("src_position was less than 0.  Actual value " + src_position);
        }
        if (src_position >= src.length) {
            throw new IllegalArgumentException("src_position was greater than src array size.  Tried to write starting at position " + src_position + " but the array length is " + src.length);
        }
        if (src_position + length > src.length) {
            throw new IllegalArgumentException("src_position + length would overrun the src array.  Expected end at " + (src_position + length) + " actual end at " + src.length);
        }
        if (dst_position < 0) {
            throw new IllegalArgumentException("dst_position was less than 0.  Actual value " + dst_position);
        }
        if (dst_position >= dst.length) {
            throw new IllegalArgumentException("dst_position was greater than dst array size.  Tried to write starting at position " + dst_position + " but the array length is " + dst.length);
        }
        if (dst_position + length > dst.length) {
            throw new IllegalArgumentException("dst_position + length would overrun the dst array.  Expected end at " + (dst_position + length) + " actual end at " + dst.length);
        }
        System.arraycopy(src, src_position, dst, dst_position, length);
    }
    
    public static void arrayMoveWithin(final Object[] array, final int moveFrom, final int moveTo, final int numToMove) {
        if (numToMove <= 0) {
            return;
        }
        if (moveFrom == moveTo) {
            return;
        }
        if (moveFrom < 0 || moveFrom >= array.length) {
            throw new IllegalArgumentException("The moveFrom must be a valid array index");
        }
        if (moveTo < 0 || moveTo >= array.length) {
            throw new IllegalArgumentException("The moveTo must be a valid array index");
        }
        if (moveFrom + numToMove > array.length) {
            throw new IllegalArgumentException("Asked to move more entries than the array has");
        }
        if (moveTo + numToMove > array.length) {
            throw new IllegalArgumentException("Asked to move to a position that doesn't have enough space");
        }
        final Object[] toMove = new Object[numToMove];
        System.arraycopy(array, moveFrom, toMove, 0, numToMove);
        Object[] toShift;
        int shiftTo;
        if (moveFrom > moveTo) {
            toShift = new Object[moveFrom - moveTo];
            System.arraycopy(array, moveTo, toShift, 0, toShift.length);
            shiftTo = moveTo + numToMove;
        }
        else {
            toShift = new Object[moveTo - moveFrom];
            System.arraycopy(array, moveFrom + numToMove, toShift, 0, toShift.length);
            shiftTo = moveFrom;
        }
        System.arraycopy(toMove, 0, array, moveTo, toMove.length);
        System.arraycopy(toShift, 0, array, shiftTo, toShift.length);
    }
}
