package co.faveo.helpdesk.pro.client.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.faveo.helpdesk.pro.client.R;
import co.faveo.helpdesk.pro.client.application.Helpdesk;
import co.faveo.helpdesk.pro.client.fragments.Conversation;
import co.faveo.helpdesk.pro.client.fragments.Details;
import co.faveo.helpdesk.pro.client.model.Data;
import es.dmoral.toasty.Toasty;

public class TicketDetailActivity extends AppCompatActivity implements Conversation.OnFragmentInteractionListener,
        Details.OnFragmentInteractionListener, View.OnClickListener {
    public ViewPagerAdapter adapter;
    ImageView imageViewBack, imageViewSource;
    FloatingActionButton floatingActionButton;
    ViewPager viewPager;
    Conversation fragmentConversation;
    Details fragmentDetail;
    View viewpriority, viewCollapsePriority;
    ProgressDialog progressDialog;
    String status;
    ArrayList<Data> statusItems;
    LoaderTextView loaderTextViewusername, loaderTextViewStatus, loaderTextViewTitle, loaderTextViewNumber, loaderTextViewDepartment;
    String ticketSubject, ticketNumberMain, userName, ticketStatus, ticketPriorityColor, departmentname, sourcename;
    String ticketId;
    TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition(), true);

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };
    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //floatingActionButton.setRotation(positionOffset * 180.0f);

        }

        /**
         * This method is for controlling the FAB button.
         * @param position of the FAB button.
         */
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    //floatingActionButton.show();
                    break;

                case 1:
                    //floatingActionButton.hide();
                    break;
                default:
                    //floatingActionButton.show();
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
    private int id = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);

        overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_in_from_right);
        Window window = TicketDetailActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(TicketDetailActivity.this, R.color.mainActivityTopBar));
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarTicketDetail);
        setSupportActionBar(mToolbar);
        statusItems = new ArrayList<>();
        progressDialog = new ProgressDialog(this);
        imageViewBack = (ImageView) findViewById(R.id.imageViewBackTicketDetail);
        imageViewSource = findViewById(R.id.imageView_default_profile);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_add);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        loaderTextViewusername = (LoaderTextView) mAppBarLayout.findViewById(R.id.textViewagentName);
        loaderTextViewStatus = (LoaderTextView) mAppBarLayout.findViewById(R.id.status);
        loaderTextViewTitle = (LoaderTextView) mToolbar.findViewById(R.id.subject);
        loaderTextViewNumber = (LoaderTextView) mAppBarLayout.findViewById(R.id.title);
        viewpriority = mToolbar.findViewById(R.id.viewPriority);
        loaderTextViewDepartment = findViewById(R.id.department);
        viewCollapsePriority = mAppBarLayout.findViewById(R.id.viewPriority1);
        setupViewPager();
        JSONObject jsonObject1;
        String json = Prefs.getString("DEPENDENCY", "");
        try {
            jsonObject1 = new JSONObject(json);
            JSONArray jsonArrayHelpTopics = jsonObject1.getJSONArray("status");
            for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {
                Data data1 = new Data(Integer.parseInt(jsonArrayHelpTopics.getJSONObject(i).getString("id")), jsonArrayHelpTopics.getJSONObject(i).getString("name"));
                statusItems.add(data1);
                //menu.add("First Menu");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(onTabSelectedListener);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TicketDetailActivity.this, TicketReplyActivity.class);
                startActivity(intent);
            }
        });
        final Intent intent = getIntent();
        ticketId = intent.getStringExtra("ticket_id");
        ticketNumberMain = intent.getStringExtra("ticket_number");
        ticketPriorityColor = intent.getStringExtra("ticket_priority");
        ticketSubject = intent.getStringExtra("ticket_subject");
        ticketStatus = intent.getStringExtra("ticket_status");
        userName = intent.getStringExtra("ticket_opened_by");
        departmentname = intent.getStringExtra("department");
        sourcename = intent.getStringExtra("source");
        try {
            viewpriority.setBackgroundColor(Color.parseColor(ticketPriorityColor));
            Log.d("status", ticketStatus);
            viewCollapsePriority.setBackgroundColor(Color.parseColor(ticketPriorityColor));
            loaderTextViewusername.setText(userName);
            loaderTextViewStatus.setText(ticketStatus);
            loaderTextViewTitle.setText(ticketNumberMain);
            loaderTextViewNumber.setText(ticketSubject);
            loaderTextViewDepartment.setText(departmentname);

        } catch (NullPointerException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        switch (sourcename) {
            case "chat":
            case "Chat":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.ic_chat_bubble_outline_black_24dp);
                imageViewSource.setColorFilter(color);
                break;
            }
            case "web":
            case "Web":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.web_design);
                imageViewSource.setColorFilter(color);
                break;
            }
            case "agent":
            case "Agent":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.mail);
                imageViewSource.setColorFilter(color);
                break;
            }
            case "email":
            case "Email":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.mail);
                imageViewSource.setColorFilter(color);
                break;
            }
            case "facebook":
            case "Facebook":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.facebook);
                imageViewSource.setColorFilter(color);
                break;
            }
            case "twitter":
            case "Twitter":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.twitter);
                imageViewSource.setColorFilter(color);
                break;
            }
            case "call":
            case "Call":{
                int color = Color.parseColor(ticketPriorityColor);
                imageViewSource.setImageResource(R.drawable.phone);
                imageViewSource.setColorFilter(color);
                break;
            }
            default:
                imageViewSource.setVisibility(View.GONE);
                break;
        }
    }

    private void setupViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentConversation = new Conversation();
        fragmentDetail = new Details();
        adapter.addFragment(fragmentConversation, getString(R.string.conversation));
        adapter.addFragment(fragmentDetail, getString(R.string.detail));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(onPageChangeListener);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        for (int i = 0; i < statusItems.size(); i++) {
            Data data = statusItems.get(i);
            menu.add(data.getName());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //int id1 = item.getItemId();
        //int id1 = item.getItemId();
        status = Prefs.getString("ticketstatus", null);
        Log.d("status", status);

        for (int i = 0; i < statusItems.size(); i++) {
            Data data = statusItems.get(i);
            if (data.getName().equals(item.toString())) {
                id = data.getID();
                Log.d("ID", "" + id);
            }
        }
        try {
            status = Prefs.getString("ticketstatus", null);
            if (status.equalsIgnoreCase(item.toString())) {
                Toasty.warning(TicketDetailActivity.this, "Ticket is already in " + item.toString() + " state", Toast.LENGTH_SHORT).show();
            } else {
                try {

                    new BottomDialog.Builder(TicketDetailActivity.this)
                            .setContent(getString(R.string.statusConfirmation))
                            .setTitle("Changing status")
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
                                    progressDialog.show();
                                    progressDialog.setMessage(getString(R.string.pleasewait));
                                    new StatusChange(ticketId, id).execute();
                                    Prefs.putString("tickets", null);
                                }
                            }).onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            bottomDialog.dismiss();
                        }
                    })
                            .show();

                } catch (NumberFormatException e) {
                    e.printStackTrace();

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    /**
     * Async task for changing the status of the ticket.
     */
    private class StatusChange extends AsyncTask<String, Void, String> {
        String ticketId;
        int statusId;

        StatusChange(String ticketId, int statusId) {

            this.ticketId = ticketId;
            this.statusId = statusId;

        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().postStatusChanged(ticketId, statusId);
            //return new Helpdesk().postStatusChanged(ticketId,statusId);
        }

        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
            //progressDialog.dismiss();
//            if (result == null) {
//                Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
            try {
                String state = Prefs.getString("403", null);
                if (state.equals("403") && !state.equals("null")) {
                    Toasty.warning(TicketDetailActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("message");
                for (int i = 0; i < jsonArray.length(); i++) {
                    String message = jsonArray.getString(i);
                    if (message.contains("Permission denied")) {
                        Toasty.warning(TicketDetailActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                        Prefs.putString("403", "null");
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {

                JSONObject jsonObject = new JSONObject(result);
                //JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                //JSONObject jsonObject2=jsonObject.getJSONObject("error");
                //String message1=jsonObject2.getString("ticket_id");
                String message2 = jsonObject.getString("message");


//                if (message2.contains("permission denied")&&Prefs.getString("403",null).equals("403")){
//
//                }
                if (!message2.equals("null")) {
                    Toasty.success(TicketDetailActivity.this, getString(R.string.successfullyChanged), Toast.LENGTH_LONG).show();
                    Prefs.putString("ticketstatus", "Deleted");
                    finish();
                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));

                }

//                if (message2.contains("Status changed to Deleted")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_deleted), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Deleted");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                } else if (message2.contains("Status changed to Open")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_opened), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Open");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                } else if (message2.contains("Status changed to Closed")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_closed), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Closed");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                } else if (message2.contains("Status changed to Resolved")) {
//                    Toasty.success(TicketDetailActivity.this, getString(R.string.status_resolved), Toast.LENGTH_LONG).show();
//                    Prefs.putString("ticketstatus", "Resolved");
//                    finish();
//                    startActivity(new Intent(TicketDetailActivity.this, MainActivity.class));
//                }
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();

            }


        }


    }

//    private class FetchTicketDetail extends AsyncTask<String, Void, String> {
//        String ticketID;
//        String agentName;
//        String title;
//        FetchTicketDetail(String ticketID) {
//
//            this.ticketID = ticketID;
//        }
//
//        protected String doInBackground(String... urls) {
//            return new Helpdesk().getTicketDetail(ticketID);
//        }
//
//        protected void onPostExecute(String result) {
//            //progressBar.setVisibility(View.GONE);
//            if (isCancelled()) return;
////            if (progressDialog.isShowing())
////                progressDialog.dismiss();
//
//            if (result == null) {
//                //Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            JSONObject jsonObject = null;
//            try {
//                jsonObject = new JSONObject(result);
//                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
//                JSONObject jsonObject2=jsonObject1.getJSONObject("ticket");
//                String ticketNumber=jsonObject2.getString("ticket_number");
//                String statusName=jsonObject2.getString("status_name");
//                String subject=jsonObject2.getString("title");
//                String department=jsonObject2.getString("dept_name");
//                String priorityColor=jsonObject2.getString("priority_color");
//                if (!priorityColor.equals("")||!priorityColor.equals("null")){
//                    viewpriority.setBackgroundColor(Color.parseColor(priorityColor));
//                    viewCollapsePriority.setBackgroundColor(Color.parseColor(priorityColor));
//                }
//                else{
//                    viewpriority.setVisibility(View.GONE);
//                    viewCollapsePriority.setVisibility(View.GONE);
//                }
//                JSONObject jsonObject3=jsonObject2.getJSONObject("from");
//                String userName = jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name");
//                if (userName.equals("")||userName.equals("null null")||userName.equals(" ")){
//                    userName=jsonObject3.getString("user_name");
//                    loaderTextViewusername.setText(userName);
//                }
//                else{
//                    userName=jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name");
//                    loaderTextViewusername.setText(userName);
//                }
//                if (!statusName.equals("null")||!statusName.equals("")){
//                    loaderTextViewStatus.setText(statusName);
//                }
//                else{
//                    loaderTextViewStatus.setVisibility(View.GONE);
//                }
//                loaderTextViewNumber.setText(ticketNumber);
//                if (subject.startsWith("=?")){
//                    title=subject.replaceAll("=?UTF-8?Q?","");
//                    String newTitle=title.replaceAll("=E2=80=99","");
//                    String second1=newTitle.replace("=C3=BA","");
//                    String third = second1.replace("=C2=A0", "");
//                    String finalTitle=third.replace("=??Q?","");
//                    String newTitle1=finalTitle.replace("?=","");
//                    String newTitle2=newTitle1.replace("_"," ");
//                    Log.d("new name",newTitle2);
//                    loaderTextViewTitle.setText(newTitle2);
//                }
//                else if (!subject.equals("null")){
//                    loaderTextViewTitle.setText(subject);
//                }
//                else if (subject.equals("null")){
//                    loaderTextViewTitle.setText("");
//                }
//
//                Log.d("TITLE",subject);
//                Log.d("TICKETNUMBER",ticketNumber);
//                //String priority=jsonObject1.getString("priority_id");
//
//
//
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

}
