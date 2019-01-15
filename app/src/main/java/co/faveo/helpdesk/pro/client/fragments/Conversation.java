package co.faveo.helpdesk.pro.client.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.ArrayList;
import java.util.List;

import co.faveo.helpdesk.pro.client.R;
import co.faveo.helpdesk.pro.client.activity.ShowingAttachment;
import co.faveo.helpdesk.pro.client.activity.TicketDetailActivity;
import co.faveo.helpdesk.pro.client.activity.TicketReplyActivity;
import co.faveo.helpdesk.pro.client.application.Helpdesk;
import co.faveo.helpdesk.pro.client.application.Helper;
import co.faveo.helpdesk.pro.client.dialog.CircleTransform;
import co.faveo.helpdesk.pro.client.model.TicketThread;
import co.faveo.helpdesk.pro.client.receiver.InternetReceiver;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Conversation.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Conversation#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Conversation extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    AsyncTask<String, Void, String> task;
    ShimmerRecyclerView recyclerView;
    TextView empty_view;
    TextView noInternet_view;
    TextView textView;
    View rootView, view;
    ProgressDialog progressDialog;
    TicketThreadAdapter ticketThreadAdapter;
    List<TicketThread> ticketThreadList = new ArrayList<>();
    List<TicketThread> ticketThreads=new ArrayList<>();
    SwipeRefreshLayout swipeRefresh;
    String file;
    String type;
    String noOfAttachment;
    StringBuilder stringBuilderName;
    StringBuilder stringBuilderFile;
    String name;
    int count=0;
    FloatingActionButton fab;
    private String mParam1;
    private String mParam2;
    public static final int ALL_ROW = 0;

    public static final int LIMITED_ROW = 1;
    private OnFragmentInteractionListener mListener;

    public Conversation() {

    }

    public static Conversation newInstance(String param1, String param2) {
        Conversation fragment = new Conversation();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            Log.d("calledOnCreate", "true");
            rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
            fab = (FloatingActionButton) ((TicketDetailActivity) getActivity()).findViewById(R.id.fab_add);
            //fab.setVisibility(View.GONE);
            recyclerView = (ShimmerRecyclerView) rootView.findViewById(R.id.cardList);
            empty_view = (TextView) rootView.findViewById(R.id.empty_view);
            noInternet_view = (TextView) rootView.findViewById(R.id.noiternet_view);
            textView = (TextView) rootView.findViewById(R.id.totalcount);
            swipeRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
            textView.setVisibility(View.GONE);
            swipeRefresh.setColorSchemeResources(R.color.faveo_blue);
            swipeRefresh.setRefreshing(false);
            swipeRefresh.setEnabled(false);


            //            swipeRefresh.setRefreshing(true);
//            new FetchFirst(getActivity()).execute();
            if (InternetReceiver.isConnected()) {
                noInternet_view.setVisibility(View.GONE);
                // swipeRefresh.setRefreshing(true);
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(getString(R.string.pleasewait));
                //swipeRefresh.setRefreshing(true);
                textView.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(true);
                task = new FetchTicketThreads(getActivity(), Prefs.getString("TICKETid", null));
                task.execute();
            } else {
                noInternet_view.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                empty_view.setVisibility(View.GONE);
            }
//
//            swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//                @Override
//                public void onRefresh() {
//                    if (InternetReceiver.isConnected()) {
////                        loading = true;
//                        recyclerView.setVisibility(View.VISIBLE);
//                        noInternet_view.setVisibility(View.GONE);
//
//                        if (ticketThreadList.size() != 0) {
//                            ticketThreadList.clear();
//                            ticketThreadAdapter.notifyDataSetChanged();
////                            progressDialog=new ProgressDialog(getActivity());
////                            progressDialog.setMessage(getString(R.string.pleasewait));
////                            progressDialog.show();
//                            task = new FetchTicketThreads(getActivity(),Prefs.getString("TICKETid", null));
//                            task.execute();
//                        }
//                    } else {
//                        recyclerView.setVisibility(View.INVISIBLE);
//                        swipeRefresh.setRefreshing(false);
//                        empty_view.setVisibility(View.GONE);
//                        noInternet_view.setVisibility(View.VISIBLE);
//                    }
//
//                }
//            });
        }

