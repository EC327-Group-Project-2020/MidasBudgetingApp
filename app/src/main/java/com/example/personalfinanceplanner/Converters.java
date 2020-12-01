package com.example.personalfinanceplanner;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class Converters {

        //string to ArrayList converter, for converting a string of stored currencies in Room back to an ArrayList
        @TypeConverter
        public ArrayList<String> stringToArrayList(String roomData) {
            if (roomData == null) {
                return new ArrayList<String>(); //creates empty list instance
            }

            Type listType = new TypeToken<ArrayList<String>>(){}.getType();

            return new Gson().fromJson(roomData, listType);
        }

        //Object List to string converter, for converting List of Objects into string that can be stored in Room
        @TypeConverter
        public String arrayListToString(ArrayList<String> currencyList) {
            return new Gson().toJson(currencyList);
        }


        //NEED TO EDIT FOR STORING TIMESTAMP DATA IN ROOM
        @TypeConverter
        public static Date fromTimestamp(Long value) {
            return value == null ? null : new Date(value);
        }

        @TypeConverter
        public static Long dateToTimestamp(Date date) {
            return date == null ? null : date.getTime();
        }
    }
