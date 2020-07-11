package com.example.minzint.a4queue.utilities;

import com.example.minzint.a4queue.models.AccountStat;
import com.example.minzint.a4queue.models.FutureBookingsDetails;
import com.example.minzint.a4queue.models.QueueInfo;
import com.example.minzint.a4queue.models.QueueStatByDayTime;
import com.example.minzint.a4queue.models.ReceiptInfo;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface RetrofitApiInterface {

    @GET("mobile_app/bookings_mobile.php")
    Call<List<FutureBookingsDetails>> getBookings(@Query("retrieve") String user_id);

    @GET("mobile_app/retrieveFutureBookings.php")
    Call<List<FutureBookingsDetails>> getFutureBookings(@Query("retrieve") String user_id);

    @GET("mobile_app/retrieve_receipt_info.php")
    Call<List<ReceiptInfo>> getReceiptInfo(@Query("retrieve") String user_id);

    @POST("mobile_app/delete_booking.php")
    void deleteBooking(@Query("delete") String booking_id);

    @GET("mobile_app/retrieve_queue_records.php")
    Call<List<QueueInfo>> getQueueInfo(@Query("retrieve") String query);

    @GET("mobile_app/retrieveBookingTimes.php")
    Call<List<QueueStatByDayTime>> getStatByTimes(@Query("retrieve") String address);

    @GET("mobile_app/getStatistics_mobile.php")
    Call<List<AccountStat>> getStatistics(@Query("retrieve") String user_id);
}
