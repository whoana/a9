SELECT * FROM tsu0302 WHERE PACKAGE = 'system';
-------------------------------
-- agent 등록 
-------------------------------
-- AGENT01
SELECT * FROM tim0211;

-- 사용자 􀳿
-- iip/iip$$
SELECT * FROM TSU0101 ;

SELECT * FROM TIH0201 ;
SELECT * FROM TIH0202 ;

SELECT * FROM tan0201;

SELECT * FROM TIM0002;

SELECT * FROM tsu03 02 WHERE PACKAGE = 'inhouse.moel';

--------------------------------------------------------
-- 인터페이스 파일(에러파일포함) 체크 에러 발생건에 대한 설정값 조회  
--------------------------------------------------------
SELECT 
	 b.INTERFACE_ID 
	,c.INTEGRATION_ID 
	,c.INTERFACE_NM 
	,b.DIRECTION
	,b.DIRECTIORY
	,b.ERR_DIR
	,b.MAX_FILE_COUNT
	,b.FILE_TIME_LIMIT
	,b.DIR_CHECK_DELAY
	,b.ERR_FILE_DUR 
	,b.AGENT_ID 
  FROM TIH0202 a 
 INNER JOIN TIH0201 b
    ON a.INTERFACE_ID = b.INTERFACE_ID
   AND (a.CHECK_FILE_CD > '0' OR a.CHECK_ERR_FILE_CD > '0') -- 체크 코드가 '0' 보다큰 (에러인)
   AND b.DEL_YN = 'N'
 INNER JOIN TAN0201 c 
    ON c.INTERFACE_ID = a.INTERFACE_ID;
   
   SELECT * FROM tih0202;

--------------------------------------------------------
-- 파일처리지연 로그 리스트 조회    
--------------------------------------------------------
SELECT 
	a.*
  FROM TIH0202 a
 INNER JOIN TIH0201 b
    ON a.INTERFACE_ID = b.INTERFACE_ID
   AND a.LAZY_FILE_COUNT > b.MAX_FILE_COUNT
   AND b.DEL_YN = 'N'
   AND a.CHECK_FILE_CD = '0'
;    

--------------------------------------------------------
-- 에러파일발생 로그 리스트 조회    
--------------------------------------------------------
SELECT 
	a.*
  FROM TIH0202 a
 INNER JOIN TIH0201 b
    ON a.INTERFACE_ID = b.INTERFACE_ID
   AND a.ERR_FILE_COUNT > 0
   AND b.DEL_YN = 'N'
   AND a.CHECK_ERR_FILE_CD = '0'
;    

-----------------------------------------------
-- 인하우스 환경변수 작업 
-----------------------------------------------
--inhouse.moel.file.interface.check.delay
--inhouse.moel.file.interface.time.limit
--inhouse.moel.file.interface.error.duration

INSERT INTO IIP_MOEL.TSU0302 (PACKAGE, ATTRIBUTE_ID, IDX, ATTRIBUTE_NM, ATTRIBUTE_VALUE, COMMENTS, DEL_YN, REG_DATE, REG_USER) VALUES('inhouse.moel', 'file.interface.check.delay'	 , 0, 'file.interface.check.delay'	 , '5'									, '체크주기(분)'						, 'N', 'whoana', to_char(sysdate,'yyyymmddHH24miss'));
INSERT INTO IIP_MOEL.TSU0302 (PACKAGE, ATTRIBUTE_ID, IDX, ATTRIBUTE_NM, ATTRIBUTE_VALUE, COMMENTS, DEL_YN, REG_DATE, REG_USER) VALUES('inhouse.moel', 'file.interface.time.limit'	 , 0, 'file.interface.time.limit'	 , '10'									, '파일전송제한시간(분)'					, 'N', 'whoana', to_char(sysdate,'yyyymmddHH24miss'));
INSERT INTO IIP_MOEL.TSU0302 (PACKAGE, ATTRIBUTE_ID, IDX, ATTRIBUTE_NM, ATTRIBUTE_VALUE, COMMENTS, DEL_YN, REG_DATE, REG_USER) VALUES('inhouse.moel', 'file.interface.error.duration', 0, 'file.interface.error.duration', '1440'								, '에러파일체크유효시간(분,현재시간으로부터)'	, 'N', 'whoana', to_char(sysdate,'yyyymmddHH24miss'));

DELETE FROM tsu0302 WHERE PACKAGE = 'inhouse.moel' AND attribute_id = 'file.interface.error.dir';
DELETE FROM tsu0302 WHERE PACKAGE = 'inhouse.moel' AND attribute_id = 'file.interface.path'
 
SELECT * FROM tih0202;

SELECT * FROM TIH0201 ;
UPDATE TIH0201 SET MAX_FILE_COUNT  = 1;
SELECT 
	INTERFACE_ID
	DIRECTION
	DIRECTORY
	ERR_DIRECTORY
	MAX_FILE_COUNT
	FILE_TIME_LIMIT
	DIR_CHECK_DELAY
	ERR_FILE_DUR
	AGENT_ID
	DEL_YN
	REG_DATE
	REG_USER
	MOD_DATE
	MOD_USER
 FROM TIH0201
 WHERE 
 
SELECT * FROM tim0214;

SELECT * FROM tim0205;
SELECT * FROM tim0211;

SELECT * 
  FROM tim0212 a
 INNER JOIN tim0205 b
    ON a.ITEM_ID  = b.PROCESS_ID
   AND a.AGENT_ID = 'AG00000001'
   AND b.del_yn = 'N'
   
;

SELECT a.* 
  FROM TIM0205 a
INNER JOIN tim0212 b
  ON a.PROCESS_ID = b.ITEM_ID
  AND b.AGENT_ID = 'AG00000001'
  AND a.DEL_YN = 'N'
 ;
 

SELECT * FROM top0802 ORDER BY GET_DATE desc;

SELECT *
  FROM TOP0802 a
INNER JOIN TIM0205 b
	ON a.PROCESS_ID  = b.PROCESS_ID 
INNER JOIN tim0212 c
	ON b.PROCESS_ID  = c.ITEM_ID 
	AND c.ITEM_TYPE = '1'
	AND c.AGENT_ID = 'AG00000001'	
ORDER BY a.REG_DATE desc;


ALTER TABLE TIH0201 ADD (confirm_cd varchar(5) default '0');
ALTER TABLE TIH0201 ADD (confirm_msg varchar(4000));

UPDATE tih0201 SET confirm_cd = '1', CONFIRM_MSG = '확인완료';

COMMIT;

SELECT * FROM tih0201;

 