package com.main.usermanagement.ui.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.main.usermanagement.R;
import com.main.usermanagement.ui.activities.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
    }

    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        view.findViewById(R.id.item_account).setOnClickListener(item -> {
            showPopup(getView());
        });

        view.findViewById(R.id.item_log_out).setOnClickListener(item -> {
            new AlertDialog.Builder(getContext())
                    .setMessage("Log out your account?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(intent);
                            getActivity().finish();
                        }})
                    .setNegativeButton(android.R.string.no, null).show();

        });

        return view;
    }


    private void showPopup(View anchorView) {
        View popupView = getLayoutInflater().inflate(R.layout.bottom_sheet_personal_info, null);

        final PopupWindow background = new PopupWindow(
                getLayoutInflater().inflate(R.layout.bottom_sheet_background, null),
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        background.setAnimationStyle(R.style.PopupBackgroundAnimation);


        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        popupWindow.setAnimationStyle(R.style.PopupAnimation);

        int[] location = new int[2];
        anchorView.getLocationOnScreen(location);
        background.showAtLocation(anchorView, Gravity.TOP, location[0], location[1]);
        popupWindow.showAtLocation(anchorView, Gravity.TOP, location[0], location[1]);

        popupView.findViewById(R.id.btn_dismiss).setOnClickListener(view -> {
            background.dismiss();
            popupWindow.dismiss();

        });

    }
}