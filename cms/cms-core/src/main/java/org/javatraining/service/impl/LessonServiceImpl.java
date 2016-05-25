package org.javatraining.service.impl;

import org.javatraining.dao.LessonDAO;
import org.javatraining.dao.LessonLinkDAO;
import org.javatraining.dao.NewsDAO;
import org.javatraining.dao.PracticeLessonDAO;
import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.LessonLinkEntity;
import org.javatraining.entity.NewsEntity;
import org.javatraining.entity.PracticeLessonEntity;
import org.javatraining.entity.util.Pair;
import org.javatraining.model.LessonLinkVO;
import org.javatraining.model.LessonVO;
import org.javatraining.model.LessonWithDetailsVO;
import org.javatraining.model.PracticeLessonVO;
import org.javatraining.model.conversion.LessonConverter;
import org.javatraining.model.conversion.LessonLinkConverter;
import org.javatraining.model.conversion.PracticeLessonConverter;
import org.javatraining.service.LessonService;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.javatraining.model.conversion.LessonConverter.*;

/**
 * Created by asudak on 6/24/15.
 */
@Stateless
public class LessonServiceImpl implements LessonService {

    @EJB
    private NewsDAO newsDAO;
    @EJB
    private LessonDAO lessonDAO;
    @EJB
    private LessonLinkDAO lessonLinkDAO;
    @EJB
    private PracticeLessonDAO practiceLessonDAO;

    @Override
    public void save(@NotNull @Valid LessonVO lessonVO) {
        LessonEntity lessonEntity = convertVOToEntity(lessonVO);
        lessonDAO.save(lessonEntity);
        lessonVO.setId(lessonEntity.getId());
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.setTitle("К курсу" + lessonEntity.getCourse().getName()
                + " была добавлена лекция");
        newsEntity.setDescription(lessonEntity.getDescription());
        newsEntity.setDate((Timestamp) lessonEntity.getCreateDate());
        newsEntity.setCourse(lessonEntity.getCourse());
        newsDAO.save(newsEntity);
    }

    @Override
    public LessonVO update(@NotNull @Valid LessonVO lessonVO) {
        return convertEntityToVO(lessonDAO.update(convertVOToEntity(lessonVO)));
    }

    @Override
    public void remove(@NotNull LessonVO lessonVO) {
        lessonDAO.remove(convertVOToEntity(lessonVO));
    }

    @Override
    public void save(@NotNull @Valid LessonWithDetailsVO lessonVO) {
        LessonEntity lessonEntity = convertVOToEntity(lessonVO);
        lessonDAO.save(lessonEntity);
        lessonVO.setId(lessonEntity.getId());
        for (LessonLinkVO linkVO : lessonVO.getLinks()) {
            linkVO.setLessonId(lessonVO.getId());
            LessonLinkEntity linkEntity = LessonLinkConverter.convertVOToEntity(linkVO);
            lessonLinkDAO.save(linkEntity);
            linkVO.setId(lessonEntity.getId());
        }
        for (PracticeLessonVO practiceVO : lessonVO.getPractices()) {
            PracticeLessonEntity practiceEntity = PracticeLessonConverter.convertVOToEntity(practiceVO);
            practiceEntity.setLesson(lessonEntity);
            practiceLessonDAO.save(practiceEntity);
            practiceVO.setId(practiceEntity.getId());
        }
    }

    @Nullable
    @Override
    public LessonVO getById(@NotNull Long id) {
        return convertEntityToVO(lessonDAO.getById(id));
    }

    @Nullable
    @Override
    public Set<LessonVO> getByCourseId(@NotNull Long courseId) {
        return convertEntitiesToVOs(lessonDAO.getByCourseId(courseId));
    }

    @Nullable
    @Override
    public List<PracticeLessonVO> getPracticesByCourseId(@NotNull Long courseId) {
        return PracticeLessonConverter.convertEntitiesToVOs(practiceLessonDAO.getPracticesForCourse(courseId));
    }

    @Nullable
    @Override
    public Set<LessonWithDetailsVO> getWithPracticesByCourseId(@NotNull Long courseId) {
        List<Pair<LessonEntity, List<PracticeLessonEntity>>> lessonsWithPractices = lessonDAO.getWithPracticesByCourseId(courseId);
        return lessonsWithPractices.stream()
                .map((pair) -> LessonConverter.convertEntitiesToVOWithDetails(pair.first, null, pair.second))
                .collect(Collectors.toSet());
    }

    @Nullable
    @Override
    public LessonVO getByOrderNum(@NotNull Long courseId, @NotNull Long orderNum) {
        return convertEntityToVO(lessonDAO.getByOrderNum(courseId, orderNum));
    }

    @Nullable
    @Override
    public LessonWithDetailsVO getByOrderNum(@NotNull Long courseId, @NotNull Long orderNum, boolean withDetails) {
        if (!withDetails) {
            return (LessonWithDetailsVO) getByOrderNum(courseId, orderNum);
        }
        LessonEntity lessonEntity = lessonDAO.getByOrderNum(courseId, orderNum);
        List<LessonLinkEntity> lessonLinksForLesson = lessonLinkDAO.getAllLessonLinksByLesson(lessonEntity);
        List<PracticeLessonEntity> practicesForLesson = practiceLessonDAO.getPracticesForLesson(lessonEntity);
        LessonWithDetailsVO lessonWithDetailsVO =
                LessonConverter.convertEntitiesToVOWithDetails(lessonEntity, lessonLinksForLesson, practicesForLesson);
        return lessonWithDetailsVO;
    }

    @NotNull
    @Override
    public LessonVO updateByOrderNum(@NotNull Long courseId, @NotNull Long orderNum, LessonVO lesson) {
        return convertEntityToVO(lessonDAO.updateByOrderNum(courseId, orderNum, convertVOToEntity(lesson)));
    }

    @NotNull
    @Override
    public LessonWithDetailsVO updateByOrderNum(@NotNull LessonWithDetailsVO lesson, List<Long> removedLinkIds, List<Long> removedPracticeIds) {
        LessonEntity lessonEntity = convertVOToEntity(lesson);
        LessonEntity updatedLessonEntity = lessonDAO.updateByOrderNum(lesson.getCourseId(), lesson.getOrderNum(), lessonEntity);
        removedLinkIds.stream().forEach(lessonLinkDAO::removeById);
        List<LessonLinkEntity> lessonLinkEntities = lesson.getLinks().stream()
                .map(LessonLinkConverter::convertVOToEntity)
                .peek(entity -> entity.setLesson(updatedLessonEntity))
                .map(entity -> entity.getId() == null ? lessonLinkDAO.save(entity) : lessonLinkDAO.update(entity))
                .collect(Collectors.toList());
        removedPracticeIds.stream().forEach(practiceLessonDAO::removeById);
        List<PracticeLessonEntity> practiceLessonEntities = lesson.getPractices().stream()
                .map(PracticeLessonConverter::convertVOToEntity)
                .peek(entity -> entity.setLesson(updatedLessonEntity))
                .map(entity -> entity.getId() == null ? practiceLessonDAO.save(entity) : practiceLessonDAO.update(entity))
                .collect(Collectors.toList());
        return convertEntitiesToVOWithDetails(updatedLessonEntity, lessonLinkEntities, practiceLessonEntities);
    }

    @Override
    public void deleteByOrderNum(@NotNull @Valid Long courseId, @NotNull @Valid Long orderNum) {
        lessonDAO.removeByOrderNum(courseId, orderNum);
    }
}
