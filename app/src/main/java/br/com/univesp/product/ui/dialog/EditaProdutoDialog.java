package br.com.univesp.product.ui.dialog;

import android.content.Context;

import br.com.univesp.product.model.Product;

public class EditaProdutoDialog extends FormularioProdutoDialog {

    private static final String TITULO = "Editando produto";
    private static final String TITULO_BOTAO_POSITIVO = "Editar";

    public EditaProdutoDialog(Context context,
                              Product product,
                              ConfirmacaoListener listener) {
        super(context, TITULO, TITULO_BOTAO_POSITIVO, listener, product);
    }
}
