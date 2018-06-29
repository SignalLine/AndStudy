package cn.com.single.andstudy.pintu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.com.single.andstudy.R;

/**
 * @author li
 *         Create on 2018/6/26.
 * @Description
 *      自定义控件选择，九宫格，RelativeLayout id+rule
 *      切图
 *      动画图层
 *      pause  resume  restart
 *      时间  mhandler实现  sendMessageDelayed()
 *
 */

public class GamePintuLayout extends RelativeLayout implements View.OnClickListener {

    private int mColumn = 3;
    /**
     * 容器的内边距
     */
    private int mPadding;
    /**
     * 每张小图之间的距离（横纵）
     */
    private int mMargin = 3;

    private ImageView[] mGamePintuItems;


    private int mItemWidth;
    /**
     * 游戏图片
     */
    private Bitmap mBitmap;

    private List<ImagePiece> mItemBitmaps;

    private boolean once;
    /**
     * 游戏容器的宽度
     */
    private int mWidth;

    private ImageView mFirst;
    private ImageView mSecond;

    /**
     * 动画层
     */
    private RelativeLayout mAnimLayout;
    private boolean isAniming;

    private boolean isGameSuccess;
    private boolean isGameOver;

    private int level = 1;

    public interface GamePintuListener{
        void nextLevel(int nextLevel);
        void timeChanged(int currentTime);
        void gameOver(int level);
    }
    private GamePintuListener mGamePintuListener;
    public void setOnGamePintuListener(GamePintuListener gamePintuListener){
        mGamePintuListener = gamePintuListener;
    }

    private static final int TIME_CHANGED = 0x110;
    private static final int NEXT_LEVEL = 0X111;

    private boolean isTimeEnabled = false;
    private int mTime;
    /**
     * 设置是否开启时间
     * @param timeEnabled
     */
    public void setTimeEnabled(boolean timeEnabled) {
        isTimeEnabled = timeEnabled;
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_CHANGED:

                    if(isGameSuccess || isGameOver || isPause){
                        return;
                    }

                    if(mGamePintuListener != null){
                        mGamePintuListener.timeChanged(mTime);
                    }

                    if(mTime <= 0){
                        isGameOver = true;
                        if (mGamePintuListener != null) {
                            mGamePintuListener.gameOver(level);
                        }
                        return;
                    }

                    mTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED,1000);

                    break;
                case NEXT_LEVEL:

                    level = level + 1;
                    if(mGamePintuListener != null){
                        mGamePintuListener.nextLevel(level);
                    }else {
                        nextLevel(-1);
                    }

