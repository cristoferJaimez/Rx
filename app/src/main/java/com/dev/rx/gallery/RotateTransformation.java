package com.dev.rx.gallery;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class RotateTransformation extends BitmapTransformation {

    private static final String ID = "com.dev.rx.gallery.RotateTransformation";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final float rotateRotationAngle;

    public RotateTransformation(ImagePreviewActivity imagePreviewActivity, float rotateRotationAngle) {
        this.rotateRotationAngle = rotateRotationAngle;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotateRotationAngle);
        return Bitmap.createBitmap(toTransform, 0, 0, toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RotateTransformation) {
            RotateTransformation other = (RotateTransformation) o;
            return rotateRotationAngle == other.rotateRotationAngle;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (int) (ID.hashCode() + rotateRotationAngle * 1000);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        byte[] radiusData = ByteBuffer.allocate(4).putFloat(rotateRotationAngle).array();
        messageDigest.update(radiusData);
    }
}
