package com.coolweather.android.db;

import org.litepal.crud.DataSupport;

public class Collection extends DataSupport {
    private Integer id;
    private String weatherId;
    private String countyName;

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

    @Override
    public String toString() {
        return "Collection{" +
                "id=" + id +
                ", weatherId='" + weatherId + '\'' +
                ", countyName='" + countyName + '\'' +
                '}';
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }
}
