package co.faveo.helpdesk.pro.client;

import android.Manifest;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.flipboard.bottomsheet.commons.MenuSheetView;
import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;
import com.pixplicity.easyprefs.library.Prefs;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.AudioPickActivity;
import com.vincent.filepicker.activity.ImagePickActivity;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.activity.VideoPickActivity;
import com.vincent.filepicker.filter.entity.AudioFile;
import com.vincent.filepicker.filter.entity.ImageFile;
import com.vincent.filepicker.filter.entity.NormalFile;
import com.vincent.filepicker.filter.entity.VideoFile;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadStatusDelegate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import butterknife.BindView;
import es.dmoral.toasty.Toasty;

import static com.vincent.filepicker.activity.AudioPickActivity.IS_NEED_RECORDER;
import static com.vincent.filepicker.activity.ImagePickActivity.IS_NEED_CAMERA;

public class CreateTicketActivity extends AppCompatActivity implements PermissionCallback, ErrorCallback {
  ImageView imageViewBack;
    boolean allCorrect;
    BottomSheetLayout bottomSheet;
    ProgressDialog progressDialog;
    ArrayAdapter<Data> spinnerPriArrayAdapter, spinnerHelpArrayAdapter;
    ArrayList<Data> helptopicItems, priorityItems;
    Button button;
    int gallery,document,camera,audio=0;
    private static final int PICKFILE_REQUEST_CODE = 1234;
    String token;
    ImageButton imageButtonAttachmentClose;
    TextView attachmentFileName;
    RelativeLayout attachment_layout;
    String path="";
    String name,email;
    EditText emailedittext,fnameedittext,msgedittext,subedittext;
    Spinner spinnerpri,spinnerhelp;
    ImageButton refresh;
    Animation rotation;
    Button buttonSubmit;
    String splChrs = "-/@#$%^&_+=()" ;
    public static String
            keyDepartment = "", valueDepartment = "",
            keySLA = "", valueSLA = "",
            keyStatus = "", valueStatus = "",
            keyStaff = "", valueStaff = "",
            keyName="",
            keyPriority = "", valuePriority = "",
            keyTopic = "", valueTopic = "",
            keySource = "", valueSource = "",
            keyType = "", valueType = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Window window = CreateTicketActivity.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(CreateTicketActivity.this,R.color.faveo));
        attachment_layout= (RelativeLayout) findViewById(R.id.attachment_layout);
        attachmentFileName= (TextView) findViewById(R.id.attachment_name);
        imageButtonAttachmentClose= (ImageButton) findViewById(R.id.attachment_close);
        emailedittext= (EditText) findViewById(R.id.email_edittext);
        fnameedittext= (EditText) findViewById(R.id.fname_edittext);
        msgedittext= (EditText) findViewById(R.id.msg_edittext);
        subedittext= (EditText) findViewById(R.id.sub_edittext);
        spinnerpri= (Spinner) findViewById(R.id.spinner_pri);
        spinnerhelp= (Spinner) findViewById(R.id.spinner_help);
        refresh= (ImageButton) findViewById(R.id.imageRefresh);
        buttonSubmit= (Button) findViewById(R.id.buttonSubmit);
        rotation = AnimationUtils.loadAnimation(this, R.anim.rotate);
        bottomSheet= (BottomSheetLayout) findViewById(R.id.bottomsheet);
        name=Prefs.getString("clientNameForFeedback",null);
        email=Prefs.getString("emailForFeedback",null);
        emailedittext.setText(email);
        fnameedittext.setText(name);
        emailedittext.setEnabled(false);
        fnameedittext.setEnabled(false);
        button= (Button) findViewById(R.id.attachment);


        setUpViews();
        imageButtonAttachmentClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachment_layout.setVisibility(View.GONE);
                attachmentFileName.setText("");
                //attachmentFileSize.setText("");
                path="";
                //toolbarAttachment.setVisibility(View.GONE);
            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = CreateTicketActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                MenuSheetView menuSheetView =
                        new MenuSheetView(CreateTicketActivity.this, MenuSheetView.MenuType.LIST, "Choose...", new MenuSheetView.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                if (bottomSheet.isSheetShowing()) {
                                    bottomSheet.dismissSheet();
                                }
                                if (item.getItemId()==R.id.imageGalley){
                                    gallery=2;
                                    reqPermissionCamera();
                                    return true;
                                }
                                else if (item.getItemId()==R.id.videoGallery){
                                    camera=3;
                                    reqPermissionCamera();
                                    return true;
                                }
                                else if (item.getItemId()==R.id.musicGallery){
                                    audio=4;
                                    reqPermissionCamera();
                                    return true;
                                }
                                else if (item.getItemId()==R.id.documentGallery){
                                    document=1;
                                    reqPermissionCamera();
                                    return true;
                                }

                                return true;
                            }
                        });
                menuSheetView.inflateMenu(R.menu.navigation);
                bottomSheet.showWithSheetView(menuSheetView);

