package com.dash.a18_shopping_cart.view.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dash.a18_shopping_cart.R;
import com.dash.a18_shopping_cart.model.bean.CartBean;
import com.dash.a18_shopping_cart.model.bean.CountPriceBean;
import com.dash.a18_shopping_cart.presenter.MainPresenter;
import com.dash.a18_shopping_cart.util.ApiUtil;
import com.dash.a18_shopping_cart.util.OkHttp3Util;
import com.dash.a18_shopping_cart.view.activity.MainActivity;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Dash on 2018/1/3.
 */
public class MyAdapter extends BaseExpandableListAdapter{
    private RelativeLayout relative_progress;
    private MainPresenter mainPresenter;
    private Handler handler;
    private CartBean cartBean;
    private Context context;
    private int childIndex;
    private int allIndex;

    public MyAdapter(Context context, CartBean cartBean, Handler handler, MainPresenter mainPresenter, RelativeLayout relative_progress) {
        this.context = context;
        this.cartBean = cartBean;
        this.handler = handler;
        this.mainPresenter = mainPresenter;
        this.relative_progress = relative_progress;
    }

    @Override
    public int getGroupCount() {
        return cartBean.getData().size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return cartBean.getData().get(groupPosition).getList().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return cartBean.getData().get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        return cartBean.getData().get(groupPosition).getList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean b, View view, ViewGroup viewGroup) {
        final GroupHolder holder;
        if (view == null){
            view = View.inflate(context, R.layout.group_layout,null);
            holder = new GroupHolder();

            holder.checkBox = view.findViewById(R.id.group_check);
            holder.textView = view.findViewById(R.id.group_text);

            view.setTag(holder);
        }else {
            holder = (GroupHolder) view.getTag();
        }

        final CartBean.DataBean dataBean = cartBean.getData().get(groupPosition);
        //赋值
        holder.textView.setText(dataBean.getSellerName());
        holder.checkBox.setChecked(dataBean.isGroup_check());

        //一级列表的点击事件
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                relative_progress.setVisibility(View.VISIBLE);
                //点击一级列表的时候,子条目需要一个个的去执行更新,,,,全都执行完成之后再去请求查询购物车
                //使用递归的形式...例如一组里面3个孩子,,,0,1,2
                childIndex = 0;
                //更新
                updateChildCheckedInGroup(holder.checkBox.isChecked(),dataBean);

            }
        });

        return view;
    }

    /**
     * 点击以及列表的时候,去改变所有子条目的状态
     * @param
     * @param checked
     * @param dataBean
     */
    private void updateChildCheckedInGroup(final boolean checked, final CartBean.DataBean dataBean) {

        CartBean.DataBean.ListBean listBean = dataBean.getList().get(childIndex);

        //请求更新购物车的接口...更新成功之后,再次请求查询购物车的接口,进行数据的展示
        //?uid=71&sellerid=1&pid=1&selected=0&num=10
        Map<String, String> params = new HashMap<>();
        params.put("uid","3690");
        params.put("sellerid", String.valueOf(listBean.getSellerid()));
        params.put("pid", String.valueOf(listBean.getPid()));
        params.put("selected", String.valueOf(checked ? 1:0));
        params.put("num", String.valueOf(listBean.getNum()));

        OkHttp3Util.doPost(ApiUtil.updateCartUrl, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()){
                   childIndex ++;//0,1,2....size()
                   if (childIndex < dataBean.getList().size()){
                       //继续更新
                       updateChildCheckedInGroup(checked,dataBean);
                   }else {
                       //请求查询购物车的操作...重新展示数据
                       mainPresenter.getCartData(ApiUtil.selectCartUrl);
                   }
                }
            }
        });

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean b, View view, ViewGroup viewGroup) {
        ChildHolder holder;
        if (view == null){
            view = View.inflate(context, R.layout.child_layout,null);
            holder = new ChildHolder();

            holder.checkBox = view.findViewById(R.id.child_check);
            holder.text_title = view.findViewById(R.id.child_title);
            holder.imageView = view.findViewById(R.id.child_image);
            holder.text_price = view.findViewById(R.id.child_price);
            holder.text_jian = view.findViewById(R.id.text_jian);
            holder.text_num = view.findViewById(R.id.text_num);
            holder.text_add = view.findViewById(R.id.text_add);
            holder.text_delete = view.findViewById(R.id.text_delete);

            view.setTag(holder);
        }else {
            holder = (ChildHolder) view.getTag();
        }

        final CartBean.DataBean.ListBean listBean = cartBean.getData().get(groupPosition).getList().get(childPosition);

        //赋值
        holder.text_title.setText(listBean.getTitle());
        holder.text_price.setText("¥"+listBean.getBargainPrice());

        String[] strings = listBean.getImages().split("\\|");
        Glide.with(context).load(strings[0]).into(holder.imageView);

        holder.checkBox.setChecked(listBean.getSelected() == 0? false:true);//根据0,1进行设置是否选中
        //setText()我们使用一定是设置字符串
        holder.text_num.setText(listBean.getNum()+"");

        //点击进行更新购物车的操作
        //给checkBox设置点击事件,,,,更新是否选中
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progressBar要显示
                relative_progress.setVisibility(View.VISIBLE);

                //请求更新购物车的接口...更新成功之后,再次请求查询购物车的接口,进行数据的展示
                //?uid=71&sellerid=1&pid=1&selected=0&num=10
                Map<String, String> params = new HashMap<>();
                params.put("uid","3690");
                params.put("sellerid", String.valueOf(listBean.getSellerid()));
                params.put("pid", String.valueOf(listBean.getPid()));
                params.put("selected", String.valueOf(listBean.getSelected() == 0?1:0));//listBean.getSelected()...0--->1,,,1--->0
                params.put("num", String.valueOf(listBean.getNum()));

                OkHttp3Util.doPost(ApiUtil.updateCartUrl, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //更新成功之后,再次请求查询购物车的接口,进行数据的展示
                        if (response.isSuccessful()){
                            mainPresenter.getCartData(ApiUtil.selectCartUrl);
                        }
                    }
                });

            }
        });

        //加
        holder.text_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //progressBar要显示
                relative_progress.setVisibility(View.VISIBLE);

                //请求更新购物车的接口...更新成功之后,再次请求查询购物车的接口,进行数据的展示
                //?uid=71&sellerid=1&pid=1&selected=0&num=10
                Map<String, String> params = new HashMap<>();
                params.put("uid","3690");
                params.put("sellerid", String.valueOf(listBean.getSellerid()));
                params.put("pid", String.valueOf(listBean.getPid()));
                params.put("selected", String.valueOf(listBean.getSelected()));//listBean.getSelected()...0--->1,,,1--->0


                params.put("num", String.valueOf(listBean.getNum() +1 ));

                OkHttp3Util.doPost(ApiUtil.updateCartUrl, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //更新成功之后,再次请求查询购物车的接口,进行数据的展示
                        if (response.isSuccessful()){
                            mainPresenter.getCartData(ApiUtil.selectCartUrl);
                        }
                    }
                });

            }
        });

        //减
        holder.text_jian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int num = listBean.getNum();
                if (num == 1){
                    return;
                }

                //progressBar要显示
                relative_progress.setVisibility(View.VISIBLE);

                //请求更新购物车的接口...更新成功之后,再次请求查询购物车的接口,进行数据的展示
                //?uid=71&sellerid=1&pid=1&selected=0&num=10
                Map<String, String> params = new HashMap<>();
                params.put("uid","3690");
                params.put("sellerid", String.valueOf(listBean.getSellerid()));
                params.put("pid", String.valueOf(listBean.getPid()));
                params.put("selected", String.valueOf(listBean.getSelected()));//listBean.getSelected()...0--->1,,,1--->0
                params.put("num", String.valueOf(num - 1));

                OkHttp3Util.doPost(ApiUtil.updateCartUrl, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        //更新成功之后,再次请求查询购物车的接口,进行数据的展示
                        if (response.isSuccessful()){
                            mainPresenter.getCartData(ApiUtil.selectCartUrl);
                        }
                    }
                });
            }
        });

        //删除
        holder.text_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //显示progress
                relative_progress.setVisibility(View.VISIBLE);

                //uid=72&pid=1&source=android
                Map<String, String> params = new HashMap<>();
                params.put("uid","3690");
                params.put("pid", String.valueOf(listBean.getPid()));
                params.put("source","android");

                OkHttp3Util.doPost(ApiUtil.deleteCartUrl, params, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()){

                            //删除成功之后.....再次查询购物车
                            mainPresenter.getCartData(ApiUtil.selectCartUrl);

                        }
                    }
                });

            }
        });

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    /**
     * 计算数量和价格,,,并发送到activity显示
     */
    public void sendPriceAndCount() {
        double price = 0;
        int count = 0;

        for (int i=0;i<cartBean.getData().size();i++){

            List<CartBean.DataBean.ListBean> listBeans = cartBean.getData().get(i).getList();
            for (int j = 0; j< listBeans.size(); j++){
                CartBean.DataBean.ListBean listBean = listBeans.get(j);

                //选中的时候计算价格和数量
                if (listBean.getSelected() == 1){

                    price += listBean.getBargainPrice() * listBean.getNum();
                    count += listBean.getNum();
                }

            }
        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String priceString = decimalFormat.format(price);

        //封装一下
        CountPriceBean countPriceBean = new CountPriceBean(priceString, count);
        //发送给activity/fragment进行显示

        Message msg = Message.obtain();

        msg.what = 0;
        msg.obj = countPriceBean;
        handler.sendMessage(msg);

    }

    /**
     * 更新所有的子孩子的状态...跟随全选状态改变
     * @param checked
     */
    public void setAllChildChecked(boolean checked) {

        //显示进度条
        relative_progress.setVisibility(View.VISIBLE);

        //通过遍历,把所有的孩子装到一个大的集合中
        List<CartBean.DataBean.ListBean> allList = new ArrayList<>();
        for (int i=0;i<cartBean.getData().size();i++){
            for (int j = 0;j<cartBean.getData().get(i).getList().size();j++){
                allList.add(cartBean.getData().get(i).getList().get(j));

            }
        }

        //更新每一个子孩子的状态...递归
        allIndex = 0;

        updateAllChild(checked,allList);


    }

    /**
     * 更新所有的孩子
     * @param checked
     * @param allList
     */
    private void updateAllChild(final boolean checked, final List<CartBean.DataBean.ListBean> allList) {

        CartBean.DataBean.ListBean listBean = allList.get(allIndex);

        //请求更新购物车的接口...更新成功之后,再次请求查询购物车的接口,进行数据的展示
        //?uid=71&sellerid=1&pid=1&selected=0&num=10
        Map<String, String> params = new HashMap<>();
        params.put("uid","3690");
        params.put("sellerid", String.valueOf(listBean.getSellerid()));
        params.put("pid", String.valueOf(listBean.getPid()));
        params.put("selected", String.valueOf(checked? 1:0));
        params.put("num", String.valueOf(listBean.getNum()));

        OkHttp3Util.doPost(ApiUtil.updateCartUrl, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()){
                    allIndex ++;
                    //判断
                    if (allIndex < allList.size()){
                        //继续更新
                        updateAllChild(checked,allList);

                    }else {
                        //查询购物车
                        mainPresenter.getCartData(ApiUtil.selectCartUrl);

                    }


                }
            }
        });

    }

    private class GroupHolder{
        CheckBox checkBox;
        TextView textView;
    }

    private class ChildHolder{
        CheckBox checkBox;
        ImageView imageView;
        TextView text_title;
        TextView text_price;
        TextView text_num;
        TextView text_jian;
        TextView text_add;
        TextView text_delete;
    }
}
