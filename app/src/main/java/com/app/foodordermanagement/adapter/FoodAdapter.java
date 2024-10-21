package com.app.foodordermanagement.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.foodordermanagement.R;
import com.app.foodordermanagement.listener.IClickFoodListener;
import com.app.foodordermanagement.model.Food;
import com.app.foodordermanagement.utils.Constant;
import com.app.foodordermanagement.utils.GlideUtils;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private final List<Food> listFood;
    private final IClickFoodListener iClickFoodListener;

    public FoodAdapter(List<Food> list, IClickFoodListener listener) {
        this.listFood = list;
        this.iClickFoodListener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = listFood.get(position);
        if (food == null) return;

        GlideUtils.loadUrl(food.getImage(), holder.imgFood);
        holder.tvName.setText(food.getName());
        holder.tvDescription.setText(food.getDescription());
        holder.tvRate.setText(String.valueOf(food.getRate()));

        if (food.getSale() <= 0) {
            holder.tvPrice.setVisibility(View.GONE);
            String strPrice = food.getPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strPrice);
        } else {
            holder.tvPrice.setVisibility(View.VISIBLE);

            String strOldPrice = food.getPrice() + Constant.CURRENCY;
            holder.tvPrice.setText(strOldPrice);
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = food.getRealPrice() + Constant.CURRENCY;
            holder.tvPriceSale.setText(strRealPrice);
        }

        holder.layoutItem.setOnClickListener(view
                -> iClickFoodListener.onClickFoodItem(food));
    }

    @Override
    public int getItemCount() {
        if (listFood != null) {
            return listFood.size();
        }
        return 0;
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgFood;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvPriceSale;
        private final TextView tvDescription;
        private final TextView tvRate;
        private final LinearLayout layoutItem;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.img_drink);
            tvName = itemView.findViewById(R.id.tv_name);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvPriceSale = itemView.findViewById(R.id.tv_price_sale);
            tvDescription = itemView.findViewById(R.id.tv_description);
            tvRate = itemView.findViewById(R.id.tv_rate);
            layoutItem = itemView.findViewById(R.id.layout_item);
        }
    }
}
