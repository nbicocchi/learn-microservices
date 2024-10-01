drop table grades;
create table grades(
    id serial primary key,
    value int not null check(value > 0),
    student_id int not null
);