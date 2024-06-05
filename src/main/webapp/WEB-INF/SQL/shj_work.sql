show user;
-- USER이(가) "SEMI_ORAUSER4"입니다.

select * from tab;

select to_char(sysdate, 'yyyy-mm-dd')
from dual;

select *
from member;

select *
from memberidx;



select userid, name, email, phone, gender, status
from member 
join memberidx 
on member.memberidx = memberidx.memberidx
where memberidx.memberidx != 9 and name like '%'|| '테' ||'%' 
order by registerday desc ;


select ceil(count(*)/10)
from member 
join memberidx 
on member.memberidx = memberidx.memberidx
where memberidx.memberidx != 9;



select userid, name, email, phone, gender, status
from member 
join memberidx 
on member.memberidx = memberidx.memberidx
where memberidx.memberidx != 9 and name like '%'|| '테' ||'%' 
order by registerday desc ;


SELECT rno, userid, name, email, gender 
FROM 
( 
    select rownum AS RNO, userid, name, email, gender
    from 
    (
        select userid, name, email, gender
        from member
        where userid != 'admin' and name like '%'|| '테' ||'%'
        order by registerday desc
    ) V 
) T
WHERE T.rno BETWEEN 1 AND 10;



SELECT rno, userid, name, email, phone, gender, status
FROM 
( 
    select rownum AS RNO, userid, name, email, phone, gender, status
    from 
    (
        select userid, name, email, phone, gender, status
        from member join memberidx on member.memberidx = memberidx.memberidx
        where memberidx.memberidx != 9 and name like '%'|| '테' ||'%'
        order by registerday desc
    ) 
) T
WHERE T.rno BETWEEN 1 AND 20;


select count(*)
from tbl_member
where userid != 'admin'
and name like '%'|| '아' ||'%'

select count(*)
from member join memberidx on member.memberidx = memberidx.memberidx
where memberidx.memberidx != 9 and name like '%' || '테' || '%';


update member set memberidx = 9
where userid = 'ejss0125';
-- 1 행 이(가) 업데이트되었습니다.

commit;
-- 커밋 완료.






-- 회원테이블
create table member 
(userid              VARCHAR2(20)   not null                    -- 회원아이디
,pwd                 VARCHAR2(20)   not null                    -- 암호
,name                NVARCHAR2(20)  not null                    -- 회원명
,email               VARCHAR2(100)  not null                    -- 이메일
,phone               VARCHAR2(20)   not null                    -- 연락처
,address             NVARCHAR2(100) not null                    -- 주소
,addressDetail       NVARCHAR2(100) not null                    -- 상세주소
,gender              NVARCHAR2(2)   not null                    -- 성별
,birthday            NVARCHAR2(20)  not null                    -- 생년월일
,point               NVARCHAR2(20)                              -- 포인트
,registerDay         NVARCHAR2(20)  default sysdate  not null   -- 가입일자
,pwdUpdateDay        NVARCHAR2(20)                              -- 암호변경일
,memberIdx           NVARCHAR2(1) not null                      -- 회원상태코드
,constraint PK_member_userid primary key(userid)
,constraint UQ_member_email  unique(email)
,constraint FK_member_fk_memberIdx foreign key(memberIdx) references memberIdx(memberIdx)
);
-- Table MEMBER이(가) 생성되었습니다.



-- 회원상태 테이블
create table memberIdx
(
memberIdx           NVARCHAR2(1)  not null        -- 회원상태코드
,status             NVARCHAR2(20) not null        -- 상태 (가입, 탈퇴, 휴면, 관리자, 팀원)
,constraint PK_memberIdx_memberIdx primary key(memberIdx)
);
-- Table MEMBERIDX이(가) 생성되었습니다.




commit;
--커밋 완료.




select userid, name, email, phone, address, addressdetail, gender
 , birthday, point, registerday, memberidx.status
from member join memberidx on member.memberidx = memberidx.memberidx
where memberidx.memberidx != 9 and userid = 'admin_1'


select *
from log;

select *
from coupon;

select *
from mycoupon;

select *
from member;

insert into mycoupon(coindex, userid, coname, costatus) values('503', 'kmj0228', '무료로 드립니다 [100% 쿠폰]', '1');

commit;


select COUPON.CONAME as coname, COTYPE, CODISCOUNT, CODATE, COREGISTERDAY, COINDEX, costatus
from COUPON join MYCOUPON on COUPON.CONAME = MYCOUPON.CONAME join MEMBER on MYCOUPON.USERID = MEMBER.USERID
where MEMBER.USERID = 'kmj0228'



select logindex, userid, logindate, ipaddress
from 
(
    select log.logindex AS logindex, member.userid AS userid, logindate, ipaddress
    from log 
    join member on log.userid = member.userid
    where member.userid = 'kmj0228'
    order by logindate desc
) T
WHERE rownum <= 5;


update member
set memberidx = '1'
where userid = 'auto007' and memberidx = '3';


update member
set memberidx = '1'
where memberidx = '3';


commit;



select *
from member;

select *
from review;

select *
from point;


select member.userid AS userid, poincome, podetail, podate
from point
join member on point.userid = member.userid
where member.userid = 'admin_1'
order by podate desc;


insert into point(userid, poincome, podetail, podate) values('admin_1', 1000, '상품 구매 적립', '2024-05-27 15:16:00');
insert into point(userid, poincome, podetail, podate) values('admin_1', 1450, '상품 구매 적립', '2024-05-27 15:10:00');
insert into point(userid, poincome, podetail, podate) values('admin_1', 12850, '이벤트 적립', '2024-05-27 14:05:00');



commit;



select review.rindex, review.rstar, review.rdetail, review.rdate
from review join orders on review.oindex = orders.oindex
join member on orders.userid = member.userid
where member.userid = 'admin_1'
order by odate desc;




select coupon.coname
from coupon join mycoupon on coupon.coname = mycoupon.coname
join member on mycoupon.userid = member.userid
where member.userid = ''
order by coindex desc;



insert into mycoupon(coindex, userid, coname, costatus) VALUES (seq_coindex.nextval, ?, ?, 1)


delete from mycoupon
where userid = 'admin_1';

commit;


select userid, name, email, phone, address, addressdetail, gender, member.memberIdx
, birthday, point, registerday, memberidx.status
from member join memberidx on member.memberidx = memberidx.memberidx
where memberidx.memberidx = 1 and userid = 'admin_1'

select *
from point;

select *
from review;


select rindex, rstar, rdetail, rdate
from member join orders on member.userid = orders.userid
join review on orders.oindex = review.oindex
where member.userid = 'gpwjd1wldms'




delete from review
where to_char(rindex) = 21;

rollback;

update member set point = point - 500
where userid = 'gpwjd1wldms';

select *
from point
where userid = 'gpwjd1wldms';


insert into point(userid, poincome, podetail, podate) VALUES ('gpwjd1wldms', -500, '관리자 리뷰 삭제 차감', to_char(sysdate, 'yyyy-mm-dd hh24:mi:ss'))

commit;


------ **** local 한 semi_orauser4 로 연결하기 **** ------
show user;
-- USER이(가) "SEMI_ORAUSER4"입니다.

select * from tab;
----------------------------------------------------------














