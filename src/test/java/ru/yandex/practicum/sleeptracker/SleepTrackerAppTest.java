package ru.yandex.practicum.sleeptracker;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.sleeptracker.analysis.AverageDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.BadQualitySessionsAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.ChronotypeAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MaxDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.MinDurationAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.SleeplessNightsAnalysis;
import ru.yandex.practicum.sleeptracker.analysis.TotalSessionsAnalysis;
import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepQuality;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SleepTrackerAppTest {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");

    @Test
    void shouldCountAllSessions() {
        SleepAnalysisResult result = new TotalSessionsAnalysis().apply(List.of(
                session("01.10.25 23:00", "02.10.25 07:00", SleepQuality.GOOD),
                session("02.10.25 14:00", "02.10.25 15:00", SleepQuality.NORMAL)
        ));

        assertEquals(2L, result.getValue());
    }

    @Test
    void shouldReturnZeroSessionsForEmptyLog() {
        assertEquals(0L, new TotalSessionsAnalysis().apply(List.of()).getValue());
    }

    @Test
    void shouldFindMinimumDuration() {
        SleepAnalysisResult result = new MinDurationAnalysis().apply(List.of(
                session("01.10.25 10:00", "01.10.25 11:30", SleepQuality.GOOD),
                session("01.10.25 14:00", "01.10.25 14:20", SleepQuality.NORMAL)
        ));

        assertEquals(20L, result.getValue());
    }

    @Test
    void shouldReturnZeroMinimumForEmptyLog() {
        assertEquals(0L, new MinDurationAnalysis().apply(List.of()).getValue());
    }

    @Test
    void shouldFindMaximumDuration() {
        SleepAnalysisResult result = new MaxDurationAnalysis().apply(List.of(
                session("01.10.25 10:00", "01.10.25 11:30", SleepQuality.GOOD),
                session("01.10.25 14:00", "01.10.25 14:20", SleepQuality.NORMAL)
        ));

        assertEquals(90L, result.getValue());
    }

    @Test
    void shouldReturnZeroMaximumForEmptyLog() {
        assertEquals(0L, new MaxDurationAnalysis().apply(List.of()).getValue());
    }

    @Test
    void shouldCalculateAverageDuration() {
        SleepAnalysisResult result = new AverageDurationAnalysis().apply(List.of(
                session("01.10.25 10:00", "01.10.25 11:00", SleepQuality.GOOD),
                session("01.10.25 14:00", "01.10.25 15:01", SleepQuality.NORMAL)
        ));

        assertEquals(60.5, (Double) result.getValue(), 0.001);
    }

    @Test
    void shouldReturnZeroAverageForEmptyLog() {
        assertEquals(0.0, (Double) new AverageDurationAnalysis().apply(List.of()).getValue(), 0.001);
    }

    @Test
    void shouldCountBadQualitySessions() {
        SleepAnalysisResult result = new BadQualitySessionsAnalysis().apply(List.of(
                session("01.10.25 10:00", "01.10.25 11:00", SleepQuality.BAD),
                session("01.10.25 14:00", "01.10.25 15:00", SleepQuality.NORMAL),
                session("01.10.25 23:00", "02.10.25 07:00", SleepQuality.BAD)
        ));

        assertEquals(2L, result.getValue());
    }

    @Test
    void shouldReturnZeroWhenThereAreNoBadSessions() {
        SleepAnalysisResult result = new BadQualitySessionsAnalysis().apply(List.of(
                session("01.10.25 10:00", "01.10.25 11:00", SleepQuality.GOOD)
        ));

        assertEquals(0L, result.getValue());
    }

    @Test
    void shouldNotCountNightAsSleeplessWhenSleepCrossesMidnight() {
        SleepAnalysisResult result = new SleeplessNightsAnalysis().apply(List.of(
                session("01.10.25 23:00", "02.10.25 08:00", SleepQuality.GOOD)
        ));

        assertEquals(0L, result.getValue());
    }

    @Test
    void shouldNotCountNightAsSleeplessForSleepFromTwoToFive() {
        SleepAnalysisResult result = new SleeplessNightsAnalysis().apply(List.of(
                session("02.10.25 02:00", "02.10.25 05:00", SleepQuality.GOOD)
        ));

        assertEquals(0L, result.getValue());
    }

    @Test
    void shouldCountNightAsSleeplessForEveningSleep() {
        SleepAnalysisResult result = new SleeplessNightsAnalysis().apply(List.of(
                session("01.10.25 17:00", "01.10.25 23:00", SleepQuality.GOOD)
        ));

        assertEquals(1L, result.getValue());
    }

    @Test
    void shouldCountNightAsSleeplessForMorningSleepAfterSix() {
        SleepAnalysisResult result = new SleeplessNightsAnalysis().apply(List.of(
                session("02.10.25 07:00", "02.10.25 11:00", SleepQuality.GOOD)
        ));

        assertEquals(1L, result.getValue());
    }

    @Test
    void shouldCountSleeplessNightsAcrossMonthBoundary() {
        SleepAnalysisResult result = new SleeplessNightsAnalysis().apply(List.of(
                session("30.10.25 23:00", "31.10.25 05:00", SleepQuality.GOOD),
                session("02.11.25 07:00", "02.11.25 08:00", SleepQuality.NORMAL)
        ));

        assertEquals(2L, result.getValue());
    }

    @Test
    void shouldReturnZeroSleeplessNightsForEmptyLog() {
        assertEquals(0L, new SleeplessNightsAnalysis().apply(List.of()).getValue());
    }

    @Test
    void shouldClassifyUserAsOwl() {
        SleepAnalysisResult result = new ChronotypeAnalysis().apply(List.of(
                session("01.10.25 23:30", "02.10.25 10:00", SleepQuality.GOOD),
                session("02.10.25 23:40", "03.10.25 09:30", SleepQuality.GOOD),
                session("03.10.25 22:30", "04.10.25 08:00", SleepQuality.NORMAL)
        ));

        assertEquals(Chronotype.OWL, result.getValue());
    }

    @Test
    void shouldClassifyUserAsLark() {
        SleepAnalysisResult result = new ChronotypeAnalysis().apply(List.of(
                session("01.10.25 21:00", "02.10.25 06:00", SleepQuality.GOOD),
                session("02.10.25 20:30", "03.10.25 05:30", SleepQuality.GOOD)
        ));

        assertEquals(Chronotype.LARK, result.getValue());
    }

    @Test
    void shouldClassifyUserAsPigeonWhenTypesAreTied() {
        SleepAnalysisResult result = new ChronotypeAnalysis().apply(List.of(
                session("01.10.25 23:30", "02.10.25 10:00", SleepQuality.GOOD),
                session("02.10.25 21:00", "03.10.25 06:00", SleepQuality.GOOD)
        ));

        assertEquals(Chronotype.PIGEON, result.getValue());
    }

    @Test
    void shouldIgnoreDaytimeSessionsWhenClassifyingChronotype() {
        SleepAnalysisResult result = new ChronotypeAnalysis().apply(List.of(
                session("01.10.25 14:00", "01.10.25 15:00", SleepQuality.GOOD),
                session("01.10.25 23:30", "02.10.25 10:00", SleepQuality.GOOD)
        ));

        assertEquals(Chronotype.OWL, result.getValue());
    }

    private SleepingSession session(String start, String end, SleepQuality quality) {
        return new SleepingSession(
                LocalDateTime.parse(start, FORMATTER),
                LocalDateTime.parse(end, FORMATTER),
                quality
        );
    }
}
