package com.xy.wechatkeyboarddemo;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

/**
 * A class which extends/implements both [WindowInsetsAnimationCompat.Callback] and
 * [View.OnApplyWindowInsetsListener], which should be set on the root view in your layout.
 * <p>
 * This class enables the root view is selectively defer handling any insets which match
 * [deferredInsetTypes], to enable better looking [WindowInsetsAnimationCompat]s.
 * <p>
 * An example is the following: when a [WindowInsetsAnimationCompat] is started, the system will dispatch
 * a [WindowInsetsCompat] instance which contains the end state of the animation. For the scenario of
 * the IME being animated in, that means that the insets contains the IME height. If the view's
 * [View.OnApplyWindowInsetsListener] simply always applied the combination of
 * [WindowInsetsCompat.Type.ime] and [WindowInsetsCompat.Type.systemBars] using padding, the viewport of any
 * child views would then be smaller. This results in us animating a smaller (padded-in) view into
 * a larger viewport. Visually, this results in the views looking clipped.
 * <p>
 * This class allows us to implement a different strategy for the above scenario, by selectively
 * deferring the [WindowInsetsCompat.Type.ime] insets until the [WindowInsetsAnimationCompat] is ended.
 * For the above example, you would create a [RootViewDeferringInsetsCallback] like so:
 * <p>
 * ``` 监听软键盘
 * val callback = RootViewDeferringInsetsCallback(
 * persistentInsetTypes = WindowInsetsCompat.Type.systemBars(),
 * deferredInsetTypes = WindowInsetsCompat.Type.ime()
 * )
 * ```
 * <p>
 * This class is not limited to just IME animations, and can work with any [WindowInsetsCompat.Type]s.
 * <p>
 * persistentInsetTypes the bitmask of any inset types which should always be handled
 * through padding the attached view
 * deferredInsetTypes   the bitmask of insets types which should be deferred until after
 * any related [WindowInsetsAnimationCompat]s have ended
 * <p>
 * WindowInsetsCompat.Type.ime();软键盘
 * WindowInsetsCompat.Type.systemBars();//所有的bar
 * WindowInsetsCompat.Type.statusBars();//状态栏
 * WindowInsetsCompat.Type.navigationBars();//导航bar
 */

/**
 * 根view Insets监听
 * 如果在fragment中 需要设置 WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
 */
public class RootViewDeferringInsetsCallback extends WindowInsetsAnimationCompat.Callback implements OnApplyWindowInsetsListener {
    private View view;
    private WindowInsetsCompat lastWindowInsets;
    /**
     * 是否是延时动作（用来设置view panding）
     */
    private boolean deferredInsets = false;
    /**
     * 持久动作类型（用来设置view panding）
     */
    private int persistentInsetTypes;
    /**
     * 延时动作类型（用来设置view panding）
     */
    private int deferredInsetTypes;
    /**
     * 是否设置View pandding（systemBars,statusBars等）
     */
    private boolean isPadding = false;

    /**
     * Creates a new {@link WindowInsetsAnimationCompat} callback with the given
     * {@link #getDispatchMode() dispatch mode}.
     *
     * @param dispatchMode The dispatch mode for this callback. See {@link #getDispatchMode()}.
     */
    public RootViewDeferringInsetsCallback(int dispatchMode) {
        super(dispatchMode);
    }

    public RootViewDeferringInsetsCallback(int persistentInsetTypes, int deferredInsetTypes) {
        this(DISPATCH_MODE_CONTINUE_ON_SUBTREE);
        this.deferredInsetTypes = deferredInsetTypes;
        this.persistentInsetTypes = persistentInsetTypes;
    }

    public RootViewDeferringInsetsCallback(int persistentInsetTypes, int deferredInsetTypes, boolean isPadding) {
        this(persistentInsetTypes, deferredInsetTypes);
        this.isPadding = isPadding;
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat windowInsets) {
        view = v;
        lastWindowInsets = windowInsets;
        if (isPadding) {
            int types;
            if (deferredInsets) {
                types = deferredInsetTypes;
            } else {
                types = persistentInsetTypes | deferredInsetTypes;
            }

            // Finally we apply the resolved insets by setting them as padding
            Insets typeInsets = windowInsets.getInsets(types);
            v.setPadding(typeInsets.left, typeInsets.top, typeInsets.right, typeInsets.bottom);
        }

        // We return the new WindowInsetsCompat.CONSUMED to stop the insets being dispatched any
        // further into the view hierarchy. This replaces the deprecated
        // WindowInsetsCompat.consumeSystemWindowInsets() and related functions.
        return WindowInsetsCompat.CONSUMED;
    }

    @Override
    public void onPrepare(@NonNull WindowInsetsAnimationCompat animation) {
        if ((animation.getTypeMask() & deferredInsetTypes) != 0) {
            // We defer the WindowInsetsCompat.Type.ime() insets if the IME is currently not visible.
            // This results in only the WindowInsetsCompat.Type.systemBars() being applied, allowing
            // the scrolling view to remain at it's larger size.
            deferredInsets = true;
        }
    }

    @NonNull
    @Override
    public WindowInsetsCompat onProgress(@NonNull WindowInsetsCompat insets, @NonNull List<WindowInsetsAnimationCompat> runningAnimations) {
        return insets;
    }

    @Override
    public void onEnd(@NonNull WindowInsetsAnimationCompat animation) {
        if (deferredInsets && (animation.getTypeMask() & deferredInsetTypes) != 0) {
            // If we deferred the IME insets and an IME animation has finished, we need to reset
            // the flag
            deferredInsets = false;

            // And finally dispatch the deferred insets to the view now.
            // Ideally we would just call view.requestApplyInsets() and let the normal dispatch
            // cycle happen, but this happens too late resulting in a visual flicker.
            // Instead we manually dispatch the most recent WindowInsets to the view.
            if (lastWindowInsets != null && view != null) {
                ViewCompat.dispatchApplyWindowInsets(view, lastWindowInsets);
            }
        }
    }
}
