package co.faveo.helpdesk.pro.client.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.faveo.helpdesk.pro.client.R;
import co.faveo.helpdesk.pro.client.UIUtils;
import co.faveo.helpdesk.pro.client.activity.CreateTicketActivity;
import co.faveo.helpdesk.pro.client.activity.LoginActivity;
import co.faveo.helpdesk.pro.client.activity.MainActivity;
import co.faveo.helpdesk.pro.client.application.Constants;
import co.faveo.helpdesk.pro.client.application.Helpdesk;
import co.faveo.helpdesk.pro.client.dialog.CircleTransform;
import co.faveo.helpdesk.pro.client.dialog.ConfirmationDialog;
import co.faveo.helpdesk.pro.client.model.DataModel;
import co.faveo.helpdesk.pro.client.model.NavDrawerItem;
import es.dmoral.toasty.Toasty;


/**
 * This is the fragment where we are going to handle the
 * drawer item events,for create ticket ,inbox,client list...
 */
public class FragmentDrawer extends Fragment implements View.OnClickListener {


    static String token;
    private static String[] titles = null;
    View containerView;
    View layout;
    Context context;
    DataModel[] drawerItem;
    DrawerItemCustomAdapter drawerItemCustomAdapter;
    ConfirmationDialog confirmationDialog;
    //int count=0;
    ProgressDialog progressDialog;
    String title;
    int responseCodeForShow;
    int opencount = 0, closecount = 0;
    @BindView(R.id.usernametv)
    TextView userNameText;
    @BindView(R.id.domaintv)
    TextView domainAddress;
    @BindView(R.id.roleTv)
    TextView userRole;
    @BindView(R.id.imageView_default_profile)
    ImageView profilePic;
    @BindView(R.id.listviewNavigation)
    ListView listView;
    @BindView(R.id.ticket_list)
    LinearLayout ticketList;
    @BindView(R.id.clientList)
    TextView textViewClientList;
    @BindView(R.id.clientImage)
    ImageView clientImage;
    @BindView(R.id.create_ticket)
    LinearLayout linearLayoutCreate;
    @BindView(R.id.client_list)
    LinearLayout linearClientList;
    @BindView(R.id.about)
    LinearLayout linearLayoutAbout;
    @BindView(R.id.logout)
    LinearLayout linearLog;
    @BindView(R.id.aboutimage)
    ImageView imageviewabout;
    @BindView(R.id.abouttext)
    TextView textviewabout;
    @BindView(R.id.logoutimage)
    ImageView imageViewlogout;
    @BindView(R.id.logouttext)
    TextView textviewlogout;
    int option = 5;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private FragmentDrawerListener drawerListener;
    Fragment fragment = null;
    String titleFragment;
    public FragmentDrawer() {

    }

    public static List<NavDrawerItem> getData() {
        List<NavDrawerItem> data = new ArrayList<>();

        for (String title : titles) {
            NavDrawerItem navItem = new NavDrawerItem();
            navItem.setTitle(title);
            data.add(navItem);
        }
        return data;
    }

