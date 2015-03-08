package apps.baveltman.criminalintent;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * This class serializes Crime objects into a JSON format
 */
public class CriminalIntentJsonSerializer {
    private Context mContext;
    private String mFilename;

    public CriminalIntentJsonSerializer(Context c, String f) {
        mContext = c;
        mFilename = f;
    }

    /**
     * Saves arraylist of crimes to a JSON file
     * @param crimes
     * @throws JSONException
     * @throws IOException
     */
    public void saveCrimes(ArrayList<Crime> crimes) throws JSONException, IOException {

        // Build an array in JSON
        JSONArray array = new JSONArray();
        for (Crime c : crimes)
            array.put(c.toJSON());

        // Write the file to disk
        Writer writer = null;
        try {

            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());

        } finally {

            if (writer != null)
                writer.close();

        }
    }

    /**
     * Loads JSON file of crimes and creates an ArrayList<Crime> from it
     * @throws IOException
     * @throws JSONException
     */
    public ArrayList<Crime> loadCrimes() throws IOException, JSONException {
        ArrayList<Crime> crimes = new ArrayList<Crime>();
        BufferedReader reader = null;
        try {
                // Open and read the file into a StringBuilder
                InputStream in = mContext.openFileInput(mFilename);
                reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder jsonString = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    // Line breaks are omitted and irrelevant
                    jsonString.append(line);
                }

                // Parse the JSON using JSONTokener
                JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
                // Build the array of crimes from JSONObjects
                for (int i = 0; i < array.length(); i++) {
                    crimes.add(new Crime(array.getJSONObject(i)));
                }

       } catch (FileNotFoundException e) {
            // Ignore this one; it happens when starting fresh
        } finally {
            if (reader != null)
                reader.close();
        }
        return crimes;
    }
}

