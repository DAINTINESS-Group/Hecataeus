DECLARE

	my_id EMP.E_ID%TYPE;

	NMAX	NUMBER(7);
  	NMIN	NUMBER(7);

  	ID	NUMBER(7);
	TITLE 	VARCHAR(20);

	type r_cursor is REF CURSOR;

	my_emp EMP%ROWTYPE;

	CURSOR my_cur IS
	SELECT * FROM EMP
	FOR UPDATE;

BEGIN

--	INSERT INTO EMP(E_ID) SELECT P_ID FROM PRJS;
--	INSERT INTO PRJS (SELECT E_ID, E_TITLE, E_SAL FROM EMP where e_title='temp');
--	INSERT INTO EMP(E_ID,E_TITLE) ((SELECT P_ID, p_name FROM prjs where p_name='temp'));
--	INSERT INTO PRJS SELECT E_ID, E_TITLE, E_SAL FROM EMP where e_title='temp';
--	INSERT INTO PRJS VALUES (45,'yty',98);


	SELECT E_ID,E_SAL
	INTO NMAX, NMIN
	FROM EMP
	WHERE E_SAL>1200;

	OPEN r_cursor 
	FOR SELECT E_ID,E_TITLE FROM EMP;

	FETCH r_cursor INTO ID,TITLE;

	OPEN r_cursor 
	FOR SELECT E_ID FROM EMP;	

	FETCH r_cursor INTO ID;

	SELECT E_ID,'ROUND(T.SYDEL*T.NMIN,0)'
	FROM EMP;

END;