                    break;
                default:
                    break;
            }
        }
    };

    public GamePintuLayout(Context context) {
        this(context,null);
    }

    public GamePintuLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public GamePintuLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        mMargin = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,3,getResources().getDisplayMetrics());
        mPadding = min(getPaddingLeft(),getPaddingRight(),getPaddingTop(),getPaddingBottom());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //取最小值
        mWidth = Math.min(getMeasuredWidth(),getMeasuredHeight());
        if(!once){
            //进行切图，排序
            initBitmap();
            //设置ImageView（Item）的宽高等属性
            initItem();

            //判断是否开启时间
            checkTimeEnable();

            once = true;
        }
        //设置为正方形
        setMeasuredDimension(mWidth,mWidth);
    }

    private void checkTimeEnable() {
        if(isTimeEnabled){
            //根据当前等级设置时间
            countTimeBaseLevel();

            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    private void countTimeBaseLevel() {

        mTime = (int) Math.pow(2,level) * 60;
    }

    /**
     * 进行切图，排序
     */
    private void initBitmap() {
        if(mBitmap == null){
            mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        }

        mItemBitmaps = ImageSplitterUtil.splitImage(mBitmap,mColumn);
        //乱序
        Collections.sort(mItemBitmaps, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece o1, ImagePiece o2) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置ImageView（Item）的宽高等属性
     */
    private void initItem() {
        mItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;

        mGamePintuItems = new ImageView[mColumn * mColumn];
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView item = new ImageView(getContext());

            item.setOnClickListener(this);
            item.setImageBitmap(mItemBitmaps.get(i).getBitmap());

            mGamePintuItems[i] = item;

            item.setId(i + 1);
            //在item的tag中存储index
            item.setTag(i + "_" + mItemBitmaps.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new LayoutParams(mItemWidth,mItemWidth);

            //设置Item间横向间隙，通过rightMargin
            //不是最后一列
            if((i + 1) % mColumn != 0){
                lp.rightMargin = mMargin;
            }
            //不是第一列
            if(i % mColumn != 0){
                lp.addRule(RelativeLayout.RIGHT_OF,mGamePintuItems[i-1].getId());
            }

            //如果不是第一行,设置topMargin和rule
            if((i + 1) > mColumn){
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW,mGamePintuItems[i - mColumn].getId());
            }

            addView(item,lp);
        }
    }

    public void nextLevel(int level){
        this.removeAllViews();
        mAnimLayout = null;

        if(level > 0){
            mColumn = level;
        }

        mColumn++;
        isGameSuccess = false;
        isGameOver = false;

        checkTimeEnable();

        initBitmap();
        initItem();
    }

    /**
     * 重新开始
     */
    public void restart(){
        isGameOver = false;
        mColumn--;
        nextLevel(-1);
    }

    private boolean isPause;

    /**
     * 暂停
     */
    public void pause(){
        isPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }

    /**
     * 恢复
     */
    public void resume(){
        if(isPause){
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }


    /**
     * 获取多个参数的最小值
     *
     * @param params
     * @return
     */
    private int min(int... params) {

        int min = params[0];
        if(params.length > 0){
            for (int param : params) {
                if (param < min) {
                    min = param;
                }
            }
        }

        return min;
    }



    @Override
    public void onClick(View v) {

        if(isAniming){
            return;
        }

        //点击同一个
        if(mFirst == v){
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }

        if(mFirst == null){
            mFirst = (ImageView) v;

            mFirst.setColorFilter(Color.parseColor("#55FF0000"));
        }else {
            mSecond = (ImageView) v;
            //交换我们的item
            exchangeView();
        }
    }


    /**
     * 交换我们的item
     */
    private void exchangeView() {
        mFirst.setColorFilter(null);
        //构造动画层
        setUpAnimLayout();

        String firstTag = (String) mFirst.getTag();
        String secondTag = (String) mSecond.getTag();

        ImageView first = new ImageView(getContext());
        final Bitmap firstBitmap = mItemBitmaps.get(getImageIdByTag(firstTag)).getBitmap();
        first.setImageBitmap(firstBitmap);
        RelativeLayout.LayoutParams lp = new LayoutParams(mItemWidth,mItemWidth);
        lp.leftMargin = mFirst.getLeft() - mPadding;
        lp.topMargin = mFirst.getTop() - mPadding;
        first.setLayoutParams(lp);
        mAnimLayout.addView(first);

        ImageView second = new ImageView(getContext());
        final Bitmap secondBitmap = mItemBitmaps.get(getImageIdByTag(secondTag)).getBitmap();
        second.setImageBitmap(firstBitmap);
        RelativeLayout.LayoutParams lp2 = new LayoutParams(mItemWidth,mItemWidth);
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        lp2.topMargin = mSecond.getTop() - mPadding;
        second.setLayoutParams(lp2);
        mAnimLayout.addView(second);

        //设置动画
        TranslateAnimation anim = new TranslateAnimation(0,mSecond.getLeft()
                - mFirst.getLeft(), 0,mSecond.getTop() - mFirst.getTop());
        anim.setDuration(300);
        anim.setFillAfter(true);
        first.startAnimation(anim);

        TranslateAnimation anim2 = new TranslateAnimation(0,-mSecond.getLeft()
                + mFirst.getLeft(), 0,-mSecond.getTop() + mFirst.getTop());
        anim2.setDuration(300);
        anim2.setFillAfter(true);
        second.startAnimation(anim2);

        //动画监听
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

                isAniming = true;
                if(mFirst != null){
                    mFirst.setVisibility(INVISIBLE);
                }
                if(mSecond != null){
                    mSecond.setVisibility(INVISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {


                String firstTag = (String) mFirst.getTag();
                String secondTag = (String) mSecond.getTag();

                mFirst.setImageBitmap(secondBitmap);
                mSecond.setImageBitmap(firstBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(VISIBLE);
                mSecond.setVisibility(VISIBLE);

                mFirst = mSecond = null;

                mAnimLayout.removeAllViews();
                //判断用户游戏是否成功
                checkSuccess();

                isAniming = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    /**
     * 判断游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;
        for (int i = 0; i < mGamePintuItems.length; i++) {
            ImageView imageView = mGamePintuItems[i];
            if(getImageIndex((String) imageView.getTag()) != i){
                isSuccess = false;
            }
        }


        if(isSuccess){
            isGameSuccess = true;
            mHandler.removeMessages(TIME_CHANGED);

            Log.d("TAG","success");
            Toast.makeText(getContext(), "Success,level up!", Toast.LENGTH_SHORT).show();

            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }

    public int getImageIdByTag(String tag){
        String[] split = tag.split("_");
        return Integer.parseInt(split[0]);
    }

    public int getImageIndex(String tag){
        String[] split = tag.split("_");
        return Integer.parseInt(split[1]);
    }

    /**
     * 构造动画层
     */
    private void setUpAnimLayout() {
        if(mAnimLayout == null){
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }
    }
}
