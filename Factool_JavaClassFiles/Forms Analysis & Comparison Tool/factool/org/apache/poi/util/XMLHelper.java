// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import javax.xml.parsers.DocumentBuilderFactory;

public final class XMLHelper
{
    private static POILogger logger;
    
    public static DocumentBuilderFactory getDocumentBuilderFactory() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setExpandEntityReferences(false);
        trySetSAXFeature(factory, "http://javax.xml.XMLConstants/feature/secure-processing", true);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-general-entities", false);
        trySetSAXFeature(factory, "http://xml.org/sax/features/external-parameter-entities", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        trySetSAXFeature(factory, "http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        return factory;
    }
    
    private static void trySetSAXFeature(final DocumentBuilderFactory documentBuilderFactory, final String feature, final boolean enabled) {
        try {
            documentBuilderFactory.setFeature(feature, enabled);
        }
        catch (Exception e) {
            XMLHelper.logger.log(5, "SAX Feature unsupported", feature, e);
        }
        catch (AbstractMethodError ame) {
            XMLHelper.logger.log(5, "Cannot set SAX feature because outdated XML parser in classpath", feature, ame);
        }
    }
    
    static {
        XMLHelper.logger = POILogFactory.getLogger(XMLHelper.class);
    }
}
