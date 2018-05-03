INSERT INTO acct (id,version,name,begin_date,begin_balance,type) VALUES (100000,0,'Bank of Foo','2018-01-01',0.00,'CHECKING');
INSERT INTO acct (id,version,name,begin_date,begin_balance,type) VALUES (200000,0,'Bar Banks','2018-01-01',0.00,'CC');
INSERT INTO acct (id,version,name,begin_date,begin_balance,type) VALUES (300000,0,'Baz Financial','2018-01-01',0.00,'CC');

INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (101000,0,100000,'DEBIT','2018-01-05',-10,null,'McDonalds',null);
INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (102000,0,100000,'CHECK','2018-01-10',-20,'1234','Jewel Osco',null);
INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (103000,0,100000,'DEBIT','2018-01-15',-30,null,'ATM','1st and Main');
INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (104000,0,100000,'CREDIT','2018-01-20',100,null,'Paycheck',null);
INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (105000,0,100000,'DEBIT','2018-01-25',-40,null,'Jewel Osco',null);

INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (201000,0,200000,'DEBIT','2018-01-05',-100,null,'Amazon',null);
INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (202000,0,200000,'FEE','2018-01-15',-1,null,'Late fee',null);
INSERT INTO tran (id,version,acct_id,type,post_date,amount,check_number,name,memo) VALUES (203000,0,200000,'PAYMENT','2018-01-25',101,null,'Thank you',null);

INSERT INTO acct_nbr (id,version,acct_id,number,eff_date) VALUES (110000,0,100000,'12345678901','2018-01-01');

INSERT INTO acct_nbr (id,version,acct_id,number,eff_date) VALUES (210000,0,200000,'1234-5678-9012-3456','2018-01-01');
INSERT INTO acct_nbr (id,version,acct_id,number,eff_date) VALUES (220000,0,200000,'1234-5678-8765-4321','2018-01-16');
INSERT INTO acct_nbr (id,version,acct_id,number,eff_date) VALUES (230000,0,200000,'1234-5678-0123-4567','2018-01-26');

INSERT INTO acct_nbr (id,version,acct_id,number,eff_date) VALUES (310000,0,300000,'9012-3456-7890-1234','2018-01-01');

INSERT INTO payee (id,version,name) values (600000,0,'State Power and Light');
INSERT INTO payee (id,version,name) values (700000,0,'Mobile Phones');
INSERT INTO payee (id,version,name) values (800000,0,'National Gas');
INSERT INTO payee (id,version,name) values (900000,0,'Village of Wherever');