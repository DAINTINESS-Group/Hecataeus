--Q1
SELECT *
FROM 	EMP;



--View 1
CREATE VIEW EMP_PRJS AS
SELECT E.E_ID, E.E_NAME, E.E_SAL, 
            P.P_ID, P.P_NAME, P_BUDGET
FROM EMP E, PRJS P, WORKS W
WHERE W.E_ID = E.E_ID 
AND W.P_ID = P.P_ID;



--Q2: 

SELECT P_ID, P_NAME, P_BUDGET, 
             SUM(E_SAL) AS P_EXPENSES
FROM Emp_prjs
GROUP BY P_ID, P_NAME, P_BUDGET;

