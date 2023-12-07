drop table if exists public.member;
create table public.member (
   id bigserial,
   mobile varchar(11) not null,
   primary key (id),
   CONSTRAINT unq_member_mobile UNIQUE (mobile)
) WITH (OIDS = false) TABLESPACE pg_default;

COMMENT ON TABLE public.member IS '会员账号表';
COMMENT ON COLUMN public.member.mobile IS '手机号';

drop table if exists public.passenger;
create table public.passenger (
                                  id bigserial,
                                  member_id bigint not null,
                                  name varchar(16) not null,
                                  id_card varchar(18) not null,
                                  type char(1) not null,
                                  create_time timestamp(0),
                                  update_time timestamp(0),
                                  primary key (id)
) WITH (OIDS = false) TABLESPACE pg_default;

CREATE INDEX passenger_member_id ON public.passenger(member_id);
COMMENT ON TABLE public.passenger IS '乘车人';
COMMENT ON COLUMN public.passenger.member_id IS '添加此乘车人的会员账号id';
COMMENT ON COLUMN public.passenger.name IS '姓名';
COMMENT ON COLUMN public.passenger.id_card IS '身份证';
COMMENT ON COLUMN public.passenger.type IS '旅客类型|枚举[PassengerTypeEnum]';
COMMENT ON COLUMN public.passenger.create_time IS '新增时间';
COMMENT ON COLUMN public.passenger.update_time IS '修改时间';


drop table if exists `ticket`;
create table `ticket` (
                          `id` bigint not null comment 'id',
                          `member_id` bigint not null comment '会员id',
                          `passenger_id` bigint not null comment '乘客id',
                          `passenger_name` varchar(20) comment '乘客姓名',
                          `train_date` date not null comment '日期',
                          `train_code` varchar(20) not null comment '车次编号',
                          `carriage_index` int not null comment '箱序',
                          `seat_row` char(2) not null comment '排号|01, 02',
                          `seat_col` char(1) not null comment '列号|枚举[SeatColEnum]',
                          `start_station` varchar(20) not null comment '出发站',
                          `start_time` time not null comment '出发时间',
                          `end_station` varchar(20) not null comment '到达站',
                          `end_time` time not null comment '到站时间',
                          `seat_type` char(1) not null comment '座位类型|枚举[SeatTypeEnum]',
                          `create_time` datetime(3) comment '新增时间',
                          `update_time` datetime(3) comment '修改时间',
                          primary key (`id`),
                          index `member_id_index` (`member_id`)
) engine=innodb default charset=utf8mb4 comment='车票';
