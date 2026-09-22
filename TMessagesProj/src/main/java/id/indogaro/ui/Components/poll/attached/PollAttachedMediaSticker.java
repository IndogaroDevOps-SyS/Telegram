package id.indogaro.ui.Components.poll.attached;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;

import id.indogaro.messenger.DocumentObject;
import id.indogaro.messenger.ImageLocation;
import id.indogaro.messenger.ImageReceiver;
import id.indogaro.messenger.MediaController;
import id.indogaro.messenger.MessageObject;
import id.indogaro.tgnet.TLRPC;
import id.indogaro.ui.ActionBar.Theme;
import id.indogaro.ui.Components.EmojiView;
import id.indogaro.ui.Components.poll.PollAttachedMedia;

public class PollAttachedMediaSticker extends PollAttachedMedia {
    public final TLRPC.Document sticker;
    public final Object parent;
    public final boolean isEmoji;

    public PollAttachedMediaSticker(TLRPC.Document sticker, Object parent) {
        this.sticker = sticker;
        this.parent = parent;
        this.isEmoji = MessageObject.isAnimatedEmoji(sticker);
        setupImageReceiver(imageReceiver);
    }

    private void setupImageReceiver(ImageReceiver imageReceiver) {
        final boolean isWebpSticker = MessageObject.isStickerDocument(sticker) || MessageObject.isVideoSticker(sticker);
        final boolean isAnimatedSticker = MessageObject.isAnimatedStickerDocument(sticker, true);

        final Drawable thumb = DocumentObject.getSvgThumb(sticker, Theme.key_chat_serviceBackground, 1.0f);
        imageReceiver.setImage(ImageLocation.getForDocument(sticker), "38_38", thumb, sticker.size,
            isWebpSticker ? "webp" : null, parent, 0);
    }

    @Override
    protected void draw(Canvas canvas, int w, int h) {
        imageReceiver.setImageCoords(0, 0, w, h);
        imageReceiver.draw(canvas);
    }
}
