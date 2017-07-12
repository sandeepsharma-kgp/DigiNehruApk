package com.example.sandeepsharma.diginehru.Utils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.StatFs;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Contacts;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.example.sandeepsharma.diginehru.AppConstants.AppConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ProjectUtil {

    /**
     * Function to be used to send email.
     *
     * @param context
     * @param address
     */

    private String strCategory, strAction, strLabel;
    private HashMap<String, long[]> time;
    private ProgressDialog mProgressDialog;

    public ProjectUtil() {
        time = new LinkedHashMap<String, long[]>();
    }

    public void startSppiner(Activity activity, View view, String titleTxt, String bodyText, boolean isCancelable) {
        try {
            if (null == mProgressDialog) {
                mProgressDialog = ProgressDialog.show(activity, titleTxt, bodyText, true);
                mProgressDialog.setCancelable(isCancelable);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL | ProgressDialog.STYLE_SPINNER);
                //mProgressDialog.setIndeterminate(true);
                //mProgressDialog.setIndeterminateDrawable(getResources().getDrawable(R.anim.progress_dialog_infinitely));
                if (null != view) mProgressDialog.setView(view);
            }
            if (!mProgressDialog.isShowing()) mProgressDialog.show();
        } catch (Exception ex) {

        }
    }

    public static long getMonthStartTimeInMilli(int month, int year) {
        Calendar calendar = Calendar.getInstance();

        return new GregorianCalendar(year, month, 1).getTimeInMillis();
    }

    public static long getLongTypeDate(String date, String format) {
        long milliseconds = 0;
        try {
            SimpleDateFormat f = new SimpleDateFormat(format);
            Date d = f.parse(date);
            milliseconds = d.getTime();
        } catch (Exception e) {
            Log.e("date format", Log.getStackTraceString(e));
        }
        return milliseconds;
    }

    public static long getMonthEndTimeInMilli(int month, int year) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        if (currentMonth != month && currentYear == year) {
            calendar.set(year, month + 1, 1, 00, 00, 00);
            // calendar.set(Calendar.MILLISECOND, 00);
            calendar.add(Calendar.MILLISECOND, -1 * (calendar.get(Calendar.MILLISECOND) + 1));
        } else {
            calendar.set(year, month, calendar.get(Calendar.DATE) + 1, 00, 00, 00);
            // calendar.set(Calendar.MILLISECOND, 00);
            calendar.add(Calendar.MILLISECOND, -1 * (calendar.get(Calendar.MILLISECOND) + 1));
        }
        return calendar.getTimeInMillis();
    }

    public static long getCurrentMonthStartTimeInMilli() {
        Calendar calendar = Calendar.getInstance();   // this takes current date
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTimeInMillis();
    }


    public static long getCurrentTimeInMilli() {
        return Calendar.getInstance().getTimeInMillis();
    }

    public void stopSppiner() {
        try {
            if (null != mProgressDialog) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        } catch (Exception ex) {
        }
    }

    public static void hideKeyBoard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String addLineBreak() {
        return System.getProperty("line.separator");
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    public static void sendMail(Context context, String address) {

        // Creating intent for sending mail....
        String[] recipients = new String[]{address,};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        try {
            context.startActivity(Intent.createChooser(emailIntent,
                    "Send mail..."));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Email Client Available.",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public static String getDate(long timeInMilli, String format) {
        if (timeInMilli != 0) {
            SimpleDateFormat sdf = new SimpleDateFormat(format == null ? "dd/MM/yyyy" : format);
            String date = null;
            Date now = new Date(timeInMilli);
            date = sdf.format(now);

            return date;
        } else {
            return "0";
        }

    }

    /**
     * Function to be used to send email.
     *
     * @param context
     * @param subject
     * @param message
     * @param address
     */
    public static void sendMail(Context context, String address,
                                String message, String subject) {
        // Creating intent for sending mail....
        String[] recipients = new String[]{address,};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // Subject of
        // message
        emailIntent.putExtra(Intent.EXTRA_TEXT, message); // Body of message
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        try {
            Intent _intent = Intent.createChooser(emailIntent, "Send mail...");
            _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(_intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Email Client Available.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static void sendMailMorePeople(Context context, String[] address,
                                          String message, String subject) {
        // Creating intent for sending mail....

        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject); // Subject of
        // message
        emailIntent.putExtra(Intent.EXTRA_TEXT, message); // Body of message
        emailIntent.putExtra(Intent.EXTRA_EMAIL, address);
        try {
            Intent _intent = Intent.createChooser(emailIntent, "Send mail...");
            _intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(_intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "No Email Client Available.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Function to be used to send sms.
     *
     * @param context
     * @param number
     */

    public static void sendSmsProgramatically(Context context, String number, String smsText) {
        SmsManager manager = SmsManager.getDefault();
        manager.sendTextMessage(number, null, smsText, null, null);
    }

    public static void sendSms(Context context, String number, String smsText) {

        Intent _smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"
                + number));
        _smsIntent.putExtra("sms_body", smsText);
        _smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(_smsIntent);
    }

    /**
     * Function used to detect if Network is available or not.
     *
     * @param context
     * @return
     */
    public static boolean isDataConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info == null)
            return false;

        return connectivityManager.getActiveNetworkInfo().isConnected();
    }


    public static String getDeviceId(Context context) {
        String hwdId = null;


        if (hwdId == null) {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            hwdId = telephonyManager.getDeviceId();

            if (hwdId == null)
                hwdId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);


            return hwdId;
        }
        return null;
    }

    public static JSONObject getDevice(Context context) {
        JSONObject device = new JSONObject();
        try {
            String hwdId = null;
            String deviceName = null;


            if (hwdId == null) {
                TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                hwdId = telephonyManager.getDeviceId();
                deviceName = android.os.Build.MANUFACTURER + android.os.Build.MODEL;

                if (hwdId == null) {
                    hwdId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }


                device.put("device_id", hwdId);

                device.put("device_name", deviceName);

                return device;
            }
        } catch (Exception e) {
        }
        return null;
    }


    public static String getConfiguredAccount(Context context) {
        AccountManager manager = AccountManager.get(context);
        Account[] accounts = manager.getAccountsByType("com.google");
        List<String> possibleEmails = new LinkedList<String>();

        for (Account account : accounts) {
            // account.name as an email address only for certain
            // account.type
            // values.
            possibleEmails.add(account.name);
        }

        if (!possibleEmails.isEmpty() && possibleEmails.get(0) != null) {
            String email = possibleEmails.get(0);
            String[] parts = email.split("@");
            if (parts.length > 0 && parts[0] != null)
                return parts[0] + "@" + parts[1];
            else
                return null;
        } else
            return null;
    }

    public static String getCurrentDate() {
        String date = null;
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        month = month + 1;
        int year = calendar.get(Calendar.YEAR);

        date = year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
        return date;
    }

    public static int getCurrentDay() {
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
    }

    public static int getCurrentMonth() {
        return (Calendar.getInstance().get(Calendar.MONTH) + 1);
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static String getCurrentTime() {
        String time = null;
        Calendar calendar = Calendar.getInstance();
        String am_pm = calendar.get(Calendar.AM_PM) == 0 ? "AM" : "PM";
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        time = (hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) +
                " " + am_pm;
        return time;
    }

    public static  void setImage(final String picture, ImageView image, int width, int height,Context context) {
        if (picture != null) {
            if (!picture.endsWith("pdf")) {
                if (new File(picture).exists()) {
                    Picasso.with(context)
                            .load(new File(picture))
                            .resize(width, height)
                            .centerCrop()

                            .into(image);
                } else if (!picture.isEmpty()) {
                    Picasso.with(context)
                            .load(picture)
                            .resize(width, height)
                            .centerCrop()
                            .into(image);
                }
            }
        }
    }

    public static boolean isSDCardExists() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public static long getAvailableInternalMemory() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();

        return (availableBlocks * blockSize) / 1048576;
    }

    public static String createAppStorage(Context context) {
        String path = null;

        if (ProjectUtil.isSDCardExists()) {
            File folder = new File(context.getExternalCacheDir().getAbsolutePath() + "/happay");
            if (!folder.isDirectory())
                folder.mkdir();
            if (folder.isDirectory())
                path = folder.getAbsolutePath();
        } else if (getAvailableInternalMemory() > 250) {
            File folder = context.getDir("happay", Context.MODE_PRIVATE);
            if (folder.isDirectory())
                path = folder.getAbsolutePath();
        }

        return path;
    }

    public static String getSimNo(Context context) {
        String simNo = null;
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        simNo = telephonyManager.getSimSerialNumber();
        if (simNo == null)
            simNo = String.valueOf(new Date().getTime());
        return simNo;
    }

    public static boolean oppositeSigns(int x, int y) {
        return ((x ^ y) < 0);
    }

    public static boolean oppositeFloatSigns(float x, float y) {
        return ((x * y) < 0);
    }

    public static void writeFileToPath(File path, String name, String result) {
        if (result != null) {
            try {
                File cacheFile = new File(path, name);
                FileWriter fOut = new FileWriter(cacheFile);
                fOut.write(result);
                fOut.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String readFileToString(File file) throws IOException {
        FileInputStream fIn = new FileInputStream(file);
        return convertStreamToString(fIn);
    }

    public static String convertStreamToString(InputStream is) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append((line + "\n"));
            }
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void saveBitmap(Bitmap bitmap, File destFile) throws IOException {
        if (bitmap == null)
            return;
        /*if (destFile.exists ())
            destFile.delete ();    */

        try {
            FileOutputStream out = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File saveBitmapAtPath(Bitmap bitmap, String path, String name) throws IOException {
        if (bitmap == null)
            return null;
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }

        File destFile = new File(file, name);

        try {
            FileOutputStream out = new FileOutputStream(destFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return destFile;
    }

    public static void downloadImage(Activity activity, String urls, String userName) {
        try {
            URL url = new URL(urls);
            InputStream istream = url.openStream();

            Bitmap bitmap = BitmapFactory.decodeStream(istream);

            saveBitmap(bitmap, new File(activity.getCacheDir(), userName));
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (Exception e) {

        }
    }

    public static Bitmap downloadSaveNReturnBitmap(Activity activity, String urls, String dir, String fileName) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urls);
            InputStream istream = url.openStream();

            bitmap = BitmapFactory.decodeStream(istream);
            saveBitmapAtPath(bitmap, dir, fileName);
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        } catch (Exception e) {

        }
        return bitmap;
    }

    public final static Bitmap circularImgage(Bitmap bitmap, int pixels) {


        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels; //pixels

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap = null;

        return output;
    }

    public void showCircularPicture(final ImageView profilePic, Bitmap bitmap, int pixels) {

        Bitmap output = circularImgage(bitmap, pixels);

        profilePic.setBackgroundDrawable(new BitmapDrawable(output));
    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)
            throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
    }

    public static Bitmap loadContactPhoto(ContentResolver cr, long id) {
        Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
        InputStream input = Contacts.openContactPhotoInputStream(cr, uri);
        if (input == null) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap loadContactPhoto(ContentResolver cr, long id, long photo_id) {

        Uri uri = ContentUris.withAppendedId(Contacts.CONTENT_URI, id);
        InputStream input = Contacts.openContactPhotoInputStream(cr, uri);
        if (input != null) {
            return BitmapFactory.decodeStream(input);
        } else {
            Log.d("PHOTO", "first try failed to load photo");

        }

        byte[] photoBytes = null;

        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, photo_id);

        Cursor c = cr.query(photoUri, new String[]{CommonDataKinds.Photo.PHOTO}, null, null, null);

        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {

            c.close();
        }

        if (photoBytes != null)
            return BitmapFactory.decodeByteArray(photoBytes, 0, photoBytes.length);
        else
            Log.d("PHOTO", "second try also failed");
        return null;
    }

    private byte[] loadLocalContactPhotoBytes(ContentResolver cr, Cursor cursor, byte[] defaultPhotoBytes) {
        byte[] photoBytes = null;// = cursor.getBlob(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Photo.PHOTO));

//      int id = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts._ID));
        int id = cursor.getInt(cursor.getColumnIndex(Contacts.PHOTO_ID));
//      Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, id);
//      Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Uri photoUri = ContentUris.withAppendedId(ContactsContract.Data.CONTENT_URI, id);

        Cursor c = cr.query(photoUri, new String[]{CommonDataKinds.Photo.PHOTO}, null, null, null);

        try {
            if (c.moveToFirst())
                photoBytes = c.getBlob(0);

        } catch (Exception e) {
            Log.e("load photo exception", e.toString());

        } finally {

            c.close();
        }

        photoBytes = photoBytes == null ? defaultPhotoBytes : photoBytes;
        return photoBytes;
    }

    public InputStream openPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = context.getContentResolver().query(photoUri,
                new String[]{Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                byte[] data = cursor.getBlob(0);
                if (data != null) {
                    return new ByteArrayInputStream(data);
                }
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public InputStream openDisplayPhoto(Context context, long contactId) {
        Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri displayPhotoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.DISPLAY_PHOTO);
        try {
            AssetFileDescriptor fd =
                    context.getContentResolver().openAssetFileDescriptor(displayPhotoUri, "r");
            return fd.createInputStream();
        } catch (IOException e) {
            return null;
        }
    }

    public static ArrayList<String> getPhoneNumbers(String id, ContentResolver mContentResolver) {
        ArrayList<String> phones = new ArrayList<String>();

        Cursor cursor = mContentResolver.query(
                CommonDataKinds.Phone.CONTENT_URI,
                null,
                CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{id}, null);

        while (cursor.moveToNext()) {
            phones.add(cursor.getString(cursor.getColumnIndex(CommonDataKinds.Phone.NUMBER)));
        }

        cursor.close();
        return (phones);
    }

    public static void setKeyboardFocus(final EditText primaryTextField) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                primaryTextField.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 100);
    }

    public static int getRandom() {
        Random rand = new Random();
        int randomNumber = rand.nextInt(899) + 100;
        return randomNumber;
    }

    /**
     * Round to certain number of decimals
     *
     * @param d
     * @param decimalPlace
     * @return
     */
    public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

    public static double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public static Bitmap queryContactImage(Activity activity, int imageDataRow) {
        Cursor c = activity.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[]{
                CommonDataKinds.Photo.PHOTO
        }, ContactsContract.Data._ID + "=?", new String[]{
                Integer.toString(imageDataRow)
        }, null);
        byte[] imageBytes = null;
        if (c != null) {
            if (c.moveToFirst()) {
                imageBytes = c.getBlob(0);
            }
            c.close();
        }

        if (imageBytes != null) {
            return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } else {
            return null;
        }
    }

    public static String getStringFromJSONObject(JSONObject obj, String string) {
        if (obj.has(string)) {
            try {
                if (!obj.getString(string).equalsIgnoreCase("null") && !obj.getString(string).equalsIgnoreCase("none"))
                    return obj.getString(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean getBooleanFromJSONObject(JSONObject obj, String string) {
        if (obj.has(string)) {
            try {
                return obj.getBoolean(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public static String getTimeStamp(String date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format == null ? "dd/MM/yyyy" : format);
        Date now = null;
        try {
            now = sdf.parse(date);
            return String.valueOf(now.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
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

    public static String[] getArrayFromJSON(String val) {
        String[] array = new String[0];
        if (val != null) {
            try {

                JSONArray arr = new JSONArray(val);
                array = new String[arr.length()];
                for (int i = 0; i < arr.length(); i++) {
                    array[i] = arr.getString(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return array;
    }

    public static JSONArray getJSONArrayFromJSONObject(JSONObject obj, String string) {
        if (!obj.isNull(string)) {
            try {
                return obj.getJSONArray(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


    public static JSONObject getJSONObjectFromJSONObject(JSONObject obj, String string) {
        if (!obj.isNull(string)) {
            try {
                return obj.getJSONObject(string);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static Date convertStringToDate(String sDate) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {

            date = df.parse(sDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        return date;
    }

    public static long getTodayEndTimeInMilli() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, 00, 00, 00);
        // calendar.set(Calendar.MILLISECOND, 00);
        calendar.add(Calendar.MILLISECOND, -1 * (calendar.get(Calendar.MILLISECOND) + 1));
        return calendar.getTimeInMillis();
    }

    public static long getTodayStartTimeInMilli() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 00, 00, 00);
        calendar.add(Calendar.MILLISECOND, 1 * (calendar.get(Calendar.MILLISECOND) + 1));
        return calendar.getTimeInMillis();
    }

    /**
     * resize
     *
     * @param filePath
     * @return
     */
    public static Bitmap getSmallBitmap(String filePath, int reqWidth, int reqHeight) {

        File file = new File(filePath);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize based on a preset ratio
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap compressedImage = BitmapFactory.decodeFile(filePath, options);

        return compressedImage;
    }

    public static String getCurrentZoneFromUtc(String utcTime) {
        String localTime = null;
        if (utcTime != null) {
            if (!utcTime.equals("")) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                    Date value = formatter.parse(utcTime);
                    formatter.setTimeZone(TimeZone.getDefault());
                    localTime = formatter.format(value);
                } catch (ParseException pe) {
                    localTime = utcTime;
                }
            }
        }
        return localTime;
    }

    public static String getUtcTimeZoneFromCurrent(String localTime) {
        String utcTime = null;
        if (localTime != null) {
            if (!localTime.equals("")) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                    Date date = formatter.parse(localTime);

                    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
                    utcTime = formatter.format(date);

                } catch (ParseException pe) {
                    utcTime = localTime;
                }
            }
        }
        return utcTime;
    }

    public static String getCurrentZoneFromUtc(String utcTime, String format) {
        String localTime = null;
        if (utcTime != null && !utcTime.isEmpty()) {
            try {
                SimpleDateFormat formatter = new SimpleDateFormat(format == null ? "yyyy-MM-dd HH:mm:ss" : format);
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date value = formatter.parse(utcTime);
                formatter.setTimeZone(TimeZone.getDefault());
                localTime = formatter.format(value);
            } catch (ParseException pe) {
                localTime = utcTime;
            }
        }
        return localTime;
    }

    public static String getUtcTimeZoneFromTimeStamp(String timeStamp) {
        String utcTime = null;
        if (timeStamp != null && !timeStamp.isEmpty()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.valueOf(timeStamp));

            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            utcTime = formatter.format(calendar.getTime());
        }
        return utcTime;
    }

    public static int generateRandomId() {
        Random random = new Random();
        return random.nextInt(100000);
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    public static long getDifferenceDays(String d1, String d2, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format == null ? "dd/MM/yyyy" : format);
        Date date1 = null;
        try {

            date1 = sdf.parse(d1);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        Date date2 = null;
        try {

            date2 = sdf.parse(d2);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }
        if (date1 != null && date2 != null) {
            long diff = date2.getTime() - date1.getTime();
            return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } else {
            return 0;
        }
    }

    public static ArrayList<String> getSortedList(ArrayList<String> list) {
        try {
            if (isNumeric(list.get(0))) {
                Collections.sort(list, NUMBER_ORDER);
                return list;
            }

        } catch (Exception e) {

        }
        Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
        return list;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static final Comparator<String> NUMBER_ORDER = new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            if (s1.trim().length() < s2.trim().length()) {
                return -1;
            }
            int x = Integer.parseInt(s1.replaceAll("\\D", ""));
            int y = Integer.parseInt(s2.replaceAll("\\D", ""));
            return (x < y) ? -1 : ((x == y) ? 0 : 1);
        }
    };

   /* public static float round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }*/

    public static boolean containsInJsonArray(JSONArray jsonArray, String toSearch){
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                if (jsonArray.getString(i).equals(toSearch))
                    return true;
            }
        }catch(JSONException ex){
            Log.e("containsInJsonArray",ex.getMessage());
        }
        return false;
    }

    public static String createTemporaryFile(String part, String ext) throws IOException {
        File tempDir = Environment.getExternalStorageDirectory();
        //Log.e("createTemporaryFile",tempDir.getAbsolutePath());
        tempDir = new File(tempDir.getAbsolutePath() + "/.temp/");
        if (!tempDir.exists()) {
            tempDir.mkdir();
        }
        return File.createTempFile(part, ext, tempDir).getAbsolutePath();
    }

    public static Bitmap getBitmapFromUri(Uri uri, ContentResolver contentResolver) throws IOException {

        ParcelFileDescriptor parcelFileDescriptor =
                contentResolver.openFileDescriptor(uri, "r");
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
    }


}
