package com.example.sandeepsharma.diginehru.Activity;

/**
 * Created by sandeepsharma on 15/06/17.
 */

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
import com.example.sandeepsharma.diginehru.R;
import com.example.sandeepsharma.diginehru.Utils.ProjectUtil;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class ImageCropperActivity extends AppCompatActivity {

    public int SELECT_PICTURE = 55;
    public Bitmap mBitMap;
    private static int DEFAULT_ASPECT_RATIO_X = 10;
    private static int DEFAULT_ASPECT_RATIO_Y = 10;
    boolean isAspectFix = false;

    private Uri mImageUri;
    File photo = null;
    String path;

    //private TransactionModel tModel;
    String mId;
    int fieldId;

    private String imgName = "picture";
    private String extension = ".jpg";

    CropImageView mCropImageView;
    ProgressBar mProgressBar;
    boolean ocr;
    float screenH, screenW;
    boolean permissionGranted = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_cropper);

        //tModel = getIntent().getParcelableExtra(AppConstants.TRANSACTION);
        mId = getIntent().getStringExtra("id");
        String TAG = "onCreate";
        /*if(getIntent().getStringExtra(AppConstants.IMAGE_TYPE).equals(AppConstants.IMAGE_BILL)){
            DEFAULT_ASPECT_RATIO_Y = 15;
         isAspectFix = false;
      }*/
        if (getIntent().hasExtra("field_id")) {
            fieldId = getIntent().getIntExtra("field_id", 0);
        }
        if (getIntent().hasExtra("ocr")) {
            ocr = true;
        }
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            getWindowManager().getDefaultDisplay().getRealMetrics(outMetrics);
        } else {
            getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        }
        screenH = outMetrics.heightPixels;
        screenW = outMetrics.widthPixels;
        //Log.d(TAG,"H:"+screenH+",W:"+screenW);
        mCropImageView = (CropImageView) findViewById(R.id.CropImageView);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        if (savedInstanceState == null) {
            showImageOptions();
        }else{
            mImageUri = savedInstanceState.getParcelable("uri");
            path = savedInstanceState.getString("path");
            permissionGranted = savedInstanceState.getBoolean("permission");

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.everything_dot_me, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //setResult(RESULT_CANCELED);
        //finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        permissionStorage();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putParcelable("uri", mImageUri);
        savedInstanceState.putString("path", path);
        savedInstanceState.putBoolean("permission", permissionGranted);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AppConstants.PERMISSION_HAPPAY_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay!
                    permissionGranted = true;
                    showImageOptions();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        Snackbar snackbar = Snackbar.make(mCropImageView
                                , getString(R.string.message_permission_storage)
                                , Snackbar.LENGTH_INDEFINITE);
                        View snackBarView = snackbar.getView();
                        snackBarView.setBackgroundResource(R.color.error_color);
                        snackbar.setAction(R.string.ok, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityCompat.requestPermissions(ImageCropperActivity.this,
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        AppConstants.PERMISSION_HAPPAY_STORAGE);
                            }
                        });
                        snackbar.show();

                    } else {

                       ProjectUtil.showToast(this,getString(R.string.message_permission_storage));

                    }
                }
                return;
            }
        }
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK)
            mProgressBar.setVisibility(View.VISIBLE);

        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && (data != null || mImageUri != null)) {
                    Uri selectedImage = null;
                    if (data != null)
                        selectedImage = data.getData();

                    try {
                        if (selectedImage != null) { // null for camera
                            //mBitMap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);                          // failed in case of large bitmaps
                            try {

                                mBitMap = getBitmapFromUri(selectedImage);
                                mImageUri = null; //selectedImage;

                                //String path = null;
                                if (("file".equals(selectedImage.getScheme()) || "content".equals(selectedImage.getScheme())) && mBitMap == null) {

                                    InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                                    try {
                                        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), mId + ".pdf");
                                        final OutputStream output = new FileOutputStream(file);
                                        try {
                                            try {

                                                output.write(readFully(inputStream));

                                                output.flush();
                                            } finally {
                                                output.close();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        path = file.getAbsolutePath();

                                        long size = file.length();
                                        if (size > 1024 * 1024 * 2) {

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    ProjectUtil.showToast(ImageCropperActivity.this,"Sorry, we don't support file of size more than 2 mb.");
                                                    file.delete();
                                                    setResult(RESULT_CANCELED);
                                                    finish();
                                                    return;
                                                }
                                            });
                                        }
                                    } finally {
                                        inputStream.close();
                                    }

                                }


                            } catch (Exception e) {
                                Log.e("cropper error", Log.getStackTraceString(e));
                                // mImageUri = null;
                            }

                        }


                        mProgressBar.post(new Runnable() {
                            @Override
                            public void run() {
                                processImage();
                                mProgressBar.setVisibility(View.GONE);
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    setResult(RESULT_CANCELED);
                    finish();
                }

            }
        };

        new Thread(runnable).start();
    }

    public void processImage() {
        if (mBitMap == null) {
            try {
                mBitMap = getBitmapFromUri(mImageUri);
                saveBitmapAtPath(mBitMap, path);
            } catch (Exception e) {
                e.printStackTrace();
            }

            setImage(mBitMap);
        } else {
            try {
                if (path == null)
                    path = photo.getAbsolutePath();

                saveBitmapAtPath(mBitMap, path);
                setImage(mBitMap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void setImage(Bitmap bm) {
        if (path != null) {
            if (ocr) {
                Intent intent = new Intent();
                intent.putExtra("ocr", true);
                intent.putExtra("file", path);
                intent.putExtra("field_id", fieldId);
                setResult(RESULT_OK, intent);
                finish();
                //return;
            } else {
                if (mBitMap != null)
                    mCropImageView.setImageBitmap(mBitMap);
                else if (mImageUri != null) {
                    try {
                        mCropImageView.setImageUriAsync(mImageUri);
                    } catch (Exception e) {
                        finish();
                        ProjectUtil.showToast(this,"Only Image and PDF file types are allowed. Please attach a valid file.");
                    }
                } else {
                    Intent intent = new Intent();

                    intent.putExtra("file", path);
                    intent.putExtra("field_id", fieldId);

                    setResult(RESULT_OK, intent);
                    finish();
                }

                // Sets initial aspect ratio to 10/10, for demonstration purposes
                mCropImageView.setAspectRatio(DEFAULT_ASPECT_RATIO_X, DEFAULT_ASPECT_RATIO_Y);
                mCropImageView.setFixedAspectRatio(isAspectFix);

                final Button cropButton = (Button) findViewById(R.id.button_crop);
                cropButton.setEnabled(true);
                cropButton.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            mBitMap = mCropImageView.getCroppedImage();

                            saveBitmapAtPath(mBitMap, path);

                            Intent intent = new Intent();

                            intent.putExtra("file", path);
                            intent.putExtra("field_id", fieldId);

                            setResult(RESULT_OK, intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            finish();
                            ProjectUtil.showToast(ImageCropperActivity.this,"Sorry, Cannot handle given file.");
                        }
                    }
                });
            }
        } else {
            finish();
            ProjectUtil.showToast(this,"Sorry, Cannot handle given file.");
        }
    }

    private void showImageOptions(){
        permissionGranted = true;
        try {
            // place where to store camera taken picture
            photo = createTemporaryFile(imgName, extension);
            path = photo.getAbsolutePath();
            photo.delete();

            mImageUri = Uri.fromFile(photo);

            Intent pickIntent = new Intent();
            pickIntent.setType("image/*");
            pickIntent.setAction(Intent.ACTION_PICK);

            if (mImageUri != null) {

                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                //takePhotoIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 512 * 1000);

                Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                imageIntent.setType("image/*");

                Intent pdfIntent = new Intent(Intent.ACTION_GET_CONTENT);
                pdfIntent.setType("application/pdf");

                Intent mailIntent = new Intent(Intent.ACTION_MAIN);
                mailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);

                String pickTitle = "Choose.."; // Or get from strings.xml
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

                Intent[] intents = new Intent[]{takePhotoIntent, imageIntent, pdfIntent};

                /*if(isCallable(mailIntent))
                    intents = new Intent[] { takePhotoIntent, fileIntent, mailIntent};*/

                chooserIntent.putExtra
                        (
                                Intent.EXTRA_INITIAL_INTENTS,
                                intents
                        );

                startActivityForResult(chooserIntent, SELECT_PICTURE);
            } else {

                String pickTitle = "Select a Picture"; // Or get from strings.xml
                Intent chooserIntent = Intent.createChooser(pickIntent, pickTitle);

                startActivityForResult(chooserIntent, SELECT_PICTURE);
            }
        } catch (IOException e) {

            ProjectUtil.showToast(this,"camera not available");
            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private File createTemporaryFile(String part, String ext) throws IOException {
        File tempDir = Environment.getExternalStorageDirectory();
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    public Bitmap grabImage() {
        Bitmap bitmap = null;
        this.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = this.getContentResolver();
        try {
            bitmap = MediaStore.Images.Media.getBitmap(cr, mImageUri);                // failed in case of large bitmaps

            //String path = photo.getAbsolutePath();
            //bitmap = decodeSampledBitmapFromResource(path, DEFAULT_ASPECT_RATIO_X*30, DEFAULT_ASPECT_RATIO_Y*30);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromResource(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        // Calculate inSampleSize
        options.inSampleSize = ProjectUtil.calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        String TAG = "getBitmapFromUri";
        if (!ocr) {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            // Calculate inSampleSize
            options.inSampleSize = ProjectUtil.calculateInSampleSize(options,
                    AppConstants.BILL_WIDTH,
                    AppConstants.BILL_HEIGHT);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            parcelFileDescriptor.close();
            return image;
        } else {
            ParcelFileDescriptor parcelFileDescriptor =
                    getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            //Log.d(TAG,"IM:"+imageHeight+",IW:"+imageWidth+",type:"+imageType);
            int inSampleSize = calculateInSampleSize(imageHeight, imageWidth);
            //Log.d(TAG,"inSampleSize:"+inSampleSize);
            options.inSampleSize = inSampleSize - 1;
            options.inJustDecodeBounds = false;
            options.inMutable = true;
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
            parcelFileDescriptor.close();
            return image;
        }
    }

    private int calculateInSampleSize(int imageHeight, int imageWidth) {
        String TAG = "calculateInSampleSize";
        int reqH, reqW;
        if (imageHeight > imageWidth) {
            reqH = (int) ((screenH - (getActionBarHeight() + getStatusBarHeight())) * 0.9);
            reqW = (int) (imageWidth / (float) imageHeight * reqH);
        } else {
            reqW = (int) (screenW * 0.9);
            reqH = (int) (imageHeight / (float) imageWidth * reqW);
        }
        Log.d(TAG, "reqH:" + reqH + ",reqW:" + reqW);
        int inSampleSize = 1;
        Log.d(TAG, "inSampleSize:" + inSampleSize);
        while (imageHeight / inSampleSize > reqH || imageWidth / inSampleSize > reqW) {
            Log.d(TAG, "imageHeight,inSampleSize > reqH:" + imageHeight + "," + inSampleSize + "," + reqH + ":imageWidth,inSampleSize > reqW" + imageWidth + "," + inSampleSize + "," + reqW);
            inSampleSize *= 2;
        }

        /*if (imageHeight > reqH || imageWidth > reqW) {

            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;
            inSampleSize *= 2;
            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqH
                    || (halfWidth / inSampleSize) > reqW) {
                Log.d("calculateInSampleSize:","halfH:"+halfHeight+",halfW:"+halfWidth+" > req");
                inSampleSize *= 2;
                Log.d("calculateInSampleSize:","inSampleSize:"+inSampleSize);
            }
        }*/

        return inSampleSize;
    }

    public int getStatusBarHeight() {
        String TAG = "getStatusBarHeight";
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        Log.d(TAG, "statusbar height=" + result);
        return result;
    }

    public int getActionBarHeight() {
        String TAG = "getActionBarHeight";
        // Calculate ActionBar height
        int actionBarHeight = 0;
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }
        Log.d(TAG, "ActionBar height=" + actionBarHeight);
        return actionBarHeight;
    }

    /**
     * returns the bytesize of the give bitmap
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static int byteSizeOf(Bitmap bitmap) {
        int size = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            size = bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            size = bitmap.getByteCount();
        } else {
            size = bitmap.getRowBytes() * bitmap.getHeight();
        }

        return size;
    }

    public byte[] readBytes(InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        return byteBuffer.toByteArray();
    }

    public static byte[] readFully(InputStream stream) throws IOException {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    public void saveBitmapAtPath(Bitmap bitmap, String path) throws IOException {
        if (bitmap == null) {
            ProjectUtil.showToast(this,"Sorry, can't process that");
            return;
        }

        File file = new File(path);

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private boolean isCallable(Intent intent) {
        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    private void permissionStorage() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            permissionGranted = false;

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Snackbar snackbar = Snackbar.make(mCropImageView
                        , getString(R.string.message_permission_storage)
                        , Snackbar.LENGTH_INDEFINITE);
                View snackBarView = snackbar.getView();
                snackBarView.setBackgroundResource(R.color.error_color);
                snackbar.setAction(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(ImageCropperActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                AppConstants.PERMISSION_HAPPAY_STORAGE);
                    }
                });
                snackbar.show();

            } else {

                // No explanation needed, we can request the permission.

                try {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            AppConstants.PERMISSION_HAPPAY_STORAGE);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }catch (Exception e){
                    Toast.makeText(this, "Storage permission not granted.", Toast.LENGTH_LONG).show();
                }
            }
        } else if(!permissionGranted){
            showImageOptions();
        }
    }


}
