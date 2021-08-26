// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.SubtractPtg;
import org.apache.poi.ss.formula.ptg.AddPtg;
import org.apache.poi.ss.formula.ptg.ConcatPtg;
import org.apache.poi.ss.formula.ptg.LessThanPtg;
import org.apache.poi.ss.formula.ptg.NotEqualPtg;
import org.apache.poi.ss.formula.ptg.LessEqualPtg;
import org.apache.poi.ss.formula.ptg.GreaterThanPtg;
import org.apache.poi.ss.formula.ptg.GreaterEqualPtg;
import org.apache.poi.ss.formula.ptg.EqualPtg;
import org.apache.poi.ss.formula.ptg.UnionPtg;
import org.apache.poi.ss.formula.ptg.DividePtg;
import org.apache.poi.ss.formula.ptg.MultiplyPtg;
import org.apache.poi.ss.formula.constant.ErrorConstant;
import org.apache.poi.ss.formula.ptg.ArrayPtg;
import org.apache.poi.ss.formula.ptg.UnaryMinusPtg;
import org.apache.poi.ss.formula.ptg.UnaryPlusPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.PercentPtg;
import org.apache.poi.ss.formula.ptg.PowerPtg;
import java.util.List;
import org.apache.poi.ss.formula.ptg.MissingArgPtg;
import java.util.ArrayList;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.ptg.FuncPtg;
import org.apache.poi.ss.formula.ptg.AttrPtg;
import org.apache.poi.ss.formula.ptg.FuncVarPtg;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.AreaPtg;
import org.apache.poi.ss.formula.ptg.RefPtg;
import org.apache.poi.ss.formula.ptg.BoolPtg;
import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.ErrPtg;
import org.apache.poi.ss.formula.ptg.ValueOperatorPtg;
import org.apache.poi.ss.formula.ptg.OperandPtg;
import org.apache.poi.ss.formula.ptg.ParenthesisPtg;
import org.apache.poi.ss.formula.ptg.OperationPtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.AbstractFunctionPtg;
import org.apache.poi.ss.formula.ptg.MemAreaPtg;
import org.apache.poi.ss.formula.ptg.MemFuncPtg;
import org.apache.poi.ss.formula.ptg.RangePtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import java.util.regex.Pattern;
import org.apache.poi.ss.SpreadsheetVersion;

public final class FormulaParser
{
    private final String _formulaString;
    private final int _formulaLength;
    private int _pointer;
    private ParseNode _rootNode;
    private static final char TAB = '\t';
    private static final char CR = '\r';
    private static final char LF = '\n';
    private char look;
    private FormulaParsingWorkbook _book;
    private SpreadsheetVersion _ssVersion;
    private int _sheetIndex;
    private static final Pattern CELL_REF_PATTERN;
    
    private FormulaParser(final String formula, final FormulaParsingWorkbook book, final int sheetIndex) {
        this._formulaString = formula;
        this._pointer = 0;
        this._book = book;
        this._ssVersion = ((book == null) ? SpreadsheetVersion.EXCEL97 : book.getSpreadsheetVersion());
        this._formulaLength = this._formulaString.length();
        this._sheetIndex = sheetIndex;
    }
    
    public static Ptg[] parse(final String formula, final FormulaParsingWorkbook workbook, final int formulaType, final int sheetIndex) {
        final FormulaParser fp = new FormulaParser(formula, workbook, sheetIndex);
        fp.parse();
        return fp.getRPNPtg(formulaType);
    }
    
    private void GetChar() {
        if (this._pointer > this._formulaLength) {
            throw new RuntimeException("too far");
        }
        if (this._pointer < this._formulaLength) {
            this.look = this._formulaString.charAt(this._pointer);
        }
        else {
            this.look = '\0';
        }
        ++this._pointer;
    }
    
    private void resetPointer(final int ptr) {
        this._pointer = ptr;
        if (this._pointer <= this._formulaLength) {
            this.look = this._formulaString.charAt(this._pointer - 1);
        }
        else {
            this.look = '\0';
        }
    }
    
    private RuntimeException expected(final String s) {
        String msg;
        if (this.look == '=' && this._formulaString.substring(0, this._pointer - 1).trim().length() < 1) {
            msg = "The specified formula '" + this._formulaString + "' starts with an equals sign which is not allowed.";
        }
        else {
            msg = "Parse error near char " + (this._pointer - 1) + " '" + this.look + "'" + " in specified formula '" + this._formulaString + "'. Expected " + s;
        }
        return new FormulaParseException(msg);
    }
    
    private static boolean IsAlpha(final char c) {
        return Character.isLetter(c) || c == '$' || c == '_';
    }
    
    private static boolean IsDigit(final char c) {
        return Character.isDigit(c);
    }
    
    private static boolean IsWhite(final char c) {
        return c == ' ' || c == '\t' || c == '\r' || c == '\n';
    }
    
