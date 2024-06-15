package ru.andryss.homeworkbot.commands.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Util class for localizing messages
 */
@Component
@RequiredArgsConstructor
public class MessageUtils {

    private final MessageSource messageSource;

    /**
     * Returns localized message by pattern (e.g. "Some ${placeholder} text" will result in "Some resolved text" for "placeholder=resolved" line with "en" locale)
     *
     * @param lang language code
     * @param pattern message pattern
     * @param args optional arguments for message
     * @return localized message
     */
    public String localize(String lang, String pattern, Object... args) {
        Locale locale = new Locale(lang);
        StringSubstitutor filler = new StringSubstitutor(s -> messageSource.getMessage(s, args, locale));
        return filler.replace(pattern);
    }
}
