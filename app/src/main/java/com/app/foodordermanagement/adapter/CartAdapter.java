package com.app.foodordermanagement.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.foodordermanagement.R;
import com.app.foodordermanagement.model.Food;
import com.app.foodordermanagement.utils.Constant;
import com.app.foodordermanagement.utils.GlideUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final List<Food> listFood;
    private final IClickCartListener iClickCartListener;

    public interface IClickCartListener {
        void onClickDeleteItem(Food food, int position);
        void onClickUpdateItem(Food food, int position);
        void onClickEditItem(Food food);
    }

    public CartAdapter(List<Food> list, IClickCartListener listener) {
        this.listFood = list;
        this.iClickCartListener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Food food = listFood.get(position);
        if (food == null) return;

        GlideUtils.loadUrl(food.getImage(), holder.imgFood);
        holder.tvName.setText(food.getName());
        String strPrice = food.getPriceOneFood() + Constant.CURRENCY;
        holder.tvPrice.setText(strPrice);
        holder.tvOption.setText(food.getOption());
        String strQuantity = "x" + food.getCount();
        holder.tvQuantity.setText(strQuantity);
        holder.tvCount.setText(String.valueOf(food.getCount()));

        holder.tvSub.setOnClickListener(v -> {
            String strCount = holder.tvCount.getText().toString();
            int count = Integer.parseInt(strCount);
            if (count <= 1) {
                return;
            }
            int newCount = count - 1;
            holder.tvCount.setText(String.valueOf(newCount));

            int totalPrice = food.getPriceOneFood() * newCount;
            food.setCount(newCount);
            food.setTotalPrice(totalPrice);

            iClickCartListener.onClickUpdateItem(food, holder.getAdapterPosition());
        });

        holder.tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.tvCount.getText().toString()) + 1;
            holder.tvCount.setText(String.valueOf(newCount));

            int totalPrice = food.getPriceOneFood() * newCount;
            food.setCount(newCount);
            food.setTotalPrice(totalPrice);

            iClickCartListener.onClickUpdateItem(food, holder.getAdapterPosition());
        });

        holder.imgEdit.setOnClickListener(v -> iClickCartListener.onClickEditItem(food));
        holder.imgDelete.setOnClickListener(v
                -> iClickCartListener.onClickDeleteItem(food, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        if (listFood != null) {
            return listFood.size();
        }
        return 0;
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgFood;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvOption;
        private final TextView tvQuantity;
        private final TextView tvSub;
        private final TextView tvCount;
        private final TextView tvAdd;
        private final ImageView imgEdit;
        private final ImageView imgDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.img_drink);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvOption = itemView.findViewById(R.id.tv_option);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvSub = itemView.findViewById(R.id.tv_sub);
            tvAdd = itemView.findViewById(R.id.tv_add);
            tvCount = itemView.findViewById(R.id.tv_count);
            imgEdit = itemView.findViewById(R.id.img_edit);
            imgDelete = itemView.findViewById(R.id.img_delete);
        }
    }
}
