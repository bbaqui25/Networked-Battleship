
//
// ERROR ACCESSING 0th INDEX IN LOCATION ARRAY
//

public class Ship {


    private int size;
    private boolean hasSunk;
    private boolean beenPlaced;
    private String name;
    private int spacesLeft;
    private String[] shipImages;
    public ShipIndices[] shipindices;
    //private String[] location = new String[size];
    

    //
    // Constructor
    //
    public Ship(String NAME, int SIZE) {

        //for (int i = 0 ; i < size ; ++i) {
        //    location[i] = "";
        //}

        name = NAME;
        size = SIZE;
        hasSunk = false;
        beenPlaced = false;
        shipindices=new ShipIndices[size];
    }

    //
    // Setters/Getters
    //

    public void setSize(int newSize) {

        if (newSize > 1 && size < 6) {
            size = newSize;
        } else {
            System.out.println("Invalid ship size");
        }
    }

    public void setToHasSunk() {
        hasSunk = true;
    }

    /*public void setLocation(String[] newLocation) {

        if (location.length == newLocation.length) {
            for (int i = 0 ; i < size ; ++i) {
                location[i] = newLocation[i];
            }
        }
    }*/

    public String getName() {
        return name;
    }

    public int getSize() {
        return size;
    }

    public boolean isSunk() {
        return hasSunk;
    }

    public boolean isPlace() {
        return beenPlaced;
    }

    /*public String[] getLocation() {
        return location;
    }*/

    public void setToBeenPlaced() {
        beenPlaced = true;
    }
}
