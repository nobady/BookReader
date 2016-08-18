package com.sanqiwan.reader.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class Rotate3d extends Animation {
    private float mCenterX;
    private float mCenterY;
    private float mFromDegree;
    private float mToDegree;
    private static final float DEPTH_Z = 100.0f;
    private Camera mCamera;

    public Rotate3d(float fromDegree, float toDegree) {
        mFromDegree = fromDegree;
        mToDegree = toDegree;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        mCenterX = width / 2;
        mCenterY = width / 2;
        mCamera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float degrees = mFromDegree + ((mToDegree - mFromDegree) * interpolatedTime);
        boolean overHalf = (interpolatedTime > 0.5f);
        if (overHalf) {
            // 翻转过半的情况下，为保证数字仍为可读的文字而非镜面效果的文字，需翻转180度。
            degrees = degrees - 180;
        }
        final float centerX = mCenterX;
        final float centerY = mCenterY;
        final Camera camera = mCamera;
        final Matrix matrix = t.getMatrix();
        camera.save();


        float depth = (0.5f - Math.abs(interpolatedTime - 0.5f)) * DEPTH_Z;
        camera.translate(0.0f, 0.0f, depth);
        camera.rotateY(degrees);
        camera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-centerX, -centerY);
        matrix.postTranslate(centerX, centerY);
    }
}