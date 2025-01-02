package com.example.ca3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ca3.R;
import com.example.ca3.model.Memory;

import java.util.ArrayList;
import java.util.List;

public class MemoryAdapter extends RecyclerView.Adapter<MemoryAdapter.MemoryViewHolder> implements android.widget.Filterable {

    private List<Memory> memoryList = new ArrayList<>();
    private List<Memory> memoryListFull = new ArrayList<>();
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    // Click Listener Interface
    public interface OnItemClickListener {
        void onItemClick(Memory memory);
    }

    // Long Click Listener Interface
    public interface OnItemLongClickListener {
        void onItemLongClick(Memory memory, int position);
    }

    // Setter for Click Listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    // Setter for Long Click Listener
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public MemoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memory, parent, false);
        return new MemoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MemoryViewHolder holder, int position) {
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

    // Update the adapter's data
    public void submitList(List<Memory> memories) {
        memoryList = new ArrayList<>(memories);
        memoryListFull = new ArrayList<>(memories);
        notifyDataSetChanged();
    }

    // Filter implementation
    @Override
    public Filter getFilter() {
        return memoryFilter;
    }

    private Filter memoryFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Memory> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(memoryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Memory memory : memoryListFull) {
                    if (memory.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(memory);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            memoryList.clear();
            memoryList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    class MemoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textTitle;

        MemoryViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewMemory);
            textTitle = itemView.findViewById(R.id.textViewTitle);

            // Handle Click Events
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (clickListener != null && position != RecyclerView.NO_POSITION) {
                    clickListener.onItemClick(memoryList.get(position));
                }
            });

            // Handle Long Click Events
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener.onItemLongClick(memoryList.get(position), position);
                    return true;
                }
                return false;
            });
        }
    }
}
