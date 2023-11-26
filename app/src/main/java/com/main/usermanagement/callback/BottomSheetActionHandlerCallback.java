package com.main.usermanagement.callback;

import android.view.View;
import android.widget.PopupWindow;

public interface BottomSheetActionHandlerCallback {
    void action(View popupView, PopupWindow popupWindow, PopupWindow background);
}
