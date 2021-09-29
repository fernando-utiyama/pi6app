package br.com.univesp.product.ui.recyclerview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import br.com.univesp.product.R;
import br.com.univesp.product.model.Product;

public class ListaProdutosAdapter extends
        RecyclerView.Adapter<ListaProdutosAdapter.ViewHolder> {

    private final OnItemClickListener onItemClickListener;
    private OnItemClickRemoveContextMenuListener
            onItemClickRemoveContextMenuListener = (posicao, produtoRemovido) -> {
    };
    private final Context context;
    private final List<Product> products = new ArrayList<>();

    public ListaProdutosAdapter(Context context,
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
        View viewCriada = LayoutInflater.from(context).inflate(R.layout.produto_item, parent, false);
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
    }

    public void edita(int posicao, Product product) {
        products.set(posicao, product);
        notifyItemChanged(posicao);
    }

    public void remove(int posicao) {
        products.remove(posicao);
        notifyItemRemoved(posicao);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView campoId;
        private final TextView campoNome;
        private final TextView campoPreco;
        private final TextView campoQuantidade;
        private final TextView campoVendedor;
        private Product product;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            campoId = itemView.findViewById(R.id.produto_item_id);
            campoNome = itemView.findViewById(R.id.produto_item_product);
            campoPreco = itemView.findViewById(R.id.produto_item_price);
            campoQuantidade = itemView.findViewById(R.id.produto_item_among);
            campoVendedor = itemView.findViewById(R.id.produto_item_seller);
            configuraItemClique(itemView);
            configuraMenuDeContexto(itemView);
        }

        private void configuraMenuDeContexto(@NonNull View itemView) {
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
            });
        }

        private void configuraItemClique(@NonNull View itemView) {
            itemView.setOnClickListener(v -> onItemClickListener
                    .onItemClick(getAdapterPosition(), product));
        }

        void vincula(Product product) {
            this.product = product;
            campoId.setText(String.valueOf(product.getId()));
            campoNome.setText(product.getProduct());
            campoPreco.setText(formataParaMoeda(product.getPrice()));
            campoQuantidade.setText(String.valueOf(product.getAmong()));
            campoVendedor.setText(String.valueOf(product.getSeller()));
        }

        private String formataParaMoeda(BigDecimal valor) {
            NumberFormat formatador = NumberFormat.getCurrencyInstance();
            return formatador.format(valor);
        }

    }

    public interface OnItemClickListener {
        void onItemClick(int posicao, Product product);
    }

    public interface OnItemClickRemoveContextMenuListener {
        void onItemClick(int posicao, Product productRemovido);
    }

}
