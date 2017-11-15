package edu.kvcc.cis298.cis298inclass3;

import android.support.v4.app.Fragment;

/**
 * Created by cisco on 10/16/2017.
 */

public class CrimeListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
