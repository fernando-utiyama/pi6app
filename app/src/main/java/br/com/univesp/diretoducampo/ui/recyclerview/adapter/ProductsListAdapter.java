package br.com.univesp.diretoducampo.ui.recyclerview.adapter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import br.com.univesp.diretoducampo.R;
import br.com.univesp.diretoducampo.model.Product;

public class ProductsListAdapter extends
        RecyclerView.Adapter<ProductsListAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private OnItemClickRemoveContextMenuListener
            onItemClickRemoveContextMenuListener = (posicao, produtoRemovido) -> {
    };
    private final Context context;
    private final List<Product> products = new ArrayList<>();

    public ProductsListAdapter(Context context,
                               OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
        this.context = context;
    }

    public void setOnItemClickRemoveContextMenuListener(OnItemClickRemoveContextMenuListener onItemClickRemoveContextMenuListener) {
        this.onItemClickRemoveContextMenuListener = onItemClickRemoveContextMenuListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View viewCriada = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
        return new ViewHolder(viewCriada);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.vincula(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void atualiza(List<Product> products) {
        notifyItemRangeRemoved(0, this.products.size());
        this.products.clear();
        this.products.addAll(products);
        this.notifyItemRangeInserted(0, this.products.size());
    }

    public void adiciona(Product... products) {
        int tamanhoAtual = this.products.size();
        Collections.addAll(this.products, products);
        int tamanhoNovo = this.products.size();
        notifyItemRangeInserted(tamanhoAtual, tamanhoNovo);
        Toast.makeText(context, "Produto adicionado com sucesso!", Toast.LENGTH_LONG).show();
    }

    public void edita(int posicao, Product product) {
        products.set(posicao, product);
        notifyItemChanged(posicao);
        Toast.makeText(context, "Produto " + product.getProduct() + " editado com sucesso!", Toast.LENGTH_LONG).show();
    }

    public void remove(int posicao, Product removedProduct) {
        products.remove(posicao);
        notifyItemRemoved(posicao);
        Toast.makeText(context, "Produto " + removedProduct.getProduct() + " foi removido com sucesso!", Toast.LENGTH_LONG).show();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView campoId;
        private final TextView campoNome;
        private final TextView campoPreco;
        private final TextView campoQuantidade;
        private final TextView campoVendedor;
        private final TextView campoVendedorTelefone;
        private final ImageView campoProductImage;
        private Product product;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            campoId = itemView.findViewById(R.id.produto_item_id);
            campoNome = itemView.findViewById(R.id.produto_item_product);
            campoPreco = itemView.findViewById(R.id.produto_item_price);
            campoQuantidade = itemView.findViewById(R.id.produto_item_among);
            campoVendedor = itemView.findViewById(R.id.produto_item_seller);
            campoVendedorTelefone = itemView.findViewById(R.id.produto_item_seller_phone);
            campoProductImage = itemView.findViewById(R.id.product_image);

            setItemClick(itemView);
            setContextMenu(itemView);
        }

        public Bitmap drawableToBitmap (Drawable drawable) {
            Bitmap bitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    return bitmapDrawable.getBitmap();
                }
            }

            if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }



        private void setContextMenu(@NonNull View itemView) {
            itemView.setOnCreateContextMenuListener((menu, v, menuInfo) -> {
                new MenuInflater(context).inflate(R.menu.lista_produtos_menu, menu);
                menu.findItem(R.id.menu_lista_produtos_remove)
                        .setOnMenuItemClickListener(
                                item -> {
                                    int posicaoProduto = getAdapterPosition();
                                    onItemClickRemoveContextMenuListener
                                            .onItemClick(posicaoProduto, product);
                                    return true;
                                });

                menu.findItem(R.id.menu_products_share_social_media)
                        .setOnMenuItemClickListener(
                                item -> {
                                    try {
                                        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            Toast.makeText(itemView.getContext(), "Por favor dê as permissões pedidas para continuar. Tente novamente.", Toast.LENGTH_LONG).show();
                                        } else if(ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            Toast.makeText(itemView.getContext(), "Por favor dê as permissões pedidas para continuar. Tente novamente.", Toast.LENGTH_LONG).show();
                                        } else {
                                            Drawable foundDrawable = getImageProduct(context, product.getProduct());
                                            Bitmap productImage = drawableToBitmap(foundDrawable);
                                            String path = MediaStore.Images.Media.insertImage(itemView.getContext().getContentResolver(), productImage,
                                                    product.getProduct().concat(".jpg"), null);
                                            Uri imageUri = Uri.parse(path);

                                            String shareBody = "Veja o produto " + product.getProduct() + " do vendedor " + product.getSeller() +
                                                    " / Telefone: " + product.getSellerPhone() + " no app DiretoDuCampo! \n Baixe agora mesmo em: https://bit.ly/DiretoDuCampoApp";

                                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                            shareIntent.setAction(Intent.ACTION_SEND);
                                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                            shareIntent.setType("text/plain");

                                            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                                            shareIntent.setType("image/jpeg");
                                            itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Compartilhar produto com..."));
                                        }
                                    } catch (android.content.ActivityNotFoundException ex) {
                                        Log.e(this.getClass().getName(),ex.getMessage());
                                    } catch (Exception ex) {
                                        Log.e(this.getClass().getName(),ex.getMessage());
                                    }
                                    return true;
                                });
                });
        }

        private void setItemClick(@NonNull View itemView) {
            itemView.setOnClickListener(v -> onItemClickListener
                    .onItemClick(this.getAbsoluteAdapterPosition(), product));
        }

        private Drawable getImageProduct(Context ctx, String productName) {
            Drawable foundDrawable = null;

            if (productName.contains("bacaxi")) {
                foundDrawable = ctx.getDrawable(R.drawable.abacaxi);
            } else if (productName.contains("cerola")) {
                foundDrawable = ctx.getDrawable(R.drawable.acerola);
            } else if (productName.contains("cula")) {
                foundDrawable = ctx.getDrawable(R.drawable.rucula);
            } else if (productName.contains("açã")) {
                foundDrawable = ctx.getDrawable(R.drawable.maca);
            } else if (productName.contains("elão")) {
                foundDrawable = ctx.getDrawable(R.drawable.melao);
            } else {
                foundDrawable = ctx.getDrawable(R.drawable.logo);
            }

            return foundDrawable;
        }

        void vincula(Product product) {
            this.product = product;
            campoId.setText(String.valueOf(product.getId()));
            campoNome.setText(product.getProduct());
            campoPreco.setText(formataParaMoeda(product.getPrice()));
            campoQuantidade.setText(String.valueOf(product.getQuantity()));
            campoVendedor.setText(String.valueOf(product.getSeller()));

            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            String countryIso = telephonyManager.getNetworkCountryIso().toUpperCase();

            campoVendedorTelefone.setText(PhoneNumberUtils.formatNumber(String.valueOf(product.getSellerPhone()), countryIso));
            campoProductImage.setImageDrawable(getImageProduct(context,product.getProduct()));
        }

        private String formataParaMoeda(BigDecimal valor) {
            NumberFormat formatador = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            return formatador.format(valor);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position, Product product);
    }

    public interface OnItemClickRemoveContextMenuListener {
        void onItemClick(int position, Product removedProduct);
    }

}
