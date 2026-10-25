package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.util.List;
import java.util.function.Function;

public class MinDurationAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long minDuration = sessions.stream()
                .map(SleepingSession::getDurationMinutes)
                .min(Long::compareTo)
                .orElse(0L);

        return new SleepAnalysisResult("Минимальная продолжительность сессии (мин.)", minDuration);
    }
}
