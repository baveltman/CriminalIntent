package apps.baveltman.criminalintent;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Model for a crime
 */
public class Crime {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;

    //JSON attributes for JSON serialized Crime object
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    public UUID getId(){
        return mId;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setTitle(String title){
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    @Override
    public String toString(){
        return mTitle;
    }

    /**
     * serializes this crime to a JSON object
     * @throws JSONException
     */
    public JSONObject toJSON() throws JSONException {
        JSONObject jsonCrime = new JSONObject();
        jsonCrime.put(JSON_ID, getId().toString());
        jsonCrime.put(JSON_TITLE, getTitle());
        jsonCrime.put(JSON_SOLVED, isSolved());
        jsonCrime.put(JSON_DATE, getDate());
        return jsonCrime;
    }
}
