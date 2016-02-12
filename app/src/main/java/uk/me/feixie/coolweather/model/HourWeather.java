package uk.me.feixie.coolweather.model;

import java.io.Serializable;

/**
 * Created by Fei on 11/02/2016.
 */
public class HourWeather implements Serializable {

    private int id;
    private String temperature;
    private String description;
    private String icon;
    private String time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "HourWeather{" +
                "id=" + id +
                ", temperature='" + temperature + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
