/**
 * Created by Quoc Khanh on 10/01/2016.
 */

package com.android.quockhach_calculator;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;

import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class CalculatorExpressionTokenizer {

    private final Map<String, String> mReplacementMap;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CalculatorExpressionTokenizer(Context context) {
        mReplacementMap = new HashMap<>();

        Locale locale = context.getResources().getConfiguration().locale;
        if (Utils.hasLollipop() && !context.getResources().getBoolean(R.bool.use_localized_digits)) {
            locale = new Locale.Builder()
                .setLocale(locale)
                .setUnicodeLocaleKeyword("nu", "latn")
                .build();
        }

        final DecimalFormatSymbols symbols = new DecimalFormatSymbols(locale);
        final char zeroDigit = symbols.getZeroDigit();

        mReplacementMap.put(".", String.valueOf(symbols.getDecimalSeparator()));

        for (int i = 0; i <= 9; ++i) {
            mReplacementMap.put(Integer.toString(i), String.valueOf((char) (i + zeroDigit)));
        }

        mReplacementMap.put("/", context.getString(R.string.op_div));
        mReplacementMap.put("*", context.getString(R.string.op_mul));
        mReplacementMap.put("-", context.getString(R.string.op_sub));

        mReplacementMap.put("cos", context.getString(R.string.fun_cos));
        mReplacementMap.put("ln", context.getString(R.string.fun_ln));
        mReplacementMap.put("log", context.getString(R.string.fun_log));
        mReplacementMap.put("sin", context.getString(R.string.fun_sin));
        mReplacementMap.put("tan", context.getString(R.string.fun_tan));

        mReplacementMap.put("Infinity", context.getString(R.string.inf));
    }

    public String getNormalizedExpression(String expr) {
        for (Entry<String, String> replacementEntry : mReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getValue(), replacementEntry.getKey());
        }
        return expr;
    }

    public String getLocalizedExpression(String expr) {
        for (Entry<String, String> replacementEntry : mReplacementMap.entrySet()) {
            expr = expr.replace(replacementEntry.getKey(), replacementEntry.getValue());
        }
        return expr;
    }
}
