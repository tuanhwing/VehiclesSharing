package project.com.vehiclessharing.asynctask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by Hihihehe on 5/26/2017.
 */

public class ImageTask extends AsyncTask<String,Void,Bitmap> {

    public ImageTask() {
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

    }

    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap bitmap=null;
        try{
        String link=params[0];
            bitmap = BitmapFactory.decodeStream((InputStream) new URL(link).getContent());
        }
        catch (Exception e)
        {
            Log.e("Loi",e.toString());
        }
        return bitmap;
    }

}
