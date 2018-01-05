package com.dash.a18_shopping_cart.model;

import com.dash.a18_shopping_cart.model.bean.CartBean;
import com.dash.a18_shopping_cart.presenter.MainPresenter;
import com.dash.a18_shopping_cart.presenter.inter.IMainPresenter;
import com.dash.a18_shopping_cart.util.OkHttp3Util;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by Dash on 2018/1/3.
 */
public class MainModel {

    private IMainPresenter iMainPresenter;

    public MainModel(IMainPresenter iMainPresenter) {
        this.iMainPresenter = iMainPresenter;
    }

    //在这里真正获取购物车的数据
    public void getCartData(String selectCartUrl) {

        Map<String, String> params = new HashMap<>();
        params.put("uid","3690");
        params.put("source","android");

        OkHttp3Util.doPost(selectCartUrl, params, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()){

                    String json = response.body().string();

                    if ("null".equals(json)){

                        iMainPresenter.onSuccess(null);

                    }else {
                        //解析
                        CartBean cartBean = new Gson().fromJson(json,CartBean.class);

                        //接口回调...presenter层
                        iMainPresenter.onSuccess(cartBean);
                    }



                }
            }
        });

    }
}
