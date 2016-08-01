package go.furniture.furniturego;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import go.furniture.furniturego.manager.ImageManager;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
//    private final String FOLDER_NAME = "/FurnitureGo";
//    private final String FILE_NAME_PREFIX = "furGogo";
    Toast mToast;
    private ImageView mImageView;
    private Bitmap mBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void onFurPreviewSave(View view) {
        if (null == mImageView) {
            goToast("No photo taken");
            return;
        }
        //Get text and save image
        EditText edtFur = (EditText) findViewById(R.id.edtFurPreview);
        EditText edtFurPrice = (EditText) findViewById(R.id.edtFurPrice);
        RatingBar rabFurStar = (RatingBar) findViewById(R.id.rabFurStar);
        try{
            File imageFile = ImageManager.saveImage(ImageManager.addText(mBitmap,Math.round(rabFurStar.getRating()), edtFurPrice.getText().toString(), edtFur.getText().toString()));
            addImageGallery(imageFile);
            goToast(imageFile.getName() + " saved");
        }catch(Exception e){
            goToast(e.getMessage());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "image.jpg");
            mImageView = (ImageView) findViewById(R.id.imvFurPreview);
            mBitmap = ImageManager.decodeSampledBitmapFromFile(file.getAbsolutePath(), 800, 600);
            mImageView.setImageBitmap(mBitmap);
        }
    }

    /**
     * Register taken photo to gallery
     * http://stackoverflow.com/questions/9360091/how-to-register-an-image-so-that-it-is-immediately-viewable-from-gallery-applica
     *
     * @param file
     */
    private void addImageGallery(File file) {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void goToast(String msg) {
        if (null != mToast) {
            mToast.cancel();
        }
        mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

}
