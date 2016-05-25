/**
 * Created by olga on 25.06.15.
 */
function CourseContentService(Restangular) {
    var restBase = 'resources/course';

    var createLectureRest = function (courseId) {
        return Restangular.one(restBase, courseId).all('lesson');
    };

    this.getLectures = function (courseId) {
        if (courseId === undefined) {
            return [];
        }
        return createLectureRest(courseId).getList();
    };

    this.createLecture = function (newLecture) {
        return createLectureRest(newLecture.courseId).post(newLecture);
    };

    this.removeLecture = function (courseId, lectureOrderNum) {
        return Restangular.one(restBase, courseId).one('lesson', lectureOrderNum).remove();
    };

    this.updateLecture = function (lecture, removedLinks, removedPractices) {
        return Restangular.one(restBase, lecture.courseId).one('lesson', lecture.orderNum)
            .customPUT(lecture, undefined, {removedLinks: removedLinks, removedPractices: removedPractices}, undefined);
    };

    this.getLecture = function (courseId, lectureOrderNum) {
        return Restangular.one(restBase, courseId).one('lesson', lectureOrderNum).get();
    };

    this.normalizeLecture = function(lecture) {
        return {
            'id': lecture.id,
            'orderNum': lecture.orderNum,
            'courseId': lecture.courseId,
            'topic': lecture.topic,
            'content': lecture.content,
            'createDate': lecture.createDate,
            'links': lecture.links,
            'practices': lecture.practices
        }
    };
}