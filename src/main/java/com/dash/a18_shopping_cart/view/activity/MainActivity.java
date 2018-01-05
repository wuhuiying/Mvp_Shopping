package com.dash.a18_shopping_cart.view.activity;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dash.a18_shopping_cart.R;
import com.dash.a18_shopping_cart.model.bean.CartBean;
import com.dash.a18_shopping_cart.model.bean.CountPriceBean;
import com.dash.a18_shopping_cart.presenter.MainPresenter;
import com.dash.a18_shopping_cart.util.ApiUtil;
import com.dash.a18_shopping_cart.view.IView.IMainActivity;
import com.dash.a18_shopping_cart.view.adapter.MyAdapter;
import com.dash.a18_shopping_cart.view.custom.MyExpanableListView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements IMainActivity, View.OnClickListener {

    private MyExpanableListView expanableListView;
    private MainPresenter mainPresenter;
    private CheckBox check_all;
    private TextView text_total;
    private TextView text_buy;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0){
                CountPriceBean countPriceBean = (CountPriceBean) msg.obj;

                text_total.setText("合计:¥"+countPriceBean.getPriceString());
                text_buy.setText("去结算("+countPriceBean.getCount()+")");
            }

        }
    };
    private RelativeLayout relative_progress;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //找到控件
        expanableListView = findViewById(R.id.expanable_list_view);
        check_all = findViewById(R.id.check_all);
        text_total = findViewById(R.id.text_total);
        text_buy = findViewById(R.id.text_buy);
        relative_progress = findViewById(R.id.relative_progress);



        //去掉默认的指示器
        expanableListView.setGroupIndicator(null);

        //获取数据....MVP
        mainPresenter = new MainPresenter(this);

        //全选设置点击事件
        check_all.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();

        //显示进度条
        relative_progress.setVisibility(View.VISIBLE);

        //调用获取数据的方法
        mainPresenter.getCartData(ApiUtil.selectCartUrl);

    }

    /**
     * 只要购物车页面显示  就要去网络获取新的数据....获取购物车数据操作放到获取焦点的生命周期方法中
     */


    @Override
    public void onCartSuccess(final CartBean cartBean) {
        //处于子线程!!!!!!!!!!!!!!!!!!!

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                //获取数据成功...隐藏
                relative_progress.setVisibility(View.GONE);

                if (cartBean != null){
                    //需要更改获取的cartBean数据

                    //1.根据某一个组中的二级所有的子条目是否选中,确定当前一级列表是否选中
                    for (int i = 0;i<cartBean.getData().size();i++){

                        if(isChildInGroupChecked(i,cartBean)){
                            cartBean.getData().get(i).setGroup_check(true);
                        }
                    }

                    //2.设置是否全选选中...根据所有的一级列表是否选中,确定全选是否选中
                    check_all.setChecked(isAllGroupChecked(cartBean));


                    //设置适配器
                    myAdapter = new MyAdapter(MainActivity.this, cartBean,handler,mainPresenter,relative_progress);
                    expanableListView.setAdapter(myAdapter);

                    //展开所有的组...expanableListView.expandGroup()
                    for (int i = 0;i<cartBean.getData().size();i++){
                        expanableListView.expandGroup(i);
                    }

                    //3.计算总价和商品的数量
                    myAdapter.sendPriceAndCount();
                }else {
                    Toast.makeText(MainActivity.this,"购物车空,请添加购物车",Toast.LENGTH_SHORT).show();
                }



            }
        });

    }

    /**
     * 所有的组是否选中
     * @return
     * @param cartBean
     */
    private boolean isAllGroupChecked(CartBean cartBean) {
        for (int i =0;i<cartBean.getData().size();i++){
            if (! cartBean.getData().get(i).isGroup_check()){//表示有没选中的组
                return false;
            }
        }

        return true;
    }

    /**
     * 当前组中所有的子条目是否全部选中
     * @param i
     * @param cartBean
     * @return
     */
    private boolean isChildInGroupChecked(int i, CartBean cartBean) {
        //当前组中所有子条目的数据
        List<CartBean.DataBean.ListBean> listBeans = cartBean.getData().get(i).getList();
        for (int j = 0;j<listBeans.size();j++){
            if (listBeans.get(j).getSelected() == 0){//有未选中的条目
                return false;
            }
        }

        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.check_all:

                //所有孩子的状态跟着全选的状态进行改变
                if (myAdapter != null){
                    myAdapter.setAllChildChecked(check_all.isChecked());
                }
                break;
        }
    }
}
