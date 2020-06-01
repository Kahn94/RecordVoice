package ru.kahn.recordvoice.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.Date;
import java.util.List;

import ru.kahn.recordvoice.R;

public class AdapterRecordAudio extends RecyclerView.Adapter<AdapterRecordAudio.ViewHolder>{

    private List<File> fileAudioData;
    private ClickListener click;

    public interface ClickListener {
        void onClickCard(File postFile, int position);
        void onClickPlay(ViewHolder holder, File postFile, int position);
    }

    public AdapterRecordAudio(List<File> fileAudioData, ClickListener click) {
        this.fileAudioData = fileAudioData;
        this.click = click;
    }

    @NonNull
    @Override
    public AdapterRecordAudio.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_record_audio_file, parent, false);
        return new AdapterRecordAudio.ViewHolder(v);
    }

    @SuppressLint({"NewApi", "DefaultLocale"})
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final File postFile = fileAudioData.get(position);

        Date lastModified = new Date(postFile.lastModified());
        String data = String.format("%te.%tm.%tY %tT", lastModified.getTime(), lastModified.getTime(), lastModified.getTime(), lastModified.getTime());

        holder.fileRecordNameItem.setText(postFile.getName());
        holder.fileRecordDateItem.setText(data);
        holder.fileRecordSizeItem.setText(humanReadableByteCount(postFile.length(), true));
        holder.imageRecordFile.setImageResource(R.mipmap.ic_play_record_voice);
        holder.imageRecordFile.setTag(R.mipmap.ic_play_record_voice);

        holder.imageRecordFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click.onClickPlay(holder, postFile, position);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                click.onClickCard(postFile, position);
                return false;
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (fileAudioData == null) {
            return 0;
        }
        return fileAudioData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageRecordFile;
        TextView fileRecordNameItem;
        TextView fileRecordSizeItem;
        TextView fileRecordDateItem;

        ViewHolder(View itemView) {
            super(itemView);
            fileRecordNameItem = itemView.findViewById(R.id.file_record_name_item);
            fileRecordSizeItem = itemView.findViewById(R.id.file_record_size_item);
            fileRecordDateItem = itemView.findViewById(R.id.file_record_date_item);
            imageRecordFile = itemView.findViewById(R.id.image_record_file);
        }
    }

    @SuppressLint("DefaultLocale")
    private String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}
