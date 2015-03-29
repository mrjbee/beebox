package org.monroe.team.android.box;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

public final class BitmapUtils {
   private BitmapUtils() {}

    /**
     * int imageHeight = options.outHeight;
     * int imageWidth = options.outWidth;
     * String imageType = options.outMimeType;
     * @param decoder
     * @return options with filled bitmap data
     */
    public static BitmapFactory.Options bitmapDimensionAndType(BitmapDecoder decoder){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decoder.decode(options);
        options.inJustDecodeBounds = false;
        return options;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeBitmap(BitmapDecoder decoder, int reqWidth, int reqHeight){
         BitmapFactory.Options options = bitmapDimensionAndType(decoder);
         options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
         return decoder.decode(options);
    }

    public static interface BitmapDecoder{
        public Bitmap decode(BitmapFactory.Options options);
    }

    public static BitmapDecoder fromFile(final File file) {
        return fromFile(file.getAbsolutePath());
    }

    public static BitmapDecoder fromFile(final String filePath){
        return new BitmapDecoder() {
            @Override
            public Bitmap decode(BitmapFactory.Options options) {
                return BitmapFactory.decodeFile(filePath,options);
            }
        };
    }

}
