INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(2, 'codedealerb@gmail.com', '123', 'teacherName_1', 'teacherSecondName_1', 'teacherSurname_1', 'TEACHER');
INSERT INTO person(id, email, phone, name, second_name, last_name, personRole) VALUES(3, 'selivanovavika1993@gmail.com', '123', 'teacherName_2', 'teacherSecondName_2', 'teacherSurname_2', 'TEACHER');

INSERT INTO courses(id, name, startdate, enddate, description) VALUES (1, 'Java SE', '2015-10-01', '2016-07-31','Java Platform, Standard Edition (Java SE) offers the rich user interface, performance and security that today''s applications require. With Java SE Training, learn how it gives customers enterprise features that minimize the costs of deployment and maintenance of their Java-based IT environment.');
INSERT INTO courses(id, name, startdate, enddate, description) VALUES (2, 'Java EE', '2015-10-01', '2016-07-31','Companies utilize the new, lightweight Java EE Web Profile to create next-generation web applications. Through Java EE Training, learn more about the full power of the Java EE platform for enterprise applications and more.');
INSERT INTO courses(id, name, startdate, enddate, description) VALUES (4, 'Old course', '2014-01-10', '2015-06-30','Description');
INSERT INTO courses(id, name, startdate, enddate, description) VALUES (3, 'Android', '2014-01-10', '2015-07-31','Android is a mobile operating system (OS) based on the Linux kernel and currently developed by Google.');

INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (1, 1, 3, 'SIGNED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (3, 3, 3, 'SIGNED');
INSERT INTO course_person_status(id, course_id, person_id, course_status) VALUES (2, 2, 3, 'SIGNED');


INSERT INTO news(id, title, description, date, course_id) VALUES (1, 'Java SE', 'Курс Java SE начался', '2015-07-08 18:48:05',1);
INSERT INTO news(id, title, description, date, course_id) VALUES (2, 'Java EE', 'Курс Java EE начался', '2015-09-01 17:35:01',2);
INSERT INTO news(id, title, description, date, course_id) VALUES (3, 'Android', 'Курс Android начался', '2015-01-01 17:35:01',3);
