package com.kimballleavitt.swipe_soundboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andrognito.patternlockview.PatternLockView;
import com.kimballleavitt.swipe_soundboard.model.SoundMappings;
import com.kimballleavitt.swipe_soundboard.ui.notifications.NotificationsFragment;
import com.kimballleavitt.swipe_soundboard.util.PathStripper;

import java.lang.reflect.Field;

public class AddDefaultSound extends AppCompatActivity {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_default_sound);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setAdapter(new AddDefaultSound.MyAdapter(R.raw.class.getFields()));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    public class MyAdapter extends RecyclerView.Adapter<AddDefaultSound.MyAdapter.MyViewHolder> {
        private final Field[] fields;

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
                        Intent returnIntent = new Intent();
                        returnIntent.putExtra("id", Integer.parseInt(textView.getTag().toString()));
                        setResult(Activity.RESULT_OK, returnIntent);
                        finish();
                    }
                });
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(Field[] fields) {
            this.fields = fields;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public AddDefaultSound.MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            return new AddDefaultSound.MyAdapter.MyViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(AddDefaultSound.MyAdapter.MyViewHolder holder, int position) {
            Field field = fields[position];
            String path = field.getName();
            String strippedPath = PathStripper.strip(path);
            holder.textView.setText(strippedPath);
            try {
                holder.textView.setTag(field.getInt(field));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return fields.length;
        }
    }

    public void listRaw() {
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            String name = field.getName();
            int resId;
            try {
                resId = field.getInt(field);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
