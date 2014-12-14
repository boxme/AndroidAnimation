package com.desmond.androidanimation.FoldingLayoutAnimation;

/**
 * This interface listens for when the folding layout begins folding
 * (enters a folded state from a completely unfolded state), or ends
 * folding (enters a completely unfolded state from a folded state).
 */
public interface OnFoldListener {
    public void onStartFold();
    public void onEndFold();
}
