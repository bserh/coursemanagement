package org.javatraining.integration.google.calendar;

import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import org.javatraining.integration.google.calendar.exception.CalendarException;
import org.javatraining.integration.google.calendar.exception.CalendarRoleAlreadyExistsException;
import org.javatraining.integration.google.calendar.exception.CalendarRoleNotExistsException;
import org.javatraining.model.PersonVO;
import org.javatraining.service.PersonService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class CalendarServiceImpl implements CalendarService {

    private static final String CALENDAR_ROLE_TEACHER = "writer";
    private static final String CALENDAR_ROLE_STUDENT = "reader";

    @Inject
    private com.google.api.services.calendar.Calendar calendarService;
    @Inject
    private PersonService personService;

    @Override
    public CalendarVO addCalendar(@Valid CalendarVO calendarVO) {
        try {
            Calendar calendar = new Calendar();
            calendar.setSummary(calendarVO.getTitle());
            Calendar addedCalendar = calendarService.calendars().insert(calendar).execute();
            if (addedCalendar == null) {
                throw new CalendarException(String.format("Calendar %s can't be added.", calendarVO.getTitle()));
            }

            BatchRequest batch = calendarService.batch();
            JsonBatchCallback<AclRule> callback = new JsonBatchCallback<AclRule>() {
                @Override
                public void onSuccess(AclRule aclRule, HttpHeaders httpHeaders) throws IOException {
                }

                @Override
                public void onFailure(GoogleJsonError e, HttpHeaders responseHeaders) {
                    throw new CalendarException(e.getMessage());
                }
            };
            AclRule rule;
            AclRule.Scope scope;

            rule = new AclRule();
            scope = new AclRule.Scope();
            scope.setType("default");
            rule.setScope(scope).setRole("reader");
            calendarService.acl().insert(addedCalendar.getId(), rule).queue(batch, callback);

            List<PersonVO> teachers = calendarVO.getTeachers();
            for (PersonVO teacher : teachers) {
                rule = new AclRule();
                scope = new AclRule.Scope();
                scope.setType("user").setValue(teacher.getEmail());
                rule.setScope(scope).setRole(CALENDAR_ROLE_TEACHER);
                calendarService.acl().insert(addedCalendar.getId(), rule).queue(batch, callback);
            }

            if (calendarVO.getStudents() != null) {
                List<PersonVO> students = calendarVO.getStudents();
                for (PersonVO student : students) {
                    rule = new AclRule();
                    scope = new AclRule.Scope();
                    scope.setType("user").setValue(student.getEmail());
                    rule.setScope(scope).setRole(CALENDAR_ROLE_STUDENT);
                    calendarService.acl().insert(addedCalendar.getId(), rule).queue(batch, callback);
                }
            }

            batch.execute();
            calendarVO.setId(addedCalendar.getId());
            return calendarVO;

        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    @Override
    public void removeCalendar(@Valid CalendarVO calendarVO) {
        try {
            calendarService.calendars().delete(calendarVO.getId()).execute();
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    @Override
    public CalendarVO getCalendarById(@NotNull String calendarId) {
        try {
            CalendarListEntry calendarListEntry = calendarService.calendarList().get(calendarId).execute();
            CalendarVO calendarVO = new CalendarVO();
            calendarVO.setId(calendarId);
            calendarVO.setTitle(calendarListEntry.getSummary());
            List<PersonVO> teachersList = new ArrayList<>();
            List<PersonVO> studentsList = new ArrayList<>();
            List<AclRule> aclRules = calendarService.acl().list(calendarId).execute().getItems();
            if (aclRules != null) {
                for (AclRule aclRule : aclRules) {
                    if (aclRule.getRole() == CALENDAR_ROLE_TEACHER) {
                        teachersList.add(personService.getByEmail(aclRule.getScope().getValue()));
                    } else if (aclRule.getRole() == CALENDAR_ROLE_STUDENT) {
                        studentsList.add(personService.getByEmail(aclRule.getScope().getValue()));
                    }
                }
            }
            calendarVO.setTeachers(teachersList);
            calendarVO.setStudents(studentsList);
            return calendarVO;
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    @Override
    public void addTeacherToCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO teacher) {
        addPersonToCalendar(calendarVO.getId(), teacher.getEmail(), CALENDAR_ROLE_TEACHER);
    }

    @Override
    public void removeTeacherFromCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO teacher) {
        removePersonFromCalendar(calendarVO.getId(), teacher.getEmail(), CALENDAR_ROLE_TEACHER);
    }

    @Override
    public void addStudentToCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO student) {
        addPersonToCalendar(calendarVO.getId(), student.getEmail(), CALENDAR_ROLE_STUDENT);
    }

    @Override
    public void removeStudentFromCalendar(@Valid CalendarVO calendarVO, @Valid PersonVO student) {
        removePersonFromCalendar(calendarVO.getId(), student.getEmail(), CALENDAR_ROLE_STUDENT);
    }


    public List<String> getListOfCalendarsIds() {
        try {
            CalendarList feed = calendarService.calendarList().list().execute();
            List<String> calendarsIds = new ArrayList<>();
            if (feed.getItems() != null) {
                for (CalendarListEntry entry : feed.getItems()) {
                    calendarsIds.add(entry.getId());
                }
            }
            return calendarsIds;
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }


    public List<String> getListOfCalendarsSummaries() {
        try {
            CalendarList feed = calendarService.calendarList().list().execute();
            List<String> calendarsSummaries = new ArrayList<>();
            if (feed.getItems() != null) {
                for (CalendarListEntry entry : feed.getItems()) {
                    calendarsSummaries.add(entry.getSummary());
                }
            }
            return calendarsSummaries;
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    public List<String> getListOfCalendarSubscribers(@NotNull String calendarId) {
        try {
            List<AclRule> aclRules = calendarService.acl().list(calendarId).execute().getItems();
            List<String> emailsList = new ArrayList<>();
            if (aclRules != null) {
                for (AclRule aclRule : aclRules) {
                    emailsList.add(aclRule.getScope().getValue());
                }
            }
            return emailsList;
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    private void removePersonFromCalendar(String calendarId, String email, String role) {
        try {
            String deletedRuleId = getRuleIdForPerson(calendarId, email, role);
            if (deletedRuleId == null) {
                throw new CalendarRoleNotExistsException(String.format("%s with email %s is not exist.", role, email));
            }
            calendarService.acl().delete(calendarId, deletedRuleId).execute();
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    private void addPersonToCalendar(String calendarId, String email, String role) {
        try {
            String ruleId = getRuleIdForPerson(calendarId, email, role);
            if (ruleId != null) {
                throw new CalendarRoleAlreadyExistsException(String.format("%s with email %s is already exist.", role, email));
            }

            AclRule rule = new AclRule();
            AclRule.Scope scope = new AclRule.Scope();
            scope.setType("user").setValue(email);
            rule.setScope(scope).setRole(role);
            calendarService.acl().insert(calendarId, rule).execute();
        } catch (IOException e) {
            throw new CalendarException(e);
        }
    }

    private String getRuleIdForPerson(String calendarId, String email, String role) throws IOException {
        List<AclRule> aclRules = calendarService.acl().list(calendarId).execute().getItems();
        String ruleId = null;
        if (aclRules != null) {
            for (AclRule aclRule : aclRules) {
                if (aclRule.getScope().getValue().equalsIgnoreCase(email) && aclRule.getRole().equalsIgnoreCase(role)) {
                    ruleId = aclRule.getId();
                }
            }
        }
        return ruleId;
    }

}
