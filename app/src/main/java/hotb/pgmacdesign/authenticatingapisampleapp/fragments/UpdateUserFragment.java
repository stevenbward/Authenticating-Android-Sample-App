package hotb.pgmacdesign.authenticatingapisampleapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.activities.MainActivity;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.MyApplication;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.SimpleUtilities;
import hotb.pgmacdesign.authenticatingsdk.datamodels.AuthenticatingException;
import hotb.pgmacdesign.authenticatingsdk.datamodels.UserHeader;
import hotb.pgmacdesign.authenticatingsdk.interfaces.OnTaskCompleteListener;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingAPICalls;
import hotb.pgmacdesign.authenticatingsdk.networking.AuthenticatingConstants;

/**
 * Created by pmacdowell on 2017-08-04.
 */

public class UpdateUserFragment extends Fragment implements View.OnClickListener {

    private static UpdateUserFragment instance;

    private MainActivity activity;
    private MainActivityListener listener;

    private UserHeader.User user;

    //UI
    private TextView update_user_first_name_tv, update_user_last_name_tv,
            update_user_email_tv, update_user_phone_tv, update_user_year_tv,
            update_user_month_tv, update_user_day_tv, update_user_address_tv,
            update_user_city_tv, update_user_state_tv, update_user_zip_tv,
            update_user_ssn_tv, update_user_buildingnumber_tv,
            update_user_street_tv, update_user_province_tv;
    private EditText update_user_first_name_et, update_user_last_name_et,
            update_user_email_et, update_user_phone_et, update_user_year_et,
            update_user_month_et, update_user_day_et, update_user_address_et,
            update_user_city_et, update_user_state_et, update_user_zip_et,
            update_user_ssn_et, update_user_buildingnumber_et,
            update_user_street_et, update_user_province;
    private LinearLayout update_user_fname_layout, update_user_lname_layout,
            update_user_email_layout, update_user_phone_layout, update_user_yob_layout,
            update_user_mob_layout, update_user_dob_layout, update_user_address_layout,
            update_user_city_layout, update_user_state_layout, update_user_zipcode_layout,
            update_user_buildingnumber_layout, update_user_street_layout,
            update_user_province_layout, update_user_ssn_layout;
    private TextView update_user_top_tv;
    private Button update_user_button;

    public static UpdateUserFragment getInstance(MainActivity activity, MainActivityListener listener){
        if(instance == null){
            instance = new UpdateUserFragment(activity, listener);
        }
        return instance;
    }

    public UpdateUserFragment(){};

    public UpdateUserFragment(MainActivity activity, MainActivityListener listener){
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
        View view = inflater.inflate(R.layout.update_user_fragment, container, false);
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
        user = null;
    }

