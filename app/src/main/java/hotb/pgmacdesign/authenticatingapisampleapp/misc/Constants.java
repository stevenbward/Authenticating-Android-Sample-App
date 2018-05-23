package hotb.pgmacdesign.authenticatingapisampleapp.misc;

import android.Manifest;

import hotb.pgmacdesign.authenticatingapisampleapp.R;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class Constants {

    //App Data
    public static final String SAMPLE_COMPANY_API_KEY = "e6017834-e840-49fc-80f3-f23b36c6fe00";

    //Shared Prefs
    public static final String SHARED_PREFS_NAME = "AuthenticateAPISampleApp_SP";
    public static final String ACCESS_CODE = "access_code";

    //Menu Items
    public static final int[] TOP_MENU_ITEMS = {
            R.id.nav_update_user, R.id.nav_social_proof, R.id.nav_contact_proof,
            R.id.nav_photo_proof, R.id.nav_identity_proof, R.id.nav_background_report
    };
    public static final int[] BOTTOM_MENU_ITEMS = {
            R.id.nav_api_calls_info, R.id.nav_web_links
    };

    //enums
    public static enum AllFragments {
        ApiCallsFragment, BackgroundReportFragment, ContactProofFragment, HomeFragment,
        IdentityProofFragment, PhotoProofFragment, SocialProofFragment, WebLinksFragment,
        AboutUsFragment, UpdateUserFragment
    }

    //Permissions
    public static final String[] permissions = {
            Manifest.permission.CAMERA, Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    //Documentation URLs
    public static final String MAIN_DOCUMENTATION_PAGE = "https://docs.authenticating.com";
    public static final String MAIN_DOCUMENTATION_PAGE_STR = "Main Documentation";
    public static final String REGISTER_FOR_ACCOUNT = "https://www.authenticating.com/";
    public static final String REGISTER_FOR_ACCOUNT_STR = "Authenticating.com";
    public static final String CONTACT_US_PAGE = "https://github.com/stevenbward/Authenticating-Android-SDK";
    public static final String CONTACT_US_PAGE_STR = "SDK Repository";

    //Misc
    public static final String[] LV_URLs = {MAIN_DOCUMENTATION_PAGE,
            REGISTER_FOR_ACCOUNT, CONTACT_US_PAGE};
    public static final String[] URL_DESCRIPTIONS = {MAIN_DOCUMENTATION_PAGE_STR,
            REGISTER_FOR_ACCOUNT_STR, CONTACT_US_PAGE_STR};
    public static final String[] API_CALLS = {
            "getUser", "updateUser", "comparePhotos",
            "verifyPhone", "verifyPhoneCode", "getAvailableNetworks",
            "verifySocialNetworks", "getQuizRequest",
            "verifyQuiz", "generateCriminalReport", "verifyEmail", "uploadId", "uploadIdEnhanced",
            "checkUploadId", "uploadPassport", "checkUploadPassport"
    };
}
