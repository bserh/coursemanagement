function NewsService(Restangular) {
    var restBase = 'resources/courses';
    var News = Restangular.all(restBase);
    this.getNews = function () {
        return News.getList();
    };

    this.getPersonNews = function (personId) {
        if (personId === undefined) {
            return {};
        }
        return Restangular.one(restBase, personId).all('news').getList();
    };

    this.getNewsFromCourse = function (courseId) {
        if (courseId === undefined) {
            return {};
        }
        return Restangular.one(restBase, courseId).all('news').getList();
    };


    this.isNewsSuccessfullyCreated = function (returnedObject) {
        return returnedObject.responseStatus == 201;
    };


    this.createNews = function (newNews, courseId) {
        console.log("service: create news"+newNews.courseId);
        return Restangular.one(restBase,courseId).all('saveNews').post(newNews);
    };


    this.updateNews = function (newNews) {
        return Restangular.one(restBase).all('news').put(newNews);
    };

    this.removeNews = function (newsId) {
         return Restangular.one(restBase, newsId).remove();
    };


}