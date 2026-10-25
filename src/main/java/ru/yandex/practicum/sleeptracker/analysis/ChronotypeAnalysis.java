package ru.yandex.practicum.sleeptracker.analysis;

import ru.yandex.practicum.sleeptracker.model.Chronotype;
import ru.yandex.practicum.sleeptracker.model.SleepAnalysisResult;
import ru.yandex.practicum.sleeptracker.model.SleepingSession;

import java.time.LocalTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ChronotypeAnalysis implements Function<List<SleepingSession>, SleepAnalysisResult> {

    private static final LocalTime SIX_AM = LocalTime.of(6, 0);
    private static final LocalTime SEVEN_AM = LocalTime.of(7, 0);
    private static final LocalTime NINE_AM = LocalTime.of(9, 0);
    private static final LocalTime TEN_PM = LocalTime.of(22, 0);
    private static final LocalTime ELEVEN_PM = LocalTime.of(23, 0);

    @Override
    public SleepAnalysisResult apply(List<SleepingSession> sessions) {
        List<Chronotype> nightTypes = sessions.stream()
                .filter(this::isNightSession)
                .map(this::classifySession)
                .collect(Collectors.toList());

        long owlNights = countType(nightTypes, Chronotype.OWL);
        long larkNights = countType(nightTypes, Chronotype.LARK);
        long pigeonNights = countType(nightTypes, Chronotype.PIGEON);

        Chronotype result = selectChronotype(owlNights, larkNights, pigeonNights);
        return new SleepAnalysisResult("Хронотип пользователя", result);
    }

    private boolean isNightSession(SleepingSession session) {
        boolean crossesDateBoundary = !session.getStart().toLocalDate().equals(session.getEnd().toLocalDate());
        boolean startsBeforeNightEnds = session.getStart().toLocalTime().isBefore(SIX_AM);
        return crossesDateBoundary || startsBeforeNightEnds;
    }

    private Chronotype classifySession(SleepingSession session) {
        LocalTime sleepTime = session.getStart().toLocalTime();
        LocalTime wakeTime = session.getEnd().toLocalTime();

        if (sleepTime.isAfter(ELEVEN_PM) && wakeTime.isAfter(NINE_AM)) {
            return Chronotype.OWL;
        }
        if (sleepTime.isBefore(TEN_PM) && wakeTime.isBefore(SEVEN_AM)) {
            return Chronotype.LARK;
        }
        return Chronotype.PIGEON;
    }

    private long countType(List<Chronotype> nightTypes, Chronotype type) {
        return nightTypes.stream()
                .filter(nightType -> nightType == type)
                .count();
    }

    private Chronotype selectChronotype(long owlNights, long larkNights, long pigeonNights) {
        if (owlNights > larkNights && owlNights > pigeonNights) {
            return Chronotype.OWL;
        }
        if (larkNights > owlNights && larkNights > pigeonNights) {
            return Chronotype.LARK;
        }
        return Chronotype.PIGEON;
    }
}
