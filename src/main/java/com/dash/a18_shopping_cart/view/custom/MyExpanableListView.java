package com.dash.a18_shopping_cart.view.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ExpandableListView;

/**
 * Created by Dash on 2018/1/3.
 */
public class MyExpanableListView extends ExpandableListView {
    public MyExpanableListView(Context context) {
        super(context);
    }

    public MyExpanableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyExpanableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //高度
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, height);
    }
}
