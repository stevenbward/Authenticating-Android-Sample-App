package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.PictureTakenListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.MyApplication;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.SimpleUtilities;
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.datamodels.CheckPhotoResults;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class PhotoProofFragment extends Fragment implements
        View.OnClickListener, PictureTakenListener {
    enum PhotoType {
        comparePhotos, uploadId, uploadIdEnhanced, uploadPassport
    }

    public static final int LEFT_SIDE = 333;
    public static final int RIGHT_SIDE = 444;

    private String leftPhotoPath, rightPhotoPath;
    private File leftPhotoFile, rightPhotoFile;
    private Uri leftPhotoUri, rightPhotoUri;
    private Bitmap leftBitmap, rightBitmap;
    private PhotoType photoType;
    private AlertDialog d;

    private static PhotoProofFragment instance;

    //UI
    private TextView photoproof_top_tv, photoproof_right_iv_tv, photoproof_left_iv_tv,
            photoproof_reset_tv;
    private Spinner photoproof_spinner;
    private ImageView photoproof_left_iv, photoproof_right_iv;
    private Button photoproof_button, photoproof_status_button;

    private ArrayAdapter<String> spinnerAdapter;
    private MainActivity activity;
    private MainActivityListener listener;

    private boolean leftPictureSet, rightPictureSet;

    public static PhotoProofFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new PhotoProofFragment(activity, listener);
        }
        return instance;
    }

    public PhotoProofFragment(){};

    public PhotoProofFragment(MainActivity activity, MainActivityListener listener){
        this.activity = activity;
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.photoproof_fragment, container, false);
        initUI(view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Setup Variables
     */
    private void initVariables(){
        this.leftPictureSet = this.rightPictureSet = false;
        this.photoType = PhotoType.uploadId;
        String[] strs = {"Upload ID", "Upload ID Enhanced", "Compare Photos", "Upload Passport"};
        this.spinnerAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, strs);
    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {
        photoproof_spinner = (Spinner) view.findViewById(R.id.photoproof_spinner);
        photoproof_top_tv = (TextView) view.findViewById(R.id.photoproof_top_tv);
        photoproof_right_iv_tv = (TextView) view.findViewById(R.id.photoproof_right_iv_tv);
        photoproof_left_iv_tv = (TextView) view.findViewById(R.id.photoproof_left_iv_tv);
        photoproof_reset_tv = (TextView) view.findViewById(R.id.photoproof_reset_tv);
        photoproof_left_iv = (ImageView) view.findViewById(R.id.photoproof_left_iv);
        photoproof_right_iv = (ImageView) view.findViewById(R.id.photoproof_right_iv);
        photoproof_button = (Button) view.findViewById(R.id.photoproof_button);
        photoproof_status_button = (Button) view.findViewById(R.id.photoproof_status_button);

        photoproof_status_button.setTransformationMethod(null);
        photoproof_button.setTransformationMethod(null);
        photoproof_status_button.setOnClickListener(this);
        photoproof_button.setOnClickListener(this);
        photoproof_right_iv.setOnClickListener(this);
        photoproof_left_iv.setOnClickListener(this);
        photoproof_reset_tv.setOnClickListener(this);

        photoproof_spinner.setAdapter(spinnerAdapter);
        photoproof_spinner.setSelection(0);
        setTextFields(0);
        photoproof_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    photoType = PhotoType.uploadId;
                } else if(position == 1){
                    photoType = PhotoType.uploadIdEnhanced;
                } else if(position == 2){
                    photoType = PhotoType.comparePhotos;
                } else if(position == 3){
                    photoType = PhotoType.uploadPassport;
                }
                setTextFields(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void setTextFields(int i) {
        switch (i){

            case 2:
                photoproof_left_iv_tv.setText("-- Img 1 --");
                photoproof_right_iv_tv.setText("-- Img 2 --");
                photoproof_left_iv_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                photoproof_right_iv_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                break;

            default:
            case 1:
            case 0:
                photoproof_left_iv_tv.setText("-- ID Front --");
                photoproof_right_iv_tv.setText("-- ID Back --");
                photoproof_left_iv_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                photoproof_right_iv_tv.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.photoproof_status_button:

                switch (photoType){
                    case uploadPassport:
                        activity.showOrHideLoadingAnimation(true);
                        AuthenticatingAPICalls.checkUploadPassport(new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object o, int i) {
                                activity.showOrHideLoadingAnimation(false);
                                String responseFromServer = null;
                                if(i == AuthenticatingConstants.TAG_CHECK_PHOTO_RESULT) {
                                    CheckPhotoResults myObjectToReturn = (CheckPhotoResults) o;
                                    if (myObjectToReturn != null) {
                                        try {
                                            responseFromServer = myObjectToReturn.getDescription();
                                            L.m("response == " + new Gson().toJson(
                                                    myObjectToReturn, CheckPhotoResults.class));
                                            responseFromServer = "Check Upload Passport: " + responseFromServer;
                                        } catch (Exception e){e.printStackTrace();}
                                    }
                                } else if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE){
                                    AuthenticatingException e = (AuthenticatingException) o;
                                    if(e != null) {
                                        e.printStackTrace();
                                        responseFromServer = e.getAuthErrorString();
                                    }
                                } else {
                                    L.m("Response == " + o);
                                }
                                if(SimpleUtilities.isNullOrEmpty(responseFromServer)){
                                    responseFromServer = "An error has occurred";
                                }
                                d = new AlertDialog.Builder(getActivity())
                                        .setTitle("Result")
                                        .setMessage(responseFromServer)
                                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create();
                                if(d != null){
                                    d.show();
                                }
                            }
                        }, Constants.SAMPLE_COMPANY_API_KEY, MyApplication.getSharedPrefsInstance().getString(
                                Constants.ACCESS_CODE, ""));
                        break;

                    case comparePhotos:
                        L.Toast(getActivity(), "'Compare Photos' does not need to check status");
                        break;

                    case uploadIdEnhanced:
                    case uploadId:
                        activity.showOrHideLoadingAnimation(true);
                        AuthenticatingAPICalls.checkUploadId(new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object o, int i) {
                                activity.showOrHideLoadingAnimation(false);
                                String responseFromServer = null;
                                if(i == AuthenticatingConstants.TAG_CHECK_PHOTO_RESULT) {
                                    CheckPhotoResults myObjectToReturn = (CheckPhotoResults) o;
                                    if (myObjectToReturn != null) {
                                        try {
                                            responseFromServer = myObjectToReturn.getDescription();
                                            L.m("response == " + new Gson().toJson(
                                                    myObjectToReturn, CheckPhotoResults.class));
                                            responseFromServer = "Check Upload ID: " + responseFromServer;
                                        } catch (Exception e){e.printStackTrace();}
                                    }
                                } else if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE){
                                    AuthenticatingException e = (AuthenticatingException) o;
                                    if(e != null) {
                                        e.printStackTrace();
                                        responseFromServer = e.getAuthErrorString();
                                    }
                                } else {
                                    L.m("Response == " + o);
                                }
                                if(SimpleUtilities.isNullOrEmpty(responseFromServer)){
                                    responseFromServer = "An error has occurred";
                                }
                                d = new AlertDialog.Builder(getActivity())
                                        .setTitle("Result")
                                        .setMessage(responseFromServer)
                                        .setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        })
                                        .create();
                                if(d != null){
                                    d.show();
                                }
                            }
                        }, Constants.SAMPLE_COMPANY_API_KEY, MyApplication.getSharedPrefsInstance().getString(
                                Constants.ACCESS_CODE, ""));
                        break;
                }

                break;

            case R.id.photoproof_reset_tv:
                leftPictureSet = rightPictureSet = false;
                rightBitmap = leftBitmap = null;
                photoproof_left_iv.setImageResource(R.drawable.ic_menu_camera);
                photoproof_right_iv.setImageResource(R.drawable.ic_menu_camera);
                photoproof_left_iv.setOnClickListener(this);
                photoproof_right_iv.setOnClickListener(this);
                break;

            case R.id.photoproof_left_iv:
                takePicture(true);
                break;

            case R.id.photoproof_right_iv:
                takePicture(false);
                break;

            case R.id.photoproof_button:
                if(photoType == null){
                    photoType = PhotoType.uploadId;
                }
                if(leftPictureSet && rightPictureSet){
                    //updatePhotos();
                    if(photoType == PhotoType.uploadId){
                        listener.showOrHideLoadingAnimation(true);

                        AuthenticatingAPICalls.uploadId(
                                new OnTaskCompleteListener() {
                                    @Override
                                    public void onTaskComplete(Object o, int i) {
                                        listener.showOrHideLoadingAnimation(false);
                                        if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                            AuthenticatingException e = (AuthenticatingException) o;
                                            if(e != null){
                                                L.Toast(getActivity(), "An error has Occurred: " +
                                                        e.getAuthErrorString());
                                            }

                                        } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                            L.toast(getActivity(), "Your photo has been uploaded successfully");
                                        }
                                    }
                                }, Constants.SAMPLE_COMPANY_API_KEY,
                                MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                                leftBitmap, rightBitmap
                        );
                    } else if (photoType == PhotoType.uploadIdEnhanced){
                        listener.showOrHideLoadingAnimation(true);

                        AuthenticatingAPICalls.uploadIdEnhanced(
                                new OnTaskCompleteListener() {
                                    @Override
                                    public void onTaskComplete(Object o, int i) {
                                        listener.showOrHideLoadingAnimation(false);
                                        if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                            AuthenticatingException e = (AuthenticatingException) o;
                                            if(e != null){
                                                L.Toast(getActivity(), "An error has Occurred: " +
                                                        e.getAuthErrorString());
                                            }

                                        } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                            L.toast(getActivity(), "Your photo has been uploaded successfully");
                                        }
                                    }
                                }, Constants.SAMPLE_COMPANY_API_KEY,
                                MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                                leftBitmap, rightBitmap
                        );

                    } else if (photoType == PhotoType.comparePhotos){
                        listener.showOrHideLoadingAnimation(true);
                        AuthenticatingAPICalls.comparePhotos(
                                new OnTaskCompleteListener() {
                                    @Override
                                    public void onTaskComplete(Object o, int i) {
                                        listener.showOrHideLoadingAnimation(false);
                                        if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                            AuthenticatingException e = (AuthenticatingException) o;
                                            if(e != null){
                                                L.Toast(getActivity(), "An error has Occurred: " +
                                                        e.getAuthErrorString());
                                            }

                                        } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                            L.toast(getActivity(), "Your photo has been uploaded successfully");
                                        }
                                    }
                                }, Constants.SAMPLE_COMPANY_API_KEY,
                                MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                                leftBitmap, rightBitmap
                        );
                    } else if (photoType == PhotoType.uploadPassport){
                        listener.showOrHideLoadingAnimation(true);
                        AuthenticatingAPICalls.uploadPassport(
                                new OnTaskCompleteListener() {
                                    @Override
                                    public void onTaskComplete(Object o, int i) {
                                        listener.showOrHideLoadingAnimation(false);
                                        if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                            AuthenticatingException e = (AuthenticatingException) o;
                                            if(e != null){
                                                L.Toast(getActivity(), "An error has Occurred: " +
                                                        e.getAuthErrorString());
                                            }

                                        } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                            L.toast(getActivity(), "Your passport has been uploaded successfully");
                                        }
                                    }
                                }, Constants.SAMPLE_COMPANY_API_KEY, MyApplication.getSharedPrefsInstance()
                                        .getString(Constants.ACCESS_CODE, ""), leftBitmap
                        );
                    }

                }
                break;
        }
    }



    private static Bitmap resizePhoto(@NonNull Bitmap bmp){
        try {
            double height = Math.sqrt(1000000 /
                    (((double) bmp.getWidth()) / bmp.getHeight()));
            double width = (height / bmp.getHeight()) * bmp.getWidth();
            Bitmap bmp1 = Bitmap.createScaledBitmap(bmp, (int)(width),
                    (int)(height), true);
            return bmp1;
        } catch (Exception e){
            e.printStackTrace();
            return bmp;
        }
    }

    private void updatePhotos(File file1, File file2){
        /*
        circle_loading_view.startDeterminate();
        circle_loading_view.setPercent(10);
        IF IT FAILS:
        circle_loading_view.stopFailure();
        */
    }

    /**
     * Take the picture
     * @param isLeftSide if is the left image, send true, else for right, send false
     */
    private void takePicture(boolean isLeftSide) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile(isLeftSide);
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(getActivity(),
                            "hotb.pgmacdesign.authenticatingapisampleapp.android.fileprovider",
                            photoFile);
                    if (isLeftSide) {
                        leftPhotoUri = photoURI;
                    } else {
                        rightPhotoUri = photoURI;
                    }
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    if (isLeftSide) {
                        activity.startActivityForResult(takePictureIntent, LEFT_SIDE);
                    } else {
                        activity.startActivityForResult(takePictureIntent, RIGHT_SIDE);
                    }
                } catch (IllegalArgumentException ile){
                    //This will trigger if the file provider is malformed or missing
                }
            }
        }
    }

    /**
     * Create and get the image file
     * @param isLeftSide
     * @return
     * @throws IOException
     */
    private File createImageFile(boolean isLeftSide) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "authentication_photo_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        if(isLeftSide){
            if(leftPhotoUri != null) {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(
                        activity.getContentResolver(), leftPhotoUri);
                if (bmp != null) {
                    leftBitmap = bmp;
                }
            }
        } else {
            if(rightPhotoUri != null) {
                Bitmap bmp = MediaStore.Images.Media.getBitmap(
                        activity.getContentResolver(), rightPhotoUri);
                if (bmp != null) {
                    rightBitmap = bmp;
                }
            }
        }
        // Save a file: path for use with ACTION_VIEW intents
        if(isLeftSide) {
            leftPhotoPath = image.getAbsolutePath();
        } else {
            rightPhotoPath = image.getAbsolutePath();
        }
        return image;
    }

    @Override
    public void onResume() {
        super.onResume();
        checkButton();
    }


    @Override
    public void photoTaken(boolean isLeftSide) {
        if(isLeftSide){
            try {
                leftPhotoFile = createImageFile(true);
                photoproof_left_iv.setPadding(20, 20, 20, 20);
                if(leftBitmap != null){
                    photoproof_left_iv.setImageBitmap(leftBitmap);
                } else {
                    photoproof_left_iv.setImageResource(R.drawable.checkmark);
                }
                photoproof_left_iv.setOnClickListener(null);
                leftPictureSet = true;
                checkButton();
            } catch (IOException ioe){
                ioe.printStackTrace();
                Toast.makeText(getActivity(), "Error when taking photo", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                rightPhotoFile = createImageFile(false);
                photoproof_right_iv.setPadding(20, 20, 20, 20);
                if(rightBitmap != null){
                    photoproof_right_iv.setImageBitmap(rightBitmap);
                } else {
                    photoproof_right_iv.setImageResource(R.drawable.checkmark);
                }
                photoproof_right_iv.setOnClickListener(null);
                rightPictureSet = true;
                checkButton();
            } catch (IOException ioe){
                ioe.printStackTrace();
                Toast.makeText(getActivity(), "Error when taking photo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkButton(){
        try {
            if (leftPictureSet && rightPictureSet) {
                photoproof_button.setEnabled(true);
            } else {
                photoproof_button.setEnabled(false);
            }
        } catch (Exception e){}
    }

    private static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            cursor.close();
            return Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }


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
                final String[] selectionArgs = new String[] {
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
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
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


    private static String encodeImage(Bitmap bm) {
        if (bm == null) {
            return null;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }
}
