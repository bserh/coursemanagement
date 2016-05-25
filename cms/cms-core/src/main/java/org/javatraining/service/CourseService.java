package org.javatraining.service;

import org.javatraining.entity.enums.PersonRole;
import org.javatraining.model.*;

import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * Created by olga on 07.06.15.
 */
public interface CourseService {

    //  Course methods

    CourseVO saveCourse(@NotNull @Valid CourseVO courseVO);

    CourseVO updateCourse(@NotNull @Valid CourseVO courseVO);

    CourseVO removeCourse(@NotNull CourseVO courseVO);

    void save(@NotNull @Valid CourseWithDetailsVO courseVO);

    void createFromPrototype(@NotNull @Valid CourseWithDetailsVO courseWithDetailsVO, @NotNull CourseVO coursePrototype);

    @NotNull
    @Valid
    List<CourseVO> getAllStartedAfterDate(Date date);

    @NotNull
    @Valid
    List<CourseVO> getAllEndedBeforeDate(Date date);

    @NotNull
    @Valid
    List<CourseVO> getAll();

    void clear();

    @Valid
    CourseVO getCourseById(@NotNull Long id);

    //  Course --- Person methods

    CourseVO addPersonsToCourse(@NotNull CourseVO courseVO, @NotNull @Valid List<PersonVO> persons);

    CourseVO removePersonsFromCourse(@NotNull CourseVO courseVO, @NotNull @Valid List<PersonVO> persons);


    @NotNull
    @Valid
    List<PersonVO> getAllPersonsFromCourseByRole(@NotNull CourseVO courseVO, @NotNull PersonRole role);

    @NotNull
    @Valid
    List<PersonVO> getAllStudentsWithMarksFromCourse(@NotNull CourseVO courseVO);

    @NotNull
    List<CoursePersonStatusVO> getSubscribersWithStatusesForCourse(@NotNull CourseVO courseVO);

    //  Course --- News methods; News methods

    NewsVO addNewsToCourse(@NotNull CourseVO courseVO, @NotNull @Valid NewsVO newsVO);

    NewsVO removeNewsFromCourse(@NotNull CourseVO courseVO, @NotNull NewsVO newsVO);

    NewsVO updateNews(@NotNull @Valid NewsVO newsVO);

    NewsVO getNewsById(@NotNull Long id);

    NewsVO removeNewsById(@NotNull Long newsId);

    NewsVO removeNews(@NotNull NewsVO newsVO);


    @NotNull
    @Valid
    List<NewsVO> getAllNewsFromCourse(@NotNull CourseVO courseVO);

    @NotNull
    @Valid
    List<NewsVO> getAllPersonsNews(@NotNull Long personId);

    @NotNull
    @Valid
    List<NewsVO> getAllNews();

    @Nullable
    @Valid
    List<NewsVO> getNewsByCourseId(@NotNull Long courseId);


    @Nullable
    @Valid
    NewsVO getNewsByIdFromCourse(@NotNull Long courseId, @NotNull Long newsId);


}
