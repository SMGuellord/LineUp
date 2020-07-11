package com.example.minzint.a4queue.models;

import com.google.gson.annotations.SerializedName;

public class AccountStat {

    @SerializedName("footsteps_pm")
    private int footsteps_pm;
    @SerializedName("time_queuing_pm")
    private String time_queuing_pm;
    @SerializedName("time_saved_pm")
    private String time_saved_pm;
    @SerializedName("bookings_completed")
    private int bookings_completed;
    @SerializedName("services_provided_pm")
    private int services_provided_pm;
    @SerializedName("money_made_pm")
    private double money_made_pm;
    @SerializedName("money_spent_pm")
    private double money_spent_pm;

    @SerializedName("footsteps_all_time")
    private int footsteps_all_time;
    @SerializedName("time_queuing_all_time")
    private String time_queuing_all_time;
    @SerializedName("time_saved_all_time")
    private String time_saved_all_time;
    @SerializedName("bookings_completed_all_time")
    private int bookings_completed_all_time;
    @SerializedName("services_provided_all_time")
    private int services_provided_all_time;
    @SerializedName("money_made_all_time")
    private double money_made_all_time;
    @SerializedName("money_spent_all_time")
    private double money_spent_all_time;

    public AccountStat(int footsteps_pm, String time_queuing_pm, String time_saved_pm, int bookings_completed, int services_provided_pm, double money_made_pm, double money_spent_pm, int footsteps_all_time, String time_queuing_all_time, String time_saved_all_time, int bookings_completed_all_time, int services_provided_all_time, double money_made_all_time, double money_spent_all_time) {
        this.footsteps_pm = footsteps_pm;
        this.time_queuing_pm = time_queuing_pm;
        this.time_saved_pm = time_saved_pm;
        this.bookings_completed = bookings_completed;
        this.services_provided_pm = services_provided_pm;
        this.money_made_pm = money_made_pm;
        this.money_spent_pm = money_spent_pm;
        this.footsteps_all_time = footsteps_all_time;
        this.time_queuing_all_time = time_queuing_all_time;
        this.time_saved_all_time = time_saved_all_time;
        this.bookings_completed_all_time = bookings_completed_all_time;
        this.services_provided_all_time = services_provided_all_time;
        this.money_made_all_time = money_made_all_time;
        this.money_spent_all_time = money_spent_all_time;
    }

    public int getFootsteps_pm() {
        return footsteps_pm;
    }

    public void setFootsteps_pm(int footsteps_pm) {
        this.footsteps_pm = footsteps_pm;
    }

    public String getTime_queuing_pm() {
        return time_queuing_pm;
    }

    public void setTime_queuing_pm(String time_queuing_pm) {
        this.time_queuing_pm = time_queuing_pm;
    }

    public String getTime_saved_pm() {
        return time_saved_pm;
    }

    public void setTime_saved_pm(String time_saved_pm) {
        this.time_saved_pm = time_saved_pm;
    }

    public int getBookings_completed() {
        return bookings_completed;
    }

    public void setBookings_completed(int bookings_completed) {
        this.bookings_completed = bookings_completed;
    }

    public int getServices_provided_pm() {
        return services_provided_pm;
    }

    public void setServices_provided_pm(int services_provided_pm) {
        this.services_provided_pm = services_provided_pm;
    }

    public double getMoney_made_pm() {
        return money_made_pm;
    }

    public void setMoney_made_pm(double money_made_pm) {
        this.money_made_pm = money_made_pm;
    }

    public double getMoney_spent_pm() {
        return money_spent_pm;
    }

    public void setMoney_spent_pm(double money_spent_pm) {
        this.money_spent_pm = money_spent_pm;
    }

    public int getFootsteps_all_time() {
        return footsteps_all_time;
    }

    public void setFootsteps_all_time(int footsteps_all_time) {
        this.footsteps_all_time = footsteps_all_time;
    }

    public String getTime_queuing_all_time() {
        return time_queuing_all_time;
    }

    public void setTime_queuing_all_time(String time_queuing_all_time) {
        this.time_queuing_all_time = time_queuing_all_time;
    }

    public String getTime_saved_all_time() {
        return time_saved_all_time;
    }

    public void setTime_saved_all_time(String time_saved_all_time) {
        this.time_saved_all_time = time_saved_all_time;
    }

    public int getBookings_completed_all_time() {
        return bookings_completed_all_time;
    }

    public void setBookings_completed_all_time(int bookings_completed_all_time) {
        this.bookings_completed_all_time = bookings_completed_all_time;
    }

    public int getServices_provided_all_time() {
        return services_provided_all_time;
    }

    public void setServices_provided_all_time(int services_provided_all_time) {
        this.services_provided_all_time = services_provided_all_time;
    }

    public double getMoney_made_all_time() {
        return money_made_all_time;
    }

    public void setMoney_made_all_time(double money_made_all_time) {
        this.money_made_all_time = money_made_all_time;
    }

    public double getMoney_spent_all_time() {
        return money_spent_all_time;
    }

    public void setMoney_spent_all_time(double money_spent_all_time) {
        this.money_spent_all_time = money_spent_all_time;
    }

    @Override
    public String toString() {
        return "AccountStat{" +
                "footsteps_pm=" + footsteps_pm +
                ", time_queuing_pm='" + time_queuing_pm + '\'' +
                ", time_saved_pm='" + time_saved_pm + '\'' +
                ", bookings_completed=" + bookings_completed +
                ", services_provided_pm=" + services_provided_pm +
                ", money_made_pm=" + money_made_pm +
                ", money_spent_pm=" + money_spent_pm +
                ", footsteps_all_time=" + footsteps_all_time +
                ", time_queuing_all_time='" + time_queuing_all_time + '\'' +
                ", time_saved_all_time='" + time_saved_all_time + '\'' +
                ", bookings_completed_all_time=" + bookings_completed_all_time +
                ", services_provided_all_time=" + services_provided_all_time +
                ", money_made_all_time=" + money_made_all_time +
                ", money_spent_all_time=" + money_spent_all_time +
                '}';
    }
}
