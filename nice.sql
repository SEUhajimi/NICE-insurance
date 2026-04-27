
CREATE DATABASE  IF NOT EXISTS `nice` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `nice`;
-- MySQL dump 10.13  Distrib 8.0.44, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: nice
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `hjb_customer`
--

DROP TABLE IF EXISTS `hjb_customer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_customer` (
  `CUST_ID` int NOT NULL COMMENT 'Customer id number',
  `FNAME` varchar(20) NOT NULL COMMENT 'Customer first name',
  `LNAME` varchar(20) NOT NULL COMMENT 'Customer last name',
  `Gender` char(1) DEFAULT NULL COMMENT 'F, M',
  `Marital_status` char(1) DEFAULT NULL COMMENT 'M, S, W',
  `Cust_Type` char(1) NOT NULL COMMENT 'A for auto, H for home, B for Both',
  `Addr_Street` varchar(30) NOT NULL COMMENT 'address street',
  `Addr_City` varchar(30) NOT NULL COMMENT 'address: city',
  `Addr_State` varchar(30) NOT NULL COMMENT 'address state',
  `Zipcode` varchar(10) NOT NULL COMMENT 'address zipcode',
  PRIMARY KEY (`CUST_ID`),
  CONSTRAINT `Cust_type_CK` CHECK ((`Cust_Type` in (_utf8mb4'A',_utf8mb4'B',_utf8mb4'H'))),
  CONSTRAINT `Gender_CK` CHECK ((`Gender` in (_utf8mb4'F',_utf8mb4'M'))),
  CONSTRAINT `Marital_Status_CK` CHECK ((`Marital_status` in (_utf8mb4'M',_utf8mb4'S',_utf8mb4'W')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_customer`
--

LOCK TABLES `hjb_customer` WRITE;
/*!40000 ALTER TABLE `hjb_customer` DISABLE KEYS */;
INSERT INTO `hjb_customer` VALUES (1,'James','Smith','M','M','B','123 Maple St','New York','NY','10001'),(2,'Mary','Johnson','F','S','A','456 Oak Ave','Los Angeles','CA','90001'),(3,'Robert','Williams','M','M','H','789 Pine Rd','Chicago','IL','60601'),(4,'Patricia','Brown','F','W','B','321 Elm St','Houston','TX','77001'),(5,'Michael','Jones','M','S','A','654 Cedar Ln','Phoenix','AZ','85001'),(6,'Jennifer','Garcia','F','M','H','987 Birch Dr','Philadelphia','PA','19101'),(7,'William','Miller','M','M','B','159 Walnut St','San Antonio','TX','78201'),(8,'Elizabeth','Davis','F','S','A','753 Chestnut St','San Diego','CA','92101'),(9,'David','Rodriguez','M','M','H','852 Aspen Ct','Dallas','TX','75201'),(10,'Barbara','Martinez','F','W','B','951 Willow Way','San Jose','CA','95101'),(11,'Richard','Hernandez','M','S','A','147 Cherry St','Austin','TX','73301'),(12,'Susan','Lopez','F','M','H','258 Hickory Ave','Jacksonville','FL','32099'),(13,'Joseph','Gonzalez','M','M','B','369 Poplar Rd','Fort Worth','TX','76101'),(14,'Jessica','Wilson','F','S','A','123 Ironwood Dr','Columbus','OH','43085'),(15,'Thomas','Anderson','M','M','H','456 Spruce St','Charlotte','NC','28201'),(16,'Sarah','Thomas','F','W','B','789 Magnolia Ln','San Francisco','CA','94101'),(17,'Charles','Taylor','M','S','A','321 Sycamore St','Indianapolis','IN','46201'),(18,'Karen','Moore','F','M','H','654 Juniper Dr','Seattle','WA','98101'),(19,'Christopher','Jackson','M','M','B','987 Redwood Ct','Denver','CO','80201'),(20,'Nancy','Martin','F','S','A','159 Palm Ave','Washington','DC','20001'),(21,'Daniel','Lee','M','M','H','753 Douglas Fir Ln','Boston','MA','02101'),(22,'Lisa','Perez','F','W','B','852 Hemlock St','El Paso','TX','79901'),(23,'Matthew','Thompson','M','S','A','951 Cypress Dr','Nashville','TN','37201'),(24,'Betty','White','F','M','H','147 Banyan St','Detroit','MI','48201'),(25,'Anthony','Harris','M','M','B','258 Laurel Ave','Oklahoma City','OK','73101'),(26,'Sandra','Sanchez','F','S','A','369 Hawthorn Rd','Portland','OR','97201'),(27,'Mark','Clark','M','M','H','123 Ginkgo Ln','Las Vegas','NV','89101'),(28,'Ashley','Ramirez','F','W','B','456 Dogwood St','Memphis','TN','38101'),(29,'Steven','Lewis','M','S','A','789 Boxwood Dr','Louisville','KY','40201'),(30,'Emily','Robinson','F','M','H','321 Alder Ct','Baltimore','MD','21201');
/*!40000 ALTER TABLE `hjb_customer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_policy` (SUPERTYPE for AutoPolicy and HomePolicy)
--

DROP TABLE IF EXISTS `hjb_policy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_policy` (
  `POLICY_ID` int NOT NULL AUTO_INCREMENT COMMENT 'Policy ID (shared by auto and home subtypes)',
  `SDATE` date NOT NULL COMMENT 'Policy start date',
  `EDATE` date NOT NULL COMMENT 'Policy end date',
  `Amount` decimal(10,2) NOT NULL COMMENT 'Insurance premium amount',
  `Status` char(1) NOT NULL COMMENT 'C for current, E for expired',
  `Policy_Type` char(1) NOT NULL COMMENT 'A for Auto, H for Home',
  `HJB_CUSTOMER_CUST_ID` int NOT NULL COMMENT 'Customer id (mandatory)',
  PRIMARY KEY (`POLICY_ID`),
  KEY `HJB_POLICY_HJB_CUSTOMER_FK` (`HJB_CUSTOMER_CUST_ID`),
  CONSTRAINT `HJB_POLICY_HJB_CUSTOMER_FK` FOREIGN KEY (`HJB_CUSTOMER_CUST_ID`) REFERENCES `hjb_customer` (`CUST_ID`),
  CONSTRAINT `Policy_Type_CK` CHECK ((`Policy_Type` in (_utf8mb4'A',_utf8mb4'H'))),
  CONSTRAINT `Policy_Status_CK` CHECK ((`Status` in (_utf8mb4'C',_utf8mb4'E'))),
  CONSTRAINT `Policy_Amount_CK` CHECK ((`Amount` >= 0.01)),
  CONSTRAINT `HJB_POLICY_CK_SEDATE` CHECK ((`EDATE` > `SDATE`))
) ENGINE=InnoDB AUTO_INCREMENT=231 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_policy`
--

LOCK TABLES `hjb_policy` WRITE;
/*!40000 ALTER TABLE `hjb_policy` DISABLE KEYS */;
INSERT INTO `hjb_policy` VALUES
  (101,'2025-01-10','2026-01-10',850.00,'C','A',1),
  (102,'2025-02-15','2026-02-15',1200.50,'C','A',2),
  (103,'2024-06-01','2025-06-01',980.00,'E','A',4),
  (104,'2025-03-20','2026-03-20',1100.00,'C','A',5),
  (105,'2025-01-05','2026-01-05',750.00,'C','A',7),
  (106,'2024-12-10','2025-12-10',1350.25,'C','A',8),
  (107,'2025-04-01','2026-04-01',890.00,'C','A',10),
  (108,'2025-02-28','2026-02-28',1050.00,'C','A',11),
  (109,'2024-09-15','2025-09-15',1150.00,'E','A',13),
  (110,'2025-01-25','2026-01-25',920.75,'C','A',14),
  (111,'2025-03-05','2026-03-05',1280.00,'C','A',16),
  (112,'2025-02-14','2026-02-14',990.00,'C','A',17),
  (113,'2024-11-01','2025-11-01',1100.50,'C','A',19),
  (114,'2025-05-15','2026-05-15',870.00,'C','A',20),
  (115,'2025-01-30','2026-01-30',1400.00,'C','A',22),
  (116,'2025-03-22','2026-03-22',1050.20,'C','A',23),
  (117,'2024-07-18','2025-07-18',950.00,'E','A',25),
  (118,'2025-02-08','2026-02-08',1180.00,'C','A',26),
  (119,'2025-04-12','2026-04-12',1250.00,'C','A',28),
  (120,'2025-01-15','2026-01-15',900.00,'C','A',29),
  (121,'2025-02-01','2026-02-01',880.00,'C','A',1),
  (122,'2025-03-10','2026-03-10',1120.00,'C','A',2),
  (123,'2025-01-20','2026-01-20',1050.00,'C','A',4),
  (124,'2025-04-05','2026-04-05',960.00,'C','A',5),
  (125,'2025-05-01','2026-05-01',1300.00,'C','A',7),
  (126,'2025-02-18','2026-02-18',1080.00,'C','A',8),
  (127,'2025-03-25','2026-03-25',1150.00,'C','A',10),
  (128,'2025-01-12','2026-01-12',940.00,'C','A',11),
  (129,'2025-04-22','2026-04-22',1220.00,'C','A',13),
  (130,'2025-02-05','2026-02-05',1000.00,'C','A',14),
  (201,'2025-01-15','2026-01-15',2500.00,'C','H',3),
  (202,'2025-02-10','2026-02-10',3100.50,'C','H',4),
  (203,'2024-05-20','2025-05-20',1800.00,'E','H',6),
  (204,'2025-03-01','2026-03-01',4200.00,'C','H',7),
  (205,'2025-01-01','2026-01-01',2750.00,'C','H',9),
  (206,'2024-11-15','2025-11-15',3300.25,'C','H',10),
  (207,'2025-04-12','2026-04-12',2900.00,'C','H',12),
  (208,'2025-02-28','2026-02-28',3600.00,'C','H',13),
  (209,'2024-08-05','2025-08-05',2100.00,'E','H',15),
  (210,'2025-01-20','2026-01-20',4500.75,'C','H',16),
  (211,'2025-03-15','2026-03-15',3800.00,'C','H',18),
  (212,'2025-02-14','2026-02-14',3200.00,'C','H',19),
  (213,'2024-12-01','2025-12-01',2400.50,'C','H',21),
  (214,'2025-05-10','2026-05-10',4100.00,'C','H',22),
  (215,'2025-01-30','2026-01-30',2950.00,'C','H',24),
  (216,'2025-03-22','2026-03-22',3700.20,'C','H',25),
  (217,'2024-06-18','2025-06-18',1950.00,'E','H',27),
  (218,'2025-02-05','2026-02-05',4400.00,'C','H',28),
  (219,'2025-04-01','2026-04-01',3150.00,'C','H',30),
  (220,'2025-01-10','2026-01-10',2800.00,'C','H',3),
  (221,'2025-02-15','2026-02-15',3500.00,'C','H',4),
  (222,'2025-03-10','2026-03-10',3900.00,'C','H',6),
  (223,'2025-01-25','2026-01-25',2600.00,'C','H',7),
  (224,'2025-04-05','2026-04-05',4300.00,'C','H',9),
  (225,'2025-05-01','2026-05-01',3200.00,'C','H',10),
  (226,'2025-02-20','2026-02-20',3400.00,'C','H',12),
  (227,'2025-03-30','2026-03-30',4100.00,'C','H',13),
  (228,'2025-01-05','2026-01-05',2700.00,'C','H',15),
  (229,'2025-04-20','2026-04-20',4600.00,'C','H',16),
  (230,'2025-02-12','2026-02-12',3300.00,'C','H',18);
/*!40000 ALTER TABLE `hjb_policy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_autopolicy` (SUBTYPE of hjb_policy)
--

DROP TABLE IF EXISTS `hjb_autopolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_autopolicy` (
  `AP_ID` int NOT NULL COMMENT 'Auto policy ID (equals POLICY_ID in hjb_policy)',
  PRIMARY KEY (`AP_ID`),
  CONSTRAINT `HJB_AUTOPOLICY_HJB_POLICY_FK` FOREIGN KEY (`AP_ID`) REFERENCES `hjb_policy` (`POLICY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_autopolicy`
--

LOCK TABLES `hjb_autopolicy` WRITE;
/*!40000 ALTER TABLE `hjb_autopolicy` DISABLE KEYS */;
INSERT INTO `hjb_autopolicy` VALUES (101),(102),(103),(104),(105),(106),(107),(108),(109),(110),(111),(112),(113),(114),(115),(116),(117),(118),(119),(120),(121),(122),(123),(124),(125),(126),(127),(128),(129),(130);
/*!40000 ALTER TABLE `hjb_autopolicy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_homepolicy` (SUBTYPE of hjb_policy)
--

DROP TABLE IF EXISTS `hjb_homepolicy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_homepolicy` (
  `HP_ID` int NOT NULL COMMENT 'Home policy ID (equals POLICY_ID in hjb_policy)',
  PRIMARY KEY (`HP_ID`),
  CONSTRAINT `HJB_HOMEPOLICY_HJB_POLICY_FK` FOREIGN KEY (`HP_ID`) REFERENCES `hjb_policy` (`POLICY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_homepolicy`
--

LOCK TABLES `hjb_homepolicy` WRITE;
/*!40000 ALTER TABLE `hjb_homepolicy` DISABLE KEYS */;
INSERT INTO `hjb_homepolicy` VALUES (201),(202),(203),(204),(205),(206),(207),(208),(209),(210),(211),(212),(213),(214),(215),(216),(217),(218),(219),(220),(221),(222),(223),(224),(225),(226),(227),(228),(229),(230);
/*!40000 ALTER TABLE `hjb_homepolicy` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_vehicle`
--

DROP TABLE IF EXISTS `hjb_vehicle`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_vehicle` (
  `VIN` varchar(17) NOT NULL COMMENT 'Vehicle identification number',
  `MMY` varchar(50) NOT NULL COMMENT 'vehicle make-model-year',
  `Status` char(1) NOT NULL COMMENT 'L:leased F: financed O:owned',
  `HJB_AUTOPOLICY_AP_ID` int NOT NULL COMMENT 'Auto policy ID (mandatory)',
  PRIMARY KEY (`VIN`),
  KEY `HJB_VEHICLE_HJB_AUTOPOLICY_FK` (`HJB_AUTOPOLICY_AP_ID`),
  CONSTRAINT `HJB_VEHICLE_HJB_AUTOPOLICY_FK` FOREIGN KEY (`HJB_AUTOPOLICY_AP_ID`) REFERENCES `hjb_autopolicy` (`AP_ID`),
  CONSTRAINT `Vehicle_status_CK` CHECK ((`Status` in (_utf8mb4'F',_utf8mb4'L',_utf8mb4'O'))),
  CONSTRAINT `VIN_Length_CK` CHECK ((char_length(`VIN`) = 17))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_vehicle`
--

LOCK TABLES `hjb_vehicle` WRITE;
/*!40000 ALTER TABLE `hjb_vehicle` DISABLE KEYS */;
INSERT INTO `hjb_vehicle` VALUES ('VIN10000000000001','2022 Toyota Camry','O',101),('VIN10000000000002','2021 Honda Accord','F',102),('VIN10000000000003','2020 Ford F-150','O',103),('VIN10000000000004','2023 Tesla Model 3','L',104),('VIN10000000000005','2019 Chevrolet Malibu','O',105),('VIN10000000000006','2022 BMW X5','F',106),('VIN10000000000007','2021 Audi A4','L',107),('VIN10000000000008','2020 Nissan Altima','O',108),('VIN10000000000009','2023 Jeep Grand Cherokee','F',109),('VIN10000000000010','2022 Subaru Outback','O',110),('VIN10000000000011','2021 Mercedes-Benz C300','L',111),('VIN10000000000012','2018 Volkswagen Jetta','O',112),('VIN10000000000013','2022 Hyundai Tucson','F',113),('VIN10000000000014','2023 Kia Telluride','O',114),('VIN10000000000015','2021 Lexus RX 350','F',115),('VIN10000000000016','2020 Mazda CX-5','O',116),('VIN10000000000017','2022 Porsche Macan','L',117),('VIN10000000000018','2021 Dodge Ram 1500','O',118),('VIN10000000000019','2023 Rivian R1S','F',119),('VIN10000000000020','2022 Volvo XC90','L',120),('VIN10000000000021','2020 Chrysler 300','O',121),('VIN10000000000022','2021 Cadillac Escalade','F',122),('VIN10000000000023','2019 Buick Enclave','O',123),('VIN10000000000024','2022 GMC Sierra','L',124),('VIN10000000000025','2023 Acura MDX','O',125),('VIN10000000000026','2021 Infiniti Q50','F',126),('VIN10000000000027','2020 Lincoln Navigator','L',127),('VIN10000000000028','2022 Land Rover Defender','O',128),('VIN10000000000029','2023 Ford Mustang','F',129),('VIN10000000000030','2021 Toyota RAV4','O',130);
/*!40000 ALTER TABLE `hjb_vehicle` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_driver`
-- 1:M with hjb_vehicle: one vehicle can have many drivers; each driver belongs to one vehicle
--

DROP TABLE IF EXISTS `hjb_driver`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_driver` (
  `Driver_License` varchar(20) NOT NULL COMMENT 'Driver license number',
  `FNAME` varchar(20) NOT NULL COMMENT 'First name of driver',
  `LNAME` varchar(20) NOT NULL COMMENT 'Last name of driver',
  `Birthday` date NOT NULL COMMENT 'birthday to derive age',
  `HJB_VEHICLE_VIN` varchar(17) NOT NULL COMMENT 'Vehicle VIN (mandatory - 1:M relation)',
  PRIMARY KEY (`Driver_License`),
  KEY `HJB_DRIVER_HJB_VEHICLE_FK` (`HJB_VEHICLE_VIN`),
  CONSTRAINT `HJB_DRIVER_HJB_VEHICLE_FK` FOREIGN KEY (`HJB_VEHICLE_VIN`) REFERENCES `hjb_vehicle` (`VIN`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_driver`
--

LOCK TABLES `hjb_driver` WRITE;
/*!40000 ALTER TABLE `hjb_driver` DISABLE KEYS */;
INSERT INTO `hjb_driver` VALUES ('LIC001','James','Smith','1985-05-12','VIN10000000000001'),('LIC002','Mary','Johnson','1990-08-22','VIN10000000000002'),('LIC003','Robert','Williams','1978-11-05','VIN10000000000003'),('LIC004','Patricia','Brown','1992-03-30','VIN10000000000004'),('LIC005','Michael','Jones','1982-07-15','VIN10000000000005'),('LIC006','Jennifer','Garcia','1988-12-01','VIN10000000000006'),('LIC007','William','Miller','1995-04-18','VIN10000000000007'),('LIC008','Elizabeth','Davis','1980-09-25','VIN10000000000008'),('LIC009','David','Rodriguez','1987-01-10','VIN10000000000009'),('LIC010','Barbara','Martinez','1991-06-20','VIN10000000000010'),('LIC011','Richard','Hernandez','1984-03-15','VIN10000000000011'),('LIC012','Susan','Lopez','1975-10-12','VIN10000000000012'),('LIC013','Joseph','Gonzalez','1993-02-28','VIN10000000000013'),('LIC014','Jessica','Wilson','1989-08-05','VIN10000000000014'),('LIC015','Thomas','Anderson','1981-12-20','VIN10000000000015'),('LIC016','Sarah','Thomas','1994-05-15','VIN10000000000016'),('LIC017','Charles','Taylor','1986-01-30','VIN10000000000017'),('LIC018','Karen','Moore','1979-04-14','VIN10000000000018'),('LIC019','Christopher','Jackson','1992-11-11','VIN10000000000019'),('LIC020','Nancy','Martin','1983-06-01','VIN10000000000020'),('LIC021','Daniel','Lee','1990-02-14','VIN10000000000021'),('LIC022','Lisa','Perez','1987-08-30','VIN10000000000022'),('LIC023','Matthew','Thompson','1995-12-25','VIN10000000000023'),('LIC024','Betty','White','1982-10-15','VIN10000000000024'),('LIC025','Anthony','Harris','1988-03-12','VIN10000000000025'),('LIC026','Sandra','Sanchez','1991-11-19','VIN10000000000026'),('LIC027','Mark','Clark','1984-07-07','VIN10000000000027'),('LIC028','Ashley','Ramirez','1978-01-05','VIN10000000000028'),('LIC029','Steven','Lewis','1989-09-18','VIN10000000000029'),('LIC030','Emily','Robinson','1993-05-20','VIN10000000000030');
/*!40000 ALTER TABLE `hjb_driver` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_home`
--

DROP TABLE IF EXISTS `hjb_home`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_home` (
  `Home_ID` int NOT NULL COMMENT 'Home ID',
  `PDate` date NOT NULL COMMENT 'Home purchase date',
  `PValue` decimal(10,2) NOT NULL COMMENT 'Home purchase value',
  `Area` int NOT NULL COMMENT 'in Sq.Ft.',
  `Home_Type` char(1) NOT NULL COMMENT 'Home type: S M C T',
  `AFN` int NOT NULL COMMENT 'auto fire notification: 0 or 1',
  `HSS` int NOT NULL COMMENT 'Home security system: 0 or 1',
  `SP` char(1) DEFAULT NULL COMMENT 'Swimming pool: U O I M or null',
  `Basement` int NOT NULL COMMENT 'Basement: 0 or 1',
  `HJB_HOMEPOLICY_HP_ID` int NOT NULL COMMENT 'Home policy id number (mandatory)',
  PRIMARY KEY (`Home_ID`),
  KEY `HJB_HOME_HJB_HOMEPOLICY_FK` (`HJB_HOMEPOLICY_HP_ID`),
  CONSTRAINT `HJB_HOME_HJB_HOMEPOLICY_FK` FOREIGN KEY (`HJB_HOMEPOLICY_HP_ID`) REFERENCES `hjb_homepolicy` (`HP_ID`),
  CONSTRAINT `AFN_CK` CHECK ((`AFN` in (0,1))),
  CONSTRAINT `Basement_CK` CHECK ((`Basement` in (0,1))),
  CONSTRAINT `Home_Type_CK` CHECK ((`Home_Type` in (_utf8mb4'C',_utf8mb4'M',_utf8mb4'S',_utf8mb4'T'))),
  CONSTRAINT `HSS_CK` CHECK ((`HSS` in (0,1))),
  CONSTRAINT `PositiveArea` CHECK ((`Area` >= 1)),
  CONSTRAINT `SP_CK` CHECK ((`SP` in (_utf8mb4'I',_utf8mb4'M',_utf8mb4'O',_utf8mb4'U')))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_home`
--

LOCK TABLES `hjb_home` WRITE;
/*!40000 ALTER TABLE `hjb_home` DISABLE KEYS */;
INSERT INTO `hjb_home` VALUES (501,'2015-05-20',450000.00,2500,'S',1,1,'O',1,201),(502,'2018-11-10',320000.00,1800,'T',0,1,NULL,0,202),(503,'2012-03-15',280000.00,1500,'C',1,0,'I',0,203),(504,'2020-07-22',650000.00,3500,'S',1,1,'U',1,204),(505,'2017-09-01',410000.00,2200,'M',0,1,'M',1,205),(506,'2019-12-05',520000.00,2800,'S',1,1,'O',1,206),(507,'2014-04-18',380000.00,2000,'T',0,0,NULL,0,207),(508,'2016-10-30',490000.00,2600,'S',1,1,'I',1,208),(509,'2011-08-25',250000.00,1400,'C',0,1,NULL,0,209),(510,'2021-01-12',720000.00,4000,'S',1,1,'O',1,210),(511,'2018-05-05',550000.00,3100,'S',1,1,'U',1,211),(512,'2015-03-20',430000.00,2400,'M',0,1,'M',1,212),(513,'2013-11-11',310000.00,1700,'T',1,0,NULL,0,213),(514,'2022-06-01',680000.00,3800,'S',1,1,'O',1,214),(515,'2017-02-14',420000.00,2300,'C',0,1,'I',0,215),(516,'2019-08-30',590000.00,3200,'S',1,1,'U',1,216),(517,'2010-12-25',290000.00,1600,'M',1,0,'M',1,217),(518,'2020-04-15',750000.00,4200,'S',1,1,'O',1,218),(519,'2016-07-07',480000.00,2700,'T',0,1,NULL,0,219),(520,'2014-09-18',340000.00,1900,'C',1,1,'I',0,220),(521,'2018-02-10',510000.00,2900,'S',1,1,'U',1,221),(522,'2012-11-20',360000.00,2100,'M',0,0,'M',1,222),(523,'2019-05-01',610000.00,3300,'S',1,1,'O',1,223),(524,'2015-10-15',440000.00,2500,'T',0,1,NULL,0,224),(525,'2017-12-30',530000.00,3000,'C',1,0,'I',0,225),(526,'2013-06-22',390000.00,2200,'S',1,1,'U',1,226),(527,'2021-08-08',780000.00,4500,'S',1,1,'O',1,227),(528,'2016-03-12',460000.00,2600,'M',0,1,'M',1,228),(529,'2019-11-19',640000.00,3600,'S',1,1,'U',1,229),(530,'2014-01-05',370000.00,2100,'T',0,0,NULL,0,230);
/*!40000 ALTER TABLE `hjb_home` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_auto_invoice`
-- Invoices for Auto policies only (separated from home to prevent overlap)
--

DROP TABLE IF EXISTS `hjb_auto_invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_auto_invoice` (
  `I_ID` int NOT NULL AUTO_INCREMENT COMMENT 'Auto invoice ID',
  `I_Date` date NOT NULL COMMENT 'Invoice date',
  `Due` date NOT NULL COMMENT 'Invoice due date',
  `Amount` decimal(10,2) NOT NULL COMMENT 'Invoice amount',
  `HJB_AUTOPOLICY_AP_ID` int NOT NULL COMMENT 'Auto policy ID (mandatory)',
  PRIMARY KEY (`I_ID`),
  KEY `HJB_AUTO_INVOICE_HJB_AUTOPOLICY_FK` (`HJB_AUTOPOLICY_AP_ID`),
  CONSTRAINT `HJB_AUTO_INVOICE_HJB_AUTOPOLICY_FK` FOREIGN KEY (`HJB_AUTOPOLICY_AP_ID`) REFERENCES `hjb_autopolicy` (`AP_ID`),
  CONSTRAINT `Auto_Invoice_Amount_CK` CHECK ((`Amount` >= 0.01)),
  CONSTRAINT `Auto_Invoice_DUE_IDATE` CHECK ((`Due` > `I_Date`))
) ENGINE=InnoDB AUTO_INCREMENT=816 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_auto_invoice`
--

LOCK TABLES `hjb_auto_invoice` WRITE;
/*!40000 ALTER TABLE `hjb_auto_invoice` DISABLE KEYS */;
INSERT INTO `hjb_auto_invoice` VALUES (801,'2025-01-11','2025-01-31',850.00,101),(802,'2025-02-16','2025-03-05',1200.50,102),(803,'2024-06-02','2024-06-20',980.00,103),(804,'2025-03-21','2025-04-10',1100.00,104),(805,'2025-01-06','2025-01-25',750.00,105),(806,'2024-12-11','2024-12-30',1350.25,106),(807,'2025-04-02','2025-04-20',890.00,107),(808,'2025-03-01','2025-03-15',1050.00,108),(809,'2024-09-16','2024-10-05',1150.00,109),(810,'2025-01-26','2025-02-10',920.75,110),(811,'2025-03-06','2025-03-25',1280.00,111),(812,'2025-02-15','2025-03-01',990.00,112),(813,'2024-11-02','2024-11-20',1100.50,113),(814,'2025-05-16','2025-06-05',870.00,114),(815,'2025-01-31','2025-02-15',1400.00,115);
/*!40000 ALTER TABLE `hjb_auto_invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_home_invoice`
-- Invoices for Home policies only (separated from auto to prevent overlap)
--

DROP TABLE IF EXISTS `hjb_home_invoice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_home_invoice` (
  `I_ID` int NOT NULL AUTO_INCREMENT COMMENT 'Home invoice ID',
  `I_Date` date NOT NULL COMMENT 'Invoice date',
  `Due` date NOT NULL COMMENT 'Invoice due date',
  `Amount` decimal(10,2) NOT NULL COMMENT 'Invoice amount',
  `HJB_HOMEPOLICY_HP_ID` int NOT NULL COMMENT 'Home policy ID (mandatory)',
  PRIMARY KEY (`I_ID`),
  KEY `HJB_HOME_INVOICE_HJB_HOMEPOLICY_FK` (`HJB_HOMEPOLICY_HP_ID`),
  CONSTRAINT `HJB_HOME_INVOICE_HJB_HOMEPOLICY_FK` FOREIGN KEY (`HJB_HOMEPOLICY_HP_ID`) REFERENCES `hjb_homepolicy` (`HP_ID`),
  CONSTRAINT `Home_Invoice_Amount_CK` CHECK ((`Amount` >= 0.01)),
  CONSTRAINT `Home_Invoice_DUE_IDATE` CHECK ((`Due` > `I_Date`))
) ENGINE=InnoDB AUTO_INCREMENT=831 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_home_invoice`
--

LOCK TABLES `hjb_home_invoice` WRITE;
/*!40000 ALTER TABLE `hjb_home_invoice` DISABLE KEYS */;
INSERT INTO `hjb_home_invoice` VALUES (816,'2025-01-16','2025-02-05',2500.00,201),(817,'2025-02-11','2025-03-01',3100.50,202),(818,'2024-05-21','2024-06-10',1800.00,203),(819,'2025-03-02','2025-03-20',4200.00,204),(820,'2025-01-02','2025-01-20',2750.00,205),(821,'2024-11-16','2024-12-05',3300.25,206),(822,'2025-04-13','2025-05-01',2900.00,207),(823,'2025-03-01','2025-03-20',3600.00,208),(824,'2024-08-06','2024-08-25',2100.00,209),(825,'2025-01-21','2025-02-10',4500.75,210),(826,'2025-03-16','2025-04-05',3800.00,211),(827,'2025-02-15','2025-03-05',3200.00,212),(828,'2024-12-02','2024-12-20',2400.50,213),(829,'2025-05-11','2025-05-30',4100.00,214),(830,'2025-01-31','2025-02-20',2950.00,215);
/*!40000 ALTER TABLE `hjb_home_invoice` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_auto_payment`
-- Payments for Auto invoices only
--

DROP TABLE IF EXISTS `hjb_auto_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_auto_payment` (
  `P_ID` int NOT NULL AUTO_INCREMENT COMMENT 'Auto payment ID',
  `Method` varchar(20) NOT NULL COMMENT 'Payment method: Check, Credit, Debit, PayPal',
  `HJB_AUTO_INVOICE_I_ID` int NOT NULL COMMENT 'Auto invoice ID (mandatory)',
  `Pay_Amount` decimal(10,2) NOT NULL COMMENT 'Payment amount',
  `Pay_Date` date NOT NULL COMMENT 'Payment date',
  PRIMARY KEY (`P_ID`),
  KEY `HJB_AUTO_PAYMENT_HJB_AUTO_INVOICE_FK` (`HJB_AUTO_INVOICE_I_ID`),
  CONSTRAINT `HJB_AUTO_PAYMENT_HJB_AUTO_INVOICE_FK` FOREIGN KEY (`HJB_AUTO_INVOICE_I_ID`) REFERENCES `hjb_auto_invoice` (`I_ID`),
  CONSTRAINT `Auto_Pay_Amount_CK` CHECK ((`Pay_Amount` >= 0.01)),
  CONSTRAINT `Auto_Pay_Method_CK` CHECK ((`Method` in (_utf8mb4'Check',_utf8mb4'Credit',_utf8mb4'Debit',_utf8mb4'PayPal')))
) ENGINE=InnoDB AUTO_INCREMENT=916 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_auto_payment`
--

LOCK TABLES `hjb_auto_payment` WRITE;
/*!40000 ALTER TABLE `hjb_auto_payment` DISABLE KEYS */;
INSERT INTO `hjb_auto_payment` VALUES (901,'Credit',801,850.00,'2025-01-15'),(902,'PayPal',802,1200.50,'2025-02-20'),(903,'Check',803,980.00,'2024-06-10'),(904,'Debit',804,1100.00,'2025-03-25'),(905,'Credit',805,750.00,'2025-01-10'),(906,'PayPal',806,1350.25,'2024-12-20'),(907,'Check',807,890.00,'2025-04-05'),(908,'Debit',808,1050.00,'2025-03-10'),(909,'Credit',809,1150.00,'2024-09-20'),(910,'PayPal',810,920.75,'2025-02-01'),(911,'Check',811,1280.00,'2025-03-10'),(912,'Debit',812,990.00,'2025-02-20'),(913,'Credit',813,1100.50,'2024-11-10'),(914,'PayPal',814,870.00,'2025-05-20'),(915,'Check',815,1400.00,'2025-02-05');
/*!40000 ALTER TABLE `hjb_auto_payment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hjb_home_payment`
-- Payments for Home invoices only
--

DROP TABLE IF EXISTS `hjb_home_payment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hjb_home_payment` (
  `P_ID` int NOT NULL AUTO_INCREMENT COMMENT 'Home payment ID',
  `Method` varchar(20) NOT NULL COMMENT 'Payment method: Check, Credit, Debit, PayPal',
  `HJB_HOME_INVOICE_I_ID` int NOT NULL COMMENT 'Home invoice ID (mandatory)',
  `Pay_Amount` decimal(10,2) NOT NULL COMMENT 'Payment amount',
  `Pay_Date` date NOT NULL COMMENT 'Payment date',
  PRIMARY KEY (`P_ID`),
  KEY `HJB_HOME_PAYMENT_HJB_HOME_INVOICE_FK` (`HJB_HOME_INVOICE_I_ID`),
  CONSTRAINT `HJB_HOME_PAYMENT_HJB_HOME_INVOICE_FK` FOREIGN KEY (`HJB_HOME_INVOICE_I_ID`) REFERENCES `hjb_home_invoice` (`I_ID`),
  CONSTRAINT `Home_Pay_Amount_CK` CHECK ((`Pay_Amount` >= 0.01)),
  CONSTRAINT `Home_Pay_Method_CK` CHECK ((`Method` in (_utf8mb4'Check',_utf8mb4'Credit',_utf8mb4'Debit',_utf8mb4'PayPal')))
) ENGINE=InnoDB AUTO_INCREMENT=931 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hjb_home_payment`
--

LOCK TABLES `hjb_home_payment` WRITE;
/*!40000 ALTER TABLE `hjb_home_payment` DISABLE KEYS */;
INSERT INTO `hjb_home_payment` VALUES (916,'Debit',816,2500.00,'2025-01-25'),(917,'Credit',817,3100.50,'2025-02-20'),(918,'PayPal',818,1800.00,'2024-06-01'),(919,'Check',819,4200.00,'2025-03-15'),(920,'Debit',820,2750.00,'2025-01-10'),(921,'Credit',821,3300.25,'2024-11-25'),(922,'PayPal',822,2900.00,'2025-04-20'),(923,'Check',823,3600.00,'2025-03-15'),(924,'Debit',824,2100.00,'2024-08-15'),(925,'Credit',825,4500.75,'2025-01-30'),(926,'PayPal',826,3800.00,'2025-03-25'),(927,'Check',827,3200.00,'2025-02-28'),(928,'Debit',828,2400.50,'2024-12-15'),(929,'Credit',829,4100.00,'2025-05-20'),(930,'PayPal',830,2950.00,'2025-02-10');
/*!40000 ALTER TABLE `hjb_home_payment` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-04-26
