package br.com.univesp.diretoducampo.model;

import android.graphics.Bitmap;

import java.math.BigDecimal;
import java.sql.Blob;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    private final long id;
    private final String product;
    private final String productImage;
    private final BigDecimal price;
    private final int quantity;
    private final String seller;
    private final String sellerPhone;

    public Product(long id, String product, String productImage, BigDecimal price, int quantity, String seller, String sellerPhone) {
        this.id = id;
        this.product = product;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
        this.seller = seller;
        this.sellerPhone = sellerPhone;
    }

    public long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public String getProductImage() {
        return productImage;
    }

    public BigDecimal getPrice() {
        return price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSeller() {
        return seller;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

}

