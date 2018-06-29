package cn.com.single.andstudy;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * @author li
 *         Create on 2018/6/13.
 * @Description
 *      1.attr.xml
 *      2.布局文件使用
 *      3.构造方法中获取自定义属性
 *      4.onMeasure
 *      5.onDraw
 */

public class ChangeColorIconWithText extends View{

    private int color = 0Xff45c01a;
    private Bitmap mIconBitmap;
    private String text = "微信";
    private int textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12
            ,getResources().getDisplayMetrics());

    private Canvas mCanvas;
    private Bitmap mBitmap;
    private Paint mPaint;

    private float mAlpha = 0.0f;
    private Rect mIconRect;
    private Rect mTextBound;

    private Paint mTextPaint;

    public ChangeColorIconWithText(Context context) {
        this(context,null);
    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ChangeColorIconWithText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.ChangeColorIconWithText);
        int n = a.getIndexCount();
        for (int i = 0; i < n; i++) {
            int attr = a.getIndex(i);
            switch (attr) {
                case R.styleable.ChangeColorIconWithText_icon:
                    BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                    mIconBitmap = drawable.getBitmap();
                    break;
                case R.styleable.ChangeColorIconWithText_text:
                    text = a.getString(attr);
                    break;
                case R.styleable.ChangeColorIconWithText_color:
                    color = a.getColor(attr,0Xff45c01a);
                    break;
                case R.styleable.ChangeColorIconWithText_text_size:
                    textSize = (int) a.getDimension(attr,TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12
                            ,getResources().getDisplayMetrics()));
                    break;
                default:
                    break;
            }
        }

        a.recycle();

        mTextBound = new Rect();
        mTextPaint = new Paint();
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xff555555);

        mTextPaint.getTextBounds(text,0,text.length(),mTextBound);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int iconWidth = Math.min(getMeasuredWidth()-getPaddingLeft()-getPaddingRight(),
                getMeasuredHeight() - getPaddingTop() - getPaddingBottom()-mTextBound.height());

        int left = getMeasuredWidth() / 2 - iconWidth / 2;
        int top = (getMeasuredHeight() - mTextBound.height()) / 2 - iconWidth / 2;

        mIconRect = new Rect(left,top,left + iconWidth,top + iconWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mIconBitmap,null,mIconRect,null);

        //内存去准备bitmap，setAlpha，纯色， xfermode，图标
        int alpha = (int) Math.ceil(255 * mAlpha);

        setupTargetBitmap(alpha);

        //绘制原文本，绘制变色的文本
        drawSourceText(canvas,alpha);

        drawTargetText(canvas,alpha);

        canvas.drawBitmap(mBitmap,0,0,null);

    }

    /**
     * 绘制变色的文本
     * @param canvas
     * @param alpha
     */
    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setColor(color);

        mTextPaint.setAlpha(alpha);
        int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(text, x,y,mTextPaint);
    }

    /**
     * 绘制原文本
     *
     * @param canvas
     * @param alpha
     */
    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setColor(0xff333333);
        mTextPaint.setAlpha((255 - alpha));

        int x = getMeasuredWidth() / 2 - mTextBound.width() / 2;
        int y = mIconRect.bottom + mTextBound.height();
        canvas.drawText(text, x,y,mTextPaint);
    }

    /**
     * 在内存中UI之可变色的icon
     */
    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(),getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPaint = new Paint();
        mPaint.setColor(color);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);

        mCanvas.drawRect(mIconRect,mPaint);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap,null,mIconRect,mPaint);
    }

    public void setIconAlpha(float alpha){
        this.mAlpha = alpha;
        invalidateView();
    }

    /**
     * 重绘
     */
    private void invalidateView() {
        if(Looper.getMainLooper() == Looper.myLooper()){
            invalidate();
        }else {
            postInvalidate();
        }
    }

    private static final String INSTANCE_STATUS = "instance_status";
    private static final String STATUS_ALPHA = "status_alpha";

    /**
     * 防止被回收
     * @return
     */
    @Override
    protected Parcelable onSaveInstanceState() {

        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATUS,super.onSaveInstanceState());
        bundle.putFloat(STATUS_ALPHA,mAlpha);

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        if(state instanceof Bundle){
            Bundle bundle = (Bundle) state;

            mAlpha = bundle.getFloat(STATUS_ALPHA);

            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATUS));
            return;
        }

        super.onRestoreInstanceState(state);

    }
}
