package com.example.musicapp;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SongActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private SongAdapter songAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.song_activity);

        // Khởi tạo DatabaseReference để truy cập vào node "songs" trên Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference().child("songs");

        // Khởi tạo MediaPlayer để phát nhạc từ URL
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());

        // Khởi tạo RecyclerView và Adapter
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        songList = new ArrayList<>();
        songAdapter = new SongAdapter(songList);
        recyclerView.setAdapter(songAdapter);

        // Lấy dữ liệu từ Firebase và cập nhật RecyclerView khi có thay đổi
        fetchSongsFromFirebase();
    }

    private void fetchSongsFromFirebase() {
        // Tạo query để lọc các bài hát có danh mục là "Anime"
        Query query = mDatabase.orderByChild("songsCategory").equalTo("Anime");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                songList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Song song = postSnapshot.getValue(Song.class);
                    songList.add(song);
                }
                songAdapter.notifyDataSetChanged(); // Cập nhật RecyclerView khi có dữ liệu mới
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseData", "Error loading Firebase data: " + databaseError.getMessage());
            }
        });
    }

    // Phương thức để phát nhạc từ URL
    public void playMusic(String url) {
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    Toast.makeText(SongActivity.this, "Đang phát nhạc...", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            Log.e("MediaPlayer", "Lỗi khi phát nhạc: " + e.getMessage());
            Toast.makeText(SongActivity.this, "Lỗi khi phát nhạc", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Adapter cho RecyclerView
    public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

        private final List<Song> songList;

        public SongAdapter(List<Song> songList) {
            this.songList = songList;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_song, parent, false);
            return new SongViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            Song song = songList.get(position);
            holder.textViewTitle.setText(song.getSongTitle());
            holder.textViewArtist.setText(song.getArtist());
            holder.textViewUrl.setText(song.getSongLink());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Lấy ra MainActivity từ Context của itemView
                    SongActivity mainActivity = (SongActivity) v.getContext();
                    // Gọi phương thức playMusic để phát nhạc từ URL của bài hát được chọn
                    mainActivity.playMusic(song.getSongLink());
                }
            });
        }

        @Override
        public int getItemCount() {
            return songList.size();
        }

        public class SongViewHolder extends RecyclerView.ViewHolder {
            public TextView textViewTitle, textViewArtist, textViewUrl;

            public SongViewHolder(View view) {
                super(view);
                textViewTitle = view.findViewById(R.id.textViewTitle);
                textViewArtist = view.findViewById(R.id.textViewArtist);
                textViewUrl = view.findViewById(R.id.textViewUrl);
            }
        }
    }
}
