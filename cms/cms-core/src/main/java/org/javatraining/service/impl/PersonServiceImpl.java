package org.javatraining.service.impl;

import org.javatraining.dao.*;
import org.javatraining.dao.exception.EntityNotExistException;
import org.javatraining.entity.*;
import org.javatraining.entity.enums.CourseStatus;
import org.javatraining.entity.enums.PersonRole;
import org.javatraining.entity.util.Pair;
import org.javatraining.model.*;
import org.javatraining.model.conversion.CourseWithStatusConverter;
import org.javatraining.model.conversion.MarkConverter;
import org.javatraining.model.conversion.PersonConverter;
import org.javatraining.service.PersonService;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by olga on 07.06.15.
 */
@Stateless
public class PersonServiceImpl implements PersonService {
    @EJB
    private PersonDAO personDAO;
    @EJB
    private CourseDAO courseDAO;
    @EJB
    private MarkDAO markDAO;
    @EJB
    private PracticeLessonDAO practiceLessonDAO;

    @Override
    public boolean savePersonDescription(@NotNull PersonDescriptionVO personDescriptionVO) {
        //FIXME add implementation after creating specified repository
        return true;
    }

    @Nullable
    @Override
    public PersonDescriptionVO getPersonDescription(@NotNull Long id) {
        //FIXME add implementation after creating specified repository
        return null;
    }

    @Override
    public PersonDescriptionVO getPersonPhone(@NotNull Long personId) {
        PersonEntity personEntity = personDAO.getById(personId);
        if (personEntity == null) {
            return null;
        }
        PersonDescriptionVO personDescriptionVO = new PersonDescriptionVO();
        personDescriptionVO.setPhoneNumber(personEntity.getPhone());
        return personDescriptionVO;
    }

    @Nullable
    @Override
    public PersonDescriptionVO updatePersonPhone(@NotNull PersonDescriptionVO personDescriptionVO) {
        if (personDescriptionVO.getId() == null) {
            return null;
        }
        PersonEntity personEntity = personDAO.getById(personDescriptionVO.getId());
        if (personEntity == null) {
            return null;
        }
        personEntity.setPhone(personDescriptionVO.getPhoneNumber());
        PersonEntity updatedPersonEntity = personDAO.update(personEntity);
        // FIXME: do conversation
        return personDescriptionVO;
    }

    @Override
    public PersonDescriptionVO updatePersonDescription(@NotNull PersonDescriptionVO personDescriptionVO) {
        //FIXME add implementation after creating specified repository
        return null;
    }

    @Override
    public void removePersonDescription(Long id) {
        //FIXME add implementation after creating specified repository
    }

    @EJB
    private CoursePersonStatusDAO coursePersonStatusDAO;

    @Override
    public void saveStudent(@NotNull @Valid PersonVO personVO) {
        personVO.setPersonRole(PersonRole.STUDENT);
        PersonEntity entity = PersonConverter.convertVOToEntity(personVO);
        PersonEntity savedEntity = personDAO.save(entity);
        personVO.setId(savedEntity.getId());
    }

    @Override
    public void save(@NotNull @Valid PersonVO personVO) {
        PersonVO personVOFromEmail = getByEmail(personVO.getEmail());
        if (personVOFromEmail != null) {
            updatePersonVO(personVO, personVOFromEmail);
        } else {
            saveStudent(personVO);
        }
    }

    @Override
    public PersonVO update(@NotNull @Valid PersonVO personVO) {
        PersonEntity entity = PersonConverter.convertVOToEntity(personVO);
        PersonEntity updatedEntity = personDAO.update(entity);
        return PersonConverter.convertEntityToVO(updatedEntity);
    }

    @Override
    public void remove(@NotNull PersonVO personVO) {
        personDAO.removeById(personVO.getId());
    }

    @Override
    public PersonVO getById(@NotNull Long id) {
        try {
            PersonEntity personEntity = personDAO.getById(id);
            return PersonConverter.convertEntityToVO(personEntity);
        } catch (EntityNotExistException e) {
            return null;
        }
    }

    @Override
    public PersonVO getByEmail(@NotNull String email) {
        try {
            PersonEntity personEntity = personDAO.getByEmail(email);
            return PersonConverter.convertEntityToVO(personEntity);
        } catch (EntityNotExistException e) {
            return null;
        }
    }

    @Override
    public List<PersonVO> getPersonsByRole(@NotNull @Valid PersonRole role) {
        List<PersonEntity> personEntities = personDAO.getByPersonRole(role);
        return new ArrayList<>(PersonConverter.convertEntitiesToVOs(personEntities));
    }

    @Override
    public void addPersonRequestForCourse(@NotNull PersonVO personVO, @NotNull CourseVO courseVO) {
        CoursePersonStatusEntity entity = new CoursePersonStatusEntity(CourseStatus.REQUESTED);
        PersonEntity personEntity = personDAO.getById(personVO.getId());
        entity.setPerson(personEntity);
        CourseEntity courseEntity = courseDAO.getById(courseVO.getId());
        entity.setCourse(courseEntity);
        coursePersonStatusDAO.save(entity);
    }

