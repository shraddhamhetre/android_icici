package com.youtility.intelliwiz20.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by PrashantD on 28/11/17.
 */

public class SignatureView extends View {

    private Canvas m_Canvas;

    private Path m_Path;

    private Paint m_Paint;

    private ArrayList<Pair<Path, Paint>> paths = new ArrayList<>();

    private ArrayList<Pair<Path, Paint>> undonePaths = new ArrayList<>();

    private float mX, mY;

    private static final float TOUCH_TOLERANCE = 4;

    private boolean isEraserActive = false;

    public SignatureView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        onCanvasInitialization();
    }

    public void onCanvasInitialization() {

        m_Paint = new Paint();
        m_Paint.setAntiAlias(true);
        m_Paint.setDither(true);
        m_Paint.setColor(Color.parseColor("#000000"));
        m_Paint.setStyle(Paint.Style.STROKE);
        m_Paint.setStrokeJoin(Paint.Join.ROUND);
        m_Paint.setStrokeCap(Paint.Cap.ROUND);
        m_Paint.setStrokeWidth(2);

        m_Canvas = new Canvas();
        m_Path = new Path();
        Paint newPaint = new Paint(m_Paint);
        paths.add(new Pair<>(m_Path, newPaint));


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touch_move(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (Pair<Path, Paint> p : paths) {
            canvas.drawPath(p.first, p.second);
        }
    }

    private void touch_start(float x, float y) {

        if (isEraserActive) {
            m_Paint.setColor(Color.WHITE);
            m_Paint.setStrokeWidth(10);
            Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
            paths.add(new Pair<>(m_Path, newPaint));

        } else {
            m_Paint.setColor(Color.BLACK);
            m_Paint.setStrokeWidth(2);
            Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
            paths.add(new Pair<>(m_Path, newPaint));

        }


        m_Path.reset();
        m_Path.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touch_move(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            m_Path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touch_up() {
        m_Path.lineTo(mX, mY);

        // commit the path to our offscreen
        m_Canvas.drawPath(m_Path, m_Paint);

        // kill this so we don't double draw
        m_Path = new Path();
        Paint newPaint = new Paint(m_Paint); // Clones the mPaint object
        paths.add(new Pair<>(m_Path, newPaint));
    }

    public void activateEraser()
    {
        isEraserActive = true;
    }


    public void deactivateEraser()
    {
        isEraserActive = false;
    }

    public boolean isEraserActive()
    {
        return isEraserActive;
    }


    public void reset()
    {
        paths.clear();
        invalidate();
    }
}
