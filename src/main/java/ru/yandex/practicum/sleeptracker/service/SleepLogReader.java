package ru.yandex.practicum.sleeptracker.service;

import ru.yandex.practicum.sleeptracker.exception.SleepLogFormatException;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class SleepLogReader {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.uu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private SleepLogReader() {
    }

    public static List<SleepingSession> read(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath));

        return IntStream.range(0, lines.size())
                .filter(index -> !lines.get(index).isBlank())
                .mapToObj(index -> parseLine(lines.get(index), index + 1))
                .collect(Collectors.toList());
    }

    private static SleepingSession parseLine(String line, int lineNumber) {
        String[] parts = line.split(";", -1);
        if (parts.length != 3) {
            throw new SleepLogFormatException("В строке " + lineNumber + " ожидается формат: дата засыпания;дата пробуждения;качество сна");
        }

        LocalDateTime start = parseDateTime(parts[0], lineNumber);
        LocalDateTime end = parseDateTime(parts[1], lineNumber);
        SleepQuality quality = parseQuality(parts[2], lineNumber);

        if (end.isBefore(start)) {
            throw new SleepLogFormatException("В строке " + lineNumber + " время пробуждения раньше времени засыпания");
        }

        return new SleepingSession(start, end, quality);
    }

    private static LocalDateTime parseDateTime(String value, int lineNumber) {
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new SleepLogFormatException("В строке " + lineNumber + " указана некорректная дата");
        }
    }

    private static SleepQuality parseQuality(String value, int lineNumber) {
        String quality = value.trim();
        try {
            return SleepQuality.valueOf(quality);
        } catch (IllegalArgumentException exception) {
            throw new SleepLogFormatException("Неизвестное качество сна в строке " + lineNumber + ": " + quality);
        }
    }
}
