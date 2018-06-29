package cn.com.single.andstudy;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author li
 *         Create on 2018/6/20.
 * @Description
 */

public class ReFlashListView extends ListView implements AbsListView.OnScrollListener{

    private View header;

    int headerHeight;//顶部布局文件高度

    int firstVisibleItem;//当前可见第一个item位置
    int scrollState;//listview当前滚动状态
    boolean isRemark;//标记 当前在listView最顶端摁下
    int startY;//摁下时的Y值

    int state;//当前的状态
    final int NONE = 0;//正常状态
    final int PULL = 1;//提示下拉刷新
    final int RELESE = 2;//松开释放
    final int REFLASHING = 3;//刷新状态

    IReflashListener mReflashListener;

    private View footer;
    int totalItemCount;//总数量
    int lastVisibleItem;//最后一个可见item
    boolean isLoading;//正在加载


    public ReFlashListView(Context context) {
        this(context,null);
    }

    public ReFlashListView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public ReFlashListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        initView(context);
    }

    /**
     * 初始化界面 添加顶部布局文件到listView
     * @param context
     */
    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        header = inflater.inflate(R.layout.header_layout,null);

        measureView(header);

        headerHeight = header.getMeasuredHeight();
        //
        Log.i("tag","headerHeight--->" + headerHeight);

        topPadding(-headerHeight);
        this.addHeaderView(header);


        footer = inflater.inflate(R.layout.footer_layout,null);
        //初始先隐藏
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
        this.addFooterView(footer);

        this.setOnScrollListener(this);

    }

    /**
     * 通知父布局  占用的宽度和高度
     * @param view
     */
    private void measureView(View view){
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if(p == null){
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                                    , ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0,0,p.width);
        int height;
        int tempHeight = p.height;
        if(tempHeight > 0){
            height = MeasureSpec.makeMeasureSpec(tempHeight,MeasureSpec.EXACTLY);
        }else {
            height = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
        }

        view.measure(width,height);

    }

    /**
     * 设置header布局的上边距
     *
     * @param top
     */
    private void topPadding(int top){
        header.setPadding(header.getLeft(),top,header.getRight(),header.getBottom());

        header.invalidate();
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        this.scrollState = scrollState;

        if(totalItemCount == lastVisibleItem && scrollState == SCROLL_STATE_IDLE){
            if(!isLoading){
                isLoading = true;
                //加载更多数据
                footer.findViewById(R.id.load_layout).setVisibility(VISIBLE);

                mReflashListener.onLoad();
            }

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        this.firstVisibleItem = firstVisibleItem;

        this.lastVisibleItem = firstVisibleItem + visibleItemCount;
        this.totalItemCount = totalItemCount;

    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:

                if(firstVisibleItem == 0){
                    isRemark = true;
                    startY = (int) ev.getY();
                }

                break;
            case MotionEvent.ACTION_MOVE:

                onMove(ev);

                break;
            case MotionEvent.ACTION_UP:

                if(RELESE == state){
                    state = REFLASHING;
                    //加载最新数据
                    reflashViewByState();

                    mReflashListener.onReflash();

                }else if(state == PULL){
                    state = NONE;
                    isRemark = false;

                    reflashViewByState();
                }

                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 判断移动过程中的操作
     * @param ev
     */
    private void onMove(MotionEvent ev){
        if(!isRemark){
            return;
        }

        int tempY = (int) ev.getY();
        //移动的距离
        int space = tempY - startY;
        int topPadding = space - headerHeight;
        switch (state) {
            case NONE:

//                reflashComplete();

                if(space > 0){
                    state = PULL;

                    reflashViewByState();
                }

                break;
            case PULL:

                topPadding(topPadding);

                if(space > headerHeight + 30
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL){
                    state = RELESE;
                    reflashViewByState();
                }
                break;
            case RELESE:

                topPadding(topPadding);

                if(space < headerHeight + 30){
                    state = PULL;
                    reflashViewByState();
                }else if(space <= 0){
                    state = NONE;
                    isRemark = false;
                    reflashViewByState();
                }

                break;
            case REFLASHING:
                break;
            default:
                break;
        }
    }

    /**
     * 根据当前状态改变页面显示
     */
    private void reflashViewByState(){

        TextView tip = (TextView) header.findViewById(R.id.tip);

        ImageView arrow = (ImageView) header.findViewById(R.id.arrow);
        ProgressBar progress = (ProgressBar) header.findViewById(R.id.progress);

        RotateAnimation anim = new RotateAnimation(0,180
                        , Animation.RELATIVE_TO_SELF,0.5F
                        ,Animation.RELATIVE_TO_SELF,0.5f);

        anim.setDuration(500);
        anim.setFillAfter(true);

        RotateAnimation anim2 = new RotateAnimation(180,0
                , Animation.RELATIVE_TO_SELF,0.5F
                ,Animation.RELATIVE_TO_SELF,0.5f);

        anim2.setDuration(500);
        anim2.setFillAfter(true);

        switch (state) {
            case NONE:

                topPadding(-headerHeight);
                arrow.clearAnimation();

                break;
            case PULL:

                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText("下拉可以刷新");

                arrow.clearAnimation();
                arrow.setAnimation(anim2);

                break;
            case RELESE:

                arrow.setVisibility(VISIBLE);
                progress.setVisibility(GONE);
                tip.setText("松开可以刷新");


                arrow.clearAnimation();
                arrow.setAnimation(anim);

                break;
            case REFLASHING:

                topPadding(50);

                arrow.clearAnimation();
                arrow.setVisibility(GONE);
                progress.setVisibility(VISIBLE);
                tip.setText("正在刷新...");

                break;
            default:
                break;
        }
    }

    /**
     * 获取完数据
     */
    public void reflashComplete(){
        state = NONE;
        isRemark = false;
        reflashViewByState();

        TextView updateTime = (TextView) header.findViewById(R.id.lastupdate_time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
        String time = format.format(new Date());

        updateTime.setText(time);
    }

    public void loadComplete(){
        isLoading = false;
        footer.findViewById(R.id.load_layout).setVisibility(GONE);
    }


    public void setInterface(IReflashListener listener){
        this.mReflashListener = listener;
    }

    /**
     * 刷新数据接口
     */
    public interface IReflashListener{
        void onReflash();

        void onLoad();
    }
}
