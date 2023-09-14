package com.example.gridlayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;
import com.example.gridlayout.Coordinate;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String globalMode = "dig";

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
        return new Coordinate(quotient, remainder);
    }

    private int coordinatesToNodeID(int row, int col){
        return row*COLUMN_COUNT+col;
    }

    private void recursivelyRevealAdjacentMines(TextView tv){
        Queue<Integer> q = new LinkedList<Integer>();
        ArrayList<Integer> alreadyChecked = new ArrayList<>();

        q.add(findIndexOfCellTextView(tv));
        alreadyChecked.add(findIndexOfCellTextView(tv));

        while(!q.isEmpty()){
            Integer currCellIndex = q.remove();
            System.out.println("Looking at "+currCellIndex);
            // reveal this cell's adjacent mine number
            TextView currTV = cell_tvs.get(currCellIndex);
            currTV.setText(Integer.toString(revealedCells.get(currCellIndex)));
            currTV.setTextColor(Color.WHITE);
            currTV.setBackgroundColor(Color.BLACK);
            Coordinate currCoordinate = nodeIDToCoordinates(currCellIndex);
            Integer adjacentMineNumber = revealedCells.get(currCellIndex);
            if (adjacentMineNumber!=0){
                continue;
            }
            // check all neighbors...
            // for loop for all 8 neighbors + itself (but don't actually check itself
            for (int rowOffset=-1;rowOffset<2;rowOffset++){
                for (int colOffset=-1;colOffset<2;colOffset++) {
                    int currNeighborRow = currCoordinate.row+rowOffset;
                    int currNeighborCol = currCoordinate.col+colOffset;
                    int currNeighborNodeID = coordinatesToNodeID(currNeighborRow,currNeighborCol);
                    System.out.println("currCoor: "+currCoordinate.row + ","+currCoordinate.col);
                    System.out.println("currNeighbor: "+currNeighborRow+ ","+currNeighborCol);
                    System.out.println("currNeighborNodeID: "+currNeighborNodeID);
                    // if itself skip,
                    if (rowOffset==0 && colOffset==0){
                        continue;
                    }
                    // if valid neighbor node has value of 0, add this neighbor node to q
                    else if (currNeighborRow>=0&&currNeighborRow<12&&currNeighborCol>=0&&currNeighborCol<10) {
                        if (!alreadyChecked.contains(currNeighborNodeID)) {
                            q.add(currNeighborNodeID);
                        }
                    }
                    alreadyChecked.add(currNeighborNodeID);
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cell_tvs = new ArrayList<TextView>();
        mineCells = new ArrayList<Integer>();
        revealedCells = new ArrayList<Integer>();
        Random rand = new Random();

        // Method (2): add 12*10 dynamically created cells
        GridLayout grid = (GridLayout) findViewById(R.id.gridLayout01);
        for (int i = 0; i<=11; i++) {
            for (int j=0; j<=9; j++) {
                TextView tv = new TextView(this);
                tv.setHeight( dpToPixel(30) );
                tv.setWidth( dpToPixel(30) );
                tv.setTextSize(15);//dpToPixel(32) );
                tv.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                tv.setTextColor(Color.BLUE);
                tv.setBackgroundColor(Color.BLUE);
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
        for (int i=0;i<4;i++) {
            int potentialMineIndex = rand.nextInt(120);
            // check that index isn't already a mine
            while(mineCells.contains(potentialMineIndex)){
                potentialMineIndex = rand.nextInt(120);
            }
            mineCells.add(potentialMineIndex);
        }

        // Calculate numbers for each grid square... 0-4 or... -1 for a mine!
        for (int i=0;i<120;i++){
            // if it's a mine, give it -1 and continue
            if (mineCells.contains(i)){
                revealedCells.add(Integer.valueOf(-1));
                continue;
            }
            // initialize a temp counter variable to 0
            int countAdjacentMines = 0;

            // translate textview id to xy coordinates
            Coordinate currCoordinate = nodeIDToCoordinates(i);
            // for loop for all 8 neighbors + itself (but don't actually check itself
            for (int rowOffset=-1;rowOffset<2;rowOffset++){
                for (int colOffset=-1;colOffset<2;colOffset++) {
                    int currNeighborRow = currCoordinate.row+rowOffset;
                    int currNeighborCol = currCoordinate.col+colOffset;
                    // if itself skip,
                    if (rowOffset==0 && colOffset==0){
                        continue;
                    }
                    // if valid and it's a mine, ++ counter
                    else if (currNeighborRow>=0&&currNeighborRow<12&&currNeighborCol>=0&&currNeighborCol<10) {
                        int currNeighborNodeID = coordinatesToNodeID(currNeighborRow,currNeighborCol);
                        if (mineCells.contains(currNeighborNodeID)){
                            countAdjacentMines+=1;
                        }
                    }
                    // if not valid... coordinates then do nothing
                }
            }
            // update revealedCells at index i with the counter variable!
            revealedCells.add(countAdjacentMines);
        }

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

    public void onClickTV(View view) {
        TextView tv = (TextView) view;
        int currTVTextColor = tv.getCurrentTextColor();

        int n = findIndexOfCellTextView(tv);
        // if in dig mode
        if (globalMode == "dig") {
            // if blue cell and unrevealed (aka no flag),
            if (currTVTextColor== Color.BLUE && tv.getText()=="") {
                Integer adjacentMines = revealedCells.get(n);
                // if mine, end game
                if (adjacentMines==-1){
                    tv.setText(getString(R.string.mine));
                    tv.setBackgroundColor(Color.WHITE);
                }
                // else if number 1-4 inclusive, show number
                else if (adjacentMines<=4 && adjacentMines>0){
                    tv.setText(Integer.toString(adjacentMines));
                    tv.setTextColor(Color.WHITE);
                    tv.setBackgroundColor(Color.BLACK);
                }
                // else if number is 0, expand
                else if (adjacentMines==0){
                    recursivelyRevealAdjacentMines(tv);
//                    tv.setText(Integer.toString(adjacentMines));
//                    tv.setTextColor(Color.WHITE);
//                    tv.setBackgroundColor(Color.BLACK);
                }
                // error
                else {
                    System.out.println("ERROR: clicked on an unrevealed cell in dig mode with invalid text or invalid adjacentMines calculations");
                }
            }
            // if flagged, do nothing
            else if (currTVTextColor == Color.BLUE && tv.getText()==getString(R.string.flag)){
                return;
            }
            // if number (aka already revealed), do nothing
            else if (currTVTextColor== Color.WHITE) {
                return;
            }
        }
        // else in flag mode
        else if (globalMode == "flag") {
            TextView flagsLeftCounterTextView = findViewById(R.id.flagsLeftCounter);
            Integer currentFlagsLeftCounter = Integer.parseInt((String) flagsLeftCounterTextView.getText());
            // if blue cell and text is flag icon, then unrevealed but flagged cell
            if (currTVTextColor==Color.BLUE && tv.getText()==getString(R.string.flag)){
                // delete flag icon and inc flags left counter
                tv.setText("");
                flagsLeftCounterTextView.setText(Integer.toString(currentFlagsLeftCounter+1));
            }
            // else if blue cell and no flag icon,
            else if (currTVTextColor==Color.BLUE ){
                // show flag icon and dec flags left counter
                tv.setText(getString(R.string.flag));
                flagsLeftCounterTextView.setText(Integer.toString(currentFlagsLeftCounter-1));
            }
            // else if number, already revealed, do nothing
            else if (currTVTextColor==Color.WHITE){
                return;
            }
        } else {
            System.out.println("ERROR: Global Mode is neither dig nor flag.");
        }
    }

    public void onClickButton(View view) {
        Button button = (Button) view;
        String mode = button.getText().toString();
        // if mode is flag, set to dig
        if (mode==getString(R.string.flag)){
            button.setText(getString(R.string.mine));
            globalMode = "dig";
        }
        // else set to flag
        else{
            button.setText(getString(R.string.flag));
            globalMode = "flag";
        }
    }

}