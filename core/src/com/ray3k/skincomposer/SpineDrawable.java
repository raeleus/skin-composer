package com.ray3k.skincomposer;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.utils.SkeletonDrawable;

public class SpineDrawable extends SkeletonDrawable {
    private float cropX;
    private float cropY;
    private float cropWidth;
    private float cropHeight;
    
    public SpineDrawable(SkeletonRenderer renderer, Skeleton skeleton,
                         AnimationState state) {
        super(renderer, skeleton, state);
        setCrop(getSkeleton().getData().getX(), getSkeleton().getData().getY(), getSkeleton().getData().getWidth(),
                getSkeleton().getData().getHeight());
    }
    
    public SpineDrawable(SkeletonRenderer renderer, SkeletonData skeletonData, AnimationStateData animationStateData) {
        this(renderer, new Skeleton(skeletonData), new AnimationState(animationStateData));
    }
    
    public void setCrop(float x, float y, float width, float height) {
        cropX = x;
        cropY = y;
        cropWidth = width;
        cropHeight = height;
        setMinSize(cropWidth, cropHeight);
    }
    
    @Override
    public void draw(Batch batch, float x, float y, float width, float height) {
        getSkeleton().setScale(width / cropWidth, height / cropHeight);
        getSkeleton().setColor(batch.getColor());
        super.draw(batch, x - cropX, y - cropY, width, height);
    }
}