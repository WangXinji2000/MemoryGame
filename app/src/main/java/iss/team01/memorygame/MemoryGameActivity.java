package iss.team01.memorygame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import java.util.ArrayList;
import java.util.Random;

public class MemoryGameActivity extends AppCompatActivity implements View.OnClickListener{

    private int[] imageButtons = new int[]{R.id.imageButton1, R.id.imageButton2, R.id.imageButton3, R.id.imageButton4, R.id.imageButton5, R.id.imageButton6, R.id.imageButton7, R.id.imageButton8, R.id.imageButton9, R.id.imageButton10, R.id.imageButton11, R.id.imageButton12};
    private int compareId;
    private boolean[] imageButtonFlags = new boolean[12];
    private boolean flag = false;
    private ArrayList imageLocations = new ArrayList<>();
    private int[] arr = {0, 1, 2, 3, 4, 5, 0, 1, 2, 3, 4, 5};
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_memory_game);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        imageLocations = getIntent().getCharSequenceArrayListExtra("imageUrls");
        setButton();
    }

    public void onClick(View view) {
        int id = view.getId();
        flag = !flag;
        int image1 = 100;
        int image2 = 101;
        for(int i = 0; i < imageButtons.length; i++){
            if(id == imageButtons[i])
                image1 = i;
            if(compareId == imageButtons[i])
                image2 = i;
        }
        ImageButton btn = findViewById(id);
        ImageButton btn2;

        if(flag){
            Bitmap bitmap = BitmapFactory.decodeFile(imageLocations.get(arr[image1]).toString());
            btn.setImageBitmap(bitmap);
            compareId = id;
        }
        else{
            if(id == compareId){
                btn.setImageResource(R.drawable.dodo);
            }
            else{
                Bitmap bitmap = BitmapFactory.decodeFile(imageLocations.get(arr[image1]).toString());
                btn.setImageBitmap(bitmap);
                btn2 = findViewById(compareId);
                if(arr[image1] == arr[image2]){
                        imageButtonFlags[image1] = true;
                        btn2.setImageResource(R.drawable.correct);
                        imageButtonFlags[image2] = true;
                        btn.setImageResource(R.drawable.correct);
                        count++;
                        if(count == 6){
                            Intent intent = new Intent(MemoryGameActivity.this,MainActivity.class);
                            startActivity(intent);
                        }

                }
                else{
                    
                    btn.setImageResource(R.drawable.dodo);
                    btn2.setImageResource(R.drawable.dodo);
                }
            }
            compareId = 0;
        }
    }


    public void setButton(){
        count = 0;
        Random random = new Random();
        for (int i = arr.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = arr[i];
            arr[i] = arr[j];
            arr[j] = temp;
        }
        for (int i = 0; i < imageButtons.length; i++) {
            ImageButton btn = findViewById(imageButtons[i]);
            //Bitmap bitmap = BitmapFactory.decodeFile(imageLocations.get(arr[i]).toString());
            //btn.setImageBitmap(bitmap);
            btn.setImageResource(R.drawable.dodo);
            if (btn != null) {
                btn.setOnClickListener(this);
            }
        }
    }


}