package com.berhasil.imgretrofit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.img_title)
    EditText imgTitle;
    @BindView(R.id.imageView)
    ImageView imgView;
    @BindView(R.id.choseBtn)
    Button choseBtn;
    @BindView(R.id.uploadBtn)
    Button uploadBtn;

    private static final int IMG_REQUEST = 777;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        choseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choseBtn :
                selectImage();
                break;
            case R.id.uploadBtn :
                uploadImage();
                break;
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMG_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==IMG_REQUEST && resultCode==RESULT_OK && data!=null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imgView.setImageBitmap(bitmap);
                imgView.setVisibility(View.VISIBLE);
                imgTitle.setVisibility(View.VISIBLE);
                choseBtn.setEnabled(false);
                uploadBtn.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imgToString() {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        final  int destWidth = 600;
        int destHeigt = height/(width / destWidth);
        Bitmap b2 = bitmap.createScaledBitmap(bitmap, destWidth, destHeigt, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        b2.compress(Bitmap.CompressFormat.JPEG, 70, byteArrayOutputStream);
        byte [] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte, Base64.DEFAULT);
    }

    private void uploadImage() {
        String Image = imgToString();
        String Title = imgTitle.getText().toString();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<imageClass> call = apiInterface.uploadImg(Title, Image);

        call.enqueue(new Callback<imageClass>() {
            @Override
            public void onResponse(Call<imageClass> call, Response<imageClass> response) {
                imageClass imageClass = response.body();
                Toast.makeText(MainActivity.this, "Server response : "+ imageClass.getResponse(), Toast.LENGTH_SHORT).show();
                imgView.setVisibility(View.GONE);
                imgTitle.setVisibility(View.GONE);
                choseBtn.setEnabled(true);
                uploadBtn.setEnabled(false);
                imgTitle.setText("");
            }

            @Override
            public void onFailure(Call<imageClass> call, Throwable t) {
                Toast.makeText(MainActivity.this, "gagal menyimpan data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
