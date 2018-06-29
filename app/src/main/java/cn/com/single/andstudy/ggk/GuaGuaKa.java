package cn.com.single.andstudy.ggk;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import cn.com.single.andstudy.R;

/**
 * @author li
 *         Create on 2018/6/29.
 * @Description
 *      刮刮卡
 */

public class GuaGuaKa extends View {

    private Paint mOuterPaint;
    private Path mPath;
    private Canvas mCanvas;
    private Bitmap mBitmap;

    private int mLastX;
    private int mLastY;
    private Bitmap bitmap;

    private Bitmap mOuterBitmap;

    //---------------------------

    private String mText = "谢谢惠顾";
    private Paint mBackPaint;
    /**
     * 记录刮刮卡信息文本的宽高
     */
    private Rect mTextBound;
    private int mTextSize = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
            ,22,getResources().getDisplayMetrics());
    private int mTextColor = 0x000000;
    /**
     * 判断遮盖区域示范消除达到区域阈值60
     */
    private volatile boolean mComplete = false;

    public interface OnGGKCompleteListener{
        void complete();
    }
    private OnGGKCompleteListener mCompleteListener;

    public void setCompleteListener(OnGGKCompleteListener completeListener) {
        mCompleteListener = completeListener;
    }

    public GuaGuaKa(Context context) {
        this(context,null);
    }

    public GuaGuaKa(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GuaGuaKa(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme()
                .obtainStyledAttributes(attrs,R.styleable.GuaGuaKa,defStyleAttr,0);
        mText = a.getString(R.styleable.GuaGuaKa_ggk_text);
        mTextSize = (int) a.getDimension(R.styleable.GuaGuaKa_ggk_textSize
                , TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP
                        ,22,getResources().getDisplayMetrics()));
        mTextColor = a.getColor(R.styleable.GuaGuaKa_ggk_textColor,0x000000);

        a.recycle();

        init();
    }

    private void init() {
        mOuterPaint = new Paint();
        mPath = new Path();

        mOuterBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.fg_guaguaka);

//        mText = "谢谢惠顾";
        mTextBound = new Rect();
        mBackPaint = new Paint();

//        mTextSize = 30;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        //初始化画板
        mBitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        setupOutPaint();
        setupBackPaint();

        //设置图层
        mCanvas.drawColor(Color.parseColor("#c0c0c0"));
        mCanvas.drawRoundRect(new RectF(0,0,width,height),30,30,mOuterPaint);

        mCanvas.drawBitmap(mOuterBitmap,null,new Rect(0,0,width,height),null);

    }

    /**
     * 设置获奖信息的画笔
     */
    private void setupBackPaint() {
        mBackPaint.setColor(mTextColor);
        mBackPaint.setStyle(Paint.Style.FILL);
        mBackPaint.setTextSize(mTextSize);
        //获得当前画笔绘制文本的宽高
        mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);

    }

    private void setupOutPaint() {
        //设置绘制path画笔的属性
        mOuterPaint.setColor(Color.parseColor("#c0c0c0"));
        mOuterPaint.setAntiAlias(true);
        mOuterPaint.setDither(true);
        //圆角
        mOuterPaint.setStrokeJoin(Paint.Join.ROUND);
        mOuterPaint.setStrokeCap(Paint.Cap.ROUND);
        mOuterPaint.setStyle(Paint.Style.FILL);

        mOuterPaint.setStrokeWidth(20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:

                mLastX = x;
                mLastY = y;
                mPath.moveTo(mLastX,mLastY);

                break;
            case MotionEvent.ACTION_MOVE:

                int dx = Math.abs(x - mLastX);
                int dy = Math.abs(y - mLastY);
                if(dx > 3 || dy > 3){
                    mPath.lineTo(x,y);
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:

                new Thread(mRunnable).start();

                break;
            default:
                break;
        }

        invalidate();

        return true;
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int w = getWidth();
            int h = getHeight();

            float wipeArea = 0;
            float totalArea = w * h;
            Bitmap bitmap = mBitmap;

            int[] mPixels = new int[w * h];
            //获得bitmap上的所有像素信息
            bitmap.getPixels(mPixels,0,w,0,0,w,h);

            for (int i = 0; i < w; i++) {
                for (int j = 0; j < h; j++) {
                    int index = i + j * w;
                    if(mPixels[index] == 0){
                        wipeArea++;
                    }
                }
            }

            if(wipeArea > 0 && totalArea > 0){
                int percent = (int)(wipeArea * 100 / totalArea);

                if(percent > 60){
                    //清除图层区域
                    mComplete = true;
                    postInvalidate();
                }
            }
        }
    };

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);


//        canvas.drawBitmap(bitmap,0,0,null);

        canvas.drawText(mText,getWidth() / 2 - mTextBound.width() / 2
                ,getHeight() / 2 + mTextBound.height() / 2,mBackPaint);

        if(mComplete){
            if(mCompleteListener != null){
                mCompleteListener.complete();
            }
        }

        if(!mComplete){
            drawPath();

            canvas.drawBitmap(mBitmap,0,0,null);
        }
    }

    private void drawPath() {
        mOuterPaint.setStyle(Paint.Style.STROKE);
        mOuterPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));

        mCanvas.drawPath(mPath,mOuterPaint);
    }

    public void setText(String text) {
        mText = text;
        //获得当前画笔绘制文本的宽高
        mBackPaint.getTextBounds(mText,0,mText.length(),mTextBound);
    }

    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }
}
