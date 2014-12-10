package com.desmond.androidanimation.CardFlipAnimation;

/**
 * This interface is used to prevent flipping multiple cards at the same time.
 * These callbacks methods are used to disable & re-enable touches when a card
 * flip animation begins and ends respectively.
 */
public interface CardFlipListener {
    public void onCardFlipEnd();
    public void onCardFlipStart();
}
