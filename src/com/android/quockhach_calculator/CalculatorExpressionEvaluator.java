/**
 * Created by Quoc Khanh on 10/01/2016.
 */

package com.android.quockhach_calculator;

import org.javia.arity.Symbols;
import org.javia.arity.SyntaxException;
import org.javia.arity.Util;

public class CalculatorExpressionEvaluator {

    private static final int MAX_DIGITS = 12;
    private static final int ROUNDING_DIGITS = Math.max(17 - MAX_DIGITS, 0);

    private final Symbols mSymbols;
    private final CalculatorExpressionTokenizer mTokenizer;

    public CalculatorExpressionEvaluator(CalculatorExpressionTokenizer tokenizer) {
        mSymbols = new Symbols();
        mTokenizer = tokenizer;
    }

    public void evaluate(CharSequence expr, EvaluateCallback callback) {
        evaluate(expr.toString(), callback);
    }

    public void evaluate(String expr, EvaluateCallback callback) {
        expr = mTokenizer.getNormalizedExpression(expr);

        while (expr.length() > 0 && "+-/*".indexOf(expr.charAt(expr.length() - 1)) != -1) {
            expr = expr.substring(0, expr.length() - 1);
        }

        try {
            if (expr.length() == 0 || Double.valueOf(expr) != null) {
                callback.onEvaluate(expr, null, Calculator.INVALID_RES_ID);
                return;
            }
        } catch (NumberFormatException e) {
        }

        try {
            double result = mSymbols.eval(expr);
            if (Double.isNaN(result)) {
                callback.onEvaluate(expr, null, R.string.error_nan);
            } else {
                final String resultString = mTokenizer.getLocalizedExpression(
                        Util.doubleToString(result, MAX_DIGITS, ROUNDING_DIGITS));
                callback.onEvaluate(expr, resultString, Calculator.INVALID_RES_ID);
            }
        } catch (SyntaxException e) {
            callback.onEvaluate(expr, null, R.string.error_syntax);
        }
    }

    public interface EvaluateCallback {
        public void onEvaluate(String expr, String result, int errorResourceId);
    }
}
