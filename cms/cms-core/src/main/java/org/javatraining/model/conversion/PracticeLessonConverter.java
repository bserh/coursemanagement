package org.javatraining.model.conversion;

import org.javatraining.entity.PracticeLessonEntity;
import org.javatraining.model.PracticeLessonVO;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by olga on 18.06.15.
 */
public class PracticeLessonConverter {
    public static PracticeLessonVO convertEntityToVO(PracticeLessonEntity entity) {
        PracticeLessonVO practiceLessonVO = new PracticeLessonVO(entity.getId(), entity.getTask(), entity.getOrderNum());
        practiceLessonVO.setLessonOrderNum(entity.getLesson().getOrderNum());
        return practiceLessonVO;
    }

    public static PracticeLessonEntity convertVOToEntity(PracticeLessonVO lessonVO) {
        PracticeLessonEntity lessonEntity = new PracticeLessonEntity(lessonVO.getId(), lessonVO.getTask(), lessonVO.getOrderNum());
        return lessonEntity;
    }

    public static List<PracticeLessonVO> convertEntitiesToVOs(Collection<PracticeLessonEntity> entities) {
        return entities.stream().map(PracticeLessonConverter::convertEntityToVO).collect(Collectors.toList());
    }
}
