// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonsLogger extends POILogger
{
    private static LogFactory _creator;
    private Log log;
    
    public CommonsLogger() {
        this.log = null;
    }
    
    @Override
    public void initialize(final String cat) {
        this.log = CommonsLogger._creator.getInstance(cat);
    }
    
    @Override
    public void log(final int level, final Object obj1) {
        if (level == 9) {
            if (this.log.isFatalEnabled()) {
                this.log.fatal(obj1);
            }
        }
        else if (level == 7) {
            if (this.log.isErrorEnabled()) {
                this.log.error(obj1);
            }
        }
        else if (level == 5) {
            if (this.log.isWarnEnabled()) {
                this.log.warn(obj1);
            }
        }
        else if (level == 3) {
            if (this.log.isInfoEnabled()) {
                this.log.info(obj1);
            }
        }
        else if (level == 1) {
            if (this.log.isDebugEnabled()) {
                this.log.debug(obj1);
            }
        }
        else if (this.log.isTraceEnabled()) {
            this.log.trace(obj1);
        }
    }
    
    @Override
    public void log(final int level, final Object obj1, final Throwable exception) {
        if (level == 9) {
            if (this.log.isFatalEnabled()) {
                if (obj1 != null) {
                    this.log.fatal(obj1, exception);
                }
                else {
                    this.log.fatal((Object)exception);
                }
            }
        }
        else if (level == 7) {
            if (this.log.isErrorEnabled()) {
                if (obj1 != null) {
                    this.log.error(obj1, exception);
                }
                else {
                    this.log.error((Object)exception);
                }
            }
        }
        else if (level == 5) {
            if (this.log.isWarnEnabled()) {
                if (obj1 != null) {
                    this.log.warn(obj1, exception);
                }
                else {
                    this.log.warn((Object)exception);
                }
            }
        }
        else if (level == 3) {
            if (this.log.isInfoEnabled()) {
                if (obj1 != null) {
                    this.log.info(obj1, exception);
                }
                else {
                    this.log.info((Object)exception);
                }
            }
        }
        else if (level == 1) {
            if (this.log.isDebugEnabled()) {
                if (obj1 != null) {
                    this.log.debug(obj1, exception);
                }
                else {
                    this.log.debug((Object)exception);
                }
            }
        }
        else if (this.log.isTraceEnabled()) {
            if (obj1 != null) {
                this.log.trace(obj1, exception);
            }
            else {
                this.log.trace((Object)exception);
            }
        }
    }
    
    @Override
    public boolean check(final int level) {
        if (level == 9) {
            if (this.log.isFatalEnabled()) {
                return true;
            }
        }
        else if (level == 7) {
            if (this.log.isErrorEnabled()) {
                return true;
            }
        }
        else if (level == 5) {
            if (this.log.isWarnEnabled()) {
                return true;
            }
        }
        else if (level == 3) {
            if (this.log.isInfoEnabled()) {
                return true;
            }
        }
        else if (level == 1 && this.log.isDebugEnabled()) {
            return true;
        }
        return false;
    }
    
    static {
        CommonsLogger._creator = LogFactory.getFactory();
    }
}
