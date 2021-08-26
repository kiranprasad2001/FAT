// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

final class ParseNode
{
    public static final ParseNode[] EMPTY_ARRAY;
    private final Ptg _token;
    private final ParseNode[] _children;
    private boolean _isIf;
    private final int _tokenCount;
    
    public ParseNode(final Ptg token, final ParseNode[] children) {
        if (token == null) {
            throw new IllegalArgumentException("token must not be null");
        }
        this._token = token;
        this._children = children;
        this._isIf = isIf(token);
        int tokenCount = 1;
        for (int i = 0; i < children.length; ++i) {
            tokenCount += children[i].getTokenCount();
        }
        if (this._isIf) {
            tokenCount += children.length;
        }
        this._tokenCount = tokenCount;
    }
    
    public ParseNode(final Ptg token) {
        this(token, ParseNode.EMPTY_ARRAY);
    }
    
    public ParseNode(final Ptg token, final ParseNode child0) {
        this(token, new ParseNode[] { child0 });
    }
    
    public ParseNode(final Ptg token, final ParseNode child0, final ParseNode child1) {
        this(token, new ParseNode[] { child0, child1 });
    }
    
    private int getTokenCount() {
        return this._tokenCount;
    }
    
    public int getEncodedSize() {
        int result = (this._token instanceof ArrayPtg) ? 8 : this._token.getSize();
        for (int i = 0; i < this._children.length; ++i) {
            result += this._children[i].getEncodedSize();
        }
        return result;
    }
    
    public static Ptg[] toTokenArray(final ParseNode rootNode) {
        final TokenCollector temp = new TokenCollector(rootNode.getTokenCount());
        rootNode.collectPtgs(temp);
        return temp.getResult();
    }
    
    private void collectPtgs(final TokenCollector temp) {
        if (isIf(this._token)) {
            this.collectIfPtgs(temp);
            return;
        }
        final boolean isPreFixOperator = this._token instanceof MemFuncPtg || this._token instanceof MemAreaPtg;
        if (isPreFixOperator) {
            temp.add(this._token);
        }
        for (int i = 0; i < this.getChildren().length; ++i) {
            this.getChildren()[i].collectPtgs(temp);
        }
        if (!isPreFixOperator) {
            temp.add(this._token);
        }
    }
    
    private void collectIfPtgs(final TokenCollector temp) {
        this.getChildren()[0].collectPtgs(temp);
        final int ifAttrIndex = temp.createPlaceholder();
        this.getChildren()[1].collectPtgs(temp);
        final int skipAfterTrueParamIndex = temp.createPlaceholder();
        final int trueParamSize = temp.sumTokenSizes(ifAttrIndex + 1, skipAfterTrueParamIndex);
        final AttrPtg attrIf = AttrPtg.createIf(trueParamSize + 4);
        if (this.getChildren().length > 2) {
            this.getChildren()[2].collectPtgs(temp);
            final int skipAfterFalseParamIndex = temp.createPlaceholder();
            final int falseParamSize = temp.sumTokenSizes(skipAfterTrueParamIndex + 1, skipAfterFalseParamIndex);
            final AttrPtg attrSkipAfterTrue = AttrPtg.createSkip(falseParamSize + 4 + 4 - 1);
            final AttrPtg attrSkipAfterFalse = AttrPtg.createSkip(3);
            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue);
            temp.setPlaceholder(skipAfterFalseParamIndex, attrSkipAfterFalse);
        }
        else {
            final AttrPtg attrSkipAfterTrue2 = AttrPtg.createSkip(3);
            temp.setPlaceholder(ifAttrIndex, attrIf);
            temp.setPlaceholder(skipAfterTrueParamIndex, attrSkipAfterTrue2);
        }
        temp.add(this._token);
    }
    
    private static boolean isIf(final Ptg token) {
        if (token instanceof FuncVarPtg) {
            final FuncVarPtg func = (FuncVarPtg)token;
            if ("IF".equals(func.getName())) {
                return true;
            }
        }
        return false;
    }
    
    public Ptg getToken() {
        return this._token;
    }
    
    public ParseNode[] getChildren() {
        return this._children;
    }
    
    static {
        EMPTY_ARRAY = new ParseNode[0];
    }
    
    private static final class TokenCollector
    {
        private final Ptg[] _ptgs;
        private int _offset;
        
        public TokenCollector(final int tokenCount) {
            this._ptgs = new Ptg[tokenCount];
            this._offset = 0;
        }
        
        public int sumTokenSizes(final int fromIx, final int toIx) {
            int result = 0;
            for (int i = fromIx; i < toIx; ++i) {
                result += this._ptgs[i].getSize();
            }
            return result;
        }
        
        public int createPlaceholder() {
            return this._offset++;
        }
        
        public void add(final Ptg token) {
            if (token == null) {
                throw new IllegalArgumentException("token must not be null");
            }
            this._ptgs[this._offset] = token;
            ++this._offset;
        }
        
        public void setPlaceholder(final int index, final Ptg token) {
            if (this._ptgs[index] != null) {
                throw new IllegalStateException("Invalid placeholder index (" + index + ")");
            }
            this._ptgs[index] = token;
        }
        
        public Ptg[] getResult() {
            return this._ptgs;
        }
    }
}
