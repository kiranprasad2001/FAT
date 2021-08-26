// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public abstract class MinaMaxa extends MultiOperandNumericFunction
{
    public static final Function MAXA;
    public static final Function MINA;
    
    protected MinaMaxa() {
        super(true, true);
    }
    
    static {
        MAXA = new MinaMaxa() {
            @Override
            protected double evaluate(final double[] values) {
                return (values.length > 0) ? MathX.max(values) : 0.0;
            }
        };
        MINA = new MinaMaxa() {
            @Override
            protected double evaluate(final double[] values) {
                return (values.length > 0) ? MathX.min(values) : 0.0;
            }
        };
    }
}
