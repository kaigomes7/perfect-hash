package hash341;

import java.io.Serializable;

public class City implements Serializable {
    public String name;
    public float latitude;
    public float longitude;

    public City(String cityState, String[] latLong) {
        name = cityState;
        latitude = Float.parseFloat(latLong[0]);
        longitude = Float.parseFloat(latLong[1]);
    }
}