    private void SkipWhite() {
        while (IsWhite(this.look)) {
            this.GetChar();
        }
    }
    
    private void Match(final char x) {
        if (this.look != x) {
            throw this.expected("'" + x + "'");
        }
        this.GetChar();
    }
    
    private String GetNum() {
        final StringBuffer value = new StringBuffer();
        while (IsDigit(this.look)) {
            value.append(this.look);
            this.GetChar();
        }
        return (value.length() == 0) ? null : value.toString();
    }
    
    private ParseNode parseRangeExpression() {
        ParseNode result = this.parseRangeable();
        boolean hasRange = false;
        while (this.look == ':') {
            final int pos = this._pointer;
            this.GetChar();
            final ParseNode nextPart = this.parseRangeable();
            checkValidRangeOperand("LHS", pos, result);
            checkValidRangeOperand("RHS", pos, nextPart);
            final ParseNode[] children = { result, nextPart };
            result = new ParseNode(RangePtg.instance, children);
            hasRange = true;
        }
        if (hasRange) {
            return augmentWithMemPtg(result);
        }
        return result;
    }
    
    private static ParseNode augmentWithMemPtg(final ParseNode root) {
        Ptg memPtg;
        if (needsMemFunc(root)) {
            memPtg = new MemFuncPtg(root.getEncodedSize());
        }
        else {
            memPtg = new MemAreaPtg(root.getEncodedSize());
        }
        return new ParseNode(memPtg, root);
    }
    
    private static boolean needsMemFunc(final ParseNode root) {
        final Ptg token = root.getToken();
        if (token instanceof AbstractFunctionPtg) {
            return true;
        }
        if (token instanceof ExternSheetReferenceToken) {
            return true;
        }
        if (token instanceof NamePtg || token instanceof NameXPtg) {
            return true;
        }
        if (token instanceof OperationPtg || token instanceof ParenthesisPtg) {
            for (final ParseNode child : root.getChildren()) {
                if (needsMemFunc(child)) {
                    return true;
                }
            }
            return false;
        }
        return !(token instanceof OperandPtg) && token instanceof OperationPtg;
    }
    
    private static void checkValidRangeOperand(final String sideName, final int currentParsePosition, final ParseNode pn) {
        if (!isValidRangeOperand(pn)) {
            throw new FormulaParseException("The " + sideName + " of the range operator ':' at position " + currentParsePosition + " is not a proper reference.");
        }
    }
    
    private static boolean isValidRangeOperand(final ParseNode a) {
        final Ptg tkn = a.getToken();
        if (tkn instanceof OperandPtg) {
            return true;
        }
        if (tkn instanceof AbstractFunctionPtg) {
            final AbstractFunctionPtg afp = (AbstractFunctionPtg)tkn;
            final byte returnClass = afp.getDefaultOperandClass();
            return 0 == returnClass;
        }
        if (tkn instanceof ValueOperatorPtg) {
            return false;
        }
        if (tkn instanceof OperationPtg) {
            return true;
        }
        if (tkn instanceof ParenthesisPtg) {
            return isValidRangeOperand(a.getChildren()[0]);
        }
        return tkn == ErrPtg.REF_INVALID;
    }
    
