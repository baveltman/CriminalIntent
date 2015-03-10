package apps.baveltman.criminalintent;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.Date;
import java.util.UUID;


/**
 * Controller for fragment_crime
 * displays details of a crime
 * allows to edit details of a current crime
 */
public class CrimeFragment extends Fragment {

    public static String EXTRA_CRIME_ID = "baveltman.apps.criminalintent.EXTRA_CRIME_ID";
    private static final String DIALOG_DATE = "date";
    private static final String TAG = "CrimeFragment";
    private static final String DIALOG_IMAGE = "image";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_PHOTO = 1;
    private static final int REQUEST_CONTACT = 2;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;
    private ImageButton mCameraButton;
    private ImageView mPhotoImageView;
    private Button mSendReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //look to see if arguments were passed in a bundle and act accordingly
        UUID crimeId = (UUID)getArguments().getSerializable(EXTRA_CRIME_ID);

        if (crimeId != null){
            //passed a specific crime so let's get the crime in question
            mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        } else {
            //no crime Id passed, so let's
            mCrime = new Crime();
        }

        //activity will have an ActionBar
        setHasOptionsMenu(true);

        //retain instance vars on configuration change
        setRetainInstance(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {

            case android.R.id.home:
                returnToPreviousActivity();
                return true;

           case R.id.menu_item_delete_crime:
               //delete action
               CrimeLab.get(getActivity()).deleteCrime(mCrime);
               returnToPreviousActivity();
               return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void returnToPreviousActivity() {
        if (NavUtils.getParentActivityName(getActivity()) != null) {
            NavUtils.navigateUpFromSameTask(getActivity());
        }
    }

    /**
     * Since we're dealing with a fragment, we're actually using the onCreateView method to inflate the view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, parent, false);
        bindFragmentUiElements(v);
        bindListenersAndEvents();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(getActivity()) != null) {
                getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        return v;

    }

    /**
     * Once view is ready, show photo in imageView if photo is available
     */
    @Override
    public void onStart() {
        super.onStart();
        showPhoto();
    }

    /**
     * Save crimes to JSON file on application pause
     */
    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).saveCrimes();
    }

    /**
     * release bitmap memory resources on stop of this fragment
     */
    @Override
    public void onStop() {
        super.onStop();
        PictureUtils.cleanImageView(mPhotoImageView);
    }

    /**
     * Interperts child fragment result and acts according to intent
     * @param requestCode unique identifier to ascertain which child fragment has returned
     * @param resultCode status returned by child fragement
     * @param i intent with extras returned by child fragment
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent i){
        if (resultCode != Activity.RESULT_OK) {return;}

        //handle result from date dialog
        if (requestCode == REQUEST_DATE) {
            Date date = (Date)i.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            mDateButton.setText(mCrime.getDate().toString());
        }

        //handle result from cameraFragment
        else if (requestCode == REQUEST_PHOTO) {
            // Create a new Photo object and attach it to the crime
            String filename = i.getStringExtra(CrimeCameraFragment.EXTRA_PHOTO_FILENAME);
            if (filename != null) {
                Photo p = new Photo(filename);
                mCrime.setPhoto(p);
                showPhoto();
            }
        }


        else if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = i.getData();

            // Specify which fields you want your query to return values for
            //specify the query field to return
            String[] queryField={ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts._ID};

            Cursor cursor = getActivity().getContentResolver().query(contactUri, queryField, null, null, null);

            String suspectName=null;
            String suspectPhoneNumber=null;
            int contactId;

            if(cursor.getCount()>0){
                cursor.moveToFirst();
                suspectName=cursor.getString(0);
            }else{
                if(cursor !=null)
                    cursor.close();
                return;
            }

            //the second number is the id
            contactId=cursor.getInt(1);

            Cursor cursor2 = getActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{String.valueOf(contactId)}, null);

            if (cursor2.moveToNext())
            {
                mCrime.setPhoneNumber(cursor2.getString(0));
            }

            if(cursor2 !=null)
                cursor2.close();

            mCrime.setSuspect(suspectName);
            mSuspectButton.setText(suspectName);
            mCallSuspectButton.setText(getString(R.string.crime_call_name, suspectName));

            if(cursor !=null)
                cursor.close();

            return;
        }
    }

    /**
     * this method is used to pass a crimeId to this fragement as an argument
     * so that the fragment can be initialized by any activity
     */
    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void bindFragmentUiElements(View v) {
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());

        mCameraButton = (ImageButton)v.findViewById(R.id.crime_imageButton);
        // If camera is not available, disable camera functionality
        PackageManager pm = getActivity().getPackageManager();
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            mCameraButton.setEnabled(false);
        }

        mPhotoImageView = (ImageView)v.findViewById(R.id.crime_imageView);
        mSendReportButton = (Button)v.findViewById(R.id.crime_send_report_button);

        mSuspectButton = (Button)v.findViewById(R.id.crime_choose_suspect_button);
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButton = (Button)v.findViewById(R.id.crime_call_suspect);
        if (mCrime.getSuspect() != null) {
            mCallSuspectButton.setText(getString(R.string.crime_call_name, mCrime.getSuspect()));
        }
    }

    private void bindListenersAndEvents() {

        mCameraButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CrimeCameraActivity.class);
                startActivityForResult(i, REQUEST_PHOTO);
            }
        });

        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(fm, DIALOG_DATE);
            }
        });

        mCallSuspectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:" + mCrime.getPhoneNumber());
                Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
                startActivity(callIntent);
            }
        });

        mSendReportButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        mSuspectButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        mPhotoImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Photo p = mCrime.getPhoto();

                if (p == null)
                    return;

                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                String path = getActivity()
                        .getFileStreamPath(p.getFilename()).getAbsolutePath();
                ImageFragment.newInstance(path)
                        .show(fm, DIALOG_IMAGE);
            }
        });

        mTitleField.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence c, int start, int before, int count) {
                mCrime.setTitle(c.toString());
            }

            public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                // nothing yet
            }

            public void afterTextChanged(Editable c) {
                // nothing yet
            }
        });

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });
    }

    private void showPhoto() {

        // (Re)set the image button's image based on our photo
        Photo p = mCrime.getPhoto();
        BitmapDrawable b = null;

        if (p != null) {
            String path = getActivity()
                    .getFileStreamPath(p.getFilename()).getAbsolutePath();
            b = PictureUtils.getScaledDrawable(getActivity(), path);
        }

        mPhotoImageView.setImageDrawable(b);

    }

    /**
     * returns a string representing a crime report
     * @return
     */
    private String getCrimeReport() {
        String solvedString = null;

        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mCrime.getDate()).toString();
        String suspect = mCrime.getSuspect();

        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
