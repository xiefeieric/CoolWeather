package uk.me.feixie.coolweather.util;

/**
 * Created by Fei on 01/02/2016.
 */
public class GlobalConstant {

    public static final String OPEN_API_KEY = "&appid=cc0d847424665d15e9c9668c87ba90e5";

    public static final String WEATHER_SERVER = "http://api.openweathermap.org/data/2.5/weather?q=";
    public static final String ONE_DAY_WEATHER_SERVER = "http://api.openweathermap.org/data/2.5/forecast?q=";
    public static final String WEATHER_FORECAST_SERVER = "http://api.openweathermap.org/data/2.5/forecast/daily?q=";
//    api.openweathermap.org/data/2.5/forecast/daily?q=London&mode=xml&units=metric&cnt=7
//    http://openweathermap.org/img/w/10d.png
    public static final String WEATHER_ICON_PATH = "http://openweathermap.org/img/w/";

    public static final String UNIT_CELSIUS = "&units=metric";

    public static final String UNIT_FAHRENHEIT = "&units=imperial";

    public static final String LOCATION_STATUS_CURRENT = "gps";
    public static final String LOCATION_STATUS_INPUT = "input";

    public static final String APPS_URL = "http://www.feixie.me.uk/apps.json";

}