    private ParseNode parseRangeable() {
        this.SkipWhite();
        int savePointer = this._pointer;
        final SheetIdentifier sheetIden = this.parseSheetName();
        if (sheetIden == null) {
            this.resetPointer(savePointer);
        }
        else {
            this.SkipWhite();
            savePointer = this._pointer;
        }
        final SimpleRangePart part1 = this.parseSimpleRangePart();
        if (part1 == null) {
            if (sheetIden == null) {
                return this.parseNonRange(savePointer);
            }
            if (this.look == '#') {
                return new ParseNode(ErrPtg.valueOf(this.parseErrorLiteral()));
            }
            final String name = this.parseAsName();
            if (name.length() == 0) {
                throw new FormulaParseException("Cell reference or Named Range expected after sheet name at index " + this._pointer + ".");
            }
            final Ptg nameXPtg = this._book.getNameXPtg(name, sheetIden);
            if (nameXPtg == null) {
                throw new FormulaParseException("Specified name '" + name + "' for sheet " + sheetIden.asFormulaString() + " not found");
            }
            return new ParseNode(nameXPtg);
        }
        else {
            final boolean whiteAfterPart1 = IsWhite(this.look);
            if (whiteAfterPart1) {
                this.SkipWhite();
            }
            if (this.look == ':') {
                final int colonPos = this._pointer;
                this.GetChar();
                this.SkipWhite();
                SimpleRangePart part2 = this.parseSimpleRangePart();
                if (part2 != null && !part1.isCompatibleForArea(part2)) {
                    part2 = null;
                }
                if (part2 != null) {
                    return this.createAreaRefParseNode(sheetIden, part1, part2);
                }
                this.resetPointer(colonPos);
                if (!part1.isCell()) {
                    String prefix;
                    if (sheetIden == null) {
                        prefix = "";
                    }
                    else {
                        prefix = "'" + sheetIden.getSheetIdentifier().getName() + '!';
                    }
                    throw new FormulaParseException(prefix + part1.getRep() + "' is not a proper reference.");
                }
                return this.createAreaRefParseNode(sheetIden, part1, part2);
            }
            else if (this.look == '.') {
                this.GetChar();
                int dotCount = 1;
                while (this.look == '.') {
                    ++dotCount;
                    this.GetChar();
                }
                final boolean whiteBeforePart2 = IsWhite(this.look);
                this.SkipWhite();
                final SimpleRangePart part3 = this.parseSimpleRangePart();
                final String part1And2 = this._formulaString.substring(savePointer - 1, this._pointer - 1);
                if (part3 == null) {
                    if (sheetIden != null) {
                        throw new FormulaParseException("Complete area reference expected after sheet name at index " + this._pointer + ".");
                    }
                    return this.parseNonRange(savePointer);
                }
                else if (whiteAfterPart1 || whiteBeforePart2) {
                    if (part1.isRowOrColumn() || part3.isRowOrColumn()) {
                        throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must not contain whitespace.");
                    }
                    return this.createAreaRefParseNode(sheetIden, part1, part3);
                }
                else {
                    if (dotCount == 1 && part1.isRow() && part3.isRow()) {
                        return this.parseNonRange(savePointer);
                    }
                    if ((part1.isRowOrColumn() || part3.isRowOrColumn()) && dotCount != 2) {
                        throw new FormulaParseException("Dotted range (full row or column) expression '" + part1And2 + "' must have exactly 2 dots.");
                    }
                    return this.createAreaRefParseNode(sheetIden, part1, part3);
                }
            }
            else {
                if (part1.isCell() && this.isValidCellReference(part1.getRep())) {
                    return this.createAreaRefParseNode(sheetIden, part1, null);
                }
                if (sheetIden != null) {
                    throw new FormulaParseException("Second part of cell reference expected after sheet name at index " + this._pointer + ".");
                }
                return this.parseNonRange(savePointer);
            }
        }
    }
    
    private ParseNode parseNonRange(final int savePointer) {
        this.resetPointer(savePointer);
        if (Character.isDigit(this.look)) {
            return new ParseNode(this.parseNumber());
        }
        if (this.look == '\"') {
            return new ParseNode(new StringPtg(this.parseStringLiteral()));
        }
        final String name = this.parseAsName();
        if (this.look == '(') {
            return this.function(name);
        }
        if (name.equalsIgnoreCase("TRUE") || name.equalsIgnoreCase("FALSE")) {
            return new ParseNode(BoolPtg.valueOf(name.equalsIgnoreCase("TRUE")));
        }
        if (this._book == null) {
            throw new IllegalStateException("Need book to evaluate name '" + name + "'");
        }
        final EvaluationName evalName = this._book.getName(name, this._sheetIndex);
        if (evalName == null) {
            throw new FormulaParseException("Specified named range '" + name + "' does not exist in the current workbook.");
        }
        if (evalName.isRange()) {
            return new ParseNode(evalName.createPtg());
        }
        throw new FormulaParseException("Specified name '" + name + "' is not a range as expected.");
    }
    
    private String parseAsName() {
        final StringBuilder sb = new StringBuilder();
        if (!Character.isLetter(this.look) && this.look != '_') {
            throw this.expected("number, string, or defined name");
        }
        while (isValidDefinedNameChar(this.look)) {
            sb.append(this.look);
            this.GetChar();
        }
        this.SkipWhite();
        return sb.toString();
    }
    
