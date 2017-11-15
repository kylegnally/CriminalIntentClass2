package edu.kvcc.cis298.cis298inclass3;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import java.util.List;
import java.util.UUID;

/**
 * Created by cisco on 10/30/2017.
 */

public class CrimePagerActivity extends AppCompatActivity {

    //Key to use for the Crime's Id.
    private static final String EXTRA_CRIME_ID =
            "edu.kvcc.cis298.cis298inclass2.crime_id";

    //Setup class level vars
    private ViewPager mViewPager;
    private List<Crime> mCrimes;

    //Static method to get an Intent that can start this Activity
    public static Intent newIntent(Context packageContext, UUID crimeId) {
        Intent intent = new Intent(packageContext, CrimePagerActivity.class);
        intent.putExtra(EXTRA_CRIME_ID, crimeId);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_pager);

        //Get the Id for the crime from the Intent's extras
        UUID crimeId = (UUID) getIntent()
                .getSerializableExtra(EXTRA_CRIME_ID);

        //Get the view pager widget from the view.
        mViewPager = (ViewPager) findViewById(R.id.activity_crime_pager_view_pager);

        //Get the list of crimes from our singleton.
        mCrimes = CrimeLab.get(this).getCrimes();
        //Get the fragment manager that we have been using. Since we have
        //been using the Support Fragment Manager, we can just call the
        //getSupportFragmentManager method to get it.
        FragmentManager fragmentManager = getSupportFragmentManager();
        //Set up the adapter for the ViewPager. The new adapter requires
        //use to pass a fragment manager into it's constructor, and then
        //override it's abstract methods getItem, and getCount
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            //Override of getItem
            @Override
            public Fragment getItem(int position) {
                //Get a specific crime from the list by the position passed
                //to this method
                Crime crime = mCrimes.get(position);
                //Create a new Fragment using the static method we made
                //on the CrimeFragment class called newInstance and return
                //that fragment. There would be more work here if we had not
                //already made the newInstance method to make the fragment
                //for us.
                return CrimeFragment.newInstance(crime.getId());
            }

            //Override of getCount
            @Override
            public int getCount() {
                //Return the size of the crimes list.
                return mCrimes.size();
            }
        });

        //Set the current item for the view pager
        //Without this, it would just start up the view pager on the wrong
        //crime.
        for (int i = 0; i < mCrimes.size(); i++) {
            //If the crime at index i is the crime with the passed in UUID
            //then we can set the current item to that index.
            if (mCrimes.get(i).getId().equals(crimeId)) {
                mViewPager.setCurrentItem(i);
                //Break out of the loop early to possibly save some time.
                //Maybe it won't always be O(N)???
                break;
            }
        }
    }
}