    /**
     * Setup UI
     * @param view
     */
    private void initUI(View view) {
        update_user_first_name_tv = (TextView) view.findViewById(
                R.id.update_user_first_name_tv);
        update_user_last_name_tv = (TextView) view.findViewById(
                R.id.update_user_last_name_tv);
        update_user_email_tv = (TextView) view.findViewById(
                R.id.update_user_email_tv);
        update_user_phone_tv = (TextView) view.findViewById(
                R.id.update_user_phone_tv);
        update_user_year_tv = (TextView) view.findViewById(
                R.id.update_user_year_tv);
        update_user_month_tv = (TextView) view.findViewById(
                R.id.update_user_month_tv);
        update_user_day_tv = (TextView) view.findViewById(
                R.id.update_user_day_tv);
        update_user_address_tv = (TextView) view.findViewById(
                R.id.update_user_address_tv);
        update_user_city_tv = (TextView) view.findViewById(
                R.id.update_user_city_tv);
        update_user_state_tv = (TextView) view.findViewById(
                R.id.update_user_state_tv);
        update_user_zip_tv = (TextView) view.findViewById(
                R.id.update_user_zip_tv);
        update_user_top_tv = (TextView) view.findViewById(
                R.id.update_user_top_tv);
        update_user_ssn_tv = (TextView) view.findViewById(
                R.id.update_user_ssn_tv);

        update_user_first_name_et = (EditText) view.findViewById(
                R.id.update_user_first_name_et);
        update_user_last_name_et = (EditText) view.findViewById(
                R.id.update_user_last_name_et);
        update_user_email_et = (EditText) view.findViewById(
                R.id.update_user_email_et);
        update_user_phone_et = (EditText) view.findViewById(
                R.id.update_user_phone_et);
        update_user_month_et = (EditText) view.findViewById(
                R.id.update_user_month_et);
        update_user_year_et = (EditText) view.findViewById(
                R.id.update_user_year_et);
        update_user_day_et = (EditText) view.findViewById(
                R.id.update_user_day_et);
        update_user_address_et = (EditText) view.findViewById(
                R.id.update_user_address_et);
        update_user_city_et = (EditText) view.findViewById(
                R.id.update_user_city_et);
        update_user_state_et = (EditText) view.findViewById(
                R.id.update_user_state_et);
        update_user_zip_et = (EditText) view.findViewById(
                R.id.update_user_zip_et);
        update_user_ssn_et = (EditText) view.findViewById(
                R.id.update_user_ssn_et);
        update_user_buildingnumber_et = (EditText) view.findViewById(
                R.id.update_user_buildingnumber_et);
        update_user_street_et = (EditText) view.findViewById(
                R.id.update_user_street_et);
        update_user_province = (EditText) view.findViewById(
                R.id.update_user_province);

        update_user_fname_layout = (LinearLayout) view.findViewById(
                R.id.update_user_fname_layout);
        update_user_lname_layout = (LinearLayout) view.findViewById(
                R.id.update_user_lname_layout);
        update_user_email_layout = (LinearLayout) view.findViewById(
                R.id.update_user_email_layout);
        update_user_phone_layout = (LinearLayout) view.findViewById(
                R.id.update_user_phone_layout);
        update_user_yob_layout = (LinearLayout) view.findViewById(
                R.id.update_user_yob_layout);
        update_user_mob_layout = (LinearLayout) view.findViewById(
                R.id.update_user_mob_layout);
        update_user_dob_layout = (LinearLayout) view.findViewById(
                R.id.update_user_dob_layout);
        update_user_address_layout = (LinearLayout) view.findViewById(
                R.id.update_user_address_layout);
        update_user_city_layout = (LinearLayout) view.findViewById(
                R.id.update_user_city_layout);
        update_user_state_layout = (LinearLayout) view.findViewById(
                R.id.update_user_state_layout);
        update_user_zipcode_layout = (LinearLayout) view.findViewById(
                R.id.update_user_zipcode_layout);
        update_user_buildingnumber_layout = (LinearLayout) view.findViewById(
                R.id.update_user_buildingnumber_layout);
        update_user_street_layout = (LinearLayout) view.findViewById(
                R.id.update_user_street_layout);
        update_user_province_layout = (LinearLayout) view.findViewById(
                R.id.update_user_province_layout);
        update_user_ssn_layout = (LinearLayout) view.findViewById(
                R.id.update_user_ssn_layout);


        update_user_top_tv = (TextView) view.findViewById(
                R.id.update_user_top_tv);
        update_user_button = (Button) view.findViewById(
                R.id.update_user_button);

        update_user_button.setOnClickListener(this);
    }



