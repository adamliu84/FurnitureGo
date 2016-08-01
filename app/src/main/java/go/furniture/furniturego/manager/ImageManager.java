package go.furniture.furniturego.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by adam on 1/8/16.
 */
public class ImageManager {

    public final static String FOLDER_NAME = "/FurnitureGo";
    public final static String FILE_NAME_PREFIX = "furGogo";

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) { // BEST QUALITY MATCH
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // Calculate inSampleSize
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        int inSampleSize = 1;
        if (height > reqHeight) {
            inSampleSize = Math.round((float) height / (float) reqHeight);
        }
        int expectedWidth = width / inSampleSize;
        if (expectedWidth > reqWidth) {
            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
            inSampleSize = Math.round((float) width / (float) reqWidth);
        }
        options.inSampleSize = inSampleSize;
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap addText(Bitmap srcBitmap, int nStars, String szPrice ,String gText) {
        Bitmap destBitmap = srcBitmap.copy(srcBitmap.getConfig(), true);
        Canvas canvas = new Canvas(destBitmap);

        // new antialised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.rgb(61, 61, 61));
        // text size in pixels
        paint.setTextSize((int) (20 * 1.5));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(gText, 0, gText.length(), bounds);
        int x = (destBitmap.getWidth() - bounds.width()) / 2;
        int y = (destBitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(gText, x, y, paint);

        // draw price
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(61, 61, 61));
        paint.setTextSize(50);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        bounds = new Rect();
        szPrice = "$"+szPrice;
        paint.getTextBounds(szPrice , 0, szPrice .length(), bounds);
        canvas.drawText(szPrice , 20, 50, paint);

        // draw star
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(61, 61, 61));
        paint.setTextSize(60);
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
        bounds = new Rect();
        StringBuilder szBuilder = new StringBuilder();
        for(int i=0;i < nStars; i++){
            szBuilder.append("★");
        }
        String szStarInput = szBuilder.toString();
        paint.getTextBounds(szStarInput , 0, szStarInput .length(), bounds);
        canvas.drawText(szStarInput , 20, 90, paint);
        
        return destBitmap;
    }

    public static File saveImage(Bitmap ImageToSave) throws Exception {
        // Create imageDir
        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + FOLDER_NAME;
        String CurrentDateAndTime = getCurrentDateAndTime();
        File directory = new File(file_path);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // Create image
        File imageFile = new File(directory, FILE_NAME_PREFIX + CurrentDateAndTime + ".jpg");
        Log.d("imageFile", imageFile.getAbsolutePath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            // Use the compress method on the BitMap object to write image to the OutputStream
            ImageToSave.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            throw(e);
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
                throw(e);
            }
        }
        return imageFile;
    }

    private static String getCurrentDateAndTime() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        return df.format(c.getTime());
    }
}
