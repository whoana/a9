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
    ON c.INTERFACE_ID = a.INTERFACE_ID

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
   
;

SELECT * 
  FROM TOP0802 a
INNER JOIN TIM0205 b
	ON a.PROCESS_ID  = b.PROCESS_ID 
ORDER BY a.REG_DATE desc;


SELECT * FROM tim0212;

SELECT * FROM tsu0205;
SELECT * FROM top0810 ORDER BY GET_DATE  desc;
SELECT * FROM TIM0211;

SELECT * FROM tih0201;








/* 고용노동부-파일인터페이스정보 */
DROP TABLE TIH0201 
	CASCADE CONSTRAINTS;

/* 고용노동부-파일인터페이스상태로그 */
DROP TABLE TIH0202 
	CASCADE CONSTRAINTS;

/* 고용노동부-파일인터페이스정보 */
CREATE TABLE TIH0201 (
	INTERFACE_ID VARCHAR(50) NOT NULL, /* 인터페이스ID */
	AGENT_ID VARCHAR(50) NOT NULL, /* 에이전트ID */
	DIRECTION VARCHAR(5) NOT NULL, /* 송수신구분 */
	DIRECTORY VARCHAR(1024) NOT NULL, /* 디렉토리 */
	ERR_DIRECTORY VARCHAR(1024), /* 에러디렉토리 */
	MAX_FILE_COUNT INTEGER DEFAULT 0, /* 파일발생건수입계치 */
	FILE_TIME_LIMIT INTEGER DEFAULT 5, /* 파일처리초과시간(분) */
	DIR_CHECK_DELAY INTEGER DEFAULT 10, /* 디렉토리체크주기(분) */
	ERR_FILE_DUR INTEGER DEFAULT 1440, /* 에러파일체크만기(분) */
	DEL_YN VARCHAR(1) DEFAULT 'N' NOT NULL, /* 삭제구분 */
	REG_DATE VARCHAR(17) NOT NULL, /* 등록일 */
	REG_USER VARCHAR(100) NOT NULL, /* 등록자 */
	MOD_DATE VARCHAR(17), /* 최종수정일 */
	MOD_USER VARCHAR(100) /* 최종수정자 */
);

COMMENT ON TABLE TIH0201 IS '고용노동부-파일인터페이스정보';

COMMENT ON COLUMN TIH0201.INTERFACE_ID IS '인터페이스ID';

COMMENT ON COLUMN TIH0201.AGENT_ID IS '에이전트ID';

COMMENT ON COLUMN TIH0201.DIRECTION IS '송수신구분';

COMMENT ON COLUMN TIH0201.DIRECTORY IS '디렉토리';

COMMENT ON COLUMN TIH0201.ERR_DIRECTORY IS '에러디렉토리';

COMMENT ON COLUMN TIH0201.MAX_FILE_COUNT IS '파일발생건수입계치';

COMMENT ON COLUMN TIH0201.FILE_TIME_LIMIT IS '파일처리초과시간(분)';

COMMENT ON COLUMN TIH0201.DIR_CHECK_DELAY IS '디렉토리체크주기(분)';

COMMENT ON COLUMN TIH0201.ERR_FILE_DUR IS '에러파일체크만기(분)';

COMMENT ON COLUMN TIH0201.DEL_YN IS '삭제구분';

COMMENT ON COLUMN TIH0201.REG_DATE IS '등록일';

COMMENT ON COLUMN TIH0201.REG_USER IS '등록자';

COMMENT ON COLUMN TIH0201.MOD_DATE IS '최종수정일';

COMMENT ON COLUMN TIH0201.MOD_USER IS '최종수정자';

CREATE UNIQUE INDEX PK_TIH0201
	ON TIH0201 (
		INTERFACE_ID ASC,
		AGENT_ID ASC
	);

ALTER TABLE TIH0201
	ADD
		CONSTRAINT PK_TIH0201
		PRIMARY KEY (
			INTERFACE_ID,
			AGENT_ID
		);

/* 고용노동부-파일인터페이스상태로그 */
CREATE TABLE TIH0202 (
	INTERFACE_ID VARCHAR(50) NOT NULL, /* 인터페이스ID */
	AGENT_ID VARCHAR(50) NOT NULL, /* 에이전트ID */
	CHECK_DATE VARCHAR(14) NOT NULL, /* 체크시작시간 */
	CHECK_FILE_CD VARCHAR(5) DEFAULT '0', /* 체크파일코드 */
	CHECK_FILE_MSG VARCHAR(4000), /* 체크파일메시지 */
	CHECK_ERR_FILE_CD VARCHAR(5) DEFAULT '0', /* 체크에러파일코드 */
	CHECK_ERR_FILE_MSG VARCHAR(4000), /* 체크에러파일메시지 */
	FILE_COUNT INTEGER, /* 시간미초과파일건수 */
	LAZY_FILE_COUNT INTEGER, /* 시간초과파일건수 */
	ERR_FILE_COUNT INTEGER, /* 에러파일건수 */
	REG_DATE VARCHAR(17) NOT NULL /* 등록일시 */
);

COMMENT ON TABLE TIH0202 IS '고용노동부-파일인터페이스상태로그';

COMMENT ON COLUMN TIH0202.INTERFACE_ID IS '인터페이스ID';

COMMENT ON COLUMN TIH0202.AGENT_ID IS '에이전트ID';

COMMENT ON COLUMN TIH0202.CHECK_DATE IS '체크시작시간';

COMMENT ON COLUMN TIH0202.CHECK_FILE_CD IS '
에러코드
------------------------------------------------------------
0	정상 
1	전송지연
9	파일체크실패

       ';

COMMENT ON COLUMN TIH0202.CHECK_FILE_MSG IS '
에러코드
------------------------------------------------------------
0	정상 
1	전송지연
9	파일체크실패';

COMMENT ON COLUMN TIH0202.CHECK_ERR_FILE_CD IS '
에러코드
------------------------------------------------------------
0	정상 
2 	전송에러 
9	에러파일체크실패

       ';

COMMENT ON COLUMN TIH0202.CHECK_ERR_FILE_MSG IS '
에러코드
------------------------------------------------------------
0	정상 
2 	전송에러 
9	에러파일체크실패';

COMMENT ON COLUMN TIH0202.FILE_COUNT IS '시간미초과파일건수';

COMMENT ON COLUMN TIH0202.LAZY_FILE_COUNT IS '시간초과파일건수';

COMMENT ON COLUMN TIH0202.ERR_FILE_COUNT IS '에러파일건수';

COMMENT ON COLUMN TIH0202.REG_DATE IS '등록일시';

CREATE UNIQUE INDEX PK_TIH0202
	ON TIH0202 (
		INTERFACE_ID ASC,
		AGENT_ID ASC
	);

ALTER TABLE TIH0202
	ADD
		CONSTRAINT PK_TIH0202
		PRIMARY KEY (
			INTERFACE_ID,
			AGENT_ID
		);

ALTER TABLE TIH0201
	ADD
		CONSTRAINT FK_TAN0201_TO_TIH0201
		FOREIGN KEY (
			INTERFACE_ID
		)
		REFERENCES TAN0201 (
			INTERFACE_ID
		);

ALTER TABLE TIH0202
	ADD
		CONSTRAINT FK_TIH0201_TO_TIH0202
		FOREIGN KEY (
			INTERFACE_ID,
			AGENT_ID
		)
		REFERENCES TIH0201 (
			INTERFACE_ID,
			AGENT_ID
		);