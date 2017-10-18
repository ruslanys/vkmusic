package me.ruslanys.vkmusic.ui.model;

import com.google.common.collect.Lists;
import lombok.SneakyThrows;
import me.ruslanys.vkmusic.entity.Audio;
import me.ruslanys.vkmusic.entity.domain.DownloadStatus;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.net.URL;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class AudioTableModel extends AbstractTableModel {

    private static final String[] COLUMN_LABELS = new String[] { "ID", "Исполнитель", "Наименование",
            "Продолжительность", "Статус" };

    private final List<Audio> entities = Lists.newArrayList();
    private final Map<Long, Audio> hashMap = new HashMap<>();

    private final EnumMap<DownloadStatus, ImageIcon> statusIcon = new EnumMap<>(DownloadStatus.class);

    public AudioTableModel() {
        for (DownloadStatus status : DownloadStatus.values()) {
            Image image = loadImage(status).getScaledInstance(16, 16, Image.SCALE_SMOOTH);
            statusIcon.put(status, new ImageIcon(image));
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Audio audio = entities.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return audio.getId();
            case 1:
                return audio.getArtist();
            case 2:
                return audio.getTitle();
            case 3:
                return getDuration(audio.getDuration());
            case 4:
                return statusIcon.get(audio.getStatus());
            default:
                return "";
        }
    }

    @SneakyThrows
    private Image loadImage(DownloadStatus status) {
        URL resource = getClass().getClassLoader().getResource("images/status/" + status.name().toLowerCase() + ".png");
        return ImageIO.read(resource);
    }

    private String getDuration(int durationInSec) {
        int minutes = durationInSec / 60;
        int seconds = durationInSec % 60;

        String minutesStr = String.valueOf(minutes);
        String secondsStr = String.valueOf(seconds);
        return StringUtils.leftPad(minutesStr, 2, '0') + ":" + StringUtils.leftPad(secondsStr, 2, '0');
    }

    @Override
    public int getRowCount() {
        return entities.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_LABELS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLUMN_LABELS[column];
    }

    private void addInternal(Audio audio) {
        if (hashMap.get(audio.getId()) != null) {
            return;
        }

        hashMap.put(audio.getId(), audio);
        entities.add(audio.getPosition() - 1, audio);
    }

    public void add(Audio audio) {
        addInternal(audio);
        fireTableDataChanged();
    }

    public void add(List<Audio> audioList) {
        for (Audio audio : audioList) {
            addInternal(audio);
        }

        fireTableDataChanged();
    }

    public Audio get(long id) {
        return hashMap.get(id);
    }

    public void remove(int id) {
        Audio audio = hashMap.remove(id);
        entities.remove(audio);

        fireTableDataChanged();
    }

    public void clear() {
        entities.clear();
        hashMap.clear();

        fireTableDataChanged();
    }
}
