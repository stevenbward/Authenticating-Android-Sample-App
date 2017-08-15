package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Timer;
import java.util.UUID;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.MyApplication;
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.datamodels.SimpleResponseObj;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class SocialProofFragment extends Fragment implements View.OnClickListener {

    private static SocialProofFragment instance;

    private LinearLayout socialproof_instagram_layout, socialproof_facebook_layout,
            socialproof_twitter_layout, socialproof_google_layout;
    private MainActivity activity;
    private MainActivityListener listener;

    private Timer timer;

    public static SocialProofFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new SocialProofFragment(activity, listener);
        }
        return instance;
    }

    public SocialProofFragment(){};

    public SocialProofFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.socialproof_fragment, container, false);
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
        socialproof_instagram_layout = (LinearLayout) view.findViewById(
                R.id.socialproof_instagram_layout);
        socialproof_facebook_layout = (LinearLayout) view.findViewById(
                R.id.socialproof_facebook_layout);
        socialproof_twitter_layout = (LinearLayout) view.findViewById(
                R.id.socialproof_twitter_layout);
        socialproof_google_layout = (LinearLayout) view.findViewById(
                R.id.socialproof_google_layout);

        socialproof_instagram_layout.setOnClickListener(this);
        socialproof_facebook_layout.setOnClickListener(this);
        socialproof_twitter_layout.setOnClickListener(this);
        socialproof_google_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String network = null;

        //Make sure to get real data here from the Social Media account login:
        String userId = UUID.randomUUID().toString();
        String accessToken = UUID.randomUUID().toString();
        switch (v.getId()){
            case R.id.socialproof_instagram_layout:
                network = "instagram";
                break;
            case R.id.socialproof_facebook_layout:
                network = "facebook";
                break;
            case R.id.socialproof_twitter_layout:
                network = "twitter";
                break;
            case R.id.socialproof_google_layout:
                network = "google";
                break;

        }
        listener.showOrHideLoadingAnimation(true);
        AuthenticatingAPICalls.verifySocialNetworks(
                new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(Object o, int i) {
                        listener.showOrHideLoadingAnimation(false);
                        if(i == AuthenticatingConstants.TAG_ERROR_RESPONSE){
                            AuthenticatingException e = (AuthenticatingException) o;
                            L.toast(getActivity(), "An Error has occurred: " +
                                    e.getAuthErrorString());
                        } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE_OBJ){
                            SimpleResponseObj s = (SimpleResponseObj) o;
                            L.Toast(getActivity(), "Call was successful == " +
                                    s.getSimpleResponse().getSuccess());
                        }
                    }
                }, Constants.SAMPLE_COMPANY_API_KEY,
                MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                network, accessToken, userId
        );
    }
}
