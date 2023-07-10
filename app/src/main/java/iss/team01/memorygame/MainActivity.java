package iss.team01.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Intent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import com.bumptech.glide.Glide;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Button scrapeBtn;

    private EditText inputURL;
    private ImageButton imageButton1;
    private TextView downloadText;
    private int[] imageButtons = new int[]{R.id.imageButton1, R.id.imageButton2, R.id.imageButton3, R.id.imageButton4, R.id.imageButton5, R.id.imageButton6, R.id.imageButton7, R.id.imageButton8, R.id.imageButton9, R.id.imageButton10, R.id.imageButton11, R.id.imageButton12, R.id.imageButton13, R.id.imageButton14, R.id.imageButton15, R.id.imageButton16, R.id.imageButton17, R.id.imageButton18, R.id.imageButton19, R.id.imageButton20};
    private boolean[] imageButtonsFlag = new boolean[20];
    private int[] chooseImage = new int[6];
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        downloadText = this.findViewById(R.id.textView);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);//Achieve full screen
        inputURL = findViewById(R.id.editText1);
        scrapeBtn = findViewById(R.id.scrapeBtn);

        setButton();
        // button to start scraper
        scrapeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = 0;
                if(isHttpUrl(inputURL.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Downloading", Toast.LENGTH_SHORT).show();
                    downloadText.setText(0 + " of 20");
                    startDownloadImage(inputURL.getText().toString());
                }
                else{
                    Toast.makeText(MainActivity.this, "Please enter the correct URL", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    public void setButton(){
        for (int i = 0; i < imageButtons.length; i++) {
            ImageButton btn = findViewById(imageButtons[i]);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id != R.id.scrapeBtn){
            ImageButton imageButton = findViewById(id);
            boolean flag = true;
            for(int i = 0; i < 6; i++){
                if(id == chooseImage[i])
                    flag = false;
            }
            if ((flag == true) && (count < 6)){
                imageButton.setImageResource(R.drawable.dodo);
                chooseImage[count] = id;
                count++;
            }
            else if (count == 6)
                Toast.makeText(MainActivity.this, "You have selected a sufficient number of images", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this, "You have selected this images", Toast.LENGTH_SHORT).show();
        }
    }

    protected void startDownloadImage(String imgURL) {

        List<String> imageUrls = new ArrayList<>();
        List<String> destFilename = new ArrayList<>();
        List<File> destFile= new ArrayList<>();
        File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        for(int i = 0; i < 20; i++){
            destFilename.add(UUID.randomUUID().toString() +
                    ".jpg");
            destFile.add(new File(dir, destFilename.get(i)));
        }
        // creating a background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d("MainActivity", "Scraping images...");
                    // Fetch the HTML code of the website
                    // TODO: change it to take in user input
                    Document doc = Jsoup.connect(imgURL).get();
                    //"https://stocksnap.io/"
                    Log.d("MainActivity", "Scrape successful.");
                    // Select all .jpeg elements tagged with <img> on the page
                    Elements images = doc.select("img[src$=.jpg]");
                    // Narrow it down to the first 20
                    Elements first20Images = images.stream()
                            .limit(20)
                            .collect(Collectors.toCollection(Elements::new));
                    // for debugging, comment out if not needed
                    Log.d("MainActivity", "Number of unique images in first20: " + first20Images.size());
                    Log.d("MainActivity", "Adding images to list.");
                    // Iterate over the <img> elements and add their source URLs to the list
                    for (Element image : first20Images) {
                        String imageUrl = image.attr("src");
                        Log.d("MainActivity", imageUrl);
                        imageUrls.add(imageUrl);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < imageUrls.size(); i++){
                    ImageDownloader imgDL = new ImageDownloader();
                    if (imgDL.downloadImage(imageUrls.get(i), destFile.get(i))) {
                        int finalI = i;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap bitmap = BitmapFactory.decodeFile(destFile.get(finalI).getAbsolutePath());
                                ImageButton imageView = findViewById(imageButtons[finalI]);
                                imageView.setImageBitmap(bitmap);
                                downloadText.setText(finalI + 1 + " of 20");
                                imageButtonsFlag[finalI] = true;
                                if(finalI == 19)
                                    Toast.makeText(MainActivity.this, "Download complete", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }

            }
        }).start();
    }

    public static boolean isHttpUrl(String urls) {
        try {
            URL url = new URL(urls);
            url.toURI();
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}




