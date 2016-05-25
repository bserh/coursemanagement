INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(1, 'dorogan.olga.test@gmail.com', '123', 'Olga', 'teacherSecondName_1', 'olgaSurname', 'TEACHER');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(2, 'codedealerb@gmail.com', '123', 'teacherName_1', 'teacherSecondName_1', 'teacherSurname_1', 'TEACHER');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(3, 'selivanovavika1993@gmail.com', '123', 'teacherName_2', 'teacherSecondName_2', 'teacherSurname_2', 'TEACHER');

INSERT INTO courses(id, name, startdate, enddate, description, calendar_id) VALUES (1, 'Java SE', '2015-10-01', '2016-07-31','Java Platform, Standard Edition (Java SE) offers the rich user interface, performance and security that today''s applications require. With Java SE Training, learn how it gives customers enterprise features that minimize the costs of deployment and maintenance of their Java-based IT environment.', 'f4ca4vc4jvpb0h3kg6848fc7ag@group.calendar.google.com');
INSERT INTO courses(id, name, startdate, enddate, description, calendar_id) VALUES (2, 'Java EE', '2015-10-01', '2016-07-31','Companies utilize the new, lightweight Java EE Web Profile to create next-generation web applications. Through Java EE Training, learn more about the full power of the Java EE platform for enterprise applications and more.', 'f4ca4vc4jvpb0h3kg6848fc7ag@group.calendar.google.com');
INSERT INTO courses(id, name, startdate, enddate, description, calendar_id) VALUES (4, 'Old course', '2014-01-10', '2015-06-30','Description', 'f4ca4vc4jvpb0h3kg6848fc7ag@group.calendar.google.com');
INSERT INTO courses(id, name, startdate, enddate, description, calendar_id) VALUES (3, 'Android', '2014-01-10', '2015-07-31','Android is a mobile operating system (OS) based on the Linux kernel and currently developed by Google.', 'f4ca4vc4jvpb0h3kg6848fc7ag@group.calendar.google.com');

INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (1, 1, 3, 'SIGNED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (2, 3, 3, 'SIGNED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (3, 1, 2, 'SIGNED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (4, 1, 1, 'SIGNED');

INSERT INTO news(id, title, description, date, course_id) VALUES (1, 'Java SE', 'Курс Java SE начался', '2015-07-08 18:48:05',1);
INSERT INTO news(id, title, description, date, course_id) VALUES (2, 'Java EE', 'Курс Java EE начался', '2015-09-01 17:35:01',2);
INSERT INTO news(id, title, description, date, course_id) VALUES (3, 'Android', 'Курс Android начался', '2015-01-01 17:35:01',3);

-- Persons to test approving / rejecting course status
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(4, 'leo@gmail.com', '123', 'Leonard', 'Leakey', 'Hofstadter', 'STUDENT');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(5, 'shelly@gmail.com', '123', 'Sheldon', 'Lee', 'Cooper', 'STUDENT');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(6, 'howie@gmail.com', '123', 'Howard', 'Joel', 'Wolowitz', 'STUDENT');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(7, 'raj@gmail.com', '123', 'Rajesh', 'Ramayan', 'Koothrappali', 'STUDENT');

INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (5, 1, 4, 'REQUESTED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (6, 1, 5, 'REQUESTED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (7, 1, 6, 'REQUESTED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (8, 1, 7, 'REQUESTED');

INSERT INTO lesson(id, date, description, order_num, topic, type, course_id) VALUES(1, '2015-10-01', 'Intro', 1, 'Intro', null, 1);
INSERT INTO lesson(id, date, description, order_num, topic, type, course_id) VALUES(2, '2015-10-08', 'Collections', 2, 'Collections', null, 1);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (1, 1, 'Install Ubuntu 15.01', 1);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (2, 2, 'Install Intellij Idea', 1);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (3, 1, 'Choose one collection (List, Set or Map) and create its realization', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (4, 2, 'Test collection realization', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (5, 3, 'Fifth task', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (6, 4, 'Sixth task', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (7, 5, 'Seventh task', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (8, 6, 'Eighth task', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (9, 7, 'Ninth task', 2);
INSERT INTO practice_lesson(id, orderNum, task, lesson_id) VALUES (10, 8, 'Tenth task', 2);

INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (1, 100, 1, 4);
INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (2, 90, 3, 4);

-- Persons to test student progress
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(8, 'dorogan.olga.n@gmail.com', '123', 'Olga', '', 'olga', 'STUDENT');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (9, 1, 8, 'SIGNED');
INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (3, 10, 1, 8);
INSERT INTO mark(id, mark, lesson_id, person_id) VALUES (4, 20, 3, 8);