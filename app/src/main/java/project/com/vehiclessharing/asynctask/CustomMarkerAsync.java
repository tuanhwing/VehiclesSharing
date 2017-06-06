package project.com.vehiclessharing.asynctask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.InputStream;
import java.net.URL;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.activity.MainActivity;
import project.com.vehiclessharing.custom.CustomIconMarker;
import project.com.vehiclessharing.model.ForGraber;
import project.com.vehiclessharing.model.ForNeeder;
import project.com.vehiclessharing.model.RequestFromGraber;
import project.com.vehiclessharing.model.RequestFromNeeder;
import project.com.vehiclessharing.model.User;

/**
 * Created by Hihihehe on 6/5/2017.
 */

public class CustomMarkerAsync extends AsyncTask<User, Void, Bitmap> {

    private String hashKey;
    private Context mContext;
    private GoogleMap mGoogleMap;
    private User mUser;
    private Bitmap imageFromURL;
    private RequestFromNeeder needer=null;
    private RequestFromGraber graber=null;

    public CustomMarkerAsync(String key, Context mContext) {
        this.hashKey = key;
        this.mContext = mContext;
        this.mGoogleMap= MainActivity.mGoogleMap;
    }
    public RequestFromNeeder getNeeder() {
        return needer;
    }

    public void setNeeder(RequestFromNeeder needer) {
        this.needer = needer;
    }

    public RequestFromGraber getGraber() {
        return graber;
    }

    public void setGraber(RequestFromGraber graber) {
        this.graber = graber;
    }

    @Override
    protected void onPreExecute() {

    }

    /**
     * Download image from url
     * @param params
     * @return
     */
    @Override
    protected Bitmap doInBackground(User... params) {
        mUser=params[0];
        Bitmap bitmap= null;
        if (mUser.getImage() != null || !mUser.getImage().isEmpty()) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(mUser.getImage()).getContent());

            } catch (Exception e) {
                Log.e("Loi", e.toString());
            }
        }
        return bitmap;
    }

    /**
     *
     * @param bitmap
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
       //getCustomMarkerView: get Image downloaded and custom icon of marker with this image
        Bitmap bitmap1= getCustomMarkerView(bitmap);

        LatLng source=new LatLng(0,0);
        Marker customMarker=null;
        //object of user need add marker graber or needer
        if(graber!=null)
        {
            source= new LatLng(graber.getSourceLocation().getLatidude(), graber.getSourceLocation().getLongtitude());
            customMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title(mUser.getFullName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));
            customMarker.setTag(graber);
            ForNeeder.markerHashMap.put(hashKey, customMarker);
        }
        else if (needer!=null)
        {
            source= new LatLng(needer.getSourceLocation().getLatidude(), needer.getSourceLocation().getLongtitude());
            customMarker = mGoogleMap.addMarker(new MarkerOptions().position(source).title(mUser.getFullName())
                    .icon(BitmapDescriptorFactory.fromBitmap(bitmap1)));
            customMarker.setTag(needer);
            ForGraber.markerHashMap.put(hashKey, customMarker);
        }
    }

    /**
     * Get image from url and put into View
     *
     * @param
     * @return view have icon get from url or default icon
     */
    private Bitmap getCustomMarkerView(Bitmap bitmap) {
        View customMarkerView = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);

        if (bitmap==null) {
            markerImageView.setImageResource(R.drawable.temp);
        } else {
            try {
                markerImageView.setImageBitmap(bitmap);
            } catch (Exception e) {
                Log.e("Loi", e.toString());
            }
        }
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

}
