package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import com.example.gridlayout.Coordinate;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int COLUMN_COUNT = 10;

    // save the TextViews of all cells in an array, so later on,
    // when a TextView is clicked, we know which cell it is
    private ArrayList<TextView> cell_tvs;
    private ArrayList<Integer> mineCells;
    private ArrayList<Integer> revealedCells;

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    private Coordinate nodeIDToCoordinates(int nodeID){
        int quotient, remainder;
        quotient = nodeID/COLUMN_COUNT;
        remainder = nodeID%COLUMN_COUNT;
        Coordinate result = new Coordinate(quotient, remainder);
        return result;
    };

    private int coordinatesToNodeID(int row, int col){
        return row*COLUMN_COUNT+col;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();

        // Method (2): add 12*10 dynamically created cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i<=11; i++) {
            for (int j=0; j<=9; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(30) );
                tv.setWidth( dpToPixel(30) );
                tv.setTextSize( 5 );//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.GRAY);
                tv.setBackgroundColor(Color.GRAY);
                tv.setOnClickListener(this::onClickTV);

                GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                lp.setMargins(dpToPixel(2), dpToPixel(2), dpToPixel(2), dpToPixel(2));
                lp.rowSpec = GridLayout.spec(i);
                lp.columnSpec = GridLayout.spec(j);

                grid.addView(tv, lp);

                cell_tvs.add(tv);
            }
        }

        // Randomly select 4 coordinates to have the mine
        mineCells = new ArrayList<Integer>();
        Random rand = new Random();
        for (int i=0;i<4;i++) {
            int potentialMineIndex = rand.nextInt(120);
            // check that index isn't already a mine
            while(mineCells.contains(potentialMineIndex)){
                potentialMineIndex = rand.nextInt(120);
            }
            mineCells.add(potentialMineIndex);
        }

        // Calculate numbers for each grid square... 0-4 or... -1 for a mine!
//        for (int i=0;i<120;i++){
//            // if it's a mine, give it -1 and continue
//            if (mineCells.contains(i)){
//                revealedCells.add(-1);
//            }
//            // initialize a temp counter variable to 0
//            int countAdjacentMines = 0;
//
//            // translate textview id to xy coordinates
//            Coordinate currCoordinate = nodeIDToCoordinates(i);
//            // for loop for all 8 neighbors
//            for (int j=0;j<8;j++){
//
//            }
//                // for each neighbor, check if it's valid
//                    // if valid and it's a mine, ++ counter
//            // update revealedCells at index i with the counter variable!
//        }

        // Create button on the bottom of the screen that can be clicked
        // to switch between dig mode and flag mode
        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(this::onClickButton);
    }

    private int findIndexOfCellTextView(TextView tv) {
        for (int n=0; n<cell_tvs.size(); n++) {
            if (cell_tvs.get(n) == tv)
                return n;
        }
        return -1;
    }

    public void onClickTV(View view){
        TextView tv = (TextView) view;
        int n = findIndexOfCellTextView(tv);
        int i = n/COLUMN_COUNT;
        int j = n%COLUMN_COUNT;
        tv.setText(String.valueOf(i)+String.valueOf(j));
        if (tv.getCurrentTextColor() == Color.GRAY) {
            tv.setTextColor(Color.GREEN);
            tv.setBackgroundColor(Color.parseColor("lime"));
        }else {
            tv.setTextColor(Color.GRAY);
            tv.setBackgroundColor(Color.LTGRAY);
        }
    }

    public void onClickButton(View view) {
        Button button = (Button) view;
        String mode = button.getText().toString();
        // if mode is flag, set to dig
        if (mode==getString(R.string.flag)){
            button.setText(getString(R.string.mine));
        }
        // else set to flag
        else{
            button.setText(getString(R.string.flag));
        }
    }

}