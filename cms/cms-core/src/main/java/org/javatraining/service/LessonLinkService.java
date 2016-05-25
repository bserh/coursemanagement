package org.javatraining.service;

import org.javatraining.model.LessonLinkVO;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

/**
 * Created by asudak on 7/1/15.
 */
public interface LessonLinkService {
    void save(@NotNull @Valid LessonLinkVO lessonLinkVO);

    LessonLinkVO update(@NotNull @Valid LessonLinkVO lessonLinkVO);

    void remove(@NotNull LessonLinkVO lessonLinkVO);

    @Valid
    @Nullable
    LessonLinkVO getById(@NotNull Long id);

    @Nullable
    @Valid
    Set<LessonLinkVO> getByLessonId(@NotNull Long courseId, @NotNull Long lessonOrderNum);

    @Nullable
    @Valid
    LessonLinkVO getByOrderNum(@NotNull Long courseId, @NotNull Long lessonOrderNum, @NotNull Long orderNum);

    @Nullable
    @Valid
    LessonLinkVO updateByOrderNum(@NotNull Long courseId, @NotNull Long lessonOrderNum, @NotNull Long orderNum, LessonLinkVO lesson);

    void deleteByOrderNum(@NotNull @Valid Long courseId, @NotNull @Valid Long lessonOrderNum, @NotNull @Valid Long orderNum);
}
