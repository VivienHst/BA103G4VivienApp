package com.beanlife.recyclecan;

/**
 * Created by Vivien on 2017/8/25.
 */

public class ProductList {
    private int prodPic;
    private String prodName;
    private String prodPrice;
    private String prodDesc;

    public ProductList(int prodPic, String prodName, String prodPrice, String prodDesc) {
        this.prodPic = prodPic;
        this.prodName = prodName;
        this.prodPrice = prodPrice;
        this.prodDesc = prodDesc;
    }

    public int getProdPic() {
        return prodPic;
    }

    public void setProdPic(int prodPic) {
        this.prodPic = prodPic;
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

    public String getProdDesc() {
        return prodDesc;
    }

    public void setProdDesc(String prodDesc) {
        this.prodDesc = prodDesc;
    }
}
