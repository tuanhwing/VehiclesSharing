package project.com.vehiclessharing.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import project.com.vehiclessharing.R;
import project.com.vehiclessharing.fragment.HistoryOfGraber_Fragment;
import project.com.vehiclessharing.fragment.HistoryOfNeeder_Fragment;

/**
 * Created by Hihihehe on 6/6/2017.
 */

public class HistoryFragmentPagerAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 2;
    private String tabTitles[] = new String[]{"History of Graber", "History of Needer"};
    private Context mContext;

    public HistoryFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        if (position==0)
        {
            return HistoryOfGraber_Fragment.newInstance(position + 1);
        }
        return HistoryOfNeeder_Fragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        Drawable image=null;
        if (position == 0) {
            image = mContext.getResources().getDrawable(R.drawable.ic_directions_car_indigo_a700_24dp);
        } else if(position==1)
        {
            image = mContext.getResources().getDrawable(R.drawable.ic_accessibility_orange_a700_24dp);
        }

        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
        // Replace blank spaces with image icon
        SpannableString sb = new SpannableString("   " + tabTitles[position]);
        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return sb;
    }

}
