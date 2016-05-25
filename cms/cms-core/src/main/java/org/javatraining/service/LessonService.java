package org.javatraining.service;

import org.javatraining.model.LessonVO;
import org.javatraining.model.LessonWithDetailsVO;
import org.javatraining.model.PracticeLessonVO;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * Created by asudak on 6/24/15.
 */
public interface LessonService {
    void save(@NotNull @Valid LessonVO lessonVO);

    LessonVO update(@NotNull @Valid LessonVO lessonVO);

    void remove(@NotNull LessonVO lessonVO);

    void save(@NotNull @Valid LessonWithDetailsVO lessonVO);

    @Valid
    @Nullable
    LessonVO getById(@NotNull Long id);

    @Nullable
    @Valid
    Set<LessonVO> getByCourseId(@NotNull Long courseId);

    @Nullable
    @Valid
    List<PracticeLessonVO> getPracticesByCourseId(@NotNull Long courseId);

    @Nullable
    @Valid
    Set<LessonWithDetailsVO> getWithPracticesByCourseId(@NotNull Long courseId);

    @Nullable
    @Valid
    LessonVO getByOrderNum(@NotNull Long courseId, @NotNull Long orderNum);

    @Nullable
    @Valid
    LessonWithDetailsVO getByOrderNum(@NotNull Long courseId, @NotNull Long orderNum, boolean withDetails);

    @NotNull
    @Valid
    LessonVO updateByOrderNum(@NotNull Long courseId, @NotNull Long orderNum, LessonVO lesson);

    @NotNull
    @Valid
    LessonWithDetailsVO updateByOrderNum(@NotNull LessonWithDetailsVO lesson, List<Long> removedLinkIds, List<Long> removedPracticeIds);

    void deleteByOrderNum(@NotNull @Valid Long courseId, @NotNull @Valid Long orderNum);
}
