delete
from user_role;
delete
from usr;

insert into usr(id, active, password, username)
values (1, true, '$2a$08$hZvIjE54uLcWr2xJGpPq7O5odaGiKQdgmw.JeN1vA6WFM93g07orO', 'max'),
       (2, true, '$2a$08$hZvIjE54uLcWr2xJGpPq7O5odaGiKQdgmw.JeN1vA6WFM93g07orO', 'mike');

insert into user_role(user_id, roles)
values (1, 'USER'),
       (1, 'ADMIN'),
       (2, 'USER');
