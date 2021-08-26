// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.eventfilesystem;

import java.util.Collection;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DocumentDescriptor;
import org.apache.poi.poifs.filesystem.POIFSDocumentPath;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class POIFSReaderRegistry
{
    private Set omnivorousListeners;
    private Map selectiveListeners;
    private Map chosenDocumentDescriptors;
    
    POIFSReaderRegistry() {
        this.omnivorousListeners = new HashSet();
        this.selectiveListeners = new HashMap();
        this.chosenDocumentDescriptors = new HashMap();
    }
    
    void registerListener(final POIFSReaderListener listener, final POIFSDocumentPath path, final String documentName) {
        if (!this.omnivorousListeners.contains(listener)) {
            Set descriptors = this.selectiveListeners.get(listener);
            if (descriptors == null) {
                descriptors = new HashSet();
                this.selectiveListeners.put(listener, descriptors);
            }
            final DocumentDescriptor descriptor = new DocumentDescriptor(path, documentName);
            if (descriptors.add(descriptor)) {
                Set listeners = this.chosenDocumentDescriptors.get(descriptor);
                if (listeners == null) {
                    listeners = new HashSet();
                    this.chosenDocumentDescriptors.put(descriptor, listeners);
                }
                listeners.add(listener);
            }
        }
    }
    
    void registerListener(final POIFSReaderListener listener) {
        if (!this.omnivorousListeners.contains(listener)) {
            this.removeSelectiveListener(listener);
            this.omnivorousListeners.add(listener);
        }
    }
    
    Iterator getListeners(final POIFSDocumentPath path, final String name) {
        final Set rval = new HashSet(this.omnivorousListeners);
        final Set selectiveListeners = this.chosenDocumentDescriptors.get(new DocumentDescriptor(path, name));
        if (selectiveListeners != null) {
            rval.addAll(selectiveListeners);
        }
        return rval.iterator();
    }
    
    private void removeSelectiveListener(final POIFSReaderListener listener) {
        final Set selectedDescriptors = this.selectiveListeners.remove(listener);
        if (selectedDescriptors != null) {
            final Iterator iter = selectedDescriptors.iterator();
            while (iter.hasNext()) {
                this.dropDocument(listener, iter.next());
            }
        }
    }
    
    private void dropDocument(final POIFSReaderListener listener, final DocumentDescriptor descriptor) {
        final Set listeners = this.chosenDocumentDescriptors.get(descriptor);
        listeners.remove(listener);
        if (listeners.size() == 0) {
            this.chosenDocumentDescriptors.remove(descriptor);
        }
    }
}
