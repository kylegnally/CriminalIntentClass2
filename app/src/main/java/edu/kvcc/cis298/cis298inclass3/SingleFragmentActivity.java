package edu.kvcc.cis298.cis298inclass3;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by cisco on 10/16/2017.
 */

//This is a generic Activity good for hosting a single fragment.
//It inherits from FragmentActivity of the support library
//It is an abstract class, so it can't be instantiated.
public abstract class SingleFragmentActivity extends AppCompatActivity {

    //This declares a abstract method for creating a fragment
    //that can be hosted in this activity.
    //All child activities of this activity will have to
    //write an implementation of this method.
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment);

        //Get a handle to the fragment manager that can store all of
        //our instantiated fragments
        FragmentManager fm = getSupportFragmentManager();

        //Get the fragment we want from the fragment manager.
        //The first time this is run, it will return null since it does not exist
        //We send into this method, the Id of the framelayout where we expect
        //the fragment to be.
        //Obviously when we start up, the fragment will not be in our framelayout.
        //There is nothing in there.
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        //Check to see if there was a fragment in the frame layout we used to get
        //out the fragment we want.
        if (fragment == null) {
            fragment = createFragment();
            //We then need to start and finish a transaction to get the
            //fragment added to the frame layout
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
