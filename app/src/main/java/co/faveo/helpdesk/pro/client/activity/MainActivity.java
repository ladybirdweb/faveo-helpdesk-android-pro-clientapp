package co.faveo.helpdesk.pro.client.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.getkeepsafe.taptargetview.TapTargetView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;
import co.faveo.helpdesk.pro.client.R;
import co.faveo.helpdesk.pro.client.SharedPreference;
import co.faveo.helpdesk.pro.client.application.MessageEvent;
import co.faveo.helpdesk.pro.client.fragments.About;
import co.faveo.helpdesk.pro.client.fragments.EditCustomer;
import co.faveo.helpdesk.pro.client.fragments.FragmentDrawer;
import co.faveo.helpdesk.pro.client.fragments.MyClosedTickets;
import co.faveo.helpdesk.pro.client.fragments.MyOpenTickets;
import co.faveo.helpdesk.pro.client.receiver.InternetReceiver;
import io.fabric.sdk.android.Fabric;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity implements
        FragmentDrawer.FragmentDrawerListener, About.OnFragmentInteractionListener, MyOpenTickets.OnFragmentInteractionListener, EditCustomer.OnFragmentInteractionListener,
        MyClosedTickets.OnFragmentInteractionListener {
    SharedPreference sharedPreferenceObj;
    FabSpeedDial fabSpeedDial;
    Fragment fragment;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        //FirebaseCrash.setCrashCollectionEnabled(true);

        //Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
        sharedPreferenceObj = new SharedPreference(MainActivity.this);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_in_from_right);
        Window window = MainActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(MainActivity.this, R.color.mainActivityTopBar));
        ButterKnife.bind(this);
        Prefs.putString("ticketThread", "");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarMain);
        mToolbar.inflateMenu(R.menu.search_menu);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_black_24dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        FragmentDrawer drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);
        MyOpenTickets inboxTickets = new MyOpenTickets();
        //inboxTickets.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container_body, inboxTickets);
        fragmentTransaction.commit();
        setActionBarTitle(getResources().getString(R.string.mytickets));

        // We load a drawable and create a location to show a tap target here
        // We need the display to get the width and height at this point in time
        final Display display = getWindowManager().getDefaultDisplay();
        // Load our little droid guy
        final Drawable droid = ContextCompat.getDrawable(this, R.mipmap.ic_launcher);
        // Tell our droid buddy where we want him to appear
        final Rect droidTarget = new Rect(0, 0, droid.getIntrinsicWidth() * 2, droid.getIntrinsicHeight() * 2);
        // Using deprecated methods makes you look way cool
        droidTarget.offset(display.getWidth() / 2, display.getHeight() / 2);

        final SpannableString sassyDesc = new SpannableString("It allows you to go back, sometimes");
        sassyDesc.setSpan(new StyleSpan(Typeface.ITALIC), sassyDesc.length() - "sometimes".length(), sassyDesc.length(), 0);

        // We have a sequence of targets, so lets build it!
        final TapTargetSequence sequence = new TapTargetSequence(this)
                .targets(
                        // This tap target will target the back button, we just need to pass its containing toolbar
                        // Likewise, this tap target will target the search button
                        TapTarget.forToolbarMenuItem(mToolbar, R.id.action_search, "This is a search icon", "From here you will be able to search ticket and user in your helpdesk.")
                                .dimColor(android.R.color.black)
                                .outerCircleColor(R.color.faveo)
                                .targetCircleColor(android.R.color.white)
                                .transparentTarget(true)
                                .textColor(android.R.color.white)
                                .id(2).cancelable(false)
//                        TapTarget.forToolbarMenuItem(mToolbar, R.id.action_noti, "This is a notification icon", "You will get all the notification in your helpdesk from here.")
//                                .dimColor(android.R.color.black)
//                                .outerCircleColor(R.color.faveo)
//                                .targetCircleColor(android.R.color.white)
//                                .transparentTarget(true)
//                                .textColor(android.R.color.white)
//                                .id(3).cancelable(false)
                )
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        new BottomDialog.Builder(MainActivity.this)
                                .setContent(R.string.intro)
                                .setPositiveText("ok")
                                .setCancelable(false)
                                .setPositiveBackgroundColorResource(R.color.white)
                                //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
                                .setPositiveTextColorResource(R.color.faveo)
                                //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                                .onPositive(new BottomDialog.ButtonCallback() {
                                    @Override
                                    public void onClick(BottomDialog dialog) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {
                        Log.d("TapTargetView", "Clicked on " + lastTarget.id());
                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
//                        final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
//                                .setTitle("Uh oh")
//                                .setMessage("You canceled the sequence")
//                                .setPositiveButton("Oops", null).show();
//                        TapTargetView.showFor(dialog,
//                                TapTarget.forView(dialog.getButton(DialogInterface.BUTTON_POSITIVE), "Uh oh!", "You canceled the sequence at step " + lastTarget.id())
//                                        .cancelable(false)
//                                        .tintTarget(false), new TapTargetView.Listener() {
//                                    @Override
//                                    public void onTargetClick(TapTargetView view) {
//                                        super.onTargetClick(view);
//                                        dialog.dismiss();
//                                    }
//                                });
                    }
                });

        if (sharedPreferenceObj.getApp_runFirst().equals("FIRST")) {
            // That's mean First Time Launch
            // After your Work , SET Status NO
            TapTargetView.showFor(this,                 // `this` is an Activity
                    TapTarget.forToolbarNavigationIcon(mToolbar, "This is the hamburger icon,from here you can control the app.You will get option to create ticket,access the settings and support page and also you will get option to log out from the app.", sassyDesc).id(1)
                            .dimColor(android.R.color.black)
                            .outerCircleColor(R.color.faveo)
                            .targetCircleColor(android.R.color.white)
                            .transparentTarget(true)
                            .textColor(android.R.color.white).cancelable(false),                 // Specify the target radius (in dp)
                    new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                        @Override
                        public void onTargetClick(TapTargetView view) {
                            super.onTargetClick(view);      // This call is optional
                            sequence.start();
                        }
                    });

            sharedPreferenceObj.setApp_runFirst("NO");
        } else {

            // App is not First Time Launch
        }

        fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_main);

