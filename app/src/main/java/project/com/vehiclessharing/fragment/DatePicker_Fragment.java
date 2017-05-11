package project.com.vehiclessharing.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

/**
 * Created by Tuan on 01/05/2017.
 */

public class DatePicker_Fragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Do something with the date chosen by the user
//        String userId = HomeActivity.mUser.getUid();
//        ProfileActivity.mDatabase.child("users").child(userId).child("birthDay").child("day").setValue(dayOfMonth);
//        ProfileActivity.mDatabase.child("users").child(userId).child("birthDay").child("month").setValue(month);
//        ProfileActivity.mDatabase.child("users").child(userId).child("birthDay").child("year").setValue(year);
//        db.updateDay(userId,dayOfMonth);
//        db.updateMonth(userId,month);
//        db.updateYear(userId,year);
//        Toast.makeText(getActivity(), "Update birthday success!", Toast.LENGTH_SHORT).show();
//        if(!HomeActivity.currentUser.getBirthDay().compareBirhtday(new BirthDay(dayOfMonth,month,year)) &&
//                Validation.checkValidBirthDay(new BirthDay(dayOfMonth,month,year)).getIsValid()){
//            ProfileActivity.isBirthDayChanged = true;
//            ProfileActivity.day = dayOfMonth;
//            ProfileActivity.month = month;
//            ProfileActivity.year = year;
//            ProfileActivity.hideOrShowSave();
//            ProfileActivity.txtBirthday.setText(dayOfMonth + "/" + month + "/" + year);
//        }
    }
}
