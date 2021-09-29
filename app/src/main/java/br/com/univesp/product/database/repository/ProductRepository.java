package br.com.univesp.product.database.repository;

import java.util.List;

import br.com.univesp.product.asynctask.BaseAsyncTask;
import br.com.univesp.product.database.dao.ProductDAO;
import br.com.univesp.product.model.Product;
import br.com.univesp.product.retrofit.ProductRetrofit;
import br.com.univesp.product.retrofit.ProductService;
import br.com.univesp.product.retrofit.callback.BaseCallback;
import retrofit2.Call;

public class ProductRepository {

    private final ProductDAO dao;
    private ProductService productService;

    public ProductRepository(ProductDAO dao) {
        this.dao = dao;
    }

    public void findProducts(ProductsCallback<List<Product>> callback) {
        new BaseAsyncTask<>(dao::findAll,
                productList -> {
                    callback.sucess(productList);
                    findProductsAPI(callback);
                }).execute();
    }

    public void findProductsAPI(ProductsCallback<List<Product>> callback) {
        productService = new ProductRetrofit().getProductService();
        Call<List<Product>> call = productService.getAllProducts();
        call.enqueue(new BaseCallback<>(new BaseCallback.ResponseCallback<List<Product>>() {
            @Override
            public void sucess(List<Product> products) {
                localUpdate(products, callback);
            }

            @Override
            public void fail(String error) {
                callback.fail(error);
            }
        }));
    }

    private void localUpdate(List<Product> products, ProductsCallback<List<Product>> callback) {
        new BaseAsyncTask<>(() -> {
            dao.saveAll(products);
            return dao.findAll();
        }, callback::sucess).execute();
    }


    public void save(Product product, ProductsCallback<Product> callback) {
        Call<Product> call = productService.postProduct(product);
        call.enqueue(new BaseCallback<>(new BaseCallback.ResponseCallback<Product>() {
            @Override
            public void sucess(Product newProduct) {
                localSave(newProduct, callback);
            }

            @Override
            public void fail(String error) {
                callback.fail(error);
            }
        }));
    }

    private void localSave(Product newProduct, ProductsCallback<Product> callback) {
        new BaseAsyncTask<>(() -> {
            long id = dao.save(newProduct);
            return dao.findByID(id);
        }, callback::sucess).execute();
    }

    public interface ProductsCallback<T> {
        void sucess(T products);

        void fail(String error);
    }

}
