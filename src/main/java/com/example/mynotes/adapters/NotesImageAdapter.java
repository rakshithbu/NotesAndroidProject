package com.example.mynotes.adapters;

import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mynotes.NotesEdit;
import com.example.mynotes.R;
import com.example.mynotes.model.ImageUrl;

import java.util.ArrayList;

public class NotesImageAdapter extends RecyclerView.Adapter<NotesImageAdapter.ViewHolder> {
    private ArrayList<ImageUrl> imageUrls;
    private NotesEdit context;

    public NotesImageAdapter(NotesEdit context, ArrayList<ImageUrl> imageUrls) {
        this.context = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public NotesImageAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_layout_image_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        Glide.with(context).load(imageUrls.get(i).getImageUrl()).into(viewHolder.img);
        viewHolder.img.setTag(imageUrls.get(i).getImageUrl());
        viewHolder.removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.removeImageFromList(viewHolder.img.getTag().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        ImageButton removeBtn;
        public ViewHolder(View view) {
            super(view);
            img = view.findViewById(R.id.imageView);
            removeBtn = view.findViewById(R.id.deleteImage);
        }
    }
}