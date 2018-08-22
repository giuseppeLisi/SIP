package com.example.asus.sip;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class EncryptionActivity extends AppCompatActivity {

    private Integer REQUEST_CAMERA=1, SELECT_FILE=0;

    private ImageView boxImageEnc;
    private Button buttonGoEncry;
    public EditText textToEncry;

    private String userChoosenTask;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encryption);

        buttonGoEncry = (Button) findViewById(R.id.button_to_encryption);
        textToEncry = (EditText) findViewById(R.id.editText_encryption);

        boxImageEnc = findViewById(R.id.box_image_view);
        boxImageEnc = (ImageView) findViewById(R.id.box_image_view);

        FloatingActionButton fabCamera = (FloatingActionButton) findViewById(R.id.fab);
        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });
    }

    //Verifica permessi ed eventualmente mostra un dialog di richiesta
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case UtilityPermission.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                }
                break;
        }
    }

    //Metodo per la selezione della fonte dell'immagine
    private void selectImage() {

        //Sequenza di selezione del FAB
        final CharSequence[] items ={"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(EncryptionActivity.this);

        builder.setTitle("Add Image");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                boolean result=UtilityPermission.checkPermission(EncryptionActivity.this);
                if (items[i].equals("Camera")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[i].equals("Gallery")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();
                }
            }
        });
        builder.show();
    }

    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);

        //Setta visibilità button e editText per acquisire la stringa da criptare
        buttonGoEncry.setVisibility(View.VISIBLE);
        //textToEncry.setVisibility(View.VISIBLE);
    }

    //Manipola il risultato proveniente da startActivityForResult() e richiama uno dei due metodi
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Setta visibilità button e editText per acquisire la stringa da criptare
        buttonGoEncry.setVisibility(View.VISIBLE);
        textToEncry.setVisibility(View.VISIBLE);

        boxImageEnc.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //Setta visibilità button e editText per acquisire la stringa da criptare
        buttonGoEncry.setVisibility(View.VISIBLE);
        textToEncry.setVisibility(View.VISIBLE);

        boxImageEnc.setImageBitmap(bm);
    }

    public void signIn(View view) {

        final LayoutInflater li = LayoutInflater.from(this);
        final View prompt = li.inflate(R.layout.sign_in, null);

        //Dialog per inserire la password e verificarla
        final AlertDialog.Builder builderSign = new AlertDialog.Builder(this);
        builderSign.setView(prompt);
        final EditText check1 = (EditText) prompt.findViewById(R.id.edit_text_password);
        final EditText check2 = (EditText) prompt.findViewById(R.id.edit_text_confirmpassword);
        builderSign.setTitle("Please enter a password")
                .setCancelable(false)
                .setPositiveButton("Insert", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    //Inserire operazioni del pulsante positivo
                        String passwordCheck2 = check2.getText().toString();
                        String passwordCheck1 = check1.getText().toString();
                            if (passwordCheck1.equals(passwordCheck2)) {
                                password = passwordCheck1;
                                Toast.makeText(EncryptionActivity.this, "OK", Toast.LENGTH_SHORT)
                                        .show();
                            }
                            else {
                                Toast.makeText(EncryptionActivity.this, "Password uncorrected", Toast.LENGTH_SHORT)
                                        .show();
                            }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
        //Inserire operazioni per il ritornare allìattività
                    }
                });
        builderSign.create().show();

    }

}
