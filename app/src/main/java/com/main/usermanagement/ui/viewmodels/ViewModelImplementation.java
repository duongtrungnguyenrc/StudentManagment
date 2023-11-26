package com.main.usermanagement.ui.viewmodels;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ViewModelImplementation extends ViewModel {
    private MutableLiveData<Fragment> currentFragment = new MutableLiveData<>();

    public MutableLiveData<Fragment> getCurrentFragment() {
        return currentFragment;
    }

    public void setCurrentFragment(Fragment fragment) {
        currentFragment.setValue(fragment);
    }
}