//                if (bottomNavigationView.getVisibility()==View.GONE){
//                    bottomNavigationView.setVisibility(View.VISIBLE);
//                }
//                else if (bottomNavigationView.getVisibility()==View.VISIBLE){
//                    bottomNavigationView.setVisibility(View.GONE);
//                }


            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateTicketActivity.this);

                // Setting Dialog Title
                // Setting Dialog Message
                alertDialog.setMessage(getString(R.string.refreshPage));

                // Setting Icon to Dialog
                alertDialog.setIcon(R.mipmap.ic_launcher);

                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Write your code here to invoke YES event
                        //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                        if (InternetReceiver.isConnected()){

                            refresh.startAnimation(rotation);
//                            progressDialog=new ProgressDialog(CreateTicketActivity.this);
//                            progressDialog.setMessage(getString(R.string.refreshing));
//                            progressDialog.show();
                            new FetchDependency().execute();
                            setUpViews();
                        }
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
            }
        });
        imageViewBack= (ImageView) findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!msgedittext.getText().toString().equals("")||!subedittext.getText().toString().equals("")){
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateTicketActivity.this);

                    // Setting Dialog Message
                    alertDialog.setMessage(R.string.discard);

                    // Setting Icon to Dialog
                    alertDialog.setIcon(R.mipmap.ic_launcher);

                    // Setting Positive "Yes" Button

                    alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Write your code here to invoke YES event
                            //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                            finish();
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
                }
                else{
                    finish();
                }


            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SendPostRequest().execute();
                final Data helpTopic = (Data) spinnerhelp.getSelectedItem();
                final Data priority = (Data) spinnerpri.getSelectedItem();
                String finalName=name;
                String finalEmail=email;
                String subject=subedittext.getText().toString();
                String message=msgedittext.getText().toString();

                allCorrect = true;

                if (finalName.length()==0&&finalName.length()==0){
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.fill_firstname), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                if (finalName.length() == 0 && finalEmail.length() == 0 && subject.length() == 0 && message.length() == 0 && helpTopic.ID == 0 && priority.ID == 0) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.fill_all_the_details), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                } else if (finalName.trim().length() == 0||finalName.equals("null")||finalName.equals(null)) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.fill_firstname), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (finalEmail.trim().length() == 0 || !Helper.isValidEmail(finalEmail)) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                } else if (subject.trim().length() == 0) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.sub_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (subject.matches("[" + splChrs + "]+")) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.only_special_characters_not_allowed_here), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }
                else if (priority.ID == 0) {
                    allCorrect = false;
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.please_select_some_priority), Toast.LENGTH_SHORT).show();
                } else if (helpTopic.ID == 0) {
                    allCorrect = false;
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.select_some_helptopic), Toast.LENGTH_SHORT).show();
                } else if (message.trim().length() == 0) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.msg_must_not_be_empty), Toast.LENGTH_SHORT).show();
                    allCorrect = false;
                }


                if (allCorrect){
                    if (InternetReceiver.isConnected()){
                        if (path.equals("")) {
                            try {
                                finalName = URLEncoder.encode(finalName.trim(), "utf-8");
                                subject = URLEncoder.encode(subject.trim(), "utf-8");
                                message = URLEncoder.encode(message.trim(), "utf-8");
                                finalEmail = URLEncoder.encode(finalEmail.trim(), "utf-8");
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateTicketActivity.this);

                                // Setting Dialog Message
                                alertDialog.setMessage(getString(R.string.createConfirmation));

                                // Setting Icon to Dialog
                                alertDialog.setIcon(R.mipmap.ic_launcher);

                                // Setting Positive "Yes" Button
                                final String finalSubject = subject;
                                final String finalMessage = message;
                                final String finalName1 = finalName;
                                final String finalEmail1 = finalEmail;
                                alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Write your code here to invoke YES event
                                        //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                                        if (InternetReceiver.isConnected()) {
                                            progressDialog = new ProgressDialog(CreateTicketActivity.this);
                                            progressDialog.setMessage("Please wait");
                                            progressDialog.show();
                                            new CreateNewTicket(finalSubject, finalMessage, helpTopic.ID, priority.ID, finalName1, finalEmail1).execute();
                                        }
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

                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }else{
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateTicketActivity.this);

                            // Setting Dialog Message
                            alertDialog.setMessage(getString(R.string.createConfirmation));


                            // Setting Icon to Dialog
                            alertDialog.setIcon(R.mipmap.ic_launcher);

                            // Setting Positive "Yes" Button
                            final String finalSubject1 = subject;
                            final String finalMessage1 = message;
                            final String finalName2 = finalName;
                            final String finalEmail2 = finalEmail;
                            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Write your code here to invoke YES event
                                    //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                                    if (InternetReceiver.isConnected()) {
                                        progressDialog = new ProgressDialog(CreateTicketActivity.this);
                                        progressDialog.setMessage(getString(R.string.creating_ticket));
                                        progressDialog.show();
                                        try {
                                            token = Prefs.getString("TOKEN", null);
                                            String uploadId = UUID.randomUUID().toString();
                                            new MultipartUploadRequest(CreateTicketActivity.this, uploadId, Constants.URL + "helpdesk/create/satellite/ticket?token=" + token)
                                                    .addFileToUpload(path, "media_attachment[]")
                                                    //Adding file
                                                    .addParameter("subject", finalSubject1)
                                                    .addParameter("body", finalMessage1)
                                                    .addParameter("help_topic", "" + helpTopic.ID)
                                                    .addParameter("priority", "" + priority.ID)
                                                    .addParameter("first_name", finalName2)
                                                    .addParameter("email", finalEmail2)
                                                    //.addParameter("cc[]", String.valueOf(Arrays.asList("sayar@gmail.com","demoadmin@gmail.com")))
                                                    //Adding text parameter to the request
                                                    //.setNotificationConfig(new UploadNotificationConfig())
                                                    .setMaxRetries(1)
                                                    .setMethod("POST").setDelegate(new UploadStatusDelegate() {
                                                @Override
                                                public void onProgress(UploadInfo uploadInfo) {

                                                }

                                                @Override
                                                public void onError(UploadInfo uploadInfo, Exception exception) {

                                                }

                                                @Override
                                                public void onCompleted(UploadInfo uploadInfo, ServerResponse serverResponse) {
                                                    progressDialog.dismiss();
                                                    Log.d("newStyle", serverResponse.getBodyAsString());
                                                    Log.i("newStyle", String.format(Locale.getDefault(),
                                                            "ID %1$s: completed in %2$ds at %3$.2f Kbit/s. Response code: %4$d, body:[%5$s]",
                                                            uploadInfo.getUploadId(), uploadInfo.getElapsedTime() / 1000,
                                                            uploadInfo.getUploadRate(), serverResponse.getHttpCode(),
                                                            serverResponse.getBodyAsString()));
//                                    if (serverResponse.getBodyAsString().contains("Ticket created successfully!")) {
//                                        Toasty.success(CreateTicketActivity.this, getString(R.string.ticket_created_success), Toast.LENGTH_LONG).show();
//                                        finish();
//                                        editTextEmail.setText("");
//                                        id = 0;
//                                        Prefs.putString("newuseremail", null);
//                                        startActivity(new Intent(CreateTicketActivity.this, MainActivity.class));
//
//                                    }

                                                    if (serverResponse.getBodyAsString().contains("Permission denied")){
                                                        Toasty.warning(CreateTicketActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                                                        return;
                                                    }

                                                    try {
                                                        JSONObject jsonObject = new JSONObject(serverResponse.getBodyAsString());
                                                        //JSONObject jsonObject1 = jsonObject.getJSONObject("response");
                                                        String message = jsonObject.getString("message");
                                                        if (message.equals("Ticket created successfully!")) {
                                                            Intent intent = new Intent(CreateTicketActivity.this, MainActivity.class);
                                                            startActivity(intent);
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    String state = Prefs.getString("403", null);
                                                    try {
                                                        if (state.equals("403") && !state.equals("null")) {
                                                            Toasty.warning(CreateTicketActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                                                            Prefs.putString("403", "null");
                                                            return;
                                                        }
                                                    } catch (NullPointerException e) {
                                                        e.printStackTrace();
                                                    }


//                            Intent intent=new Intent(CreateTicketActivity.this,MainActivity.class);
//                            startActivity(intent);

                                                }

                                                @Override
                                                public void onCancelled(UploadInfo uploadInfo) {

                                                }
                                            })
                                                    .startUpload(); //Starting the upload
                                        } catch (MalformedURLException | NullPointerException | IllegalArgumentException | FileNotFoundException e) {
                                            e.printStackTrace();
                                        }
                                    }
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
                        }
                    }
                }



            }
        });
    }



    private class CreateNewTicket extends AsyncTask<File, Void, String> {
        String fname, lname, email, code;
        String subject;
        public String body;
        String phone;
        String mobile;
        int helpTopic;
        // int SLA;
        int priority;
        //int dept;
        int userID;
        int staff;
        String string;

        CreateNewTicket(String subject, String body,
                        int helpTopic, int priority, String fname, String email) {

            this.subject = subject;
            this.body = body;
            this.helpTopic = helpTopic;
            this.priority = priority;
            this.fname = fname;
            this.email = email;

        }

//        protected String doInBackground(String... urls) {
//
        //return new Helpdesk().postCreateTicket(userID, subject, body, helpTopic, priority, fname, lname, phone, email, code, staff, mobile+ collaborators, new File[]{new File(result)});
//        }

        @Override
        protected String doInBackground(File... files) {


            return new Helpdesk().postCreateTicket(subject, body, helpTopic, priority, fname, email);
        }

        protected void onPostExecute(String result) {
            //Toast.makeText(CreateTicketActivity.this, "api called", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

            if (result == null) {
                Toasty.error(CreateTicketActivity.this, getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();
                return;
            }


//            try{
//                JSONObject jsonObject=new JSONObject(result);
//                JSONObject jsonObject1=jsonObject.getJSONObject("response");
//                String message=jsonObject1.getString("message");
//                if (message.equals("Permission denied, you do not have permission to access the requested page.")){
//                    Toasty.warning(CreateTicketActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
//                    Prefs.putString("403", "null");
//                    return;
//                }
//
//            }catch (JSONException e){
//                e.printStackTrace();
//            }
            String state=Prefs.getString("403",null);
////                if (message1.contains("The ticket id field is required.")){
////                    Toasty.warning(TicketDetailActivity.this, getString(R.string.please_select_ticket), Toast.LENGTH_LONG).show();
////                }
////                else if (message1.contains("The status id field is required.")){
////                    Toasty.warning(TicketDetailActivity.this, getString(R.string.please_select_status), Toast.LENGTH_LONG).show();
////                }
////               else
            try {
                if (state.equals("403") && !state.equals("null")) {
                    Toasty.warning(CreateTicketActivity.this, getString(R.string.permission), Toast.LENGTH_LONG).show();
                    Prefs.putString("403", "null");
                    return;
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }

            if (result.contains("Ticket created successfully!")) {
                Toasty.success(CreateTicketActivity.this, getString(R.string.ticket_created_success), Toast.LENGTH_LONG).show();
                finish();
                Prefs.putString("newuseremail",null);
                startActivity(new Intent(CreateTicketActivity.this, MainActivity.class));

            }


        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        String fileName;
        String filePath = null;
        try {
            if (data.toString().equals("")) {
                attachment_layout.setVisibility(View.GONE);
            } else {
                attachment_layout.setVisibility(View.VISIBLE);
                switch (requestCode) {
                    case Constant.REQUEST_CODE_PICK_IMAGE:
                        if (resultCode == RESULT_OK) {
                            ArrayList<ImageFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_IMAGE);
                            StringBuilder builder = new StringBuilder();
                            for (ImageFile file : list) {
                                filePath = file.getPath();
                                Log.d("filePath", path);
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length() / 1024;
                            if (size > 6000) {
                                Toasty.info(CreateTicketActivity.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos = path.lastIndexOf("/");
                                fileName = path.substring(pos + 1, path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName", fileName);
                            }
                        }
                        break;
                    case Constant.REQUEST_CODE_PICK_VIDEO:
                        if (resultCode == RESULT_OK) {
                            ArrayList<VideoFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_VIDEO);
                            StringBuilder builder = new StringBuilder();
                            for (VideoFile file : list) {
                                filePath = file.getPath();
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length() / 1024;
                            if (size > 6000) {
                                Toasty.info(CreateTicketActivity.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos = path.lastIndexOf("/");
                                fileName = path.substring(pos + 1, path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName", fileName);
                            }
                        }
                        break;
                    case Constant.REQUEST_CODE_PICK_AUDIO:
                        if (resultCode == RESULT_OK) {
                            ArrayList<AudioFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_AUDIO);
                            StringBuilder builder = new StringBuilder();
                            for (AudioFile file : list) {
                                filePath = file.getPath();
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length() / 1024;
                            if (size > 6000) {
                                Toasty.info(CreateTicketActivity.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos = path.lastIndexOf("/");
                                fileName = path.substring(pos + 1, path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName", fileName);
                            }
                        }
                        break;
                    case Constant.REQUEST_CODE_PICK_FILE:
                        if (resultCode == RESULT_OK) {
                            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                            StringBuilder builder = new StringBuilder();
                            for (NormalFile file : list) {
                                filePath = file.getPath();
                                builder.append(path + "\n");
                            }
                            File file = new File(filePath);
                            long size = file.length() / 1024;
                            if (size > 6000) {
                                Toasty.info(CreateTicketActivity.this, getString(R.string.fileSize), Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                path = filePath;
                                int pos = path.lastIndexOf("/");
                                fileName = path.substring(pos + 1, path.length());
                                attachmentFileName.setText(fileName);
                                Log.d("fileName", fileName);
                            }
                        }
                        break;

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED){
            switch (requestCode) {
                case 3:
                    break;
                //Read External Storage
                case 4:
                    Intent imageIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(imageIntent, 11);
                    break;
                //Camera
                case 5:
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 12);
                    }
                    break;

            }

            Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

        }

    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try {

                URL url = new URL(Constants.URL + "authenticate"); // here is your URL path

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", Prefs.getString("USERNAME", null));
                postDataParams.put("password", Prefs.getString("PASSWORD", null));
                Log.e("params",postDataParams.toString());

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

                int responseCode=conn.getResponseCode();

                if (responseCode == HttpsURLConnection.HTTP_OK) {

                    BufferedReader in=new BufferedReader(new
                            InputStreamReader(
                            conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line="";

                    while((line = in.readLine()) != null) {

                        sb.append(line);
                        break;
                    }

                    in.close();
                    return sb.toString();

                }
                else {
                    return new String("false : "+responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        @Override
        protected void onPostExecute(String result) {
            //progressDialog.dismiss();
            //Toast.makeText(getApplicationContext(), result,
            //Toast.LENGTH_LONG).show();
            Log.d("resultFromNewCall",result);
            try {
                JSONObject jsonObject=new JSONObject(result);
                JSONObject jsonObject1=jsonObject.getJSONObject("data");
                token = jsonObject1.getString("token");
                Prefs.putString("TOKEN", token);
                Log.d("TOKEN",token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
//            try {
//                uploadMultipartData();
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }

        }
    }
    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key= itr.next();
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
    public void onShowRationalDialog(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app.");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onDialogShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    @Override
    public void onShowSettings(final PermissionInterface permissionInterface, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("We need permissions for this app. Open setting screen?");
        builder.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionInterface.onSettingsShown();
            }
        });
        builder.setNegativeButton(R.string.btn_cancel, null);
        builder.show();
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        Log.d("requestCode",""+requestCode);

        if (document==1){
            Intent intent4 = new Intent(this, NormalFilePickActivity.class);
            intent4.putExtra(Constant.MAX_NUMBER, 1);
            //intent4.putExtra(IS_NEED_FOLDER_LIST, true);
            intent4.putExtra(NormalFilePickActivity.SUFFIX,
                    new String[] {"xlsx", "xls", "doc", "dOcX", "ppt", ".pptx", "pdf"});
            startActivityForResult(intent4, Constant.REQUEST_CODE_PICK_FILE);
//            new MaterialFilePicker()
//                    .withActivity(TicketReplyActivity.this)
//                    .withRequestCode(FILE_PICKER_REQUEST_CODE)
//                    .withHiddenFiles(true)
//                    .start();
            document=0;
        }
        if (gallery==2){
            Intent intent1 = new Intent(this, ImagePickActivity.class);
            intent1.putExtra(IS_NEED_CAMERA, true);
            intent1.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent1, Constant.REQUEST_CODE_PICK_IMAGE);
//            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//            intent.setType("image/*");
//            startActivityForResult(intent, PICKFILE_REQUEST_CODE);
            gallery=0;
        }

        if (camera==3){
            Intent intent2 = new Intent(this, VideoPickActivity.class);
            intent2.putExtra(IS_NEED_CAMERA, true);
            intent2.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent2, Constant.REQUEST_CODE_PICK_VIDEO);
//            Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//
//            startActivityForResult(intent, CAMERA_REQUEST);
            camera=0;
        }
        if (audio==4){
            Intent intent3 = new Intent(this, AudioPickActivity.class);
            intent3.putExtra(IS_NEED_RECORDER, true);
            intent3.putExtra(Constant.MAX_NUMBER, 1);
            startActivityForResult(intent3, Constant.REQUEST_CODE_PICK_AUDIO);
            audio=0;

        }
    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toasty.warning(CreateTicketActivity.this,getString(R.string.permission_camera_denied),Toast.LENGTH_SHORT).show();
        return;
    }
    private void reqPermissionCamera() {
        new AskPermission.Builder(this).setPermissions(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setCallback(this)
                .setErrorCallback(this)
                .request(PICKFILE_REQUEST_CODE);
    }

    public void setUpViews() {
        JSONObject jsonObject;
        Data data;
        String json = Prefs.getString("DEPENDENCY", "");
        try {
            helptopicItems = new ArrayList<>();
            helptopicItems.add(new Data(0, "--"));
            jsonObject = new JSONObject(json);
            JSONArray jsonArrayHelpTopics = jsonObject.getJSONArray("helptopics");
            for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {
                Data data1 = new Data(Integer.parseInt(jsonArrayHelpTopics.getJSONObject(i).getString("id")), jsonArrayHelpTopics.getJSONObject(i).getString("topic"));
                helptopicItems.add(data1);
                Collections.sort(helptopicItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }


//            typeItems=new ArrayList<>();
//            typeItems.add(new Data(0,"--"));
//            jsonObject=new JSONObject(json);
//            JSONArray jsonArrayType=jsonObject.getJSONArray("type");
//            for (int i=0;i<jsonArrayType.length();i++){
//                Data data1 = new Data(Integer.parseInt(jsonArrayType.getJSONObject(i).getString("id")), jsonArrayType.getJSONObject(i).getString("name"));
//
//                typeItems.add(data1);
//                Collections.sort(typeItems, new Comparator<Data>() {
//                    @Override
//                    public int compare(Data lhs, Data rhs) {
//                        return lhs.getName().compareTo(rhs.getName());
//                    }
//                });
//            }

            JSONArray jsonArrayPriorities = jsonObject.getJSONArray("priorities");
            priorityItems = new ArrayList<>();
            priorityItems.add(new Data(0, "--"));
            for (int i = 0; i < jsonArrayPriorities.length(); i++) {
                Data data2 = new Data(Integer.parseInt(jsonArrayPriorities.getJSONObject(i).getString("priority_id")), jsonArrayPriorities.getJSONObject(i).getString("priority"));
                priorityItems.add(data2);
                Collections.sort(priorityItems, new Comparator<Data>() {
                    @Override
                    public int compare(Data lhs, Data rhs) {
                        return lhs.getName().compareTo(rhs.getName());
                    }
                });
            }
        } catch (JSONException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }



        spinnerHelpArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, helptopicItems); //selected item will look like a spinner set from XML
        spinnerHelpArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerhelp.setAdapter(spinnerHelpArrayAdapter);

        spinnerPriArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, priorityItems); //selected item will look like a spinner set from XML
        spinnerPriArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerpri.setAdapter(spinnerPriArrayAdapter);


        spinnerpri.setDropDownWidth(1000);

    }
    private class FetchDependency extends AsyncTask<String, Void, String> {
        String unauthorized;

        protected String doInBackground(String... urls) {

            return new Helpdesk().getDependency();

        }

        protected void onPostExecute(String result) {
            Log.d("Depen Response : ", result + "");
            refresh.clearAnimation();
            if (result==null) {
//                try {
//                    unauthorized = Prefs.getString("unauthorized", null);
//                    if (unauthorized.equals("true")) {
//                        loading.setText("Oops! Something went wrong.");
//                        progressDialog.setVisibility(View.INVISIBLE);
//                        textViewtryAgain.setVisibility(View.VISIBLE);
//                        textViewrefresh.setVisibility(View.VISIBLE);
//                        Prefs.putString("unauthorized", "false");
//                        textViewrefresh.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                            }
//                        });
//
//                    }
//
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
            }
//            String state=Prefs.getString("403",null);
//
//            try {
//                if (state.equals("403") && !state.equals(null)) {
//                    Toasty.info(SplashActivity.this, getString(R.string.roleChanged), Toast.LENGTH_LONG).show();
//                    Prefs.clear();
//                    Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
//                    Prefs.putString("403", "null");
//                    startActivity(intent);
//                    return;
//                }
//            }catch (NullPointerException e){
//                e.printStackTrace();
//            }


            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject jsonObject1 = jsonObject.getJSONObject("data");
                Prefs.putString("DEPENDENCY", jsonObject1.toString());
                // Preference.setDependencyObject(jsonObject1, "dependency");
                JSONArray jsonArrayDepartments = jsonObject1.getJSONArray("departments");
                for (int i = 0; i < jsonArrayDepartments.length(); i++) {
                    keyDepartment += jsonArrayDepartments.getJSONObject(i).getString("id") + ",";
                    valueDepartment += jsonArrayDepartments.getJSONObject(i).getString("name") + ",";
                }
                Prefs.putString("keyDept", keyDepartment);
                Prefs.putString("valueDept", valueDepartment);


                JSONArray jsonArraySla = jsonObject1.getJSONArray("sla");
                for (int i = 0; i < jsonArraySla.length(); i++) {
                    keySLA += jsonArraySla.getJSONObject(i).getString("id") + ",";
                    valueSLA += jsonArraySla.getJSONObject(i).getString("name") + ",";
                }
                Prefs.putString("keySLA", keySLA);
                Prefs.putString("valueSLA", valueSLA);

                JSONArray jsonArrayStaffs = jsonObject1.getJSONArray("staffs");
                for (int i = 0; i < jsonArrayStaffs.length(); i++) {
                    keyName +=jsonArrayStaffs.getJSONObject(i).getString("first_name") + jsonArrayStaffs.getJSONObject(i).getString("last_name") +",";
                    keyStaff += jsonArrayStaffs.getJSONObject(i).getString("id") + ",";
                    valueStaff += jsonArrayStaffs.getJSONObject(i).getString("email") + ",";
                }
                Prefs.putString("keyName",keyName);
                Prefs.putString("keyStaff", keyStaff);
                Prefs.putString("valueStaff", valueStaff);

                JSONArray jsonArrayType = jsonObject1.getJSONArray("type");
                for (int i = 0; i < jsonArrayType.length(); i++) {
                    keyType += jsonArrayType.getJSONObject(i).getString("id") + ",";
                    valueType += jsonArrayType.getJSONObject(i).getString("name") + ",";
                }
                Prefs.putString("keyType", keyType);
                Prefs.putString("valueType", valueType);

//                JSONArray jsonArrayStaffs = jsonObject1.getJSONArray("staffs");
//                for (int i = 0; i < jsonArrayStaffs.length(); i++) {
//                    keyStaff += jsonArrayStaffs.getJSONObject(i).getString("id") + ",";
//                    valueStaff += jsonArrayStaffs.getJSONObject(i).getString("email") + ",";
//                }


//                JSONArray jsonArrayTeams = jsonObject1.getJSONArray("teams");
//                for (int i = 0; i < jsonArrayTeams.length(); i++) {
//                    keyTeam += jsonArrayTeams.getJSONObject(i).getString("id") + ",";
//                    valueTeam += jsonArrayTeams.getJSONObject(i).getString("name") + ",";
//                }

                //Set<String> keyPri = new LinkedHashSet<>();
                // Set<String> valuePri = new LinkedHashSet<>();
                JSONArray jsonArrayPriorities = jsonObject1.getJSONArray("priorities");
                for (int i = 0; i < jsonArrayPriorities.length(); i++) {
                    // keyPri.add(jsonArrayPriorities.getJSONObject(i).getString("priority_id"));
                    //valuePri.add(jsonArrayPriorities.getJSONObject(i).getString("priority"));
                    keyPriority += jsonArrayPriorities.getJSONObject(i).getString("priority_id") + ",";
                    valuePriority += jsonArrayPriorities.getJSONObject(i).getString("priority") + ",";
                }
                Prefs.putString("keyPri", keyPriority);
                Prefs.putString("valuePri", valuePriority);
                //Prefs.putOrderedStringSet("keyPri", keyPri);
                // Prefs.putOrderedStringSet("valuePri", valuePri);
                //Log.d("Testtttttt", Prefs.getOrderedStringSet("keyPri", keyPri) + "   " + Prefs.getOrderedStringSet("valuePri", valuePri));


                JSONArray jsonArrayHelpTopics = jsonObject1.getJSONArray("helptopics");
                for (int i = 0; i < jsonArrayHelpTopics.length(); i++) {

                    keyTopic += jsonArrayHelpTopics.getJSONObject(i).getString("id") + ",";
                    valueTopic += jsonArrayHelpTopics.getJSONObject(i).getString("topic") + ",";
                }

                Prefs.putString("keyHelpTopic", keyTopic);
                Prefs.putString("valueHelptopic", valueTopic);

                JSONArray jsonArrayStatus = jsonObject1.getJSONArray("status");
                for (int i = 0; i < jsonArrayStatus.length(); i++) {

                    keyStatus += jsonArrayStatus.getJSONObject(i).getString("id") + ",";
                    valueStatus += jsonArrayStatus.getJSONObject(i).getString("name") + ",";

                }
                Prefs.putString("keyStatus", keyStatus);
                Prefs.putString("valueStatus", valueStatus);

                JSONArray jsonArraySources = jsonObject1.getJSONArray("sources");
                for (int i = 0; i < jsonArraySources.length(); i++) {
                    keySource += jsonArraySources.getJSONObject(i).getString("id") + ",";
                    valueSource += jsonArraySources.getJSONObject(i).getString("name") + ",";
                }

                Prefs.putString("keySource", keySource);
                Prefs.putString("valueSource", valueSource);

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


            } catch (JSONException | NullPointerException e) {
                //Toasty.error(SplashActivity.this, "Parsing Error!", Toast.LENGTH_LONG).show();

                e.printStackTrace();
            }

//            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//            builder.setTitle("Welcome to FAVEO");
//            //builder.setMessage("After 2 second, this dialog will be closed automatically!");
//            builder.setCancelable(true);
//
//            final AlertDialog dlg = builder.create();
//
//            dlg.show();
//
//            final Timer t = new Timer();
//            t.schedule(new TimerTask() {
//                public void run() {
//                    dlg.dismiss(); // when the task active then close the dialog
//                    t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
//                }
//            }, 3000);
        }
    }

    @Override
    public void onBackPressed() {
        if (!msgedittext.getText().toString().equals("")||!subedittext.getText().toString().equals("")){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateTicketActivity.this);

            // Setting Dialog Message
            alertDialog.setMessage(R.string.discard);

            // Setting Icon to Dialog
            alertDialog.setIcon(R.mipmap.ic_launcher);

            // Setting Positive "Yes" Button

            alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Write your code here to invoke YES event
                    //Toast.makeText(getApplicationContext(), "You clicked on YES", Toast.LENGTH_SHORT).show();
                    finish();
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
        }
        else{
            finish();
        }
    }
}
