/*
 * SPDX-License-Identifier: Apache-2.0
 *
 * Copyright 2020-2024 The JReleaser authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jreleaser.version;

import org.jreleaser.bundle.RB;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.binarySearch;
import static java.util.Collections.emptyList;
import static org.jreleaser.util.CollectionUtils.listOf;
import static org.jreleaser.util.StringUtils.isBlank;
import static org.jreleaser.util.StringUtils.isNotBlank;
import static org.jreleaser.util.StringUtils.requireNonBlank;

/**
 * @author Andres Almiray
 * @since 0.9.1
 */
public class CalVer implements Version<CalVer> {
    private static final Map<String, String> PATTERNS = new LinkedHashMap<>();

    private static final String T_YEAR = "YEAR";
    private static final String T_YEAR_LONG = "YYYY";
    private static final String T_YEAR_SHORT = "YY";
    private static final String T_YEAR_ZERO = "0Y";
    private static final String T_MONTH = "MONTH";
    private static final String T_MONTH_SHORT = "MM";
    private static final String T_MONTH_ZERO = "0M";
    private static final String T_WEEK = "WEEK";
    private static final String T_WEEK_SHORT = "WW";
    private static final String T_WEEK_ZERO = "0W";
    private static final String T_DAY = "DAY";
    private static final String T_DAY_SHORT = "DD";
    private static final String T_DAY_ZERO = "0D";
    private static final String T_MINOR = "MINOR";
    private static final String T_MICRO = "MICRO";
    private static final String T_MODIFIER = "MODIFIER";
    private static final String T_MODIFIER_OP = "[MODIFIER]";
    private static final String T_MODIFIER_OP2 = "MODIFIER]";

    private static final String[] YEARS = {T_YEAR_ZERO, T_YEAR_SHORT, T_YEAR_LONG};
    private static final String[] MONTHS = {T_MONTH_ZERO, T_MONTH_SHORT};
    private static final String[] WEEKS = {T_WEEK_ZERO, T_WEEK_SHORT};
    private static final String[] DAYS = {T_DAY_ZERO, T_DAY_SHORT};
    private static final String[] NUMBERS = {T_MICRO, T_MINOR};

    static {
        PATTERNS.put(T_YEAR_LONG, "([2-9][0-9]{3})");
        PATTERNS.put(T_YEAR_SHORT, "([1-9]|[1-9][0-9]|[1-9][0-9]{2})");
        PATTERNS.put(T_YEAR_ZERO, "(0[1-9]|[1-9][0-9]|[1-9][0-9]{2})");
        PATTERNS.put(T_MONTH_SHORT, "([1-9]|1[0-2])");
        PATTERNS.put(T_MONTH_ZERO, "(0[1-9]|1[0-2])");
        PATTERNS.put(T_WEEK_SHORT, "([1-9]|[1-4][0-9]|5[0-2])");
        PATTERNS.put(T_WEEK_ZERO, "(0[1-9]|[1-4][0-9]|5[0-2])");
        PATTERNS.put(T_DAY_SHORT, "([1-9]|[1-2][0-9]|3[0-1])");
        PATTERNS.put(T_DAY_ZERO, "(0[1-9]|[1-2][0-9]|3[0-1])");
        PATTERNS.put(T_MINOR, "(0|[1-9]\\d*)");
        PATTERNS.put(T_MICRO, "(0|[1-9]\\d*)");
        PATTERNS.put(T_MODIFIER, "([a-zA-Z\\-][0-9a-zA-Z\\-]*)");
        PATTERNS.put(T_MODIFIER_OP, "([a-zA-Z\\-][0-9a-zA-Z\\-]*))?");
    }

    private final String year;
    private final String month;
    private final String week;
    private final String day;
    private final String minor;
    private final String micro;
    private final String modifier;
    private final String pattern;

    private final int yearAsInt;
    private final int monthAsInt;
    private final int weekAsInt;
    private final int dayAsInt;
    private final int minorAsInt;
    private final int microAsInt;

