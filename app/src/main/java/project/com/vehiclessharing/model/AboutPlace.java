package project.com.vehiclessharing.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import project.com.vehiclessharing.activity.HomeActivity;

/**
 * Created by Hihihehe on 5/16/2017.
 */

public class AboutPlace {
    private static AboutPlace instance;
    private static Geocoder geocoder;
    private static List<Address> addresses;

    public static AboutPlace getInstance()
    {
        return instance=new AboutPlace();
    }
    public String getCurrentPlace(Context activity) {
        String fullAddress="";
        GoogleMap mMap= HomeActivity.mGoogleMap;
        Location myLocation  =mMap.getMyLocation() ;
        geocoder=new Geocoder(activity, Locale.getDefault());
        if(myLocation!=null)
        {
            double dLatitude = myLocation.getLatitude();
            double dLongitude = myLocation.getLongitude();
            try {
                addresses = geocoder.getFromLocation(dLatitude, dLongitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String area = addresses.get(0).getLocality();
                String city = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();

                fullAddress = address + ", " + area + ", " + city + ", " + country;

            }catch (IOException e)
            {
                Toast.makeText(activity, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Toast.makeText(activity, "Unable to fetch the current location", Toast.LENGTH_SHORT).show();
        }
        return fullAddress;
    }

    public LatLng getLatLngByName(Context activity,String location)
    {
        geocoder=new Geocoder(activity);
        try{
            addresses=geocoder.getFromLocationName(location,1);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        LatLng latLng=new LatLng(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
        return latLng;
    }
    public String getAddressByLatLng(Context activity,LatLng latLng) throws IOException {
        String fullAddress="";
        geocoder=new Geocoder(activity);
        try {
            addresses=geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            String[] listAddress=new String[3];
            listAddress[0] = addresses.get(0).getAddressLine(0);
            listAddress[1] = addresses.get(0).getLocality();
            listAddress[2] = addresses.get(0).getAdminArea();
           listAddress[3]= addresses.get(0).getCountryName();
        for (int i=0;i<listAddress.length;i++)
        {
            if(listAddress[i]!=null)
            {
                fullAddress+=listAddress;
                if(i!=listAddress.length)
                {
                    fullAddress+=", ";
                }
            }
        }
        }catch (IOException e)
        {
            e.printStackTrace();
        }
        return fullAddress;



       // String[] geo=address.split(",");
       /* String uri = "http://maps.google.com/maps/api/geocode/json?address=" +
                youraddress + "&sensor=false"
        HttpGet httpGet = new HttpGet(uri);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());

            lng = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lng");

            lat = ((JSONArray)jsonObject.get("results")).getJSONObject(0)
                    .getJSONObject("geometry").getJSONObject("location")
                    .getDouble("lat");

            Log.d("latitude", lat);
            Log.d("longitude", lng);
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }
}
