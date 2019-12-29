package com.kimballleavitt.swipe_soundboard.ui.notifications;

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

import com.andrognito.patternlockview.PatternLockView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kimballleavitt.swipe_soundboard.R;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;
import com.kimballleavitt.swipe_soundboard.util.PathStripper;

import java.io.File;

public class NotificationsFragment extends Fragment {

    private NotificationsViewModel notificationsViewModel;
    private RecyclerView recyclerView;
    private BottomNavigationView navbar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        notificationsViewModel = ViewModelProviders.of(this).get(NotificationsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new MyAdapter());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        PatternLockView patternLockView = root.findViewById(R.id.pattern_lock_view);
        patternLockView.setInputEnabled(false);
        navbar = getActivity().findViewById(R.id.nav_view);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && navbar.isShown()) {
                    navbar.setVisibility(View.GONE);
                } else if (dy < 0) {
                    navbar.setVisibility(View.VISIBLE);

                }
            }
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        return root;
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
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
                        PatternLockView patternLockView = getView().findViewById(R.id.pattern_lock_view);
                        patternLockView.setPattern(PatternLockView.PatternViewMode.AUTO_DRAW, pattern.getOriginalPattern());
                    }
                });
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter() {

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
            String path = SoundMappings.getInstance().values().get(position).getPath();
            assert path != null;
            // Get the file we are going after
            File file = new File(path);
            // Get the name of the file
            String strippedPath = file.getName();
            strippedPath = strippedPath.substring(strippedPath.lastIndexOf("/") + 1);
            // Check to see if the "filename" we have gotten is actually a resource ID by
            // attempting to parse it as an Integer. If an exception is thrown we will not
            // convert it using GetResources, if it is resource ID we will get it using the
            // Android API for getting a resource file name
            boolean isInt = true;
            int resId = 0;
            try {
                resId = Integer.parseInt(strippedPath);
            }
            catch (Exception e)
            {
                isInt = false;
            }
            if (isInt){
                strippedPath = getResources().getResourceEntryName(resId);
            }
            // Set the list item's textView to the string that we have determined is the name of
            // the file we will be playing
            holder.textView.setText(strippedPath);
            SoundMappings.StoragePattern pattern = SoundMappings.getInstance().keys().get(position);
            // Then map the patten that corresponds to that file to the list item
            holder.textView.setTag(pattern);
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return SoundMappings.getInstance().size();
        }
    }
}