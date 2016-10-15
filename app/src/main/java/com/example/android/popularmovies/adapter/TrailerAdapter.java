package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.model.Trailer;

import java.util.ArrayList;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private ArrayList<Trailer> trailerList;
    private Context context;
    private int layout;

    public TrailerAdapter(Context mContext, int layout, ArrayList<Trailer> trailerList ){
        this.trailerList = trailerList;
        this.context = mContext;
        this.layout = layout;
    }

    public static class TrailerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        View view;

        public TrailerViewHolder(View v) {
            super(v);
            view = v;
            imageView = (ImageView) v.findViewById(R.id.trailer_icon);
            textView = (TextView) v.findViewById(R.id.trailer_text);
        }
    }

    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new TrailerAdapter.TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, final int position) {

        holder.imageView.setImageResource(R.drawable.ic_ondemand_video_black_24dp);
        int x = position + 1;
        holder.textView.setText("Trailer " + x);
        holder.view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String videoKey = trailerList.get(position).getKey();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + videoKey));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }
}