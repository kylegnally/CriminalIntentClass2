package edu.kvcc.cis298.cis298inclass3;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by cisco on 10/16/2017.
 */

public class CrimeLab {
    //This static variable will hold our single instance of
    //crime lab.
    //It might seem weird that the class is holding a reference
    //to itself, but it that's just how it's done.
    //Kind of like a Linked List.
    private static CrimeLab sCrimeLab;

    //A list to hold our crimes
    private List<Crime> mCrimes;

    //This will be the public static method that is used to always
    //get the same instance that is stored in the static sCrimeLab
    //field.
    //Currently it takes in a Context that we will not use until
    //we get to the sqlite part.
    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    //This is the constructor. It is private because it is part
    //of a singleton. This ensures that the only code that can
    //make an instance of this class is this class.
    //The instance is made above in the get method.
    private CrimeLab(Context context) {
        mCrimes = new ArrayList<>();

        //This is just dummy data that we will remove later
        for (int i = 0; i < 5; i++) {
            Crime crime = new Crime();
            crime.setTitle("Crime #" + i);
            crime.setSolved(i % 2 == 0); //Every other one
            mCrimes.add(crime);
        }
    }

    //Add a new crime to the list
    public void addCrime(Crime c) {
        mCrimes.add(c);
    }

    //Getter for all of the crimes
    public List<Crime> getCrimes() {
        return mCrimes;
    }

    //Setter for all of the crimes
    public void setCrimes(List<Crime> crimes) {
        mCrimes = crimes;
    }

    //Getter for a single crime
    public Crime getCrime(UUID id) {
        for (Crime crime : mCrimes) {
           if (crime.getId().equals(id)) {
               return crime;
           }
        }
        return null;
    }
}
