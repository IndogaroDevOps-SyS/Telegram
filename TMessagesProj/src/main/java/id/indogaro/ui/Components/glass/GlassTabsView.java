package id.indogaro.ui.Components.glass;

import static id.indogaro.messenger.AndroidUtilities.dp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.math.MathUtils;

import id.indogaro.messenger.LiteMode;
import id.indogaro.ui.ActionBar.Theme;
import id.indogaro.ui.Components.LayoutHelper;
import id.indogaro.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import id.indogaro.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import id.indogaro.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed;
import id.indogaro.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import id.indogaro.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;

public class GlassTabsView extends FrameLayout {

    public final LinearLayout linearLayout;

    public GlassTabsView(@NonNull Context context) {
        super(context);
        setPadding(dp(8), dp(8), dp(8), dp(8));

        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        addView(linearLayout, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));
    }

    private float lensVisibility;

    private final Rect lensBounds = new Rect();
    private final Rect lensBoundsForeground = new Rect();
    private final Paint lensPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    protected void setLensColor(int lensBackgroundColor, int lensForegroundColor) {
        lensPaint.setColor(lensBackgroundColor);
    }

    protected void setLensBounds(int l, int t, int r, int b) {
        lensBounds.set(l, t, r, b);
        checkBounds();
    }

    protected void setLensVisibility(float visibility) {
        lensVisibility = visibility;
        checkBounds();
    }

    private void checkBounds() {
        int i = dp(7f * lensVisibility);
        lensBoundsForeground.set(lensBounds);
        lensBoundsForeground.inset(-i, -i);
    }

}
