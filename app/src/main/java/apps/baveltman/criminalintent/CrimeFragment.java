package apps.baveltman.criminalintent;


import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;


/**
 * Controller for fragment_crime
 * displays details of a crime
 * allows to edit details of a current crime
 */
public class CrimeFragment extends Fragment {

    public static String EXTRA_CRIME_ID = "baveltman.apps.criminalintent.EXTRA_CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_DATE = 0;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //look to see if arguments were passed in a bundle and act accordingly
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);

        if (crimeId != null){
            //passed a specific crime so let's get the crime in question
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        } else {
            //no crime Id passed, so let's
            mCrime = new Crime();
        }

        setHasOptionsMenu(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
            case android.R.id.home:
                    if (NavUtils.getParentActivityName(getActivity()) != null) {
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Since we're dealing with a fragment, we're actually using the onCreateView method to inflate the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        bindFragmentUiElements(v);
        bindListenersAndEvents();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        return v;

    }

    /**
     * Save crimes to JSON file on application pause
     */
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    /**
     * Interperts child fragment result and acts according to intent
     * @param requestCode unique identifier to ascertain which child fragment has returned
     * @param resultCode status returned by child fragement
     * @param i intent with extras returned by child fragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        if (resultCode != Activity.RESULT_OK) {return;}
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)i.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mDateButton.setText(mCrime.getDate().toString());
        }
}

    /**
     * this method is used to pass a crimeId to this fragement as an argument
     * so that the fragment can be initialized by any activity
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void bindFragmentUiElements(View v) {
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void bindListenersAndEvents() {

        mDateButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mTitleField.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // nothing yet
            }

            public void afterTextChanged(Editable c) {
                // nothing yet
            }
        });

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
    }
}