    public void setDrawerListener(FragmentDrawerListener listener) {
        this.drawerListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titles = (new String[]{"Item1", "Item2", "Item3", "Item4"});
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getActivity().getApplicationContext();
        titleFragment=getActivity().getString(R.string.app_name);
        layout = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        listView = (ListView) layout.findViewById(R.id.listviewNavigation);
        layout.findViewById(R.id.create_ticket).setOnClickListener(this);
        layout.findViewById(R.id.client_list).setOnClickListener(this);
        layout.findViewById(R.id.about).setOnClickListener(this);
        layout.findViewById(R.id.logout).setOnClickListener(this);
        drawerItem = new DataModel[2];
        ButterKnife.bind(this, layout);
        confirmationDialog = new ConfirmationDialog();
        drawerItem[0] = new DataModel(R.drawable.inbox_tickets, getString(R.string.mytickets), Prefs.getString("openCount", null));
        drawerItem[1] = new DataModel(R.drawable.closed_ticket, getString(R.string.myclosed), Prefs.getString("closeCount", null));
        //drawerItem[1] = new DataModel(R.drawable.closed_ticket,getString(R.string.closed_tickets),Prefs.getString("closedTickets", null));
        drawerItemCustomAdapter = new DrawerItemCustomAdapter(getActivity(), R.layout.list_view_item_row, drawerItem);
        listView.setAdapter(drawerItemCustomAdapter);
        progressDialog = new ProgressDialog(getActivity());
        drawerItemCustomAdapter.notifyDataSetChanged();
        UIUtils.setListViewHeightBasedOnItems(listView);
        UIUtils.setListViewHeightBasedOnItems(listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Fragment fragment = null;
                    option = 0;
                    titleFragment = getString(R.string.mytickets);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(titleFragment);
                    if (fragment == null)
                        fragment = new MyOpenTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(titleFragment);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                } else if (position == 1) {
                    Fragment fragment = null;
                    option = 1;
                    titleFragment = getString(R.string.myclosed);
                    fragment = getActivity().getSupportFragmentManager().findFragmentByTag(titleFragment);
                    if (fragment == null)
                        fragment = new MyClosedTickets();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.container_body, fragment);
                        // fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                        ((MainActivity) getActivity()).setActionBarTitle(titleFragment);
                        mDrawerLayout.closeDrawer(GravityCompat.START);
                    }
                }
            }

        });
        try {
            String letter = Prefs.getString("profilePicture", null);
            Log.d("profilePicture", letter);
            if (letter.contains("jpg") || letter.contains("png") || letter.contains("jpeg")) {
                Picasso.with(context).load(letter).transform(new CircleTransform()).into(profilePic);
            } else {
                int color = Color.parseColor("#ffffff");
                String letter1 = String.valueOf(Prefs.getString("PROFILE_NAME", "").charAt(0));
                ColorGenerator generator = ColorGenerator.MATERIAL;
                TextDrawable drawable = TextDrawable.builder()
                        .buildRound(letter1, color);
                //profilePic.setAlpha(0.2f);
                profilePic.setColorFilter(context.getResources().getColor(R.color.faveo), PorterDuff.Mode.SRC_IN);
                profilePic.setImageDrawable(drawable);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        userRole.setText(Prefs.getString("ROLE", ""));
        domainAddress.setText(Prefs.getString("BASE_URL", ""));
        userNameText.setText(Prefs.getString("PROFILE_NAME", ""));
        try {
            String cameFromSetting = Prefs.getString("cameFromSetting", null);
            if (cameFromSetting.equals("true")) {
                option = 5;
                textviewabout.setTextColor(getResources().getColor(R.color.black));
                imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                textViewClientList.setTextColor(getResources().getColor(R.color.black));
                clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                textviewlogout.setTextColor(getResources().getColor(R.color.black));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                Prefs.putString("cameFromSetting", "false");
                drawerItemCustomAdapter.notifyDataSetChanged();

            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = 6;
                linearClientList.setBackgroundColor(getResources().getColor(R.color.grey_200));
                Prefs.putString("normalclientlist", "true");
                Prefs.putString("filtercustomer", "true");
                title = getString(R.string.client_list);
                textViewClientList.setTextColor(getResources().getColor(R.color.faveo));
                clientImage.setColorFilter(getResources().getColor(R.color.faveo));
                textviewabout.setTextColor(getResources().getColor(R.color.black));
                imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                titleFragment = getString(R.string.myprofile);
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);

                textviewlogout.setTextColor(getResources().getColor(R.color.black));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                if (fragment == null)
                    fragment = new EditCustomer();
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    // fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity) getActivity()).setActionBarTitle(title);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }

            }
        });

        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                option = 6;
                linearClientList.setBackgroundColor(getResources().getColor(R.color.grey_200));
                Prefs.putString("normalclientlist", "true");
                Prefs.putString("filtercustomer", "true");
                title = getString(R.string.client_list);
                textViewClientList.setTextColor(getResources().getColor(R.color.faveo));
                clientImage.setColorFilter(getResources().getColor(R.color.faveo));
                textviewabout.setTextColor(getResources().getColor(R.color.black));
                imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                titleFragment = getString(R.string.myprofile);
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);

                textviewlogout.setTextColor(getResources().getColor(R.color.black));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                if (fragment == null)
                    fragment = new EditCustomer();
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.container_body, fragment);
                    // fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    ((MainActivity) getActivity()).setActionBarTitle(title);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });


        ticketList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //count++;
                if (listView.getVisibility() == View.VISIBLE) {
                    listView.setVisibility(View.GONE);
                } else {
                    listView.setVisibility(View.VISIBLE);
                }
            }
        });
        return layout;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Whenever the this method is going to be called then the
     * async task will be cancelled .
     */
    @Override
    public void onStop() {
        // notice here that I keep a reference to the task being executed as a class member:
//        if (this.new FetchDependency() != null && this.new FetchDependency().getStatus() == AsyncTask.Status.RUNNING)
//            this.new FetchDependency().cancel(true);
        super.onStop();
    }


    public void setUp(int fragmentId, DrawerLayout drawerLayout, final Toolbar toolbar) {
        containerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                new SendPostRequest().execute();
                //new FetchDependency().execute();
                opencount = 0;
                closecount = 0;
                View view = getActivity().getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                new FetchFirst(getActivity()).execute();
                drawerItemCustomAdapter.notifyDataSetChanged();
                getActivity().invalidateOptionsMenu();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                //new SendPostRequest().execute();
                getActivity().invalidateOptionsMenu();

            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                toolbar.setAlpha(1 - slideOffset / 2);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while (itr.hasNext()) {

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));

        }
        return result.toString();
    }

    @Override
    public void onClick(View v) {
        Fragment fragment=null;
        titleFragment=getString(R.string.app_name);
        switch (v.getId()) {

            case R.id.create_ticket:
                option = 6;
                linearLayoutCreate.setBackgroundColor(getResources().getColor(R.color.grey_200));
                Prefs.putString("firstusername", "null");
                Prefs.putString("lastusername", "null");
                Prefs.putString("firstuseremail", "null");
                Prefs.putString("firstusermobile", "null");
                mDrawerLayout.closeDrawer(GravityCompat.START);
                Intent inte = new Intent(getContext(), CreateTicketActivity.class);
                startActivity(inte);
                break;
            case R.id.client_list:
                option = 6;
                linearClientList.setBackgroundColor(getResources().getColor(R.color.grey_200));
                Prefs.putString("normalclientlist", "true");
                Prefs.putString("filtercustomer", "true");
                textViewClientList.setTextColor(getResources().getColor(R.color.faveo));
                clientImage.setColorFilter(getResources().getColor(R.color.faveo));
                textviewabout.setTextColor(getResources().getColor(R.color.black));
                imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                titleFragment = getString(R.string.myprofile);
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);

                textviewlogout.setTextColor(getResources().getColor(R.color.black));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                if (fragment == null)
                    fragment = new EditCustomer();
                break;
            case R.id.about:
                option = 6;
                textviewabout.setTextColor(getResources().getColor(R.color.faveo));
                imageviewabout.setColorFilter(getResources().getColor(R.color.faveo));
                linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.grey_200));

                linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                textViewClientList.setTextColor(getResources().getColor(R.color.black));
                clientImage.setColorFilter(getResources().getColor(R.color.grey_500));

                textviewlogout.setTextColor(getResources().getColor(R.color.black));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                titleFragment = getString(R.string.about);
                fragment = getActivity().getSupportFragmentManager().findFragmentByTag(title);
                if (fragment == null)
                    fragment = new About();
                break;
            case R.id.logout:
                option = 6;
                textviewlogout.setTextColor(getResources().getColor(R.color.faveo));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.faveo));
                linearLog.setBackgroundColor(getResources().getColor(R.color.grey_200));

                textviewlogout.setTextColor(getResources().getColor(R.color.black));
                imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                textViewClientList.setTextColor(getResources().getColor(R.color.black));
                clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                mDrawerLayout.closeDrawers();
                drawerItemCustomAdapter.notifyDataSetChanged();
                new BottomDialog.Builder(getActivity())
                        .setTitle(getString(R.string.confirmLogOut))
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
                                String url = Prefs.getString("URLneedtoshow", null);
                                Prefs.clear();
                                Prefs.putString("URLneedtoshow", url);
                                //getActivity().getSharedPreferences(Constants.PREFERENCE, Context.MODE_PRIVATE).edit().clear().apply();

                                Intent intent = new Intent(getActivity(), LoginActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                Toasty.success(getActivity(), "Logged out successfully!", Toast.LENGTH_SHORT).show();
                            }
                        }).onNegative(new BottomDialog.ButtonCallback() {
                    @Override
                    public void onClick(@NonNull BottomDialog bottomDialog) {
                        bottomDialog.dismiss();
                    }
                })
                        .show();

                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            ((MainActivity) getActivity()).setActionBarTitle(titleFragment);
            mDrawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public interface FragmentDrawerListener {
        void onDrawerItemSelected(View view, int position);
    }


