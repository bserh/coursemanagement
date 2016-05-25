package org.javatraining.service.impl;

import org.javatraining.dao.LessonDAO;
import org.javatraining.dao.LessonLinkDAO;
import org.javatraining.entity.LessonLinkEntity;
import org.javatraining.model.LessonLinkVO;
import org.javatraining.service.LessonLinkService;

import javax.annotation.Nullable;
import javax.ejb.EJB;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

import static org.javatraining.model.conversion.LessonLinkConverter.*;

/**
 * Created by asudak on 7/1/15.
 */
public class LessonLinkServiceImpl implements LessonLinkService {
    @EJB
    private LessonDAO lessonDAO;
    @EJB
    private LessonLinkDAO lessonLinkDAO;

    @Override
    public void save(@NotNull @Valid LessonLinkVO lessonLinkVO) {
        LessonLinkEntity lessonLinkEntity = convertVOToEntity(lessonLinkVO);
        lessonLinkDAO.save(lessonLinkEntity);
        lessonLinkVO.setId(lessonLinkEntity.getId());
    }

    @Override
    public LessonLinkVO update(@NotNull @Valid LessonLinkVO lessonLinkVO) {
        return convertEntityToVO(lessonLinkDAO.update(convertVOToEntity(lessonLinkVO)));
    }

    @Override
    public void remove(@NotNull LessonLinkVO lessonLinkVO) {
        lessonLinkDAO.remove(convertVOToEntity(lessonLinkVO));
    }

    @Nullable
    @Override
    public LessonLinkVO getById(@NotNull Long id) {
        return convertEntityToVO(lessonLinkDAO.getById(id));
    }

    @Nullable
    @Override
    public Set<LessonLinkVO> getByLessonId(@NotNull Long courseId, @NotNull Long lessonOrderNum) {
        return convertEntitiesToVOs(lessonLinkDAO.getAllLessonLinksByLesson(lessonDAO.getByOrderNum(courseId, lessonOrderNum)));
    }

    @Nullable
    @Override
    public LessonLinkVO getByOrderNum(@NotNull Long courseId, @NotNull Long lessonOrderNum, @NotNull Long orderNum) {
        return convertEntityToVO(lessonLinkDAO.getByOrderNum(lessonDAO.getByOrderNum(courseId, lessonOrderNum), orderNum));
    }

    @Nullable
    @Override
    public LessonLinkVO updateByOrderNum(@NotNull Long courseId, @NotNull Long lessonOrderNum, @NotNull Long orderNum, LessonLinkVO lessonLink) {
        return convertEntityToVO(lessonLinkDAO.updateByOrderNum(lessonDAO.getByOrderNum(courseId, lessonOrderNum), orderNum, convertVOToEntity(lessonLink)));
    }

    @Override
    public void deleteByOrderNum(@NotNull @Valid Long courseId, @NotNull @Valid Long lessonOrderNum, @NotNull @Valid Long orderNum) {
        lessonLinkDAO.deleteByOrderNum(lessonDAO.getByOrderNum(courseId, lessonOrderNum), orderNum);
    }
}
