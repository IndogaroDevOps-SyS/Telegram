package id.indogaro.ui.Components.Premium.boosts;

import android.app.Activity;

import id.indogaro.ui.ActionBar.BaseFragment;
import id.indogaro.ui.ActionBar.Theme;
import id.indogaro.ui.Stories.DarkThemeResourceProvider;
import id.indogaro.ui.WrappedResourceProvider;

public class DarkFragmentWrapper extends BaseFragment {

    private final BaseFragment parentFragment;

    DarkFragmentWrapper(BaseFragment parentFragment) {
        this.parentFragment = parentFragment;
    }

    @Override
    public boolean isLightStatusBar() {
        return false;
    }

    @Override
    public Activity getParentActivity() {
        return parentFragment.getParentActivity();
    }

    @Override
    public Theme.ResourcesProvider getResourceProvider() {
        return new WrappedResourceProvider(new DarkThemeResourceProvider());
    }

    @Override
    public boolean presentFragment(BaseFragment fragment) {
        return false;
    }
}
