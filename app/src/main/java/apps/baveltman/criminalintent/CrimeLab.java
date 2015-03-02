package apps.baveltman.criminalintent;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * models a list of crimes
 * this class is a singleton
 * the singleton exists as long as the application stays in memory
 */
public class CrimeLab {
    private static CrimeLab sCrimeLab;
    private Context mAppContext;
    private ArrayList<Crime> mCrimes;

    private CrimeLab(Context appContext){
        mAppContext = appContext;
        mCrimes = new ArrayList<Crime>();
        createRandomCrimes();
    }

    private void createRandomCrimes() {
        for (int i = 0; i < 100; ++i) {
            Crime c = new Crime();
            c.setTitle("Crime #" + i);
            c.setSolved(i % 2 == 0); // Every other one
            mCrimes.add(c);
        }
    }

    public static CrimeLab get(Context c) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(c.getApplicationContext());
        }
        return sCrimeLab;
    }

    public ArrayList<Crime> getCrimes(){
        return mCrimes;
    }

    /**
     * searches arrayList of crimes for crime with specified UUID
     * @param id
     * @return Crime if Crime with specified UUID is found. Null otherwise
     */
    public Crime getCrime(UUID id){
        for(Crime crime : mCrimes){
            if(crime.getId().equals(id)){
                return crime;
            }
        }
        return null;
    }

}
