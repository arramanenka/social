create constraint user_id_constraint on (u:app_user) assert u.puId is unique;
create constraint user_name_constraint on (u:app_user) assert u.name is unique;