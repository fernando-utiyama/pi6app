package br.com.univesp.diretoducampo.database.repository;

import android.content.Context;
import android.util.Log;

import java.util.List;

import br.com.univesp.diretoducampo.asynctask.BaseAsyncTask;
import br.com.univesp.diretoducampo.database.DatabaseStock;
import br.com.univesp.diretoducampo.database.dao.ProductDAO;
import br.com.univesp.diretoducampo.model.Product;
import br.com.univesp.diretoducampo.retrofit.ProductRetrofit;
import br.com.univesp.diretoducampo.retrofit.ProductService;
import br.com.univesp.diretoducampo.retrofit.callback.BaseCallback;
import br.com.univesp.diretoducampo.retrofit.callback.VoidCallback;
import retrofit2.Call;

public class ProductRepository {

    private final ProductDAO dao;
    private final ProductService productService;

    public ProductRepository(Context context) {
        DatabaseStock db = DatabaseStock.getInstance(context);
        dao = db.getProductDAO();
        productService = new ProductRetrofit().getProductService();
    }

    public void findProducts(ProductsCallback<List<Product>> callback) {
        new BaseAsyncTask<>(dao::findAll,
                productList -> {
                    callback.success(productList);
                    findProductsAPI(callback);
                }).execute();
    }

    public void findProductsAPI(ProductsCallback<List<Product>> callback) {
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

    public void update(Product product, ProductsCallback<Product> callback) {
        Call<Product> call = productService.updateProduct(product.getId(), product);
        call.enqueue(new BaseCallback<>(new BaseCallback.ResponseCallback<Product>() {
            @Override
            public void sucess(Product updatedProduct) {
                localUpdate(updatedProduct, callback);
            }

            @Override
            public void fail(String error) {
                callback.fail(error);
            }
        }));
    }

    public void delete(Product product, ProductsCallback<Void> callback) {
        Call<Void> call = productService.deleteProduct(product.getId());
        call.enqueue(new VoidCallback(new VoidCallback.ResponseCallback() {
            @Override
            public void sucess() {
                localDelete(product, callback);
            }

            @Override
            public void fail(String error) {
                callback.fail(error);
            }
        }));
    }

    private void localDelete(Product product, ProductsCallback<Void> callback) {
        new BaseAsyncTask<>(() -> {
            dao.remove(product);
            return null;
        }, callback::success)
                .execute();
    }

    private void localUpdate(List<Product> products, ProductsCallback<List<Product>> callback) {
        new BaseAsyncTask<>(() -> {
            dao.saveAll(products);
            return dao.findAll();
        }, callback::success).execute();
    }

    private void localUpdate(Product product, ProductsCallback<Product> callback) {
        new BaseAsyncTask<>(() -> {
            try {
                dao.save(product);
            } catch(Exception ex) {
                return dao.findByID(product.getId());
            }
            return dao.findByID(product.getId());
        }, callback::success).execute();
    }

    private void localSave(Product newProduct, ProductsCallback<Product> callback) {
        new BaseAsyncTask<>(() -> {
            dao.updateProduct(newProduct);
            return newProduct;
        }, callback::success).execute();
    }

    public interface ProductsCallback<T> {
        void success(T products);

        void fail(String error);
    }

}
