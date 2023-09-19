package com.example.gridlayout;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Results extends AppCompatActivity {
    private boolean won;
    private int seconds=0;
    public Results(){
    }

    public Results(boolean tempWon, int tempSeconds){
        won = tempWon;
        seconds = tempSeconds;
        System.out.println("Called Results Constructor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results_page);
        TextView resultTextTV = findViewById(R.id.ResultsText);

        Intent intent = getIntent();
        boolean won = intent.getBooleanExtra("wonBoolean",true);
        int time = intent.getIntExtra("time",0);

        if (!won) {
            // losing results page
            System.out.println("LOST");
            resultTextTV.setText("Used "+ time +" seconds.\n You lost.");
        } else if (won) {
            // winning results page
            System.out.println("WON");
            resultTextTV.setText("Used "+ time +" seconds.\n You won.\n Good job!");
        } else {
            System.out.println("Error: remainingCellsTillWin is a negative variable and it should not be.");
        }
        Button btn = (Button) findViewById(R.id.playAgainButton);
        btn.setOnClickListener(this::onClickButton);
    }

    public void onClickButton(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
