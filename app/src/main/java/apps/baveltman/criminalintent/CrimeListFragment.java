package apps.baveltman.criminalintent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * a fragment that is a list UI of crimes
 */
public class CrimeListFragment extends ListFragment {



    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceStateBundle){
        super.onCreate(savedInstanceStateBundle);
        getActivity().setTitle(R.string.crimes_title);
        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        //set the adapter for the list
        ArrayAdapter<Crime> adapter = new CrimeAdapter(mCrimes);

        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);

        // Start CrimeActivity
        Intent i = new Intent(getActivity(), CriminalActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
    }

    /**
     * Nested private class that encapsulates a custom adapted
     * used to display Crime specific information in the CrimeListFragment
     */
    private class CrimeAdapter extends ArrayAdapter<Crime> {

        public CrimeAdapter(ArrayList<Crime> crimes) {
            super(getActivity(), 0, crimes);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // If we weren't given a view, inflate one
            if(convertView == null) {
                convertView = getActivity().getLayoutInflater()
                    .inflate(R.layout.list_item_crime, null);
            }

            // Configure the view for this Crime
            Crime c = getItem(position);
            TextView titleTextView =
                (TextView)convertView.findViewById(R.id.crime_list_item_titleTextView);
            titleTextView.setText(c.toString());

            TextView dateTextView =
                (TextView)convertView.findViewById(R.id.crime_list_item_dateTextView);
            dateTextView.setText(c.getDate().toString());

            CheckBox solvedCheckBox =
                (CheckBox)convertView.findViewById(R.id.crime_list_item_solvedListCheckBox);
            solvedCheckBox.setChecked(c.isSolved());

            return convertView;
        }
    }
}
