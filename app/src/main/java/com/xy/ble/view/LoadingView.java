package com.xy.ble.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.xy.ble.R;

/**
 * Created by Administrator on 2018/2/28.
 */

public class LoadingView extends View {
    private static final String LOG_TAG = LoadingView.class.getSimpleName();

    private int default_width = 300;

    private int default_height = 300;

    private int start_angle_one = 20;

    private int start_angle_two = 60;

    private int start_angle_three = 105;

    private int offset;

    private Paint paint_light;

    private Paint paint_white;

    private Bitmap bitmap_icon;

    private MyRotateAnimation myRotateAnimation;

    public LoadingView(Context context) {
        super(context);
        initData();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        paint_light = new Paint();
        paint_light.setColor(getResources().getColor(R.color.light_white, null));
        paint_light.setStrokeWidth(1.0f);
        paint_light.setStyle(Paint.Style.STROKE);
        paint_light.setAntiAlias(true);

        paint_white = new Paint();
        paint_white.setColor(getResources().getColor(R.color.white, null));
        paint_white.setStrokeWidth(6.0f);
        paint_white.setStyle(Paint.Style.STROKE);
        paint_white.setAntiAlias(true);
        paint_white.setStrokeCap(Paint.Cap.ROUND);

        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap_icon = BitmapFactory.decodeResource(getResources(), R.mipmap.bluetooth_icon_press, options);

        myRotateAnimation = new MyRotateAnimation();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getSize(default_width, widthMeasureSpec);
        int height = getSize(default_height, widthMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(bitmap_icon, (getMeasuredWidth() / 2 - bitmap_icon.getWidth() / 2), (getMeasuredHeight() / 2 - bitmap_icon.getHeight() / 2), paint_light);

        int length = (getMeasuredWidth() / 2 - bitmap_icon.getWidth() / 2);

        int r1 = bitmap_icon.getWidth() / 2 + length / 3;
        int r2 = bitmap_icon.getWidth() / 2 + (length * 2 / 3);
        int r3 = bitmap_icon.getWidth() / 2 + length - 10;

        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r1, paint_light);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r2, paint_light);
        canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, r3, paint_light);


        int value = calValue(offset, 140) / 2;

        canvas.drawArc(getMeasuredWidth() / 2 - r1, getMeasuredHeight() / 2 - r1, getMeasuredWidth() / 2 + r1, getMeasuredHeight() / 2 + r1, start_angle_one, 150, false, paint_white);
        canvas.drawArc(getMeasuredWidth() / 2 - r2, getMeasuredHeight() / 2 - r2, getMeasuredWidth() / 2 + r2, getMeasuredHeight() / 2 + r2, start_angle_two, 60 + value, false, paint_white);
        canvas.drawArc(getMeasuredWidth() / 2 - r2, getMeasuredHeight() / 2 - r2, getMeasuredWidth() / 2 + r2, getMeasuredHeight() / 2 + r2, start_angle_two + 180, 90 - value, false, paint_white);
        canvas.drawArc(getMeasuredWidth() / 2 - r3, getMeasuredHeight() / 2 - r3, getMeasuredWidth() / 2 + r3, getMeasuredHeight() / 2 + r3, start_angle_three, 80 - value, false, paint_white);
        canvas.drawArc(getMeasuredWidth() / 2 - r3, getMeasuredHeight() / 2 - r3, getMeasuredWidth() / 2 + r3, getMeasuredHeight() / 2 + r3, start_angle_three + 180, 40 + value, false, paint_white);

    }

    private int getSize(int defaultSize, int measureSpec) {
        int real_size = defaultSize;
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);

        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                real_size = defaultSize;
                break;
            case MeasureSpec.AT_MOST:
                real_size = size;
                break;
            case MeasureSpec.EXACTLY:
                real_size = size;
                break;
        }
        return real_size;
    }

    public void update() {
        offset = 0;
        this.startAnimation(myRotateAnimation);
    }

    private int calValue(int total, int offset) {
        int real_value;

        int z = (total / offset) % 2;

        real_value = (offset - total % offset) * z + (1 - z) * (total % offset);

        return real_value;
    }

    private class MyRotateAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            offset = offset + 2;
            int value = calValue(offset, 140);
            start_angle_one = 20 - value;
            start_angle_two = 60 + value;
            start_angle_three = 105 - value;
            invalidate();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(15000);
            setFillAfter(true);
        }
    }
}
