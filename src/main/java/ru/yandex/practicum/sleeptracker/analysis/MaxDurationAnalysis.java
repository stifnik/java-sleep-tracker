package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class MaxDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long maxDuration = sessions.stream()
                .map(SleepingSession::getDurationMinutes)
                .max(Long::compareTo)
                .orElse(0L);

        return new SleepAnalysisResult("Максимальная продолжительность сессии (мин.)", maxDuration);
    }
}
