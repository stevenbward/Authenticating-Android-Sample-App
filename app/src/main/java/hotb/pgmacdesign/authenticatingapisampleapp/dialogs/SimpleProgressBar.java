package hotb.pgmacdesign.authenticatingapisampleapp.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.mingle.widget.LoadingView;

import hotb.pgmacdesign.authenticatingapisampleapp.R;

/**
 * Created by pmacdowell on 2017-07-27.
 */

public class SimpleProgressBar extends Dialog {

    private com.mingle.widget.LoadingView progress_bar;

    public SimpleProgressBar(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_progress_bar_layout);
        progress_bar = (LoadingView) this.findViewById(R.id.progress_bar);
    }

    @Override
    public void show() {
        try {
            super.show();
            progress_bar.invalidate();
            progress_bar.setVisibility(View.VISIBLE);
            progress_bar.bringToFront();
        } catch (Exception e){
            //Window leak, ignore
        }
    }

    @Override
    public void hide() {
        try {
            super.hide();
        } catch (Exception e){
            //Window leak, ignore
        }
    }
}
