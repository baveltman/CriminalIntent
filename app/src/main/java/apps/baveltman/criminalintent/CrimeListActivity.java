package apps.baveltman.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * root activity that manages the fragments that
 * show the a list of all crimes for the user
 */
public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks{
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    /**
     * required callback for one of the hosted fragments (CrimeListFragment)
     * @param crime
     */
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detailFragmentContainer) == null) {
            // Start an instance of CrimePagerActivity
            Intent i = new Intent(this, CrimePagerActivity.class);
            i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId());
            startActivity(i);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            Fragment oldDetail = fm.findFragmentById(R.id.detailFragmentContainer);
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());

            if (oldDetail != null) {
                ft.remove(oldDetail);
            }

            ft.add(R.id.detailFragmentContainer, newDetail);
            ft.commit();
        }
    }

    /**
     * required callback for one of the hosted fragments (CrimeListFragment)
     * only important in the twopane layout
     * @param crime
     */
    public void onCrimeUpdated(Crime crime) {
        FragmentManager fm = getSupportFragmentManager();
        CrimeListFragment listFragment = (CrimeListFragment)
                fm.findFragmentById(R.id.fragmentContainer);
        listFragment.updateUI();
    }
}
