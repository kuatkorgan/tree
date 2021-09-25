create schema tree;

use tree;

create table tree
(
    id bigint unsigned auto_increment,
    `left` int not null,
    `right` int not null,
    `level` tinyint unsigned,
    name varchar(70),
    primary key (id)
);

truncate table tree;

insert into  tree (`left`, `right`, level, name)
values (1, 10, 1, 'Комплектующие'),
        (2, 7, 2, 'Процессоры'),
       (3, 4, 3, 'Intel'),
       (15, 16, 3, 'Без микрофона'),
       (5, 6, 3, 'AMD'),
       (11, 20, 1, 'Аудиотехника'),
       (8, 9, 2, 'ОЗУ'),
       (12, 17, 2, 'Наушники'),
       (13, 14, 3, 'С микрофоном'),
       (18, 19 , 2, 'Колонки');


