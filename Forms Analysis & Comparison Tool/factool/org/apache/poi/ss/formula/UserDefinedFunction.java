// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;
import org.apache.poi.ss.formula.eval.FunctionNameEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class UserDefinedFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    private UserDefinedFunction() {
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        final int nIncomingArgs = args.length;
        if (nIncomingArgs < 1) {
            throw new RuntimeException("function name argument missing");
        }
        final ValueEval nameArg = args[0];
        if (!(nameArg instanceof FunctionNameEval)) {
            throw new RuntimeException("First argument should be a NameEval, but got (" + nameArg.getClass().getName() + ")");
        }
        final String functionName = ((FunctionNameEval)nameArg).getFunctionName();
        final FreeRefFunction targetFunc = ec.findUserDefinedFunction(functionName);
        if (targetFunc == null) {
            throw new NotImplementedFunctionException(functionName);
        }
        final int nOutGoingArgs = nIncomingArgs - 1;
        final ValueEval[] outGoingArgs = new ValueEval[nOutGoingArgs];
        System.arraycopy(args, 1, outGoingArgs, 0, nOutGoingArgs);
        return targetFunc.evaluate(outGoingArgs, ec);
    }
    
    static {
        instance = new UserDefinedFunction();
    }
}
