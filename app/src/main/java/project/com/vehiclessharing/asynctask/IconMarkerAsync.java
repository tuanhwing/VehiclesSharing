package project.com.vehiclessharing.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

import project.com.vehiclessharing.R;

/**
 * Created by Hihihehe on 6/3/2017.
 */

public class IconMarkerAsync extends AsyncTask<String, Void, Bitmap> {
    private Context mContext;

    public IconMarkerAsync(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * custom icon with image from url
     *
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(String... params) {

        View customMarkerView = getCustomMarkerView(params[0]);
        //final View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
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

    /**
     * Get image from url and put into View
     *
     * @param urlImage
     * @return view have icon get from url or default icon
     */
    private View getCustomMarkerView(String urlImage) {
        Bitmap bitmap = null;
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
        if (urlImage == null || urlImage.isEmpty()) {
            markerImageView.setImageResource(R.drawable.temp);
        } else {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(urlImage).getContent());
                markerImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("Loi", e.toString());
            }
        }
        return customMarkerView;
    }

    /**
     * Check have internet?
     *
     * @param context
     * @return true if access internet
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
