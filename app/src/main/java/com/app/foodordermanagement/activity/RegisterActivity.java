package com.app.foodordermanagement.activity;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.app.foodordermanagement.R;
import com.app.foodordermanagement.model.User;
import com.app.foodordermanagement.prefs.DataStoreManager;
import com.app.foodordermanagement.utils.GlobalFunction;
import com.app.foodordermanagement.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class RegisterActivity extends BaseActivity {

    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtRePassword;
    private Button btnRegister;
    private LinearLayout layoutLogin;
    private boolean isEnableButtonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initUi();
        initListener();
    }

    private void initUi() {
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtRePassword = findViewById(R.id.re_edt_password);
        btnRegister = findViewById(R.id.btn_register);
        layoutLogin = findViewById(R.id.layout_login);
    }

    private void initListener() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    edtEmail.setBackgroundResource(R.drawable.bg_white_corner_16_border_main);
                } else {
                    edtEmail.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray);
                }

                String strPassword = edtPassword.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strPassword)) {
                    isEnableButtonRegister = true;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                } else {
                    isEnableButtonRegister = false;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                }
            }
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (!StringUtil.isEmpty(s.toString())) {
                    edtPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_main);
                } else {
                    edtPassword.setBackgroundResource(R.drawable.bg_white_corner_16_border_gray);
                }

                String strEmail = edtEmail.getText().toString().trim();
                if (!StringUtil.isEmpty(s.toString()) && !StringUtil.isEmpty(strEmail)) {
                    isEnableButtonRegister = true;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_enable_corner_16);
                } else {
                    isEnableButtonRegister = false;
                    btnRegister.setBackgroundResource(R.drawable.bg_button_disable_corner_16);
                }
            }
        });

        edtRePassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String password = edtPassword.getText().toString().trim();
                String rePassword = s.toString().trim();
                if (!password.equals(rePassword)) {
                    edtRePassword.setError(getString(R.string.msg_password_not_match));
                } else {
                    edtRePassword.setError(null);
                }
                updateRegisterButtonState();
            }
        });

        layoutLogin.setOnClickListener(v -> finish());
        btnRegister.setOnClickListener(v -> onClickValidateRegister());
    }

    private void updateRegisterButtonState() {
        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        String strRePassword = edtRePassword.getText().toString().trim();
        
        isEnableButtonRegister = !StringUtil.isEmpty(strEmail) && 
                                 !StringUtil.isEmpty(strPassword) && 
                                 !StringUtil.isEmpty(strRePassword) && 
                                 strPassword.equals(strRePassword);
        
        btnRegister.setBackgroundResource(isEnableButtonRegister ? 
                R.drawable.bg_button_enable_corner_16 : 
                R.drawable.bg_button_disable_corner_16);
    }

    private void onClickValidateRegister() {
        if (!isEnableButtonRegister) return;

        String strEmail = edtEmail.getText().toString().trim();
        String strPassword = edtPassword.getText().toString().trim();
        String strRePassword = edtRePassword.getText().toString().trim();

        if (StringUtil.isEmpty(strEmail)) {
            showToastMessage(getString(R.string.msg_email_require));
        } else if (StringUtil.isEmpty(strPassword)) {
            showToastMessage(getString(R.string.msg_password_require));
        } else if (!StringUtil.isValidEmail(strEmail)) {
            showToastMessage(getString(R.string.msg_email_invalid));
        } else if (!strPassword.equals(strRePassword)) {
            showToastMessage(getString(R.string.msg_password_not_match));
        } else {
            checkExistingEmail(strEmail, strPassword);
        }
    }

    private void checkExistingEmail(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> signInMethods = task.getResult().getSignInMethods();
                        if (signInMethods != null && !signInMethods.isEmpty()) {
                            showProgressDialog(false);
                            showToastMessage(getString(R.string.msg_email_already_exists));
                        } else {
                            registerUserFirebase(email, password);
                        }
                    } else {
                        showProgressDialog(false);
                        showToastMessage(getString(R.string.msg_error_occurred));
                    }
                });
    }

    private void registerUserFirebase(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            User userObject = new User(user.getEmail(), password);
                            DataStoreManager.setUser(userObject);
                            showSuccessMessageAndRedirect();
                        }
                    } else {
                        showToastMessage(getString(R.string.msg_register_error));
                    }
                });
    }

    private void showSuccessMessageAndRedirect() {
        Toast.makeText(this, R.string.msg_register_success, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> {
            GlobalFunction.startActivity(RegisterActivity.this, LoginActivity.class);
            finish();
        }, 500); // Chờ 3 giây trước khi chuyển màn hình
    }
}
