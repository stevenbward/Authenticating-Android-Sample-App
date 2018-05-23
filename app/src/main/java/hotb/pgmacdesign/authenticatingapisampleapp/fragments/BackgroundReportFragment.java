package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class BackgroundReportFragment  extends Fragment {

    private static BackgroundReportFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private TextView backgroundreport_top_tv, backgroundreport_bottom_tv;
    private Button backgroundreport_submit_button;


    public static BackgroundReportFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new BackgroundReportFragment(activity, listener);
        }
        return instance;
    }

    public BackgroundReportFragment(){};

    public BackgroundReportFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.backgroundreport_fragment, container, false);
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

        backgroundreport_top_tv = (TextView) view.findViewById(
                R.id.backgroundreport_top_tv);
        backgroundreport_bottom_tv = (TextView) view.findViewById(
                R.id.backgroundreport_bottom_tv);
        backgroundreport_submit_button = (Button) view.findViewById(
                R.id.backgroundreport_submit_button);

        backgroundreport_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthenticatingAPICalls.generateBackgroundReport(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object o, int i) {
                                if (i == AuthenticatingConstants.TAG_ERROR_RESPONSE) {
                                    AuthenticatingException e = (AuthenticatingException) o;
                                    L.Toast(getActivity(), "An error has Occurred: " +
                                            e.getAuthErrorString());
                                    backgroundreport_bottom_tv.setText("Call Failed");
                                } else if (i == AuthenticatingConstants.TAG_SIMPLE_RESPONSE) {
                                    L.toast(getActivity(), "Success!");
                                    backgroundreport_bottom_tv.setText("Call Succeeded");
                                } else {
                                    backgroundreport_bottom_tv.setText("Call Failed");
                                }
                            }
                        }, Constants.SAMPLE_COMPANY_API_KEY,
                        MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, "")
                );
                /*
                if(successful){

                } else {

                }
                */
            }
        });

    }
}
