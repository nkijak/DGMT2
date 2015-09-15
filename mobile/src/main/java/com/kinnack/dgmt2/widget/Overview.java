package com.kinnack.dgmt2.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class Overview extends View {
    public Overview(Context context_, AttributeSet attrs_, int defStyle_) {
        super(context_, attrs_, defStyle_);
        setup();
    }

    public Overview(Context context_, AttributeSet attrs_) {
        super(context_, attrs_);
        setup();
    }

    private void setup() {
        setMinimumHeight(150);
        setMinimumWidth(300);
        _paint = new Paint();
        _paint.setColor(_color);
        _counts = new ArrayList<Integer>();
    }

    private int _color = 0xff479e00;
    private Paint _paint;
    private List<Integer> _counts;
    private int _maxCountForSet;
    private int _maxCount = -1;

    private Map<String, Integer> _buckets;

    public void setBuckets(TreeMap<String, Integer> buckets) {
        _buckets = buckets;

        _counts.clear();
        _counts.addAll(buckets.values());

        _maxCount = Collections.max(buckets.values());

        invalidate();
    }

//    public void setExercseSet(ExerciseSet exercseSet_) {
//        _exercseSet = exercseSet_;
//
//        _counts.clear();
//        _maxCountForSet = Integer.MIN_VALUE;
//        if (_exercseSet == null) return;
//        for(int count : _exercseSet.getCounts()) {
//
//            if (count > _maxCountForSet) _maxCountForSet = count;
//            _counts.add(count);
//        }
//
//        invalidate();
//    }

    public int getColor() {
        return _color;
    }

    public void setColor(int color_) {
        _color = color_;

        _paint.setColor(_color);
    }

    public void setMaxX(int maxX) {
        _maxCount = maxX;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec_, int heightMeasureSpec_) {
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec_);
        setMeasuredDimension(Math.max(parentWidth, getSuggestedMinimumWidth()), getSuggestedMinimumHeight());
    }

    @Override
    protected void onDraw(Canvas canvas_) {


        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(0xffcccccc);

        Paint highlightPaint = new Paint();
        highlightPaint.setColor(0x88397f00);
        highlightPaint.setStrokeWidth(2);

        int drawingHeight = getHeight() - getPaddingTop()+getPaddingBottom();
        int drawingWidth = getWidth() - getPaddingRight()+getPaddingLeft();

        canvas_.drawRect(0, 0, drawingWidth, drawingHeight, backgroundPaint);


        float baseY = (float)Math.floor(drawingHeight*0.95);
        canvas_.drawLine(5, baseY, drawingWidth-5,baseY, highlightPaint);
        canvas_.drawLine(drawingWidth-5, baseY, drawingWidth-5, 0, highlightPaint);

        if (_buckets == null) return;

        int x = (int)Math.floor(drawingWidth*0.05)+10;
        int chartWidth = (int)Math.floor(drawingWidth*0.95);
        int gap = (int)Math.floor(chartWidth*0.9/_counts.size()+1);
        _paint.setStrokeWidth(40);
        float heightPercent = 95.0f;
        Log.d("SetOverviewChart", "Drawing chart...");
        float min = 1000.0f;
        float prevMin = 1000.0f;
        int minCount = 1000;
        int startMinLine = 10000;

        float max = -1000.0f;
        float prevMax = -1000.0f;
        int maxCount = -1000;
        int startMaxLine = 10000;

        float colHeight = 0f;

        for (Integer count : _counts) {
            heightPercent = 1.0f - count*1.0f/_maxCount;
            colHeight = (drawingHeight*heightPercent);
            prevMax = max;
            max = Math.max(max, colHeight);
            maxCount = Math.max(maxCount, count);
            if (prevMax != max) startMaxLine = x;

            prevMin = min;
            min = Math.min(min, colHeight);
            minCount = Math.min(minCount, count);
            if (prevMin != min) startMinLine = x;
            Log.d("SetOverviewChart", "Drawing "+count+" at "+heightPercent+"% of "+_maxCount+" from "+baseY+" to "+(drawingHeight*heightPercent));
            canvas_.drawLine(x, baseY, x, colHeight, _paint);
            x += gap;
        }

        highlightPaint.setStrokeWidth(1f);
        highlightPaint.setTextSize(20.0f);
        canvas_.drawLine(startMinLine, min, drawingWidth-5,min, highlightPaint);
        canvas_.drawText(""+maxCount, chartWidth, min+22, highlightPaint);

        canvas_.drawLine(startMaxLine, max, drawingWidth-5,max, highlightPaint);
        canvas_.drawText(""+minCount, chartWidth, max+22, highlightPaint);

    }

}
