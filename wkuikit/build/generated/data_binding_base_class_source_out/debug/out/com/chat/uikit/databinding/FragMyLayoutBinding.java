// Generated by view binder compiler. Do not edit!
package com.chat.uikit.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.chat.base.ui.components.AvatarView;
import com.chat.uikit.R;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragMyLayoutBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final AvatarView avatarView;

  @NonNull
  public final TextView nameTv;

  @NonNull
  public final AppCompatImageView qrIv;

  @NonNull
  public final RecyclerView recyclerView;

  @NonNull
  public final SmartRefreshLayout refreshLayout;

  private FragMyLayoutBinding(@NonNull LinearLayout rootView, @NonNull AvatarView avatarView,
      @NonNull TextView nameTv, @NonNull AppCompatImageView qrIv,
      @NonNull RecyclerView recyclerView, @NonNull SmartRefreshLayout refreshLayout) {
    this.rootView = rootView;
    this.avatarView = avatarView;
    this.nameTv = nameTv;
    this.qrIv = qrIv;
    this.recyclerView = recyclerView;
    this.refreshLayout = refreshLayout;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragMyLayoutBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragMyLayoutBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.frag_my_layout, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragMyLayoutBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.avatarView;
      AvatarView avatarView = ViewBindings.findChildViewById(rootView, id);
      if (avatarView == null) {
        break missingId;
      }

      id = R.id.nameTv;
      TextView nameTv = ViewBindings.findChildViewById(rootView, id);
      if (nameTv == null) {
        break missingId;
      }

      id = R.id.qrIv;
      AppCompatImageView qrIv = ViewBindings.findChildViewById(rootView, id);
      if (qrIv == null) {
        break missingId;
      }

      id = R.id.recyclerView;
      RecyclerView recyclerView = ViewBindings.findChildViewById(rootView, id);
      if (recyclerView == null) {
        break missingId;
      }

      id = R.id.refreshLayout;
      SmartRefreshLayout refreshLayout = ViewBindings.findChildViewById(rootView, id);
      if (refreshLayout == null) {
        break missingId;
      }

      return new FragMyLayoutBinding((LinearLayout) rootView, avatarView, nameTv, qrIv,
          recyclerView, refreshLayout);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}