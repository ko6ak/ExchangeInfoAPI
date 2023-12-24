create table currency (
    name varchar(255) not null,
    quote numeric(20,6),
    timestamp timestamp(6),
    primary key (name));