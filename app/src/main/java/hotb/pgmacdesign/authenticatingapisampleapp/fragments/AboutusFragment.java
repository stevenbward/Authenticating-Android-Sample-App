package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;

/**
 * Created by pmacdowell on 2017-07-31.
 */

public class AboutusFragment extends Fragment {


    private static AboutusFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    public static AboutusFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new AboutusFragment(activity, listener);
        }
        return instance;
    }

    public AboutusFragment(){};

    public AboutusFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.aboutus_fragment, container, false);
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

    }
}
