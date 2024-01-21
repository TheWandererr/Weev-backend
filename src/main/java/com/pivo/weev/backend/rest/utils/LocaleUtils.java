package com.pivo.weev.backend.rest.utils;

import static com.pivo.weev.backend.rest.utils.Constants.Languages.ACCEPTED_LANGUAGES;
import static com.pivo.weev.backend.rest.utils.Constants.Languages.DEFAULT_LANGUAGE;
import static com.pivo.weev.backend.utils.Constants.Symbols.DASH;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.LocaleUtils.languagesByCountry;

import com.pivo.weev.backend.utils.ArrayUtils;
import com.pivo.weev.backend.utils.CollectionUtils;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import lombok.experimental.UtilityClass;
import org.springframework.context.i18n.LocaleContextHolder;

@UtilityClass
public class LocaleUtils {

    public static Locale getRequestLocale() {
        return LocaleContextHolder.getLocale();
    }

    public static void setRequestLocale(Locale locale, boolean inheritable) {
        LocaleContextHolder.setLocale(locale, inheritable);
    }

    public static Locale getDefaultLocale() {
        return Locale.US;
    }

    public static Locale getAcceptedLocale() {
        Locale requestLocale = getRequestLocale();
        return isSupportedLanguage(requestLocale.getLanguage()) ? requestLocale : findAcceptedLocale(requestLocale.getCountry());
    }

    public static Locale getAcceptedLocale(String language) {
        return isSupportedLanguage(language) ? org.apache.commons.lang3.LocaleUtils.toLocale(language) : getDefaultLocale();
    }

    public static Locale parseLocale(String languageTag) {
        return Locale.forLanguageTag(languageTag);
    }

    private static Locale findAcceptedLocale(String country) {
        List<Locale> locales = languagesByCountry(country);
        return CollectionUtils.findFirst(locales, locale -> isSupportedLanguage(locale.getLanguage())).orElse(getDefaultLocale());
    }

    public static String getAcceptedLanguage(String requestedLanguage) {
        return ACCEPTED_LANGUAGES.contains(requestedLanguage) ? requestedLanguage : DEFAULT_LANGUAGE;
    }

    public static String getLanguage(String languageTag) {
        return ofNullable(languageTag)
                .map(tag -> tag.split(DASH))
                .map(ArrayUtils::first)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .orElse(DEFAULT_LANGUAGE);
    }

    public static boolean isSupportedLanguage(String language) {
        return ACCEPTED_LANGUAGES.contains(language);
    }

    public static boolean isSupportedLanguageTag(String languageTag) {
        return isSupportedLocale(parseLocale(languageTag));
    }

    public static boolean isSupportedLocale(Locale locale) {
        return isSupportedLanguage(locale.getLanguage());
    }
}
