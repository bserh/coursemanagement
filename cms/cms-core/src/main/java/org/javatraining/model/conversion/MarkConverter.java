package org.javatraining.model.conversion;

import org.javatraining.entity.MarkEntity;
import org.javatraining.model.MarkVO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by olga on 18.06.15.
 */
public class MarkConverter {
    public static MarkVO convertEntityToVO(MarkEntity markEntity) {
        MarkVO markVO = new MarkVO(markEntity.getId(), (int) ((long) markEntity.getMark()));
        markVO.setLessonId(markEntity.getLessons().getId());
        return markVO;
    }

    public static MarkEntity convertVOToEntity(MarkVO markVO) {
        MarkEntity markEntity = new MarkEntity((long) ((int) markVO.getMark()));
        markEntity.setId(markVO.getId());
        return markEntity;
    }

    public static List<MarkVO> convertEntitiesToVOs(Collection<MarkEntity> markEntities) {
        List<MarkVO> markVOs = new ArrayList<>(markEntities.size());
        for (MarkEntity markEntity : markEntities) {
            markVOs.add(convertEntityToVO(markEntity));
        }
        return markVOs;
    }
}
