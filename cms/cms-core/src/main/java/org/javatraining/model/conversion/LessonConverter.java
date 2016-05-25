package org.javatraining.model.conversion;

import org.javatraining.entity.CourseEntity;
import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.LessonLinkEntity;
import org.javatraining.entity.PracticeLessonEntity;
import org.javatraining.model.LessonLinkVO;
import org.javatraining.model.LessonVO;
import org.javatraining.model.LessonWithDetailsVO;
import org.javatraining.model.PracticeLessonVO;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.*;

/**
 * Created by asudak on 6/24/15.
 */
public class LessonConverter {
    public static LessonVO convertEntityToVO(LessonEntity lessonEntity) {
        LessonVO lessonVO = new LessonVO();
        fillLessonVOFromEntity(lessonVO, lessonEntity);
        return lessonVO;
    }

    public static LessonWithDetailsVO convertEntitiesToVOWithDetails(@NotNull LessonEntity lessonEntity,
                                                                     @Nullable Collection<LessonLinkEntity> linkEntities,
                                                                     @Nullable Collection<PracticeLessonEntity> practiceEntities) {
        LessonWithDetailsVO lessonWithDetailsVO = new LessonWithDetailsVO();
        fillLessonVOFromEntity(lessonWithDetailsVO, lessonEntity);
        if (linkEntities != null) {
            Set<LessonLinkVO> lessonLinkVOs = LessonLinkConverter.convertEntitiesToVOs(linkEntities);
            lessonWithDetailsVO.setLinks(new ArrayList<>(lessonLinkVOs));
        }
        if (practiceEntities != null) {
            List<PracticeLessonVO> practiceLessonVOs = PracticeLessonConverter.convertEntitiesToVOs(practiceEntities);
            lessonWithDetailsVO.setPractices(practiceLessonVOs);
        }
        return lessonWithDetailsVO;
    }

    public static LessonEntity convertVOToEntity(LessonVO lessonVO) {
        LessonEntity lessonEntity = new LessonEntity();
        lessonEntity.setId(lessonVO.getId());
        lessonEntity.setCreateDate(lessonVO.getCreateDate());
        lessonEntity.setDescription(lessonVO.getContent());
        lessonEntity.setTopic(lessonVO.getTopic());
        lessonEntity.setOrderNum(lessonVO.getOrderNum());
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setId(lessonVO.getCourseId());
        lessonEntity.setCourse(courseEntity);
        return lessonEntity;
    }

    public static Set<LessonVO> convertEntitiesToVOs(@NotNull Collection<LessonEntity> lessonEntities) {
        Set<LessonVO> lessonVOs = new HashSet<>();
        for (LessonEntity lessonEntity : lessonEntities) {
            lessonVOs.add(convertEntityToVO(lessonEntity));
        }
        return lessonVOs;
    }

    public static Set<LessonEntity> convertVOsToEntities(@NotNull Collection<LessonVO> lessonVOs) {
        Set<LessonEntity> lessonEntities = new HashSet<>();
        for (LessonVO LessonVO : lessonVOs) {
            lessonEntities.add(convertVOToEntity(LessonVO));
        }
        return lessonEntities;
    }

    private static void fillLessonVOFromEntity(LessonVO lessonVO, LessonEntity lessonEntity) {
        lessonVO.setId(lessonEntity.getId());
        lessonVO.setCreateDate(lessonEntity.getCreateDate());
        lessonVO.setContent(lessonEntity.getDescription());
        lessonVO.setTopic(lessonEntity.getTopic());
        lessonVO.setOrderNum(lessonEntity.getOrderNum());
        lessonVO.setCourseId(lessonEntity.getCourse().getId());
    }
}
