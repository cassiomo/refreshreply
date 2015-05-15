package com.xzero.refreshreply.helpers;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * Handle manipulations of images -- mostly Bitmap.
 */
public class ImageUtil {

    public static byte[] getByteArrayFromBitmap(Bitmap imageBitmap, int compressionFactor) {

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, compressionFactor, stream);
        return stream.toByteArray();
    }
}
