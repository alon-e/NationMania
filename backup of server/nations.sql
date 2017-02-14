-- MySQL dump 10.13  Distrib 5.6.23, for Win64 (x86_64)
--
-- Host: localhost    Database: DbMysql05
-- ------------------------------------------------------
-- Server version	5.5.22-0ubuntu1-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `nations`
--

DROP TABLE IF EXISTS `nations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nations` (
  `nation_id` int(11) NOT NULL AUTO_INCREMENT,
  `nation_name` varchar(50) DEFAULT NULL,
  `nation_caption` varchar(45) DEFAULT NULL,
  `nation_weight` int(11) DEFAULT '5',
  `continent_id` int(11) NOT NULL,
  PRIMARY KEY (`nation_id`),
  UNIQUE KEY `nation_name_UNIQUE` (`nation_name`),
  KEY `fk_Nations_Continents1_idx` (`continent_id`),
  CONSTRAINT `fk_Nations_Continents1` FOREIGN KEY (`continent_id`) REFERENCES `continents` (`continent_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=196 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nations`
--

LOCK TABLES `nations` WRITE;
/*!40000 ALTER TABLE `nations` DISABLE KEYS */;
INSERT INTO `nations` VALUES (1,'<Lithuania>','Lithuania',21,5),(2,'<Tuvalu>','Tuvalu',10,7),(3,'<The_Bahamas>','The Bahamas',14,4),(4,'<Moldova>','Moldova',13,5),(5,'<Afghanistan>','Afghanistan',35,3),(6,'<Somalia>','Somalia',19,2),(7,'<Guyana>','Guyana',15,6),(8,'<Qatar>','Qatar',12,3),(10,'<Estonia>','Estonia',39,5),(11,'<Peru>','Peru',51,8),(13,'<Japan>','Japan',72,3),(14,'<Hungary>','Hungary',23,5),(15,'<Australia>','Australia',83,7),(16,'<Jordan>','Jordan',18,3),(17,'<Guatemala>','Guatemala',21,4),(18,'<Costa_Rica>','Costa Rica',18,4),(19,'<Slovakia>','Slovakia',17,5),(20,'<Belgium>','Belgium',43,5),(21,'<Russia>','Russia',57,3),(23,'<Zambia>','Zambia',15,2),(24,'<San_Marino>','San Marino',9,5),(25,'<Norway>','Norway',58,5),(26,'<Tunisia>','Tunisia',23,2),(27,'<Libya>','Libya',18,2),(28,'<Ghana>','Ghana',22,2),(29,'<Oman>','Oman',11,3),(30,'<Spain>','Spain',81,2),(31,'<Equatorial_Guinea>','Equatorial Guinea',9,2),(32,'<Germany>','Germany',93,5),(33,'<Mongolia>','Mongolia',21,3),(34,'<Netherlands>','Netherlands',68,5),(35,'<Eritrea>','Eritrea',14,2),(36,'<France>','France',83,5),(37,'<India>','India',134,3),(38,'<South_Africa>','South Africa',38,2),(39,'<Saint_Lucia>','Saint Lucia',11,6),(40,'<Switzerland>','Switzerland',56,5),(41,'<Fiji>','Fiji',14,7),(42,'<Sweden>','Sweden',43,5),(43,'<Brunei>','Brunei',8,3),(44,'<Nicaragua>','Nicaragua',17,4),(45,'<Sri_Lanka>','Sri Lanka',35,3),(46,'<Lebanon>','Lebanon',26,3),(47,'<North_Korea>','North Korea',19,3),(48,'<Burkina_Faso>','Burkina Faso',41,2),(49,'<United_Arab_Emirates>','United Arab Emirates',28,3),(50,'<Seychelles>','Seychelles',9,2),(51,'<Madagascar>','Madagascar',25,2),(52,'<Swaziland>','Swaziland',10,2),(53,'<Malawi>','Malawi',13,2),(54,'<Namibia>','Namibia',19,2),(55,'<Rwanda>','Rwanda',13,2),(56,'<Cambodia>','Cambodia',22,3),(57,'<Philippines>','Philippines',55,3),(58,'<Kyrgyzstan>','Kyrgyzstan',27,3),(60,'<Mauritius>','Mauritius',13,2),(61,'<Benin>','Benin',16,2),(62,'<Iceland>','Iceland',15,5),(63,'<Yemen>','Yemen',13,2),(64,'<Syria>','Syria',34,3),(65,'<Vietnam>','Vietnam',18,3),(66,'<Mozambique>','Mozambique',17,2),(67,'<Senegal>','Senegal',16,2),(68,'<Latvia>','Latvia',18,5),(69,'<Panama>','Panama',18,4),(70,'<Gabon>','Gabon',15,2),(71,'<Turkmenistan>','Turkmenistan',10,3),(72,'<Romania>','Romania',69,5),(73,'<Nepal>','Nepal',64,3),(74,'<Canada>','Canada',93,6),(75,'<Mali>','Mali',27,2),(77,'<Greece>','Greece',48,5),(79,'<Ethiopia>','Ethiopia',24,2),(80,'<Kuwait>','Kuwait',12,3),(81,'<Honduras>','Honduras',19,4),(82,'<Cyprus>','Cyprus',25,5),(83,'<Marshall_Islands>','Marshall Islands',8,7),(84,'<Bolivia>','Bolivia',23,8),(86,'<Grenada>','Grenada',12,4),(87,'<Montenegro>','Montenegro',15,5),(88,'<Argentina>','Argentina',40,8),(89,'<Indonesia>','Indonesia',39,3),(90,'<Uzbekistan>','Uzbekistan',14,3),(91,'<Republic_of_Macedonia>','Republic of Macedonia',20,5),(92,'<Albania>','Albania',20,5),(93,'<Armenia>','Armenia',37,3),(94,'<Colombia>','Colombia',34,6),(95,'<Zimbabwe>','Zimbabwe',22,2),(96,'<Belarus>','Belarus',20,5),(97,'<Angola>','Angola',18,2),(98,'<Liechtenstein>','Liechtenstein',10,5),(99,'<Poland>','Poland',107,5),(100,'<Slovenia>','Slovenia',39,5),(101,'<Laos>','Laos',15,3),(102,'<Suriname>','Suriname',15,6),(103,'<Singapore>','Singapore',32,3),(104,'<Lesotho>','Lesotho',15,2),(105,'<Cape_Verde>','Cape Verde',9,2),(106,'<Cameroon>','Cameroon',21,2),(107,'<Tanzania>','Tanzania',24,2),(108,'<East_Timor>','East Timor',11,7),(109,'<Andorra>','Andorra',9,2),(111,'<Samoa>','Samoa',15,7),(112,'<Pakistan>','Pakistan',49,3),(113,'<Azerbaijan>','Azerbaijan',41,3),(114,'<Paraguay>','Paraguay',16,8),(115,'<South_Korea>','South Korea',50,3),(116,'<Aruba>','Aruba',9,8),(117,'<Botswana>','Botswana',19,2),(118,'<Jamaica>','Jamaica',19,6),(120,'<Comoros>','Comoros',8,2),(121,'<Togo>','Togo',24,2),(122,'<El_Salvador>','El Salvador',17,4),(123,'<Italy>','Italy',76,2),(124,'<Kenya>','Kenya',26,2),(125,'<Liberia>','Liberia',14,2),(127,'<Czech_Republic>','Czech Republic',45,5),(128,'<Democratic_Republic_of_the_Congo>','Democratic Republic of the Congo',21,2),(129,'<Luxembourg>','Luxembourg',23,5),(130,'<Papua_New_Guinea>','Papua New Guinea',20,7),(131,'<Uganda>','Uganda',26,2),(132,'<Turkey>','Turkey',53,3),(133,'<Federated_States_of_Micronesia>','Federated States of Micronesia',11,7),(134,'<Vatican_City>','Vatican City',8,5),(135,'<Saudi_Arabia>','Saudi Arabia',21,3),(136,'<Tajikistan>','Tajikistan',24,3),(137,'<Finland>','Finland',34,5),(138,'<Ivory_Coast>','Ivory Coast',14,2),(139,'<Tonga>','Tonga',12,7),(140,'<Burma>','Burma',37,3),(141,'<Kosovo>','Kosovo',17,5),(142,'<Kazakhstan>','Kazakhstan',20,3),(143,'<United_Kingdom>','United Kingdom',121,5),(144,'<Bangladesh>','Bangladesh',40,3),(145,'<Burundi>','Burundi',13,2),(147,'<Nigeria>','Nigeria',30,2),(148,'<Sierra_Leone>','Sierra Leone',17,3),(149,'<Dominican_Republic>','Dominican Republic',19,6),(150,'<Denmark>','Denmark',37,5),(151,'<Venezuela>','Venezuela',26,6),(152,'<Bahrain>','Bahrain',12,3),(153,'<Thailand>','Thailand',45,3),(154,'<Iran>','Iran',53,3),(155,'<Iraq>','Iraq',29,3),(156,'<Guinea-Bissau>','Guinea-Bissau',11,2),(157,'<Chile>','Chile',33,8),(158,'<Croatia>','Croatia',28,5),(159,'<United_States>','United States',342,6),(160,'<Bhutan>','Bhutan',14,3),(161,'<Portugal>','Portugal',40,5),(162,'<Central_African_Republic>','Central African Republic',17,2),(163,'<Georgia_(country)>','Georgia ',24,3),(164,'<Vanuatu>','Vanuatu',11,7),(165,'<Ecuador>','Ecuador',20,8),(167,'<Sudan>','Sudan',18,2),(168,'<Solomon_Islands>','Solomon Islands',17,7),(169,'<Maldives>','Maldives',18,3),(170,'<Austria>','Austria',43,5),(171,'<South_Sudan>','South Sudan',13,2),(172,'<Kiribati>','Kiribati',11,7),(173,'<Barbados>','Barbados',11,6),(174,'<New_Zealand>','New Zealand',45,7),(175,'<Djibouti>','Djibouti',11,2),(176,'<Morocco>','Morocco',23,2),(177,'<Uruguay>','Uruguay',20,8),(178,'<Monaco>','Monaco',7,5),(179,'<Niger>','Niger',19,2),(180,'<Dominica>','Dominica',12,6),(181,'<Nauru>','Nauru',8,7),(182,'<Mauritania>','Mauritania',17,2),(183,'<Republic_of_the_Congo>','Republic of the Congo',15,2),(184,'<Haiti>','Haiti',16,6),(185,'<Serbia>','Serbia',29,5),(186,'<Algeria>','Algeria',28,2),(187,'<Brazil>','Brazil',43,8),(188,'<Malaysia>','Malaysia',33,3),(191,'<Israel>','Israel',36,3),(192,'<Ukraine>','Ukraine',39,5),(195,'<Bulgaria>','Bulgaria',33,5);
/*!40000 ALTER TABLE `nations` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-06-18 15:15:09
