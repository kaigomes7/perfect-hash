package program2;

import java.util.Scanner;

import city.City;
import citytable.CityTable;

public class Program2 {

    public static void printCityAndUrl(City aCity) {
        if (aCity != null) {
            System.out.println("Found " + aCity.name + " (" + aCity.latitude + "," + aCity.longitude + ")");
            System.out.println(String.format("http://www.google.com/maps?z=10&q=%f,%f", aCity.latitude, aCity.longitude));
        }
    }

    public static void main(String[] args) {

        CityTable US_Cities;
        String cName = "";
        City city;

        US_Cities = CityTable.readFromFile("US_Cities_LL.ser");

        Scanner scan = new Scanner(System.in);
        while (!cName.equals("quit")) {
            System.out.print("Enter City, State (or 'quit'): ");
            cName = scan.nextLine();
            if (!cName.equals("quit")) {
                city = US_Cities.find(cName);
                if (city != null) {
                    printCityAndUrl(city);
                } else {
                    System.out.println("Could not find " + "'" + cName + "'");
                }
            }
        }
        scan.close();
    }
}
