package br.com.univesp.product.ui.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.univesp.product.R;
import br.com.univesp.product.asynctask.BaseAsyncTask;
import br.com.univesp.product.database.EstoqueDatabase;
import br.com.univesp.product.database.dao.ProductDAO;
import br.com.univesp.product.database.repository.ProductRepository;
import br.com.univesp.product.model.Product;
import br.com.univesp.product.ui.dialog.EditaProdutoDialog;
import br.com.univesp.product.ui.dialog.SalvaProdutoDialog;
import br.com.univesp.product.ui.recyclerview.adapter.ListaProdutosAdapter;

public class ListaProdutosActivity extends AppCompatActivity {

    private static final String TITULO_APPBAR = "DiretoDuCampo";
    private ListaProdutosAdapter adapter;
    private ProductDAO dao;
    private ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_produtos);
        setTitle(TITULO_APPBAR);

        configuraListaProdutos();
        configuraFabSalvaProduto();

        EstoqueDatabase db = EstoqueDatabase.getInstance(this);
        dao = db.getProductDAO();
        repository = new ProductRepository(dao);
        repository.findProducts(new ProductRepository.ProductsCallback<List<Product>>() {
            @Override
            public void sucess(List<Product> products) {
                adapter.atualiza(products);
            }

            @Override
            public void fail(String error) {
                Toast.makeText(ListaProdutosActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void configuraListaProdutos() {
        RecyclerView listaProdutos = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ListaProdutosAdapter(this, this::abreFormularioEditaProduto);
        listaProdutos.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(this::remove);
    }

    private void remove(int posicao,
                        Product productRemovido) {
        new BaseAsyncTask<>(() -> {
            dao.remove(productRemovido);
            return null;
        }, resultado -> adapter.remove(posicao))
                .execute();
    }

    private void configuraFabSalvaProduto() {
        FloatingActionButton fabAdicionaProduto = findViewById(R.id.activity_lista_produtos_fab_adiciona_produto);
        fabAdicionaProduto.setOnClickListener(v -> abreFormularioSalvaProduto());
    }

    private void abreFormularioSalvaProduto() {
        new SalvaProdutoDialog(this, newProduct ->
                repository.save(newProduct, new ProductRepository.ProductsCallback<Product>() {
                    @Override
                    public void sucess(Product products) {
                        adapter.adiciona(products);
                    }

                    @Override
                    public void fail(String error) {
                        Toast.makeText(ListaProdutosActivity.this, error, Toast.LENGTH_SHORT).show();
                    }
                })).show();
    }

    private void abreFormularioEditaProduto(int posicao, Product product) {
        new EditaProdutoDialog(this, product,
                produtoEditado -> edita(posicao, produtoEditado))
                .show();
    }

    private void edita(int posicao, Product product) {
        new BaseAsyncTask<>(() -> {
            dao.updateProduct(product);
            return product;
        }, produtoEditado ->
                adapter.edita(posicao, produtoEditado))
                .execute();
    }


}
