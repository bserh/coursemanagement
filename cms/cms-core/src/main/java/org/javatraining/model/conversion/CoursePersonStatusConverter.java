package org.javatraining.model.conversion;

import org.javatraining.entity.CoursePersonStatusEntity;
import org.javatraining.entity.PersonEntity;
import org.javatraining.model.CoursePersonStatusVO;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by olga on 30.06.15.
 */
public class CoursePersonStatusConverter {
    public static CoursePersonStatusVO convertEntityToVO(@NotNull CoursePersonStatusEntity entity) {
        CoursePersonStatusVO vo = new CoursePersonStatusVO(entity.getCourseStatus(), entity.getCourse().getId(), entity.getPerson().getId());
        return vo;
    }

    public static CoursePersonStatusVO convertEntityToVO(@NotNull PersonEntity personEntity, @NotNull CoursePersonStatusEntity statusEntity) {
        CoursePersonStatusVO vo = new CoursePersonStatusVO(statusEntity.getCourseStatus(), null, personEntity.getId());
        vo.setId(statusEntity.getId());
        vo.setPersonFirstName(personEntity.getName());
        vo.setPersonLastName(personEntity.getLastName());
        return vo;
    }

    public static CoursePersonStatusEntity convertVOToEntityWithoutEntityRelations(@NotNull CoursePersonStatusVO vo) {
        CoursePersonStatusEntity entity = new CoursePersonStatusEntity(vo.getCourseStatus());
        entity.setId(vo.getId());
        return entity;
    }

    public static List<CoursePersonStatusVO> convertEntitiesToVOs(@NotNull @Valid List<CoursePersonStatusEntity> entities) {
        List<CoursePersonStatusVO> vos = new ArrayList<>(entities.size());
        for (CoursePersonStatusEntity entity : entities) {
            vos.add(convertEntityToVO(entity));
        }
        return vos;
    }
}