    private void updateUserTVs() {
        if(user != null){
            update_user_first_name_et.setText("" + user.getFirstName());
            update_user_last_name_et.setText("" + user.getLastName());
            update_user_email_et.setText("" + user.getEmail());
            update_user_phone_et.setText("" + user.getPhone());
            update_user_month_et.setText("" + user.getMonth());
            update_user_year_et.setText("" + user.getYear());
            update_user_day_et.setText("" + user.getDay());
            update_user_address_et.setText("" + user.getAddress());
            update_user_city_et.setText("" + user.getCity());
            update_user_state_et.setText("" + user.getState());
            update_user_zip_et.setText("" + user.getZipcode());
            update_user_buildingnumber_et.setText("" + user.getBuildingNumber());
            update_user_street_et.setText("" + user.getStreet());
            update_user_province.setText("" + user.getProvince());
            update_user_ssn_et.setText("");

            String country = user.getCountry();
            if(!SimpleUtilities.isNullOrEmpty(country)) {
                if(country.equalsIgnoreCase("CAN")){
                    update_user_province_layout.setVisibility(View.VISIBLE);
                    update_user_address_layout.setVisibility(View.GONE);
                    update_user_state_layout.setVisibility(View.GONE);
                    update_user_buildingnumber_layout.setVisibility(View.VISIBLE);
                    update_user_street_layout.setVisibility(View.VISIBLE);
                    update_user_province_layout.setVisibility(View.VISIBLE);

                } else if (country.equalsIgnoreCase("USA")){
                    update_user_province_layout.setVisibility(View.GONE);
                    update_user_address_layout.setVisibility(View.VISIBLE);
                    update_user_state_layout.setVisibility(View.VISIBLE);
                    update_user_buildingnumber_layout.setVisibility(View.GONE);
                    update_user_street_layout.setVisibility(View.GONE);
                    update_user_province_layout.setVisibility(View.GONE);

                } //Can add more here
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.update_user_button:
                if(user == null){
                    user = new UserHeader.User();
                }
                String email = update_user_email_et.getText().toString();
                String phone = update_user_phone_et.getText().toString();
                String firstName = update_user_first_name_et.getText().toString();
                String lastName = update_user_last_name_et.getText().toString();
                String address = update_user_address_et.getText().toString();
                String city = update_user_city_et.getText().toString();
                String state = update_user_state_et.getText().toString();
                String zipCode = update_user_zip_et.getText().toString();
                String buildingNumber = update_user_buildingnumber_et.getText().toString();
                String street = update_user_street_et.getText().toString();
                String province = update_user_province.getText().toString();
                String year = update_user_year_et.getText().toString();
                Integer yearInt = null;
                String month = update_user_month_et.getText().toString();
                Integer monthInt = null;
                String day = update_user_day_et.getText().toString();
                Integer dayInt = null;
                String ssn = update_user_ssn_et.getText().toString();
                try {
                    yearInt = Integer.parseInt(year);
                    monthInt = Integer.parseInt(month);
                    dayInt = Integer.parseInt(day);
                } catch (Exception e){}

                listener.showOrHideLoadingAnimation(true);
                AuthenticatingAPICalls.updateUser(
                        new OnTaskCompleteListener() {
                            @Override
                            public void onTaskComplete(Object o, int i) {
                                listener.showOrHideLoadingAnimation(false);
                                //NOTE! Always wipe SSN on return regardless of type for security
                                update_user_ssn_et.setText("");
                                if(i == AuthenticatingConstants.TAG_ERROR_RESPONSE){
                                    AuthenticatingException e = (AuthenticatingException) o;
                                    L.Toast(getActivity(), "A error has occurred: " + e.getAuthErrorString());
                                }
                                if(i == AuthenticatingConstants.TAG_USER_HEADER){
                                    L.toast(getActivity(), "Your account has been Updated");
                                    UserHeader userHeader = (UserHeader) o;
                                    if(userHeader != null) {
                                        UserHeader.User user = userHeader.getUser();
                                    }

                                } else {
                                    L.Toast(getActivity(), "An unknown error has occurred");
                                }

                            }
                        }, Constants.SAMPLE_COMPANY_API_KEY,
                        MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, ""),
                        firstName, lastName, yearInt, monthInt, dayInt, address,
                        city, state, zipCode, street, province, buildingNumber, email, phone, ssn
                );
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.showOrHideLoadingAnimation(true);
        AuthenticatingAPICalls.getUser(
                new OnTaskCompleteListener() {
                    @Override
                    public void onTaskComplete(Object o, int i) {
                        listener.showOrHideLoadingAnimation(false);
                        if(i == AuthenticatingConstants.TAG_USER_HEADER){
                            //Success
                            UserHeader returnedUser = (UserHeader)o;
                            try {
                                user = returnedUser.getUser();
                                updateUserTVs();
                            } catch (Exception e){
                                user = new UserHeader.User();
                            }
                        } else {
                            //Not a success, no user
                            user = new UserHeader.User();
                        }
                    }
                }, Constants.SAMPLE_COMPANY_API_KEY,
                MyApplication.getSharedPrefsInstance().getString(Constants.ACCESS_CODE, "")
        );
    }
}
