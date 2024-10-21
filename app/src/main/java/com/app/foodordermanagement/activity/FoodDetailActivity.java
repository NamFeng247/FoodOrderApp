package com.app.foodordermanagement.activity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.foodordermanagement.MyApplication;
import com.app.foodordermanagement.R;
import com.app.foodordermanagement.adapter.ToppingAdapter;
import com.app.foodordermanagement.database.FoodDatabase;
import com.app.foodordermanagement.event.DisplayCartEvent;
import com.app.foodordermanagement.model.Food;
import com.app.foodordermanagement.model.RatingReview;
import com.app.foodordermanagement.model.Topping;
import com.app.foodordermanagement.utils.Constant;
import com.app.foodordermanagement.utils.GlideUtils;
import com.app.foodordermanagement.utils.GlobalFunction;
import com.app.foodordermanagement.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class FoodDetailActivity extends BaseActivity {

    private ImageView imgFood;
    private TextView tvName;
    private TextView tvPriceSale;
    private TextView tvDescription;
    private TextView tvSub;
    private TextView tvAdd;
    private TextView tvCount;
    private RelativeLayout layoutRatingAndReview;
    private TextView tvRate;
    private TextView tvCountReview;
    private RecyclerView rcvTopping;
    private EditText edtNotes;
    private TextView tvTotal;
    private TextView tvAddOrder;

    private int mFoodId;
    private Food mFoodOld;
    private Food mFood;
    private List<Topping> listTopping;


    private String variantText = "";
    private String sizeText = "";
    private String sugarText = "";
    private String iceText = "";
    private String toppingIdsText = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        getDataIntent();
        initUi();
        getFoodDetailFromFirebase();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) return;
        mFoodId = bundle.getInt(Constant.FOOD_ID);
        if (bundle.get(Constant.FOOD_OBJECT) != null) {
            mFoodOld = (Food) bundle.get(Constant.FOOD_OBJECT);
        }
    }

    private void initUi() {
        imgFood = findViewById(R.id.img_drink);
        tvName = findViewById(R.id.tv_name);
        tvPriceSale = findViewById(R.id.tv_price_sale);
        tvDescription = findViewById(R.id.tv_description);
        tvSub = findViewById(R.id.tv_sub);
        tvAdd = findViewById(R.id.tv_add);
        tvCount = findViewById(R.id.tv_count);
        layoutRatingAndReview = findViewById(R.id.layout_rating_and_review);
        tvCountReview = findViewById(R.id.tv_count_review);
        tvRate = findViewById(R.id.tv_rate);
        edtNotes = findViewById(R.id.edt_notes);
        tvTotal = findViewById(R.id.tv_total);
        tvAddOrder = findViewById(R.id.tv_add_order);
    }

    private void getFoodDetailFromFirebase() {
        showProgressDialog(true);
        MyApplication.get(this).getFoodDetailDatabaseReference(mFoodId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        showProgressDialog(false);
                        mFood = snapshot.getValue(Food.class);
                        if (mFood == null) return;

                        initToolbar();
                        initData();
                        initListener();
                        /*getListToppingFromFirebase();*/
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void initToolbar() {
        ImageView imgToolbarBack = findViewById(R.id.img_toolbar_back);
        TextView tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        imgToolbarBack.setOnClickListener(view -> finish());
        tvToolbarTitle.setText(mFood.getName());
    }

    private void initData() {
        if (mFood == null) return;
        GlideUtils.loadUrlBanner(mFood.getBanner(), imgFood);
        tvName.setText(mFood.getName());
        String strPrice = mFood.getRealPrice() + Constant.CURRENCY;
        tvPriceSale.setText(strPrice);
        tvDescription.setText(mFood.getDescription());
        if (mFoodOld != null) {
            mFood.setCount(mFoodOld.getCount());
        } else {
            mFood.setCount(1);
        }
        tvCount.setText(String.valueOf(mFood.getCount()));
        tvRate.setText(String.valueOf(mFood.getRate()));
        String strCountReview = "(" + mFood.getCountReviews() + ")";
        tvCountReview.setText(strCountReview);

        if (mFoodOld != null) {
            if (StringUtil.isEmpty(mFoodOld.getToppingIds())) calculatorTotalPrice();
        } else {
            calculatorTotalPrice();
        }
        if (mFoodOld != null) {

            edtNotes.setText(mFoodOld.getNote());
        }
    }

    private void initListener() {
        tvSub.setOnClickListener(v -> {
            int count = Integer.parseInt(tvCount.getText().toString());
            if (count <= 1) {
                return;
            }
            int newCount = Integer.parseInt(tvCount.getText().toString()) - 1;
            tvCount.setText(String.valueOf(newCount));

            calculatorTotalPrice();
        });

        tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(tvCount.getText().toString()) + 1;
            tvCount.setText(String.valueOf(newCount));

            calculatorTotalPrice();
        });

        layoutRatingAndReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            RatingReview ratingReview = new RatingReview(RatingReview.TYPE_RATING_REVIEW_DRINK,
                    String.valueOf(mFood.getId()));
            bundle.putSerializable(Constant.RATING_REVIEW_OBJECT, ratingReview);
            GlobalFunction.startActivity(FoodDetailActivity.this,
                    RatingReviewActivity.class, bundle);
        });

        tvAddOrder.setOnClickListener(view -> {
            mFood.setOption(getAllOption());
//            mFood.setVariant(currentVariant);
//            mFood.setSize(currentSize);
            /*mFood.setSugar(currentSugar);
            mFood.setIce(currentIce);*/
            /*mFood.setToppingIds(toppingIdsText);*/
            String notes = edtNotes.getText().toString().trim();
            if (!StringUtil.isEmpty(notes)) {
                mFood.setNote(notes);
            }

            if (!isFoodInCart()) {
                FoodDatabase.getInstance(FoodDetailActivity.this).foodDAO().insertFood(mFood);
            } else {
                FoodDatabase.getInstance(FoodDetailActivity.this).foodDAO().updateFood(mFood);
            }
            GlobalFunction.startActivity(FoodDetailActivity.this, CartActivity.class);
            EventBus.getDefault().post(new DisplayCartEvent());
            finish();
        });
    }

