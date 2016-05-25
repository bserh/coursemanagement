package org.javatraining.model.conversion;

import org.javatraining.entity.CourseEntity;
import org.javatraining.entity.NewsEntity;
import org.javatraining.model.NewsVO;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by vika on 16.06.15.
 */
public class NewsConverter {
    public static NewsVO convertEntityToVO(NewsEntity newsEntity) {
        NewsVO newsVO = new NewsVO(newsEntity.getId(), newsEntity.getTitle(), newsEntity.getContent(), newsEntity.getDate());
        newsVO.setCourseId(newsEntity.getCourses().getId());
        return newsVO;
    }

    public static NewsEntity convertVOToEntity(@NotNull NewsVO newsVO) {
        NewsEntity newsEntity = new NewsEntity();
        if (newsVO.getId() != null) {
            newsEntity.setId(newsVO.getId());
        }
        if (newsVO.getContent() != null) {
            newsEntity.setDescription(newsVO.getContent());
        }
        if (newsVO.getDate() != null) {
            newsEntity.setDate((newsVO.getDate()));
        }
        if (newsVO.getTitle() != null) {
            newsEntity.setTitle(newsVO.getTitle());
        }
        CourseEntity courseEntity = new CourseEntity();
        courseEntity.setId(newsVO.getCourseId());
        newsEntity.setCourse(courseEntity);

        return newsEntity;
    }

    public static Set<NewsVO> convertEntitiesToVOs(@NotNull Collection<NewsEntity> newsEntities) {
        Set<NewsVO> news = new HashSet<>(newsEntities.size());
        for (NewsEntity newsEntity : newsEntities) {
            news.add(convertEntityToVO(newsEntity));
        }
        return news;
    }

    public static Set<NewsEntity> convertVOsToEntities(@NotNull Collection<NewsVO> newsVOs) {
        Set<NewsEntity> newsEntities = new HashSet<>(newsVOs.size());
        for (NewsVO newsVO : newsVOs) {
            newsEntities.add(convertVOToEntity(newsVO));
        }
        return newsEntities;
    }
}
