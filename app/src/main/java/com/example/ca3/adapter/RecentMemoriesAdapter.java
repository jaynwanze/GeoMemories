package com.example.ca3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ca3.R;
import com.example.ca3.model.Memory;
import java.util.ArrayList;
import java.util.List;

public class RecentMemoriesAdapter extends RecyclerView.Adapter<RecentMemoriesAdapter.RecentMemoryViewHolder> {

    private List<Memory> memoryList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Memory memory);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecentMemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_memory, parent, false);
        return new RecentMemoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentMemoryViewHolder holder, int position) {
        Memory memory = memoryList.get(position);
        holder.textTitle.setText(memory.getTitle());
        Glide.with(holder.imageView.getContext())
                .load(memory.getPhotoUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return memoryList.size();
    }

    public void submitList(List<Memory> memories) {
        memoryList = memories;
        notifyDataSetChanged();
    }

    class RecentMemoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textTitle;

        RecentMemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMemoryRecent);
            textTitle = itemView.findViewById(R.id.textViewMemoryTitleRecent);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(memoryList.get(position));
                }
            });
        }
    }
}
