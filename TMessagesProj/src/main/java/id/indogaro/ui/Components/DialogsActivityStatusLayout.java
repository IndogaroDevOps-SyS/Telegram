package id.indogaro.ui.Components;

import static id.indogaro.messenger.AndroidUtilities.dp;
import static id.indogaro.messenger.AndroidUtilities.lerp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

import id.indogaro.messenger.AndroidUtilities;
import id.indogaro.ui.ActionBar.ActionBar;
import id.indogaro.ui.ActionBar.Theme;

import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;

public class DialogsActivityStatusLayout extends View {
    private final BoolAnimator animatorStatusBarVisible = new BoolAnimator(this, CubicBezierInterpolator.EASE_OUT_QUINT, 380L);

    private final Paint fillingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final RectF statusBarRectF = new RectF();
    private final RectF indogaroLogoRectF = new RectF();
    private final RectF animatingRectF = new RectF();

    public DialogsActivityStatusLayout(Context context) {
        super(context);
        updateColors();
    }

    public void updateColors() {
        fillingPaint.setColor(Theme.getColor(Theme.key_indogaro_color));
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int h = ActionBar.getCurrentActionBarHeight();
        final int t = getPaddingTop();

        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(t + h, MeasureSpec.EXACTLY));

        statusBarRectF.set(0, 0, getMeasuredWidth(), t);

        final int t2 = t + h / 2 - dp(15);
        indogaroLogoRectF.set(dp(12), t2, dp(12) + dp(30), t2 + dp(30));
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        final float factor = animatorStatusBarVisible.getFloatValue();
        lerp(indogaroLogoRectF, statusBarRectF, factor, animatingRectF);

        final float radius = lerp(dp(15), 0, factor);

        canvas.drawRoundRect(animatingRectF, radius, radius, fillingPaint);
    }



    private final Runnable justForTestR = this::justForTest;

    private void justForTest() {
        animatorStatusBarVisible.setValue(!animatorStatusBarVisible.getValue(), true);
        AndroidUtilities.runOnUIThread(justForTestR, 3000);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(justForTestR, 3000);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(justForTestR);
    }
}
