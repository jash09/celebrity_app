package com.example.celebrityapp;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;

    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebnames = new ArrayList<String>();
    int chosenceleb=0;
    String[] answers = new String[4];


    public  class ImageDownloader extends AsyncTask<String,Void,Bitmap>{


        @Override
        protected Bitmap doInBackground(String... urls) {

            try{
                URL url= new URL(urls[0]);
                HttpURLConnection connection2=(HttpURLConnection)url.openConnection();
                connection2.connect();
                InputStream inputStream = connection2.getInputStream();
                Bitmap mybitmap = BitmapFactory.decodeStream(inputStream);
                return mybitmap;
            }catch(Exception e){

                e.printStackTrace();
                return null;
            }
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String>{


        @Override
        protected String doInBackground(String... urls) {

            String result="";
            URL url;
            HttpURLConnection connection=null;
            try{

                url = new URL(urls[0]);

                connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);
                int data =reader.read();
                while (data!=-1){

                    char current=(char) data;
                    result=result+current;
                    data = reader.read();
                }
                return result;
            }catch(Exception e){

                e.printStackTrace();
                return null;
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView);

        DownloadTask task =new DownloadTask();
        String result = null;
        try{

            result=task.execute("http://www.posh24.se/kandisar").get();
            String[] splitResult = result.split("<div class=\"listedArticles\">");
            Pattern p =Pattern.compile("img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);
            while (m.find()) {

               celeburls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);
            while (m.find()){

                celebnames.add(m.group(1));
            }
            Random rand = new Random();
            chosenceleb=rand.nextInt(celeburls.size());
            ImageDownloader imagetask = new ImageDownloader();
            Bitmap celebImage = imagetask.execute(celeburls.get(chosenceleb)).get();
            imageView.setImageBitmap(celebImage);

        }catch (Exception e){
            e.printStackTrace();

        }
    }
}
