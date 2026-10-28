package com.example.contacts.ui.detail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.IntentCompat;

import com.example.contacts.R;
import com.example.contacts.data.ContactRepository;
import com.example.contacts.data.ServiceLocator;
import com.example.contacts.databinding.ActivityContactDetailBinding;
import com.example.contacts.domain.model.Contact;
import com.example.contacts.ui.BaseActivity;
import com.example.contacts.ui.add.AddContactActivity;
import com.example.contacts.util.AppIcon;
import com.example.contacts.util.AppIcons;
import com.example.contacts.util.Constants;
import com.example.contacts.util.ExtensionsKt;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ContactDetailActivity extends BaseActivity {
    private ActivityContactDetailBinding binding;
    private final ContactRepository repository = ServiceLocator.contactRepository();
    private Contact contact;

    private final ActivityResultLauncher<Intent> editLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    setResult(RESULT_OK); // báo danh sách đọc lại dữ liệu
                    reload();
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ExtensionsKt.applySystemBarsPadding(binding.getRoot());

        // Nhận Contact qua Intent, nhưng luôn đọc bản mới nhất từ repository theo id
        Contact received = IntentCompat.getParcelableExtra(getIntent(), Constants.EXTRA_CONTACT, Contact.class);
        contact = received == null ? null : repository.findById(received.getId());
        if (contact == null) {
            finish();
            return;
        }

        setupIcons();
        bind();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnBackBottom.setOnClickListener(v -> finish());
        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddContactActivity.class);
            intent.putExtra(Constants.EXTRA_CONTACT, contact);
            editLauncher.launch(intent);
        });
        // Intent ngầm định ACTION_DIAL (tel:SĐT): chỉ mở màn hình quay số, không cần quyền CALL_PHONE
        binding.btnCall.setOnClickListener(v ->
                ExtensionsKt.startSafely(this, ExtensionsKt.toDialIntent(contact.getPhone())));
        binding.btnSms.setOnClickListener(v ->
                ExtensionsKt.startSafely(this, ExtensionsKt.toSmsIntent(contact.getPhone())));
        binding.btnFavoriteAction.setOnClickListener(v -> binding.cbFavorite.toggle());
        binding.btnDelete.setOnClickListener(v -> confirmDelete());
        binding.rowPhone.setOnClickListener(v -> {
            ExtensionsKt.copyToClipboard(this, "phone", contact.getPhone());
            Toast.makeText(this, R.string.msg_copied, Toast.LENGTH_SHORT).show();
        });
    }

    private void setupIcons() {
        AppIcons.set(binding.btnBack, AppIcon.BACK);
        AppIcons.set(binding.btnEdit, AppIcon.EDIT);
        AppIcons.set(binding.btnCall, AppIcon.CALL, R.color.ink, 24);
        AppIcons.set(binding.btnSms, AppIcon.SMS, R.color.ink, 24);
        AppIcons.set(binding.btnDelete, AppIcon.DELETE, R.color.error, 24);
        AppIcons.set(binding.imgPhone, AppIcon.CALL, R.color.ink, 20);
        AppIcons.set(binding.imgGroup, AppIcon.GROUP, R.color.ink, 20);
        AppIcons.set(binding.imgGender, AppIcon.GENDER, R.color.ink, 20);
        AppIcons.set(binding.imgNote, AppIcon.NOTE, R.color.ink, 20);
    }

    private void reload() {
        Contact latest = repository.findById(contact.getId());
        if (latest == null) {
            finish();
            return;
        }
        contact = latest;
        bind();
    }

    private void bind() {
        binding.tvAvatar.setText(ExtensionsKt.avatarLetter(contact.getName()));
        ExtensionsKt.tintBackground(binding.heroCard, contact.getGroup().getColorRes());
        int accent = ContextCompat.getColor(this, contact.getGroup().getAccentRes());
        binding.tvAvatar.setTextColor(accent);
        binding.tvGroupChip.setTextColor(accent);
        binding.tvGroupChip.setCompoundDrawablesRelativeWithIntrinsicBounds(
                AppIcons.drawable(this, AppIcon.forGroup(contact.getGroup()), contact.getGroup().getAccentRes(), 14),
                null, null, null);
        binding.tvName.setText(contact.getName());
        binding.tvGroupChip.setText(contact.getGroup().getLabelRes());
        binding.tvPhone.setText(contact.getPhone());
        binding.tvGroup.setText(contact.getGroup().getLabelRes());
        binding.tvGender.setText(contact.getGender().getLabelRes());
        if (contact.getNote().isEmpty()) {
            binding.tvNote.setText(R.string.no_note);
        } else {
            binding.tvNote.setText(contact.getNote());
        }

        boolean favorite = contact.getFavorite();
        AppIcons.set(binding.btnFavoriteAction, favorite ? AppIcon.STAR : AppIcon.STAR_OUTLINE,
                favorite ? R.color.star : R.color.ink, 24);
        // Gỡ listener trước khi setChecked để không tự kích hoạt đổi yêu thích
        binding.cbFavorite.setOnCheckedChangeListener(null);
        binding.cbFavorite.setChecked(favorite);
        binding.cbFavorite.setOnCheckedChangeListener((button, checked) -> {
            repository.toggleFavorite(contact.getId());
            setResult(RESULT_OK); // quay lại thì danh sách + thống kê được cập nhật
            reload();
        });
    }

    private void confirmDelete() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, contact.getName()))
                .setPositiveButton(R.string.action_delete, (d, w) -> {
                    Contact removed = repository.delete(contact.getId());
                    // Gửi liên hệ đã xóa về danh sách để hiện Snackbar "Hoàn tác"
                    setResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_DELETED, removed));
                    finish();
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }
}
