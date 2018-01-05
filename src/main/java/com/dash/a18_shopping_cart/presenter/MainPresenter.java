package com.dash.a18_shopping_cart.presenter;

import com.dash.a18_shopping_cart.model.MainModel;
import com.dash.a18_shopping_cart.model.bean.CartBean;
import com.dash.a18_shopping_cart.presenter.inter.IMainPresenter;
import com.dash.a18_shopping_cart.view.IView.IMainActivity;
import com.dash.a18_shopping_cart.view.activity.MainActivity;

/**
 * Created by Dash on 2018/1/3.
 */
public class MainPresenter implements IMainPresenter{

    private MainModel mainModel;
    private IMainActivity iMainActivity;

    public MainPresenter(IMainActivity iMainActivity) {

        this.iMainActivity = iMainActivity;

        //创建model
        mainModel = new MainModel(this);

    }

    //当前中间者不去直接获取网络数据,,,需要让model获取,,,创建presenter的时候就去创建出model,,,构造方法中
    public void getCartData(String selectCartUrl) {
        //需要让model获取,

        mainModel.getCartData(selectCartUrl);

    }

    @Override
    public void onSuccess(CartBean cartBean) {
        //回调给view层...activity
        iMainActivity.onCartSuccess(cartBean);
    }
}
