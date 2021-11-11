package br.com.univesp.diretoducampo.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import br.com.univesp.diretoducampo.database.converter.BigDecimalConverter;
import br.com.univesp.diretoducampo.database.dao.ProductDAO;
import br.com.univesp.diretoducampo.model.Product;

@Database(entities = {Product.class}, version = 1, exportSchema = false)
@TypeConverters(value = {BigDecimalConverter.class})
public abstract class DatabaseStock extends RoomDatabase {

    private static final String SQL_LITE_DATABASE_NAME = "estoque.db";

    public abstract ProductDAO getProductDAO();

    public static DatabaseStock getInstance(Context context) {
        return Room.databaseBuilder(
                context,
                DatabaseStock.class,
                SQL_LITE_DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build();
    }
}