package com.desmond.androidanimation.AndroidCurvedAnimation;

import android.animation.TypeEvaluator;

/**
 * This evaluator interpolates between two PathPoint values given the value fraction, the
 * proportion traveled between those two points. The value of the interpolation depends
 * on the operation specified by the endValue (the operation for the interval between
 * PathPoints is always specified by the end point of that interval).
 */
public class PathEvaluator implements TypeEvaluator<PathPoint> {

    @Override
    public PathPoint evaluate(float fraction, PathPoint startValue, PathPoint endValue) {
        float x, y;
        if (endValue.mOperation == PathPoint.CURVE) {
            float oneMinusT = 1 - fraction;
            x = oneMinusT * oneMinusT * oneMinusT * startValue.mX +
                    3 * oneMinusT * oneMinusT * fraction * endValue.mControl0X +
                    3 * oneMinusT * fraction * fraction * endValue.mControl1X +
                    fraction * fraction * fraction * endValue.mX;

            y = oneMinusT * oneMinusT * oneMinusT * startValue.mY +
                    3 * oneMinusT * oneMinusT * fraction * endValue.mControl0Y +
                    3 * oneMinusT * fraction * fraction * endValue.mControl1Y +
                    fraction * fraction * fraction * endValue.mY;
        }
        else if (endValue.mOperation == PathPoint.LINE) {
            x = startValue.mX + fraction * (endValue.mX - startValue.mX);
            y = startValue.mY + fraction * (endValue.mY - startValue.mY);
        }
        else {
            x = endValue.mX;
            y = endValue.mY;
        }

        return PathPoint.moveTo(x, y);
    }
}
