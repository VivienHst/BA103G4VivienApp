package com.beanlife;
/**
 * Created by Java on 2017/9/10.
 * Product連線取值
 * 設定servlet的路徑
 */

public class Common {
//    public final static String URL;
//    public final static String URL = "10.0.2.2:8081";
    public final static String URL = "10.120.38.16:8081";

    public final static String PROD_URL = "http://" + URL + "/BA103G4vivien/ProdServletForApp";
    public final static String ACT_URL = "http://" + URL + "/BA103G4vivien/ActServletForApp";
    public final static String STORE_URL = "http://" + URL + "/BA103G4vivien/StoreServletForApp";
    public final static String REVIEW_URL = "http://" + URL + "/BA103G4vivien/ReviewServletForApp";
    public final static String MEM_URL = "http://" + URL + "/BA103G4vivien/MemServletForApp";
    public final static String LIKE_REV_URL = "http://" + URL + "/BA103G4vivien/Like_revServletForApp";
    public final static String ORD_URL = "http://" + URL + "/BA103G4vivien/OrdServletForApp";
    public final static String CART_URL = "http://" + URL + "/BA103G4vivien/Cart_listServletForApp";

    //login state
    public final static String LOGIN_STATE = "loginState";

}
