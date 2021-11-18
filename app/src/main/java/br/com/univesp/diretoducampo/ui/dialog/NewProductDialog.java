package br.com.univesp.diretoducampo.ui.dialog;

import android.content.Context;

public class NewProductDialog extends NewProductForm {
    private static final String TITLE_ADD_NEW_PRODUCT = "Cadastrar novo produto";
    private static final String TITLE_POSITIVE_SAVE_BUTTON = "Salvar";

    public NewProductDialog(Context context,
                              ListenerConfirm listener) {
        super(context, TITLE_ADD_NEW_PRODUCT, TITLE_POSITIVE_SAVE_BUTTON, listener);
    }
}
