package com.example.yamuna.objectmonitormicrosoftvision;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.microsoft.projectoxford.vision.VisionServiceClient;
import com.microsoft.projectoxford.vision.VisionServiceRestClient;
import com.microsoft.projectoxford.vision.contract.AnalysisResult;
import com.microsoft.projectoxford.vision.contract.Caption;
import com.microsoft.projectoxford.vision.rest.VisionServiceException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button btn;
    TextView txt;

    public VisionServiceClient visionServiceClient  = new VisionServiceRestClient("236d9f7d00af4ea9bf3470b6d3f5647b");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bitmap myBitmap= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.chair);
        image=(ImageView)findViewById(R.id.image);
        btn=(Button)findViewById(R.id.btn);


        image.setImageBitmap(myBitmap);
        ByteArrayOutputStream outputStream= new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final AsyncTask<InputStream,String,String> visionTask = new AsyncTask<InputStream, String, String>() {

                    ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
                    @Override
                    protected String doInBackground(InputStream... params) {
                        try{
                            publishProgress("Identifying...");
                            String[] features = {"Description"};
                            String[] details = {};

                            AnalysisResult result = visionServiceClient.analyzeImage(params[0],features,details);
                            String strResult = new Gson().toJson(result);
                            return strResult;

                        } catch (Exception e) {
                            e.printStackTrace();
                            return null;
                        }
                    }

                    @Override
                    protected void onPreExecute() {
                        mDialog.show();
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        mDialog.dismiss();

                        AnalysisResult result = new Gson().fromJson(s,AnalysisResult.class);
                        txt=(TextView)findViewById(R.id.txtdesc);
                        StringBuilder stringBuilder = new StringBuilder();
                        for (Caption caption:result.description.captions)
                        {
                            stringBuilder.append(caption.text);
                        }
                        txt.setText(stringBuilder);
                    }

                    @Override
                    protected void onProgressUpdate(String... values) {
                        mDialog.setMessage(values[0]);
                    }
                };
                visionTask.execute(inputStream);
            }
        });
    }
}
