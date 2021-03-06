package edu.kvcc.cis298.cis298inclass3;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";

    //This string will be used as a key for the fragment manager
    //when we create the date dialog.
    //We send it a fragmentManager, and what key we want it to use
    //when it makes the new fragment.
    private static final String DIALOG_DATE = "DialogDate";

    //A integer request code that we can send over to the date dialog.
    //when control is returned back to here because the dialog closes,
    //we can check this request code to see what work we need to do.
    //Checking it will happen in onActivityResult despite the fact
    //that we are dealing with fragments.
    private  static final int REQUEST_DATE = 0;

    //A integer request code that we can send over to the contact
    //selection implicit intent. When we return here in onActivityResult
    //we can check the REQUEST code to see if we are returning from the
    //date dialog or the contact selection.
    private static final int REQUEST_CONTACT = 1;

    public static CrimeFragment newInstance(UUID crimeId) {
        //Create a new Bundle to store the args for our fragment
        Bundle args = new Bundle();
        //Put the crimeId that was passed in into the bundle object args
        args.putSerializable(ARG_CRIME_ID, crimeId);
        //Create a new CrimeFragment
        CrimeFragment fragment = new CrimeFragment();
        //Set the arguments on the fragment to the args Bundle we created above
        fragment.setArguments(args);
        //Return the newly created fragment
        return fragment;
    }

    //Define a single crime to use in our fragment
    private Crime mCrime;
    //Define a EditText so we can interact with the one in the layout
    private EditText mTitleField;
    //Define a Button and Checkbox to interact with
    private Button mDateButton;
    private CheckBox mSolvedCheckbox;
    private Button mReportButton;
    private Button mSuspectButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get the UUID for the crime from the intent used to start the hosting activity
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        //Get a handle to the crimeLab, and once we have it, call the getCrime method
        //sending in the UUID to get the specific crime we need.
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Inflate the view and store it a variable so we can setup listeners
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        //We need the view to be able to call findViewById. This is different than with an activity
        //Activities have only one layout, so it can be assumed when we call findViewById that
        //we are referring to the only view it has.
        //Here, we need to specify the view that we are pulling widgets out of.
        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Not doing anything here. I don't think we can get rid of it because
                //it is declared abstract in the parent
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Set the title on the crime each time a character is entered
                //into the edittext.
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Not doing anything here. I don't think we can get rid of it because
                //it is declared abstract in the parent
            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mDateButton.setText(mCrime.getDate().toString());
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment
                        .newInstance(mCrime.getDate());

                //Set the target fragment for the dialog for when it
                //is done. This is where we are expecting it to return
                //to once it has finished it's work.
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);

                //The show method on the DialogFragment we made an instance
                //of right above takes in a fragmentManager, and a key that
                //will be used by the fragment manager to hold on to the
                //instance of the fragment.
                //This way on screen rotation, the fragment can be retrieved
                //from the fragmentManager's list without any extra work.
                //We did not need to do this when making the other fragments
                //because we did the transaction and committing ourselves.
                dialog.show(manager, DIALOG_DATE);
            }
        });

        mSolvedCheckbox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckbox.setChecked(mCrime.isSolved());
        mSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        //Wire up the report button to launch an implicit intent that
        //will allow the user to email a report
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        //The ContactsContract.Contacts.CONTENT_URI is just a pointer
        //to the database on the device that holds all of the contacts
        //This is the standard way to get to contacts and interact with
        //them.
        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the activity for a result. Send over the
                //Int REQUEST code defined at the top of this class
                //so we can check it later in onActivityResult
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        //Set the text on the button when if the suspect is set.
        //This may not be useful for us since we are not doing the
        //sqlite database yet. Every crime we have will load with
        //no suspect already set. If we were using sqlite, the
        //suspect might be set and stored in the DB, making this more
        //useful.
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //Do some checking to see if the device has a contacts app
        //that can be used to select a contact.
        //Make a new package manager instance from the activity
        PackageManager packageManager = getActivity().getPackageManager();
        //If there is NOT a default activity that can handle the
        //pickContact intent defined above.
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            //Disable the suspect button
            mSuspectButton.setEnabled(false);
        }

        // Inflate the layout for this fragment
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //If the result code is not RESULT_OK skip doing work.
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        //Check to see if the requestCode is the same as the date request
        //code we used when we set the target fragment for the date dialog
        if (requestCode == REQUEST_DATE) {
            //Get the date from the intent object that was returned / passed
            //into this method. Use the KEY that was declared on the
            //DatePickerFragment as a const to access the data in the intent.
            //This is a little different than how we have done it in the past.
            Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            //Set the date
            mCrime.setDate(date);
            mDateButton.setText(mCrime.getDate().toString());
        } else if (requestCode == REQUEST_CONTACT && data != null) {
            Uri contactUri = data.getData();
            //Specify which fields you want your query to return
            //values for
            //Get the Display name for showing the contacts name
            //Get the _ID for use in querying the email table
            String[] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME, //index 0
                    ContactsContract.Contacts._ID           //index 1
            };

            //Get the content resolver that we can use to make queries on contacts
            ContentResolver contentResolver = getActivity().getContentResolver();

            //Perform your query - the contactUri is like a "where"
            //clause here
            Cursor c = contentResolver.query(contactUri, queryFields, null,null,null);

            //Make a new Cursor that will be used for the Email table
            Cursor emailCursor = null;

            try {
                //Double-check that you already got results
                if (c.getCount() == 0) {
                    return;
                }

                //Pull out the first column of the first row of data -
                //that is your suspect's name

                //Move the cursor to the first result. There should only
                //be one result, but even if there are more, we will take
                //the first, so move there.
                c.moveToFirst();
                //Get out the DisplayName of the contact. This is done
                //by referencing the index of the Column we put in the
                //query String[]. We defined it up above with only one
                //entry in the array, which was DISPLAY_NAME, and so
                //the only thing we have to pull out is the data in the
                //zero 0 index.
                //If we had 2 columns, we would need to pull them both out
                //with indexes 0 and 1.
                String suspect = c.getString(0);

                //Get the ID of the contact. It will be in the 1 index
                //since it is in the 2nd spot of the queryFields array above
                String id = c.getString(1);
                //Alternatively you could do this, probably less efficent:
                //String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                //Now that we have the ID of the contact, let's try to get the email
                //of the contact from the email table

                //Make query fields for getting the email
                String[] emailQueryFields = new String[]{
                        ContactsContract.CommonDataKinds.Email.DATA
                };

                //Make a query to the email contact URI.
                //Argument order is as follows:
                //1. Content URI
                //2. Query Fields
                //3. Where Clause
                //4. Where Args
                //5. Sort Order??
                emailCursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                        emailQueryFields,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                        new String[]{id},
                        null
                );

                //Move to the first record in the query result
                emailCursor.moveToFirst();

                //I would assign this to a class level if I had it.
                //local for demo purposes
                //Since we only queried out the email, I know it is in index 0
                String contactEmail = emailCursor.getString(0);

                //Once we have it, use the suspect string to update
                //the crime and the button text
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);

            } catch (Exception e) {
                Log.e("Crime", e.getMessage() + e.getStackTrace());
            } finally {
                if (c != null) {
                    c.close();
                }
                if (emailCursor != null) {
                    emailCursor.close();
                }
            }


        }
    }

    private  String getCrimeReport() {
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
            suspect = getString(R.string.crime_report_suspect);
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dateString, solvedString, suspect);

        return report;
    }
}
