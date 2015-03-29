package org.monroe.team.android.box.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

final public class FileUtils {

    private FileUtils() {}

    public static boolean hasSD(){
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED))
            return true;
        return false;
    }

    public static File createStoragePrivateFile(Context context, String filename) {
        return createStorageFile(context, filename, Context.MODE_PRIVATE);
    }

    public static File createStoragePublicFile(Context context, String filename) {
        return createStorageFile(context, filename, Context.MODE_WORLD_WRITEABLE);
    }

    public static File createStorageFile(Context context, String filename, int mode){
        File file = storageFile(context, filename);
        if (file.exists()) return null;
        FileOutputStream fos = null;
        try {
            fos = context.openFileOutput(filename, mode);
        } catch (FileNotFoundException e) {
            return null;
        } finally {
            if (fos != null){
                try {
                    fos.close();
                } catch (IOException e) {}
            }
        }
        return file;
    }

    public static File storageFile(Context context, String filename) {
        return new File(context.getFilesDir(),filename);
    }


    public static String timeName() {
        return ""+System.currentTimeMillis();
    }


    public static String getFilePathFromContentUri(Uri selectedVideoUri,
                                             ContentResolver contentResolver) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};

        Cursor cursor = contentResolver.query(selectedVideoUri, filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


}
