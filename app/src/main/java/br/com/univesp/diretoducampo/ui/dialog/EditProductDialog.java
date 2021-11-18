package br.com.univesp.diretoducampo.ui.dialog;

import android.content.Context;

import br.com.univesp.diretoducampo.model.Product;

public class EditProductDialog extends NewProductForm {

    private static final String TITLE_EDIT_PRODUCT = "Editar produto";
    private static final String TITLE_POSITIVE_EDIT_BUTTON = "Editar";

    public EditProductDialog(Context context,
                              Product product,
                              ListenerConfirm listener) {
        super(context, TITLE_EDIT_PRODUCT, TITLE_POSITIVE_EDIT_BUTTON, listener, product);
    }
}