    private static boolean isValidDefinedNameChar(final char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        switch (ch) {
            case '.':
            case '?':
            case '\\':
            case '_': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private ParseNode createAreaRefParseNode(final SheetIdentifier sheetIden, final SimpleRangePart part1, final SimpleRangePart part2) throws FormulaParseException {
        Ptg ptg;
        if (part2 == null) {
            final CellReference cr = part1.getCellReference();
            if (sheetIden == null) {
                ptg = new RefPtg(cr);
            }
            else {
                ptg = this._book.get3DReferencePtg(cr, sheetIden);
            }
        }
        else {
            final AreaReference areaRef = createAreaRef(part1, part2);
            if (sheetIden == null) {
                ptg = new AreaPtg(areaRef);
            }
            else {
                ptg = this._book.get3DReferencePtg(areaRef, sheetIden);
            }
        }
        return new ParseNode(ptg);
    }
    
    private static AreaReference createAreaRef(final SimpleRangePart part1, final SimpleRangePart part2) {
        if (!part1.isCompatibleForArea(part2)) {
            throw new FormulaParseException("has incompatible parts: '" + part1.getRep() + "' and '" + part2.getRep() + "'.");
        }
        if (part1.isRow()) {
            return AreaReference.getWholeRow(part1.getRep(), part2.getRep());
        }
        if (part1.isColumn()) {
            return AreaReference.getWholeColumn(part1.getRep(), part2.getRep());
        }
        return new AreaReference(part1.getCellReference(), part2.getCellReference());
    }
    
    private SimpleRangePart parseSimpleRangePart() {
        int ptr = this._pointer - 1;
        boolean hasDigits = false;
        boolean hasLetters = false;
        while (ptr < this._formulaLength) {
            final char ch = this._formulaString.charAt(ptr);
            if (Character.isDigit(ch)) {
                hasDigits = true;
            }
            else if (Character.isLetter(ch)) {
                hasLetters = true;
            }
            else if (ch != '$' && ch != '_') {
                break;
            }
            ++ptr;
        }
        if (ptr <= this._pointer - 1) {
            return null;
        }
        final String rep = this._formulaString.substring(this._pointer - 1, ptr);
        if (!FormulaParser.CELL_REF_PATTERN.matcher(rep).matches()) {
            return null;
        }
        if (hasLetters && hasDigits) {
            if (!this.isValidCellReference(rep)) {
                return null;
            }
        }
        else if (hasLetters) {
            if (!CellReference.isColumnWithnRange(rep.replace("$", ""), this._ssVersion)) {
                return null;
            }
        }
        else {
            if (!hasDigits) {
                return null;
            }
            int i;
            try {
                i = Integer.parseInt(rep.replace("$", ""));
            }
            catch (NumberFormatException e) {
                return null;
            }
            if (i < 1 || i > this._ssVersion.getMaxRows()) {
                return null;
            }
        }
        this.resetPointer(ptr + 1);
        return new SimpleRangePart(rep, hasLetters, hasDigits);
    }
    
    private SheetIdentifier parseSheetName() {
        String bookName;
        if (this.look == '[') {
            final StringBuilder sb = new StringBuilder();
            this.GetChar();
            while (this.look != ']') {
                sb.append(this.look);
                this.GetChar();
            }
            this.GetChar();
            bookName = sb.toString();
        }
        else {
            bookName = null;
        }
        if (this.look == '\'') {
            final StringBuffer sb2 = new StringBuffer();
            this.Match('\'');
            for (boolean done = this.look == '\''; !done; done = (this.look != '\'')) {
                sb2.append(this.look);
                this.GetChar();
                if (this.look == '\'') {
                    this.Match('\'');
                }
            }
            final NameIdentifier iden = new NameIdentifier(sb2.toString(), true);
            this.SkipWhite();
            if (this.look == '!') {
                this.GetChar();
                return new SheetIdentifier(bookName, iden);
            }
            if (this.look == ':') {
                return this.parseSheetRange(bookName, iden);
            }
            return null;
        }
        else if (this.look == '_' || Character.isLetter(this.look)) {
            final StringBuilder sb = new StringBuilder();
            while (isUnquotedSheetNameChar(this.look)) {
                sb.append(this.look);
                this.GetChar();
            }
            final NameIdentifier iden2 = new NameIdentifier(sb.toString(), false);
            this.SkipWhite();
            if (this.look == '!') {
                this.GetChar();
                return new SheetIdentifier(bookName, iden2);
            }
            if (this.look == ':') {
                return this.parseSheetRange(bookName, iden2);
            }
            return null;
        }
        else {
            if (this.look == '!' && bookName != null) {
                this.GetChar();
                return new SheetIdentifier(bookName, null);
            }
            return null;
        }
    }
    
    private SheetIdentifier parseSheetRange(final String bookname, final NameIdentifier sheet1Name) {
        this.GetChar();
        final SheetIdentifier sheet2 = this.parseSheetName();
        if (sheet2 != null) {
            return new SheetRangeIdentifier(bookname, sheet1Name, sheet2.getSheetIdentifier());
        }
        return null;
    }
    
    private static boolean isUnquotedSheetNameChar(final char ch) {
        if (Character.isLetterOrDigit(ch)) {
            return true;
        }
        switch (ch) {
            case '.':
            case '_': {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    private boolean isValidCellReference(final String str) {
        boolean result = CellReference.classifyCellReference(str, this._ssVersion) == CellReference.NameType.CELL;
        if (result) {
            final boolean isFunc = FunctionMetadataRegistry.getFunctionByName(str.toUpperCase()) != null;
            if (isFunc) {
                final int savePointer = this._pointer;
                this.resetPointer(this._pointer + str.length());
                this.SkipWhite();
                result = (this.look != '(');
                this.resetPointer(savePointer);
            }
        }
        return result;
    }
    
    private ParseNode function(final String name) {
        Ptg nameToken = null;
        if (!AbstractFunctionPtg.isBuiltInFunctionName(name)) {
            if (this._book == null) {
                throw new IllegalStateException("Need book to evaluate name '" + name + "'");
            }
            final EvaluationName hName = this._book.getName(name, this._sheetIndex);
            if (hName == null) {
                nameToken = this._book.getNameXPtg(name, null);
                if (nameToken == null) {
                    throw new FormulaParseException("Name '" + name + "' is completely unknown in the current workbook");
                }
            }
            else {
                if (!hName.isFunctionName()) {
                    throw new FormulaParseException("Attempt to use name '" + name + "' as a function, but defined name in workbook does not refer to a function");
                }
                nameToken = hName.createPtg();
            }
        }
        this.Match('(');
        final ParseNode[] args = this.Arguments();
        this.Match(')');
        return this.getFunction(name, nameToken, args);
    }
    
    private ParseNode getFunction(final String name, final Ptg namePtg, final ParseNode[] args) {
        final FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByName(name.toUpperCase());
        final int numArgs = args.length;
        if (fm == null) {
            if (namePtg == null) {
                throw new IllegalStateException("NamePtg must be supplied for external functions");
            }
            final ParseNode[] allArgs = new ParseNode[numArgs + 1];
            allArgs[0] = new ParseNode(namePtg);
            System.arraycopy(args, 0, allArgs, 1, numArgs);
            return new ParseNode(FuncVarPtg.create(name, numArgs + 1), allArgs);
        }
        else {
            if (namePtg != null) {
                throw new IllegalStateException("NamePtg no applicable to internal functions");
            }
            final boolean isVarArgs = !fm.hasFixedArgsLength();
            final int funcIx = fm.getIndex();
            if (funcIx == 4 && args.length == 1) {
                return new ParseNode(AttrPtg.getSumSingle(), args);
            }
            this.validateNumArgs(args.length, fm);
            AbstractFunctionPtg retval;
            if (isVarArgs) {
                retval = FuncVarPtg.create(name, numArgs);
            }
            else {
                retval = FuncPtg.create(funcIx);
            }
            return new ParseNode(retval, args);
        }
    }
    
    private void validateNumArgs(final int numArgs, final FunctionMetadata fm) {
        if (numArgs < fm.getMinParams()) {
            String msg = "Too few arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg = msg + "Expected " + fm.getMinParams();
            }
            else {
                msg = msg + "At least " + fm.getMinParams() + " were expected";
            }
            msg = msg + " but got " + numArgs + ".";
            throw new FormulaParseException(msg);
        }
        int maxArgs;
        if (fm.hasUnlimitedVarags()) {
            if (this._book != null) {
                maxArgs = this._book.getSpreadsheetVersion().getMaxFunctionArgs();
            }
            else {
                maxArgs = fm.getMaxParams();
            }
        }
        else {
            maxArgs = fm.getMaxParams();
        }
        if (numArgs > maxArgs) {
            String msg2 = "Too many arguments to function '" + fm.getName() + "'. ";
            if (fm.hasFixedArgsLength()) {
                msg2 = msg2 + "Expected " + maxArgs;
            }
            else {
                msg2 = msg2 + "At most " + maxArgs + " were expected";
            }
            msg2 = msg2 + " but got " + numArgs + ".";
            throw new FormulaParseException(msg2);
        }
    }
    
    private static boolean isArgumentDelimiter(final char ch) {
        return ch == ',' || ch == ')';
    }
    
    private ParseNode[] Arguments() {
        final List<ParseNode> temp = new ArrayList<ParseNode>(2);
        this.SkipWhite();
        if (this.look == ')') {
            return ParseNode.EMPTY_ARRAY;
        }
        boolean missedPrevArg = true;
        while (true) {
            this.SkipWhite();
            if (isArgumentDelimiter(this.look)) {
                if (missedPrevArg) {
                    temp.add(new ParseNode(MissingArgPtg.instance));
                }
                if (this.look == ')') {
                    final ParseNode[] result = new ParseNode[temp.size()];
                    temp.toArray(result);
                    return result;
                }
                this.Match(',');
                missedPrevArg = true;
            }
            else {
                temp.add(this.comparisonExpression());
                missedPrevArg = false;
                this.SkipWhite();
                if (!isArgumentDelimiter(this.look)) {
                    throw this.expected("',' or ')'");
                }
                continue;
            }
        }
    }
    
    private ParseNode powerFactor() {
        ParseNode result = this.percentFactor();
        while (true) {
            this.SkipWhite();
            if (this.look != '^') {
                break;
            }
            this.Match('^');
            final ParseNode other = this.percentFactor();
            result = new ParseNode(PowerPtg.instance, result, other);
        }
        return result;
    }
    
    private ParseNode percentFactor() {
        ParseNode result = this.parseSimpleFactor();
        while (true) {
            this.SkipWhite();
            if (this.look != '%') {
                break;
            }
            this.Match('%');
            result = new ParseNode(PercentPtg.instance, result);
        }
        return result;
    }
    
    private ParseNode parseSimpleFactor() {
        this.SkipWhite();
        switch (this.look) {
            case '#': {
                return new ParseNode(ErrPtg.valueOf(this.parseErrorLiteral()));
            }
            case '-': {
                this.Match('-');
                return this.parseUnary(false);
            }
            case '+': {
                this.Match('+');
                return this.parseUnary(true);
            }
            case '(': {
                this.Match('(');
                final ParseNode inside = this.comparisonExpression();
                this.Match(')');
                return new ParseNode(ParenthesisPtg.instance, inside);
            }
            case '\"': {
                return new ParseNode(new StringPtg(this.parseStringLiteral()));
            }
            case '{': {
                this.Match('{');
                final ParseNode arrayNode = this.parseArray();
                this.Match('}');
                return arrayNode;
            }
            default: {
                if (IsAlpha(this.look) || Character.isDigit(this.look) || this.look == '\'' || this.look == '[') {
                    return this.parseRangeExpression();
                }
                if (this.look == '.') {
                    return new ParseNode(this.parseNumber());
                }
                throw this.expected("cell ref or constant literal");
            }
        }
    }
    
    private ParseNode parseUnary(final boolean isPlus) {
        final boolean numberFollows = IsDigit(this.look) || this.look == '.';
        final ParseNode factor = this.powerFactor();
        if (numberFollows) {
            Ptg token = factor.getToken();
            if (token instanceof NumberPtg) {
                if (isPlus) {
                    return factor;
                }
                token = new NumberPtg(-((NumberPtg)token).getValue());
                return new ParseNode(token);
            }
            else if (token instanceof IntPtg) {
                if (isPlus) {
                    return factor;
                }
                final int intVal = ((IntPtg)token).getValue();
                token = new NumberPtg(-intVal);
                return new ParseNode(token);
            }
        }
        return new ParseNode(isPlus ? UnaryPlusPtg.instance : UnaryMinusPtg.instance, factor);
    }
    
    private ParseNode parseArray() {
        final List<Object[]> rowsData = new ArrayList<Object[]>();
        while (true) {
            final Object[] singleRowData = this.parseArrayRow();
            rowsData.add(singleRowData);
            if (this.look == '}') {
                final int nRows = rowsData.size();
                final Object[][] values2d = new Object[nRows][];
                rowsData.toArray(values2d);
                final int nColumns = values2d[0].length;
                this.checkRowLengths(values2d, nColumns);
                return new ParseNode(new ArrayPtg(values2d));
            }
            if (this.look != ';') {
                throw this.expected("'}' or ';'");
            }
            this.Match(';');
        }
    }
    
    private void checkRowLengths(final Object[][] values2d, final int nColumns) {
        for (int i = 0; i < values2d.length; ++i) {
            final int rowLen = values2d[i].length;
            if (rowLen != nColumns) {
                throw new FormulaParseException("Array row " + i + " has length " + rowLen + " but row 0 has length " + nColumns);
            }
        }
    }
    
    private Object[] parseArrayRow() {
        final List<Object> temp = new ArrayList<Object>();
        while (true) {
            temp.add(this.parseArrayItem());
            this.SkipWhite();
            switch (this.look) {
                case ';':
                case '}': {
                    final Object[] result = new Object[temp.size()];
                    temp.toArray(result);
                    return result;
                }
                case ',': {
                    this.Match(',');
                    continue;
                }
                default: {
                    throw this.expected("'}' or ','");
                }
            }
        }
    }
    
    private Object parseArrayItem() {
        this.SkipWhite();
        switch (this.look) {
            case '\"': {
                return this.parseStringLiteral();
            }
            case '#': {
                return ErrorConstant.valueOf(this.parseErrorLiteral());
            }
            case 'F':
            case 'T':
            case 'f':
            case 't': {
                return this.parseBooleanLiteral();
            }
            case '-': {
                this.Match('-');
                this.SkipWhite();
                return convertArrayNumber(this.parseNumber(), false);
            }
            default: {
                return convertArrayNumber(this.parseNumber(), true);
            }
        }
    }
    
    private Boolean parseBooleanLiteral() {
        final String iden = this.parseUnquotedIdentifier();
        if ("TRUE".equalsIgnoreCase(iden)) {
            return Boolean.TRUE;
        }
        if ("FALSE".equalsIgnoreCase(iden)) {
            return Boolean.FALSE;
        }
        throw this.expected("'TRUE' or 'FALSE'");
    }
    
    private static Double convertArrayNumber(final Ptg ptg, final boolean isPositive) {
        double value;
        if (ptg instanceof IntPtg) {
            value = ((IntPtg)ptg).getValue();
        }
        else {
            if (!(ptg instanceof NumberPtg)) {
                throw new RuntimeException("Unexpected ptg (" + ptg.getClass().getName() + ")");
            }
            value = ((NumberPtg)ptg).getValue();
        }
        if (!isPositive) {
            value = -value;
        }
        return new Double(value);
    }
    
    private Ptg parseNumber() {
        String number2 = null;
        String exponent = null;
        final String number3 = this.GetNum();
        if (this.look == '.') {
            this.GetChar();
            number2 = this.GetNum();
        }
        if (this.look == 'E') {
            this.GetChar();
            String sign = "";
            if (this.look == '+') {
                this.GetChar();
            }
            else if (this.look == '-') {
                this.GetChar();
                sign = "-";
            }
            final String number4 = this.GetNum();
            if (number4 == null) {
                throw this.expected("Integer");
            }
            exponent = sign + number4;
        }
        if (number3 == null && number2 == null) {
            throw this.expected("Integer");
        }
        return getNumberPtgFromString(number3, number2, exponent);
    }
    
    private int parseErrorLiteral() {
        this.Match('#');
        final String part1 = this.parseUnquotedIdentifier().toUpperCase();
        if (part1 == null) {
            throw this.expected("remainder of error constant literal");
        }
        switch (part1.charAt(0)) {
            case 'V': {
                if (part1.equals("VALUE")) {
                    this.Match('!');
                    return 15;
                }
                throw this.expected("#VALUE!");
            }
            case 'R': {
                if (part1.equals("REF")) {
                    this.Match('!');
                    return 23;
                }
                throw this.expected("#REF!");
            }
            case 'D': {
                if (part1.equals("DIV")) {
                    this.Match('/');
                    this.Match('0');
                    this.Match('!');
                    return 7;
                }
                throw this.expected("#DIV/0!");
            }
            case 'N': {
                if (part1.equals("NAME")) {
                    this.Match('?');
                    return 29;
                }
                if (part1.equals("NUM")) {
                    this.Match('!');
                    return 36;
                }
                if (part1.equals("NULL")) {
                    this.Match('!');
                    return 0;
                }
                if (!part1.equals("N")) {
                    throw this.expected("#NAME?, #NUM!, #NULL! or #N/A");
                }
                this.Match('/');
                if (this.look != 'A' && this.look != 'a') {
                    throw this.expected("#N/A");
                }
                this.Match(this.look);
                return 42;
            }
            default: {
                throw this.expected("#VALUE!, #REF!, #DIV/0!, #NAME?, #NUM!, #NULL! or #N/A");
            }
        }
    }
    
    private String parseUnquotedIdentifier() {
        if (this.look == '\'') {
            throw this.expected("unquoted identifier");
        }
        final StringBuilder sb = new StringBuilder();
        while (Character.isLetterOrDigit(this.look) || this.look == '.') {
            sb.append(this.look);
            this.GetChar();
        }
        if (sb.length() < 1) {
            return null;
        }
        return sb.toString();
    }
    
    private static Ptg getNumberPtgFromString(final String number1, final String number2, final String exponent) {
        final StringBuffer number3 = new StringBuffer();
        if (number2 != null) {
            if (number1 != null) {
                number3.append(number1);
            }
            number3.append('.');
            number3.append(number2);
            if (exponent != null) {
                number3.append('E');
                number3.append(exponent);
            }
            return new NumberPtg(number3.toString());
        }
        number3.append(number1);
        if (exponent != null) {
            number3.append('E');
            number3.append(exponent);
        }
        final String numberStr = number3.toString();
        int intVal;
        try {
            intVal = Integer.parseInt(numberStr);
        }
        catch (NumberFormatException e) {
            return new NumberPtg(numberStr);
        }
        if (IntPtg.isInRange(intVal)) {
            return new IntPtg(intVal);
        }
        return new NumberPtg(numberStr);
    }
    
    private String parseStringLiteral() {
        this.Match('\"');
        final StringBuffer token = new StringBuffer();
        while (true) {
            if (this.look == '\"') {
                this.GetChar();
                if (this.look != '\"') {
                    break;
                }
            }
            token.append(this.look);
            this.GetChar();
        }
        return token.toString();
    }
    
    private ParseNode Term() {
        ParseNode result = this.powerFactor();
        while (true) {
            this.SkipWhite();
            Ptg operator = null;
            switch (this.look) {
                case '*': {
                    this.Match('*');
                    operator = MultiplyPtg.instance;
                    break;
                }
                case '/': {
                    this.Match('/');
                    operator = DividePtg.instance;
                    break;
                }
                default: {
                    return result;
                }
            }
            final ParseNode other = this.powerFactor();
            result = new ParseNode(operator, result, other);
        }
    }
    
    private ParseNode unionExpression() {
        ParseNode result = this.comparisonExpression();
        boolean hasUnions = false;
        while (true) {
            this.SkipWhite();
            switch (this.look) {
                case ',': {
                    this.GetChar();
                    hasUnions = true;
                    final ParseNode other = this.comparisonExpression();
                    result = new ParseNode(UnionPtg.instance, result, other);
                    continue;
                }
                default: {
                    if (hasUnions) {
                        return augmentWithMemPtg(result);
                    }
                    return result;
                }
            }
        }
    }
    
    private ParseNode comparisonExpression() {
        ParseNode result = this.concatExpression();
        while (true) {
            this.SkipWhite();
            switch (this.look) {
                case '<':
                case '=':
                case '>': {
                    final Ptg comparisonToken = this.getComparisonToken();
                    final ParseNode other = this.concatExpression();
                    result = new ParseNode(comparisonToken, result, other);
                    continue;
                }
                default: {
                    return result;
                }
            }
        }
    }
    
    private Ptg getComparisonToken() {
        if (this.look == '=') {
            this.Match(this.look);
            return EqualPtg.instance;
        }
        final boolean isGreater = this.look == '>';
        this.Match(this.look);
        if (isGreater) {
            if (this.look == '=') {
                this.Match('=');
                return GreaterEqualPtg.instance;
            }
            return GreaterThanPtg.instance;
        }
        else {
            switch (this.look) {
                case '=': {
                    this.Match('=');
                    return LessEqualPtg.instance;
                }
                case '>': {
                    this.Match('>');
                    return NotEqualPtg.instance;
                }
                default: {
                    return LessThanPtg.instance;
                }
            }
        }
    }
    
    private ParseNode concatExpression() {
        ParseNode result = this.additiveExpression();
        while (true) {
            this.SkipWhite();
            if (this.look != '&') {
                break;
            }
            this.Match('&');
            final ParseNode other = this.additiveExpression();
            result = new ParseNode(ConcatPtg.instance, result, other);
        }
        return result;
    }
    
    private ParseNode additiveExpression() {
        ParseNode result = this.Term();
        while (true) {
            this.SkipWhite();
            Ptg operator = null;
            switch (this.look) {
                case '+': {
                    this.Match('+');
                    operator = AddPtg.instance;
                    break;
                }
                case '-': {
                    this.Match('-');
                    operator = SubtractPtg.instance;
                    break;
                }
                default: {
                    return result;
                }
            }
            final ParseNode other = this.Term();
            result = new ParseNode(operator, result, other);
        }
    }
    
    private void parse() {
        this._pointer = 0;
        this.GetChar();
        this._rootNode = this.unionExpression();
        if (this._pointer <= this._formulaLength) {
            final String msg = "Unused input [" + this._formulaString.substring(this._pointer - 1) + "] after attempting to parse the formula [" + this._formulaString + "]";
            throw new FormulaParseException(msg);
        }
    }
    
    private Ptg[] getRPNPtg(final int formulaType) {
        final OperandClassTransformer oct = new OperandClassTransformer(formulaType);
        oct.transformFormula(this._rootNode);
        return ParseNode.toTokenArray(this._rootNode);
    }
    
    static {
        CELL_REF_PATTERN = Pattern.compile("(\\$?[A-Za-z]+)?(\\$?[0-9]+)?");
    }
    
    private static final class SimpleRangePart
    {
        private final Type _type;
        private final String _rep;
        
        public SimpleRangePart(final String rep, final boolean hasLetters, final boolean hasNumbers) {
            this._rep = rep;
            this._type = Type.get(hasLetters, hasNumbers);
        }
        
        public boolean isCell() {
            return this._type == Type.CELL;
        }
        
        public boolean isRowOrColumn() {
            return this._type != Type.CELL;
        }
        
        public CellReference getCellReference() {
            if (this._type != Type.CELL) {
                throw new IllegalStateException("Not applicable to this type");
            }
            return new CellReference(this._rep);
        }
        
        public boolean isColumn() {
            return this._type == Type.COLUMN;
        }
        
        public boolean isRow() {
            return this._type == Type.ROW;
        }
        
        public String getRep() {
            return this._rep;
        }
        
        public boolean isCompatibleForArea(final SimpleRangePart part2) {
            return this._type == part2._type;
        }
        
        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder(64);
            sb.append(this.getClass().getName()).append(" [");
            sb.append(this._rep);
            sb.append("]");
            return sb.toString();
        }
        
        private enum Type
        {
            CELL, 
            ROW, 
            COLUMN;
            
            public static Type get(final boolean hasLetters, final boolean hasDigits) {
                if (hasLetters) {
                    return hasDigits ? Type.CELL : Type.COLUMN;
                }
                if (!hasDigits) {
                    throw new IllegalArgumentException("must have either letters or numbers");
                }
                return Type.ROW;
            }
        }
    }
}
