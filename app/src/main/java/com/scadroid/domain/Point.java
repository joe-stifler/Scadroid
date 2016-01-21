package com.scadroid.domain;

import java.util.Calendar;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;

import com.scadroid.R;

/**
 * Created by joe on 16/05/15.
 */
public class Point implements Parcelable{
    private String id;
    private String name;
    private String value;
    private String type; // type of data --> boolean, int, char
    private String iconString = "" + R.drawable.light_automation; // icon as integer in a right moment, on class PointAdapter, was been just one value, ever.
    private String category; // Here it's setted if the point will be from hierarchy of lights, water, energy, etc.
    private String[] categoryArray = new String[]{"null", "water", "weather", "light", "energy", "luminosity"};
    private boolean writable; // set if the point can be edited or not
    private Calendar date;
    private int update_rate = 1; // it'll determine the update rate of value
    private double price; // used to define the value to pay by the consumption

    public Point(){}

    public Point(int id, String name, String iconString, String category, int write, int update_rate, String type, double price){
        this.id = ""+id;
        this.name = name;
        this.category = category;
        this.writable = true;
        this.update_rate = update_rate;
        this.type = type;
        this.iconString = iconString;
        this.price = price;
    }

    public Point(String id,  String value, String name, String type, String category, boolean writable, Calendar date) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.writable = true;
        this.date = date;
        this.update_rate = 1;
        this.price = 0;
    }

    public String getId() {
        return id;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public String getIconString(){
        return iconString;
    }

    public String getCategoryString(){
        return category;
    }

    public int getCategoryNumber(){
        for(int x = 0; x < categoryArray.length; x++)
            if(this.category.equals(categoryArray[x]))
                return x;
        return 0;
    }

    public boolean getWritable(){
        return writable;
    }

    public Calendar getDate(){
        return date;
    }

    public int getUpdate(){
        return update_rate;
    }

    public double getPrice(){
        return price;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIconString(String iconString){
        this.iconString = iconString;
    }

    public void setCategory(int cat) {
        this.category = categoryArray[cat];
    }

    public void setCategory(String cat) {
        this.category = cat;
    }

    public void setWritable(boolean writable){
        this.writable = writable;
    }

    public void setDate(Calendar date){
        this.date = date;
    }

    public void setUpdate(int update_rate){
        this.update_rate = update_rate;
    }

    public void setPrice(double price){
        this.price = price;
    }

    // PARCELABLE
    public Point(Parcel parcel){
        setId(parcel.readString());
        setIconString(parcel.readString());
        setName(parcel.readString());
        setValue(parcel.readString());
        setType(parcel.readString());
        setCategory(parcel.readString());
        setUpdate(parcel.readInt());
        setPrice(parcel.readDouble());
        setWritable(parcel.readInt() == 0 ? true : false);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getId());
        dest.writeString(getIconString());
        dest.writeString(getName());
        dest.writeString(getValue());
        dest.writeString(getType());
        dest.writeString(getCategoryString());
        dest.writeInt(getUpdate());
        dest.writeDouble(getPrice());
        dest.writeValue(getWritable());
    }

    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>(){
        @Override
        public Point createFromParcel(Parcel source) {
            return new Point(source);
        }
        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
}
