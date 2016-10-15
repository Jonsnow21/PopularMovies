package com.example.android.popularmovies.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.activity.DetailActivity;
import com.example.android.popularmovies.model.Movie;
import com.example.android.popularmovies.model.Review;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private ArrayList<Review> reviewArrayList;
    private Context context;
    private int layout;
    private boolean isExpanded = false;

    public ReviewAdapter(Context mContext, int layout, ArrayList<Review> reviewArrayList ){
        this.reviewArrayList = reviewArrayList;
        this.context = mContext;
        this.layout = layout;

    }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView reviewer, review;
        View view;

        public ReviewViewHolder(View v) {
            super(v);
            view = v;
            reviewer = (TextView) v.findViewById(R.id.review_by);
            review = (TextView) v.findViewById(R.id.review_text);
        }
    }

    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new ReviewAdapter.ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.ReviewViewHolder holder, final int position) {

        holder.reviewer.setText(reviewArrayList.get(position).getAuthor());
        holder.review.setText(reviewArrayList.get(position).getContent());
        holder.view.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isExpanded) {
                    holder.review.setMaxLines(Integer.MAX_VALUE);
                    holder.review.setEllipsize(null);
                    isExpanded = true;
                } else {
                    holder.review.setEllipsize(TextUtils.TruncateAt.END);
                    holder.review.setMaxLines(3);
                    isExpanded = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }
}