    public CalVer(String pattern, Map<String, String> elements) {
        String y = elements.get(T_YEAR);
        String m = elements.get(T_MONTH);
        String w = elements.get(T_WEEK);
        String d = elements.get(T_DAY);
        String n = elements.get(T_MINOR);
        String r = elements.get(T_MICRO);
        String o = elements.get(T_MODIFIER);

        this.pattern = pattern;
        this.year = isNotBlank(y) ? y.trim() : null;
        this.month = isNotBlank(m) ? m.trim() : null;
        this.week = isNotBlank(w) ? w.trim() : null;
        this.day = isNotBlank(d) ? d.trim() : null;
        this.minor = isNotBlank(n) ? n.trim() : null;
        this.micro = isNotBlank(r) ? r.trim() : null;
        this.modifier = isNotBlank(o) ? o.trim() : null;

        this.yearAsInt = isBlank(this.year) ? -1 : parseInt(this.year);
        this.monthAsInt = isBlank(this.month) ? -1 : parseInt(this.month);
        this.weekAsInt = isBlank(this.week) ? -1 : parseInt(this.week);
        this.dayAsInt = isBlank(this.day) ? -1 : parseInt(this.day);
        this.minorAsInt = isBlank(this.minor) ? -1 : parseInt(this.minor);
        this.microAsInt = isBlank(this.micro) ? -1 : parseInt(this.micro);

        // validate num of days per month
        if (yearAsInt != -1 && monthAsInt != -1 && dayAsInt != -1 && dayAsInt > YearMonth.of(yearAsInt, monthAsInt).lengthOfMonth()) {
            throw new IllegalArgumentException(RB.$("ERROR_version_parse", this));
        }
    }

    private static int parseInt(String str) {
        if ("0".equals(str)) return 0;
        if (str.startsWith("0")) {
            return Integer.parseInt(str.substring(1));
        }
        return Integer.parseInt(str);
    }

    public static CalVer of(String format, String version) {
        requireNonBlank(format, "Argument 'format' must not be blank");
        requireNonBlank(version, "Argument 'version' must not be blank");

        TokenizationResult result = TokenizationResult.tokenize(format);

        Pattern pattern = Pattern.compile("^" + result.tokens.stream()
            .map(t -> PATTERNS.getOrDefault(t, t))
            .collect(Collectors.joining("")) + "$");

        Matcher matcher = pattern.matcher(version.trim());

        if (matcher.matches()) {
            return extractCalVer(format, matcher, result);
        }

        throw new IllegalArgumentException(RB.$("ERROR_version_parse_with", version, result.f));
    }

    private static CalVer extractCalVer(String format, Matcher matcher, TokenizationResult result) {
        int i = 1;
        Map<String, String> elements = new LinkedHashMap<>();
        elements.put(T_YEAR, matcher.group(i++));
        if (isNotBlank(result.w)) {
            elements.put(T_WEEK, matcher.group(i++));
        }
        if (isNotBlank(result.m)) {
            elements.put(T_MONTH, matcher.group(i++));
        }
        if (isNotBlank(result.d)) {
            elements.put(T_DAY, matcher.group(i++));
        }
        if (isNotBlank(result.n)) {
            elements.put(T_MINOR, matcher.group(i++));
        }
        if (isNotBlank(result.r)) {
            elements.put(T_MICRO, matcher.group(i++));
        }
        if (i <= matcher.groupCount()) {
            elements.put(T_MODIFIER, matcher.group(matcher.groupCount()));
        }

        return new CalVer(format, elements);
    }


    public static CalVer defaultOf(String format) {
        requireNonBlank(format, "Argument 'format' must not be blank");

        return of(format, format.replace(T_YEAR_LONG, "2000")
            .replace(T_YEAR_SHORT, "0")
            .replace(T_YEAR_ZERO, "0")
            .replace(T_MONTH_SHORT, "1")
            .replace(T_MONTH_ZERO, "01")
            .replace(T_WEEK_SHORT, "1")
            .replace(T_WEEK_ZERO, "01")
            .replace(T_DAY_SHORT, "1")
            .replace(T_DAY_ZERO, "01")
            .replace(T_MINOR, "0")
            .replace(T_MICRO, "0")
            .replace(T_MODIFIER, "A")
            .replace("[", "")
            .replace("]", ""));
    }

    private static Tuple take(String str, int index, List<Character> delims) {
        StringBuilder b = new StringBuilder();

        for (int i = index; i < str.length(); i++) {
            char c = str.charAt(i);
            if (delims.contains(c)) {
                if (c == '[' && str.length() > i + 1) {
                    c = str.charAt(i + 1);
                }
                return new Tuple(b.toString(), c);
            }
            b.append(c);
        }

        return new Tuple(b.toString(), (char) 0);
    }

    public boolean hasYear() {
        return isNotBlank(year);
    }

    public boolean hasMonth() {
        return isNotBlank(month);
    }

    public boolean hasWeek() {
        return isNotBlank(week);
    }

    public boolean hasDay() {
        return isNotBlank(day);
    }

    public boolean hasMinor() {
        return isNotBlank(minor);
    }

    public boolean hasMicro() {
        return isNotBlank(micro);
    }

    public boolean hasModifier() {
        return isNotBlank(modifier);
    }

