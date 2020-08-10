insert into global_settings (code, name, value) values ('MULTIUSER_MODE', '', 'Yes');
insert into global_settings (code, name, value) values ('POST_PREMODERATION', '', 'Yes');
insert into global_settings (code, name, value) values ('STATISTICS_IS_PUBLIC', '', 'Yes');

select globalsett0_.id as id1_1_,
        globalsett0_.code as code2_1_,
        globalsett0_.name as name3_1_,
        globalsett0_.value as value4_1_
        from global_settings globalsett0_