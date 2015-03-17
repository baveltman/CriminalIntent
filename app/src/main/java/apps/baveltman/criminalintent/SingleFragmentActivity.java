package apps.baveltman.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

/**
 * Abstract class to manage any UI that consists of a single fragment
 */
public abstract class SingleFragmentActivity extends FragmentActivity {

    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        //initialize and inflate activity_fragment UI
        FragmentManager fm = getSupportFragmentManager(); //using the support library for backwards compatibility pre API 11
        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    /**
     * @return the id of the layout to be inflated
     */
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }
}
