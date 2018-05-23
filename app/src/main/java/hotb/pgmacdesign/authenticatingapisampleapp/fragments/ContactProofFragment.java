package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.MyApplication;
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class ContactProofFragment extends Fragment implements View.OnClickListener {

    private static ContactProofFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private RelativeLayout contactproof_phone_number_layout, contactproof_email_layout;
    private EditText contactproof_phone_et, contactproof_email_et;
    private Button contactproof_phone_submit_button, contactproof_email_submit_button;

    private TextWatcher phoneWatcher, emailWatcher;

    private boolean userHasBeenSentText;

    public static ContactProofFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new ContactProofFragment(activity, listener);
        }
        return instance;
    }

    public ContactProofFragment(){};

    public ContactProofFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.contactproof_fragment, container, false);
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
        phoneWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String phone = contactproof_phone_et.getText().toString();
                boolean phoneOk = false;
                if(!phone.isEmpty()){
                    //Do phone validation here
                    phoneOk = true;
                }
                contactproof_phone_submit_button.setEnabled(phoneOk);
            }
        };
        emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String email = contactproof_email_et.getText().toString();
                boolean emailOk = false;
                if(!email.isEmpty()){
                    //Do email validation here
                    emailOk = true;
                }
                contactproof_email_submit_button.setEnabled(emailOk);
            }
        };
        userHasBeenSentText = false;
    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {

        contactproof_phone_number_layout = (RelativeLayout) view.findViewById(
                R.id.contactproof_phone_number_layout);
        contactproof_email_layout = (RelativeLayout) view.findViewById(
                R.id.contactproof_email_layout);
        contactproof_phone_et = (EditText) view.findViewById(
                R.id.contactproof_phone_et);
        contactproof_email_et = (EditText) view.findViewById(
                R.id.contactproof_email_et);
        contactproof_phone_submit_button = (Button) view.findViewById(
                R.id.contactproof_phone_submit_button);
        contactproof_email_submit_button = (Button) view.findViewById(
                R.id.contactproof_email_submit_button);

        contactproof_email_submit_button.setOnClickListener(this);
        contactproof_phone_submit_button.setOnClickListener(this);

        contactproof_email_et.addTextChangedListener(emailWatcher);
        contactproof_phone_et.addTextChangedListener(phoneWatcher);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.contactproof_email_submit_button:
                listener.showOrHideLoadingAnimation(true);
                String email = contactproof_email_et.getText().toString();
                AuthenticatingAPICalls.updateUser(new OnTaskCompleteListener() {
                                                      @Override
                                                      public void onTaskComplete(Object o, int i) {
                      AuthenticatingAPICalls.verifyEmail(new OnTaskCompleteListener() {
                             @Override
                             public void onTaskComplete(Object o, int i) {
                                 listener.showOrHideLoadingAnimation(false);
                                 if(i == AuthenticatingConstants.TAG_ERROR_RESPONSE){
                                     L.Toast(getActivity(), "Could not send an email, please try again");
                                 } else if(i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE){
                                     L.Toast(getActivity(), "Success, check your inbox for your verification email");
                                 }
                             }
                         },Constants.SAMPLE_COMPANY_API_KEY,
                              MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, "")
                      );
                  }
              }, Constants.SAMPLE_COMPANY_API_KEY,
                        MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                        null, null, null, null, null, null, null, null, null, null, null, null, email, null, null);
                break;

            case R.id.contactproof_phone_submit_button:
                listener.showOrHideLoadingAnimation(true);
                if(!userHasBeenSentText){
                    String phone = contactproof_phone_et.getText().toString();
                    AuthenticatingAPICalls.verifyPhone(
                            new OnTaskCompleteListener() {
                                @Override
                                public void onTaskComplete(Object o, int i) {
                                    listener.showOrHideLoadingAnimation(false);
                                    if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                        AuthenticatingException e = (AuthenticatingException) o;
                                        L.Toast(getActivity(), "An error has Occurred: " +
                                                e.getAuthErrorString());
                                    } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                        L.toast(getActivity(), "You have been sent an SMS with your code");
                                        L.Toast(getActivity(), "When you receive it, enter it into the Edit Text below");
                                        userHasBeenSentText = true;
                                        contactproof_phone_et.setText("");
                                    }
                                }
                            }, Constants.SAMPLE_COMPANY_API_KEY,
                            MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, "")
                    );
                } else {
                    String code = contactproof_phone_et.getText().toString();
                    AuthenticatingAPICalls.verifyPhoneCode(
                            new OnTaskCompleteListener() {
                                @Override
                                public void onTaskComplete(Object o, int i) {
                                    listener.showOrHideLoadingAnimation(false);
                                    if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                        AuthenticatingException e = (AuthenticatingException) o;
                                        L.Toast(getActivity(), "An error has Occurred: " +
                                                e.getAuthErrorString());
                                    } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                        L.Toast(getActivity(), "Success! You have verified your phone number!");
                                    }
                                }
                            }, Constants.SAMPLE_COMPANY_API_KEY,
                            MyApplication.getSharedPrefsInstance()
                                    .getString(Constants.ACCESS_CODE, ""), code
                    );
                }

                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
