package ru.yandex.practicum.sleeptracker;

import ru.yandex.practicum.sleeptracker.analysis.AverageDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.BadQualitySessionsAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MaxDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MinDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.TotalSessionsAnalysis;
import ru.yandex.practicum.sleeptracker.exception.SleepLogFormatException;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;
import ru.yandex.practicum.sleeptracker.service.SleepLogReader;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.List;
import java.util.function.Function;

public class SleepTrackerApp {

    private static final List<Function<List<SleepingSession>, SleepAnalysisResult>> ANALYSIS_FUNCTIONS = List.of(
            new TotalSessionsAnalysis(),
            new MinDurationAnalysis(),
            new MaxDurationAnalysis(),
            new AverageDurationAnalysis(),
            new BadQualitySessionsAnalysis(),
            new SleeplessNightsAnalysis(),
            new ChronotypeAnalysis()
    );

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Укажите путь к файлу с логом сна.");
            return;
        }

        try {
            List<SleepingSession> sessions = SleepLogReader.read(args[0]);

            if (sessions.isEmpty()) {
                System.out.println("Файл не содержит записей о сне");
                return;
            }

            ANALYSIS_FUNCTIONS.stream()
                    .map(function -> function.apply(sessions))
                    .forEach(result -> System.out.println(result.getDescription() + ": " + result.getValue()));
        } catch (NoSuchFileException exception) {
            System.out.println("Файл не найден: " + args[0]);
        } catch (SleepLogFormatException exception) {
            System.out.println(exception.getMessage());
        } catch (IOException exception) {
            System.out.println("Не удалось прочитать файл: " + args[0]);
        }
    }
}
