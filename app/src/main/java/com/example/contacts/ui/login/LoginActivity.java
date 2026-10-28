package com.example.contacts.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.example.contacts.R;
import com.example.contacts.auth.AuthResult;
import com.example.contacts.data.ServiceLocator;
import com.example.contacts.databinding.ActivityLoginBinding;
import com.example.contacts.ui.BaseActivity;
import com.example.contacts.ui.main.MainActivity;
import com.example.contacts.util.AppIcon;
import com.example.contacts.util.AppIcons;
import com.example.contacts.util.Constants;
import com.example.contacts.util.ExtensionsKt;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends BaseActivity {
    private ActivityLoginBinding binding;
    private boolean passwordVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ExtensionsKt.applySystemBarsPadding(binding.scroll); // Java gọi extension của Kotlin

        AppIcons.set(binding.imgLogo, AppIcon.LOGO, R.color.white, 36);
        AppIcons.setStart(binding.tilUsername, AppIcon.USER);
        AppIcons.setStart(binding.tilPassword, AppIcon.LOCK);
        setupPasswordToggle();

        // Gõ lại thì ẩn thông báo lỗi
        ExtensionsKt.onTextChanged(binding.etUsername, text -> hideError());
        ExtensionsKt.onTextChanged(binding.etPassword, text -> hideError());

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                attemptLogin();
                return true;
            }
            return false;
        });
    }

    /** Nút ẩn/hiện mật khẩu dùng icon từ thư viện (không dùng icon mặc định của Material). */
    private void setupPasswordToggle() {
        binding.tilPassword.setEndIconMode(TextInputLayout.END_ICON_CUSTOM);
        binding.tilPassword.setEndIconContentDescription(getString(R.string.cd_toggle_password));
        renderPasswordToggle();
        binding.tilPassword.setEndIconOnClickListener(v -> {
            passwordVisible = !passwordVisible;
            renderPasswordToggle();
        });
    }

    private void renderPasswordToggle() {
        AppIcons.setEnd(binding.tilPassword, passwordVisible ? AppIcon.EYE_OFF : AppIcon.EYE);
        int cursor = binding.etPassword.getSelectionEnd();
        binding.etPassword.setTransformationMethod(
                passwordVisible ? null : PasswordTransformationMethod.getInstance());
        binding.etPassword.setSelection(Math.max(cursor, 0));
    }

    private void attemptLogin() {
        String username = String.valueOf(binding.etUsername.getText());
        String password = String.valueOf(binding.etPassword.getText());

        AuthResult result = ServiceLocator.authService().login(username, password);
        switch (result) {
            case SUCCESS:
                // Intent tường minh sang MainActivity, finish() để Back không quay lại Login
                startActivity(new Intent(this, MainActivity.class)
                        .putExtra(Constants.EXTRA_USERNAME, username.trim()));
                finish();
                break;
            case EMPTY_INPUT:
                showError(R.string.error_login_empty);
                break;
            default:
                showError(R.string.error_login_invalid);
        }
    }

    private void showError(@StringRes int message) {
        binding.tvError.setText(message);
        ExtensionsKt.setVisible(binding.tvError, true);
    }

    private void hideError() {
        ExtensionsKt.setVisible(binding.tvError, false);
    }
}
