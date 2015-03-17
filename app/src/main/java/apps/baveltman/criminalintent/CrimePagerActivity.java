package apps.baveltman.criminalintent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A ViewPager activity for individual crimes in the ListFragment
 */
public class CrimePagerActivity extends FragmentActivity
        implements CrimeFragment.Callbacks{
    private ViewPager mViewPager;
    private ArrayList<Crime> mCrimes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //since this is a simple view, I'm setting this up without an explicit layout resource for it
        //using ids.xml
        mViewPager = new ViewPager(this);
        mViewPager.setId(R.id.viewPager);

        mCrimes = CrimeLab.get(this).getCrimes();

        FragmentManager fm = getSupportFragmentManager();

        bindViewPagerAdapter(fm);
        displayCorrectItemInViewPager();
        bindEventListeners();
        setContentView(mViewPager);
    }

    private void bindEventListeners() {
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            public void onPageScrollStateChanged(int state) { }

            public void onPageScrolled(int pos, float posOffset, int posOffsetPixels) { }

            public void onPageSelected(int pos) {
                Crime crime = mCrimes.get(pos);
                if (crime.getTitle() != null) {
                    setTitle(crime.getTitle());
                }
            }

        });
    }

    /**
     * looks at intent and updates ViewPager to display designated item
     */
    private void displayCorrectItemInViewPager() {
        UUID crimeId = (UUID)getIntent()
                .getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID);

        for (int i = 0; i < mCrimes.size(); i++) {
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
    }

    /**
     * sets up the adapter required for the ViewPager to work
     * @param fm
     */
    private void bindViewPagerAdapter(FragmentManager fm) {
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
            @Override
            public int getCount() {
                return mCrimes.size();
            }

            @Override
            public Fragment getItem(int pos) {
                Crime crime = mCrimes.get(pos);
                return CrimeFragment.newInstance(crime.getId());
            }
        });
    }

    /**
     * required callback for hosted CrimeFragment
     * nothing to do here
     * @param crime
     */
    @Override
    public void onCrimeUpdated(Crime crime) {

    }
}
