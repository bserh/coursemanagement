module.exports = function (grunt) {

    // Задачи
    grunt.initConfig({
        // Склеиваем
        concat: {
            main: {
                src: [
                    'webapp/angular/app.js',
                    'webapp/angular/*/*.js'
                ],
                dest: 'webapp/build/app.js'
            }
        },
        //Подключаем все файлы которые скачал bower
        bower_concat: {
            all: {
                dest: 'webapp/build/_bower.js',  // Склеенный файл
                exclude: [  // Пакеты, которые нужно исключить из сборки
                    'blueimp-canvas-to-blob',
                    'blueimp-tmpl',
                    "blueimp-load-image",
                ]
            }
        },
        // Сжимаем
        uglify: {
            options: {
                separator: ':'
            },
            main: {
                files: {
                    // Результат задачи concat
                    'webapp/build/app.min.js': '<%= concat.main.dest %>'
                }
            },
            bower: {
                files: {
                    'webapp/build/_bower.min.js': '<%= bower_concat.all.dest %>'
                }
            }
        },
        //Склеивание в реальном времени
        watch: {
            scripts: {
                files: '<%= concat.main.src %>',
                tasks: ['concat', 'uglify:main'],
                options: {
                    spawn: false
                }
            }
        }
    });

    // Загрузка плагинов, установленных с помощью npm install
    grunt.loadNpmTasks('grunt-contrib-concat');
    grunt.loadNpmTasks('grunt-contrib-uglify');
    grunt.loadNpmTasks('grunt-bower-concat');
    grunt.loadNpmTasks('grunt-contrib-watch');

    // Задача по умолчанию
    grunt.registerTask('default', ['concat', 'bower_concat', /*'uglify'*/]);
};