package me.ruslanys.vkaudiosaver.ui.model;

import com.google.common.collect.Lists;
import me.ruslanys.vkaudiosaver.domain.Audio;
import org.apache.commons.lang3.StringUtils;

import javax.swing.table.AbstractTableModel;
import java.util.*;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class AudioTableModel extends AbstractTableModel {

    private static final String[] COLUMN_LABELS = new String[] { "ID", "Исполнитель", "Наименование",
            "Продолжительность", "Статус" };

    protected final List<Audio> entities = Lists.newArrayList();
    protected final Map<Integer, Audio> hashMap = new HashMap<>();

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
                return audio.getStatus();
            default:
                return "";
        }
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

    public void add(Audio entity) {
        if (hashMap.get(entity.getId()) != null) {
            return;
        }

        this.entities.add(entity);
        this.entities.sort(Comparator.comparing(Audio::getPosition));
        this.hashMap.put(entity.getId(), entity);
        fireTableDataChanged();
    }

    public void add(List<Audio> entities) {
        List<Audio> list = new ArrayList<>(entities);
        Iterator<Audio> it = list.iterator();
        while (it.hasNext()) {
            Audio audio = it.next();
            if (hashMap.get(audio.getId()) != null) {
                it.remove();
            } else {
                this.hashMap.put(audio.getId(), audio);
                this.entities.add(audio);
            }
        }
        this.entities.sort(Comparator.comparing(Audio::getPosition));

        fireTableDataChanged();
    }

    public Audio get(int id) {
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
