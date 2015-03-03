package apps.baveltman.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import java.sql.Time;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Dialog to set crime date
 */
public class DatePickerFragment extends DialogFragment {

    private Date mDate;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;

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
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    /**
     * looks at passed fragment arguments to set the date on the dialog
     */
    private void setCalendarDateFromArguments(View v) {
        // Create a Calendar to get the year, month, and day for DatePicker
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mDate);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        // Also get values for TimePicker
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int sec = calendar.get(Calendar.SECOND);
        int milliSec = calendar.get(Calendar.MILLISECOND);

        //Initialize DatePicker
        mDatePicker = (DatePicker)v.findViewById(R.id.dialog_date_datePicker);
        //Initialize TImePicker
        mTimePicker = (TimePicker)v.findViewById(R.id.dialog_date_timePicker);
        mTimePicker.setCurrentHour(hour);
        mTimePicker.setCurrentMinute(min);


        //bind event listeners for dialog date and time pickers
        mDatePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                mDate = new GregorianCalendar(year, month, day, mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute()).getTime();
                getArguments().putSerializable(EXTRA_DATE, mDate);
            }
        });


        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mDate = new GregorianCalendar(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth(), hourOfDay, minute).getTime();
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
