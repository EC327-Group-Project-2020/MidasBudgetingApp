package com.example.personalfinanceplanner;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.time.OffsetDateTime;
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


        //timestamp type conversions
        @RequiresApi(api = Build.VERSION_CODES.O)
        @TypeConverter
        public static OffsetDateTime stringToTimestamp(String time_string) {
            return time_string == null ? null : OffsetDateTime.parse(time_string);
        }

        @TypeConverter
        public static String timestampToString(OffsetDateTime timestamp) {
            return timestamp == null ? null : timestamp.toString();
        }
    }
