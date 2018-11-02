package co.faveo.helpdesk.pro.client.fragments;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.github.javiersantos.bottomdialogs.BottomDialog;
import com.pixplicity.easyprefs.library.Prefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import co.faveo.helpdesk.pro.client.application.Helpdesk;
import co.faveo.helpdesk.pro.client.receiver.InternetReceiver;
import co.faveo.helpdesk.pro.client.R;
import co.faveo.helpdesk.pro.client.activity.LoginActivity;
import co.faveo.helpdesk.pro.client.activity.MainActivity;
import es.dmoral.toasty.Toasty;
import io.github.yavski.fabspeeddial.FabSpeedDial;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditCustomer extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public String mParam1;
    public String mParam2;
    View rootView;
    ProgressDialog progressDialog;
    EditText editTextusername,editTextfirstname,editTextlastname,editTextemail;
    String userName="",firstName="",lastName="";
    Button buttonPassword;
    Animation animation;
    FabSpeedDial fabSpeedDial;
    private OnFragmentInteractionListener mListener;
    public EditCustomer() {
        // Required empty public constructor
    }
    public static EditCustomer newInstance(String param1, String param2) {
        EditCustomer fragment = new EditCustomer();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
            rootView=inflater.inflate(R.layout.fragment_edit_customer, container, false);
            buttonPassword= (Button) rootView.findViewById(R.id.passwordChange);
            animation= AnimationUtils.loadAnimation(getActivity(),R.anim.shake_error);
            editTextusername= (EditText) rootView.findViewById(R.id.username);
            editTextfirstname= (EditText) rootView.findViewById(R.id.firstname);
            editTextlastname= (EditText) rootView.findViewById(R.id.lastname);
            editTextemail= (EditText) rootView.findViewById(R.id.email);
            fabSpeedDial= (FabSpeedDial) getActivity().findViewById(R.id.fab_main);
            fabSpeedDial.setVisibility(View.GONE);
            editTextemail.setEnabled(false);
            editTextusername.setEnabled(false);
            progressDialog=new ProgressDialog(getActivity());
            if (InternetReceiver.isConnected()){
                progressDialog.setMessage(getString(R.string.pleasewait));
                progressDialog.show();
                new FetchClientTickets(getActivity()).execute();
            }

        }

        buttonPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
