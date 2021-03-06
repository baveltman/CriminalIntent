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
    private Photo mPhoto;
    private String mSuspect;
    private String mSuspectPhone;

    //JSON attributes for JSON serialized Crime object
    private static final String JSON_ID = "id";
    private static final String JSON_TITLE = "title";
    private static final String JSON_SOLVED = "solved";
    private static final String JSON_DATE = "date";
    private static final String JSON_PHOTO = "photo";
    private static final String JSON_SUSPECT = "suspect";
    private static final String JSON_SUSPECT_PHONE = "tel";

    public Crime() {
        mId = UUID.randomUUID();
        mDate = new Date();
    }

    /**
     * deserializes a JSON object to a Crime object
     * @param json
     * @throws JSONException
     */
    public Crime(JSONObject json) throws JSONException {
        mId = UUID.fromString(json.getString(JSON_ID));
        mTitle = json.getString(JSON_TITLE);
        mSolved = json.getBoolean(JSON_SOLVED);
        mDate = new Date(json.getLong(JSON_DATE));

        if (json.has(JSON_PHOTO)) {
            mPhoto = new Photo(json.getJSONObject(JSON_PHOTO));
        }

        if (json.has(JSON_SUSPECT)){
            mSuspect = json.getString(JSON_SUSPECT);
        }

        if (json.has(JSON_SUSPECT_PHONE)){
            mSuspectPhone = json.getString(JSON_SUSPECT_PHONE);
        }
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

    public Photo getPhoto() {
        return mPhoto;
    }

    public void setPhoto(Photo p) {
        mPhoto = p;
    }

    public String getSuspect() {
        return mSuspect;
    }
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhoneNumber() {
        return mSuspectPhone;
    }
    public void setPhoneNumber(int num) {
        mSuspectPhone = String.valueOf(num);
    }

    public void setPhoneNumber(String num) {
        mSuspectPhone = num;
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
        jsonCrime.put(JSON_DATE, getDate().getTime());

        if (mPhoto != null) {
            jsonCrime.put(JSON_PHOTO, mPhoto.toJSON());
        }

        jsonCrime.put(JSON_SUSPECT, mSuspect);
        jsonCrime.put(JSON_SUSPECT_PHONE, mSuspectPhone);

        return jsonCrime;
    }
}
