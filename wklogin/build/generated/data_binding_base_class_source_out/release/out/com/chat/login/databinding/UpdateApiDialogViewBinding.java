// Generated by view binder compiler. Do not edit!
package com.chat.login.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.chat.login.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class UpdateApiDialogViewBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final TextView cancelTv;

  @NonNull
  public final AppCompatEditText ipEt;

  @NonNull
  public final AppCompatEditText portEt;

  @NonNull
  public final TextView sureTv;

  @NonNull
  public final TextView titleCenterTv;

  private UpdateApiDialogViewBinding(@NonNull LinearLayout rootView, @NonNull TextView cancelTv,
      @NonNull AppCompatEditText ipEt, @NonNull AppCompatEditText portEt, @NonNull TextView sureTv,
      @NonNull TextView titleCenterTv) {
    this.rootView = rootView;
    this.cancelTv = cancelTv;
    this.ipEt = ipEt;
    this.portEt = portEt;
    this.sureTv = sureTv;
    this.titleCenterTv = titleCenterTv;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static UpdateApiDialogViewBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static UpdateApiDialogViewBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.update_api_dialog_view, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static UpdateApiDialogViewBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.cancelTv;
      TextView cancelTv = ViewBindings.findChildViewById(rootView, id);
      if (cancelTv == null) {
        break missingId;
      }

      id = R.id.ipEt;
      AppCompatEditText ipEt = ViewBindings.findChildViewById(rootView, id);
      if (ipEt == null) {
        break missingId;
      }

      id = R.id.portEt;
      AppCompatEditText portEt = ViewBindings.findChildViewById(rootView, id);
      if (portEt == null) {
        break missingId;
      }

      id = R.id.sureTv;
      TextView sureTv = ViewBindings.findChildViewById(rootView, id);
      if (sureTv == null) {
        break missingId;
      }

      id = R.id.titleCenterTv;
      TextView titleCenterTv = ViewBindings.findChildViewById(rootView, id);
      if (titleCenterTv == null) {
        break missingId;
      }

      return new UpdateApiDialogViewBinding((LinearLayout) rootView, cancelTv, ipEt, portEt, sureTv,
          titleCenterTv);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}