package project.com.vehiclessharing.listener;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by Hihihehe on 6/5/2017.
 */

public abstract class InfoWindowTouchListener implements View.OnTouchListener{
        private final View view;
       /* private final Drawable bgDrawableNormal;
        private final Drawable bgDrawablePressed;*/
        private final Handler handler = new Handler();

        private Marker marker;
        private boolean pressed = false;

        public InfoWindowTouchListener(View view) {
            this.view = view;
           /* this.bgDrawableNormal = bgDrawableNormal;
            this.bgDrawablePressed = bgDrawablePressed;*/
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }

        @Override
        public boolean onTouch(View vv, MotionEvent event) {
            Log.d("onTouchwindowinfo","Touch button listener success");

            return false;
        }

        private void startPress() {

        }

        private boolean endPress() {

                return false;
        }

        private final Runnable confirmClickRunnable = new Runnable() {
            public void run() {
                if (endPress()) {
                    //onClickConfirmed(view, marker);
                }
            }
        };

        /**
         * This is called after a successful click
         */
        protected abstract void onClickConfirmed(View v, Marker marker);



}
