package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;

public class SleeplessNightsAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime NOON = LocalTime.of(12, 0);
    private static final LocalTime NIGHT_END = LocalTime.of(6, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        long sleeplessNights = sessions.isEmpty() ? 0 : countSleeplessNights(sessions);
        return new SleepAnalysisResult("Количество бессонных ночей", sleeplessNights);
    }

    private long countSleeplessNights(List<SleepingSession> sessions) {
        SleepingSession firstSession = sessions.get(0);
        SleepingSession lastSession = sessions.get(sessions.size() - 1);

        LocalDate firstNight = getFirstNightDate(firstSession);
        LocalDate lastNight = lastSession.getEnd().toLocalDate();
        if (lastNight.isBefore(firstNight)) {
            lastNight = firstNight;
        }

        return countSleeplessNights(firstNight, lastNight, sessions);
    }

    private LocalDate getFirstNightDate(SleepingSession firstSession) {
        LocalDate startDate = firstSession.getStart().toLocalDate();
        LocalTime startTime = firstSession.getStart().toLocalTime();
        return startTime.isBefore(NOON) ? startDate : startDate.plusDays(1);
    }

    private long countSleeplessNights(LocalDate nightDate, LocalDate lastNight, List<SleepingSession> sessions) {
        if (nightDate.isAfter(lastNight)) {
            return 0;
        }

        LocalDateTime nightStart = LocalDateTime.of(nightDate, LocalTime.MIDNIGHT);
        LocalDateTime nightEnd = LocalDateTime.of(nightDate, NIGHT_END);
        boolean userSlept = sessions.stream()
                .filter(session -> intersects(session, nightStart, nightEnd))
                .findFirst()
                .isPresent();

        long currentNightResult = userSlept ? 0 : 1;
        return currentNightResult + countSleeplessNights(nightDate.plusDays(1), lastNight, sessions);
    }

    private boolean intersects(SleepingSession session, LocalDateTime intervalStart, LocalDateTime intervalEnd) {
        return session.getStart().isBefore(intervalEnd) && session.getEnd().isAfter(intervalStart);
    }
}
