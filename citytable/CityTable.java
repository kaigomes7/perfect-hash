package citytable;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import city.City;
import hash341.Hash24;

import java.io.Serializable;

public class CityTable implements Serializable {

    City cityHashTable[][];
    Hash24 h1;
    Hash24[] h2List;

    public CityTable(String fname, int tsize) {
        h1 = new Hash24();
        ArrayList<City>[] primaryTable = constructPrimaryTable(fname, tsize);
        constructSecondaryTable(primaryTable);
    }

    private static double getAverageHashTried(int[] collisionCountArr) {
        double total = 0;
        int len = 0;
        for (int collCount : collisionCountArr) {
            if (collCount != -1) {
                total += (collCount + 1);
                len += 1;
            }
        }
        return (double) total / len;
    }

    private void constructSecondaryTable(ArrayList<City>[] primaryTable) {
        int primaryTableSize = primaryTable.length;
        cityHashTable = new City[primaryTableSize][];
        int primaryTableElementLength;
        int secondaryTableLength;
        int[] collisionCountArr = new int[primaryTableSize];
        int moreThanOne = 0;
        h2List = new Hash24[primaryTableSize];
        Arrays.fill(collisionCountArr, -1);
        for (int i = 0; i < primaryTableSize; i++) {
            if (primaryTable[i] != null) {
                primaryTableElementLength = primaryTable[i].size();
                secondaryTableLength = primaryTableElementLength * primaryTableElementLength;
                if (secondaryTableLength > 1) {
                    moreThanOne += 1;
                    cityHashTable[i] = new City[secondaryTableLength];
                    int hashVal;
                    int j = 0;
                    City city;
                    h2List[i] = new Hash24();
                    collisionCountArr[i] = 0;
                    while (j < primaryTableElementLength) {
                        city = primaryTable[i].get(j);
                        hashVal = h2List[i].hash(city.name) % secondaryTableLength;
                        if (cityHashTable[i][hashVal] != null) { // if collision
                            collisionCountArr[i] += 1;
                            j = 0;
                            Arrays.fill(cityHashTable[i], null);
                            h2List[i] = new Hash24();
                        } else {
                            cityHashTable[i][hashVal] = city;
                            j++;
                        }
                    }
                } else {
                    cityHashTable[i] = new City[1];
                    cityHashTable[i][0] = primaryTable[i].get(0);
                }
            }
        }
        secondaryStats(collisionCountArr, moreThanOne);
    }

    private ArrayList<City>[] constructPrimaryTable(String fname, int tsize) {
        int[] maxCollisionsPair = new int[] { -1, -1 };
        int numCities = 0;
        ArrayList<City>[] hm = (ArrayList<City>[]) new ArrayList[tsize];
        try (BufferedReader reader = new BufferedReader(new FileReader(fname))) {
            String cityState = reader.readLine();
            String latLong;
            int hashVal;
            int indexSize;
            while (cityState != null) {
                latLong = reader.readLine();
                hashVal = h1.hash(cityState) % tsize;
                if (hm[hashVal] == null) {
                    hm[hashVal] = new ArrayList<City>();
                }
                // Add to hashtable
                hm[hashVal].add(new City(cityState, latLong.split(" ", 2)));
                indexSize = hm[hashVal].size();
                if (indexSize > maxCollisionsPair[1]) {
                    maxCollisionsPair[0] = hashVal;
                    maxCollisionsPair[1] = indexSize;
                }
                cityState = reader.readLine();
                numCities += 1;
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Unable to be read " + fname);
            e.printStackTrace();
        }
        primaryStats(hm, numCities, tsize, maxCollisionsPair);
        return hm;
    }

    private void primaryStats(ArrayList<City>[] t1, int numCities, int tsize, int[] maxCollisions) {
        System.out.println("Primary hash table function:");
        h1.dump();
        System.out.println("\nPrimary has table statistics");
        System.out.println("\tNumber of cities: " + String.valueOf(numCities));
        System.out.println("\tTable size: " + String.valueOf(tsize));
        System.out.println("\tMax collisions = " + String.valueOf(maxCollisions[1]));
        int[] slotsArr = new int[25];
        Arrays.fill(slotsArr, 0);
        for (ArrayList<City> key : t1) {
            if (key == null) {
                slotsArr[0] += 1;
            } else if (key.size() < 25) {
                slotsArr[key.size()] += 1;
            }
        }
        for (int i = 0; i < 25; i++) {
            System.out.println("\t\t# of primary slots with " + i + " cities = " + String.valueOf(slotsArr[i]));
        }
        System.out.println("\n*** Cities in the slot with most collisions ***");
        for (City city : t1[maxCollisions[0]]) {
            System.out.println("\t" + city.name + " (" + String.valueOf(city.latitude) + ","
                    + String.valueOf(city.longitude) + ")");
        }
    }

    private void secondaryStats(int[] collisionCountArr, int moreThanOne) {
        System.out.println("\nSecondary hash table statistics:");
        int[] collisionCountTracker = new int[20];
        Arrays.fill(collisionCountTracker, 0);
        for (int collisionCount : collisionCountArr) {
            if (collisionCount != -1 && collisionCount < 21) {
                collisionCountTracker[collisionCount] += 1;
            }
        }
        for (int i = 0; i < 20; i++) {
            System.out.println("\t\t# of secondary hash tables trying " + String.valueOf(i + 1) + " hash functions = "
                    + String.valueOf(collisionCountTracker[i]));
        }
        System.out.println("\nNumber of secondary hash tables with more than 1 item = " + String.valueOf(moreThanOne));
        System.out.println(
                "Average # of hash functions tried = " + String.valueOf(getAverageHashTried(collisionCountArr)));
    }

    public City find(String cName) {
        int primaryTableKey = h1.hash(cName) % cityHashTable.length;
        if (cityHashTable[primaryTableKey] == null) {
            return null;
        } else if (cityHashTable[primaryTableKey].length == 1) {
            return (cityHashTable[primaryTableKey][0] != null && cName.equals(cityHashTable[primaryTableKey][0].name))
                    ? cityHashTable[primaryTableKey][0]
                    : null;
        } else {
            int secondaryTableKey = h2List[primaryTableKey].hash(cName) % cityHashTable[primaryTableKey].length;
            return (cityHashTable[primaryTableKey][secondaryTableKey] != null
                    && cName.equals(cityHashTable[primaryTableKey][secondaryTableKey].name))
                            ? cityHashTable[primaryTableKey][secondaryTableKey]
                            : null;
        }
    }

    public void writeToFile(String fname) {
        try (FileOutputStream out = new FileOutputStream(fname)) {
            ObjectOutputStream oout = new ObjectOutputStream(out);
            oout.writeObject(this);
            out.close();
        } catch (IOException e) {
            System.out.println("Unable to write to " + fname);
            e.printStackTrace();
        }
    }

    public static CityTable readFromFile(String fname) {
        try (ObjectInputStream oins = new ObjectInputStream(new FileInputStream(fname))) {
            CityTable ct = (CityTable) oins.readObject();
            oins.close();
            return ct;
        } catch (IOException e) {
            System.out.println("Unable to read object " + fname);
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("Unable to read object " + fname);
            e.printStackTrace();
        }
        return null;
    }
}
