package apps.baveltman.criminalintent;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.widget.ArrayAdapter;

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
        ArrayAdapter<Crime> adapter =
                new ArrayAdapter<Crime>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        mCrimes);

        setListAdapter(adapter);
    }
}
