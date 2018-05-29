INSERT INTO `acct` (`bank_id`, `begin_balance`, `begin_date`, `fid`, `name`, `organization`, `type`, `version`) VALUES ('071904779',63865.83,'2018-04-01','1402','Joint Checking','U.S. Bank','CHECKING',0);
INSERT INTO `acct` (`bank_id`, `begin_balance`, `begin_date`, `fid`, `name`, `organization`, `type`, `version`) VALUES (NULL,-417.18,'2018-04-01','5959','Planetary (BoA)','Bank of America','CC',0);
INSERT INTO `acct` (`bank_id`, `begin_balance`, `begin_date`, `fid`, `name`, `organization`, `type`, `version`) VALUES (NULL,-960.41,'2018-04-01','5959','Spirit (BoA)','Bank of America','CC',0);
INSERT INTO `acct` (`bank_id`, `begin_balance`, `begin_date`, `fid`, `name`, `organization`, `type`, `version`) VALUES (NULL,-3127.67,'2018-04-01','10898','United (Chase)','B1','CC',0);
INSERT INTO `acct` (`bank_id`, `begin_balance`, `begin_date`, `fid`, `name`, `organization`, `type`, `version`) VALUES (NULL,-167.24,'2018-04-01','8734','BancorpSouth','BancorpSouth','CC',0);

INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('2018-04-01','154548628101',0,(SELECT id FROM acct WHERE name = 'Joint Checking'));
INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('2018-04-01','4400669867048688',0,(SELECT id FROM acct WHERE name = 'Planetary (BoA)'));
INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('2018-04-01','5466331113878784',0,(SELECT id FROM acct WHERE name = 'Spirit (BoA)'));
INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('2018-04-01','4388576031903035',0,(SELECT id FROM acct WHERE name = 'United (Chase)'));
INSERT INTO `acct_nbr` (`eff_date`, `number`, `version`, `acct_id`) VALUES ('2018-04-01','5491087740200722',0,(SELECT id FROM acct WHERE name = 'BancorpSouth'));

INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES ('P.O. Box 851001',NULL,'Dallas','Bank of America','Planetary (BoA)','4400669867048688','800-421-2110','TX',0,'75285-1001');
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES ('P.O. Box 851001',NULL,'Dallas','Bank of America','Spirit (BoA)','5466331113878784','800-421-2110','TX',0,'75285-1001');
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES ('PO Box 1423',NULL,'Charlotte','Chase Card Services','United (Chase)','4388576031903035','800-537-7783','NC',0,'28201-1423');
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES ('PO Box 4390',NULL,'Tupelo','BancorpSouth',NULL,'5491087740200722','800-844-2723','MS',0,'38803-4390');
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'State Farm','Kerry''s car','later',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'State Farm','Norman''s car','P13 7153-E13-13F',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'ComCast',NULL,'later',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'Verizon',NULL,'later',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'Nicor',NULL,'later',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'ComEd',NULL,'later',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'Village of Lisle','Lisle utitilies','later',NULL,NULL,0,NULL);
INSERT INTO `payee` (`address1`, `address2`, `city`, `name`, `nickname`, `number`, `phone_number`, `state`, `version`, `zip_code`) VALUES (NULL,NULL,NULL,'DuPage County Public Works',NULL,'later',NULL,NULL,0,NULL);
