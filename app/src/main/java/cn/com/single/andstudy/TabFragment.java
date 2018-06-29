package cn.com.single.andstudy;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * @author li
 */
public class TabFragment extends Fragment {

    private String mTitle = "Default";

    public static final String TITLE = "title";

    public TabFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(getArguments() != null){
            mTitle = getArguments().getString(TITLE);
        }

        TextView tv = new TextView(getContext());
        tv.setText(mTitle);
        tv.setTextSize(30);
        tv.setBackgroundColor(Color.parseColor("#ffffff"));
        tv.setGravity(Gravity.CENTER);

        return tv;
    }

}
