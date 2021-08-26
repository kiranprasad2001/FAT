// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.AbstractSet;
import java.util.Map;
import java.util.Collection;
import java.util.Set;
import java.util.AbstractMap;

public class BinaryTree extends AbstractMap
{
    final Node[] _root;
    int _size;
    int _modifications;
    private final Set[] _key_set;
    private final Set[] _entry_set;
    private final Collection[] _value_collection;
    static int _KEY;
    static int _VALUE;
    private static int _INDEX_SUM;
    private static int _MINIMUM_INDEX;
    private static int _INDEX_COUNT;
    private static String[] _data_name;
    
    public BinaryTree() {
        this._size = 0;
        this._modifications = 0;
        this._key_set = new Set[] { null, null };
        this._entry_set = new Set[] { null, null };
        this._value_collection = new Collection[] { null, null };
        this._root = new Node[] { null, null };
    }
    
    public BinaryTree(final Map map) throws ClassCastException, NullPointerException, IllegalArgumentException {
        this();
        this.putAll(map);
    }
    
    public Object getKeyForValue(final Object value) throws ClassCastException, NullPointerException {
        return this.doGet((Comparable)value, BinaryTree._VALUE);
    }
    
    public Object removeValue(final Object value) {
        return this.doRemove((Comparable)value, BinaryTree._VALUE);
    }
    
