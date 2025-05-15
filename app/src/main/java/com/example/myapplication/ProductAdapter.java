package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> implements Filterable {
    private List<Product> productList;
    private List<Product> productListFiltered;
    private Context context;
    private OnAddToCartClickListener listener;

    public interface OnAddToCartClickListener {
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(Context context, List<Product> productList, OnAddToCartClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = new ArrayList<>(productList);
        this.listener = listener;
    }

    public void setFilteredList(List<Product> filteredList) {
        this.productListFiltered = new ArrayList<>(filteredList);
        this.productList = new ArrayList<>(filteredList);
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = constraint.toString().toLowerCase();
                List<Product> filteredList = new ArrayList<>();
                if (query.isEmpty()) {
                    filteredList.addAll(productListFiltered);
                } else {
                    for (Product product : productListFiltered) {
                        if (product.getName().toLowerCase().contains(query)) {
                            filteredList.add(product);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            @SuppressWarnings("unchecked")
            protected void publishResults(CharSequence constraint, FilterResults results) {
                // Safely cast the results.values to List<Product>
                List<Product> filtered = (List<Product>) results.values;
                productList.clear();
                productList.addAll(filtered);
                notifyDataSetChanged();
            }
        };
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(String.format("$%.2f", product.getPrice()));
        Glide.with(context).load(product.getImageUrl()).into(holder.ivProduct);
        holder.btnAddToCart.setOnClickListener(v -> listener.onAddToCartClick(product));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct;
        TextView tvName, tvPrice;
        Button btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.iv_product);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
        }
    }
}