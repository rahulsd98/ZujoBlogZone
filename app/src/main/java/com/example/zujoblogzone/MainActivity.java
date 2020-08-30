package com.example.zujoblogzone;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize the recycler view and firebase objects
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("ZujoBlogZone");
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mAuth.getCurrentUser() == null) {
                    Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

        FirebaseRecyclerAdapter<ZujoBlogZone, ZujoBlogZoneViewHolder> FBRA = new FirebaseRecyclerAdapter<ZujoBlogZone, ZujoBlogZoneViewHolder>(
                ZujoBlogZone.class,
                R.layout.card_items,
                ZujoBlogZoneViewHolder.class,
                mDatabase
        ) {

            @Override
            protected void populateViewHolder(ZujoBlogZoneViewHolder viewHolder, ZujoBlogZone model, int i) {
                final String post_key = getRef(i).getKey().toString();
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImageUrl(getApplicationContext(), model.getImageUrl());
                viewHolder.setUserName(model.getUsername());

                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent singleActivity = new Intent(MainActivity.this, SinglePostActivity.class);
                        singleActivity.putExtra("PostID", post_key);
                        startActivity(singleActivity);
                    }
                });
            }
        };

        recyclerView.setAdapter(FBRA);
    }

    public static class ZujoBlogZoneViewHolder extends RecyclerView.ViewHolder{
        View mView;

        public ZujoBlogZoneViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setTitle(String Title){
            TextView post_title = mView.findViewById(R.id.post_title);
            post_title.setText(Title);
        }

        public void setDesc(String Desc){
            TextView post_desc = mView.findViewById(R.id.post_desc);
            post_desc.setText(Desc);
        }

        public void setImageUrl(Context ctx, String imageUrl){
            ImageView post_image = mView.findViewById(R.id.post_image);
            Picasso.get().load(imageUrl).into(post_image);
        }

        public void setUserName(String username){
            TextView post_user = mView.findViewById(R.id.post_user);
            post_user.setText(username);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings){
            return true;
        }
        else if(id == R.id.action_add){
            startActivity(new Intent(MainActivity.this, PostActivity.class));
        }
        else if(id == R.id.logout){
            mAuth.signOut();
            Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
            logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logoutIntent);
        }
        return super.onOptionsItemSelected(item);
    }
}