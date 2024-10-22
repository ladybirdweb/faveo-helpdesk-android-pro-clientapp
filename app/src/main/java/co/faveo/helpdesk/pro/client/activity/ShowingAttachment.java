package co.faveo.helpdesk.pro.client.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.kishan.askpermission.AskPermission;
import com.kishan.askpermission.ErrorCallback;
import com.kishan.askpermission.PermissionCallback;
import com.kishan.askpermission.PermissionInterface;
import com.pixplicity.easyprefs.library.Prefs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLConnection;
import java.util.ArrayList;

import co.faveo.helpdesk.pro.client.R;
import co.faveo.helpdesk.pro.client.adapter.AttachmnetAdapter;
import co.faveo.helpdesk.pro.client.model.AttachmentModel;
import es.dmoral.toasty.Toasty;

public class ShowingAttachment extends AppCompatActivity implements PermissionCallback, ErrorCallback {
    static final Integer WRITE_EXST = 0x3;
    static final Integer READ_EXST = 0x4;
    private static final int PICKFILE_REQUEST_CODE = 1234;
    ImageView imageView;
    Toolbar toolbar;
    TextView textView;
    String title;
    String type;
    String file;
    AttachmnetAdapter attachmnetAdapter;
    ArrayList<AttachmentModel> attachmentModels;
    String multipleName, multipleFile;
    String removeComma, removeCommaFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showing_attachment);
        Window window = ShowingAttachment.this.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(ShowingAttachment.this, R.color.mainActivityTopBar));
        ListView listView = (ListView) findViewById(R.id.attachment_list);
        reqPermissionCamera();
//        if (shouldAskPermissions()) {
//            askPermissions();
//        }
        try {
            multipleName = Prefs.getString("multipleName", null);
            multipleFile = Prefs.getString("multipleFile", null);
            removeComma = multipleName.substring(0, multipleName.length() - 1);
            removeCommaFile = multipleFile.substring(0, multipleFile.length() - 1);
            attachmentModels = new ArrayList<>();
            String[] fileArray = removeCommaFile.split(",");
            String[] animalsArray = removeComma.split(",");
            for (int i = 0; i < animalsArray.length; i++) {
                title = animalsArray[i];
                file = fileArray[i];
                Log.d("title", title);
                attachmentModels.add(new AttachmentModel(title, file));
            }
            Log.d("multipleN", removeComma);
            //title = Prefs.getString("fileName", null);
            //base64String = Prefs.getString("file", null);
            type = Prefs.getString("type", null);
            file = Prefs.getString("file", null);
            Log.d("type", type);
            Log.d("imagefile", Prefs.getString("base64Image", null));
        } catch (NullPointerException e) {
            e.printStackTrace();
        }


        //attachmentModels.add(new AttachmentModel(title,base64String));

        //attachmentModels.add(new AttachmentModel("second.jpg","secondfile_base64"));

        attachmnetAdapter = new AttachmnetAdapter(this, attachmentModels);
        listView.setAdapter(attachmnetAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AttachmentModel attachmentModel = attachmentModels.get(i);
                String title;
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                byte[] decodedString;
                String base64String;
                //Intent intent=new Intent(view.getContext(), ShowingAttachment.class);
                title = attachmentModel.getName();
                base64String = attachmentModel.getFile();
                decodedString = Base64.decode(base64String, Base64.DEFAULT);

                try {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();
                    File myFile = new File(path + "/" + title);
                    File file = new File(path + "/" + title);
                    Uri uri1 = Uri.fromFile(file);
                    Log.d("URI", uri1.toString());
                    myFile.createNewFile();
                    FileOutputStream fOut = new FileOutputStream(myFile);
                    fOut.write(decodedString);
                    OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
                    myOutWriter.append(title + "came from storage");
                    myOutWriter.close();
                    fOut.close();
                    Intent myIntent = new Intent(Intent.ACTION_VIEW);
                    myIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    String mime = URLConnection.guessContentTypeFromStream(new FileInputStream(myFile));
                    if (mime == null)
                        mime = URLConnection.guessContentTypeFromName(myFile.getName());
                    myIntent.setDataAndType(Uri.fromFile(myFile), mime);
                    view.getContext().startActivity(myIntent);
                    //view.getContext().startActivity(Intent.createChooser(myIntent, "Choose an app to open with"));

                    //Toast.makeText(ShowingAttachment.this, "Done writing SD 'mysdfile.txt'", Toast.LENGTH_SHORT).show();
                    //txtData.setText("");
                } catch (Exception e) {
                    //Toast.makeText(ShowingAttachment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(ShowingAttachment.this, "CLICKED AT:"+attachmentModel.getFile(), Toast.LENGTH_SHORT).show();
            }
        });
//        mediaPlayer = new MediaPlayer();
//        decodedString = Base64.decode(base64String, Base64.DEFAULT);
//        String text = new String(decodedString);

        toolbar = (Toolbar) findViewById(R.id.toolbarAttachment);
        imageView = (ImageView) toolbar.findViewById(R.id.imageViewBackAttachment);
//        touchImageView = (TouchImageView) findViewById(R.id.attachment_view);
        textView = (TextView) toolbar.findViewById(R.id.attachmenttitle);
//        pdfView = (PDFView) findViewById(R.id.pdfView);
//        textViewFileShow = (WebView) findViewById(R.id.textFile);
//        Uri uri = Uri.parse(text);
//        videoView = (VideoView) findViewById(R.id.videoView);
//        textViewFileShow.loadData(URLEncoder.encode(text).replaceAll("\\+", " "), "text/html", Xml.Encoding.UTF_8.toString());
//        try {
//            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
//            File myFile = new File(path+"/"+title);
//            File file = new File(path+"/"+title);
//            Uri uri1 = Uri.fromFile(file);
//            Log.d("URI",uri1.toString());
//            myFile.createNewFile();
//            FileOutputStream fOut = new FileOutputStream(myFile);
//            fOut.write(decodedString);
//            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
//            myOutWriter.append(title + "came from storage");
//            myOutWriter.close();
//            fOut.close();
//            Intent myIntent = new Intent(Intent.ACTION_VIEW);
//            myIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            String mime= URLConnection.guessContentTypeFromStream(new FileInputStream(myFile));
//            if(mime==null) mime=URLConnection.guessContentTypeFromName(myFile.getName());
//            myIntent.setDataAndType(Uri.fromFile(myFile), mime);
//            startActivity(myIntent);
//
//            //Toast.makeText(ShowingAttachment.this, "Done writing SD 'mysdfile.txt'", Toast.LENGTH_SHORT).show();
//            //txtData.setText("");
//        } catch (Exception e) {
//            Toast.makeText(ShowingAttachment.this, e.getMessage(), Toast.LENGTH_SHORT).show();
//        }
//        textViewFileShow.setMovementMethod(new ScrollingMovementMethod());
//        textViewFileShow.setText(text);
//        pdfView.fromBytes(decodedString).load();
////        String byteArray=new String(decodedString);
//        pdfView.fromBytes(decodedString).load();
        //df = new DecimalFormat("#.##");


//        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//        touchImageView.setImageBitmap(decodedByte);
        textView.setText(getString(R.string.attachment));
        imageView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void reqPermissionCamera() {
        new AskPermission.Builder(this).setPermissions(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .setCallback(this)
                .setErrorCallback(this)
                .request(PICKFILE_REQUEST_CODE);
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

    }

    @Override
    public void onPermissionsDenied(int requestCode) {
        Toasty.warning(ShowingAttachment.this, getString(R.string.permission_camera_denied), Toast.LENGTH_SHORT).show();
        return;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
