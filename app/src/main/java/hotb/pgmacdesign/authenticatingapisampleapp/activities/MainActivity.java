package hotb.pgmacdesign.authenticatingapisampleapp.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import hotb.pgmacdesign.authenticatingapisampleapp.R;
import hotb.pgmacdesign.authenticatingapisampleapp.dialogs.SimpleProgressBar;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.AboutusFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.ApiCallsFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.BackgroundReportFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.ContactProofFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.HomeFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.IdentityProofFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.PhotoProofFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.SocialProofFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.UpdateUserFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.fragments.WebLinksFragment;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.MainActivityListener;
import hotb.pgmacdesign.authenticatingapisampleapp.interfaces.PictureTakenListener;
import hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants;
import hotb.pgmacdesign.authenticatingsdk.networking.WebCallsLogging;

import static hotb.pgmacdesign.authenticatingapisampleapp.misc.Constants.permissions;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        MainActivityListener {

    private Constants.AllFragments currentFragment;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private RelativeLayout fragment_layout;
    private FragmentManager fm;
    private SimpleProgressBar simpleProgressBar;
    private ActionBarDrawerToggle toggle;
    private PictureTakenListener pictureTakenListener;
    private Toolbar toolbar;

    private static final int CONTAINER_FRAGMENT_INT = R.id.fragment_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        WebCallsLogging.setJsonLogging(true);
        //Check permissions. Please make sure to do this upon the request, not when the app loads
        if(ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED  ||
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.INTERNET)
                        != PackageManager.PERMISSION_GRANTED  ||
                ContextCompat.checkSelfPermission(
                        this, Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(this,
                    permissions, 12321);
        }
        setupUI();
        fm = getSupportFragmentManager();
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setupFirstFragment();
    }

    private void setupUI(){
        fragment_layout = (RelativeLayout) this.findViewById(R.id.fragment_layout);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return;
        }

        if(currentFragment != Constants.AllFragments.HomeFragment){
            fm.beginTransaction().replace(CONTAINER_FRAGMENT_INT,
                    HomeFragment.getInstance(MainActivity.this, MainActivity.this)).commit();
            this.currentFragment = Constants.AllFragments.HomeFragment;
            MainActivity.this.setToolbarTitle("Home");
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {//OLD - R.id.action_settings) {
            onNavigationItemSelected(item);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Clear the others first:
        clearSelectedTopMenuItems();
        clearSelectedBottomMenuItems();

        //Set the one to active:
        item.setChecked(true);
        manageActiveFragment(id);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void manageActiveFragment(int id){

        try {
            FragmentTransaction transaction = fm.beginTransaction();
            switch (id) {

                case R.id.action_info:
                    if(this.currentFragment != Constants.AllFragments.AboutUsFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                AboutusFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.AboutUsFragment;
                        MainActivity.this.setToolbarTitle("About HOTB");
                    }
                    break;

                case R.id.nav_social_proof:
                    if(this.currentFragment != Constants.AllFragments.SocialProofFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                SocialProofFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.SocialProofFragment;
                        MainActivity.this.setToolbarTitle("Social Proof");
                    }
                    break;

                case R.id.nav_contact_proof:
                    if(this.currentFragment != Constants.AllFragments.ContactProofFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                ContactProofFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.ContactProofFragment;
                        MainActivity.this.setToolbarTitle("Contact Proof");
                    }
                    break;

                case R.id.nav_photo_proof:
                    if(this.currentFragment != Constants.AllFragments.PhotoProofFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                PhotoProofFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.PhotoProofFragment;
                        MainActivity.this.setToolbarTitle("Photo Proof");
                    }
                    break;

                case R.id.nav_identity_proof:
                    if(this.currentFragment != Constants.AllFragments.IdentityProofFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                IdentityProofFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.IdentityProofFragment;
                        MainActivity.this.setToolbarTitle("Identity Proof");
                    }
                    break;

                case R.id.nav_background_report:
                    if(this.currentFragment != Constants.AllFragments.BackgroundReportFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                BackgroundReportFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.BackgroundReportFragment;
                        MainActivity.this.setToolbarTitle("Background Report");
                    }
                    break;

                case R.id.nav_api_calls_info:
                    if(this.currentFragment != Constants.AllFragments.ApiCallsFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                ApiCallsFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.ApiCallsFragment;
                        MainActivity.this.setToolbarTitle("Api Calls");
                    }
                    break;

                case R.id.nav_update_user:
                    if(this.currentFragment != Constants.AllFragments.UpdateUserFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                UpdateUserFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.UpdateUserFragment;
                        MainActivity.this.setToolbarTitle("Update User");
                    }
                    break;

                case R.id.nav_web_links:
                    if(this.currentFragment != Constants.AllFragments.WebLinksFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                WebLinksFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.WebLinksFragment;
                        MainActivity.this.setToolbarTitle("Web Links");
                    }
                    break;

                default:
                    if(this.currentFragment != Constants.AllFragments.HomeFragment){
                        transaction.replace(CONTAINER_FRAGMENT_INT,
                                HomeFragment.getInstance(MainActivity.this, MainActivity.this));
                        this.currentFragment = Constants.AllFragments.HomeFragment;
                        MainActivity.this.setToolbarTitle("Home");
                    }

            }
            transaction.commit();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private void clearSelectedTopMenuItems() {
        Menu menu = navigationView.getMenu();
        if(menu != null) {
            for (int i = 0; i < Constants.TOP_MENU_ITEMS.length; i++) {
                int x = Constants.TOP_MENU_ITEMS[i];
                MenuItem menuItem = menu.findItem(x);
                if (menuItem != null) {
                    menuItem.setChecked(false);
                }
            }
        }
    }

    private void clearSelectedBottomMenuItems() {
        Menu menu = navigationView.getMenu();
        if(menu != null) {
            for (int i = 0; i < Constants.BOTTOM_MENU_ITEMS.length; i++) {
                int x = Constants.BOTTOM_MENU_ITEMS[i];
                MenuItem menuItem = menu.findItem(x);
                if (menuItem != null) {
                    menuItem.setChecked(false);
                }
            }
        }
    }

    private void setupFirstFragment(){
        fm.beginTransaction().add(CONTAINER_FRAGMENT_INT,
                HomeFragment.getInstance(MainActivity.this, MainActivity.this)).commit();
        this.currentFragment = Constants.AllFragments.HomeFragment;
        MainActivity.this.setToolbarTitle("Home");
    }

    @Override
    public void showOrHideLoadingAnimation(boolean bool) {
        if(simpleProgressBar == null){
            simpleProgressBar = new SimpleProgressBar(this);
            Window window = simpleProgressBar.getWindow();
            if(window != null) {
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                lp.flags &= ~Window.FEATURE_NO_TITLE;
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(
                        ContextCompat.getColor(this, R.color.Transparent)
                ));
            }
            simpleProgressBar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
        if(bool){
            simpleProgressBar.show();
        } else {
            simpleProgressBar.hide();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Remember, DO NOT ASK FOR PERMISSIONS IN ONRESUME!
    }

    private void setupPictureTakenListener(){
        if(pictureTakenListener == null){
            pictureTakenListener = ((PictureTakenListener)PhotoProofFragment
                    .getInstance(this, this));
        }
    }

    public void setToolbarTitle(@NonNull String str){
        if(str.length() > 0){
            toolbar.setTitle(str);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PhotoProofFragment.LEFT_SIDE && resultCode == RESULT_OK) {
            setupPictureTakenListener();
            pictureTakenListener.photoTaken(true);
        }
        if (requestCode == PhotoProofFragment.RIGHT_SIDE && resultCode == RESULT_OK) {
            setupPictureTakenListener();
            pictureTakenListener.photoTaken(false);
        }
    }

}
