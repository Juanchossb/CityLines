package com.future.citylines;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

public class Navigation extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    Ofertas ofertas ;
    Perfil perfil;
    Redimir redimir;
    String USER_ID;
    LinearLayout LINEACONSTANTE;
    Button botonhome,botonexp,botonperfil;
    Utilidades util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        ofertas = new Ofertas();
        perfil = new Perfil();
        redimir = new Redimir();
        USER_ID = "1";
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        util= new Utilidades();

        LinearLayout.LayoutParams frameparams = new LinearLayout.LayoutParams(util.screenSizeX(this)/5,util.screenSizeY(this)/10);
        frameparams.setMargins(util.screenSizeX(this)/10,5,0,5);

        RelativeLayout.LayoutParams relativeparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
      //  relativeparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);


        LINEACONSTANTE = (LinearLayout) findViewById(R.id.lineaconstante);
LINEACONSTANTE.setPadding(0,0,0,5);
     //   LINEACONSTANTE.setLayoutParams(relativeparams);
        botonhome = (Button) findViewById(R.id.botonhome);
        botonhome.setLayoutParams(frameparams);
        botonexp= (Button) findViewById(R.id.botonexperiencias);
        botonexp.setLayoutParams(frameparams);
        botonexp.setTextSize(8);
        botonperfil = (Button) findViewById(R.id.botonexperiencias);
        botonperfil.setLayoutParams(frameparams);
        LINEACONSTANTE.setBackgroundColor(Color.parseColor("#ffffff"));
        LINEACONSTANTE.setAlpha(1/2);



        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (position == 0) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ofertas.newInstance(position + 1))
                    .commit();

        }else if (position == 1){
            Bundle args2 = new Bundle();
            args2.putString("userid", USER_ID);
            perfil.setArguments(args2);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, perfil.newInstance(position + 1))
                    .commit();
        }else if(position == 2){
            Bundle args2 = new Bundle();
            args2.putString("userid", USER_ID);
            redimir.setArguments(args2);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, redimir.newInstance(position + 1))
                    .commit();
        }
    }



    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = "Experiencias";
                break;
            case 2:
                mTitle = "Perfil";
                break;
            case 3:
                mTitle = "Redimir";
                break;
        }
    }

    public String getUserId(){
        return  USER_ID;
    }
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.navigation, menu);
            restoreActionBar();
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            MenuItem smenuitem = menu.findItem(R.id.search);
    //        android.support.v7.widget.SearchView searchView =(android.support.v7.widget.SearchView) MenuItemCompat.getActionView(smenuitem);
          /*  searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));

            searchView.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
*/
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_navigation, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Navigation) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);

        }
    }

}
