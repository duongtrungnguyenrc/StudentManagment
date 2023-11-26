package com.main.usermanagement.ui.components;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.main.usermanagement.R;
import com.main.usermanagement.callback.BottomSheetActionHandlerCallback;

public class BottomSheet {

    private final int bottomSheetLayout;
    private final View anchorView;
    private final LayoutInflater layoutInflater;
    private PopupWindow background, popupWindow;
    private final boolean enableBackground;

    public BottomSheet(int layout, View anchorView, LayoutInflater layoutInflater, boolean enableBackground) {
        this.bottomSheetLayout = layout;
        this.anchorView = anchorView;
        this.layoutInflater = layoutInflater;
        this.enableBackground = enableBackground;
    }

    public void showBottomSheet(BottomSheetActionHandlerCallback callback) {
        View popupView = this.layoutInflater.inflate(bottomSheetLayout, null);

        this.popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        int[] location = getViewLocation();

        // show background
        if(enableBackground)
            this.background = showBottomSheetBackground();

        // Show popup window
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(anchorView, Gravity.BOTTOM, location[0], location[1]);

        if(popupView.findViewById(R.id.btn_dismiss) != null)
            // handle dismiss
            popupView.findViewById(R.id.btn_dismiss).setOnClickListener(view -> {
                dismiss();
            });

        callback.action(popupView, popupWindow, background);
    }

    public void dismiss() {
        background.dismiss();
        popupWindow.dismiss();
    }

    private int[] getViewLocation() {
        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);

        return location;
    }

    private PopupWindow showBottomSheetBackground() {
        PopupWindow background = new PopupWindow(
            this.layoutInflater.inflate(R.layout.bottom_sheet_background, null),
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );

        background.setAnimationStyle(R.style.PopupBackgroundAnimation);
        int[] location = getViewLocation();
        background.setFocusable(false);
        background.setTouchable(false);
        background.showAtLocation(anchorView, Gravity.BOTTOM, location[0], location[1]);

        return  background;
    }
}
