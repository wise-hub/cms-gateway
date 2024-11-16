create or replace PACKAGE PKG_META AS
    FUNCTION GET_USER_METADATA(TOKEN IN VARCHAR2) RETURN TYP_METADATA;
END PKG_META;


create or replace PACKAGE BODY PKG_META AS
    FUNCTION GET_USER_METADATA(TOKEN IN VARCHAR2) RETURN TYP_METADATA IS
        user_metadata TYP_METADATA;
        token_exists NUMBER;
    BEGIN
        SELECT COUNT(*)
        INTO token_exists
        FROM TOKENS3
        WHERE token = TOKEN;

        IF token_exists = 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Token not found');
        END IF;

        SELECT TYP_METADATA(
                   userid => 12345,
                   userroles => TYP_USER_ROLES('Admin', 'User')
               )
        INTO user_metadata
        FROM DUAL
        WHERE TOKEN = TOKEN;

        RETURN user_metadata;
    END GET_USER_METADATA;
END PKG_META;


--------------------------------------------------------
--  DDL for Table GATEWAY_ROUTES
--------------------------------------------------------

  CREATE TABLE "MY_USER"."GATEWAY_ROUTES"
   (	"ID" VARCHAR2(50 BYTE),
	"URI" VARCHAR2(255 BYTE),
	"PREDICATES" VARCHAR2(255 BYTE),
	"FILTERS" VARCHAR2(255 BYTE)
   ) ;
--------------------------------------------------------
--  DDL for Index SYS_C0017290
--------------------------------------------------------

  CREATE UNIQUE INDEX "MY_USER"."SYS_C0017290" ON "MY_USER"."GATEWAY_ROUTES" ("ID")  ;
--------------------------------------------------------
--  Constraints for Table GATEWAY_ROUTES
--------------------------------------------------------

  ALTER TABLE "MY_USER"."GATEWAY_ROUTES" MODIFY ("URI" NOT NULL ENABLE);
  ALTER TABLE "MY_USER"."GATEWAY_ROUTES" MODIFY ("PREDICATES" NOT NULL ENABLE);
  ALTER TABLE "MY_USER"."GATEWAY_ROUTES" ADD PRIMARY KEY ("ID");

--------------------------------------------------------
--  DDL for Table TOKENS3
--------------------------------------------------------

  CREATE TABLE "MY_USER"."TOKENS3"
   (	"ID" NUMBER,
	"TOKEN" VARCHAR2(255 BYTE),
	"USER_ID" NUMBER
   ) ;
--------------------------------------------------------
--  DDL for Index SYS_C0017281
--------------------------------------------------------

  CREATE UNIQUE INDEX "MY_USER"."SYS_C0017281" ON "MY_USER"."TOKENS3" ("ID") ;
--------------------------------------------------------
--  DDL for Index IDX_TOKEN
--------------------------------------------------------

  CREATE INDEX "MY_USER"."IDX_TOKEN" ON "MY_USER"."TOKENS3" ("TOKEN") ;
--------------------------------------------------------
--  Constraints for Table TOKENS3
--------------------------------------------------------

  ALTER TABLE "MY_USER"."TOKENS3" MODIFY ("TOKEN" NOT NULL ENABLE);
  ALTER TABLE "MY_USER"."TOKENS3" MODIFY ("USER_ID" NOT NULL ENABLE);
  ALTER TABLE "MY_USER"."TOKENS3" ADD PRIMARY KEY ("ID");







