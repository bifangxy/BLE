package com.xy.ble.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import com.xy.ble.R;
import com.xy.ble.data.BleDevice;

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

    private int open_count;

    private Paint paint_light;

    private Paint paint_white;

    private Bitmap bitmap_icon_light;

    private Bitmap biemap_icon_dark;

    private boolean isOpen;

    private OpeningAnimation openingAnimation;

    private SearchingAnimation searchingAnimation;

    private ConnectingAnimation connectingAnimation;

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
        isOpen = false;

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
        bitmap_icon_light = BitmapFactory.decodeResource(getResources(), R.mipmap.bluetooth_icon_press, options);
        biemap_icon_dark = BitmapFactory.decodeResource(getResources(), R.mipmap.bluetooth_icon_normal, options);

        searchingAnimation = new SearchingAnimation();
        openingAnimation = new OpeningAnimation();
        connectingAnimation = new ConnectingAnimation();
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

        if (isOpen) {
            canvas.drawBitmap(bitmap_icon_light, (getMeasuredWidth() / 2 - bitmap_icon_light.getWidth() / 2), (getMeasuredHeight() / 2 - bitmap_icon_light.getHeight() / 2), paint_light);
        } else {
            canvas.drawBitmap(biemap_icon_dark, (getMeasuredWidth() / 2 - biemap_icon_dark.getWidth() / 2), (getMeasuredHeight() / 2 - biemap_icon_dark.getHeight() / 2), paint_light);
        }

        int length = (getMeasuredWidth() / 2 - bitmap_icon_light.getWidth() / 2 - 10);

        int r1 = bitmap_icon_light.getWidth() / 2 + length / 3;
        int r2 = bitmap_icon_light.getWidth() / 2 + (length * 2 / 3);
        int r3 = bitmap_icon_light.getWidth() / 2 + length;

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

    public void startOpeningAnimation() {
        open_count = 0;
        this.startAnimation(openingAnimation);
    }

    public void stopOpeningAnimation() {
        openingAnimation.cancel();
        isOpen = true;
        invalidate();
    }

    public void startSearchingAnimation() {
        offset = 0;
        this.startAnimation(searchingAnimation);
    }

    public void startConnectingAnimation() {
        offset = 0;
        this.startAnimation(connectingAnimation);
    }

    public void stopConnectingAnimation() {
        connectingAnimation.cancel();
    }

    public void setOpen(boolean open) {
        isOpen = open;
        invalidate();
    }


    private int calValue(int total, int offset) {
        int real_value;

        int z = (total / offset) % 2;

        real_value = (offset - total % offset) * z + (1 - z) * (total % offset);

        return real_value;
    }


    private class OpeningAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            open_count++;
            if (open_count % 3 == 0) {
                isOpen = !isOpen;
                invalidate();
            }
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(5000);
            setStartOffset(500);
            setFillAfter(true);
        }
    }

    private class SearchingAnimation extends Animation {
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
            setDuration(10000);
            setFillAfter(true);
        }
    }


    private class ConnectingAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            offset = offset + 2;
            int value = calValue(offset, 140);
            start_angle_three = 105 - value;
            invalidate();
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(30000);
            setFillAfter(true);
        }
    }


}
