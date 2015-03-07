package apps.baveltman.criminalintent;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * a fragment that is a list UI of crimes
 */
public class CrimeListFragment extends ListFragment {


    private boolean mSubtitleVisible;
    private ArrayList<Crime> mCrimes;
    private Button mNewCrimeButton;

    @Override
    public void onCreate(Bundle savedInstanceStateBundle){
        super.onCreate(savedInstanceStateBundle);

        //activity will have an action bar
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.crimes_title);

        //get the dataset for crimes
        mCrimes = CrimeLab.get(getActivity()).getCrimes();

        //set the adapter for the list
        ArrayAdapter<Crime> adapter = new CrimeAdapter(mCrimes);
        setListAdapter(adapter);

        //retain instance vars on configuration change
        setRetainInstance(true);
        mSubtitleVisible = false;


    }

    @TargetApi(11)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.list_item_no_crime, parent, false);
        ListView view = (ListView)v.findViewById(android.R.id.list);
        view.setEmptyView(v.findViewById(android.R.id.empty));

        bindEmptyViewUiElements(v);
        bindNewCrimeButtonEvents();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && mSubtitleVisible){
            getActivity().getActionBar().setSubtitle(R.string.subtitle);
        }

        return v;
    }

    private void bindEmptyViewUiElements(View v) {
        mNewCrimeButton = (Button)v.findViewById(R.id.new_crime_button);
    }

    private void bindNewCrimeButtonEvents() {
        mNewCrimeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startNewCrimeActivity();
            }
        });
    }

    private void startNewCrimeActivity() {
        Crime crime = new Crime();
        CrimeLab.get(getActivity()).addCrime(crime);
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
        startActivityForResult(i, 0);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subtitleMenuItem = menu.findItem(R.id.menu_item_show_subtitle);
        if(mSubtitleVisible && subtitleMenuItem != null){
            subtitleMenuItem.setTitle(R.string.hide_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_item_new_crime:
                startNewCrimeActivity();
                return true;

            case R.id.menu_item_show_subtitle:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    if (getActivity().getActionBar().getSubtitle() == null){
                        getActivity().getActionBar().setSubtitle(R.string.subtitle);
                        item.setTitle(R.string.hide_subtitle);
                        mSubtitleVisible = true;
                    } else {
                        getActivity().getActionBar().setSubtitle(null);
                        item.setTitle(R.string.show_subtitle);
                        mSubtitleVisible = false;
                    }
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);

        // Start CrimeActivity
        Intent i = new Intent(getActivity(), CrimePagerActivity.class);
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, c.getId());
        startActivity(i);
    }

    /**
     * update list data when activity and fragment are resumed
     */
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
