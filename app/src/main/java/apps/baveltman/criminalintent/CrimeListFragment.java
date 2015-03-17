package apps.baveltman.criminalintent;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
    private Callbacks mCallbacks;

    /**
     * Required interface for hosting activities.
     */
    public interface Callbacks {
        void onCrimeSelected(Crime crime);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


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

        registerViewForContextMenu(v);

        return v;
    }

    /**
     * register the List view for this list fragment to the context menu
     * @param v
     */
    private void registerViewForContextMenu(View v) {
        ListView listView = (ListView)v.findViewById(android.R.id.list);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            // Use floating context menus on Froyo and Gingerbread
            registerForContextMenu(listView);
        } else {
            // Use contextual action bar on Honeycomb and higher
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            bindChoiceModeContextMenuEventListeners(listView);
        }
    }

    @TargetApi(11)
    private void bindChoiceModeContextMenuEventListeners(ListView listView) {
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener(){

            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                // Required, but not used in this implementation
            }

            // ActionMode.Callback methods
            public boolean onCreateActionMode(ActionMode mode, Menu menu){
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.crime_list_item_context, menu);
                return true;
            }

            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Required, but not used in this implementation
            }

            public boolean onActionItemClicked(ActionMode mode, MenuItem item){
                switch (item.getItemId()) {
                    case R.id.menu_item_delete_crime:
                        CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
                        CrimeLab crimeLab = CrimeLab.get(getActivity());

                        for (int i = adapter.getCount() - 1; i >= 0; i--){
                            if (getListView().isItemChecked(i)) {
                                crimeLab.deleteCrime(adapter.getItem(i));
                            }
                        }

                        mode.finish();
                        adapter.notifyDataSetChanged();
                        return true;

                    default:
                        return false;
                }
            }

            public void onDestroyActionMode(ActionMode mode) {
                // Required, but not used in this implementation
            }

        });
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
        ((CrimeAdapter)getListAdapter()).notifyDataSetChanged();
        mCallbacks.onCrimeSelected(crime);
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

    /**
     * method to inflate the context menu for crime deletion
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.crime_list_item_context, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //distinguish which item in the list has been selected
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        CrimeAdapter adapter = (CrimeAdapter)getListAdapter();
        Crime crime = adapter.getItem(position);

        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                //delete action
                CrimeLab.get(getActivity()).deleteCrime(crime);
                adapter.notifyDataSetChanged();
                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Crime c = ((CrimeAdapter)getListAdapter()).getItem(position);

        // Start CrimeActivity
        mCallbacks.onCrimeSelected(c);
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
