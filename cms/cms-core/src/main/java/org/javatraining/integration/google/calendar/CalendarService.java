package org.javatraining.integration.google.calendar;

import org.javatraining.model.PersonVO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface CalendarService {

    CalendarVO addCalendar(@Valid CalendarVO calendarVO);

    void removeCalendar(@Valid CalendarVO calendarVO);

    @NotNull
    @Valid
    CalendarVO getCalendarById(@NotNull String calendarId);

    void addTeacherToCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO teacher);

    void removeTeacherFromCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO teacher);

    void addStudentToCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO student);

    void removeStudentFromCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO student);
}
