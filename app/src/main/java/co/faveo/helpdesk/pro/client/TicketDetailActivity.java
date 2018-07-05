package co.faveo.helpdesk.pro.client;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.elyeproj.loaderviewlibrary.LoaderTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class TicketDetailActivity extends AppCompatActivity implements Conversation.OnFragmentInteractionListener,
        Details.OnFragmentInteractionListener,View.OnClickListener{
    ImageView imageViewBack;
    FloatingActionButton floatingActionButton;
    ViewPager viewPager;
    public ViewPagerAdapter adapter;
    Conversation fragmentConversation;
    Details fragmentDetail;
    View viewpriority,viewCollapsePriority;
    ArrayList<Data> statusItems;
    LoaderTextView loaderTextViewusername,loaderTextViewStatus,loaderTextViewTitle,loaderTextViewNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_detail);
        Window window = TicketDetailActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(TicketDetailActivity.this,R.color.faveo));
        AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbarTicketDetail);
        setSupportActionBar(mToolbar);
        imageViewBack= (ImageView) findViewById(R.id.imageViewBackTicketDetail);
        floatingActionButton= (FloatingActionButton) findViewById(R.id.fab_add);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        loaderTextViewusername= (LoaderTextView) mAppBarLayout.findViewById(R.id.textViewagentName);
        loaderTextViewStatus= (LoaderTextView) mAppBarLayout.findViewById(R.id.status);
        loaderTextViewTitle= (LoaderTextView) mToolbar.findViewById(R.id.subject);
        loaderTextViewNumber= (LoaderTextView) mAppBarLayout.findViewById(R.id.title);
        viewpriority=mToolbar.findViewById(R.id.viewPriority);
        viewCollapsePriority=mAppBarLayout.findViewById(R.id.viewPriority1);
        setupViewPager();
        String json1 = Prefs.getString("DEPENDENCY", "");
        //statusItems.add(new Data(0, "Please select help topic"));

        statusItems=new ArrayList<>();
        JSONObject jsonObject1;
        try {
            jsonObject1 = new JSONObject(json1);
            JSONArray jsonArrayHelpTopics = jsonObject1.getJSONArray("status");
            for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {
                Data data1 = new Data(Integer.parseInt(jsonArrayHelpTopics.getJSONObject(i).getString("id")), jsonArrayHelpTopics.getJSONObject(i).getString("name"));
                statusItems.add(data1);
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
                Intent intent=new Intent(TicketDetailActivity.this,TicketReplyActivity.class);
                startActivity(intent);
                //Toasty.info(TicketDetailActivity.this,"clicked", Toast.LENGTH_LONG).show();
            }
        });
        if (InternetReceiver.isConnected()){
//            progressDialog=new ProgressDialog(this);
//            progressDialog.setMessage(getString(R.string.pleasewait));
//            progressDialog.show();
            Log.d("threadisrunning","true");
                    new FetchTicketDetail(Prefs.getString("TICKETid",null)).execute();
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
            floatingActionButton.setRotation(positionOffset * 180.0f);

        }
        /**
         * This method is for controlling the FAB button.
         * @param position of the FAB button.
         */
        @Override
        public void onPageSelected(int position) {
            switch (position) {
                case 0:
                    floatingActionButton.show();
                    break;

                case 1:
                    floatingActionButton.hide();
                    break;
                default:
                    floatingActionButton.show();
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

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

    private class FetchTicketDetail extends AsyncTask<String, Void, String> {
        String ticketID;
        String agentName;
        String title;
        FetchTicketDetail(String ticketID) {

            this.ticketID = ticketID;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getTicketDetail(ticketID);
        }

        protected void onPostExecute(String result) {
            //progressBar.setVisibility(View.GONE);
            if (isCancelled()) return;
//            if (progressDialog.isShowing())
//                progressDialog.dismiss();

            if (result == null) {
                //Toasty.error(TicketDetailActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONObject jsonObject2=jsonObject1.getJSONObject("ticket");
                String ticketNumber=jsonObject2.getString("ticket_number");
                String statusName=jsonObject2.getString("status_name");
                String subject=jsonObject2.getString("title");
                String department=jsonObject2.getString("dept_name");
                String priorityColor=jsonObject2.getString("priority_color");
                if (!priorityColor.equals("")||!priorityColor.equals("null")){
                    viewpriority.setBackgroundColor(Color.parseColor(priorityColor));
                    viewCollapsePriority.setBackgroundColor(Color.parseColor(priorityColor));
                }
                else{
                    viewpriority.setVisibility(View.GONE);
                    viewCollapsePriority.setVisibility(View.GONE);
                }
                JSONObject jsonObject3=jsonObject2.getJSONObject("from");
                String userName = jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name");
                if (userName.equals("")||userName.equals("null null")||userName.equals(" ")){
                    userName=jsonObject3.getString("user_name");
                    loaderTextViewusername.setText(userName);
                }
                else{
                    userName=jsonObject3.getString("first_name")+" "+jsonObject3.getString("last_name");
                    loaderTextViewusername.setText(userName);
                }
                if (!statusName.equals("null")||!statusName.equals("")){
                    loaderTextViewStatus.setText(statusName);
                }
                else{
                    loaderTextViewStatus.setVisibility(View.GONE);
                }
                loaderTextViewNumber.setText(ticketNumber);
                if (subject.startsWith("=?")){
                    title=subject.replaceAll("=?UTF-8?Q?","");
                    String newTitle=title.replaceAll("=E2=80=99","");
                    String second1=newTitle.replace("=C3=BA","");
                    String third = second1.replace("=C2=A0", "");
                    String finalTitle=third.replace("=??Q?","");
                    String newTitle1=finalTitle.replace("?=","");
                    String newTitle2=newTitle1.replace("_"," ");
                    Log.d("new name",newTitle2);
                    loaderTextViewTitle.setText(newTitle2);
                }
                else if (!subject.equals("null")){
                    loaderTextViewTitle.setText(subject);
                }
                else if (subject.equals("null")){
                    loaderTextViewTitle.setText("");
                }

                Log.d("TITLE",subject);
                Log.d("TICKETNUMBER",ticketNumber);
                //String priority=jsonObject1.getString("priority_id");





            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
