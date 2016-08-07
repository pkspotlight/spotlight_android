package me.spotlight.spotlight.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by Anatol on 7/10/2016.
 */
public class FragmentUtils {


    private static final String FRAGMENT_TAG = "Content";


    /*
        Get current
     */
    public static Fragment getCurrent (FragmentActivity activity, int id){

        return activity.getSupportFragmentManager().findFragmentById(id);

    }


    /*
        Change fragment fragment activity
     */
    public static void changeFragment(FragmentActivity activity, int contentFrame,
                                      Fragment fragment, boolean addToBackStack){

        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.replace(contentFrame, fragment, FRAGMENT_TAG);
        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.commitAllowingStateLoss();
    }


    /*
        Change fragment fragment manager
     */
    public static void changeFragment (FragmentManager fragmentManager, int contentFrame,
                                       Fragment fragment, boolean addToBackStack) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(contentFrame, fragment, FRAGMENT_TAG);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }

        fragmentTransaction.commitAllowingStateLoss();
    }




    public static void addFragment(FragmentActivity activity, int contentFrame, Fragment fragmentRemove,
                                        Fragment fragmentAdd, boolean addToBackStack) {
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.remove(fragmentRemove);
        ft.add(contentFrame, fragmentAdd);
        ft.addToBackStack(null);
        ft.commit();
    }


    /*
        Pop backstack fragment manager
     */
    public static void popBackStack(FragmentManager fragmentManager) {

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }


    /*
        Pop backstack fragment activity
     */
    public static void popBackStack(FragmentActivity activity) {

        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); ++i) {
            fragmentManager.popBackStack();
        }
    }
}
