package org.javatraining.model.conversion;

import org.javatraining.entity.CourseEntity;
import org.javatraining.model.CourseVO;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by olga on 10.06.15.
 */
public class CourseConverter {

    public static CourseVO convertEntityToVO(CourseEntity courseEntity) {
        CourseVO courseVO = new CourseVO(courseEntity.getId(), courseEntity.getName(), courseEntity.getDescription());
        courseVO.setStartDate(courseEntity.getStartdate());
        courseVO.setEndDate(courseEntity.getEnddate());
        courseVO.setCalendarId(courseEntity.getCalendar_id());
        return courseVO;
    }

    public static CourseEntity convertVOToEntity(@NotNull CourseVO courseVO) {
        CourseEntity courseEntity = new CourseEntity();
        if (courseVO.getId() != null) {
            courseEntity.setId(courseVO.getId());
        }
        courseEntity.setName(courseVO.getName());
        if (courseVO.getDescription() != null) {
            courseEntity.setDescription(courseVO.getDescription());
        }
        if (courseVO.getStartDate() != null) {
            courseEntity.setStartDate(new Date(courseVO.getStartDate().getTime()));
        }
        if (courseVO.getEndDate() != null) {
            courseEntity.setEndDate(new Date(courseVO.getEndDate().getTime()));
        }
        courseEntity.setCalendar_id(courseVO.getCalendarId());
        return courseEntity;
    }

    public static Set<CourseVO> convertEntitiesToVOs(@NotNull Collection<CourseEntity> courseEntities) {
        Set<CourseVO> courses = new HashSet<>(courseEntities.size());
        for (CourseEntity courseEntity : courseEntities) {
            courses.add(convertEntityToVO(courseEntity));
        }
        return courses;
    }

    public static Set<CourseEntity> convertVOsToEntities(@NotNull Collection<CourseVO> courseVOs) {
        Set<CourseEntity> courseEntities = new HashSet<>(courseVOs.size());
        for (CourseVO courseVO : courseVOs) {
            courseEntities.add(convertVOToEntity(courseVO));
        }
        return courseEntities;
    }

    private CourseConverter() {
    }
}
