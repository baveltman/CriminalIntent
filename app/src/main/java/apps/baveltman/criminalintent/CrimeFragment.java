package apps.baveltman.criminalintent;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;


/**
 * Controller for fragment_crime
 * displays details of a crime
 * allows to edit details of a current crime
 */
public class CrimeFragment extends Fragment {

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCrime = new Crime();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        bindFragmentUiElements(v);
        bindListenersAndEvents(v);
        return v;

    }

    private void bindFragmentUiElements(View v) {
        mTitleField = (EditText)v.findViewById(R.id.crime_title);

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setEnabled(false);
    }

    private void bindListenersAndEvents(View v) {

        mTitleField.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                    // This space intentionally left blank
            }

            public void afterTextChanged(Editable c) {
                    // This one too
            }
        });

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
    }
}
