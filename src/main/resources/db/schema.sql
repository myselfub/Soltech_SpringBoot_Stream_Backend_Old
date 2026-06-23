/* TODO: 기본 Data 만들기 */

/*
---- DB 생성 ----
CREATE DATABASE [DBNAME];
CREATE TABLESPACE [DBTABLESPACE] LOCATION '[DBTABLESPACE_PATH]';
ALTER DATABASE [DBNAME] SET default_tablespace = [DBTABLESPACE];

---- 사용자 생성 ----
CREATE USER [DBUSER] WITH PASSWORD '[DBPASSWORD]';
GRANT ALL PRIVILEGES ON DATABASE [DBNAME] TO [DBUSER];
GRANT CREATE ON TABLESPACE [DBTABLESPACE] TO [DBUSER];

---- 스키마 생성 ----
CREATE SCHEMA [DBSCHEMA] AUTHORIZATION [DBUSER];
GRANT CREATE ON SCHEMA [DBSCHEMA] TO [DBUSER];
GRANT USAGE ON SCHEMA [DBSCHEMA] TO [DBUSER];
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA [DBSCHEMA] TO [DBUSER];
ALTER DEFAULT PRIVILEGES IN SCHEMA [DBSCHEMA] GRANT SELECT, INSERT, UPDATE, DELETE ON TABLES TO [DBUSER];

---- DB링크 ----
CREATE EXTENSION IF NOT EXISTS dblink;
---- 피벗 ----
CREATE EXTENSION IF NOT EXISTS tablefunc;
*/

