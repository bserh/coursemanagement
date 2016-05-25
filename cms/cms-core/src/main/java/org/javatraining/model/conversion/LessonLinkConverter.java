package org.javatraining.model.conversion;

import org.javatraining.entity.LessonEntity;
import org.javatraining.entity.LessonLinkEntity;
import org.javatraining.model.LessonLinkVO;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by asudak on 6/24/15.
 */
public class LessonLinkConverter {
    public static LessonLinkVO convertEntityToVO(LessonLinkEntity lessonLinkEntity) {
        LessonLinkVO lessonLinkVO = new LessonLinkVO();
        lessonLinkVO.setId(lessonLinkEntity.getId());
        lessonLinkVO.setLink(lessonLinkEntity.getLink());
        lessonLinkVO.setDescription(lessonLinkEntity.getDescription());
        lessonLinkVO.setLessonId(lessonLinkEntity.getLesson().getId());
        lessonLinkVO.setOrderNum(lessonLinkEntity.getOrderNum());
        return lessonLinkVO;
    }

    public static LessonLinkEntity convertVOToEntity(LessonLinkVO lessonLinkVO) {
        LessonLinkEntity lessonLinkEntity = new LessonLinkEntity();
        lessonLinkEntity.setId(lessonLinkVO.getId());
        lessonLinkEntity.setLink(lessonLinkVO.getLink());
        lessonLinkEntity.setDescription(lessonLinkVO.getDescription());
        lessonLinkEntity.setOrderNum(lessonLinkVO.getOrderNum());
        LessonEntity lesson = new LessonEntity();
        lesson.setId(lessonLinkVO.getLessonId());
        lessonLinkEntity.setLesson(lesson);
        return lessonLinkEntity;
    }

    public static Set<LessonLinkVO> convertEntitiesToVOs(@NotNull Collection<LessonLinkEntity> lessonLinkEntities) {
        Set<LessonLinkVO> lessonLinkVOs = new HashSet<>();
        for (LessonLinkEntity lessonLinkEntity : lessonLinkEntities) {
            lessonLinkVOs.add(convertEntityToVO(lessonLinkEntity));
        }
        return lessonLinkVOs;
    }

    public static Set<LessonLinkEntity> convertVOsToEntities(@NotNull Collection<LessonLinkVO> lessonLinkVOs) {
        Set<LessonLinkEntity> lessonEntities = new HashSet<>();
        for (LessonLinkVO LessonLinkVO : lessonLinkVOs) {
            lessonEntities.add(convertVOToEntity(LessonLinkVO));
        }
        return lessonEntities;
    }
}