//    @Override
//    public void onResume() {
//        super.onResume();
//        new FetchDependency().execute();
//        drawerItemCustomAdapter.notifyDataSetChanged();
//    }

    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute() {
        }

        protected String doInBackground(String... arg0) {
            try {

                URL url = new URL(Constants.URL + "authenticate"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", Prefs.getString("USERNAME", null));
                postDataParams.put("password", Prefs.getString("PASSWORD", null));
                Log.e("params", postDataParams.toString());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                //MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    Log.d("ifresponseCode", "" + responseCode);
                    BufferedReader in = new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    in.close();
                    return sb.toString();
                } else {
                    if (responseCode == 400) {
                        Log.d("cameInThisBlock", "true");
                        responseCodeForShow = 400;
                    } else if (responseCode == 405) {
                        responseCodeForShow = 405;
                    } else if (responseCode == 302) {
                        responseCodeForShow = 302;
                    }
                    Log.d("elseresponseCode", "" + responseCode);
                    return new String("false : " + responseCode);
                }
            } catch (Exception e) {
                return new String("Exception: " + e.getMessage());
            }

        }


        @Override
        protected void onPostExecute(String result) {
            Log.d("resultFromNewCall", result);
            if (isAdded()) {
                if (responseCodeForShow == 400) {
                    final Toast toast = Toasty.info(getActivity(), getString(R.string.urlchange), Toast.LENGTH_SHORT);
                    toast.show();
                    new CountDownTimer(10000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            toast.show();
                        }

                        public void onFinish() {
                            toast.cancel();
                        }
                    }.start();
                    Prefs.clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }


                if (responseCodeForShow == 405) {
                    final Toast toast = Toasty.info(getActivity(), getString(R.string.urlchange), Toast.LENGTH_SHORT);
                    toast.show();
                    new CountDownTimer(10000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            toast.show();
                        }

                        public void onFinish() {
                            toast.cancel();
                        }
                    }.start();
                    Prefs.clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }


                if (responseCodeForShow == 302) {
                    final Toast toast = Toasty.info(getActivity(), getString(R.string.urlchange), Toast.LENGTH_SHORT);
                    toast.show();
                    new CountDownTimer(10000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            toast.show();
                        }

                        public void onFinish() {
                            toast.cancel();
                        }
                    }.start();
                    Prefs.clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                    return;
                }
            }


            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                token = jsonObject1.getString("token");
                JSONObject jsonObject2 = jsonObject1.getJSONObject("user");
                String role = jsonObject2.getString("role");
                String firstName = jsonObject2.getString("first_name");
                String lastName = jsonObject2.getString("last_name");
                String userName = jsonObject2.getString("user_name");
                String email = jsonObject2.getString("email");
                String clientname;
                if (firstName == null || firstName.equals(""))
                    clientname = userName;
                else
                    clientname = firstName + " " + lastName;
                Prefs.putString("clientNameForFeedback", clientname);
                Prefs.putString("emailForFeedback", email);
                Prefs.putString("PROFILE_NAME", clientname);
                Prefs.putString("TOKEN", token);
                Log.d("TOKEN", token);
                try {
                    String letter = Prefs.getString("profilePicture", null);
                    Log.d("profilePicture", letter);
                    if (letter.contains("jpg") || letter.contains("png") || letter.contains("jpeg")) {
                        //profilePic.setColorFilter(getContext().getResources().getColor(R.color.white));
                        //profilePic.setColorFilter(context.getResources().getColor(R.color.faveo), PorterDuff.Mode.SRC_IN);
                        Picasso.with(context).load(letter).transform(new CircleTransform()).into(profilePic);
                    } else {
                        int color = Color.parseColor("#ffffff");
                        String letter1 = String.valueOf(Prefs.getString("PROFILE_NAME", "").charAt(0));
                        ColorGenerator generator = ColorGenerator.MATERIAL;
                        TextDrawable drawable = TextDrawable.builder()
                                .buildRound(letter1, color);
                        //profilePic.setAlpha(0.2f);
                        profilePic.setColorFilter(context.getResources().getColor(R.color.faveo), PorterDuff.Mode.SRC_IN);
                        profilePic.setImageDrawable(drawable);
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
                userRole.setText(Prefs.getString("ROLE", ""));
                domainAddress.setText(Prefs.getString("BASE_URL", ""));
                userNameText.setText(Prefs.getString("PROFILE_NAME", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class FetchDependency extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //progressDialog.setMessage("Loading your information");
        }

        protected String doInBackground(String... urls) {

            return new Helpdesk().getDependency();

        }

        protected void onPostExecute(String result) {
            Log.d("Depen Response : ", result + "");
            progressDialog.dismiss();

            if (result == null) {

                return;
            }
            String state = Prefs.getString("403", null);
            try {
                if (state.equals("403") && !state.equals(null)) {
                    //Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    Prefs.putString("403", "null");
                    startActivity(intent);
                    return;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {

                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");

                int open = 0, closed = 0, trash = 0, unasigned = 0, my_tickets = 0;
                JSONArray jsonArrayTicketsCount = jsonObject1.getJSONArray("tickets_count");
                for (int i = 0; i < jsonArrayTicketsCount.length(); i++) {
                    String name = jsonArrayTicketsCount.getJSONObject(i).getString("name");
                    String count = jsonArrayTicketsCount.getJSONObject(i).getString("count");

                    switch (name) {
                        case "Open":
                            open = Integer.parseInt(count);
                            break;
                        case "Closed":
                            closed = Integer.parseInt(count);
                            break;
                        case "Deleted":
                            trash = Integer.parseInt(count);
                            break;
                        case "unassigned":
                            unasigned = Integer.parseInt(count);
                            break;
                        case "mytickets":
                            my_tickets = Integer.parseInt(count);
                            break;
                        default:
                            break;

                    }
                }


                if (open > 999)
                    Prefs.putString("inboxTickets", "999+");
                else
                    Prefs.putString("inboxTickets", open + "");

                if (closed > 999)
                    Prefs.putString("closedTickets", "999+");
                else
                    Prefs.putString("closedTickets", closed + "");

                if (my_tickets > 999)
                    Prefs.putString("myTickets", "999+");
                else
                    Prefs.putString("myTickets", my_tickets + "");

                if (trash > 999)
                    Prefs.putString("trashTickets", "999+");
                else
                    Prefs.putString("trashTickets", trash + "");

                if (unasigned > 999)
                    Prefs.putString("unassignedTickets", "999+");
                else
                    Prefs.putString("unassignedTickets", unasigned + "");
                if (isAdded()) {
                    drawerItem[0] = new DataModel(R.drawable.inbox_tickets, getString(R.string.mytickets), Prefs.getString("openCount", null));
                    drawerItem[1] = new DataModel(R.drawable.closed_ticket, getString(R.string.myclosed), Prefs.getString("closeCount", null));
                    drawerItemCustomAdapter = new DrawerItemCustomAdapter(getActivity(), R.layout.list_view_item_row, drawerItem);
                    listView.setAdapter(drawerItemCustomAdapter);
                    drawerItemCustomAdapter.notifyDataSetChanged();
                    UIUtils.setListViewHeightBasedOnItems(listView);
//                drawerItemCustomAdapter.notifyDataSetChanged();
//

                }
//                else{
//                    Toasty.warning(getActivity(),"Something went wrong",Toast.LENGTH_LONG).show();
//                }
            } catch (JSONException e) {
                String state1 = Prefs.getString("400", null);

                try {
                    if (state1.equals("badRequest")) {
                        Toasty.info(getActivity(), getString(R.string.apiDisabled), Toast.LENGTH_LONG).show();
                    } else {
                        Toasty.error(getActivity(), "Parsing Error!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                } catch (NullPointerException e1) {
                    e1.printStackTrace();
                }


            }
        }
    }

    public class DrawerItemCustomAdapter extends ArrayAdapter<DataModel> {
        Context mContext;
        int layoutResourceId;
        DataModel data[] = null;

        public DrawerItemCustomAdapter(Context mContext, int layoutResourceId, DataModel[] data) {

            super(mContext, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.mContext = mContext;
            this.data = data;
        }

        @Override
        public View getView(final int position, final View convertView, ViewGroup parent) {

            View listItem = convertView;

            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            listItem = inflater.inflate(layoutResourceId, parent, false);
            ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageView2);
            final TextView textViewName = (TextView) listItem.findViewById(R.id.inboxtv);
            TextView countNumber = (TextView) listItem.findViewById(R.id.inbox_count);

            DataModel folder = data[position];
            if (option == 0) {
                if (position == 0) {
                    listItem.setBackgroundColor(getResources().getColor(R.color.grey_200));
                    textViewName.setTextColor(getResources().getColor(R.color.faveo));
                    imageViewIcon.setColorFilter(getResources().getColor(R.color.faveo));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

//                    linearSettings.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    textViewsetting.setTextColor(getResources().getColor(R.color.black));
//                    imageViewsettimg.setColorFilter(getResources().getColor(R.color.grey_500));

                    linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewClientList.setTextColor(getResources().getColor(R.color.black));
                    clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                    textviewabout.setTextColor(getResources().getColor(R.color.black));
                    imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }

            } else if (option == 1) {
                if (position == 1) {
                    listItem.setBackgroundColor(getResources().getColor(R.color.grey_200));
                    textViewName.setTextColor(getResources().getColor(R.color.faveo));
                    imageViewIcon.setColorFilter(getResources().getColor(R.color.faveo));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

//                    linearSettings.setBackgroundColor(getResources().getColor(R.color.colorAccent));
//                    textViewsetting.setTextColor(getResources().getColor(R.color.black));
//                    imageViewsettimg.setColorFilter(getResources().getColor(R.color.grey_500));

                    linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewClientList.setTextColor(getResources().getColor(R.color.black));
                    clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                    textviewabout.setTextColor(getResources().getColor(R.color.black));
                    imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }

            } else if (option == 2) {
                if (position == 2) {
                    listItem.setBackgroundColor(getResources().getColor(R.color.grey_200));
                    textViewName.setTextColor(getResources().getColor(R.color.faveo));
                    imageViewIcon.setColorFilter(getResources().getColor(R.color.faveo));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewClientList.setTextColor(getResources().getColor(R.color.black));
                    clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                    textviewabout.setTextColor(getResources().getColor(R.color.black));
                    imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }

            } else if (option == 3) {
                if (position == 3) {
                    listItem.setBackgroundColor(getResources().getColor(R.color.grey_200));
                    textViewName.setTextColor(getResources().getColor(R.color.faveo));
                    imageViewIcon.setColorFilter(getResources().getColor(R.color.faveo));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewClientList.setTextColor(getResources().getColor(R.color.black));
                    clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                    textviewabout.setTextColor(getResources().getColor(R.color.black));
                    imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }

            } else if (option == 4) {
                if (position == 4) {
                    listItem.setBackgroundColor(getResources().getColor(R.color.grey_200));
                    textViewName.setTextColor(getResources().getColor(R.color.faveo));
                    imageViewIcon.setColorFilter(getResources().getColor(R.color.faveo));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewClientList.setTextColor(getResources().getColor(R.color.black));
                    clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                    textviewabout.setTextColor(getResources().getColor(R.color.black));
                    imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }

            } else if (option == 5) {
                if (position == 0) {
                    listItem.setBackgroundColor(getResources().getColor(R.color.grey_200));
                    textViewName.setTextColor(getResources().getColor(R.color.faveo));
                    imageViewIcon.setColorFilter(getResources().getColor(R.color.faveo));
                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                    textviewlogout.setTextColor(getResources().getColor(R.color.black));
                    imageViewlogout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLog.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    linearClientList.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                    textViewClientList.setTextColor(getResources().getColor(R.color.black));
                    clientImage.setColorFilter(getResources().getColor(R.color.grey_500));
                    textviewabout.setTextColor(getResources().getColor(R.color.black));
                    imageviewabout.setColorFilter(getResources().getColor(R.color.grey_500));
                    linearLayoutAbout.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                }
            } else {
                listItem.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                textViewName.setTextColor(getResources().getColor(R.color.black));
                imageViewIcon.setColorFilter(getResources().getColor(R.color.grey_500));
            }
            imageViewIcon.setImageResource(folder.getIcon());
            textViewName.setText(folder.getName());
            countNumber.setText(folder.getCount());
            drawerItemCustomAdapter.notifyDataSetChanged();


            return listItem;
        }

    }

    private class FetchFirst extends AsyncTask<String, Void, String> {
        Context context;

        FetchFirst(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... urls) {
//            if (nextPageURL.equals("null")) {
//                return "all done";
//            }
            String result = new Helpdesk().getTicketsByAgent();
            if (result == null)
                return null;
            String data;
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                //total = jsonObject1.getInt("total");
//                try {
//                    data = jsonObject1.getString("data");
//                } catch (JSONException e) {
//                    data = jsonObject.getString("result");
//                }
                JSONArray jsonArray = jsonObject1.getJSONArray("ticket");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                    String status = jsonObject2.getString("status_name");
                    if (status.equals("Open")) {
                        opencount++;

                    } else if (status.equals("Closed")) {

                        closecount++;

                    }

                }
                Prefs.putString("openCount", String.valueOf(opencount));
                Prefs.putString("closeCount", String.valueOf(closecount));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "success";
        }

        protected void onPostExecute(String result) {
            new FetchDependency().execute();
            try {
                String methodNotAllowed = Prefs.getString("MethodNotAllowed", null);

                if (methodNotAllowed.equalsIgnoreCase("true")) {
                    Prefs.clear();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            Activity activity = getActivity();
            if (isAdded() && activity != null) {
                if (result == null) {
                    Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    return;
                }
            }

            try {
                if (result.equals("all done")) {

                    Toasty.info(context, getString(R.string.all_caught_up), Toast.LENGTH_SHORT).show();
                    //return;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }
}

