package ru.kahn.recordvoice.fragments;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import ru.kahn.recordvoice.R;
import ru.kahn.recordvoice.activity.MainActivity;

public class PageVoice extends Fragment {

    private View view;
    private MediaRecorder mediaRecorder;
    private final Handler handler = new Handler();
    private String fileName;

    private Button startRecordButton;
    private TextView timeRecordTextView;
    private boolean startRecord = true;
    private double finalRecord = 0;

    private Calendar calendar = Calendar.getInstance();
    private int year = calendar.get(Calendar.YEAR);
    private int month = calendar.get(Calendar.MONTH)+1;
    private int day = calendar.get(Calendar.DAY_OF_MONTH);

    public static PageVoice newInstance() {
        PageVoice voiceFragment = new PageVoice();
        Bundle res = new Bundle();
        voiceFragment.setArguments(res);
        return voiceFragment;
    }

    @SuppressLint("NewApi")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main_voice, null);
        ((MainActivity)getActivity()).setTagFragmentVoice(getTag());

        timeRecordTextView = view.findViewById(R.id.time_record);
        startRecordButton = view.findViewById(R.id.start_record_button);

        final Animation animRecordRun = AnimationUtils.loadAnimation(getActivity(), R.anim.button_record_run);

        startRecordButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if(startRecord) {
                    timeRecordTextView.setText("00:00");

                    //finalRecord = 0;

                    startRecordButton.setBackgroundResource(R.drawable.button_record_run_shape);
                    v.startAnimation(animRecordRun);
                    startRecordVoice();
                    startRecordProgressUpdater();
                    ((MainActivity)getActivity()).getPager().setClickable(false);
                } else {
                    stopRecordVoice();
                    startRecordButton.setBackgroundResource(R.drawable.button_record_stop_shape);
                    v.clearAnimation();
                    ((MainActivity)getActivity()).getPager().setClickable(true);

                    String tagOfFragmentPageArchive = ((MainActivity) Objects.requireNonNull(getActivity())).getTagFragmentArchive();
                    PageArchive fragmentArchive = (PageArchive)getActivity()
                            .getSupportFragmentManager()
                            .findFragmentByTag(tagOfFragmentPageArchive);
                    Objects.requireNonNull(fragmentArchive).updateRecycler();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        stopRecordVoice();
        releaseRecorder();
        super.onDestroy();
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

    @SuppressLint("InlinedApi")
    private void startRecordVoice() {
        try {
            releaseRecorder();
            calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);

            fileName = create().getPath() + "/Record" + day + month + year + hour + minute + second + ".mpeg4"; // + day + month + year + hour + minute + second + numID;//+"/"+ day +"_"+ month +"_"+ year +"_"+ hour +":"+ minute +":"+ second;

            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.HE_AAC);
            mediaRecorder.setAudioSamplingRate(16000);
            mediaRecorder.setOutputFile(fileName);
            mediaRecorder.prepare();
            mediaRecorder.start();

            startRecord = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopRecordVoice() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            startRecord = true;
        }
        //startRecord = true;
    }

    private void releaseRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.release();
            mediaRecorder = null;
            fileName = null;
        }
    }

    @SuppressLint("DefaultLocale")
    private void startRecordProgressUpdater() {
        if(!startRecord) {
            Runnable notification = new Runnable() {
                @Override
                public void run() {
                    startRecordProgressUpdater();
                }
            };
            handler.postDelayed(notification, 1000);
        }
        finalRecord = finalRecord + 1000;
        timeRecordTextView.setText(String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes((long) finalRecord),
                TimeUnit.MILLISECONDS.toSeconds((long) finalRecord) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)
                                finalRecord)))
        );
    }
}
