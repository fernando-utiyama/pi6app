package br.com.univesp.diretoducampo.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;

import androidx.appcompat.app.AlertDialog;
import br.com.univesp.diretoducampo.R;
import br.com.univesp.diretoducampo.model.Product;

abstract public class NewProductForm {

    private final String title;
    private final String titlePositiveButton;
    private final ListenerConfirm listener;
    private final Context context;
    private Product product;
    private AlertDialog editList = null;

    NewProductForm(Context context,
                            String title,
                            String titlePositiveButton,
                   ListenerConfirm listener) {
        this.title = title;
        this.titlePositiveButton = titlePositiveButton;
        this.listener = listener;
        this.context = context;
    }

    NewProductForm(Context context,
                            String titulo,
                            String tituloBotaoPositivo,
                            ListenerConfirm listener,
                            Product product) {
        this(context, titulo, tituloBotaoPositivo, listener);
        this.product = product;
    }

    private Drawable getImageProduct(Context ctx, String productName) {
        Drawable foundDrawable = null;

        if (productName.contains("bacaxi")) {
            foundDrawable = ctx.getDrawable(R.drawable.abacaxi);
        } else if (productName.contains("enoura")) {
            foundDrawable = ctx.getDrawable(R.drawable.cenoura);
        } else if (productName.contains("cula")) {
            foundDrawable = ctx.getDrawable(R.drawable.rucula);
        } else if (productName.contains("açã")) {
            foundDrawable = ctx.getDrawable(R.drawable.maca);
        } else if (productName.contains("elão")) {
            foundDrawable = ctx.getDrawable(R.drawable.melao);
        }  else if (productName.contains("cerol")) {
            foundDrawable = ctx.getDrawable(R.drawable.acerola);
        }  else {
            foundDrawable = ctx.getDrawable(R.drawable.sem_imagem);
        }

        return foundDrawable;
    }

    public void show() {
        @SuppressLint("InflateParams") View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.product_form_dialog, null);
        tentaPreencherFormulario(viewCriada);
        AlertDialog editList = new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(viewCriada)
                .setPositiveButton(titlePositiveButton, (dialog, which) -> {
                    EditText campoNome = getEditText(viewCriada, R.id.formulario_produto_product);
                    EditText campoPreco = getEditText(viewCriada, R.id.formulario_produto_price);
                    EditText campoQuantidade = getEditText(viewCriada, R.id.formulario_produto_quantidade);
                    EditText campoVendedor = getEditText(viewCriada, R.id.formulario_produto_seller);
                    EditText campoVendedorTelefone = getEditText(viewCriada, R.id.formulario_produto_seller_phone);
                    ImageView campoProductImage = getImageView(viewCriada, R.id.formulario_produto_imagem);

                    if (campoProductImage.getDrawable() == null) {
                        campoProductImage.setImageDrawable(getImageProduct(context, campoNome.getText().toString()));
                    }

                    createProduct(campoNome, campoProductImage, campoPreco, campoQuantidade, campoVendedor, campoVendedorTelefone);
                })
                .setNegativeButton(this.context.getResources().getString(R.string.cancel_button), null)
                .show();
    }

    @SuppressLint("SetTextI18n")
    private void tentaPreencherFormulario(View viewCriada) {
        if (product != null) {
            TextView campoId = viewCriada.findViewById(R.id.formulario_produto_id);
            campoId.setText(String.valueOf(product.getId()));
            campoId.setVisibility(View.VISIBLE);
            EditText campoNome = getEditText(viewCriada, R.id.formulario_produto_product);
            campoNome.setText(product.getProduct());
            EditText campoPreco = getEditText(viewCriada, R.id.formulario_produto_price);
            campoPreco.setText(product.getPrice().toString());
            EditText campoQuantidade = getEditText(viewCriada, R.id.formulario_produto_quantidade);
            campoQuantidade.setText(String.valueOf(product.getQuantity()));
            EditText campoVendedor = getEditText(viewCriada, R.id.formulario_produto_seller);
            campoVendedor.setText(product.getSeller());
            EditText campoVendedorTelefone = getEditText(viewCriada, R.id.formulario_produto_seller_phone);
            campoVendedorTelefone.setText(product.getSellerPhone());
            campoVendedorTelefone.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                            (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        // Perform action on key press
                            if (editList != null){
                                Button save = editList.getButton(2);
                                save.performClick();
                            }
                        return true;
                    }
                    return false;
                }
            });

            ImageView campoProductImage = getImageView(viewCriada, R.id.formulario_produto_imagem);
            campoProductImage.setImageDrawable(getImageProduct(viewCriada.getContext(),campoNome.getText().toString()));
        }
    }

    private void createProduct(EditText campoNome, ImageView campoProductImage, EditText campoPreco, EditText campoQuantidade, EditText campoVendedor, EditText campoVendedorTelefone) {
        String nome = campoNome.getText().toString();
        BigDecimal preco = tentaConverterPreco(campoPreco);
        int quantidade = tentaConverterQuantidade(campoQuantidade);
        String vendedor = campoVendedor.getText().toString();
        String vendedorTelefone = campoVendedorTelefone.getText().toString();

        String productImage = "";

        long id = fillId();
        Product product = new Product(id, nome, productImage, preco, quantidade, vendedor, vendedorTelefone);
        listener.whenConfirmed(product);
    }

    private long fillId() {
        if (product != null) {
            return product.getId();
        }
        return 0;
    }

    private BigDecimal tentaConverterPreco(EditText campoPreco) {
        try {
            return new BigDecimal(campoPreco.getText().toString());
        } catch (NumberFormatException ignored) {
            return BigDecimal.ZERO;
        }
    }

    private int tentaConverterQuantidade(EditText campoQuantidade) {
        try {
            return Integer.parseInt(campoQuantidade.getText().toString());
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private EditText getEditText(View viewCriada, int idTextInputLayout) {
        TextInputLayout textInputLayout = viewCriada.findViewById(idTextInputLayout);
        return textInputLayout.getEditText();
    }

    private TextInputLayout getTextInputLayout(View viewCriada, int idTextInputLayout) {
        TextInputLayout textInputLayout = viewCriada.findViewById(idTextInputLayout);
        return textInputLayout;
    }

    private ImageView getImageView(View viewCriada, int idImageView) {
        ImageView image = viewCriada.findViewById(idImageView);
        return image;
    }

    public interface ListenerConfirm {
        void whenConfirmed(Product product);
    }
}
