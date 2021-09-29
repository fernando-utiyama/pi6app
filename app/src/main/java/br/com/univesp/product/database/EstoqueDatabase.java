package br.com.univesp.product.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import br.com.univesp.product.database.converter.BigDecimalConverter;
import br.com.univesp.product.database.dao.ProductDAO;
import br.com.univesp.product.model.Product;

@Database(entities = {Product.class}, version = 1, exportSchema = false)
@TypeConverters(value = {BigDecimalConverter.class})
public abstract class EstoqueDatabase extends RoomDatabase {

    private static final String NOME_BANCO_DE_DADOS = "estoque.db";

    public abstract ProductDAO getProductDAO();

    public static EstoqueDatabase getInstance(Context context) {
        return Room.databaseBuilder(
                context,
                EstoqueDatabase.class,
                NOME_BANCO_DE_DADOS)
                .build();
    }
}
