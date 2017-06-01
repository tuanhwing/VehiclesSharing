package project.com.vehiclessharing.custom;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.asynctask.ImageTask;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by Hihihehe on 5/27/2017
 * Make marker have avatar on marker
 */

public class CustomMarker {
    private Context mContext;

     private static CustomMarker instance;
    public static CustomMarker getInstance(Context context)
    {
        return instance=new CustomMarker(context);
    }

    public CustomMarker(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * Use canvas to custom marker have marker icon and avatar above
     * @param
     * @return
     */
    public Bitmap customMarkerWithAvatar(String url)
    {
        View customMarkerView=getCustomMarkerView(url);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
    private View getCustomMarkerView(final String urlAvatar) {
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        //get avatar of user and put it in imageview of custom_marker
        if (urlAvatar==null || urlAvatar.isEmpty()) {
            markerImageView.setImageResource(R.drawable.temp);
            //progressBar.setVisibility(View.GONE);
        } else {
           // if (isOnline()) {
                //progressBar.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(urlAvatar).into(markerImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        //progressBar.setVisibility(View.GONE);
                        Toast.makeText(mContext, "success"+ urlAvatar, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError() {
                        //progressBar.setVisibility(View.GONE);
                        //Toast.makeText(ProfileActivity.this,"Error load image",Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, "error"+urlAvatar, Toast.LENGTH_SHORT).show();
                    }
                });
           /* } else Picasso.with(getApplicationContext())
                    .load(urlAvatar)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(markerImageView);*/
        }
        ImageTask imageTask=new ImageTask();
        return customMarkerView;
    }
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
