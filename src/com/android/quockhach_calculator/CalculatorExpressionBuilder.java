/**
 * Created by Quoc Khanh on 10/01/2016.
 */

package com.android.quockhach_calculator;

import android.text.SpannableStringBuilder;
import android.text.TextUtils;

public class CalculatorExpressionBuilder extends SpannableStringBuilder {

    private final CalculatorExpressionTokenizer mTokenizer;
    private boolean mIsEdited;

    public CalculatorExpressionBuilder(
            CharSequence text, CalculatorExpressionTokenizer tokenizer, boolean isEdited) {
        super(text);

        mTokenizer = tokenizer;
        mIsEdited = isEdited;
    }

    @Override
    public SpannableStringBuilder replace(int start, int end, CharSequence tb, int tbstart,
                                          int tbend) {
        if (start != length() || end != length()) {
            mIsEdited = true;
            return super.replace(start, end, tb, tbstart, tbend);
        }

        String appendExpr =
                mTokenizer.getNormalizedExpression(tb.subSequence(tbstart, tbend).toString());
        if (appendExpr.length() == 1) {
            final String expr = mTokenizer.getNormalizedExpression(toString());
            switch (appendExpr.charAt(0)) {
                case '.':
                    final int index = expr.lastIndexOf('.');
                    if (index != -1 && TextUtils.isDigitsOnly(expr.substring(index + 1, start))) {
                        appendExpr = "";
                    }
                    break;
                case '+':
                case '*':
                case '/':
                    if (start == 0) {
                        appendExpr = "";
                        break;
                    }
                    while (start > 0 && "+-*/".indexOf(expr.charAt(start - 1)) != -1) {
                        --start;
                    }
                case '-':
                    if (start > 0 && "+-".indexOf(expr.charAt(start - 1)) != -1) {
                        --start;
                    }

                    mIsEdited = true;
                    break;
                default:
                    break;
            }
        }
        if (!mIsEdited && appendExpr.length() > 0) {
            start = 0;
            mIsEdited = true;
        }

        appendExpr = mTokenizer.getLocalizedExpression(appendExpr);
        return super.replace(start, end, appendExpr, 0, appendExpr.length());
    }
}
