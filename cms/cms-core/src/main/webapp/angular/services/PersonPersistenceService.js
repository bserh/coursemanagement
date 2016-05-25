/**
 * Created by olga on 14.07.15.
 */
function PersonPersistenceService(localStorageService) {
    this.saveInfo = function (id, role, name, email, token) {
        localStorageService.set('id', id);
        localStorageService.set('role', role);
        localStorageService.set('name', name);
        localStorageService.set('email', email);
        localStorageService.set('token', token);
    };
    this.saveHash = function (hash) {
        localStorageService.set('emailhash', hash);
    };
    this.clearInfo = function () {
        localStorageService.clearAll();
    };
    this.getId = function () {
        return localStorageService.get('id');
    };
    this.getRole = function () {
        return localStorageService.get('role');
    };
    this.getName = function () {
        return localStorageService.get('name');
    };
    this.getEmail = function () {
        return localStorageService.get('email');
    };
    this.getToken = function () {
        return localStorageService.get('token');
    };
    this.getEmailHash = function () {
        return localStorageService.get('emailhash');
    };
    this.isTeacher = function () {
        return this.getRole() == 'teacher';
    };
}