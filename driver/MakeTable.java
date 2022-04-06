package driver;

import hash341.CityTable;

public class MakeTable {

    public static void main(String[] args) {
        CityTable US_Cities = new CityTable("US_Cities_LL.txt", 16000);
        US_Cities.writeToFile("US_Cities_LL.ser");
    }

}
