package id.indogaro.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;



import id.indogaro.messenger.Utilities;
import id.indogaro.ui.ActionBar.ActionBar;
import id.indogaro.ui.ActionBar.BaseFragment;
import id.indogaro.ui.ActionBar.DrawerLayoutContainer;
import id.indogaro.ui.ActionBar.INavigationLayout;
import id.indogaro.ui.ActionBar.Theme;
import id.indogaro.ui.Components.BackButtonMenu;
import id.indogaro.ui.Components.LayoutHelper;
import id.indogaro.ui.Components.RecyclerListView;

import java.util.List;

public class ChatActivityContainer extends FrameLayout {

    public final ChatActivity chatActivity;
    private final INavigationLayout parentLayout;
    private View fragmentView;

    public ChatActivityContainer(
        Context context,
        INavigationLayout parentLayout,
        Bundle args
    ) {
        super(context);
        this.parentLayout = parentLayout;

        chatActivity = new ChatActivity(args) {
            @Override
            public void setNavigationBarColor(int color) {}

            @Override
            protected void onSearchLoadingUpdate(boolean loading) {
                ChatActivityContainer.this.onSearchLoadingUpdate(loading);
            }
        };
        chatActivity.isInsideContainer = true;
    }

    private int topPadding;
    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    protected void onSearchLoadingUpdate(boolean loading) {

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        initChatActivity();
    }

    protected void initChatActivity() {
        if (!chatActivity.onFragmentCreate()) {
            return;
        }

        fragmentView = chatActivity.fragmentView;
        chatActivity.setParentLayout(parentLayout);
        if (fragmentView == null) {
            fragmentView = chatActivity.createView(getContext());
        } else {
            ViewGroup parent = (ViewGroup) fragmentView.getParent();
            if (parent != null) {
                chatActivity.onRemoveFromParent();
                parent.removeView(fragmentView);
            }
        }
        if (chatActivity.getChatListView() != null && topPadding != 0) {
            chatActivity.getChatListView().setPadding(0, topPadding, 0, 0);
        }
        chatActivity.openedInstantly();
        addView(fragmentView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
        if (isActive) {
            chatActivity.onResume();
        }
    }

    private boolean isActive = true;
    public void onPause() {
        isActive = false;
        if (fragmentView != null) {
            chatActivity.onPause();
        }
    }

    public void onResume() {
        isActive = true;
        if (fragmentView != null) {
            chatActivity.onResume();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }
}