//    private void setValueToppingVariant(String type) {
//        currentVariant = type;
//        switch (type) {
//            case Topping.VARIANT_ICE:
//                tvVariantIce.setBackgroundResource(R.drawable.bg_main_corner_6);
//                tvVariantIce.setTextColor(ContextCompat.getColor(this, R.color.white));
//                tvVariantHot.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvVariantHot.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//
//                variantText = getString(R.string.label_variant) + " " + tvVariantIce.getText().toString();
//                break;
//
//            case Topping.VARIANT_HOT:
//                tvVariantIce.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvVariantIce.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//                tvVariantHot.setBackgroundResource(R.drawable.bg_main_corner_6);
//                tvVariantHot.setTextColor(ContextCompat.getColor(this, R.color.white));
//
//                variantText = getString(R.string.label_variant) + " " + tvVariantHot.getText().toString();
//                break;
//        }
//    }

//    private void setValueToppingSize(String type) {
//        currentSize = type;
//        switch (type) {
//            case Topping.SIZE_REGULAR:
//                tvSizeRegular.setBackgroundResource(R.drawable.bg_main_corner_6);
//                tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.white));
//                tvSizeMedium.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//                tvSizeLarge.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//
//                sizeText = getString(R.string.label_size) + " " + tvSizeRegular.getText().toString();
//                break;
//
//            case Topping.SIZE_MEDIUM:
//                tvSizeRegular.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//                tvSizeMedium.setBackgroundResource(R.drawable.bg_main_corner_6);
//                tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.white));
//                tvSizeLarge.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//
//                sizeText = getString(R.string.label_size) + " " + tvSizeMedium.getText().toString();
//                break;
//
//            case Topping.SIZE_LARGE:
//                tvSizeRegular.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvSizeRegular.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//                tvSizeMedium.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
//                tvSizeMedium.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
//                tvSizeLarge.setBackgroundResource(R.drawable.bg_main_corner_6);
//                tvSizeLarge.setTextColor(ContextCompat.getColor(this, R.color.white));
//
//                sizeText = tvSizeLarge.getText().toString() + " "
//                        + getString(R.string.label_size);
//                break;
//        }
//    }

    /*private void setValueToppingSugar(String type) {
        currentSugar = type;
        switch (type) {
            case Topping.SUGAR_NORMAL:
                tvSugarNormal.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSugarNormal.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvSugarLess.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSugarLess.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));

                sugarText = tvSugarNormal.getText().toString() + " "
                        + getString(R.string.label_sugar);
                break;

            case Topping.SUGAR_LESS:
                tvSugarNormal.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvSugarNormal.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
                tvSugarLess.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvSugarLess.setTextColor(ContextCompat.getColor(this, R.color.white));

                sugarText = tvSugarLess.getText().toString() + " "
                        + getString(R.string.label_sugar);
                break;
        }
    }*/

    /*private void setValueToppingIce(String type) {
        currentIce = type;
        switch (type) {
            case Topping.ICE_NORMAL:
                tvIceNormal.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvIceNormal.setTextColor(ContextCompat.getColor(this, R.color.white));
                tvIceLess.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvIceLess.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));

                iceText = tvIceNormal.getText().toString() + " " + getString(R.string.label_ice);
                break;

            case Topping.ICE_LESS:
                tvIceNormal.setBackgroundResource(R.drawable.bg_white_corner_6_border_main);
                tvIceNormal.setTextColor(ContextCompat.getColor(this, R.color.dark_brown));
                tvIceLess.setBackgroundResource(R.drawable.bg_main_corner_6);
                tvIceLess.setTextColor(ContextCompat.getColor(this, R.color.white));

                iceText = tvIceLess.getText().toString() + " " + getString(R.string.label_ice);
                break;
        }
    }

    private void getListToppingFromFirebase() {
        MyApplication.get(this).getToppingDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (listTopping != null) {
                            listTopping.clear();
                        } else {
                            listTopping = new ArrayList<>();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Topping topping = dataSnapshot.getValue(Topping.class);
                            if (topping != null) {
                                listTopping.add(topping);
                            }
                        }
                        displayListTopping();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void displayListTopping() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvTopping.setLayoutManager(linearLayoutManager);
        toppingAdapter = new ToppingAdapter(listTopping, this::handleClickItemTopping);
        rcvTopping.setAdapter(toppingAdapter);
        handleSetToppingDrinkOld();
    }*/

    /*@SuppressLint("NotifyDataSetChanged")
    private void handleSetToppingDrinkOld() {
        if (mFoodOld == null || StringUtil.isEmpty(mFoodOld.getToppingIds())) return;
        if (listTopping == null || listTopping.isEmpty()) return;
        String[] tempId = mFoodOld.getToppingIds().split(",");
        for (String s : tempId) {
            for (Topping topping : listTopping) {
                if (topping.getId() == Integer.parseInt(s)) {
                    topping.setSelected(true);
                    break;
                }
            }
        }
        if (toppingAdapter != null) toppingAdapter.notifyDataSetChanged();
        calculatorTotalPrice();
    }*/

    /*@SuppressLint("NotifyDataSetChanged")
    private void handleClickItemTopping(Topping topping) {
        for (Topping toppingEntity : listTopping) {
            if (toppingEntity.getId() == topping.getId()) {
                toppingEntity.setSelected(!toppingEntity.isSelected());
            }
        }
        if (toppingAdapter != null) toppingAdapter.notifyDataSetChanged();
        calculatorTotalPrice();
    }*/

    private void calculatorTotalPrice() {
        int count = Integer.parseInt(tvCount.getText().toString().trim());
        int priceOneFood = mFood.getRealPrice() + getTotalPriceTopping();
        int totalPrice = priceOneFood * count;
        String strTotalPrice = totalPrice + Constant.CURRENCY;
        tvTotal.setText(strTotalPrice);

        mFood.setCount(count);
        mFood.setPriceOneFood(priceOneFood);
        mFood.setTotalPrice(totalPrice);
    }

    private int getTotalPriceTopping() {
        if (listTopping == null || listTopping.isEmpty()) return 0;
        int total = 0;
        for (Topping topping : listTopping) {
            if (topping.isSelected()) {
                total += topping.getPrice();
            }
        }
        return total;
    }

    /*private String getAllToppingSelected() {
        if (listTopping == null || listTopping.isEmpty()) return "";
        String strTopping = "";
        for (Topping topping : listTopping) {
            if (topping.isSelected()) {
                if (StringUtil.isEmpty(strTopping)) {
                    strTopping += topping.getName();
                    toppingIdsText += String.valueOf(topping.getId());
                } else {
                    strTopping += ", " + topping.getName();
                }
                if (StringUtil.isEmpty(toppingIdsText)) {
                    toppingIdsText += String.valueOf(topping.getId());
                } else {
                    toppingIdsText += "," + topping.getId();
                }
            }
        }
        return strTopping;
    }*/

    private boolean isFoodInCart() {
        List<Food> list = FoodDatabase.getInstance(this)
                .foodDAO().checkFoodInCart(mFood.getId());
        return list != null && !list.isEmpty();
    }

    private String getAllOption() {
        String option = variantText + ", " + sizeText/* + ", " + sugarText + ", " + iceText*/;
        /*if (!StringUtil.isEmpty(getAllToppingSelected())) {
            option += ", " + getAllToppingSelected();
        }*/
        String notes = edtNotes.getText().toString().trim();
        if (!StringUtil.isEmpty(notes)) {
            option += ", " + notes;
        }
        return option;
    }
}
