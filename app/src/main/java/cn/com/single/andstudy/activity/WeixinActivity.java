package cn.com.single.andstudy.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cn.com.single.andstudy.ChangeColorIconWithText;
import cn.com.single.andstudy.R;
import cn.com.single.andstudy.TabFragment;

import static cn.com.single.andstudy.R.id.tab_contact;
import static cn.com.single.andstudy.R.id.tab_found;
import static cn.com.single.andstudy.R.id.tab_home;
import static cn.com.single.andstudy.R.id.tab_me;

/**
 * @author li
 */
public class WeixinActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager mViewPager;
    private List<Fragment> mTabs = new ArrayList<>();
    private String[] mTitles = new String[]{
            "First",
            "Second",
            "Thread",
            "Fourth"
    };

    private FragmentPagerAdapter mAdapter;

    private List<ChangeColorIconWithText> mTabIndicators = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weixin);

        setOverflowButtonAlways();
//        getActionBar().setDisplayHomeAsUpEnabled(false);

        mViewPager = (ViewPager) findViewById(R.id.view_pager);

        ChangeColorIconWithText t1 = (ChangeColorIconWithText) findViewById(tab_home);
        ChangeColorIconWithText t2 = (ChangeColorIconWithText) findViewById(tab_contact);
        ChangeColorIconWithText t3 = (ChangeColorIconWithText) findViewById(tab_found);
        ChangeColorIconWithText t4 = (ChangeColorIconWithText) findViewById(tab_me);

        mTabIndicators.add(t1);
        mTabIndicators.add(t2);
        mTabIndicators.add(t3);
        mTabIndicators.add(t4);

        t1.setOnClickListener(this);
        t2.setOnClickListener(this);
        t3.setOnClickListener(this);
        t4.setOnClickListener(this);

        t1.setIconAlpha(1.0f);

        for (String title : mTitles) {
            TabFragment fragment = new TabFragment();
            Bundle b = new Bundle();
            b.putString(TabFragment.TITLE,title);
            fragment.setArguments(b);

            mTabs.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()){

            @Override
            public int getCount() {
                return mTabs.size();
            }

            @Override
            public Fragment getItem(int position) {
                return mTabs.get(position);
            }
        };

        mViewPager.setAdapter(mAdapter);

        initEvent();
    }

    private void initEvent() {
        mViewPager.setOnPageChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {



        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main,menu);

        return true;
    }

    private void setOverflowButtonAlways(){
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKey = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");

            menuKey.setAccessible(true);
            menuKey.setBoolean(config,false);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置menu显示icon
     *
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {

        if(featureId == Window.FEATURE_ACTION_BAR && menu != null){
            if("MenuBuilder".equals(menu.getClass().getSimpleName())){
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible",Boolean.TYPE);

                    m.setAccessible(true);
                    m.invoke(menu,true);

                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public void onClick(View v) {

        resetOtherTabs();

        switch (v.getId()) {
            case R.id.tab_home:
                mTabIndicators.get(0).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(0,false);
                break;
            case R.id.tab_contact:
                mTabIndicators.get(1).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(1,false);
                break;
            case R.id.tab_found:
                mTabIndicators.get(2).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(2,false);
                break;
            case R.id.tab_me:
                mTabIndicators.get(3).setIconAlpha(1.0f);
                mViewPager.setCurrentItem(3,false);
                break;
            default:
                break;
        }
    }

    /**
     * 重置
     */
    private void resetOtherTabs() {
        for (int i = 0; i < mTabIndicators.size(); i++) {
            mTabIndicators.get(i).setIconAlpha(0);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        Log.i("TAG","position = " + position + ",positionOffset = " + positionOffset);
        if(positionOffset > 0){
            ChangeColorIconWithText left = mTabIndicators.get(position);
            ChangeColorIconWithText right = mTabIndicators.get(position + 1);

            left.setIconAlpha(1-positionOffset);
            right.setIconAlpha(positionOffset);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
