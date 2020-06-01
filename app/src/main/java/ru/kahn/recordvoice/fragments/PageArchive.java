package ru.kahn.recordvoice.fragments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ru.kahn.recordvoice.R;
import ru.kahn.recordvoice.activity.MainActivity;
import ru.kahn.recordvoice.adapters.AdapterRecordAudio;

public class PageArchive extends Fragment implements AdapterRecordAudio.ClickListener {

    private final Handler handler = new Handler();
    private View view;
    private List<File> fileAudioData = new ArrayList<>();
    private RecyclerView recyclerView;
    private MediaPlayer mediaPlayer;
    private SeekBar seekBarPlayer;
    private TextView timeRecordTextView, tvProgressTime, tvMaxTime;
    private ImageButton btForward, btPlayPause, btRewind;
    private double finalTime = 0;
    private int oldPosition = -1;

    public static PageArchive newInstance() {
        PageArchive archiveFragment = new PageArchive();
        Bundle res = new Bundle();
        archiveFragment.setArguments(res);
        return archiveFragment;
    }

    @SuppressLint({"NewApi", "ClickableViewAccessibility"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_archive, null);
        ((MainActivity)getActivity()).setTagFragmentArchive(getTag());

        fileAudioData.addAll(listFilesWithSubFolders(create()));
        recyclerView = view.findViewById(R.id.recycle_view_record_audio);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        AdapterRecordAudio adapter = new AdapterRecordAudio(fileAudioData, this);
        recyclerView.setAdapter(adapter);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();

        timeRecordTextView = view.findViewById(R.id.time_record_archive);
        tvProgressTime = view.findViewById(R.id.tv_progress_time);
        tvMaxTime = view.findViewById(R.id.tv_max_time);
        seekBarPlayer = view.findViewById(R.id.seek_bar_record);
        btRewind = view.findViewById(R.id.bt_rewind);
        btPlayPause = view.findViewById(R.id.bt_play_pause);
        btForward = view.findViewById(R.id.bt_forward);

        btRewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        seekBarPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                seekChange(v);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        pausePlay();
        releasePlayer();
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClickCard(File postFile, int position) {
        postFile.delete();
        fileAudioData.remove(position);
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }

    @SuppressLint("NewApi")
    @Override
    public void onClickPlay(AdapterRecordAudio.ViewHolder holder, File postFile, int position) {
        Integer integer = (Integer) holder.imageRecordFile.getTag();
        switch (integer) {
            case R.mipmap.ic_play_record_voice:
                if(oldPosition != -1) {
                    pausePlay();
                    Objects.requireNonNull(recyclerView.getAdapter()).notifyItemChanged(oldPosition);
                }
                ((MainActivity)getActivity()).getPager().setClickable(false);

                timeRecordTextView.setText(R.string.time_seek);
                tvProgressTime.setText(R.string.time_seek);
                tvMaxTime.setText(R.string.time_seek);

                holder.imageRecordFile.setImageResource(R.mipmap.ic_pause_record_voice);
                holder.imageRecordFile.setTag(R.mipmap.ic_pause_record_voice);
                oldPosition = position;

                startPlay(postFile);
                startPlayProgressUpdater(holder);
                break;

            case R.mipmap.ic_pause_record_voice:
                ((MainActivity)getActivity()).getPager().setClickable(true);
                oldPosition = -1;
                holder.imageRecordFile.setImageResource(R.mipmap.ic_play_record_voice);
                holder.imageRecordFile.setTag(R.mipmap.ic_play_record_voice);
                pausePlay();
                break;
        }
    }

    private ArrayList<File> listFilesWithSubFolders(File dir) {
        ArrayList<File> files = new ArrayList<>();
        for (File file : dir.listFiles()) {
            if (file.isDirectory())
                files.addAll(listFilesWithSubFolders(file));
            else
                files.add(file);
        }
        return files;
    }

    private File create() {
        File baseDir;
        baseDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);

        if (baseDir == null)
            return Environment.getExternalStorageDirectory();

        File folder = new File(baseDir, "Record");

        if (folder.exists()) {
            return folder;
        }
        if (folder.isFile()) {
            folder.delete();
        }
        if (folder.mkdirs()) {
            return folder;
        }

        return Environment.getExternalStorageDirectory();
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void startPlay(File postFile) {
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(postFile.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            seekBarPlayer.setMax(mediaPlayer.getDuration());

            tvMaxTime.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) mediaPlayer.getDuration()),
                    TimeUnit.MILLISECONDS.toSeconds((long) mediaPlayer.getDuration())%60));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pausePlay() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            seekBarPlayer.setProgress(mediaPlayer.getDuration());
        }
    }

    private void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @SuppressLint("DefaultLocale")
    private void seekChange(View v) {
        if(mediaPlayer.isPlaying()){
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
        } else {
            SeekBar sb = (SeekBar) v;
            mediaPlayer.seekTo(sb.getProgress());
            finalTime = seekBarPlayer.getProgress();
            timeRecordTextView.setText(String.format("%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                    finalTime)))
            );
            tvProgressTime.setText(timeRecordTextView.getText());
        }
    }

    @SuppressLint("DefaultLocale")
    public void startPlayProgressUpdater(final AdapterRecordAudio.ViewHolder holder) {
        seekBarPlayer.setProgress(mediaPlayer.getCurrentPosition());

        if (mediaPlayer.isPlaying()) {
            Runnable notification = new Runnable() {
                public void run() {
                    startPlayProgressUpdater(holder);
                }
            };
            handler.postDelayed(notification, 1000);
        } else {
            //mediaPlayer.pause();
            oldPosition = -1;
            holder.imageRecordFile.setImageResource(R.mipmap.ic_play_record_voice);
            holder.imageRecordFile.setTag(R.mipmap.ic_play_record_voice);
            pausePlay();
        }

        finalTime = seekBarPlayer.getProgress();
        timeRecordTextView.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalTime)))
        );
        tvProgressTime.setText(timeRecordTextView.getText());
    }

    @SuppressLint("NewApi")
    public void updateRecycler() {
        fileAudioData.clear();
        fileAudioData.addAll(listFilesWithSubFolders(create()));
        Objects.requireNonNull(recyclerView.getAdapter()).notifyDataSetChanged();
    }
}
