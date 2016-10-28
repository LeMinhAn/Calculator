/**
 * Created by Quoc Khanh on 10/01/2016.
 */

package com.android.quockhach_calculator;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.os.Parcelable;
import android.support.v7.view.ActionMode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.TextView;

public class CalculatorEditText extends EditText {

    private final static ActionMode.Callback NO_SELECTION_ACTION_MODE_CALLBACK =
            new ActionMode.Callback() {
                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }
            };

    private final float mMaximumTextSize;
    private final float mMinimumTextSize;
    private final float mStepTextSize;

    private final Paint mTempPaint = new TextPaint();
    private final Rect mTempRect = new Rect();

    private int mWidthConstraint = -1;
    private OnTextSizeChangeListener mOnTextSizeChangeListener;

    public CalculatorEditText(Context context) {
        this(context, null);
    }

    public CalculatorEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalculatorEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.CalculatorEditText, defStyle, 0);
        mMaximumTextSize = a.getDimension(
                R.styleable.CalculatorEditText_maxTextSize, getTextSize());
        mMinimumTextSize = a.getDimension(
                R.styleable.CalculatorEditText_minTextSize, getTextSize());
        mStepTextSize = a.getDimension(R.styleable.CalculatorEditText_stepTextSize,
                (mMaximumTextSize - mMinimumTextSize) / 3);

        a.recycle();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, mMaximumTextSize);
        setMinHeight(getLineHeight() + getCompoundPaddingBottom() + getCompoundPaddingTop());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            cancelLongPress();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidthConstraint =
                MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getVariableTextSize(getText().toString()));
    }

    @Override
    public Parcelable onSaveInstanceState() {
        super.onSaveInstanceState();
        return null;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);

        final int textLength = text.length();
        if (getSelectionStart() != textLength || getSelectionEnd() != textLength) {
            setSelection(textLength);
        }
        setTextSize(TypedValue.COMPLEX_UNIT_PX, getVariableTextSize(text.toString()));
    }

    @Override
    public void setTextSize(int unit, float size) {
        final float oldTextSize = getTextSize();
        super.setTextSize(unit, size);

        if (mOnTextSizeChangeListener != null && getTextSize() != oldTextSize) {
            mOnTextSizeChangeListener.onTextSizeChanged(this, oldTextSize);
        }
    }

    public void setOnTextSizeChangeListener(OnTextSizeChangeListener listener) {
        mOnTextSizeChangeListener = listener;
    }

    public float getVariableTextSize(String text) {
        if (mWidthConstraint < 0 || mMaximumTextSize <= mMinimumTextSize) {
            return getTextSize();
        }

        mTempPaint.set(getPaint());

        float lastFitTextSize = mMinimumTextSize;
        while (lastFitTextSize < mMaximumTextSize) {
            final float nextSize = Math.min(lastFitTextSize + mStepTextSize, mMaximumTextSize);
            mTempPaint.setTextSize(nextSize);
            if (mTempPaint.measureText(text) > mWidthConstraint) {
                break;
            } else {
                lastFitTextSize = nextSize;
            }
        }

        return lastFitTextSize;
    }

    @Override
    public int getCompoundPaddingTop() {
        getPaint().getTextBounds("H", 0, 1, mTempRect);

        final FontMetricsInt fontMetrics = getPaint().getFontMetricsInt();
        final int paddingOffset = -(fontMetrics.ascent + mTempRect.height());
        return super.getCompoundPaddingTop() - Math.min(getPaddingTop(), paddingOffset);
    }

    @Override
    public int getCompoundPaddingBottom() {
        final FontMetricsInt fontMetrics = getPaint().getFontMetricsInt();
        return super.getCompoundPaddingBottom() - Math.min(getPaddingBottom(), fontMetrics.descent);
    }

    public interface OnTextSizeChangeListener {
        void onTextSizeChanged(TextView textView, float oldSize);
    }
}
