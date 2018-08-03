//
// A board button that tells us what is in that location
//

import javax.swing.*;


public class GridButton extends JButton{

    private boolean has_Ship;   //ship is at this coordinate
    private boolean is_Hit;     //this section has been hit
    public int row;
    public int col;



    public GridButton(int i,int j) {
        has_Ship=false;
        is_Hit=false;
        row = i;
        col = j;
    }

    //
    // Setters/getters
    //

    public boolean getHit() {
        return is_Hit;
    }

    public boolean getShip() {
        return has_Ship;
    }

    public void setToHasShip() {
        has_Ship = true;
    }

    public void setToBeenHit() {
        is_Hit = true;
    }

    public void printRowCol() {
        System.out.println("Row = " + row + " Col = " + col);
    }

    public void setButtonText() {
        this.setText("X");
    }




}