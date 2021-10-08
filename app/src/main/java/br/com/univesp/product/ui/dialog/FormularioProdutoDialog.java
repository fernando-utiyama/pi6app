package br.com.univesp.product.ui.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.math.BigDecimal;

import androidx.appcompat.app.AlertDialog;
import br.com.univesp.product.R;
import br.com.univesp.product.model.Product;

abstract public class FormularioProdutoDialog {

    private final String titulo;
    private final String tituloBotaoPositivo;
    private final ConfirmacaoListener listener;
    private final Context context;
    private static final String TITULO_BOTAO_NEGATIVO = "Cancelar";
    private Product product;

    FormularioProdutoDialog(Context context,
                            String titulo,
                            String tituloBotaoPositivo,
                            ConfirmacaoListener listener) {
        this.titulo = titulo;
        this.tituloBotaoPositivo = tituloBotaoPositivo;
        this.listener = listener;
        this.context = context;
    }

    FormularioProdutoDialog(Context context,
                            String titulo,
                            String tituloBotaoPositivo,
                            ConfirmacaoListener listener,
                            Product product) {
        this(context, titulo, tituloBotaoPositivo, listener);
        this.product = product;
    }

    public void show() {
        @SuppressLint("InflateParams") View viewCriada = LayoutInflater.from(context)
                .inflate(R.layout.formulario_produto, null);
        tentaPreencherFormulario(viewCriada);
        new AlertDialog.Builder(context)
                .setTitle(titulo)
                .setView(viewCriada)
                .setPositiveButton(tituloBotaoPositivo, (dialog, which) -> {
                    EditText campoNome = getEditText(viewCriada, R.id.formulario_produto_product);
                    EditText campoPreco = getEditText(viewCriada, R.id.formulario_produto_price);
                    EditText campoQuantidade = getEditText(viewCriada, R.id.formulario_produto_quantidade);
                    EditText campoVendedor = getEditText(viewCriada, R.id.formulario_produto_seller);
                    criaProduto(campoNome, campoPreco, campoQuantidade, campoVendedor);
                })
                .setNegativeButton(TITULO_BOTAO_NEGATIVO, null)
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
        }
    }

    private void criaProduto(EditText campoNome, EditText campoPreco, EditText campoQuantidade, EditText campoVendedor) {
        String nome = campoNome.getText().toString();
        BigDecimal preco = tentaConverterPreco(campoPreco);
        int quantidade = tentaConverterQuantidade(campoQuantidade);
        String vendedor = campoVendedor.getText().toString();

        long id = preencheId();
        Product product = new Product(id, nome, preco, quantidade, vendedor);
        listener.quandoConfirmado(product);
    }

    private long preencheId() {
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

    public interface ConfirmacaoListener {
        void quandoConfirmado(Product product);
    }


}
