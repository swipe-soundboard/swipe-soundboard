package com.kimballleavitt.swipe_soundboard.ui.notifications;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kimballleavitt.swipe_soundboard.R;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;
import com.kimballleavitt.swipe_soundboard.util.PathStripper;

import java.util.List;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return root;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
        private SoundMappings soundMappings;
        private List<SoundMappings.StoragePattern> keys;
        private List<Uri> values;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class MyViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView textView;

            public MyViewHolder(View v) {
                super(v);
                textView = v.findViewById(R.id.sound_path_name);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SoundMappings.StoragePattern pattern = (SoundMappings.StoragePattern) textView.getTag();
                        itemView.setBackgroundColor(getResources().getColor(R.color.white));
                        textView.setTextColor(getResources().getColor(R.color.black));
                        for (int childCount = recyclerView.getChildCount(), i = 0; i < childCount; ++i) {
                            final MyViewHolder holder = (MyViewHolder) recyclerView.getChildViewHolder(recyclerView.getChildAt(i));
                            if (!holder.itemView.equals(itemView)) {
                                holder.textView.setTextColor(getResources().getColor(R.color.white));
                                holder.itemView.setBackgroundColor(getResources().getColor(R.color.black));
                            }
                        }
                    }
                });
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter() {
            soundMappings = SoundMappings.getInstance();
            keys = soundMappings.keys();
            values = soundMappings.values();
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new MyAdapter.MyViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            String path = values.get(position).getPath();
            assert path != null;
            String strippedPath = PathStripper.strip(path);
            holder.textView.setText(strippedPath);
            holder.textView.setTag(keys.get(position));
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return soundMappings.size();
        }
    }
}