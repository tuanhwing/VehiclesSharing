package project.com.vehiclessharing.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseError;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.model.HistoryOfGraber;
import project.com.vehiclessharing.model.User;
import project.com.vehiclessharing.model.UserInfomation;
import project.com.vehiclessharing.utils.UserCallback;

/**
 * Created by Hihihehe on 6/12/2017.
 */

public class HistoryItemAdapter extends ArrayAdapter<HistoryOfGraber> {

    private Activity mContext;
    private int mResource;
    private List<HistoryOfGraber> listNeeder;

    private ImageView imgAvatar;
    private TextView txtFullName,txtSourceLocation, txtDesLocation, txtDayOfTrip, txtComment;
    private RatingBar ratingBar;
    private Button btnSeeRating;



    public HistoryItemAdapter(Activity context, int resource,List<HistoryOfGraber> listNeeder) {
        super(context,resource,listNeeder);
        this.mContext=context;
        this.mResource=resource;
        this.listNeeder=listNeeder;
    }

    @NonNull
    @Override
    public View getView(int position,View convertView,ViewGroup parent) {
        Log.e("getView","In getView");
        //return super.getView(position, convertView, parent);
        LayoutInflater inflater=this.mContext.getLayoutInflater();
//        View row=inflater.inflate(this.resource,null);
        // LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(this.mResource, null);
        final HistoryOfGraber trip = this.listNeeder.get(position);
        Log.e("trip",trip.getSourceLocation());
        // View row = super.getView(position, convertView, parent);
        txtFullName = (TextView) row.findViewById(R.id.txtHistoryFullName);
        imgAvatar = (ImageView) row.findViewById(R.id.imgHistoryImage);
        txtSourceLocation = (TextView) row.findViewById(R.id.txtHistoryLocationStart);
        txtDesLocation = (TextView) row.findViewById(R.id.txtHistoryLocationEnd);
        txtDayOfTrip = (TextView) row.findViewById(R.id.txtHistoryDayOfTrip);
        ratingBar = (RatingBar) row.findViewById(R.id.rbHistory);
        btnSeeRating= (Button) row.findViewById(R.id.btnSeeRating);
        txtComment= (TextView) row.findViewById(R.id.txtHistoryComment);

        txtFullName.setText("hihihehe");
        //final String fullName, sourceLocation, desLocation, day;
        //int rating;
        if (listNeeder != null) {
            final UserCallback userCallback = new UserCallback() {
                @Override
                public void onSuccess(User user) {
                    Log.e("callback in adapter", String.valueOf(user.getImage()));
                    if (user.getImage() == null || user.getImage().isEmpty()) {
                        imgAvatar.setImageResource(R.drawable.temp);
                    } else {
                        Picasso.with(mContext)
                                .load(user.getImage())
                                .into(imgAvatar, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        //progressLoadImageFull.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onError() {
                                        Log.d("download_full_image", "failedS");
                                    }
                                });
                    }
                    txtFullName.setText(user.getFullName());
                    txtSourceLocation.setText(trip.getSourceLocation());
                    txtDesLocation.setText(trip.getDestinationLocation());
                    txtDayOfTrip.setText(trip.getDay());
                   // txtComment.setText(trip.getComment());
                    ratingBar.setRating(trip.getRating());

                }

                @Override
                public void onError(DatabaseError e) {

                }
            };
            UserInfomation userInfomation = new UserInfomation();
            userInfomation.getInfoUserById(trip.getUserId(), userCallback);
        }
            addEvents(trip);
            return row;
        }

    private void addEvents(final HistoryOfGraber trip) {
        btnSeeRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*if(layoutRating.getVisibility()==View.GONE)
                {
                    Toast.makeText(mContext, "see rating", Toast.LENGTH_SHORT).show();
                    layoutRating.setVisibility(View.VISIBLE);
                    txtComment.setText(trip.getComment());
                    ratingBar.setRating(trip.getRating());
                    btnSeeRating.setText("Hide rating");
                }
                else
                {
                    Toast.makeText(mContext, "hide rating", Toast.LENGTH_SHORT).show();
                    layoutRating.setVisibility(View.GONE);
                    btnSeeRating.setText("See rating");
                }*/
                if (txtComment.getVisibility()==View.GONE) {
                    txtComment.setVisibility(View.VISIBLE);
                }else txtComment.setVisibility(View.GONE);
            }
        });
    }
}
