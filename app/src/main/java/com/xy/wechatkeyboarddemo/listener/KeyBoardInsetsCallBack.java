package com.xy.wechatkeyboarddemo.listener;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsAnimationCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class KeyBoardInsetsCallBack extends RootViewDeferringInsetsCallback {
    public static final int KEYBOARD_TYPE = WindowInsetsCompat.Type.ime();
    public static final int SYSTEM_BAR_TYPE = WindowInsetsCompat.Type.systemBars();
    private KeyBoardListener keyboardListener;

    public KeyBoardInsetsCallBack(int dispatchMode, KeyBoardListener keyboardListener) {
        super(dispatchMode);
        this.keyboardListener = keyboardListener;
    }

    public KeyBoardInsetsCallBack(KeyBoardListener keyboardListener) {
        this(DISPATCH_MODE_STOP, keyboardListener);
    }

    @Override
    public void onPrepare(@NonNull WindowInsetsAnimationCompat animation) {

    }

    @NonNull
    @Override
    public WindowInsetsAnimationCompat.BoundsCompat onStart(@NonNull WindowInsetsAnimationCompat animation, @NonNull WindowInsetsAnimationCompat.BoundsCompat bounds) {
        keyboardListener.onAnimStart(bounds.getUpperBound().bottom - bounds.getLowerBound().bottom);
        return super.onStart(animation, bounds);
    }

    @NonNull
    @Override
    public WindowInsetsCompat onProgress(@NonNull WindowInsetsCompat insets, @NonNull List<WindowInsetsAnimationCompat> runningAnimations) {
        Insets typesInset = insets.getInsets(KEYBOARD_TYPE);
        // Then we get the persistent inset types which are applied as padding during layout
        Insets otherInset = insets.getInsets(SYSTEM_BAR_TYPE);

        // Now that we subtract the two insets, to calculate the difference. We also coerce
        // the insets to be >= 0, to make sure we don't use negative insets.
        Insets subtract = Insets.subtract(typesInset, otherInset);
        Insets diff = Insets.max(subtract, Insets.NONE);
        keyboardListener.onAnimDoing(diff.left - diff.right, diff.top - diff.bottom);
        return insets;
    }

    @Override
    public void onEnd(@NonNull WindowInsetsAnimationCompat animation) {
        keyboardListener.onAnimEnd();
    }
}

