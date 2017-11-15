package edu.kvcc.cis298.cis298inclass3;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

    private static final String EXTRA_CRIME_ID =
            "edu.kvcc.cis298.cis298inclass2.crime_id";

    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimeActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    //This is the method that we must implement from the parent
    //class that we created. It does the work of making the
    //fragment that needs to be hosted.
    @Override
    protected Fragment createFragment() {
        //Pull the crimeId out of the Intent that was used
        //to get this activity started
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);
        //Return the new fragment that gets returned from
        //calling the newInstance method on the CrimeFragment class
        return CrimeFragment.newInstance(crimeId);
    }

}
