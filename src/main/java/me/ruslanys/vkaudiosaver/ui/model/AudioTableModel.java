package me.ruslanys.vkaudiosaver.ui.model;

import me.ruslanys.vkaudiosaver.domain.Audio;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Ruslan Molchanov (ruslanys@gmail.com)
 */
public class AudioTableModel extends DefaultTableModel<Audio> {

    private static final String[] COLUMN_LABELS = new String[] { "ID", "Исполнитель", "Наименование",
            "Продолжительность", "Статус" };

    @Override
    public String[] getColumnLabels() {
        return COLUMN_LABELS;
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
}
