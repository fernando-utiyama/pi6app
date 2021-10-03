package br.com.univesp.product.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductRetrofit {

    private final ProductService productService;
    private static final String baseUrl = "https://univesp-campinas-pi6-back.herokuapp.com/";

    public ProductRetrofit() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        productService = retrofit.create(ProductService.class);
    }

    public ProductService getProductService() {
        return productService;
    }

}
