package com.coolweather.android.list;

public class City {
    private Integer id;
    private String weatherId;
    public String cityName;

    @Override
    public String toString() {
        return "City{" +
                "id=" + id +
                ", weatherId='" + weatherId + '\'' +
                ", cityName='" + cityName + '\'' +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