    public String getPattern() {
        return pattern;
    }

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getWeek() {
        return week;
    }

    public String getDay() {
        return day;
    }

    public String getMinor() {
        return minor;
    }

    public String getMicro() {
        return micro;
    }

    public String getModifier() {
        return modifier;
    }

    public int getYearAsInt() {
        return yearAsInt;
    }

    public int getMonthAsInt() {
        return monthAsInt;
    }

    public int getWeekAsInt() {
        return weekAsInt;
    }

    public int getDayAsInt() {
        return dayAsInt;
    }

    public int getMinorAsInt() {
        return minorAsInt;
    }

    public int getMicroAsInt() {
        return microAsInt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (null == o || getClass() != o.getClass()) return false;
        CalVer version = (CalVer) o;
        return Objects.equals(pattern, version.pattern) &&
            Objects.equals(year, version.year) &&
            Objects.equals(month, version.month) &&
            Objects.equals(week, version.week) &&
            Objects.equals(day, version.day) &&
            Objects.equals(minor, version.minor) &&
            Objects.equals(micro, version.micro) &&
            Objects.equals(modifier, version.modifier);
    }

    @Override
    public String toRpmVersion() {
        return toString().replace("-", "_");
    }

    @Override
    public String toString() {
        String str = safeReplace(pattern, T_YEAR_LONG, year);
        str = safeReplace(str, T_YEAR_SHORT, year);
        str = safeReplace(str, T_YEAR_ZERO, year);
        str = safeReplace(str, T_MONTH_SHORT, month);
        str = safeReplace(str, T_MONTH_ZERO, month);
        str = safeReplace(str, T_WEEK_SHORT, week);
        str = safeReplace(str, T_WEEK_ZERO, week);
        str = safeReplace(str, T_DAY_SHORT, day);
        str = safeReplace(str, T_DAY_ZERO, day);
        str = safeReplace(str, T_MINOR, minor);
        str = safeReplace(str, T_MICRO, micro);
        str = safeReplace(str, T_MODIFIER, modifier);
        str = safeReplace(str, "[", "");
        str = safeReplace(str, "]", "");

        return str;
    }

