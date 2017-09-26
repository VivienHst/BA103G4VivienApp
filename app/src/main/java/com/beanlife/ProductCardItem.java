package com.beanlife;

import java.io.Serializable;

/**
 * Created by Java on 2017/9/2.
 */

public class ProductCardItem implements Serializable {

    private int prodPic;
    private String ProdNo;
    private String prodName;
    private String prodPrice;

    public ProductCardItem(int prodPic, String prodNo, String prodName, String prodPrice) {
        this.prodPic = prodPic;
        ProdNo = prodNo;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
    }

    public int getProdPic() {
        return prodPic;
    }

    public void setProdPic(int prodPic) {
        this.prodPic = prodPic;
    }

    public String getProdNo() {
        return ProdNo;
    }

    public void setProdNo(String prodNo) {
        ProdNo = prodNo;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getProdPrice() {
        return prodPrice;
    }

    public void setProdPrice(String prodPrice) {
        this.prodPrice = prodPrice;
    }
}