    @Override
    public void removePersonRequestForCourse(@NotNull PersonVO personVO, @NotNull CourseVO courseVO) {
        CoursePersonStatusEntity existingStatus = coursePersonStatusDAO.getStatusByPersonIdAndCourseId(personVO.getId(), courseVO.getId());
        if (existingStatus != null) {
            coursePersonStatusDAO.removeById(existingStatus.getId());
        }
    }

    @Override
    public void approvePersonRequestForCourse(@NotNull CoursePersonStatusVO coursePersonStatusVO) {
        CoursePersonStatusEntity entity = coursePersonStatusDAO.getById(coursePersonStatusVO.getId());
        entity.setCourseStatus(CourseStatus.SIGNED);
        coursePersonStatusDAO.update(entity);
        coursePersonStatusVO.setCourseStatus(CourseStatus.SIGNED);
    }

    @Override
    public void rejectPersonRequestForCourse(@NotNull CoursePersonStatusVO coursePersonStatusVO) {
        CoursePersonStatusEntity entity = coursePersonStatusDAO.getById(coursePersonStatusVO.getId());
        entity.setCourseStatus(CourseStatus.UNSIGNED);
        coursePersonStatusDAO.update(entity);
        coursePersonStatusVO.setCourseStatus(CourseStatus.UNSIGNED);
    }

    @Override
    public void updatePersonStatusOnCourse(@NotNull CoursePersonStatusVO coursePersonStatusVO) {
        CoursePersonStatusEntity entity = coursePersonStatusDAO.getById(coursePersonStatusVO.getId());
        entity.setCourseStatus(coursePersonStatusVO.getCourseStatus());
        coursePersonStatusDAO.update(entity);
    }

    @Override
    public List<CourseWithStatusVO> getPersonCoursesWithStatuses(@NotNull PersonVO personVO) {
        List<Pair<CourseEntity, CourseStatus>> entitiesCoursesWithStatuses = courseDAO.getAllCoursesWithStatusesForPerson(personVO.getId());
        List<CourseWithStatusVO> voCoursesWithStatuses = entitiesCoursesWithStatuses
                .stream()
                .map((pair) -> CourseWithStatusConverter.convertEntityToVO(pair.first, pair.second))
                .collect(Collectors.toList());
        return voCoursesWithStatuses;
    }

    @Override
    public void setMark(@NotNull PersonVO personVO, @NotNull PracticeLessonVO practiceLessonVO, @NotNull @Valid MarkVO markVO) {
        PersonEntity personEntity = personDAO.getById(personVO.getId());
        PracticeLessonEntity lessonEntity = practiceLessonDAO.getById(practiceLessonVO.getId());
        MarkEntity markEntity = MarkConverter.convertVOToEntity(markVO);
        markDAO.save(markEntity, personEntity, lessonEntity);
    }

    @Override
    public void setMarks(@NotNull PersonVO personVO, @NotNull @Valid List<MarkVO> marksVO) {
        PersonEntity personEntity = personDAO.getById(personVO.getId());
        List<MarkVO> marksVOAfterSet = marksVO.stream()
                .map(markVO -> {
                    MarkEntity markEntity = MarkConverter.convertVOToEntity(markVO);
                    markEntity.setPersons(personEntity);
                    markEntity.setPracticeLesson(practiceLessonDAO.getById(markVO.getLessonId()));
                    return markEntity;
                })
                .map(markEntity -> markEntity.getId() == null ? markDAO.save(markEntity) : markDAO.update(markEntity))
                .map(MarkConverter::convertEntityToVO)
                .collect(Collectors.toList());
        marksVO.clear();
        marksVO.addAll(marksVOAfterSet);
    }

    @Override
    public void removeMark(@NotNull MarkVO markVO) {
        markDAO.removeById(markVO.getId());
    }

    @Override
    public List<MarkVO> getMarks(@NotNull PersonVO personVO, @NotNull CourseVO courseVO) {
        PersonEntity personEntity = personDAO.getById(personVO.getId());
        Set<MarkEntity> marks = personEntity.getMarks().stream()
                .filter(markEntity -> markEntity.getLessons().getLesson().getCourse().getId() == courseVO.getId())
                .collect(Collectors.toSet());
        return MarkConverter.convertEntitiesToVOs(marks);
    }

    private void updatePersonVO(PersonVO personToUpdate, PersonVO personWithUpdateInfo) {
        personToUpdate.setId(personWithUpdateInfo.getId());
        personToUpdate.setEmail(personWithUpdateInfo.getEmail());
        personToUpdate.setPersonRole(personWithUpdateInfo.getPersonRole());
        personToUpdate.setName(personWithUpdateInfo.getName());
        personToUpdate.setSecondName(personWithUpdateInfo.getSecondName());
        personToUpdate.setLastName(personWithUpdateInfo.getLastName());
    }
}
