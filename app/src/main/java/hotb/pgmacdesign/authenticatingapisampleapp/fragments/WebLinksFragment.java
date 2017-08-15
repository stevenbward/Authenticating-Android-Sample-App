package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class WebLinksFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static WebLinksFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private ListView weblinks_listview;

    public static WebLinksFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new WebLinksFragment(activity, listener);
        }
        return instance;
    }

    public WebLinksFragment(){};

    public WebLinksFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.weblinks_fragment, container, false);
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
        weblinks_listview = (ListView) view.findViewById(R.id.weblinks_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, Constants.URL_DESCRIPTIONS);
        weblinks_listview.setAdapter(adapter);
        weblinks_listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        switch (position){
            case 0: //MAIN_DOCUMENTATION_PAGE
                intent.setData(Uri.parse(Constants.MAIN_DOCUMENTATION_PAGE));
                break;
            case 1: //REGISTER_FOR_ACCOUNT
                intent.setData(Uri.parse(Constants.REGISTER_FOR_ACCOUNT));
                break;
            case 2: //CONTACT_US_PAGE
                intent.setData(Uri.parse(Constants.CONTACT_US_PAGE));
                break;
            default:
                intent.setData(Uri.parse(Constants.REGISTER_FOR_ACCOUNT));
        }
        activity.startActivity(intent);
    }
}
