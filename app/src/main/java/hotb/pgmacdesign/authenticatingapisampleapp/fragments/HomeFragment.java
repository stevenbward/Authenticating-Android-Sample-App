package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.MyApplication;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.SimpleUtilities;
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class HomeFragment extends Fragment implements View.OnClickListener {

    private static HomeFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private Button home_fragment_button;
    private TextView home_fragment_tv;
    private EditText home_fragment_et;

    public static HomeFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new HomeFragment(activity, listener);
        }
        return instance;
    }

    public HomeFragment(){};

    public HomeFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.home_fragment, container, false);
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

    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {
        home_fragment_button = (Button) view.findViewById(R.id.home_fragment_button);
        home_fragment_tv = (TextView) view.findViewById(R.id.home_fragment_tv);
        home_fragment_et = (EditText) view.findViewById(R.id.home_fragment_et);

        home_fragment_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.home_fragment_button:
                String str = home_fragment_et.getText().toString();
                if(SimpleUtilities.isNullOrEmpty(str)){
                    L.toast(getActivity(), "Please Enter a Valid Code");
                    return;
                } else {
                    try {
                        MyApplication.getSharedPrefsInstance().save(
                                Constants.ACCESS_CODE, str
                        );
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    listener.showOrHideLoadingAnimation(true);
                    AuthenticatingAPICalls.getUser(
                            new OnTaskCompleteListener() {
                                @Override
                                public void onTaskComplete(Object o, int i) {
                                    listener.showOrHideLoadingAnimation(false);
                                    if(o == null){
                                        L.toast(getActivity(), "An unknown error has occurred");
                                    }
                                    L.m("RETURNED TAG == " + i);
                                    if(i == AuthenticatingConstants.TAG_ERROR_RESPONSE){
                                        AuthenticatingException e = (AuthenticatingException) o;
                                        L.Toast(getActivity(), "An error has Occurred: " +
                                                e.getAuthErrorString());
                                    } else {
                                        L.toast(getActivity(), "Success, user retrieved");
                                    }
                                }
                            }, Constants.SAMPLE_COMPANY_API_KEY, str
                    );
                }
                break;
        }
    }
}
