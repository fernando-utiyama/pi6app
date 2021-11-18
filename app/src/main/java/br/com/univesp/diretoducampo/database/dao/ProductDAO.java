package br.com.univesp.diretoducampo.database.dao;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import br.com.univesp.diretoducampo.model.Product;

@Dao
public interface ProductDAO {

    @Insert
    long save(Product product);

    @Update
    void updateProduct(Product product);

    @Query("SELECT * FROM Product")
    List<Product> findAll();

    @Query("SELECT * FROM Product WHERE id = :id")
    Product findByID(long id);

    @Delete
    void remove(Product product);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAll(List<Product> productList);
}
