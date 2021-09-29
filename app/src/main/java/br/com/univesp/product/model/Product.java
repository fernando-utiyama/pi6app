package br.com.univesp.product.model;

import java.math.BigDecimal;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Product {

    @PrimaryKey(autoGenerate = true)
    private final long id;
    private final String product;
    private final BigDecimal price;
    private final int among;
    private final String seller;

    public Product(long id, String product, BigDecimal price, int among, String seller) {
        this.id = id;
        this.product = product;
        this.price = price;
        this.among = among;
        this.seller = seller;
    }

    public long getId() {
        return id;
    }

    public String getProduct() {
        return product;
    }

    public BigDecimal getPrice() {
        return price.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    }

    public int getAmong() {
        return among;
    }

    public String getSeller() {
        return seller;
    }

}
