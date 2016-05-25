package org.javatraining.model.conversion;

import org.javatraining.entity.CourseEntity;
import org.javatraining.entity.enums.CourseStatus;
import org.javatraining.model.CourseWithStatusVO;

/**
 * Created by olga on 06.07.15.
 */
public class CourseWithStatusConverter {
    public static CourseWithStatusVO convertEntityToVO(CourseEntity courseEntity, CourseStatus courseStatus) {
        CourseWithStatusVO courseVO = new CourseWithStatusVO();
        courseVO.setId(courseEntity.getId());
        courseVO.setName(courseEntity.getName());
        courseVO.setDescription(courseEntity.getDescription());
        courseVO.setStartDate(courseEntity.getStartdate());
        courseVO.setEndDate(courseEntity.getEnddate());
        courseVO.setCourseStatus(courseStatus);
        return courseVO;
    }
}
