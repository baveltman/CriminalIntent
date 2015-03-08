package apps.baveltman.criminalintent;

import android.content.Context;
import android.util.Log;

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
    private CriminalIntentJsonSerializer mJsonSerializer;

    private static final String TAG = "CrimeLab";
    private static final String FILENAME = "crimes.json";

    private CrimeLab(Context appContext){
        mAppContext = appContext;
        mJsonSerializer = new CriminalIntentJsonSerializer(mAppContext, FILENAME);
        mCrimes = tryLoadCrimesFromFile();
    }

    private ArrayList<Crime> tryLoadCrimesFromFile() {
        try{
            ArrayList<Crime> crimes = mJsonSerializer.loadCrimes();
            return crimes;
        } catch (Exception e){
            Log.d(TAG, "bad shit happened and we couldn't load the crime list from the JSON file")
            return new ArrayList<Crime>();
        }
    }

    public boolean saveCrimes(){
        try{
            mJsonSerializer.saveCrimes(mCrimes);
            Log.d(TAG, "updated crimes saved to file");
            return true;
        } catch (Exception e){
            Log.d(e.toString(), e.getMessage());
            return false;
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

    /**
     * adds a new crime to the modle
     * @param c
     */
    public void addCrime(Crime c){
        if (mCrimes == null){
            mCrimes = new ArrayList<Crime>();
        }
        mCrimes.add(c);
    }

}
