package co.faveo.helpdesk.pro.client;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.github.curioustechizen.ago.RelativeTimeTextView;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class MyOpenTickets extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String mParam1;
    public String mParam2;
    private OnFragmentInteractionListener mListener;
    View rootView;
    ShimmerRecyclerView recyclerView;
    ProgressDialog progressDialog;
    TextView empty_view;
    TextView noInternet_view;
    SwipeRefreshLayout swipeRefreshLayout;
    int count=0;
    TextView textView;
    String ticket;
    private boolean loading = true;
    String status;
    private SearchView searchView;
    TicketOverviewAdapter ticketOverviewAdapter;
    private android.support.v7.view.ActionMode mActionMode;
    List<TicketOverview> ticketOverviewList = new ArrayList<>();
    public MyOpenTickets() {
        // Required empty public constructor
    }
    public static MyOpenTickets newInstance(String param1, String param2) {
        MyOpenTickets fragment = new MyOpenTickets();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView==null){
            rootView = inflater.inflate(R.layout.fragment_recycler, container, false);
            swipeRefreshLayout= (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefresh);
            progressDialog=new ProgressDialog(getActivity());
            JSONObject jsonObject;
            String json = Prefs.getString("DEPENDENCY", "");
            try {
                jsonObject = new JSONObject(json);
                JSONArray jsonArrayStaffs = jsonObject.getJSONArray("status");

                for (int i = 0; i < jsonArrayStaffs.length(); i++) {
                    switch (jsonArrayStaffs.getJSONObject(i).getString("name")) {
                        case "Open":
                            Prefs.putString("openid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                            break;
                        case "Closed":
                            Prefs.putString("closedid", jsonArrayStaffs.getJSONObject(i).getString("id"));
                            break;
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            noInternet_view= (TextView) rootView.findViewById(R.id.noiternet_view);
            recyclerView= (ShimmerRecyclerView) rootView.findViewById(R.id.cardList);
            empty_view= (TextView) rootView.findViewById(R.id.empty_view);
            textView= (TextView) rootView.findViewById(R.id.totalcount);
            Prefs.putString("cameFromSetting", "true");
            swipeRefreshLayout.setColorSchemeResources(R.color.faveo_blue);
            if (InternetReceiver.isConnected()) {
                noInternet_view.setVisibility(View.GONE);
                // swipeRefresh.setRefreshing(true);
                swipeRefreshLayout.setRefreshing(true);
                new FetchFirst(getActivity()).execute();
            } else {
                noInternet_view.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                empty_view.setVisibility(View.GONE);
            }

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (InternetReceiver.isConnected()) {
                        loading = true;
                        count=0;
                        swipeRefreshLayout.setRefreshing(true);
                        recyclerView.setVisibility(View.VISIBLE);
                        noInternet_view.setVisibility(View.GONE);
//                        try {
//                            mActionMode.finish();
//                        } catch (NullPointerException e) {
//                            e.printStackTrace();
//                        }
                        new FetchFirst(getActivity()).execute();
                    } else {
                        recyclerView.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        empty_view.setVisibility(View.GONE);
                        noInternet_view.setVisibility(View.VISIBLE);
                    }
                }
            });


        }
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.mytickets));
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.search_menu, menu);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);


        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                ticketOverviewAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                ticketOverviewAdapter.getFilter().filter(query);
                return false;
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    public void setNullToActionMode() {
        Log.d("Inbox Ticket", "Came from toolbar action mode");
        if (mActionMode != null)
            mActionMode = null;
    }

    /**
     * Async task for getting the my tickets.
     */
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
            ticketOverviewList.clear();
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                //total = jsonObject1.getInt("total");
//                try {
//                    data = jsonObject1.getString("data");
//                } catch (JSONException e) {
//                    data = jsonObject.getString("result");
//                }
                JSONArray jsonArray = jsonObject1.getJSONArray("ticket");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject2=jsonArray.getJSONObject(i);
                    String status=jsonObject2.getString("status_name");
                    if (status.equals("Open")){
                        count++;
                        TicketOverview ticketOverview = Helper.parseTicketOverview(jsonArray, i);
                        if (ticketOverview != null)
                            ticketOverviewList.add(ticketOverview);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return "success";
        }

        protected void onPostExecute(String result) {
            swipeRefreshLayout.setRefreshing(false);
            textView.setText("" + count + " tickets");
            if (swipeRefreshLayout.isRefreshing())
                swipeRefreshLayout.setRefreshing(false);
            try {
                String methodNotAllowed = Prefs.getString("MethodNotAllowed", null);

                if (methodNotAllowed.equalsIgnoreCase("true")){
                    Prefs.clear();
                    Intent intent=new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            if (result == null) {
                Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }

            if (result.equals("all done")) {

                Toasty.info(context, getString(R.string.all_caught_up), Toast.LENGTH_SHORT).show();
                //return;
            }
            //recyclerView = (ShimmerRecyclerView) rootView.findViewById(R.id.cardList);
            //Collections.reverse(ticketOverviewList);
            recyclerView.setHasFixedSize(false);
            final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            recyclerView.setLayoutManager(linearLayoutManager);
            ticketOverviewAdapter = new TicketOverviewAdapter(getContext(), ticketOverviewList);
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            runLayoutAnimation(recyclerView);
            recyclerView.setAdapter(ticketOverviewAdapter);
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.setAdapter(ticketOverviewAdapter);

            if (ticketOverviewAdapter.getItemCount() == 0) {
                empty_view.setVisibility(View.VISIBLE);
            } else empty_view.setVisibility(View.GONE);


            //ticketOverviewAdapter = new TicketOverviewAdapter(getContext(), ticketOverviewList);

            //recyclerView.setAdapter(ticketOverviewAdapter);

//            if (ticketOverviewAdapter.getItemCount() == 0) {
//                empty_view.setVisibility(View.VISIBLE);
//            } else empty_view.setVisibility(View.GONE);
        }
    }
    private void runLayoutAnimation(final RecyclerView recyclerView) {
        final Context context = recyclerView.getContext();
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_right);

        recyclerView.setLayoutAnimation(controller);
        ticketOverviewAdapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    public class TicketOverviewAdapter extends RecyclerView.Adapter<TicketOverviewAdapter.TicketViewHolder> implements Filterable {
        private List<TicketOverview> ticketOverviewList;
        private List<TicketOverview> filterList;
        String subject;
        int length = 0;
        private Context context;
        ArrayList<Integer> checked_items = new ArrayList<>();
        ArrayList<String> ticketSubject = new ArrayList<>();
        private SparseBooleanArray mSelectedItemsIds;
        private List<Integer> selectedIds = new ArrayList<>();


        public TicketOverviewAdapter(Context context, List<TicketOverview> ticketOverviewList) {
            this.ticketOverviewList = ticketOverviewList;
            this.context = context;
            mSelectedItemsIds = new SparseBooleanArray();
            this.filterList=ticketOverviewList;
        }


        @Override
        public int getItemCount() {
            return ticketOverviewList.size();
        }

        @Override
        public void onBindViewHolder(final TicketOverviewAdapter.TicketViewHolder ticketViewHolder, final int i) {
            final TicketOverview ticketOverview = ticketOverviewList.get(i);

            String letter;
            int id = ticketOverviewList.get(i).getTicketID();
            if (selectedIds.contains(id)) {
                //if item is selected then,set foreground color of FrameLayout.
                ticketViewHolder.ticket.setBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                //else remove selected item color.
                //holder.rootView.setForeground(new ColorDrawable(ContextCompat.getColor(context,android.R.color.transparent)));
            }
            String clientFinalName="";
            if (!ticketOverview.getClientName().equals("")){
                letter = String.valueOf(ticketOverview.clientName.charAt(0)).toUpperCase();
            }
            else{
                letter="N";
            }
            subject = ticketOverview.ticketSubject;
            if (subject.startsWith("=?UTF-8?Q?") && subject.endsWith("?=")) {
                String first = subject.replace("=?UTF-8?Q?", "");
                String second = first.replace("_", " ");
                String second1=second.replace("=C3=BA","");
                String third = second1.replace("=C2=A0", "");
                String fourth = third.replace("?=", "");
                String fifth = fourth.replace("=E2=80=99", "'");
                ticketViewHolder.textViewSubject.setText(fifth);
            } else {
                ticketViewHolder.textViewSubject.setText(ticketOverview.ticketSubject);
            }

            if (ticketOverview.getTicketStatus().equals("Closed")){
//                ticketViewHolder.ticketStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                ticketViewHolder.ticketStatus.setText(getResources().getString(R.string.closed));
            }
            else{
//                ticketViewHolder.ticketStatus.setBackgroundColor(getResources().getColor(R.color.openColor));
//                ticketViewHolder.ticketStatus.setTextColor(getResources().getColor(R.color.colorAccent));
                ticketViewHolder.ticketStatus.setText(getResources().getString(R.string.open));
            }

//            ticketViewHolder.ticket
//                    .setBackgroundColor(mSelectedItemsIds.get(i) ? 0x9934B5E4
//                            : Color.TRANSPARENT);
            if (checked_items.contains(id)) {
                ticketViewHolder.ticket.setBackgroundColor(Color.parseColor("#E0E0E0"));
            } else {
                ticketViewHolder.ticket.setBackgroundColor(Color.parseColor("#FFFFFF"));
                //ticketViewHolder.ticket.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }

//            if (ticketOverview.ticketAttachments.equals("0")) {
//                ticketViewHolder.attachementView.setVisibility(View.GONE);
//            } else {
//                int color = Color.parseColor("#808080");
//                ticketViewHolder.attachementView.setVisibility(View.VISIBLE);
//                ticketViewHolder.attachementView.setColorFilter(color);
//            }
//            if (ticketOverview.dueDate != null && !ticketOverview.dueDate.equals("null"))
////            if (Helper.compareDates(ticketOverview.dueDate) == 1) {
////                ticketViewHolder.textViewOverdue.setVisibility(View.VISIBLE);
////            } else ticketViewHolder.textViewOverdue.setVisibility(View.GONE);
//
//                if (Helper.compareDates(ticketOverview.dueDate) == 2) {
//                    ticketViewHolder.textViewduetoday.setVisibility(View.VISIBLE);
////                    ticketViewHolder.textViewduetoday.setText(R.string.due_today);
////                    //ticketViewHolder.textViewOverdue.setBackgroundColor(Color.parseColor("#FFD700"));
////                    ((GradientDrawable) ticketViewHolder.textViewduetoday.getBackground()).setColor(Color.parseColor("#3da6d7"));
////                    ticketViewHolder.textViewduetoday.setTextColor(Color.parseColor("#ffffff"));
//                    //ticketViewHolder.textViewOverdue.setBackgroundColor();
//
//                } else if (Helper.compareDates(ticketOverview.dueDate) == 1) {
//                    ticketViewHolder.textViewOverdue.setVisibility(View.VISIBLE);
////                    ticketViewHolder.textViewOverdue.setText(R.string.overdue);
//                    //ticketViewHolder.textViewOverdue.setBackgroundColor(Color.parseColor("#ef9a9a"));
////                GradientDrawable drawable = (GradientDrawable) context.getDrawable(ticketViewHolder.textViewOverdue);
////
//////set color
////                 drawable.setColor(color);
//                    ((GradientDrawable) ticketViewHolder.textViewOverdue.getBackground()).setColor(Color.parseColor("#3da6d7"));
//                    ticketViewHolder.textViewOverdue.setTextColor(Color.parseColor("#ffffff"));
//                } else {
//                    ticketViewHolder.textViewOverdue.setVisibility(View.GONE);
//                }


            ticketViewHolder.textViewTicketID.setText(ticketOverview.ticketID + "");

            ticketViewHolder.textViewTicketNumber.setText(ticketOverview.ticketNumber);
            if (ticketOverview.getClientName().startsWith("=?")) {
                String clientName = ticketOverview.getClientName().replaceAll("=?UTF-8?Q?", "");
                String newClientName = clientName.replaceAll("=E2=84=A2", "");
                String finalName = newClientName.replace("=??Q?", "");
                String name = finalName.replace("?=", "");
                String newName = name.replace("_", " ");
                Log.d("new name", newName);
                if (!Character.isUpperCase(newName.charAt(0))){
                    clientFinalName=newName.replace(newName.charAt(0),newName.toUpperCase().charAt(0));
                    ticketViewHolder.textViewClientName.setText(clientFinalName);
                }
                else{
                    ticketViewHolder.textViewClientName.setText(newName);
                }

            } else {
                if (!Character.isUpperCase(ticketOverview.clientName.charAt(0))){
                    clientFinalName=ticketOverview.clientName.replace(ticketOverview.clientName.charAt(0),ticketOverview.clientName.toUpperCase().charAt(0));
                    ticketViewHolder.textViewClientName.setText(clientFinalName);
                }
                else{
                    ticketViewHolder.textViewClientName.setText(ticketOverview.clientName);
                }
                //ticketViewHolder.textViewClientName.setText(ticketOverview.clientName);

            }
            if (ticketOverview.priorityColor.equals("null")) {
                ticketViewHolder.ticketPriority.setBackgroundColor(Color.parseColor("#3da6d7"));
            } else if (ticketOverview.priorityColor != null) {
                ticketViewHolder.ticketPriority.setBackgroundColor(Color.parseColor(ticketOverview.priorityColor));
            }


        else if (ticketOverview.priorityColor.equals("null")){
            ticketViewHolder.ticketPriority.setBackgroundColor(Color.parseColor("#3da6d7"));
        }
            ticketViewHolder.textViewTime.setReferenceTime(Helper.relativeTime(ticketOverview.ticketTime));

            if (!ticketOverview.agentName.equals("Unassigned")) {
                //ticketViewHolder.agentAssignedImage.setVisibility(View.VISIBLE);
                ticketViewHolder.agentAssigned.setText(ticketOverview.getAgentName());
            } else {
                ticketViewHolder.agentAssigned.setText("Unassigned");
                //ticketViewHolder.agentAssignedImage.setVisibility(View.GONE);
            }




            ticketViewHolder.ticket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mActionMode != null) {
                        onListItemSelect(i);
                    }
                    else {
                        Intent intent = new Intent(v.getContext(), TicketDetailActivity.class);
                        intent.putExtra("ticket_id", ticketOverview.ticketID + "");
                        Prefs.putString("TICKETid", ticketOverview.ticketID + "");
                        Prefs.putString("ticketstatus", ticketOverview.getTicketStatus());
                        intent.putExtra("ticket_number", ticketOverview.ticketNumber);
                        intent.putExtra("ticket_opened_by", ticketOverview.clientName);
                        intent.putExtra("ticket_subject", ticketOverview.ticketSubject);
                        Log.d("clicked", "onRecyclerView");
                        v.getContext().startActivity(intent);
                    }


                }
            });
            ticketViewHolder.ticket.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onListItemSelect(i);
                    length++;
                    Log.d("noofitems", "" + length);
                    Prefs.putInt("NoOfItems", length);

