// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.FunctionEval;
import org.apache.poi.ss.formula.functions.Indirect;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.eval.ValueEval;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import org.apache.poi.ss.formula.eval.IntersectionEval;
import org.apache.poi.ss.formula.ptg.IntersectionPtg;
import org.apache.poi.ss.formula.eval.RangeEval;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.eval.UnaryPlusEval;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.formula.eval.UnaryMinusEval;
import org.apache.poi.ss.formula.ptg.UnaryMinusPtg;
import org.apache.poi.ss.formula.ptg.SubtractPtg;
import org.apache.poi.ss.formula.ptg.PowerPtg;
import org.apache.poi.ss.formula.eval.PercentEval;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.MultiplyPtg;
import org.apache.poi.ss.formula.ptg.DividePtg;
import org.apache.poi.ss.formula.eval.TwoOperandNumericOperation;
import org.apache.poi.ss.formula.ptg.AddPtg;
import org.apache.poi.ss.formula.eval.ConcatEval;
import org.apache.poi.ss.formula.ptg.ConcatPtg;
import org.apache.poi.ss.formula.ptg.NotEqualPtg;
import org.apache.poi.ss.formula.ptg.LessThanPtg;
import org.apache.poi.ss.formula.ptg.LessEqualPtg;
import org.apache.poi.ss.formula.ptg.GreaterThanPtg;
import org.apache.poi.ss.formula.ptg.GreaterEqualPtg;
import org.apache.poi.ss.formula.eval.RelationalOperationEval;
import org.apache.poi.ss.formula.ptg.EqualPtg;
import java.util.HashMap;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import java.util.Map;

final class OperationEvaluatorFactory
{
    private static final Map<OperationPtg, Function> _instancesByPtgClass;
    
    private OperationEvaluatorFactory() {
    }
    
    private static Map<OperationPtg, Function> initialiseInstancesMap() {
        final Map<OperationPtg, Function> m = new HashMap<OperationPtg, Function>(32);
        put(m, EqualPtg.instance, RelationalOperationEval.EqualEval);
        put(m, GreaterEqualPtg.instance, RelationalOperationEval.GreaterEqualEval);
        put(m, GreaterThanPtg.instance, RelationalOperationEval.GreaterThanEval);
        put(m, LessEqualPtg.instance, RelationalOperationEval.LessEqualEval);
        put(m, LessThanPtg.instance, RelationalOperationEval.LessThanEval);
        put(m, NotEqualPtg.instance, RelationalOperationEval.NotEqualEval);
        put(m, ConcatPtg.instance, ConcatEval.instance);
        put(m, AddPtg.instance, TwoOperandNumericOperation.AddEval);
        put(m, DividePtg.instance, TwoOperandNumericOperation.DivideEval);
        put(m, MultiplyPtg.instance, TwoOperandNumericOperation.MultiplyEval);
        put(m, PercentPtg.instance, PercentEval.instance);
        put(m, PowerPtg.instance, TwoOperandNumericOperation.PowerEval);
        put(m, SubtractPtg.instance, TwoOperandNumericOperation.SubtractEval);
        put(m, UnaryMinusPtg.instance, UnaryMinusEval.instance);
        put(m, UnaryPlusPtg.instance, UnaryPlusEval.instance);
        put(m, RangePtg.instance, RangeEval.instance);
        put(m, IntersectionPtg.instance, IntersectionEval.instance);
        return m;
    }
    
    private static void put(final Map<OperationPtg, Function> m, final OperationPtg ptgKey, final Function instance) {
        final Constructor<?>[] cc = ptgKey.getClass().getDeclaredConstructors();
        if (cc.length > 1 || !Modifier.isPrivate(cc[0].getModifiers())) {
            throw new RuntimeException("Failed to verify instance (" + ptgKey.getClass().getName() + ") is a singleton.");
        }
        m.put(ptgKey, instance);
    }
    
    public static ValueEval evaluate(final OperationPtg ptg, final ValueEval[] args, final OperationEvaluationContext ec) {
        if (ptg == null) {
            throw new IllegalArgumentException("ptg must not be null");
        }
        final Function result = OperationEvaluatorFactory._instancesByPtgClass.get(ptg);
        if (result != null) {
            return result.evaluate(args, ec.getRowIndex(), (short)ec.getColumnIndex());
        }
        if (!(ptg instanceof AbstractFunctionPtg)) {
            throw new RuntimeException("Unexpected operation ptg class (" + ptg.getClass().getName() + ")");
        }
        final AbstractFunctionPtg fptg = (AbstractFunctionPtg)ptg;
        final int functionIndex = fptg.getFunctionIndex();
        switch (functionIndex) {
            case 148: {
                return Indirect.instance.evaluate(args, ec);
            }
            case 255: {
                return UserDefinedFunction.instance.evaluate(args, ec);
            }
            default: {
                return FunctionEval.getBasicFunction(functionIndex).evaluate(args, ec.getRowIndex(), (short)ec.getColumnIndex());
            }
        }
    }
    
    static {
        _instancesByPtgClass = initialiseInstancesMap();
    }
}
