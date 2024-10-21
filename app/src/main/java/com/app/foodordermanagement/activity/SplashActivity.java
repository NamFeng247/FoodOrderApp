package com.app.foodordermanagement.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.animation.AlphaAnimation;

import com.app.foodordermanagement.R;
import com.app.foodordermanagement.prefs.DataStoreManager;
import com.app.foodordermanagement.utils.GlobalFunction;
import com.app.foodordermanagement.utils.StringUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Tìm ImageView và TextView trong layout
        ImageView logoImageView = findViewById(R.id.logo);
        TextView splashTextView = findViewById(R.id.tvSplashText);

        // Đặt văn bản cho TextView
        splashTextView.setText("Chạm đến hương vị, thưởng thức niềm vui!");

        // Tạo animation để chữ xuất hiện từ từ
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(2000); // Thời gian để chữ hiện ra hoàn toàn (2 giây)
        fadeIn.setStartOffset(600); // Bắt đầu sau 1 giây
        fadeIn.setFillAfter(true);

        // Áp dụng animation cho TextView
        splashTextView.startAnimation(fadeIn);

        // Tạo RequestOptions để điều chỉnh kích thước và làm mượt ảnh
        RequestOptions options = new RequestOptions()
                .override(500, 500) // Đặt kích thước mong muốn (ví dụ: 300x300 pixels)
                .centerCrop() // Cắt ảnh để vừa với kích thước mới
                .diskCacheStrategy(DiskCacheStrategy.ALL); // Lưu cache để tải nhanh hơn

        // Tải ảnh GIF vào ImageView với các tùy chọn mới
        Glide.with(this)
                .asGif()
                .load(R.drawable.foodgif)
                .apply(options)
                .into(logoImageView);

        // Tạo Handler để chuyển hoạt động sau 5 giây
        Handler handler = new Handler();
        handler.postDelayed(this::goToActivity, 5000);
    }

    private void goToActivity() {
        if (DataStoreManager.getUser() != null
                && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            GlobalFunction.startActivity(this, MainActivity.class);
        } else {
            GlobalFunction.startActivity(this, LoginActivity.class);
        }
        finish();
    }
}