//                ticketOverviewList.get(i).getTicketID();
//                Log.d("position",""+ticketOverviewList.get(i).getTicketID());
//                if (ticketViewHolder.checkBox1.isEnabled()){
//
//                }
//                else{
//                    ticketViewHolder.checkBox1.setVisibility(View.GONE);
//                }
//                if (ticketViewHolder.checkBox1.isChecked()){
//
//                }else{
//                    ticketViewHolder.checkBox1.setVisibility(View.GONE);
//                }
                    return true;
                }
            });



        }
        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence charSequence) {
                    String charString = charSequence.toString();
                    if (charString.isEmpty()) {
                        ticketOverviewList = filterList;
                    } else {
                        List<TicketOverview> filteredList = new ArrayList<>();
                        for (TicketOverview row : filterList) {

                            // name match condition. this might differ depending on your requirement
                            // here we are looking for name or phone number match
                            if (row.getTicketSubject().toLowerCase().contains(charString.toLowerCase()) ||
                                    row.getAgentName().contains(charSequence)||row.ticketNumber.toLowerCase().contains(charString.toLowerCase())||
                                    row.getTicketNumber().toLowerCase().contains(charString.toLowerCase())) {
                                filteredList.add(row);
                            }
                        }

                        ticketOverviewList = filteredList;
                    }

                    FilterResults filterResults = new FilterResults();
                    filterResults.values = ticketOverviewList;
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    ticketOverviewList = (ArrayList<TicketOverview>) filterResults.values;
                    notifyDataSetChanged();
                }
            };
        }

        private void onListItemSelect(int position) {
            ticketOverviewAdapter.toggleSelection(position);//Toggle the selection

            boolean hasCheckedItems = ticketOverviewAdapter.getSelectedCount() > 0;//Check if any items are already selected or not


            if (hasCheckedItems && mActionMode == null)
                // there are some selected items, start the actionMode
                mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_ActionMode_Callback(getActivity(), ticketOverviewAdapter, null, ticketOverviewList, false));
            else if (!hasCheckedItems && mActionMode != null)
                // there no selected items, finish the actionMode
                mActionMode.finish();

            if (mActionMode != null)
                //set action mode title on item selection
                mActionMode.setTitle(String.valueOf(ticketOverviewAdapter
                        .getSelectedCount()) + " ticket selected");


        }

        public void toggleSelection(int position) {
            selectView(position, !mSelectedItemsIds.get(position));
        }


        //Remove selected selections


        //Put or delete selected position into SparseBooleanArray
        public void selectView(int position, boolean value) {
            TicketOverview ticketOverview = ticketOverviewList.get(position);
            if (value) {
                ticketSubject.add(ticketOverview.ticketSubject);
                checked_items.add(ticketOverview.getTicketID());
                status=ticketOverview.getTicketStatus();
                Log.d("ticketsubject", ticketSubject.toString());
                Log.d("checkeditems", checked_items.toString().replace(" ", ""));
                Prefs.putString("tickets", checked_items.toString().replace(" ", ""));
                Prefs.putString("TicketSubject", ticketSubject.toString());
                mSelectedItemsIds.put(position, value);
            } else {

                int pos = checked_items.indexOf(ticketOverview.getTicketID());
                int pos1 = ticketSubject.indexOf(ticketOverview.getTicketSubject());
                try {
                    checked_items.remove(pos);
                    ticketSubject.remove(pos1);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                Log.d("ticketsubject", ticketSubject.toString());
                Log.d("checkeditems", checked_items.toString().replace(" ", ""));
                Prefs.putInt("totalticketselected", length);
                Log.d("checkeditems", "" + checked_items);
                Prefs.putInt("NoOfItems", length);
                Prefs.putString("tickets", checked_items.toString().replace(" ", ""));
                Prefs.putString("TicketSubject", ticketSubject.toString());
                mSelectedItemsIds.delete(position);
            }

            notifyDataSetChanged();
        }

        public void setSelectedIds(List<Integer> selectedIds) {
            this.selectedIds = selectedIds;
            notifyDataSetChanged();
        }

        public void removeSelection() {
            mSelectedItemsIds = new SparseBooleanArray();
            notifyDataSetChanged();
        }

        //Get total selected count
        public int getSelectedCount() {
            return mSelectedItemsIds.size();
        }

        //        //Return all selected ids
//        public SparseBooleanArray getSelectedIds() {
//            return mSelectedItemsIds;
//        }
//        public void setSelectedIds(ArrayList<Integer> checked_items) {
//            this.checked_items = checked_items;
//            notifyDataSetChanged();
//        }
        public TicketOverview getItem(int position) {
            return ticketOverviewList.get(position);
        }
        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public TicketOverviewAdapter.TicketViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View itemView = LayoutInflater.
                    from(viewGroup.getContext()).
                    inflate(R.layout.card_ticket, viewGroup, false);
            return new TicketOverviewAdapter.TicketViewHolder(itemView);
        }

        public class TicketViewHolder extends RecyclerView.ViewHolder {

            protected View ticket;
            ImageView roundedImageViewProfilePic;
            TextView textViewTicketID;
            TextView textViewTicketNumber;
            TextView textViewClientName;
            TextView textViewSubject;
            RelativeTimeTextView textViewTime;
            TextView textViewOverdue;
            View ticketPriority;
            TextView ticketStatus;
            ImageView attachementView;
            CheckBox checkBox1;
            ImageView countCollaborator;
            ImageView source;
            TextView countThread;
            TextView agentAssigned;
            ImageView agentAssignedImage;
            TextView textViewduetoday;

            TicketViewHolder(View v) {
                super(v);
                ticket = v.findViewById(R.id.ticket);
                ticketPriority = v.findViewById(R.id.priority_view);
                roundedImageViewProfilePic = (ImageView) v.findViewById(R.id.imageView_default_profile);
                textViewTicketID = (TextView) v.findViewById(R.id.textView_ticket_id);
                textViewTicketNumber = (TextView) v.findViewById(R.id.textView_ticket_number);
                textViewClientName = (TextView) v.findViewById(R.id.textView_client_name);
                textViewSubject = (TextView) v.findViewById(R.id.textView_ticket_subject);
                textViewTime = (RelativeTimeTextView) v.findViewById(R.id.textView_ticket_time);
                agentAssigned = (TextView) v.findViewById(R.id.agentassigned);
                ticketStatus= (TextView) v.findViewById(R.id.statusView);
            }

        }

    }



    public class Toolbar_ActionMode_Callback implements android.support.v7.view.ActionMode.Callback {

        private Context context;
        private TicketOverviewAdapter recyclerView_adapter;
        private ArrayList<TicketOverview> message_models;
        private boolean isListViewFragment;


        public Toolbar_ActionMode_Callback(Context context, TicketOverviewAdapter ticketOverviewAdapter,TicketOverviewAdapter recyclerView_adapter, List<TicketOverview> message_models, boolean b) {
            this.context = context;
            this.recyclerView_adapter = recyclerView_adapter;
            this.message_models = (ArrayList<TicketOverview>) message_models;
            this.isListViewFragment = isListViewFragment;
        }

        @Override
        public boolean onCreateActionMode(android.support.v7.view.ActionMode mode, Menu menu) {
            //mode.getMenuInflater().inflate(R.menu.multiplemenuinbox, menu);//Inflate the menu over action mode
            mode.getMenuInflater().inflate(R.menu.menu_closed, menu);
//            SubMenu fileMenu = menu.addSubMenu("Change Status");
//            //menu.addSubMenu("Change Status");
//            for (int i=0;i<statusItems.size();i++){
//                Data data=statusItems.get(i);
//                fileMenu.add(data.getName());
//            }
            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.support.v7.view.ActionMode mode, Menu menu) {

            //Sometimes the meu will not be visible so for that we need to set their visibility manually in this method
            //So here show action menu according to SDK Levels
            if (Build.VERSION.SDK_INT < 11) {
                //MenuItemCompat.setShowAsAction(menu.findItem(R.id.mergeticket), MenuItemCompat.SHOW_AS_ACTION_NEVER);

//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_copy), MenuItemCompat.SHOW_AS_ACTION_NEVER);
//            MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_forward), MenuItemCompat.SHOW_AS_ACTION_NEVER);
            } else {
                //menu.findItem(R.id.mergeticket).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(R.id.action_copy).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
//            menu.findItem(R.id.action_forward).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
//
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.support.v7.view.ActionMode mode, MenuItem item) {
            StringBuffer stringBuffer = new StringBuffer();
            switch (item.getItemId()) {

                case R.id.action_statusClosed:
                    try {
                        if (!Prefs.getString("tickets", null).isEmpty()) {
                            String tickets = Prefs.getString("tickets", null);
                            int pos = tickets.indexOf("[");
                            int pos1 = tickets.lastIndexOf("]");
                            String text1 = tickets.substring(pos + 1, pos1);
                            String[] namesList = text1.split(",");
                            for (String name : namesList) {
                                stringBuffer.append(name + ",");
                            }
                            int pos2 = stringBuffer.toString().lastIndexOf(",");
                            ticket = stringBuffer.toString().substring(0, pos2);
                            Log.d("tickets", ticket);
                            try {
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                                // Setting Dialog Message
                                alertDialog.setMessage(getString(R.string.statusConfirmation));

                                // Setting Icon to Dialog
                                alertDialog.setIcon(R.mipmap.ic_launcher);

                                // Setting Positive "Yes" Button
                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to invoke YES event
                                        //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                                        progressDialog.show();
                                        progressDialog.setMessage(getString(R.string.pleasewait));
                                        new StatusChange(ticket, Integer.parseInt(Prefs.getString("closedid", null))).execute();
                                        Prefs.putString("tickets", null);


                                    }
                                });

                                // Setting Negative "NO" Button
                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to invoke NO event
                                        //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
                                        dialog.cancel();
                                    }
                                });

                                // Showing Alert Message
                                alertDialog.show();

                            } catch (NumberFormatException e) {
                                e.printStackTrace();

                            }
                            return true;
                        } else {
                            Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
                            return false;
                        }
                    } catch (NullPointerException e) {
                        Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    break;
//                case R.id.action_statusResolved:
//                    try {
//                        if (!Prefs.getString("tickets", null).isEmpty()) {
//                            String tickets = Prefs.getString("tickets", null);
//                            int pos = tickets.indexOf("[");
//                            int pos1 = tickets.lastIndexOf("]");
//                            String text1 = tickets.substring(pos + 1, pos1);
//                            String[] namesList = text1.split(",");
//                            for (String name : namesList) {
//                                stringBuffer.append(name + ",");
//                            }
//                            int pos2 = stringBuffer.toString().lastIndexOf(",");
//                            ticket = stringBuffer.toString().substring(0, pos2);
//
//                            Log.d("tickets", ticket);
//                            try {
//                                new StatusChange(ticket, Integer.parseInt(Prefs.getString("resolvedid", null))).execute();
//                                Prefs.putString("tickets", null);
//                                progressDialog.show();
//                                progressDialog.setMessage(getString(R.string.pleasewait));
//                            } catch (NumberFormatException e) {
//                                e.printStackTrace();
//
//                            }
//                            return true;
//                        } else {
//                            Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
//                            return false;
//                        }
//                    } catch (NullPointerException e) {
//                        Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                    break;
//                case R.id.action_statusDeleted:
//                    try {
//                        if (!Prefs.getString("tickets", null).isEmpty()) {
//                            String tickets = Prefs.getString("tickets", null);
//                            int pos = tickets.indexOf("[");
//                            int pos1 = tickets.lastIndexOf("]");
//                            String text1 = tickets.substring(pos + 1, pos1);
//                            String[] namesList = text1.split(",");
//                            for (String name : namesList) {
//                                stringBuffer.append(name + ",");
//                            }
//                            int pos2 = stringBuffer.toString().lastIndexOf(",");
//                            ticket = stringBuffer.toString().substring(0, pos2);
//
//                            Log.d("tickets", ticket);
//                            try {
//                                new StatusChange(ticket, Integer.parseInt(Prefs.getString("deletedid", null))).execute();
//                                Prefs.putString("tickets", null);
//                                progressDialog.show();
//                                progressDialog.setMessage(getString(R.string.pleasewait));
//                            } catch (NumberFormatException e) {
//                                e.printStackTrace();
//
//                            }
//                            return true;
//                        } else {
//                            Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
//                            return false;
//                        }
//                    } catch (NullPointerException e) {
//                        Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
//                case R.id.assignticket:
//                    try {
//                        if (Prefs.getString("tickets", null).equals("null") || Prefs.getString("tickets", null).equals("[]")) {
//                            Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
//                            return false;
//                        }
//                        String ticketId = Prefs.getString("tickets", null);
//                        List<String> items = new ArrayList<String>(Arrays.asList(ticketId.split("\\s*,\\s*")));
//                        int itemCount = items.size();
//                        if (itemCount == 1) {
//                            Toasty.info(getActivity(), getString(R.string.multiAssign), Toast.LENGTH_LONG).show();
//                            return false;
//                        } else {
//                            Intent intent = new Intent(getActivity(), MultiAssigningActivity.class);
//                            startActivity(intent);
//                        }
//
////            Intent intent = new Intent(getActivity(), TicketMergeActtivity.class);
////            startActivity(intent);
//
//                    } catch (NullPointerException e) {
//                        Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
//                        e.printStackTrace();
//                    }
            }
            return false;
        }


        @Override
        public void onDestroyActionMode(android.support.v7.view.ActionMode mode) {

            //When action mode destroyed remove selected selections and set action mode to null
            //First check current fragment action mode
            Log.d("onDestroyActionMode", "CAME HERE");
            MyOpenTickets myOpenTickets = new MyOpenTickets();
            //recyclerView_adapter.removeSelection();
            myOpenTickets.setNullToActionMode();
            ticketOverviewAdapter.removeSelection();
            setNullToActionMode();
//        ((InboxTickets) inboxTickets).setNullToActionMode();
            mode.finish();

            // remove selection
//            Fragment recyclerFragment = new MainActivity().getFragment(1);//Get recycler fragment
//            if (recyclerFragment != null)
//                ((RecyclerView_Fragment) recyclerFragment).setNullToActionMode();//Set action mode null

        }
    }
    @Override
    public void onResume() {
        super.onResume();
//        progressDialog.setMessage(getString(R.string.pleasewait));
//        progressDialog.show();
//        new FetchFirst(getActivity(), page).execute();
        try {
            mActionMode.finish();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    private class StatusChange extends AsyncTask<String, Void, String> {
        int statusId;
        String ticketId;

        StatusChange(String ticketId, int statusId) {

            this.ticketId = ticketId;
            this.statusId = statusId;

        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().postStatusChangedMultiple(ticketId, statusId);
            //return new Helpdesk().postStatusChanged(ticketId,statusId);
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            String state = Prefs.getString("403", null);
            //progressDialog.dismiss();
            if (result == null) {
                Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }
            try {
                if (state.equals("403") && !state.equals("null")) {
                    Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            try{
                JSONObject jsonObject=new JSONObject(result);
                JSONArray jsonArray=jsonObject.getJSONArray("message");
                for (int i=0;i<jsonArray.length();i++){
                    String message=jsonArray.getString(i);
                    if (message.contains("Permission denied")){
                        Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                        Prefs.putString("403", "null");
                        return;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                JSONObject jsonObject1 = jsonObject.getJSONObject("response");
//                JSONArray jsonArray=jsonObject1.getJSONArray("message");
//                for (int i=0;i<jsonArray.length();i++){
//                    String message=jsonArray.getString(i);
//                    if (message.equals("Permission denied, you do not have permission to access the requested page.")){
//                        Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
//                        Prefs.putString("403", "null");
//                        return;
//                    }
//                }
//
//            }catch (JSONException e){
//                e.printStackTrace();
//            }

            try {

                JSONObject jsonObject = new JSONObject(result);
                //JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                JSONObject jsonObject2 = jsonObject.getJSONObject("error");
                JSONArray jsonArray = jsonObject2.getJSONArray("ticket_id");
                String value = jsonArray.getString(0);
                Log.d("VALUE", value);
                //String message1=jsonObject2.getString("ticket_id");
                //String message2 = jsonObject1.getString("message");


//                if (message2.contains("permission denied")&&Prefs.getString("403",null).equals("403")){
//
//                }
                if (value.contains("The ticket id field is required.")) {
                    Toasty.info(getActivity(), getString(R.string.noticket), Toast.LENGTH_LONG).show();
                    return;
                }
//               else if (message2.contains("Status changed to Deleted")) {
//                    Toasty.success(getActivity(), getString(R.string.status_deleted), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    //Prefs.putString("ticketstatus", "Deleted");
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                } else if (message2.contains("Status changed to Open")) {
//                    Toasty.success(getActivity(), getString(R.string.status_opened), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                } else if (message2.contains("Status changed to Closed")) {
//                    Toasty.success(getActivity(), getString(R.string.status_closed), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                } else if (message2.contains("Status changed to Resolved")) {
//                    Toasty.success(getActivity(), getString(R.string.status_resolved), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                }


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();

            }
            try {

                JSONObject jsonObject = new JSONObject(result);
                //JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                //String message1=jsonObject2.getString("ticket_id");
                String message2 = jsonObject.getString("message");
                if (!message2.equals("null")||!message2.equals("")){
                    Toasty.success(getActivity(),getString(R.string.successfullyChanged),Toast.LENGTH_LONG).show();
                    Prefs.putString("tickets", null);
                    getActivity().finish();
                    startActivity(new Intent(getActivity(), MainActivity.class));
                }
                else{
                    Toasty.error(getActivity(), getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                    return;
                }

//                if (message2.contains("Status changed to Deleted")) {
//                    Toasty.success(getActivity(), getString(R.string.status_deleted), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    //Prefs.putString("ticketstatus", "Deleted");
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                } else if (message2.contains("Status changed to Open")) {
//                    Toasty.success(getActivity(), getString(R.string.status_opened), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                } else if (message2.contains("Status changed to Closed")) {
//                    Toasty.success(getActivity(), getString(R.string.status_closed), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                } else if (message2.contains("Status changed to Resolved")) {
//                    Toasty.success(getActivity(), getString(R.string.status_resolved), Toast.LENGTH_LONG).show();
//                    Prefs.putString("tickets", null);
//                    getActivity().finish();
//                    startActivity(new Intent(getActivity(), MainActivity.class));
//                }


            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();

            }


        }


    }


}