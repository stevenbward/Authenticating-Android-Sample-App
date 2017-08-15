package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class PhotoProofFragment extends Fragment implements
        View.OnClickListener, PictureTakenListener {

    public static final int LEFT_SIDE = 333;
    public static final int RIGHT_SIDE = 444;

    private String leftPhotoPath, rightPhotoPath;
    private File leftPhotoFile, rightPhotoFile;
    private Uri leftPhotoUri, rightPhotoUri;
    private Bitmap leftBitmap, rightBitmap;

    private static PhotoProofFragment instance;

    //UI
    private TextView photoproof_top_tv;
    private ImageView photoproof_left_iv, photoproof_right_iv;
    private Button photoproof_button;

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
        leftPictureSet = this.rightPictureSet = false;
    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {
        photoproof_top_tv = (TextView) view.findViewById(R.id.photoproof_top_tv);
        photoproof_left_iv = (ImageView) view.findViewById(R.id.photoproof_left_iv);
        photoproof_right_iv = (ImageView) view.findViewById(R.id.photoproof_right_iv);
        photoproof_button = (Button) view.findViewById(R.id.photoproof_button);

        photoproof_button.setOnClickListener(this);
        photoproof_right_iv.setOnClickListener(this);
        photoproof_left_iv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.photoproof_left_iv:
                takePicture(true);
                break;

            case R.id.photoproof_right_iv:
                takePicture(false);
                break;

            case R.id.photoproof_button:
                if(leftPictureSet && rightPictureSet){
                    listener.showOrHideLoadingAnimation(true);
                    AuthenticatingAPICalls.comparePhotos(
                            new OnTaskCompleteListener() {
                                @Override
                                public void onTaskComplete(Object o, int i) {
                                    listener.showOrHideLoadingAnimation(false);
                                    if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                        AuthenticatingException e = (AuthenticatingException) o;
                                        L.Toast(getActivity(), "An error has Occurred: " +
                                                e.getAuthErrorString());
                                    } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE_OBJ) {
                                        L.toast(getActivity(), "Your photo has been uploaded successfully");
                                    }
                                }
                            }, Constants.SAMPLE_COMPANY_API_KEY,
                            MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                            leftBitmap, rightBitmap
                    );
                }
                break;
        }
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
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        //This file provider is in your manifest. See manifest for sample
                        "hotb.pgmacdesign.authenticatingapisampleapp.android.fileprovider",
                        photoFile);
                if(isLeftSide) {
                    leftPhotoUri = photoURI;
                } else {
                    rightPhotoUri = photoURI;
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                if(isLeftSide) {
                    activity.startActivityForResult(takePictureIntent, LEFT_SIDE);
                } else {
                    activity.startActivityForResult(takePictureIntent, RIGHT_SIDE);
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

}
