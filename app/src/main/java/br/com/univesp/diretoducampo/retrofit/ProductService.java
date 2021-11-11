package br.com.univesp.diretoducampo.retrofit;

import java.util.List;

import br.com.univesp.diretoducampo.model.Product;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ProductService {

    @GET("products/all")
    Call<List<Product>> getAllProducts();

    @POST("products/create")
    Call<Product> postProduct(@Body Product product);

    @PUT("products/product/{id}")
    Call<Product> updateProduct(@Path("id") long id, @Body Product product);

    @DELETE("products/product/{id}")
    Call<Void> deleteProduct(@Path("id") long id);

    @DELETE("products/all")
    Call<Void> deleteAllProducts();

}
