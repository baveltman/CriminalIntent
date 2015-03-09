package apps.baveltman.criminalintent;

import android.support.v4.app.Fragment;

/**
 * Activity class to instantiate CrimeCameraFragment
 */
public class CrimeCameraActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeCameraFragment();
    }
}
