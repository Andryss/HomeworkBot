package ru.andryss.homeworkbot.commands.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class for working with keyboard
 */
public class KeyboardUtils {

    private KeyboardUtils() {
        throw new UnsupportedOperationException("util class");
    }

    /**
     * Builds one row keyboard from columns
     *
     * @param cols buttons (one column == one button)
     * @return keyboard
     */
    public static List<List<String>> buildOneRowKeyboard(String... cols) {
        return List.of(List.of(cols));
    }

    /**
     * Builds one column keyboard from rows
     *
     * @param rows buttons (one row == one button)
     * @return keyboard
     */
    public static List<List<String>> buildOneColumnKeyboard(List<String> rows) {
        List<List<String>> keyboard = new ArrayList<>(rows.size());
        for (String row : rows) {
            keyboard.add(List.of(row));
        }
        return keyboard;
    }
}
