// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import java.util.Set;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.HashMap;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import java.util.Collections;
import java.util.Map;
import org.apache.poi.util.Internal;

@Internal
public final class CollaboratingWorkbooksEnvironment
{
    public static final CollaboratingWorkbooksEnvironment EMPTY;
    private final Map<String, WorkbookEvaluator> _evaluatorsByName;
    private final WorkbookEvaluator[] _evaluators;
    private boolean _unhooked;
    
    private CollaboratingWorkbooksEnvironment() {
        this._evaluatorsByName = Collections.emptyMap();
        this._evaluators = new WorkbookEvaluator[0];
    }
    
    public static void setup(final String[] workbookNames, final WorkbookEvaluator[] evaluators) {
        final int nItems = workbookNames.length;
        if (evaluators.length != nItems) {
            throw new IllegalArgumentException("Number of workbook names is " + nItems + " but number of evaluators is " + evaluators.length);
        }
        if (nItems < 1) {
            throw new IllegalArgumentException("Must provide at least one collaborating worbook");
        }
        new CollaboratingWorkbooksEnvironment(workbookNames, evaluators, nItems);
    }
    
    public static void setup(final Map<String, WorkbookEvaluator> evaluatorsByName) {
        if (evaluatorsByName.size() < 1) {
            throw new IllegalArgumentException("Must provide at least one collaborating worbook");
        }
        final WorkbookEvaluator[] evaluators = evaluatorsByName.values().toArray(new WorkbookEvaluator[evaluatorsByName.size()]);
        new CollaboratingWorkbooksEnvironment(evaluatorsByName, evaluators);
    }
    
    public static void setupFormulaEvaluator(final Map<String, FormulaEvaluator> evaluators) {
        final Map<String, WorkbookEvaluator> evaluatorsByName = new HashMap<String, WorkbookEvaluator>(evaluators.size());
        for (final String wbName : evaluators.keySet()) {
            final FormulaEvaluator eval = evaluators.get(wbName);
            if (!(eval instanceof WorkbookEvaluatorProvider)) {
                throw new IllegalArgumentException("Formula Evaluator " + eval + " provides no WorkbookEvaluator access");
            }
            evaluatorsByName.put(wbName, ((WorkbookEvaluatorProvider)eval)._getWorkbookEvaluator());
        }
        setup(evaluatorsByName);
    }
    
    private CollaboratingWorkbooksEnvironment(final String[] workbookNames, final WorkbookEvaluator[] evaluators, final int nItems) {
        this(toUniqueMap(workbookNames, evaluators, nItems), evaluators);
    }
    
    private static Map<String, WorkbookEvaluator> toUniqueMap(final String[] workbookNames, final WorkbookEvaluator[] evaluators, final int nItems) {
        final Map<String, WorkbookEvaluator> evaluatorsByName = new HashMap<String, WorkbookEvaluator>(nItems * 3 / 2);
        for (int i = 0; i < nItems; ++i) {
            final String wbName = workbookNames[i];
            final WorkbookEvaluator wbEval = evaluators[i];
            if (evaluatorsByName.containsKey(wbName)) {
                throw new IllegalArgumentException("Duplicate workbook name '" + wbName + "'");
            }
            evaluatorsByName.put(wbName, wbEval);
        }
        return evaluatorsByName;
    }
    
    private CollaboratingWorkbooksEnvironment(final Map<String, WorkbookEvaluator> evaluatorsByName, final WorkbookEvaluator[] evaluators) {
        final IdentityHashMap<WorkbookEvaluator, String> uniqueEvals = new IdentityHashMap<WorkbookEvaluator, String>(evaluators.length);
        for (final String wbName : evaluatorsByName.keySet()) {
            final WorkbookEvaluator wbEval = evaluatorsByName.get(wbName);
            if (uniqueEvals.containsKey(wbEval)) {
                final String msg = "Attempted to register same workbook under names '" + uniqueEvals.get(wbEval) + "' and '" + wbName + "'";
                throw new IllegalArgumentException(msg);
            }
            uniqueEvals.put(wbEval, wbName);
        }
        this.unhookOldEnvironments(evaluators);
        hookNewEnvironment(evaluators, this);
        this._unhooked = false;
        this._evaluators = evaluators;
        this._evaluatorsByName = evaluatorsByName;
    }
    
    private static void hookNewEnvironment(final WorkbookEvaluator[] evaluators, final CollaboratingWorkbooksEnvironment env) {
        final int nItems = evaluators.length;
        final IEvaluationListener evalListener = evaluators[0].getEvaluationListener();
        for (int i = 0; i < nItems; ++i) {
            if (evalListener != evaluators[i].getEvaluationListener()) {
                throw new RuntimeException("Workbook evaluators must all have the same evaluation listener");
            }
        }
        final EvaluationCache cache = new EvaluationCache(evalListener);
        for (int j = 0; j < nItems; ++j) {
            evaluators[j].attachToEnvironment(env, cache, j);
        }
    }
    
    private void unhookOldEnvironments(final WorkbookEvaluator[] evaluators) {
        final Set<CollaboratingWorkbooksEnvironment> oldEnvs = new HashSet<CollaboratingWorkbooksEnvironment>();
        for (int i = 0; i < evaluators.length; ++i) {
            oldEnvs.add(evaluators[i].getEnvironment());
        }
        final CollaboratingWorkbooksEnvironment[] oldCWEs = new CollaboratingWorkbooksEnvironment[oldEnvs.size()];
        oldEnvs.toArray(oldCWEs);
        for (int j = 0; j < oldCWEs.length; ++j) {
            oldCWEs[j].unhook();
        }
    }
    
    private void unhook() {
        if (this._evaluators.length < 1) {
            return;
        }
        for (int i = 0; i < this._evaluators.length; ++i) {
            this._evaluators[i].detachFromEnvironment();
        }
        this._unhooked = true;
    }
    
    public WorkbookEvaluator getWorkbookEvaluator(final String workbookName) throws WorkbookNotFoundException {
        if (this._unhooked) {
            throw new IllegalStateException("This environment has been unhooked");
        }
        final WorkbookEvaluator result = this._evaluatorsByName.get(workbookName);
        if (result == null) {
            final StringBuffer sb = new StringBuffer(256);
            sb.append("Could not resolve external workbook name '").append(workbookName).append("'.");
            if (this._evaluators.length < 1) {
                sb.append(" Workbook environment has not been set up.");
            }
            else {
                sb.append(" The following workbook names are valid: (");
                final Iterator<String> i = this._evaluatorsByName.keySet().iterator();
                int count = 0;
                while (i.hasNext()) {
                    if (count++ > 0) {
                        sb.append(", ");
                    }
                    sb.append("'").append(i.next()).append("'");
                }
                sb.append(")");
            }
            throw new WorkbookNotFoundException(sb.toString());
        }
        return result;
    }
    
    static {
        EMPTY = new CollaboratingWorkbooksEnvironment();
    }
    
    public static final class WorkbookNotFoundException extends Exception
    {
        private static final long serialVersionUID = 8787784539811167941L;
        
        WorkbookNotFoundException(final String msg) {
            super(msg);
        }
    }
}
