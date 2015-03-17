package apps.baveltman.criminalintent;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * root activity that manages the fragments that
 * show the a list of all crimes for the user
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    public void onCrimeSelected(Crime crime) {
    }
}
