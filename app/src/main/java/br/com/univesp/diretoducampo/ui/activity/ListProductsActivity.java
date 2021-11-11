package br.com.univesp.diretoducampo.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.util.List;

import br.com.univesp.diretoducampo.R;
import br.com.univesp.diretoducampo.database.repository.ProductRepository;
import br.com.univesp.diretoducampo.model.Product;
import br.com.univesp.diretoducampo.ui.dialog.EditProductDialog;
import br.com.univesp.diretoducampo.ui.dialog.NewProductDialog;
import br.com.univesp.diretoducampo.ui.recyclerview.adapter.ProductsListAdapter;

public class ListProductsActivity extends AppCompatActivity {

    private ProductsListAdapter adapter;
    private ProductRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        setTitle(this.getResources().getString(R.string.app_name));

        setProductsList();
        setNewProductButton();
        setShareAppButton();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE);
        } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        repository = new ProductRepository(this);
        findProducts();
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        findProducts();
    }

    @Override
    protected void onResume() {
        super.onResume();
        findProducts();
    }

    @Override
    public void onActivityReenter(int resultCode, Intent data) {
        super.onActivityReenter(resultCode, data);
        findProducts();
    }

    public void findProducts() {
        repository.findProducts(new ProductRepository.ProductsCallback<List<Product>>() {
            @Override
            public void success(List<Product> products) {
                adapter.atualiza(products);
            }

            @Override
            public void fail(String error) {
                //Toast.makeText(ListProductsActivity.this, "Não foi possível atualizar a lista de produtos. \n Erro: " + error, Toast.LENGTH_LONG).show();
                Log.e(this.getClass().getName(), error);
            }
        });
    }

    private void setProductsList() {
        RecyclerView productsList = findViewById(R.id.activity_lista_produtos_lista);
        adapter = new ProductsListAdapter(this, this::openEditProductFormDialog);
        productsList.setAdapter(adapter);
        adapter.setOnItemClickRemoveContextMenuListener(((posicao, productRemovido) ->
                repository.delete(productRemovido, new ProductRepository.ProductsCallback<Void>() {
                    @Override
                    public void success(Void products) {
                        adapter.remove(posicao, productRemovido);
                    }

                    @Override
                    public void fail(String error) {
                        //Toast.makeText(ListProductsActivity.this, "Não foi possível carregar a lista de produtos no momento. \n Por favor tente mais tarde!", Toast.LENGTH_LONG).show();
                        Log.e(ListProductsActivity.this.toString(), error);
                    }
                })));
    }

    private void setNewProductButton() {
        FloatingActionButton newFloatingProductButton = findViewById(R.id.activity_lista_produtos_fab_add_product);
        TooltipCompat.setTooltipText(newFloatingProductButton, "Criar novo anúncio de produto");
        newFloatingProductButton.setOnClickListener(v -> openNewProductDialog());
    }

    private void setShareAppButton() {
        FloatingActionButton newFloatingProductButton = findViewById(R.id.activity_lista_produtos_fab_share_app);
        TooltipCompat.setTooltipText(newFloatingProductButton, "Compartilhar o app DiretoDuCampo");
        newFloatingProductButton.setOnClickListener(v -> ShareAppDialog());
    }

    private void shareToWhatsApp(Context ctx){
        try {
            Bitmap imgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
            String path = MediaStore.Images.Media.insertImage(ctx.getApplicationContext().getContentResolver(), imgBitmap, "AppDiretoDuCampo.jpg", null);
            Uri imageUri = Uri.parse(path);

            String shareBody = "Conheça o aplicativo DiretoDuCampo onde encontro os melhores anúncios de verduras e legumes fresquinhos! Baixe em: https://bit.ly/DiretoDuCampoApp";

            Intent share = new Intent(Intent.ACTION_SEND);
            share.setAction(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, shareBody);
            share.setType("text/plain");

            share.putExtra(Intent.EXTRA_STREAM, imageUri);
            share.setType("image/jpeg");
            startActivity(Intent.createChooser(share, "Compartilhar com..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Log.e(this.getClass().getName(),ex.getMessage());
        } catch (Exception ex) {
            Log.e(this.getClass().getName(),ex.getMessage());
        }
    }

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (!isGranted) {
                    Toast.makeText(ListProductsActivity.this, "Por favor dê as permissões pedidas para continuar. Tente novamente.", Toast.LENGTH_LONG).show();
                }
            });

    private final ActivityResultLauncher<String> requestPermissionLauncherShareWhatsApp =
    registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            Toast.makeText(ListProductsActivity.this, "Por favor dê as permissões pedidas para continuar. Tente novamente.", Toast.LENGTH_LONG).show();
        }
    });

    private void ShareAppDialog() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncherShareWhatsApp.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE);
            } else if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncherShareWhatsApp.launch(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                shareToWhatsApp(this.getApplicationContext());
            }
    }

    private void openNewProductDialog() {
        new NewProductDialog(this, newProduct ->
                repository.save(newProduct, new ProductRepository.ProductsCallback<Product>() {
                    @Override
                    public void success(Product products) {
                        adapter.adiciona(products);
                    }

                    @Override
                    public void fail(String error) {
                        Toast.makeText(ListProductsActivity.this, "Não foi possível carregar a lista de produtos no momento. \n Por favor tente mais tarde! Erro: " + error, Toast.LENGTH_LONG).show();
                        Log.e(ListProductsActivity.this.toString(), error);
                    }
                })).show();
    }

    private void openEditProductFormDialog(int posicao, Product product) {
        new EditProductDialog(this, product,
                createdProduct -> repository.update(createdProduct, new ProductRepository.ProductsCallback<Product>() {
                    @Override
                    public void success(Product produtoEditado) {
                        adapter.edita(posicao, produtoEditado);
                    }

                    @Override
                    public void fail(String error) {
                        Toast.makeText(ListProductsActivity.this, "Não foi possível editar o produto: " + product.getProduct() + " no momento. \n Por favor tente mais tarde!", Toast.LENGTH_LONG).show();
                        Log.e(ListProductsActivity.this.toString(), error);
                    }
                }))
                .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage(this.getResources().getString(R.string.exit_question_label))
                .setCancelable(false)
                .setPositiveButton(this.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ListProductsActivity.this.finish();
                    }
                })
                .setNegativeButton(this.getResources().getString(R.string.no), null)
                .show();
    }
}
