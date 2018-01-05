package com.dash.a18_shopping_cart.util;

/**
 * Created by Dash on 2018/1/3.
 *
 * ApiUtil里面放的是所有的访问路径.....公有静态
 */
public class ApiUtil {

    //查询购物车的路径 https://www.zhaoapi.cn/product/getCarts?uid=71&source=android
    public static String selectCartUrl = "https://www.zhaoapi.cn/product/getCarts";

    //删除购物车...https://www.zhaoapi.cn/product/deleteCart?uid=72&pid=1
    public static String deleteCartUrl ="https://www.zhaoapi.cn/product/deleteCart";

    //更新购物车....?uid=71&sellerid=1&pid=1&selected=0&num=10
    public static String updateCartUrl = "https://www.zhaoapi.cn/product/updateCarts";
    //商品详情.....

}