//       fabSpeedDial.setOnClickListener(this);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onMenuItemSelected(MenuItem menuItem) {
                int id = menuItem.getItemId();

                if (id == R.id.fab_createTicket) {
                    Intent intent = new Intent(MainActivity.this, CreateTicketActivity.class);
                    startActivity(intent);
                } else if (id == R.id.fab_requestItem) {
                    title = getString(R.string.myprofile);
                    fragment = MainActivity.this.getSupportFragmentManager().findFragmentByTag(title);
                    if (fragment == null)
                        fragment = new EditCustomer();
                    if (fragment != null) {
                        FragmentManager fragmentManager = MainActivity.this.getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity.this)).setActionBarTitle(title);
                    }
                }
                //TODO: Start some activity
                return false;
            }
        });

    }

    public void setActionBarTitle(final String title) {
        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbarMain);
        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.title);
        mTitle.setText(title.toUpperCase());

    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

    }

    private void checkConnection() {
        boolean isConnected = InternetReceiver.isConnected();
        showSnackIfNoInternet(isConnected);
    }

    /**
     * Display the snackbar if network connection is not there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnackIfNoInternet(boolean isConnected) {
        if (!isConnected) {
            final Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.sry_not_connected_to_internet, Snackbar.LENGTH_INDEFINITE);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.RED);
            snackbar.setAction("X", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snackbar.dismiss();
                }
            });
            snackbar.show();
        }

    }

    /**
     * Display the snackbar if network connection is there.
     *
     * @param isConnected is a boolean value of network connection.
     */
    private void showSnack(boolean isConnected) {

        if (isConnected) {
            Snackbar snackbar = Snackbar
                    .make(findViewById(android.R.id.content), R.string.connected_to_internet, Snackbar.LENGTH_LONG);

            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            showSnackIfNoInternet(false);
        }

    }

//    /**
//     * Callback will be triggered when there is change in
//     * network connection
//     */
//    @Override
//    public void onNetworkConnectionChanged(boolean isConnected) {
//        showSnack(isConnected);
//    }

    /**
     * Handling the back button here.
     * As if we clicking twice then it will
     * ask press one more time to exit,we are handling
     * the double back button pressing here.
     */
    @Override
    public void onBackPressed() {
        new BottomDialog.Builder(MainActivity.this)
                .setTitle(R.string.log_out)
                .setContent(getString(R.string.confirmMessage))
                .setPositiveText("YES")
                .setNegativeText("NO")
                .setPositiveBackgroundColorResource(R.color.white)
                //.setPositiveBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary)
                .setPositiveTextColorResource(R.color.faveo)
                .setNegativeTextColor(R.color.black)
                //.setPositiveTextColor(ContextCompat.getColor(this, android.R.color.colorPrimary)
                .onPositive(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(BottomDialog dialog) {
                        finishAffinity();
                        System.exit(0);
                    }
                }).onNegative(new BottomDialog.ButtonCallback() {
            @Override
            public void onClick(@NonNull BottomDialog bottomDialog) {
                bottomDialog.dismiss();
            }
        })
                .show();
    }

    // This method will be called when a MessageEvent is posted (in the UI thread for Toast)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        //Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
//        Snackbar.make(findViewById(android.R.id.content), event.message, Snackbar.LENGTH_LONG).show();
        showSnack(event.message);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
