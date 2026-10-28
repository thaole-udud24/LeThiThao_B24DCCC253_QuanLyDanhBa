package com.example.contacts.ui.add;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;

import com.example.contacts.R;
import com.example.contacts.data.ContactRepository;
import com.example.contacts.data.ServiceLocator;
import com.example.contacts.databinding.ActivityAddContactBinding;
import com.example.contacts.domain.model.Contact;
import com.example.contacts.domain.model.ContactGroup;
import com.example.contacts.domain.model.Gender;
import com.example.contacts.domain.validator.ContactValidator;
import com.example.contacts.domain.validator.ValidationResult;
import com.example.contacts.ui.BaseActivity;
import com.example.contacts.util.AppIcon;
import com.example.contacts.util.AppIcons;
import com.example.contacts.util.Constants;
import com.example.contacts.util.ExtensionsKt;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

/** Thêm liên hệ; nếu Intent có EXTRA_CONTACT thì chạy ở chế độ Sửa. */
public class AddContactActivity extends BaseActivity {
    private ActivityAddContactBinding binding;
    private final ContactRepository repository = ServiceLocator.contactRepository();
    private final ContactValidator validator = ServiceLocator.contactValidator(); // Kotlin
    private Contact editing;
    private String initialSnapshot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddContactBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ExtensionsKt.applySystemBarsPadding(binding.scroll);

        editing = IntentCompat.getParcelableExtra(getIntent(), Constants.EXTRA_CONTACT, Contact.class);

        AppIcons.set(binding.btnBack, AppIcon.BACK);
        AppIcons.set(binding.imgName, AppIcon.USER, R.color.accent, 20);
        AppIcons.set(binding.imgPhone, AppIcon.CALL, R.color.accent, 20);
        AppIcons.set(binding.imgGroup, AppIcon.GROUP, R.color.accent, 20);
        AppIcons.set(binding.imgGender, AppIcon.GENDER, R.color.accent, 20);
        AppIcons.set(binding.imgFavorite, AppIcon.STAR_OUTLINE, R.color.accent, 20);
        AppIcons.set(binding.imgNote, AppIcon.NOTE, R.color.accent, 20);
        binding.btnSave.setIcon(AppIcons.drawable(this, AppIcon.SAVE, R.color.white, 20));
        AppIcons.set(binding.imgSpinnerArrow, AppIcon.ARROW_DOWN, R.color.muted, 24);
        setupGroupSpinner();

        // Chỉ điền sẵn lần đầu; khi xoay màn hình, view tự khôi phục dữ liệu người dùng đã nhập
        if (editing != null) {
            binding.tvScreenTitle.setText(R.string.edit_title);
            if (savedInstanceState == null) fillForm(editing);
        }
        initialSnapshot = snapshot();

        binding.btnBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());
        binding.btnSave.setOnClickListener(v -> save());
        ExtensionsKt.onTextChanged(binding.etName, t -> binding.tilName.setError(null));
        ExtensionsKt.onTextChanged(binding.etPhone, t -> binding.tilPhone.setError(null));

        // Cảnh báo khi thoát mà có dữ liệu chưa lưu
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (snapshot().equals(initialSnapshot)) {
                    finish();
                } else {
                    confirmDiscard();
                }
            }
        });
    }

    private void setupGroupSpinner() {
        ContactGroup[] groups = ContactGroup.values();
        String[] labels = new String[groups.length];
        for (int i = 0; i < groups.length; i++) labels[i] = getString(groups[i].getLabelRes());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.item_spinner, labels);
        adapter.setDropDownViewResource(R.layout.item_spinner);
        binding.spGroup.setAdapter(adapter);
    }

    private void fillForm(Contact c) {
        binding.etName.setText(c.getName());
        binding.etPhone.setText(c.getPhone());
        binding.etNote.setText(c.getNote());
        binding.spGroup.setSelection(c.getGroup().ordinal());
        binding.cbFavorite.setChecked(c.getFavorite());
        switch (c.getGender()) {
            case FEMALE:
                binding.rbFemale.setChecked(true);
                break;
            case OTHER:
                binding.rbOther.setChecked(true);
                break;
            default:
                binding.rbMale.setChecked(true);
        }
    }

    private Gender selectedGender() {
        if (binding.rbFemale.isChecked()) return Gender.FEMALE;
        if (binding.rbOther.isChecked()) return Gender.OTHER;
        return Gender.MALE;
    }

    private String snapshot() {
        return binding.etName.getText() + "|" + binding.etPhone.getText() + "|" + binding.etNote.getText()
                + "|" + binding.spGroup.getSelectedItemPosition() + "|" + selectedGender()
                + "|" + binding.cbFavorite.isChecked();
    }

    private void save() {
        String name = String.valueOf(binding.etName.getText()).trim();
        String phone = String.valueOf(binding.etPhone.getText()).trim();

        // Validate nằm ở Kotlin (ContactValidator); Activity chỉ hiển thị lỗi
        ValidationResult result = validator.validate(name, phone, editing == null ? null : editing.getId());
        binding.tilName.setError(result.nameError == null ? null : getString(result.nameError));
        binding.tilPhone.setError(result.phoneError == null ? null : getString(result.phoneError));
        if (!result.isValid()) return;

        ContactGroup group = ContactGroup.values()[binding.spGroup.getSelectedItemPosition()];
        String note = String.valueOf(binding.etNote.getText()).trim();
        boolean favorite = binding.cbFavorite.isChecked();

        Contact contact;
        if (editing == null) {
            contact = new Contact(name, phone, group, selectedGender(), favorite, note); // id tự sinh
            repository.add(contact);
        } else {
            contact = new Contact(name, phone, group, selectedGender(), favorite, note, editing.getId());
            repository.update(contact);
        }

        // Trả liên hệ về cho màn trước (Activity Result API)
        setResult(RESULT_OK, new Intent().putExtra(Constants.EXTRA_CONTACT, contact));
        finish();
    }

    private void confirmDiscard() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.discard_title)
                .setMessage(R.string.discard_message)
                .setPositiveButton(R.string.btn_discard, (d, w) -> finish())
                .setNegativeButton(R.string.btn_keep_editing, null)
                .show();
    }
}