    private String safeReplace(String str, CharSequence target, CharSequence replacement) {
        if (null != replacement) {
            return str.replace(target, replacement);
        }
        return str;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pattern, year, month, week, day, minor, micro, modifier);
    }

    @Override
    public int compareTo(CalVer other) {
        int result = pattern.compareTo(other.pattern);

        if (result == 0) {
            result = yearAsInt - (other.hasYear() ? other.yearAsInt : 0);
        }

        if (result == 0) {
            result = hasMonth() ? monthAsInt - (other.hasMonth() ? other.monthAsInt : 0) : 0;
        }

        if (result == 0) {
            result = hasWeek() ? weekAsInt - (other.hasWeek() ? other.weekAsInt : 0) : 0;
        }

        if (result == 0) {
            result = hasDay() ? dayAsInt - (other.hasDay() ? other.dayAsInt : 0) : 0;
        }

        if (result == 0) {
            result = hasMinor() ? minorAsInt - (other.hasMinor() ? other.minorAsInt : 0) : 0;
        }

        if (result == 0) {
            result = hasMicro() ? microAsInt - (other.hasMicro() ? other.microAsInt : 0) : 0;
        }

        if (result == 0 && isNotBlank(modifier)) {
            result = isNotBlank(other.modifier) ? modifier.compareTo(other.modifier) : -1;
        }

        return result;
    }

    @Override
    public boolean equalsSpec(CalVer version) {
        return pattern.equals(version.pattern);
    }

    private static class Tuple {
        private final String token;
        private final String sep;

        private Tuple(String token, char sep) {
            this.token = token;
            this.sep = sep != ((char) 0) ? escape(sep) : "";
        }

        private static String escape(char sep) {
            if ('.' == sep) {
                return "\\.";
            }
            return String.valueOf(sep);
        }
    }

    static class TokenizationResult {
        List<String> tokens;
        String f;
        String y = null;
        String m = null;
        String w = null;
        String d = null;
        String n = null;
        String r = null;
        String o = null;
        private Tuple s;
        private int i = 0;

        public TokenizationResult(String format, List<Character> delims) {
            this.tokens = new ArrayList<>();
            this.f = format;
            this.s = take(this.f, this.i, delims);
        }

        public static TokenizationResult tokenize(String format) {
            List<Character> delims = listOf('.', '_', '-', '[');
            TokenizationResult result = new TokenizationResult(format.trim(), delims);

            if (binarySearch(YEARS, result.s.token) < 0) {
                throw new IllegalArgumentException(RB.$("ERROR_calver_year", result.f));
            }
            result.y = result.s.token;
            result.tokens.add(result.y);
            if (isNotBlank(result.s.sep)) result.tokens.add(result.s.sep);
            result.i = result.y.length() + 1;

            result.s = take(result.f, result.i, delims);
            if (binarySearch(MONTHS, result.s.token) >= 0) {
                extractMonth(result, delims);
            } else if (binarySearch(WEEKS, result.s.token) >= 0) {
                extractWeeks(result, delims);
            }

            MicroResult microResult = extractMicro(result, delims);

            if (!microResult.done) {
                if (binarySearch(NUMBERS, result.s.token) >= 0) {
                    extractNumbers(microResult, result);
                }
                result.s = take(result.f, result.i, emptyList());
                result.o = result.s.token;
                if (isNotBlank(result.o)) result.tokens.add(result.o);
            }

            if (result.tokens.get(result.tokens.size() - 1).endsWith(T_MODIFIER_OP2)) {
                String sep = result.tokens.remove(result.tokens.size() - 2);
                String mod = "(?:" + sep + PATTERNS.get(T_MODIFIER_OP);
                result.tokens.set(result.tokens.size() - 1, mod);
            }

            return result;
        }

        private static void extractNumbers(MicroResult microResult, TokenizationResult result) {
            if (microResult.micro) {
                if (T_MICRO.equals(result.s.token)) {
                    throw new IllegalArgumentException(RB.$("ERROR_calver_micro_duplicate", result.f));
                } else {
                    throw new IllegalArgumentException(RB.$("ERROR_calver_micro_minor", result.f));
                }
            } else if (T_MINOR.equals(result.s.token)) {
                throw new IllegalArgumentException(RB.$("ERROR_calver_minor_duplicate", result.f));
            }
            result.tokens.add(result.s.token);
            if (isNotBlank(result.s.sep)) result.tokens.add(result.s.sep);
            result.r = result.s.token;
            result.i += result.r.length() + 1;
        }

        // [done, micro]
        private static MicroResult extractMicro(TokenizationResult result, List<Character> delims) {
            boolean done;
            boolean micro = false;
            if (binarySearch(NUMBERS, result.s.token) >= 0) {
                result.tokens.add(result.s.token);
                if (isNotBlank(result.s.sep)) result.tokens.add(result.s.sep);
                result.i += result.s.token.length() + 1;
                micro = T_MICRO.equals(result.s.token);
                result.n = !micro ? result.s.token : null;
                result.r = micro ? result.s.token : null;
                result.s = take(result.f, result.i, delims);
                done = isBlank(result.s.token);
            } else {
                result.s = take(result.f, result.i, emptyList());
                result.o = result.s.token;
                if (isNotBlank(result.o)) result.tokens.add(result.o);
                done = true;
            }
            return new MicroResult(micro, done);
        }

        private static void extractWeeks(TokenizationResult result, List<Character> delims) {
            // cannot have months nor days
            if (result.f.contains(T_MONTH_ZERO) || result.f.contains(T_MONTH_SHORT)) {
                throw new IllegalArgumentException(RB.$("ERROR_calver_week_month", result.f));
            }
            if (result.f.contains(T_DAY_ZERO) || result.f.contains(T_DAY_SHORT)) {
                throw new IllegalArgumentException(RB.$("ERROR_calver_week_day", result.f));
            }
            result.w = result.s.token;
            result.tokens.add(result.w);
            if (isNotBlank(result.s.sep)) result.tokens.add(result.s.sep);
            result.i += result.w.length() + 1;

            result.s = take(result.f, result.i, delims);
        }

        private static void extractMonth(TokenizationResult result, List<Character> delims) {
            // cannot have weeks
            if (result.f.contains(T_WEEK_ZERO) || result.f.contains(T_WEEK_SHORT)) {
                throw new IllegalArgumentException(RB.$("ERROR_calver_month", result.f));
            }
            result.m = result.s.token;
            result.tokens.add(result.m);
            if (isNotBlank(result.s.sep)) result.tokens.add(result.s.sep);
            result.i += result.m.length() + 1;

            result.s = take(result.f, result.i, delims);
            if (binarySearch(DAYS, result.s.token) >= 0) {
                result.d = result.s.token;
                result.tokens.add(result.d);
                if (isNotBlank(result.s.sep)) result.tokens.add(result.s.sep);
                result.i += result.d.length() + 1;
                result.s = take(result.f, result.i, delims);
            }
        }

        static class MicroResult {
            boolean micro;
            boolean done;

            public MicroResult(boolean micro, boolean done) {
                this.micro = micro;
                this.done = done;
            }
        }

    }
}
