// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.util.HashMap;
import java.util.Map;

public class POILogFactory
{
    private static Map<String, POILogger> _loggers;
    private static POILogger _nullLogger;
    private static String _loggerClassName;
    
    private POILogFactory() {
    }
    
    public static POILogger getLogger(final Class<?> theclass) {
        return getLogger(theclass.getName());
    }
    
    public static POILogger getLogger(final String cat) {
        POILogger logger = null;
        if (POILogFactory._loggerClassName == null) {
            try {
                POILogFactory._loggerClassName = System.getProperty("org.apache.poi.util.POILogger");
            }
            catch (Exception ex) {}
            if (POILogFactory._loggerClassName == null) {
                POILogFactory._loggerClassName = POILogFactory._nullLogger.getClass().getName();
            }
        }
        if (POILogFactory._loggerClassName.equals(POILogFactory._nullLogger.getClass().getName())) {
            return POILogFactory._nullLogger;
        }
        if (POILogFactory._loggers.containsKey(cat)) {
            logger = POILogFactory._loggers.get(cat);
        }
        else {
            try {
                final Class<? extends POILogger> loggerClass = (Class<? extends POILogger>)Class.forName(POILogFactory._loggerClassName);
                logger = (POILogger)loggerClass.newInstance();
                logger.initialize(cat);
            }
            catch (Exception e) {
                logger = POILogFactory._nullLogger;
            }
            POILogFactory._loggers.put(cat, logger);
        }
        return logger;
    }
    
    static {
        POILogFactory._loggers = new HashMap<String, POILogger>();
        POILogFactory._nullLogger = new NullLogger();
        POILogFactory._loggerClassName = null;
    }
}
