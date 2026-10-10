package id.indogaro.ui.Components;

import id.indogaro.messenger.ChatObject;
import id.indogaro.tgnet.TLRPC;
import id.indogaro.ui.ActionBar.ActionBar;
import id.indogaro.ui.ActionBar.Theme;

public interface ChatActivityInterface {

    default ChatObject.Call getGroupCall() {
        return null;
    }

    default TLRPC.Chat getCurrentChat() {
        return null;
    }

    default TLRPC.User getCurrentUser() {
        return null;
    }

    long getDialogId();

    default void scrollToMessageId(int id, int i, boolean b, int i1, boolean b1, int i2) {

    }

    default boolean shouldShowImport() {
        return false;
    }

    default boolean openedWithLivestream() {
        return false;
    }

    default long getMergeDialogId() {
        return 0;
    }

    default long getTopicId() {
        return 0;
    }

    default boolean isRightFragment() {
        return false;
    }

    ChatAvatarContainer getAvatarContainer();

    default void checkAndUpdateAvatar() {

    }

    SizeNotifierFrameLayout getContentView();

    ActionBar getActionBar();

    Theme.ResourcesProvider getResourceProvider();
}