DialogCustomView();
//                MyBottomSheetDialog myBottomSheetDialog = new MyBottomSheetDialog(getActivity());
//                myBottomSheetDialog.setCanceledOnTouchOutside(true);
//                myBottomSheetDialog.show();
            }
        });
        ((MainActivity) getActivity()).setActionBarTitle(getString(R.string.myprofile));
        // Inflate the layout for this fragment
        return rootView;


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_inbox, menu);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item != null) {
                item.getSubMenu().clearHeader();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        int id = item.getItemId();
        if (id == R.id.actiondone) {
            if (editTextusername.getText().toString().equals(userName)&&editTextfirstname.getText().toString().equals(firstName)&&editTextlastname.getText().toString().equals(lastName)){
                return true;
            }
            else {


                if (InternetReceiver.isConnected()) {

                    String finalFirstName=editTextfirstname.getText().toString();
                    String finalLastName=editTextlastname.getText().toString();
                    String finalUserName=editTextusername.getText().toString();

                    try {
                        finalFirstName = URLEncoder.encode(finalFirstName.trim(), "utf-8");
                        finalLastName=URLEncoder.encode(finalLastName.trim(), "utf-8");
                        finalUserName=URLEncoder.encode(finalUserName.trim(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    final String finalFirstName1 = finalFirstName;
                    final String finalLastName1 = finalLastName;
                    final String finalUserName1 = finalUserName;
                    new BottomDialog.Builder(getActivity())
                            .setContent(getString(R.string.confirmEdit))
                            .setTitle("Editing profile")
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
                                    progressDialog.setMessage(getString(R.string.pleasewait));
                                    progressDialog.show();
                                    // Write your code here to invoke YES event
                                    new EditClient(getActivity(), finalFirstName1, finalLastName1, finalUserName1).execute();
                                }
                            }).onNegative(new BottomDialog.ButtonCallback() {
                        @Override
                        public void onClick(@NonNull BottomDialog bottomDialog) {
                            bottomDialog.dismiss();
                        }
                    })
                            .show();
//                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//                    // Setting Dialog Message
//                    alertDialog.setMessage(getString(R.string.confirmEdit));
//                    // Setting Icon to Dialog
//                    alertDialog.setIcon(R.mipmap.ic_launcher);
//                    // Setting Positive "Yes" Button
//                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                        @SuppressLint("MissingPermission")
//                        public void onClick(DialogInterface dialog, int which) {
//                            progressDialog.setMessage(getString(R.string.pleasewait));
//                            progressDialog.show();
//                            // Write your code here to invoke YES event
//                            new EditClient(getActivity(),finalFirstName,finalLastName,finalUserName).execute();
//                        }
//                    });
//                    // Setting Negative "NO" Button
//                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to invoke NO event
//                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                            dialog.cancel();
//                        }
//                    });
//
//                    // Showing Alert Message
//                    alertDialog.show();


                }
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    protected void DialogCustomView() {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.bottom_sheet_layout, null);
                 final AppCompatEditText appCompatEditTextold,appCompatEditTextnew,appCompatEditTextconfirm;
         final TextView textViewOld,textViewNew,textViewConfirm;
         final TextInputLayout textInputLayoutOld,textInputLayoutNew,textInputLayoutConfirm;
        appCompatEditTextold= (AppCompatEditText) promptView.findViewById(R.id.inputoldpassword);
        appCompatEditTextnew= (AppCompatEditText) promptView.findViewById(R.id.inputnewpassword);
        appCompatEditTextconfirm= (AppCompatEditText) promptView.findViewById(R.id.input_password);
        textViewOld= (TextView) promptView.findViewById(R.id.passwordoldError);
        textViewNew= (TextView) promptView.findViewById(R.id.passwordErrornew);
        textViewConfirm= (TextView) promptView.findViewById(R.id.passwordError);
        textInputLayoutOld= (TextInputLayout) promptView.findViewById(R.id.input_layoutoldpassword);
        textInputLayoutNew= (TextInputLayout) promptView.findViewById(R.id.input_layoutnewpassword);
        textInputLayoutConfirm= (TextInputLayout) promptView.findViewById(R.id.input_layout_password);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(promptView)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                Button button1 = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                button.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        // TODO Do something
                        if (appCompatEditTextold.getText().toString().equals("") && appCompatEditTextnew.getText().toString().equals("") && appCompatEditTextconfirm.getText().toString().equals("")) {
                            textInputLayoutOld.startAnimation(animation);
                            textViewOld.setVisibility(View.VISIBLE);
                            appCompatEditTextold.requestFocus();
                            textViewOld.postDelayed(new Runnable() {
                                public void run() {
                                    textViewOld.setVisibility(View.GONE);
                                }
                            }, 3000);
                        } else if (appCompatEditTextnew.getText().toString().equals("")) {
                            textInputLayoutNew.startAnimation(animation);
                            textViewNew.setVisibility(View.VISIBLE);
                            appCompatEditTextnew.requestFocus();
                            textViewNew.postDelayed(new Runnable() {
                                public void run() {
                                    textViewNew.setVisibility(View.GONE);
                                }
                            }, 3000);
                        } else if (appCompatEditTextconfirm.getText().toString().equals("")) {
                            textInputLayoutConfirm.startAnimation(animation);
                            textViewConfirm.setVisibility(View.VISIBLE);
                            appCompatEditTextconfirm.requestFocus();
                            textViewConfirm.postDelayed(new Runnable() {
                                public void run() {
                                    textViewConfirm.setVisibility(View.GONE);
                                }
                            }, 3000);
                        } else if (appCompatEditTextold.getText().toString().equals("")) {
                            textInputLayoutOld.startAnimation(animation);
                            textViewOld.setVisibility(View.VISIBLE);
                            appCompatEditTextold.requestFocus();
                            textViewOld.postDelayed(new Runnable() {
                                public void run() {
                                    textViewOld.setVisibility(View.GONE);
                                }
                            }, 3000);
                        } else {
                            String password = Prefs.getString("PASSWORD", null);
                            if (!appCompatEditTextold.getText().toString().equals(password)) {
                                textInputLayoutOld.startAnimation(animation);
                                textViewOld.setVisibility(View.VISIBLE);
                                textViewOld.setText(getString(R.string.passwordNotMatch));
                                appCompatEditTextold.requestFocus();
                                textViewOld.postDelayed(new Runnable() {
                                    public void run() {
                                        textViewOld.setVisibility(View.GONE);
                                    }
                                }, 3000);
                                return;
                            } else {
                                if (!appCompatEditTextnew.getText().toString().equals(appCompatEditTextconfirm.getText().toString())) {
                                    textInputLayoutNew.startAnimation(animation);
                                    textViewNew.setVisibility(View.VISIBLE);
                                    textViewNew.setText(getString(R.string.passwordmustmatch));
                                    appCompatEditTextnew.requestFocus();
                                    textViewNew.postDelayed(new Runnable() {
                                        public void run() {
                                            textViewNew.setVisibility(View.GONE);
                                        }
                                    }, 3000);

                                } else if (appCompatEditTextnew.getText().toString().equals(password)) {
                                    textInputLayoutNew.startAnimation(animation);
                                    textViewNew.setVisibility(View.VISIBLE);
                                    textViewNew.setText(getString(R.string.differentpassword));
                                    appCompatEditTextnew.requestFocus();
                                    textViewNew.postDelayed(new Runnable() {
                                        public void run() {
                                            textViewNew.setVisibility(View.GONE);
                                        }
                                    }, 3000);
                                } else {
                                    final String finalOldPass = appCompatEditTextold.getText().toString().trim();
                                    final String finalNewPass = appCompatEditTextnew.getText().toString().trim();
                                    final String finalConfirmPass = appCompatEditTextconfirm.getText().toString().trim();
                                    new BottomDialog.Builder(getActivity())
                                            .setContent(R.string.change_password_text)
                                            .setTitle("Changing password")
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
                                                    progressDialog.setMessage(getString(R.string.pleasewait));
                                                    progressDialog.show();
                                                    // Write your code here to invoke YES event
                                                    new PasswordChange(getActivity(),finalOldPass,finalNewPass,finalConfirmPass).execute();
                                                }
                                            }).onNegative(new BottomDialog.ButtonCallback() {
                                        @Override
                                        public void onClick(@NonNull BottomDialog bottomDialog) {
                                            bottomDialog.dismiss();
                                        }
                                    })
                                            .show();

                                }
                            }
                        }
                    }
                });
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        dialog.show();
    }

    private class EditClient extends AsyncTask<String, Void, String> {
        Context context;
        String firstname;
        String lastname;
        String username1;
        EditClient(Context context,
                   String firstname,
                   String lastname,
                   String username1) {
            this.context = context;
            this.firstname=firstname;
            this.lastname=lastname;
            this.username1=username1;

        }

        protected String doInBackground(String... urls) {

            return new Helpdesk().saveCustomerDetails(firstname,lastname,username1);
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (isCancelled()) return;


//            if (result == null) return;
            try {
                Log.d("depenResponse", result);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            String state=Prefs.getString("403",null);
            try {
                if (state.equals("403") && !state.equals("null")) {
                    Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                String message=jsonObject1.getString("message");
                if (message.equals("Updated successfully")){
                    Toasty.success(getActivity(),getString(R.string.editedcustomerdetails),Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                }



            } catch (JSONException e) {
                Toasty.error(getActivity(), getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }

    private class FetchClientTickets extends AsyncTask<String, Void, String> {
        Context context;
        FetchClientTickets(Context context) {
            this.context = context;
        }

        protected String doInBackground(String... urls) {
            return new Helpdesk().getTicketsByUser(Prefs.getString("clientId",null));
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (isCancelled()) return;

            if (result == null) return;
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject requester = jsonObject.getJSONObject("data");
                String firstname1 = requester.getString("first_name");
                String lastName1 = requester.getString("last_name");
                String username1 = requester.getString("user_name");
                String email=requester.getString("email");

                if (!username1.equals("")||!username1.equals("null")){
                    userName=username1;
                    editTextusername.setText(username1);
                }
                if (!firstname1.equals("")||!firstname1.equals("null")){
                    firstName=firstname1;
                    editTextfirstname.setText(firstname1);
                }
                if (!lastName1.equals("")||!lastName1.equals("null")){
                    lastName=lastName1;
                    editTextlastname.setText(lastName1);
                }
                if (!email.equals("")||!email.equals("null")){
                    editTextemail.setText(email);
                }
                } catch (JSONException e) {
                Toasty.error(getActivity(), getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }
    public class MyBottomSheetDialog extends BottomSheetDialog{

        private AppCompatEditText appCompatEditTextold,appCompatEditTextnew,appCompatEditTextconfirm;
        private Context context;
        private TextView textViewOld,textViewNew,textViewConfirm;
        private TextInputLayout textInputLayoutOld,textInputLayoutNew,textInputLayoutConfirm;
        //Button button;
        @SuppressLint("StaticFieldLeak")
        private  MyBottomSheetDialog instance;

        public  MyBottomSheetDialog getInstance(@NonNull Context context) {
            return instance == null ? new MyBottomSheetDialog(context) : instance;
        }

        public MyBottomSheetDialog(@NonNull Context context) {
            super(context);
            this.context = context;
            create();
        }

        public void create() {
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null);
            setContentView(bottomSheetView);
            BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from((View) bottomSheetView.getParent());
            BottomSheetBehavior.BottomSheetCallback bottomSheetCallback = new BottomSheetBehavior.BottomSheetCallback() {
                @Override
                public void onStateChanged(@NonNull View bottomSheet, int newState) {
                    // do something
                }

                @Override
                public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                    // do something
                }
            };
            //button= (Button) bottomSheetView.findViewById(R.id.updateButton);
            appCompatEditTextold= (AppCompatEditText) bottomSheetView.findViewById(R.id.inputoldpassword);
            appCompatEditTextnew= (AppCompatEditText) bottomSheetView.findViewById(R.id.inputnewpassword);
            appCompatEditTextconfirm= (AppCompatEditText) bottomSheetView.findViewById(R.id.input_password);
            textViewOld= (TextView) bottomSheetView.findViewById(R.id.passwordoldError);
            textViewNew= (TextView) bottomSheetView.findViewById(R.id.passwordErrornew);
            textViewConfirm= (TextView) bottomSheetView.findViewById(R.id.passwordError);
            textInputLayoutOld= (TextInputLayout) bottomSheetView.findViewById(R.id.input_layoutoldpassword);
            textInputLayoutNew= (TextInputLayout) bottomSheetView.findViewById(R.id.input_layoutnewpassword);
            textInputLayoutConfirm= (TextInputLayout) bottomSheetView.findViewById(R.id.input_layout_password);
//            button.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    if (appCompatEditTextold.getText().toString().equals("")&&appCompatEditTextnew.getText().toString().equals("")&&appCompatEditTextconfirm.getText().toString().equals("")){
//                            textInputLayoutOld.startAnimation(animation);
//                            textViewOld.setVisibility(View.VISIBLE);
//                            appCompatEditTextold.requestFocus();
//                        textViewOld.postDelayed(new Runnable() {
//                            public void run() {
//                                textViewOld.setVisibility(View.GONE);
//                            }
//                        }, 3000);
//                    }
//                  else if (appCompatEditTextnew.getText().toString().equals("")){
//                        textInputLayoutNew.startAnimation(animation);
//                        textViewNew.setVisibility(View.VISIBLE);
//                        appCompatEditTextnew.requestFocus();
//                        textViewNew.postDelayed(new Runnable() {
//                            public void run() {
//                                textViewNew.setVisibility(View.GONE);
//                            }
//                        }, 3000);
//                    }
//                  else if (appCompatEditTextconfirm.getText().toString().equals("")){
//                        textInputLayoutConfirm.startAnimation(animation);
//                        textViewConfirm.setVisibility(View.VISIBLE);
//                        appCompatEditTextconfirm.requestFocus();
//                        textViewConfirm.postDelayed(new Runnable() {
//                            public void run() {
//                                textViewConfirm.setVisibility(View.GONE);
//                            }
//                        }, 3000);
//                    }
//                    else if (appCompatEditTextold.getText().toString().equals("")){
//                        textInputLayoutOld.startAnimation(animation);
//                        textViewOld.setVisibility(View.VISIBLE);
//                        appCompatEditTextold.requestFocus();
//                        textViewOld.postDelayed(new Runnable() {
//                            public void run() {
//                                textViewOld.setVisibility(View.GONE);
//                            }
//                        }, 3000);
//                    }
//                    else{
//                        String password=Prefs.getString("PASSWORD", null);
//                        if (!appCompatEditTextold.getText().toString().equals(password)){
//                            textInputLayoutOld.startAnimation(animation);
//                            textViewOld.setVisibility(View.VISIBLE);
//                            textViewOld.setText(getString(R.string.passwordNotMatch));
//                            appCompatEditTextold.requestFocus();
//                            textViewOld.postDelayed(new Runnable() {
//                                public void run() {
//                                    textViewOld.setVisibility(View.GONE);
//                                }
//                            }, 3000);
//                                return;
//                        }
//                        else{
//                            if (!appCompatEditTextnew.getText().toString().equals(appCompatEditTextconfirm.getText().toString())){
//                                textInputLayoutNew.startAnimation(animation);
//                                textViewNew.setVisibility(View.VISIBLE);
//                                textViewNew.setText(getString(R.string.passwordmustmatch));
//                                appCompatEditTextnew.requestFocus();
//                                textViewNew.postDelayed(new Runnable() {
//                                    public void run() {
//                                        textViewNew.setVisibility(View.GONE);
//                                    }
//                                }, 3000);
//
//                            }
//                            else if (appCompatEditTextnew.getText().toString().equals(password)){
//                                textInputLayoutNew.startAnimation(animation);
//                                textViewNew.setVisibility(View.VISIBLE);
//                                textViewNew.setText(getString(R.string.differentpassword));
//                                appCompatEditTextnew.requestFocus();
//                                textViewNew.postDelayed(new Runnable() {
//                                    public void run() {
//                                        textViewNew.setVisibility(View.GONE);
//                                    }
//                                }, 3000);
//                            }
//                            else{
//                                final String finalOldPass=appCompatEditTextold.getText().toString().trim();
//                                final String finalNewPass=appCompatEditTextnew.getText().toString().trim();
//                                final String finalConfirmPass=appCompatEditTextconfirm.getText().toString().trim();
//                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//                                // Setting Dialog Message
//                                alertDialog.setMessage(getString(R.string.passwordconfirm));
//                                // Setting Icon to Dialog
//                                alertDialog.setIcon(R.mipmap.ic_launcher);
//                                // Setting Positive "Yes" Button
//                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                    @SuppressLint("MissingPermission")
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        progressDialog.setMessage(getString(R.string.pleasewait));
//                                        progressDialog.show();
//                                        // Write your code here to invoke YES event
//                                       new PasswordChange(getActivity(),finalOldPass,finalNewPass,finalConfirmPass).execute();
//                                    }
//                                });
//                                // Setting Negative "NO" Button
//                                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // Write your code here to invoke NO event
//                                        //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                                        dialog.cancel();
//                                    }
//                                });
//
//                                // Showing Alert Message
//                                alertDialog.show();
//
//                            }
//                        }
//                    }
//                }
//            });


        }

    }
    private class PasswordChange extends AsyncTask<String, Void, String> {
        Context context;
        String oldPass;
        String newPass;
        String confirmPass;
        PasswordChange(Context context,
                   String oldPass,
                   String newPass,
                   String confirmPass) {
            this.context = context;
            this.oldPass=oldPass;
            this.newPass=newPass;
            this.confirmPass=confirmPass;

        }

        protected String doInBackground(String... urls) {

            return new Helpdesk().changePassword(oldPass,newPass,confirmPass);
        }

        protected void onPostExecute(String result) {
            progressDialog.dismiss();
            if (isCancelled()) return;


//            if (result == null) return;
            try {
                Log.d("depenResponse", result);
            }catch (NullPointerException e){
                e.printStackTrace();
            }
            String state=Prefs.getString("403",null);
            try {
                if (state.equals("403") && !state.equals("null")) {
                    Toasty.warning(getActivity(), getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            try {
                JSONObject jsonObject = new JSONObject(result);
                String message=jsonObject.getString("message");
                if (message.equals("Password updated successfully")){
                    Toasty.success(getActivity(),"password changed successfully",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    // Setting Dialog Message
                    alertDialog.setCancelable(false);
                    alertDialog.setMessage(getString(R.string.passwordchangesuccess));
                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.mipmap.ic_launcher);
                    // Setting Positive "Yes" Button
                    alertDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @SuppressLint("MissingPermission")
                        public void onClick(DialogInterface dialog, int which) {
                           Intent intent=new Intent(getActivity(),LoginActivity.class);
                           startActivity(intent);
                           getActivity().finish();
                            String url=Prefs.getString("URLneedtoshow",null);
                            Prefs.clear();
                            Prefs.putString("URLneedtoshow",url);
                        }
                    });
                    // Setting Negative "NO" Button
//                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            // Write your code here to invoke NO event
//                            //Toast.makeText(getApplicationContext(), "You clicked on NO", Toast.LENGTH_SHORT).show();
//                            dialog.cancel();
//                        }
//                    });

                    // Showing Alert Message
                    alertDialog.show();
                }



            } catch (JSONException e) {
                Toasty.error(getActivity(), getString(R.string.unexpected_error), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

        }
    }




}
