package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

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

public class ApiCallsFragment  extends Fragment implements AdapterView.OnItemClickListener {

    private static ApiCallsFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private ListView apicalls_listview;
    private ArrayAdapter<String> adapter;

    public static ApiCallsFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new ApiCallsFragment(activity, listener);
        }
        return instance;
    }

    public ApiCallsFragment(){};

    public ApiCallsFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.apicalls_fragment, container, false);
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
        adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, Constants.API_CALLS);
    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {
        apicalls_listview = (ListView) view.findViewById(R.id.apicalls_listview);
        apicalls_listview.setAdapter(adapter);
        apicalls_listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String str = "";
        switch (position){
            case 0:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Get the User object. ";
                break;
            case 1:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Updates the user object. This is used for any call that indicates more information is needed to run. ";
                break;

            case 2:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Compare 2 photos for the photo verification test. Note, this is a multi-part call";
                break;

            case 3:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Sends the user an SMS with a code for them to then verify";
                break;

            case 4:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Takes in the received SMS code from the verifyPhone endpoint to finish phone verification.";
                break;

            case 5:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Retrieves a list of the networks that are available for a user to verify with on the social proof test.";
                break;

            case 6:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Once a user successfully logs into their social media account, this is used to complete the test.";
                break;

            case 7:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Retrieve a list of tailored questions to the user for them to prove their identity.";
                break;

            case 8:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Verify the quiz received by passing in the user's answers to the quiz";
                break;

            case 9:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Sends a request to check for any criminal background information on a user.";
                break;

            case 10:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Starts the email validation test by sending an email to the one on file.";
                break;

            case 11:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Upload an ID (IE, a Driver's License) for verification. Takes a Front and Back.";
                break;
            case 12:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Same as uploadId except the analysis is more robust and has a higher standard to pass.";
                break;
            case 13:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Check on the status the ID uploaded to determine if it needs to be redone.";
                break;
            case 14:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Upload a passport for verification. Requires only 1 picture to be sent.";
                break;
            case 15:
                str = Constants.API_CALLS[position];
                str = str + ", " + "Check on the status the Passport uploaded to determine if it needs to be redone.";
                break;
        }
        if(!str.isEmpty()) {
            L.Toast(getActivity(), str);
        }
    }
}