//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                super.onScrolled(recyclerView, dx, dy);
//                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
//                    fab.setVisibility(View.GONE);
//                } else if (dy < 0 && fab.getVisibility()==View.GONE) {
//                    fab.setVisibility(View.VISIBLE);
//                    //fab.show();
//                }
//            }
//        });

        return rootView;
    }

    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down);

        recyclerView.setLayoutAnimation(controller);
        ticketThreadAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        Log.d("calledOnStart", "true");
        super.onStart();
//            noInternet_view.setVisibility(View.GONE);
        // swipeRefresh.setRefreshing(true);
        //Log.d("TICKETid",Prefs.getString("TICKETid", null));
        //refresh();
//            task = new FetchTicketThreads(getActivity(),Prefs.getString("TICKETid", null));
//            task.execute();
    }

    @Override
    public void onResume() {
        Log.d("calledOnResume", "true");
//        refresh();
        super.onResume();
    }


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }


//    @Override
//    public void onPause() {
//        if (task != null && task.getStatus() == AsyncTask.Status.RUNNING) {
//            task.cancel(true);
//            Log.d("Async Detail", "Cancelled");
//        }
//        super.onPause();
//    }

    class FetchTicketThreads extends AsyncTask<String, Void, String> {
        Context context;
        String ticketID;


        FetchTicketThreads(Context context, String ticketID) {
            this.context = context;
            this.ticketID = ticketID;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getTicketThread(ticketID);
        }

        protected void onPostExecute(String result) {
            swipeRefresh.setRefreshing(false);
            textView.setVisibility(View.VISIBLE);
            try {
                swipeRefresh.setRefreshing(false);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            textView.setVisibility(View.GONE);
            if (swipeRefresh.isRefreshing())
                swipeRefresh.setRefreshing(false);
            if (result == null) {
                //Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                JSONArray jsonArray = jsonObject1.getJSONArray("threads");
                for (int i = 0; i < jsonArray.length(); i++) {
                    TicketThread ticketThread = null;
                    try {
                        String clientPicture = null;
                        try {
                            clientPicture = jsonArray.getJSONObject(i).getJSONObject("user").getString("profile_pic");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        String isInternal = jsonArray.getJSONObject(i).getString("is_internal");

                        if (isInternal.equals("0")) {
                            String ticketId = jsonArray.getJSONObject(i).getString("ticket_id");
                            String firstName = jsonArray.getJSONObject(i).getJSONObject("user").getString("first_name");
                            String userName = jsonArray.getJSONObject(i).getJSONObject("user").getString("user_name");
                            String lastName = jsonArray.getJSONObject(i).getJSONObject("user").getString("last_name");
                            JSONArray jsonArray1 = jsonArray.getJSONObject(i).getJSONArray("attach");
                            stringBuilderName = new StringBuilder();
                            stringBuilderFile = new StringBuilder();
                            if (jsonArray1.length() == 0) {
                                Prefs.putString("imageBase64", "no attachment");
                                Log.d("FileBase64", "no attachment");
                                name = "";
                            } else {
                                for (int j = 0; j < jsonArray1.length(); j++) {
                                    JSONObject jsonObject5 = jsonArray1.getJSONObject(j);
                                    file = jsonObject5.getString("file");
                                    name = jsonObject5.getString("name");
                                    //stringBuilderFile=new StringBuilder(file);
                                    stringBuilderName.append(name + ",");
                                    stringBuilderFile.append(file + ",");
                                    //stringBuilderFile.append(file+",");
                                    type = jsonObject5.getString("type");
                                }
                                //Log.d("FileBase64", file);
                                name = stringBuilderName.toString();
                                file = stringBuilderFile.toString();
                                Log.d("MultipleFileName", stringBuilderName.toString());
                                noOfAttachment = "" + jsonArray1.length();
                                Log.d("Total Attachments", "" + jsonArray1.length());
                            }

                            String clientName = firstName + " " + lastName;
                            String f = "", l = "";
                            if (firstName.trim().length() != 0) {
                                f = firstName.substring(0, 1);
                            }
                            if (lastName.trim().length() != 0) {
                                l = lastName.substring(0, 1);
                            }
                            if (firstName.equals("null") && lastName.equals("null") && userName.equals("null")) {
                                clientName = "System Generated";
                            } else if (clientName.equals("") && userName.equals("null") && userName.equals("null")) {
                                clientName = "System Generated";
                            } else if ((firstName.equals("null")) && (lastName.equals("null")) && (userName != null)) {
                                clientName = userName;
                            } else if (firstName.equals("") && (lastName.equals("")) && (userName != null)) {
                                clientName = userName;
                            } else if (firstName != null || lastName != null) {
                                clientName = firstName + " " + lastName;
                            }
                            String messageTime = jsonArray.getJSONObject(i).getString("created_at");
                            String messageTitle = jsonArray.getJSONObject(i).getString("title");
                            String message = jsonArray.getJSONObject(i).getString("body");
                            Log.d("body:", message);
                            //String isReply = jsonArray.getJSONObject(i).getString("is_internal").equals("0") ? "false" : "true";
                            String isReply = "0";
                            ticketThread = new TicketThread(clientPicture, clientName, messageTime, messageTitle, message, isReply, f + l, name, file, type, ticketId, noOfAttachment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (ticketThread != null&&ticketThreads!=null){
                        ticketThreadList.add(ticketThread);
                       if (count<2){
                           ticketThreads.add(ticketThread);
                           count++;
                       }



                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            recyclerView.setHasFixedSize(false);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            //Collections.reverse(ticketThreadList);
            ticketThreadAdapter = new TicketThreadAdapter(getContext(), ticketThreadList);
            swipeRefresh.setVisibility(View.VISIBLE);
            runLayoutAnimation(recyclerView);
            recyclerView.setAdapter(ticketThreadAdapter);
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public class TicketThreadAdapter extends RecyclerView.Adapter<TicketThreadAdapter.TicketViewHolder> {
        Context context;
        private final static int rowTypeLoad=1,rowType=2;
        private List<TicketThread> ticketThreadList;


        public TicketThreadAdapter(Context context, List<TicketThread> ticketThreadList) {
            this.ticketThreadList = ticketThreadList;
            this.context = context;
        }

        @Override
        public int getItemCount() {
            Log.d("threadlist", "" + ticketThreadList.size());

            return ticketThreadList.size();
        }

        @Override
        public int getItemViewType(int position) {

            Log.d("positionFromviewType", "" + position);
            return position;
        }

        @Override
        public void onBindViewHolder(final TicketThreadAdapter.TicketViewHolder ticketViewHolder, final int i) {

                Log.d("position",""+i);
                final TicketThread ticketThread = ticketThreadList.get(i);
                final StringBuilder builder = new StringBuilder();
                String letter = "U";
                Log.d("customerUname", ticketThread.clientName);
                try {
                    if (!ticketThread.clientName.equals("")) {
                        if (Character.isUpperCase(ticketThread.clientName.charAt(0))) {
                            letter = String.valueOf(ticketThread.clientName.charAt(0));
                        } else {
                            letter = String.valueOf(ticketThread.clientName.charAt(0)).toUpperCase();
                        }

                    } else {
                        ticketViewHolder.textViewClientName.setVisibility(View.GONE);
                    }
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                //String letter = String.valueOf(ticketThread.clientName.charAt(0)).toUpperCase();
                ticketViewHolder.textViewClientName.setText(ticketThread.clientName);
                ticketViewHolder.textViewMessageTime.setReferenceTime(Helper.relativeTime(ticketThread.messageTime));
                ticketViewHolder.textViewTicketCreatedTime.setReferenceTime(Helper.relativeTime(ticketThread.messageTime));
                String message = ticketThread.message.replaceAll("\n", "");
                String message1 = message.replaceAll("\t", "");
                String message2 = message1.replaceAll("&nbsp;", " ");
                Log.d("without", message1);
                ticketViewHolder.textViewShowingSome.setText(Jsoup.parse(message1).text());
                //ticketViewHolder.textViewShowingSome.setText(message1);
                ticketViewHolder.webView.loadDataWithBaseURL(null, message2.replaceAll("\\n", "<br/>"), "text/html", "UTF-8", null);
                if (ticketThread.getClientPicture().contains("jpg") || ticketThread.getClientPicture().contains("png") || ticketThread.getClientPicture().contains("jpeg")) {
                    Picasso.with(context).load(ticketThread.getClientPicture()).transform(new CircleTransform()).into(ticketViewHolder.roundedImageViewProfilePic);
                } else if (!ticketThread.getClientPicture().contains("jpg") || !ticketThread.getClientPicture().contains("png") || !ticketThread.getClientPicture().contains("jpeg")) {
                    int color = Color.parseColor("#cdc5bf");
                    ColorGenerator generator = ColorGenerator.MATERIAL;
                    TextDrawable drawable = TextDrawable.builder()
                            .buildRound(letter, generator.getRandomColor());
                    //ticketViewHolder.roundedImageViewProfilePic.setAlpha(0.6f);
                    ticketViewHolder.roundedImageViewProfilePic.setImageDrawable(drawable);
                } else {
                    ticketViewHolder.roundedImageViewProfilePic.setVisibility(View.GONE);
                }
                ticketViewHolder.fileIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), ShowingAttachment.class);
                        Prefs.putString("fileName", ticketThread.getName());
                        Prefs.putString("file", ticketThread.getFile());
                        Prefs.putString("multipleName", ticketThread.getName());
                        Prefs.putString("multipleFile", ticketThread.getFile());
                        Log.d("MultipleFileNameFrom", ticketThread.getName());
                        view.getContext().startActivity(intent);

                    }
                });

                if (i == ticketThreadList.size() - 1) {
                    ticketViewHolder.reportAndReply.setText("reported ");
                } else {
                    ticketViewHolder.reportAndReply.setText("updated ");
                }

                for (int j=0;j<ticketThreadList.size()-1;j++){
                    ticketViewHolder.relativeLayoutWebView.setVisibility(View.VISIBLE);
                    //ticketViewHolder.reportAndReply.setVisibility(View.VISIBLE);
                    //ticketViewHolder.textViewTicketCreatedTime.setVisibility(View.VISIBLE);
                    ticketViewHolder.textViewMessageTime.setVisibility(View.VISIBLE);
                    ticketViewHolder.textViewShowingSome.setVisibility(View.GONE);
                    //ticketViewHolder.relativeLayoutWebView.setVisibility(View.GONE);
                    ticketViewHolder.linearLayout.setVisibility(View.VISIBLE);
                }

//                if (i==0){
//                    ticketViewHolder.relativeLayoutWebView.setVisibility(View.VISIBLE);
//                    //ticketViewHolder.reportAndReply.setVisibility(View.VISIBLE);
//                    //ticketViewHolder.textViewTicketCreatedTime.setVisibility(View.VISIBLE);
//                    ticketViewHolder.textViewMessageTime.setVisibility(View.VISIBLE);
//                    ticketViewHolder.textViewShowingSome.setVisibility(View.GONE);
//                    //ticketViewHolder.relativeLayoutWebView.setVisibility(View.GONE);
//                    ticketViewHolder.linearLayout.setVisibility(View.VISIBLE);
//                }
//                if (i==ticketThreadList.size()-1){
//                    ticketViewHolder.relativeLayoutWebView.setVisibility(View.VISIBLE);
//                    //ticketViewHolder.reportAndReply.setVisibility(View.VISIBLE);
//                    //ticketViewHolder.textViewTicketCreatedTime.setVisibility(View.VISIBLE);
//                    ticketViewHolder.textViewMessageTime.setVisibility(View.VISIBLE);
//                    ticketViewHolder.textViewShowingSome.setVisibility(View.GONE);
//                    //ticketViewHolder.relativeLayoutWebView.setVisibility(View.GONE);
//                    ticketViewHolder.linearLayout.setVisibility(View.VISIBLE);
//                }
//                else{
//                    ticketViewHolder.relativeLayoutWebView.setVisibility(View.GONE);
//                    //ticketViewHolder.reportAndReply.setVisibility(View.VISIBLE);
//                    //ticketViewHolder.textViewTicketCreatedTime.setVisibility(View.VISIBLE);
//                    ticketViewHolder.textViewMessageTime.setVisibility(View.VISIBLE);
//                    ticketViewHolder.textViewShowingSome.setVisibility(View.VISIBLE);
//                    //ticketViewHolder.relativeLayoutWebView.setVisibility(View.GONE);
//                    ticketViewHolder.linearLayout.setVisibility(View.VISIBLE);
//                }
                if (ticketThread.getName().equals("")) {
                    ticketViewHolder.fileIcon.setVisibility(View.GONE);
                    //ticketViewHolder.view.setVisibility(View.GONE);
                } else {
                    ticketViewHolder.fileIcon.setVisibility(View.VISIBLE);
                    //ticketViewHolder.view.setVisibility(View.VISIBLE);
                    //ticketViewHolder.textView.setText("Attachment "+"("+ticketThread.getNoOfAttachments()+")");

                }


                ticketViewHolder.thread.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ticketViewHolder.relativeLayoutWebView.getVisibility() == View.VISIBLE) {
                            ticketViewHolder.textViewShowingSome.setVisibility(View.VISIBLE);
                            ticketViewHolder.relativeLayoutWebView.setVisibility(View.GONE);
                            ticketViewHolder.webView.setVisibility(View.GONE);

                        } else {
                            ticketViewHolder.textViewShowingSome.setVisibility(View.GONE);
                            ticketViewHolder.relativeLayoutWebView.setVisibility(View.VISIBLE);
                            ticketViewHolder.webView.setVisibility(View.VISIBLE);
                        }
                    }
                });

                ticketViewHolder.replyIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), TicketReplyActivity.class);
                        intent.putExtra("ticket_id", ticketThread.ticketId);
                        view.getContext().startActivity(intent);
                    }
                });
            }




        @Override
        public TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            Log.d("onCreateViewHolder",""+i);
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_conversation, viewGroup, false);
            return new TicketViewHolder(itemView);


        }

        class TicketViewHolder extends RecyclerView.ViewHolder {



            RelativeLayout thread;
            TextView textViewShowingSome;
            ImageView roundedImageViewProfilePic, fileIcon, replyIcon;
            TextView textViewClientName;
            RelativeTimeTextView textViewMessageTime;
            RelativeTimeTextView textViewTicketCreatedTime;
            WebView webView;
            TextView reportAndReply;
            LinearLayout linearLayout;
            RelativeLayout relativeLayoutWebView;
            TicketViewHolder(View v) {
                super(v);
                    thread = (RelativeLayout) v.findViewById(R.id.thread);
                    roundedImageViewProfilePic = (ImageView) v.findViewById(R.id.imageView_default_profile);
                    textViewClientName = (TextView) v.findViewById(R.id.textView_client_name);
                    textViewMessageTime = (RelativeTimeTextView) v.findViewById(R.id.textView_ticket_time);
                    textViewTicketCreatedTime = (RelativeTimeTextView) v.findViewById(R.id.textView_ticket_related);
                    //textViewMessageTitle = (TextView) v.findViewById(R.id.textView_client_message_title);
                    webView = (WebView) v.findViewById(R.id.webView);
                    textViewShowingSome = (TextView) v.findViewById(R.id.showingSome);
                    reportAndReply = (TextView) v.findViewById(R.id.reported);
                    linearLayout = (LinearLayout) v.findViewById(R.id.linearWebView);
                    fileIcon = v.findViewById(R.id.filethread);
                    replyIcon = v.findViewById(R.id.imageviewreply);
                    relativeLayoutWebView = v.findViewById(R.id.showingWebView);


            }

        }

    }
}