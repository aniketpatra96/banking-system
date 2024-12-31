create database bankdb;
use bankdb;

create table savingsaccount(
  accNo bigint auto_increment primary key,
  fname varchar(50) not null,
  lname varchar(50) not null,
  balance bigint check(balance >= 1000),
  nominee varchar(100) not null,
  openingdate datetime not null,
  closingdate datetime not null
)auto_increment = 100000;

ALTER TABLE savingsaccount
CHANGE COLUMN accNo saccNo bigint;

ALTER TABLE savingsaccount
CHANGE COLUMN openingdate savingsopeningdate datetime;

ALTER TABLE savingsaccount
MODIFY COLUMN closingdate datetime NULL;

ALTER TABLE savingsaccount
MODIFY COLUMN balance DECIMAL(10,2) check(balance >= 1000.00);

ALTER TABLE savingsaccount
ADD COLUMN gender char NOT NULL;


ALTER TABLE savingsaccount MODIFY saccNo BIGINT AUTO_INCREMENT;



insert into savingsaccount (fname,lname,gender,balance,nominee,savingsopeningdate) 
values ('Nana','Patekar','M',50000.00,'Venkat ramanna',CURRENT_TIMESTAMP);

select * from savingsaccount;
desc savingsaccount;

create table fixeddeposit(
  fdaccNo bigint auto_increment primary key,
  amount decimal(15,2) check(amount >= 200000.00),
  nominee varchar(100) not null,
  fixedopeningdate datetime not null,
  termperiod decimal(10,2) not null check(termperiod >= 2.5),
  interestrate decimal(10,2) not null,
  saccNo bigint not null,
  foreign key (saccNo) references savingsaccount(saccNo)
)auto_increment = 5500000;

desc fixeddeposit;

insert into fixeddeposit(amount,nominee,fixedopeningdate,termperiod,interestrate,saccNo) 
values (250000.00,'Prithwiraj Chauhan',CURRENT_TIMESTAMP,5,7.15,100021);

select * from fixeddeposit;
select * from savingsaccount s inner join fixeddeposit f where s.saccNo = f.saccNo;

ALTER TABLE fixeddeposit
CHANGE COLUMN openingdate fixedopeningdate datetime;

ALTER TABLE fixeddeposit
MODIFY COLUMN termperiod DECIMAL(10,2) check(termperiod >= 2.5);

ALTER TABLE fixeddeposit
ADD CONSTRAINT accNoForeignKey
FOREIGN KEY (saccNo) REFERENCES savingsaccount (saccNo)
ON DELETE CASCADE
ON UPDATE CASCADE;


SELECT 
    TC.TABLE_NAME AS TableName,
    TC.CONSTRAINT_NAME AS ConstraintName,
    TC.CONSTRAINT_TYPE AS ConstraintType,
    KCU.COLUMN_NAME AS ColumnName,
    KCU.REFERENCED_TABLE_NAME AS ReferencedTableName,
    KCU.REFERENCED_COLUMN_NAME AS ReferencedColumnName
FROM 
    INFORMATION_SCHEMA.TABLE_CONSTRAINTS AS TC
JOIN 
    INFORMATION_SCHEMA.KEY_COLUMN_USAGE AS KCU
    ON TC.CONSTRAINT_NAME = KCU.CONSTRAINT_NAME
    AND TC.TABLE_NAME = KCU.TABLE_NAME
WHERE 
    TC.TABLE_SCHEMA = 'savingsaccount'
LIMIT 0, 1000;

CREATE TABLE LOAN_TYPES(
  LOAN_TYPE INT PRIMARY KEY,
  LOAN_NAME VARCHAR(50) NOT NULL,
  INTEREST_RATE DECIMAL(5,2) NOT NULL
);

CREATE TABLE LOAN_ACCOUNT(
  LOAN_NO bigint auto_increment PRIMARY KEY,
  AMOUNT DECIMAL(10,2) NOT NULL,
  TERM_PERIOD DECIMAL(5,2) NOT NULL,
  LOAN_DATE DATETIME NOT NULL,
  LOAN_TYPE INT NOT NULL,
  saccNo bigint NOT NULL,
  foreign key (saccNo) references savingsaccount(saccNo),
  foreign key (LOAN_TYPE) references LOAN_TYPES(LOAN_TYPE)
)auto_increment = 10000000;

INSERT INTO LOAN_ACCOUNT(AMOUNT,TERM_PERIOD,LOAN_DATE,LOAN_TYPE,saccNo)
VALUES(250000,7,CURRENT_TIMESTAMP,4,100000);

INSERT INTO LOAN_TYPES (LOAN_TYPE,LOAN_NAME,INTEREST_RATE)
VALUES
   (1,'HOUSE LOAN',7.5),
   (2,'CAR LOAN',9.3),
   (3,'BUSINESS LOAN',18),
   (4,'EDUCATION LOAN',12.5),
   (5,'TRAVEL LOAN',9.5);
   
SELECT * FROM LOAN_ACCOUNT;
SELECT * FROM LOAN_TYPES;

SELECT * FROM LOAN_ACCOUNT L NATURAL JOIN LOAN_TYPES T WHERE L.LOAN_TYPE = T.LOAN_TYPE;

SELECT *
FROM LOAN_ACCOUNT L
JOIN LOAN_TYPES T ON L.LOAN_TYPE = T.LOAN_TYPE
JOIN SAVINGSACCOUNT S ON L.saccNo = S.saccNo;

SELECT * FROM LOAN_ACCOUNT ORDER BY LOAN_NO DESC LIMIT 1;
