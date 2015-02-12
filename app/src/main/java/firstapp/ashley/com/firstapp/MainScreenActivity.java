package firstapp.ashley.com.firstapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;

import android.app.FragmentTransaction;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

//import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.LocationServices;


public class MainScreenActivity extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
         ActionBar.TabListener{
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation ;
    private Double latitute;
    private Double longitutde;
    private String loggedInUser;
    ViewPager mViewPager;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    private static DataStore newCheeses;
    private boolean hasSaidWelcome = false;
    private MongoDB eventDB;
    @Override
    protected void onStart() {
        super.onStart();
        //if (!mResolvingError) {  // more about this later
            mGoogleApiClient.connect();
        //}
        // Get the message from the intent
        Intent intent = getIntent();

        loggedInUser = intent.getStringExtra(FirstActivity.USER_NAME);
        if(!hasSaidWelcome && loggedInUser != null && ! loggedInUser.isEmpty()) {
            invalidateOptionsMenu();

            Toast.makeText(getApplicationContext(), "Welcome Back "+loggedInUser,
                    Toast.LENGTH_SHORT).show();
            hasSaidWelcome = true;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        buildGoogleApiClient();
        // Get the message from the intent
        Intent intent = getIntent();

        newCheeses = new DataStore();





        loggedInUser = intent.getStringExtra(FirstActivity.USER_NAME);
        if(loggedInUser != null && ! loggedInUser.isEmpty()) {
            // Create the text view
            TextView textView = new TextView(this);
            textView.setTextSize(20);
            textView.setText("Welcome " + loggedInUser);
        }
        // Set the text view as the activity layout
        //setContentView(textView);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //setContentView(R.layout.activity_main_screen);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.

        setContentView(R.layout.activity_main_screen);

        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by the adapter.
            // Also specify this Activity object, which implements the TabListener interface, as the
            // listener for when this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
        final Activity given = this;


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        Intent intent;
        switch (item.getItemId()) {
            case R.id.action_search:
                //openSearch();
                return true;
            case R.id.action_login:
                 intent = new Intent(this,FirstActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_logout:
                loggedInUser = null;
                invalidateOptionsMenu();
                return true;
            case R.id.action_profile:
                intent = new Intent(this,EditProfile.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                 intent = new Intent(this,Settings.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem loginItem = menu.findItem(R.id.action_login);
        MenuItem logoutItem = menu.findItem(R.id.action_logout);
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        if(loggedInUser != null && ! loggedInUser.isEmpty()){
            Log.i("Main Activity","Login item"+loginItem.toString()+"onPrepareOptionsMenu : "+loggedInUser);
            loginItem.setVisible(false);
            logoutItem.setVisible(true);
            profileItem.setVisible(true);
        }else {
            loginItem.setVisible(true);
            logoutItem.setVisible(false);
            profileItem.setVisible(false);
        }
        return true;

    }

    protected synchronized void buildGoogleApiClient() {
        // Create a GoogleApiClient instance
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            latitute = mLastLocation.getLatitude();
            longitutde = mLastLocation.getLongitude();
        }
        Log.i("Main Activity", "lat:" + latitute.toString() + " long:" + longitutde.toString());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs
        // until onConnected() is called.
        Log.i("MainActivity", "Location Connection Suspended ");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
        // More about this in the next section.
        Log.i("Main Activity","Location Connection Failed "+result.toString());

        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }

    }
    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }



    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() { }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode,
                    this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            Activity mainActivity = getActivity();
            if(mainActivity != null) {
                ((MainScreenActivity) mainActivity).onDialogDismissed();
            }
        }
    }
    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(android.support.v4.app.FragmentManager fm) {
            super(fm);
        }

        @Override
        public 	android.support.v4.app.Fragment  getItem(int i) {
            String sectionName = "Interests";
            switch (i) {
                case 0:
                    sectionName = "Your Interests";

                    break;
                case 1:
                    sectionName = "Latest";

                    break;
                case 2:
                    sectionName = "Near You";
                    break;
                    // The first section of the app is the most interesting -- it offers
                    // a launchpad into the other demonstrations in this example application.
                //    return new LaunchpadSectionFragment();

                default:

            }
            // The other sections of the app are dummy placeholders.
            //android.support.v4.app.Fragment fragment = new DummySectionFragment();
            ListFragment fragment = new ArrayListFragment();
            Bundle args = new Bundle();
            args.putString(DummySectionFragment.ARG_SECTION_NUMBER, sectionName);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "Interests";


                case 1:
                    return "Latest";

                case 2:
                    return "Near You";

                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                //    return new LaunchpadSectionFragment();

                default:

            }

            return "Section " + (position + 1);
        }
    }
    /**
     * A fragment that launches other parts of the demo application.
     */
    public static class LaunchpadSectionFragment extends android.support.v4.app.Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_launchpad, container, false);

            // Demonstration of a collection-browsing activity.
/*            rootView.findViewById(R.id.demo_collection_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), CollectionDemoActivity.class);
                            startActivity(intent);
                        }
                    });
*/
            // Demonstration of navigating to external activities.
            rootView.findViewById(R.id.demo_external_activity)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Create an intent that asks the user to pick a photo, but using
                            // FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET, ensures that relaunching
                            // the application from the device home screen does not return
                            // to the external activity.
                            Intent externalActivityIntent = new Intent(Intent.ACTION_PICK);
                            externalActivityIntent.setType("image/*");
                            externalActivityIntent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                            startActivity(externalActivityIntent);
                        }
                    });

            return rootView;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends 	android.support.v4.app.Fragment {

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    args.getString(ARG_SECTION_NUMBER));

            rootView.findViewById(R.id.organize_button)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), Organize.class);
                            startActivity(intent);
                        }
                    });

            return rootView;
        }
    }

    public static class ArrayListFragment extends ListFragment {
        int mNum;

        /**
         * Create a new instance of CountingFragment, providing "num"
         * as an argument.
         */
        static ArrayListFragment newInstance(int num) {
            ArrayListFragment f = new ArrayListFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putInt("num", num);
            f.setArguments(args);

            return f;
        }

        /**
         * When creating, retrieve this instance's number from its arguments.
         */
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mNum = getArguments() != null ? getArguments().getInt("num") : 1;
        }

        /**
         * The Fragment's UI is just a simple text view showing its
         * instance number.
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_section_dummy, container, false);
            //View tv = v.findViewById(R.id.text);
            //((TextView)tv).setText("Fragment #" + mNum);
            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, newCheeses.eventNameList()));
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            Log.i("FragmentList", "Item clicked: " + id);
            Intent intent = new Intent(getActivity(), EventDetail.class);
            Bundle b = new Bundle();
            b.putParcelable("event_detail",newCheeses.getEventForID(id));
            b.putLong("selectedID", id); //Your id
            intent.putExtras(b); //Put your id to your next Intent
            startActivity(intent);
        }
    }
}
