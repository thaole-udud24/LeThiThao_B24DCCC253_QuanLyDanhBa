package com.example.contacts.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.IntentCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.databinding.ActivityMainBinding;
import com.example.contacts.domain.model.Contact;
import com.example.contacts.domain.model.ContactGroup;
import com.example.contacts.domain.query.SortOrder;
import com.example.contacts.ui.BaseActivity;
import com.example.contacts.ui.add.AddContactActivity;
import com.example.contacts.ui.detail.ContactDetailActivity;
import com.example.contacts.ui.login.LoginActivity;
import com.example.contacts.util.AppIcon;
import com.example.contacts.util.AppIcons;
import com.example.contacts.util.Constants;
import com.example.contacts.util.ExtensionsKt;
import com.example.contacts.viewmodel.MainState;
import com.example.contacts.viewmodel.MainViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;
import com.mikepenz.iconics.typeface.IIcon;

public class MainActivity extends BaseActivity implements ContactAdapter.Listener {
    private ActivityMainBinding binding;
    private MainViewModel viewModel;
    private ContactAdapter adapter;

    // Activity Result API: dùng chung cho Thêm và Chi tiết
    private final ActivityResultLauncher<Intent> contactLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), this::onContactResult);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ExtensionsKt.applySystemBarsPadding(binding.getRoot(), false);

        // ViewModel giữ bộ lọc + danh sách khi xoay màn hình
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        setupIcons();
        setupList();
        setupSwipeToDelete();
        setupSearchAndFilters();
        setupActions();

        viewModel.getState().observe(this, this::render);
    }

    private void setupIcons() {
        AppIcons.set(binding.btnSort, AppIcon.SORT);
        AppIcons.set(binding.btnLogout, AppIcon.LOGOUT);
        AppIcons.set(binding.imgStatTotal, AppIcon.GROUP, R.color.accent, 22);
        AppIcons.set(binding.imgStatStar, AppIcon.STAR, R.color.accent, 14);
        setChipIcon(binding.chipFavorite, AppIcon.STAR_OUTLINE);
        setChipIcon(binding.chipFamily, AppIcon.HOME);
        setChipIcon(binding.chipFriends, AppIcon.GROUP);
        setChipIcon(binding.chipWork, AppIcon.WORK);
        setChipIcon(binding.chipOther, AppIcon.MORE);
        String user = getIntent().getStringExtra(Constants.EXTRA_USERNAME);
        if (user == null || user.isEmpty()) user = getString(R.string.app_name);
        binding.tvGreeting.setText(getString(R.string.greeting, user));
        binding.tvUserAvatar.setText(ExtensionsKt.avatarLetter(user));
        AppIcons.set(binding.imgEmpty, AppIcon.EMPTY, R.color.muted, 36);
        AppIcons.setStart(binding.tilSearch, AppIcon.SEARCH);
        binding.fabAdd.setIcon(AppIcons.drawable(this, AppIcon.ADD, R.color.white, 24));
    }

    /** Màu icon chip do selector chip_icon quyết định (trắng khi chọn). */
    private void setChipIcon(Chip chip, IIcon icon) {
        chip.setChipIcon(AppIcons.drawable(this, icon, R.color.ink_soft, 18));
        chip.setChipIconVisible(true);
    }

    private void setupList() {
        adapter = new ContactAdapter(this);
        // Số cột lấy từ integers.xml theo kích thước màn hình / hướng xoay
        binding.rvContacts.setLayoutManager(new GridLayoutManager(this, getResources().getInteger(R.integer.list_columns)));
        binding.rvContacts.setAdapter(adapter);
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int position = vh.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Contact removed = viewModel.delete(adapter.contactAt(position).getId());
                    if (removed != null) showDeletedSnackbar(removed);
                }
            }
        }).attachToRecyclerView(binding.rvContacts);
    }

    private void setupSearchAndFilters() {
        ExtensionsKt.onTextChanged(binding.etSearch, viewModel::setQuery);

        binding.chipGroup.setOnCheckedStateChangeListener((group, ids) -> {
            int id = ids.isEmpty() ? R.id.chipAll : ids.get(0);
            if (id == R.id.chipFavorite) {
                viewModel.setCategory(null, true);
            } else if (id == R.id.chipFamily) {
                viewModel.setCategory(ContactGroup.FAMILY, false);
            } else if (id == R.id.chipFriends) {
                viewModel.setCategory(ContactGroup.FRIENDS, false);
            } else if (id == R.id.chipWork) {
                viewModel.setCategory(ContactGroup.WORK, false);
            } else if (id == R.id.chipOther) {
                viewModel.setCategory(ContactGroup.OTHER, false);
            } else {
                viewModel.setCategory(null, false);
            }
        });
    }

    private void setupActions() {
        binding.fabAdd.setOnClickListener(v ->
                contactLauncher.launch(new Intent(this, AddContactActivity.class)));

        binding.btnSort.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, binding.btnSort);
            menu.inflate(R.menu.menu_sort);
            menu.setOnMenuItemClickListener(item -> {
                viewModel.setSort(item.getItemId() == R.id.sortFavoriteFirst
                        ? SortOrder.FAVORITE_FIRST : SortOrder.NAME_ASC);
                return true;
            });
            menu.show();
        });

        binding.btnLogout.setOnClickListener(v -> {
            // Xóa toàn bộ back stack để Back không quay lại danh bạ sau khi đăng xuất
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    /** Cập nhật danh sách + khối thống kê + trạng thái rỗng từ một MainState. */
    private void render(MainState state) {
        adapter.submitList(state.getItems());
        binding.tvTotal.setText(String.valueOf(state.getTotal()));
        binding.tvFavorites.setText(String.valueOf(state.getFavorites()));
        int percent = state.getTotal() == 0 ? 0 : state.getFavorites() * 100 / state.getTotal();
        binding.progressFavorites.setProgressCompat(percent, true); // vòng tròn = tỉ lệ yêu thích
        ExtensionsKt.setVisible(binding.emptyState, state.getItems().isEmpty());
    }

    /** Add/Detail đã sửa dữ liệu trong repository: đọc lại để danh sách + thống kê khớp. */
    private void onContactResult(ActivityResult result) {
        viewModel.refresh();
        Intent data = result.getData();
        if (result.getResultCode() != RESULT_OK || data == null) return;

        Contact deleted = IntentCompat.getParcelableExtra(data, Constants.EXTRA_DELETED, Contact.class);
        Contact saved = IntentCompat.getParcelableExtra(data, Constants.EXTRA_CONTACT, Contact.class);
        if (deleted != null) {
            showDeletedSnackbar(deleted);
        } else if (saved != null) {
            Snackbar.make(binding.coordinator, getString(R.string.msg_saved, saved.getName()), Snackbar.LENGTH_SHORT)
                    .setAnchorView(binding.fabAdd).show();
        }
    }

    private void showDeletedSnackbar(Contact removed) {
        Snackbar.make(binding.coordinator, getString(R.string.msg_deleted, removed.getName()), Snackbar.LENGTH_LONG)
                .setAnchorView(binding.fabAdd)
                .setAction(R.string.action_undo, v -> viewModel.restore(removed))
                .show();
    }

    @Override
    public void onContactClick(Contact contact) {
        // Truyền Contact (Parcelable) qua Intent
        Intent intent = new Intent(this, ContactDetailActivity.class);
        intent.putExtra(Constants.EXTRA_CONTACT, contact);
        contactLauncher.launch(intent);
    }

    @Override
    public void onFavoriteClick(Contact contact) {
        viewModel.toggleFavorite(contact.getId());
    }
}