/*
---- 공통코드 분류 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].cmmn_cd_clsf_table (
	cmmn_cd_clsf_id varchar(20) NOT NULL,
	cmmn_cd_clsf_nm varchar(40) NOT NULL,
	cmmn_cd_clsf_expln varchar(500) NULL,
	cmmn_cd_clsf_user_dfn_vl1 varchar(400) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT cmmn_cd_clsf_table_pkey PRIMARY KEY (cmmn_cd_clsf_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].cmmn_cd_clsf_table IS '공통코드 분류 테이블';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.cmmn_cd_clsf_id IS '공통코드 분류 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.cmmn_cd_clsf_nm IS '공통코드 분류 명';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.cmmn_cd_clsf_expln IS '공통코드 분류 설명';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.cmmn_cd_clsf_user_dfn_vl1 IS '공통코드 분류 사용자정의 값1';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_clsf_table.mdfr_id IS '수정자 ID';

---- 공통코드 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].cmmn_cd_table (
	cmmn_cd_clsf_id varchar(20) NOT NULL,
	cmmn_cd_id varchar(20) NOT NULL,
	cmmn_cd_nm varchar(40) NOT NULL,
	cmmn_cd_expln varchar(200) NULL,
	cmmn_cd_up_clsf_id varchar(20) NULL,
	cmmn_cd_up_id varchar(20) NULL,
	cmmn_cd_user_dfn_vl1 varchar(400) NULL,
	cmmn_cd_user_dfn_vl2 varchar(400) NULL,
	cmmn_cd_user_dfn_vl3 varchar(400) NULL,
	cmmn_cd_sort_no int4 NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT cmmn_cd_table_pkey PRIMARY KEY (cmmn_cd_clsf_id, cmmn_cd_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].cmmn_cd_table IS '공통코드 테이블';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_clsf_id IS '공통코드 분류 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_id IS '공통코드 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_nm IS '공통코드 명';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_expln IS '공통코드 설명';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_up_clsf_id IS '공통코드 상위 분류 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_up_id IS '공통코드 상위 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_user_dfn_vl1 IS '공통코드 사용자정의값1';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_user_dfn_vl2 IS '공통코드 사용자정의값2';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_user_dfn_vl3 IS '공통코드 사용자정의값3';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.cmmn_cd_sort_no IS '공통코드 정렬 번호';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].cmmn_cd_table.mdfr_id IS '수정자 ID';

---- 메세지 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].msg_table (
	msg_lang varchar(5) NOT NULL,
	msg_clsf varchar(20) NOT NULL,
	msg_cd varchar(20) NOT NULL,
	msg_nm varchar(50) NOT NULL,
	msg_cn varchar(400) NULL,
	msg_user_dfn_vl1 varchar(100) NULL,
	msg_user_dfn_vl2 varchar(100) NULL,
	msg_user_dfn_vl3 varchar(100) NULL,
	msg_user_dfn_vl4 varchar(100) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT msg_table_pkey PRIMARY KEY (msg_lang, msg_clsf, msg_cd)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].msg_table IS '메세지 테이블';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_lang IS '메세지 언어';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_clsf IS '메세지 분류';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_cd IS '메세지 코드';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_nm IS '메세지 명';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_cn IS '메세지 내용';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_user_dfn_vl1 IS '메세지 사용자정의 값1';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_user_dfn_vl2 IS '메세지 사용자정의 값2';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_user_dfn_vl3 IS '메세지 사용자정의 값3';
COMMENT ON COLUMN [DBSCHEMA].msg_table.msg_user_dfn_vl4 IS '메세지 사용자정의 값4';
COMMENT ON COLUMN [DBSCHEMA].msg_table.use_yn IS '메세지 사용 여부';
COMMENT ON COLUMN [DBSCHEMA].msg_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].msg_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].msg_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].msg_table.mdfr_id IS '수정자 ID';

---- 메뉴 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].menu_table (
	menu_id varchar(20) NOT NULL,
	menu_nm varchar(40) NOT NULL,
	menu_expln varchar(200) NULL,
	menu_url varchar(100) NULL,
	up_menu_id varchar(10) NULL,
	menu_dpth int4 NULL,
	menu_api_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	menu_popup_yn bpchar(1) DEFAULT 'N'::bpchar NOT NULL,
	menu_user_dfn_vl varchar(200) NULL,
	menu_bgng_ymd varchar(8) NOT NULL,
	menu_end_ymd varchar(8) NOT NULL,
	menu_sn varchar(8) NOT NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT menu_table_pkey PRIMARY KEY (menu_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].menu_table IS '메뉴 테이블';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_id IS '메뉴 ID';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_nm IS '메뉴 명';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_expln IS '메뉴 설명';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_url IS '메뉴 URL';
COMMENT ON COLUMN [DBSCHEMA].menu_table.up_menu_id IS '상위 메뉴 ID';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_dpth IS '메뉴 깊이';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_api_yn IS '메뉴 API 여부';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_popup_yn IS '메뉴 팝업 여부';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_user_dfn_vl IS '메뉴 사용자정의 값';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_bgng_ymd IS '메뉴 시작 일자';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_end_ymd IS '메뉴 종료 일자';
COMMENT ON COLUMN [DBSCHEMA].menu_table.menu_sn IS '메뉴 순번';
COMMENT ON COLUMN [DBSCHEMA].menu_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].menu_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].menu_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].menu_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].menu_table.mdfr_id IS '수정자 ID';

---- 권한 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].authrt_table (
	authrt_se_cd varchar(20) NOT NULL,
	menu_id varchar(20) NOT NULL,
	authrt_bgng_ymd varchar(8) NOT NULL,
	authrt_end_ymd varchar(8) NOT NULL,
	mthd_list varchar(100) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT authrt_table_pkey PRIMARY KEY (authrt_se_cd, menu_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].authrt_table IS '권한 테이블';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.authrt_se_cd IS '권한 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.menu_id IS '메뉴 ID';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.authrt_bgng_ymd IS '권한 시작 일자';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.authrt_end_ymd IS '권한 종료 일자';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.mthd_list IS '메소드 리스트';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].authrt_table.mdfr_id IS '수정자 ID';

---- 파일 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].file_table (
	file_id varchar(20) NOT NULL,
	dmn_clsf_id varchar(20) NOT NULL,
	file_nm varchar(100) NOT NULL,
	file_path varchar(512) NOT NULL,
	orgnfl_nm varchar(100) NOT NULL,
	file_extn_nm varchar(20) NULL,
	file_sz numeric(13) NULL,
	file_mime_type varchar(100) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT file_table_pkey PRIMARY KEY (file_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].file_table IS '파일 테이블';
COMMENT ON COLUMN [DBSCHEMA].file_table.file_id IS '파일 ID';
COMMENT ON COLUMN [DBSCHEMA].file_table.dmn_clsf_id IS '도메인 분류 ID';
COMMENT ON COLUMN [DBSCHEMA].file_table.file_nm IS '파일 명';
COMMENT ON COLUMN [DBSCHEMA].file_table.file_path IS '파일 경로';
COMMENT ON COLUMN [DBSCHEMA].file_table.orgnfl_nm IS '원파일 명';
COMMENT ON COLUMN [DBSCHEMA].file_table.file_extn_nm IS '파일 확장자 명';
COMMENT ON COLUMN [DBSCHEMA].file_table.file_sz IS '파일 크기';
COMMENT ON COLUMN [DBSCHEMA].file_table.file_mime_type IS '파일 MIME 유형';
COMMENT ON COLUMN [DBSCHEMA].file_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].file_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].file_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].file_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].file_table.mdfr_id IS '수정자 ID';

---- 사용자 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].user_table (
	user_id varchar(20) NOT NULL,
	user_pw varchar(100) NOT NULL,
	user_emp_no varchar(10) NOT NULL,
	user_nm varchar(40) NOT NULL,
	user_hp_no varchar(20) NULL,
	user_eml varchar(50) NULL,
	user_addr varchar(200) NULL,
	user_brth_ymd varchar(8) NULL,
	user_photo_file_path varchar(255) NULL,
	user_jncmp_ymd varchar(8) NULL,
	user_ofc_telno varchar(13) NULL,
	user_atdc_id int4 NULL,
	user_dept_se_cd varchar(20) NULL,
	user_jbgd_se_cd varchar(20) NULL,
	user_authrt_se_cd varchar(20) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT user_table_pkey PRIMARY KEY (user_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].user_table IS '사용자 테이블';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_id IS '사용자 ID';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_pw IS '사용자 비밀번호';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_emp_no IS '사용자 사원 번호';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_nm IS '사용자 명';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_hp_no IS '사용자 핸드폰 번호';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_eml IS '사용자 이메일';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_addr IS '사용자 주소';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_brth_ymd IS '사용자 출생 일자';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_photo_file_path IS '사용자 사진 파일 경로';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_jncmp_ymd IS '사용자 근태 ID(CAPS ID)';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_atdc_id IS '사용자 입사 일자';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_ofc_telno IS '사용자 사무실 전화번호';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_dept_se_cd IS '사용자 부서 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_jbgd_se_cd IS '사용자 직급 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].user_table.user_authrt_se_cd IS '사용자 권한 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].user_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].user_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].user_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].user_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].user_table.mdfr_id IS '수정자 ID';

---- 결재 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].atrz_table (
	doc_no varchar(10) NOT NULL,
	doc_se_cd varchar(20) NOT NULL,
	drft_ymd varchar(8) NOT NULL,
	drftr_id varchar(20) NOT NULL,
	doc_ttl varchar(100) NOT NULL,
	atrz_data jsonb NULL,
	atrz_stts_se_cd varchar(20) NOT NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT atrz_table_pkey PRIMARY KEY (doc_no)
) TABLESPACE [DBTABLESPACE];
CREATE INDEX IF NOT EXISTS atrz_data_idx ON [DBSCHEMA].atrz_table USING GIN(atrz_data);
COMMENT ON TABLE [DBSCHEMA].atrz_table IS '결재 테이블';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.doc_no IS '문서 번호';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.doc_se_cd IS '문서 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.drft_ymd IS '기안 일';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.drftr_id IS '기안자 ID';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.doc_ttl IS '문서 제목';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.atrz_data IS '결재 데이터';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.atrz_stts_se_cd IS '결재 상태 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].atrz_table.mdfr_id IS '수정자 ID';

---- 결재 의견 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].atrz_opnn_table (
	opnn_id varchar(20) NOT NULL,
	opnn_up_id varchar(20) NULL,
	doc_no varchar(10) NOT NULL,
	opnn_cn varchar(400) NOT NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT atrz_opnn_table_pkey PRIMARY KEY (opnn_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].atrz_opnn_table IS '결재 의견 테이블';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.opnn_id IS '의견 ID';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.opnn_up_id IS '의견 상위 ID';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.doc_no IS '문서 번호';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.opnn_cn IS '의견 내용';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].atrz_opnn_table.mdfr_id IS '수정자 ID';

---- 결재 양식 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].atrz_frm_table (
	doc_se_cd varchar(20) NOT NULL,
	atrz_frm_data jsonb NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT atrz_frm_table_pkey PRIMARY KEY (doc_se_cd)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].atrz_frm_table IS '결재 양식 테이블';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.doc_se_cd IS '문서 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.atrz_frm_data IS '결재 양식 데이터';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].atrz_frm_table.mdfr_id IS '수정자 ID';

---- 근태 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].atdc_table (
	atdc_user_id varchar(20) NOT NULL,
	atdc_ymd varchar(8) NOT NULL,
	atdc_tm varchar(6) NOT NULL,
	atdc_se_cd varchar(20) NOT NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT atdc_table_pkey PRIMARY KEY (atdc_user_id, atdc_ymd, atdc_tm, atdc_se_cd)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].atdc_table IS '근태 테이블';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.atdc_user_id IS '근태 사용자 ID';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.atdc_ymd IS '근태 일자';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.atdc_tm IS '근태 시각';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.atdc_se_cd IS '근태 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].atdc_table.mdfr_id IS '수정자 ID';

CREATE TABLE IF NOT EXISTS [DBSCHEMA].atdc_mdb_table (
	atdc_mdb_data bytea NOT NULL,
	wrt_dt timestamp NOT NULL,
	prsl_dt timestamp NOT NULL
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].atdc_mdb_table IS '근태 MDB 테이블';
COMMENT ON COLUMN [DBSCHEMA].atdc_mdb_table.atdc_mdb_data IS '근태 MDB 자료';
COMMENT ON COLUMN [DBSCHEMA].atdc_mdb_table.wrt_dt IS '작성 일시';
COMMENT ON COLUMN [DBSCHEMA].atdc_mdb_table.prsl_dt IS '열람 일시';

---- 휴가 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].vctn_table (
	vctn_user_id varchar(20) NOT NULL,
	vctn_se_cd varchar(20) NOT NULL,
	vctn_bgng_ymd varchar(8) NOT NULL,
	vctn_end_ymd varchar(8) NOT NULL,
	vctn_use_cnt numeric(6, 3) NOT NULL,
	atrz_doc_no varchar(10) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT vctn_table_pkey PRIMARY KEY (vctn_user_id, vctn_se_cd, vctn_bgng_ymd)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].vctn_table IS '휴가 테이블';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.vctn_user_id IS '휴가 사용자 ID';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.vctn_se_cd IS '휴가 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.vctn_bgng_ymd IS '휴가 시작 일자';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.vctn_end_ymd IS '휴가 종료 일자';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.vctn_use_cnt IS '휴가 사용 수';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.atrz_doc_no IS '결재 문서 번호';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].vctn_table.mdfr_id IS '수정자 ID';

---- 일정 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].schdl_table (
	schdl_id varchar(20) NOT NULL,
	schdl_user_id varchar(20) NOT NULL,
	schdl_se_cd varchar(20) NOT NULL,
	schdl_ttl varchar(100) NOT NULL,
	schdl_cn varchar(500) NULL,
	schdl_bgng_dt timestamp without time zone NOT NULL,
	schdl_end_dt timestamp without time zone NOT NULL,
	schdl_rls_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT schdl_table_pkey PRIMARY KEY (schdl_id)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].schdl_table IS '일정 테이블';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_id IS '일정 ID';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_user_id IS '일정 사용자 ID';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_se_cd IS '일정 구분 코드';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_ttl IS '일정 제목';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_cn IS '일정 내용';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_bgng_dt IS '일정 시작 일시';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_end_dt IS '일정 종료 일시';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.schdl_rls_yn IS '일정 공개 여부';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].schdl_table.mdfr_id IS '수정자 ID';

---- 일정 테이블 ----
CREATE TABLE IF NOT EXISTS [DBSCHEMA].hldy_table (
	hldy_ymd varchar(8) NOT NULL,
	hldy_ymd_sn int4 NOT NULL,
	hldy_nm varchar(50) NOT NULL,
	hldy_rmrk varchar(400) NULL,
	use_yn bpchar(1) DEFAULT 'Y'::bpchar NOT NULL,
	reg_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	rgtr_id varchar(20) NOT NULL,
	mdfcn_dt timestamp DEFAULT CURRENT_TIMESTAMP::timestamp(0) without time zone NULL,
	mdfr_id varchar(20) NOT NULL,
	CONSTRAINT hldy_table_pkey PRIMARY KEY (hldy_ymd, hldy_ymd_sn)
) TABLESPACE [DBTABLESPACE];
COMMENT ON TABLE [DBSCHEMA].hldy_table IS '휴일 테이블';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.hldy_ymd IS '휴일 일자';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.hldy_ymd_sn IS '휴일 일자 순번';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.hldy_nm IS '휴일 명';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.hldy_rmrk IS '휴일 비고';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.use_yn IS '사용 여부';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.reg_dt IS '등록 일시';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.rgtr_id IS '등록자 ID';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.mdfcn_dt IS '수정 일시';
COMMENT ON COLUMN [DBSCHEMA].hldy_table.mdfr_id IS '수정자 ID';
*/
