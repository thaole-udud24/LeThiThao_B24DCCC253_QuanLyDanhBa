package com.example.contacts.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.contacts.R;
import com.example.contacts.databinding.ItemContactBinding;
import com.example.contacts.domain.model.Contact;
import com.example.contacts.domain.model.ContactGroup;
import com.example.contacts.util.AppIcon;
import com.example.contacts.util.AppIcons;
import com.example.contacts.util.ExtensionsKt;

/** ListAdapter + DiffUtil: thêm/xóa/đổi yêu thích có animation tự động. */
public class ContactAdapter extends ListAdapter<Contact, ContactAdapter.ViewHolder> {

    public interface Listener {
        void onContactClick(Contact contact);

        void onFavoriteClick(Contact contact);
    }

    private static final DiffUtil.ItemCallback<Contact> DIFF = new DiffUtil.ItemCallback<Contact>() {
        @Override
        public boolean areItemsTheSame(@NonNull Contact a, @NonNull Contact b) {
            return a.getId().equals(b.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Contact a, @NonNull Contact b) {
            return a.equals(b); // data class Kotlin đã có equals theo từng trường
        }
    };

    private final Listener listener;

    public ContactAdapter(Listener listener) {
        super(DIFF);
        this.listener = listener;
    }

    /** Dùng cho vuốt-để-xóa (getItem của ListAdapter là protected). */
    public Contact contactAt(int position) {
        return getItem(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemContactBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemContactBinding b;

        ViewHolder(ItemContactBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(Contact c) {
            Context context = b.getRoot().getContext();
            b.tvAvatar.setText(ExtensionsKt.avatarLetter(c.getName()));
            ContactGroup group = c.getGroup();
            int accent = ContextCompat.getColor(context, group.getAccentRes());

            // Màu theo nhóm: viền thẻ, avatar, chip nhóm
            b.getRoot().setStrokeColor(ColorUtils.setAlphaComponent(accent, 80));
            ExtensionsKt.tintBackground(b.tvAvatar, group.getColorRes());
            b.tvAvatar.setTextColor(accent);
            b.tvName.setText(c.getName());
            b.tvPhone.setText(c.getPhone());
            b.tvGroup.setText(group.getLabelRes());
            b.tvGroup.setTextColor(accent);
            ExtensionsKt.tintBackground(b.tvGroup, group.getColorRes());
            b.tvGroup.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    AppIcons.drawable(context, AppIcon.forGroup(group), group.getAccentRes(), 14), null, null, null);
            b.imgChevron.setImageDrawable(AppIcons.drawable(context, AppIcon.CHEVRON, R.color.muted, 24));

            boolean fav = c.getFavorite(); // Kotlin `favorite: Boolean` -> getFavorite()
            AppIcons.set(b.btnFavorite, fav ? AppIcon.STAR : AppIcon.STAR_OUTLINE,
                    fav ? R.color.star : R.color.ink_soft, 24);

            b.getRoot().setOnClickListener(v -> listener.onContactClick(c));
            b.btnFavorite.setOnClickListener(v -> listener.onFavoriteClick(c));
        }
    }
}
