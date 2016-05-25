function CourseService(Restangular) {
    var restBase = 'resources/course';
    var Course = Restangular.all(restBase);

    this.getCourses = function () {
        return Course.getList();
    };

    this.getNewCourses = function () {
        var today = new Date();
        var todayAsString = today.getUTCFullYear() + '-' + (today.getUTCMonth() + 1) + '-' + today.getUTCDay();
        return Course.getList({'date': todayAsString, 'period': 'start_after'});
    };

    this.getOldCourses = function () {
        var today = new Date();
        var todayAsString = today.getUTCFullYear() + '-' + (today.getUTCMonth() + 1) + '-' + today.getUTCDay();
        return Course.getList({'date': todayAsString, 'period': 'end_before'});
    };

    this.createCourse = function (newCourse, prototypeId) {
        // POST returns promise, in which successHandler is executed ALWAYS when response contains any text
        // so, it's necessary to check is response status equal 2xx or not
        // or another way --- any successfully returned object contains field 'fromServer' with value 'true'
        return Course.post(newCourse, {'prototypeId': prototypeId});
    };

    this.isCourseSuccessfullyCreated = function (returnedObject) {
        return returnedObject.responseStatus == 201;
    };

    this.removeCourse = function (id) {
        return Restangular.one(restBase, id).remove();
    };

    this.updateCourse = function (course) {
        return Restangular.one(restBase).customPUT(course, course.id);
    };

    this.getCourse = function (courseId) {
        return Restangular.one(restBase, courseId).get();
    };

    this.normalizeCourse = function (course) {
        return {
            'id': course.id,
            'name': course.name,
            'description': course.description,
            'startDate': course.startDate,
            'endDate': course.endDate,
            'calendarId': course.calendarId
        };
    };

    this.subscribePersonToCourse = function (courseId, personId) {
        if (courseId === undefined || personId === undefined) {
            return {};
        }
        return Restangular.one(restBase, courseId).all('subscribe').customPUT({"id": personId});
    };

    this.getCourseSubscribers = function (courseId) {
        return Restangular.one(restBase, courseId).all('subscriber').getList();
    };

    this.updateCourseSubscribers = function (courseId, subscribers) {
        return Restangular.one(restBase, courseId).all('subscriber').customPUT(subscribers);
    };

    this.getCourseStudents = function (courseId) {
        return Restangular.one(restBase, courseId).all('student').getList();
    };

    this.getCoursePractices = function (courseId) {
        return Restangular.one(restBase, courseId).all('practice').getList();
    };

    this.getActionMsgByStatus = function (status, isTeacher) {
        var msg = 'Подписаться';
        if (isTeacher) {
            msg = 'Редактировать';
        } else {
            switch (status) {
                case "REQUESTED":
                    msg = 'Перейти к курсу';
                    break;
                case "SIGNED":
                    msg = 'Перейти к курсу';
                    break;
                case "UNSIGNED":
                    msg = 'Подписаться';
                    break;
            }
        }
        return msg;
    };

    var statusLabelPair = {};
    statusLabelPair['REQUESTED'] = 'не подтвержден';
    statusLabelPair['SIGNED'] = 'зачислен';
    statusLabelPair['UNSIGNED'] = 'не зачислен';

    this.convertStatusToLabel = function (status) {
        return statusLabelPair[status];
    };

    this.getAvailableStatuses = function () {
        return Object.keys(statusLabelPair);
    };

    this.isStatusNotProcessed = function(personWithStatus) {
        return personWithStatus.courseStatus == 'REQUESTED';
    };
}
