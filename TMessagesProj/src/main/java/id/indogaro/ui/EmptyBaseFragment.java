package id.indogaro.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import id.indogaro.ui.ActionBar.BaseFragment;
import id.indogaro.ui.Components.SizeNotifierFrameLayout;

public class EmptyBaseFragment extends BaseFragment {

    @Override
    public View createView(Context context) {
        return fragmentView = new SizeNotifierFrameLayout(context);
    }

}
