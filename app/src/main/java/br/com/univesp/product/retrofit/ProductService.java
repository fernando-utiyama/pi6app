package br.com.univesp.product.retrofit;

import java.util.List;

import br.com.univesp.product.model.Product;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface ProductService {

    @GET("products/all")
    Call<List<Product>> getAllProducts();

    @POST("products/create")
    Call<Product> postProduct(@Body Product product);

    @DELETE("products/product")
    Call<Product> deleteProduct(Long id);

}
