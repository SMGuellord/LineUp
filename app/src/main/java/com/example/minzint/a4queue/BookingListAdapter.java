package com.example.minzint.a4queue;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.minzint.a4queue.models.FutureBookingsDetails;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.BookingViewHolder>{

    // ===================================================================
    // VARIABLES

    private List<FutureBookingsDetails> future_bookings;
    private Context context;

    // ===================================================================
    // END OF VARIABLES

    public BookingListAdapter(List<FutureBookingsDetails> future_bookings, Context context){
        this.future_bookings = future_bookings;
        this.context = context;
    }


    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.future_booking_list, parent, false );
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, final int position) {

     /*   String profile = future_bookings.get(position).getProfile();
//        Set profile image
        if (profile != "") {

            Picasso.with(context)
                    .load(ServerDetails.PROFILE_DIR_URL + "" + profile)
                    .resize(80, 80).into(holder.imageViewProfile);
        }*//*else{
            holder.imageViewProfile.setImageIcon(R.drawable.profile_image);
        }*/
        holder.textViewTimes.setText("Time: "+future_bookings.get(position).getTimes());
        holder.textViewDate.setText("Date: "+future_bookings.get(position).getDate());
        holder.textViewAddress.setText("Address: "+future_bookings.get(position).getAddress());

        holder.view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Future_Booking_desc.class);
                intent.putExtra("FutureBookingsDetails", future_bookings.get(position));
                context.startActivities(new Intent[]{intent});
            }
        });
    }

    @Override
    public int getItemCount() {
        return future_bookings.size();
    }

    public class BookingViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewProfile;
        TextView textViewAddress;
        TextView textViewTimes;
        TextView textViewDate;
        View view;

        public BookingViewHolder(View itemView) {
            super(itemView);
            view = itemView;
//            imageViewProfile = itemView.findViewById(R.id.imageView16);
            textViewAddress = itemView.findViewById(R.id.textViewAddress);
            textViewTimes = itemView.findViewById(R.id.textViewTime);
            textViewDate = itemView.findViewById(R.id.textViewDate);
        }
    }
}
