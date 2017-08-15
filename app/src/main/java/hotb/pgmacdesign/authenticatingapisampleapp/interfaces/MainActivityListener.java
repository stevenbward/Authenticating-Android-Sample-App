package hotb.pgmacdesign.authenticatingapisampleapp.interfaces;

import android.support.annotation.NonNull;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public interface MainActivityListener {
    public void showOrHideLoadingAnimation(boolean bool);
    public void setToolbarTitle(@NonNull String str);
}
