package com.etex.sunmobilepro.Adaptadores;

/**
 * Created by javi on 23/06/15.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> mFragments = new ArrayList<>();   // Lista que contiene los fragments para poder instanciarlos
    CharSequence Titulos[];
    int numTabs;
    private Fragment mCurrentFragment;

    public TabsPagerAdapter(FragmentManager fm, CharSequence mTitulos[], int mNumTabs) {
        super(fm);

        this.Titulos = mTitulos;
        this.numTabs = mNumTabs;
        mCurrentFragment=null;
    }

    @Override
    public Fragment getItem(int index) {

        return  mFragments.get(index);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return Titulos[position];
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return numTabs;
    }

    // guarda el fragment actual en una varible
    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (mCurrentFragment != object) {
            mCurrentFragment = (Fragment) object;
        }
        super.setPrimaryItem(container, position, object);
    }

    // metodo get para el fragment actual
    public Fragment getFragment(int i) {

        return mFragments.get(i);
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);

    }


    //mFragmentTitles.add(title);
    /*@Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }*/

}