    public Set entrySetByValue() {
        if (this._entry_set[BinaryTree._VALUE] == null) {
            this._entry_set[BinaryTree._VALUE] = new AbstractSet() {
                @Override
                public Iterator iterator() {
                    return new BinaryTreeIterator(BinaryTree._VALUE) {
                        @Override
                        protected Object doGetNext() {
                            return this._last_returned_node;
                        }
                    };
                }
                
                @Override
                public boolean contains(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry entry = (Map.Entry)o;
                    final Object key = entry.getKey();
                    final Node node = BinaryTree.this.lookup(entry.getValue(), BinaryTree._VALUE);
                    return node != null && node.getData(BinaryTree._KEY).equals(key);
                }
                
                @Override
                public boolean remove(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry entry = (Map.Entry)o;
                    final Object key = entry.getKey();
                    final Node node = BinaryTree.this.lookup(entry.getValue(), BinaryTree._VALUE);
                    if (node != null && node.getData(BinaryTree._KEY).equals(key)) {
                        BinaryTree.this.doRedBlackDelete(node);
                        return true;
                    }
                    return false;
                }
                
                @Override
                public int size() {
                    return BinaryTree.this.size();
                }
                
                @Override
                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return this._entry_set[BinaryTree._VALUE];
    }
    
    public Set keySetByValue() {
        if (this._key_set[BinaryTree._VALUE] == null) {
            this._key_set[BinaryTree._VALUE] = new AbstractSet() {
                @Override
                public Iterator iterator() {
                    return new BinaryTreeIterator(BinaryTree._VALUE) {
                        @Override
                        protected Object doGetNext() {
                            return this._last_returned_node.getData(BinaryTree._KEY);
                        }
                    };
                }
                
                @Override
                public int size() {
                    return BinaryTree.this.size();
                }
                
                @Override
                public boolean contains(final Object o) {
                    return BinaryTree.this.containsKey(o);
                }
                
                @Override
                public boolean remove(final Object o) {
                    final int old_size = BinaryTree.this._size;
                    BinaryTree.this.remove(o);
                    return BinaryTree.this._size != old_size;
                }
                
                @Override
                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return this._key_set[BinaryTree._VALUE];
    }
    
    public Collection valuesByValue() {
        if (this._value_collection[BinaryTree._VALUE] == null) {
            this._value_collection[BinaryTree._VALUE] = new AbstractCollection() {
                @Override
                public Iterator iterator() {
                    return new BinaryTreeIterator(BinaryTree._VALUE) {
                        @Override
                        protected Object doGetNext() {
                            return this._last_returned_node.getData(BinaryTree._VALUE);
                        }
                    };
                }
                
                @Override
                public int size() {
                    return BinaryTree.this.size();
                }
                
                @Override
                public boolean contains(final Object o) {
                    return BinaryTree.this.containsValue(o);
                }
                
                @Override
                public boolean remove(final Object o) {
                    final int old_size = BinaryTree.this._size;
                    BinaryTree.this.removeValue(o);
                    return BinaryTree.this._size != old_size;
                }
                
                @Override
                public boolean removeAll(final Collection c) {
                    boolean modified = false;
                    final Iterator iter = c.iterator();
                    while (iter.hasNext()) {
                        if (BinaryTree.this.removeValue(iter.next()) != null) {
                            modified = true;
                        }
                    }
                    return modified;
                }
                
                @Override
                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return this._value_collection[BinaryTree._VALUE];
    }
    
    private Object doRemove(final Comparable o, final int index) {
        final Node node = this.lookup(o, index);
        Object rval = null;
        if (node != null) {
            rval = node.getData(this.oppositeIndex(index));
            this.doRedBlackDelete(node);
        }
        return rval;
    }
    
    private Object doGet(final Comparable o, final int index) {
        checkNonNullComparable(o, index);
        final Node node = this.lookup(o, index);
        return (node == null) ? null : node.getData(this.oppositeIndex(index));
    }
    
    private int oppositeIndex(final int index) {
        return BinaryTree._INDEX_SUM - index;
    }
    
    public Node lookup(final Comparable data, final int index) {
        Node rval = null;
        int cmp;
        for (Node node = this._root[index]; node != null; node = ((cmp < 0) ? node.getLeft(index) : node.getRight(index))) {
            cmp = compare(data, node.getData(index));
            if (cmp == 0) {
                rval = node;
                break;
            }
        }
        return rval;
    }
    
    private static int compare(final Comparable o1, final Comparable o2) {
        return o1.compareTo(o2);
    }
    
    static Node leastNode(final Node node, final int index) {
        Node rval = node;
        if (rval != null) {
            while (rval.getLeft(index) != null) {
                rval = rval.getLeft(index);
            }
        }
        return rval;
    }
    
    static Node nextGreater(final Node node, final int index) {
        Node rval = null;
        if (node == null) {
            rval = null;
        }
        else if (node.getRight(index) != null) {
            rval = leastNode(node.getRight(index), index);
        }
        else {
            Node parent = node.getParent(index);
            for (Node child = node; parent != null && child == parent.getRight(index); child = parent, parent = parent.getParent(index)) {}
            rval = parent;
        }
        return rval;
    }
    
    private static void copyColor(final Node from, final Node to, final int index) {
        if (to != null) {
            if (from == null) {
                to.setBlack(index);
            }
            else {
                to.copyColor(from, index);
            }
        }
    }
    
    private static boolean isRed(final Node node, final int index) {
        return node != null && node.isRed(index);
    }
    
    private static boolean isBlack(final Node node, final int index) {
        return node == null || node.isBlack(index);
    }
    
    private static void makeRed(final Node node, final int index) {
        if (node != null) {
            node.setRed(index);
        }
    }
    
    private static void makeBlack(final Node node, final int index) {
        if (node != null) {
            node.setBlack(index);
        }
    }
    
    private static Node getGrandParent(final Node node, final int index) {
        return getParent(getParent(node, index), index);
    }
    
    private static Node getParent(final Node node, final int index) {
        return (node == null) ? null : node.getParent(index);
    }
    
    private static Node getRightChild(final Node node, final int index) {
        return (node == null) ? null : node.getRight(index);
    }
    
    private static Node getLeftChild(final Node node, final int index) {
        return (node == null) ? null : node.getLeft(index);
    }
    
    private static boolean isLeftChild(final Node node, final int index) {
        return node == null || (node.getParent(index) != null && node == node.getParent(index).getLeft(index));
    }
    
    private static boolean isRightChild(final Node node, final int index) {
        return node == null || (node.getParent(index) != null && node == node.getParent(index).getRight(index));
    }
    
    private void rotateLeft(final Node node, final int index) {
        final Node right_child = node.getRight(index);
        node.setRight(right_child.getLeft(index), index);
        if (right_child.getLeft(index) != null) {
            right_child.getLeft(index).setParent(node, index);
        }
        right_child.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {
            this._root[index] = right_child;
        }
        else if (node.getParent(index).getLeft(index) == node) {
            node.getParent(index).setLeft(right_child, index);
        }
        else {
            node.getParent(index).setRight(right_child, index);
        }
        right_child.setLeft(node, index);
        node.setParent(right_child, index);
    }
    
    private void rotateRight(final Node node, final int index) {
        final Node left_child = node.getLeft(index);
        node.setLeft(left_child.getRight(index), index);
        if (left_child.getRight(index) != null) {
            left_child.getRight(index).setParent(node, index);
        }
        left_child.setParent(node.getParent(index), index);
        if (node.getParent(index) == null) {
            this._root[index] = left_child;
        }
        else if (node.getParent(index).getRight(index) == node) {
            node.getParent(index).setRight(left_child, index);
        }
        else {
            node.getParent(index).setLeft(left_child, index);
        }
        left_child.setRight(node, index);
        node.setParent(left_child, index);
    }
    
    private void doRedBlackInsert(final Node inserted_node, final int index) {
        Node current_node = inserted_node;
        makeRed(current_node, index);
        while (current_node != null && current_node != this._root[index] && isRed(current_node.getParent(index), index)) {
            if (isLeftChild(getParent(current_node, index), index)) {
                final Node y = getRightChild(getGrandParent(current_node, index), index);
                if (isRed(y, index)) {
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(current_node, index), index);
                    current_node = getGrandParent(current_node, index);
                }
                else {
                    if (isRightChild(current_node, index)) {
                        current_node = getParent(current_node, index);
                        this.rotateLeft(current_node, index);
                    }
                    makeBlack(getParent(current_node, index), index);
                    makeRed(getGrandParent(current_node, index), index);
                    if (getGrandParent(current_node, index) == null) {
                        continue;
                    }
                    this.rotateRight(getGrandParent(current_node, index), index);
                }
            }
            else {
                final Node y = getLeftChild(getGrandParent(current_node, index), index);
                if (isRed(y, index)) {
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(y, index);
                    makeRed(getGrandParent(current_node, index), index);
                    current_node = getGrandParent(current_node, index);
                }
                else {
                    if (isLeftChild(current_node, index)) {
                        current_node = getParent(current_node, index);
                        this.rotateRight(current_node, index);
                    }
                    makeBlack(getParent(current_node, index), index);
                    makeRed(getGrandParent(current_node, index), index);
                    if (getGrandParent(current_node, index) == null) {
                        continue;
                    }
                    this.rotateLeft(getGrandParent(current_node, index), index);
                }
            }
        }
        makeBlack(this._root[index], index);
    }
    
    void doRedBlackDelete(final Node deleted_node) {
        for (int index = BinaryTree._MINIMUM_INDEX; index < BinaryTree._INDEX_COUNT; ++index) {
            if (deleted_node.getLeft(index) != null && deleted_node.getRight(index) != null) {
                this.swapPosition(nextGreater(deleted_node, index), deleted_node, index);
            }
            final Node replacement = (deleted_node.getLeft(index) != null) ? deleted_node.getLeft(index) : deleted_node.getRight(index);
            if (replacement != null) {
                replacement.setParent(deleted_node.getParent(index), index);
                if (deleted_node.getParent(index) == null) {
                    this._root[index] = replacement;
                }
                else if (deleted_node == deleted_node.getParent(index).getLeft(index)) {
                    deleted_node.getParent(index).setLeft(replacement, index);
                }
                else {
                    deleted_node.getParent(index).setRight(replacement, index);
                }
                deleted_node.setLeft(null, index);
                deleted_node.setRight(null, index);
                deleted_node.setParent(null, index);
                if (isBlack(deleted_node, index)) {
                    this.doRedBlackDeleteFixup(replacement, index);
                }
            }
            else if (deleted_node.getParent(index) == null) {
                this._root[index] = null;
            }
            else {
                if (isBlack(deleted_node, index)) {
                    this.doRedBlackDeleteFixup(deleted_node, index);
                }
                if (deleted_node.getParent(index) != null) {
                    if (deleted_node == deleted_node.getParent(index).getLeft(index)) {
                        deleted_node.getParent(index).setLeft(null, index);
                    }
                    else {
                        deleted_node.getParent(index).setRight(null, index);
                    }
                    deleted_node.setParent(null, index);
                }
            }
        }
        this.shrink();
    }
    
    private void doRedBlackDeleteFixup(final Node replacement_node, final int index) {
        Node current_node = replacement_node;
        while (current_node != this._root[index] && isBlack(current_node, index)) {
            if (isLeftChild(current_node, index)) {
                Node sibling_node = getRightChild(getParent(current_node, index), index);
                if (isRed(sibling_node, index)) {
                    makeBlack(sibling_node, index);
                    makeRed(getParent(current_node, index), index);
                    this.rotateLeft(getParent(current_node, index), index);
                    sibling_node = getRightChild(getParent(current_node, index), index);
                }
                if (isBlack(getLeftChild(sibling_node, index), index) && isBlack(getRightChild(sibling_node, index), index)) {
                    makeRed(sibling_node, index);
                    current_node = getParent(current_node, index);
                }
                else {
                    if (isBlack(getRightChild(sibling_node, index), index)) {
                        makeBlack(getLeftChild(sibling_node, index), index);
                        makeRed(sibling_node, index);
                        this.rotateRight(sibling_node, index);
                        sibling_node = getRightChild(getParent(current_node, index), index);
                    }
                    copyColor(getParent(current_node, index), sibling_node, index);
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(getRightChild(sibling_node, index), index);
                    this.rotateLeft(getParent(current_node, index), index);
                    current_node = this._root[index];
                }
            }
            else {
                Node sibling_node = getLeftChild(getParent(current_node, index), index);
                if (isRed(sibling_node, index)) {
                    makeBlack(sibling_node, index);
                    makeRed(getParent(current_node, index), index);
                    this.rotateRight(getParent(current_node, index), index);
                    sibling_node = getLeftChild(getParent(current_node, index), index);
                }
                if (isBlack(getRightChild(sibling_node, index), index) && isBlack(getLeftChild(sibling_node, index), index)) {
                    makeRed(sibling_node, index);
                    current_node = getParent(current_node, index);
                }
                else {
                    if (isBlack(getLeftChild(sibling_node, index), index)) {
                        makeBlack(getRightChild(sibling_node, index), index);
                        makeRed(sibling_node, index);
                        this.rotateLeft(sibling_node, index);
                        sibling_node = getLeftChild(getParent(current_node, index), index);
                    }
                    copyColor(getParent(current_node, index), sibling_node, index);
                    makeBlack(getParent(current_node, index), index);
                    makeBlack(getLeftChild(sibling_node, index), index);
                    this.rotateRight(getParent(current_node, index), index);
                    current_node = this._root[index];
                }
            }
        }
        makeBlack(current_node, index);
    }
    
    private void swapPosition(final Node x, final Node y, final int index) {
        final Node x_old_parent = x.getParent(index);
        final Node x_old_left_child = x.getLeft(index);
        final Node x_old_right_child = x.getRight(index);
        final Node y_old_parent = y.getParent(index);
        final Node y_old_left_child = y.getLeft(index);
        final Node y_old_right_child = y.getRight(index);
        final boolean x_was_left_child = x.getParent(index) != null && x == x.getParent(index).getLeft(index);
        final boolean y_was_left_child = y.getParent(index) != null && y == y.getParent(index).getLeft(index);
        if (x == y_old_parent) {
            x.setParent(y, index);
            if (y_was_left_child) {
                y.setLeft(x, index);
                y.setRight(x_old_right_child, index);
            }
            else {
                y.setRight(x, index);
                y.setLeft(x_old_left_child, index);
            }
        }
        else {
            x.setParent(y_old_parent, index);
            if (y_old_parent != null) {
                if (y_was_left_child) {
                    y_old_parent.setLeft(x, index);
                }
                else {
                    y_old_parent.setRight(x, index);
                }
            }
            y.setLeft(x_old_left_child, index);
            y.setRight(x_old_right_child, index);
        }
        if (y == x_old_parent) {
            y.setParent(x, index);
            if (x_was_left_child) {
                x.setLeft(y, index);
                x.setRight(y_old_right_child, index);
            }
            else {
                x.setRight(y, index);
                x.setLeft(y_old_left_child, index);
            }
        }
        else {
            y.setParent(x_old_parent, index);
            if (x_old_parent != null) {
                if (x_was_left_child) {
                    x_old_parent.setLeft(y, index);
                }
                else {
                    x_old_parent.setRight(y, index);
                }
            }
            x.setLeft(y_old_left_child, index);
            x.setRight(y_old_right_child, index);
        }
        if (x.getLeft(index) != null) {
            x.getLeft(index).setParent(x, index);
        }
        if (x.getRight(index) != null) {
            x.getRight(index).setParent(x, index);
        }
        if (y.getLeft(index) != null) {
            y.getLeft(index).setParent(y, index);
        }
        if (y.getRight(index) != null) {
            y.getRight(index).setParent(y, index);
        }
        x.swapColors(y, index);
        if (this._root[index] == x) {
            this._root[index] = y;
        }
        else if (this._root[index] == y) {
            this._root[index] = x;
        }
    }
    
    private static void checkNonNullComparable(final Object o, final int index) {
        if (o == null) {
            throw new NullPointerException(BinaryTree._data_name[index] + " cannot be null");
        }
        if (!(o instanceof Comparable)) {
            throw new ClassCastException(BinaryTree._data_name[index] + " must be Comparable");
        }
    }
    
    private static void checkKey(final Object key) {
        checkNonNullComparable(key, BinaryTree._KEY);
    }
    
    private static void checkValue(final Object value) {
        checkNonNullComparable(value, BinaryTree._VALUE);
    }
    
    private static void checkKeyAndValue(final Object key, final Object value) {
        checkKey(key);
        checkValue(value);
    }
    
    private void modify() {
        ++this._modifications;
    }
    
    private void grow() {
        this.modify();
        ++this._size;
    }
    
    private void shrink() {
        this.modify();
        --this._size;
    }
    
    private void insertValue(final Node newNode) throws IllegalArgumentException {
        Node node = this._root[BinaryTree._VALUE];
        while (true) {
            final int cmp = compare(newNode.getData(BinaryTree._VALUE), node.getData(BinaryTree._VALUE));
            if (cmp == 0) {
                throw new IllegalArgumentException("Cannot store a duplicate value (\"" + newNode.getData(BinaryTree._VALUE) + "\") in this Map");
            }
            if (cmp < 0) {
                if (node.getLeft(BinaryTree._VALUE) == null) {
                    node.setLeft(newNode, BinaryTree._VALUE);
                    newNode.setParent(node, BinaryTree._VALUE);
                    this.doRedBlackInsert(newNode, BinaryTree._VALUE);
                    break;
                }
                node = node.getLeft(BinaryTree._VALUE);
            }
            else {
                if (node.getRight(BinaryTree._VALUE) == null) {
                    node.setRight(newNode, BinaryTree._VALUE);
                    newNode.setParent(node, BinaryTree._VALUE);
                    this.doRedBlackInsert(newNode, BinaryTree._VALUE);
                    break;
                }
                node = node.getRight(BinaryTree._VALUE);
            }
        }
    }
    
    @Override
    public int size() {
        return this._size;
    }
    
    @Override
    public boolean containsKey(final Object key) throws ClassCastException, NullPointerException {
        checkKey(key);
        return this.lookup((Comparable)key, BinaryTree._KEY) != null;
    }
    
    @Override
    public boolean containsValue(final Object value) {
        checkValue(value);
        return this.lookup((Comparable)value, BinaryTree._VALUE) != null;
    }
    
    @Override
    public Object get(final Object key) throws ClassCastException, NullPointerException {
        return this.doGet((Comparable)key, BinaryTree._KEY);
    }
    
    @Override
    public Object put(final Object key, final Object value) throws ClassCastException, NullPointerException, IllegalArgumentException {
        checkKeyAndValue(key, value);
        Node node = this._root[BinaryTree._KEY];
        if (node == null) {
            final Node root = new Node((Comparable)key, (Comparable)value);
            this._root[BinaryTree._KEY] = root;
            this._root[BinaryTree._VALUE] = root;
            this.grow();
        }
        else {
            while (true) {
                final int cmp = compare((Comparable)key, node.getData(BinaryTree._KEY));
                if (cmp == 0) {
                    throw new IllegalArgumentException("Cannot store a duplicate key (\"" + key + "\") in this Map");
                }
                if (cmp < 0) {
                    if (node.getLeft(BinaryTree._KEY) == null) {
                        final Node newNode = new Node((Comparable)key, (Comparable)value);
                        this.insertValue(newNode);
                        node.setLeft(newNode, BinaryTree._KEY);
                        newNode.setParent(node, BinaryTree._KEY);
                        this.doRedBlackInsert(newNode, BinaryTree._KEY);
                        this.grow();
                        break;
                    }
                    node = node.getLeft(BinaryTree._KEY);
                }
                else {
                    if (node.getRight(BinaryTree._KEY) == null) {
                        final Node newNode = new Node((Comparable)key, (Comparable)value);
                        this.insertValue(newNode);
                        node.setRight(newNode, BinaryTree._KEY);
                        newNode.setParent(node, BinaryTree._KEY);
                        this.doRedBlackInsert(newNode, BinaryTree._KEY);
                        this.grow();
                        break;
                    }
                    node = node.getRight(BinaryTree._KEY);
                }
            }
        }
        return null;
    }
    
    @Override
    public Object remove(final Object key) {
        return this.doRemove((Comparable)key, BinaryTree._KEY);
    }
    
    @Override
    public void clear() {
        this.modify();
        this._size = 0;
        this._root[BinaryTree._KEY] = null;
        this._root[BinaryTree._VALUE] = null;
    }
    
    @Override
    public Set keySet() {
        if (this._key_set[BinaryTree._KEY] == null) {
            this._key_set[BinaryTree._KEY] = new AbstractSet() {
                @Override
                public Iterator iterator() {
                    return new BinaryTreeIterator(BinaryTree._KEY) {
                        @Override
                        protected Object doGetNext() {
                            return this._last_returned_node.getData(BinaryTree._KEY);
                        }
                    };
                }
                
                @Override
                public int size() {
                    return BinaryTree.this.size();
                }
                
                @Override
                public boolean contains(final Object o) {
                    return BinaryTree.this.containsKey(o);
                }
                
                @Override
                public boolean remove(final Object o) {
                    final int old_size = BinaryTree.this._size;
                    BinaryTree.this.remove(o);
                    return BinaryTree.this._size != old_size;
                }
                
                @Override
                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return this._key_set[BinaryTree._KEY];
    }
    
    @Override
    public Collection values() {
        if (this._value_collection[BinaryTree._KEY] == null) {
            this._value_collection[BinaryTree._KEY] = new AbstractCollection() {
                @Override
                public Iterator iterator() {
                    return new BinaryTreeIterator(BinaryTree._KEY) {
                        @Override
                        protected Object doGetNext() {
                            return this._last_returned_node.getData(BinaryTree._VALUE);
                        }
                    };
                }
                
                @Override
                public int size() {
                    return BinaryTree.this.size();
                }
                
                @Override
                public boolean contains(final Object o) {
                    return BinaryTree.this.containsValue(o);
                }
                
                @Override
                public boolean remove(final Object o) {
                    final int old_size = BinaryTree.this._size;
                    BinaryTree.this.removeValue(o);
                    return BinaryTree.this._size != old_size;
                }
                
                @Override
                public boolean removeAll(final Collection c) {
                    boolean modified = false;
                    final Iterator iter = c.iterator();
                    while (iter.hasNext()) {
                        if (BinaryTree.this.removeValue(iter.next()) != null) {
                            modified = true;
                        }
                    }
                    return modified;
                }
                
                @Override
                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return this._value_collection[BinaryTree._KEY];
    }
    
    @Override
    public Set entrySet() {
        if (this._entry_set[BinaryTree._KEY] == null) {
            this._entry_set[BinaryTree._KEY] = new AbstractSet() {
                @Override
                public Iterator iterator() {
                    return new BinaryTreeIterator(BinaryTree._KEY) {
                        @Override
                        protected Object doGetNext() {
                            return this._last_returned_node;
                        }
                    };
                }
                
                @Override
                public boolean contains(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry entry = (Map.Entry)o;
                    final Object value = entry.getValue();
                    final Node node = BinaryTree.this.lookup(entry.getKey(), BinaryTree._KEY);
                    return node != null && node.getData(BinaryTree._VALUE).equals(value);
                }
                
                @Override
                public boolean remove(final Object o) {
                    if (!(o instanceof Map.Entry)) {
                        return false;
                    }
                    final Map.Entry entry = (Map.Entry)o;
                    final Object value = entry.getValue();
                    final Node node = BinaryTree.this.lookup(entry.getKey(), BinaryTree._KEY);
                    if (node != null && node.getData(BinaryTree._VALUE).equals(value)) {
                        BinaryTree.this.doRedBlackDelete(node);
                        return true;
                    }
                    return false;
                }
                
                @Override
                public int size() {
                    return BinaryTree.this.size();
                }
                
                @Override
                public void clear() {
                    BinaryTree.this.clear();
                }
            };
        }
        return this._entry_set[BinaryTree._KEY];
    }
    
    static {
        BinaryTree._KEY = 0;
        BinaryTree._VALUE = 1;
        BinaryTree._INDEX_SUM = BinaryTree._KEY + BinaryTree._VALUE;
        BinaryTree._MINIMUM_INDEX = 0;
        BinaryTree._INDEX_COUNT = 2;
        BinaryTree._data_name = new String[] { "key", "value" };
    }
    
    private abstract class BinaryTreeIterator implements Iterator
    {
        private int _expected_modifications;
        protected Node _last_returned_node;
        private Node _next_node;
        private int _type;
        
        BinaryTreeIterator(final int type) {
            this._type = type;
            this._expected_modifications = BinaryTree.this._modifications;
            this._last_returned_node = null;
            this._next_node = BinaryTree.leastNode(BinaryTree.this._root[this._type], this._type);
        }
        
        protected abstract Object doGetNext();
        
        @Override
        public boolean hasNext() {
            return this._next_node != null;
        }
        
        @Override
        public Object next() throws NoSuchElementException, ConcurrentModificationException {
            if (this._next_node == null) {
                throw new NoSuchElementException();
            }
            if (BinaryTree.this._modifications != this._expected_modifications) {
                throw new ConcurrentModificationException();
            }
            this._last_returned_node = this._next_node;
            this._next_node = BinaryTree.nextGreater(this._next_node, this._type);
            return this.doGetNext();
        }
        
        @Override
        public void remove() throws IllegalStateException, ConcurrentModificationException {
            if (this._last_returned_node == null) {
                throw new IllegalStateException();
            }
            if (BinaryTree.this._modifications != this._expected_modifications) {
                throw new ConcurrentModificationException();
            }
            BinaryTree.this.doRedBlackDelete(this._last_returned_node);
            ++this._expected_modifications;
            this._last_returned_node = null;
        }
    }
    
    private static final class Node implements Map.Entry
    {
        private Comparable[] _data;
        private Node[] _left;
        private Node[] _right;
        private Node[] _parent;
        private boolean[] _black;
        private int _hashcode;
        private boolean _calculated_hashcode;
        
        Node(final Comparable key, final Comparable value) {
            this._data = new Comparable[] { key, value };
            this._left = new Node[] { null, null };
            this._right = new Node[] { null, null };
            this._parent = new Node[] { null, null };
            this._black = new boolean[] { true, true };
            this._calculated_hashcode = false;
        }
        
        public Comparable getData(final int index) {
            return this._data[index];
        }
        
        public void setLeft(final Node node, final int index) {
            this._left[index] = node;
        }
        
        public Node getLeft(final int index) {
            return this._left[index];
        }
        
        public void setRight(final Node node, final int index) {
            this._right[index] = node;
        }
        
        public Node getRight(final int index) {
            return this._right[index];
        }
        
        public void setParent(final Node node, final int index) {
            this._parent[index] = node;
        }
        
        public Node getParent(final int index) {
            return this._parent[index];
        }
        
        public void swapColors(final Node node, final int index) {
            final boolean[] black = this._black;
            black[index] ^= node._black[index];
            final boolean[] black2 = node._black;
            black2[index] ^= this._black[index];
            final boolean[] black3 = this._black;
            black3[index] ^= node._black[index];
        }
        
        public boolean isBlack(final int index) {
            return this._black[index];
        }
        
        public boolean isRed(final int index) {
            return !this._black[index];
        }
        
        public void setBlack(final int index) {
            this._black[index] = true;
        }
        
        public void setRed(final int index) {
            this._black[index] = false;
        }
        
        public void copyColor(final Node node, final int index) {
            this._black[index] = node._black[index];
        }
        
        @Override
        public Object getKey() {
            return this._data[BinaryTree._KEY];
        }
        
        @Override
        public Object getValue() {
            return this._data[BinaryTree._VALUE];
        }
        
        @Override
        public Object setValue(final Object ignored) throws UnsupportedOperationException {
            throw new UnsupportedOperationException("Map.Entry.setValue is not supported");
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            final Map.Entry e = (Map.Entry)o;
            return this._data[BinaryTree._KEY].equals(e.getKey()) && this._data[BinaryTree._VALUE].equals(e.getValue());
        }
        
        @Override
        public int hashCode() {
            if (!this._calculated_hashcode) {
                this._hashcode = (this._data[BinaryTree._KEY].hashCode() ^ this._data[BinaryTree._VALUE].hashCode());
                this._calculated_hashcode = true;
            }
            return this._hashcode;
        }
    }
}
