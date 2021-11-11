package br.com.univesp.diretoducampo.retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductRetrofit {

    private final ProductService productService;
    private static final String baseUrl = "https://diretoducampo.herokuapp.com/";
    //private static final String baseUrl = "https://localhost/";

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
