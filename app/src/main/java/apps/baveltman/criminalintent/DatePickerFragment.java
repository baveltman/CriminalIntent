package apps.baveltman.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Dialog to set crime date
 */
public class DatePickerFragment extends DialogFragment {

    private Date mDate;

    public static final String EXTRA_DATE =
            "baveltman.apps.date";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mDate = (Date)getArguments().getSerializable(EXTRA_DATE);

        View v = getActivity().getLayoutInflater()
                .inflate(R.layout.dialog_date, null);

        setCalendarDateFromArguments(v);

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, null)
                .create();
    }

    /**
     * looks at passed fragment arguments to set the date on the dialog
     */
    private void setCalendarDateFromArguments(View v) {
        // Create a Calendar to get the year, month, and day
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker datePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                mDate = new GregorianCalendar(year, month, day).getTime();
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });
    }

    /**
     * used to send result to the calling fragment
     * notifies it of a date change
     * @param resultCode
     */
    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {return;}
        Intent i = new Intent();
        i.putExtra(EXTRA_DATE, mDate);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, i);
    }


    /**
     * Used to create a new instance of this fragment and pass it an argument
     * @param date is the date passed to the new instance of this fragment
     */
    public static DatePickerFragment newInstance(Date date) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
