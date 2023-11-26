package com.main.usermanagement.callback;

import java.util.List;

public interface RecyclerViewActionCallback<T> {
    default void onHasItemChecked(int position){}
    default void onHasItemChecked(T result){}
    default void onNoItemChecked() {}
}
