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
-- Table structure for table `relation_types`
--

DROP TABLE IF EXISTS `relation_types`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `relation_types` (
  `relation_type_id` int(11) NOT NULL AUTO_INCREMENT,
  `relation_type_name` varchar(45) NOT NULL,
  `relation_type_caption` varchar(45) DEFAULT NULL,
  `relation_type_weight` int(11) NOT NULL DEFAULT '5' COMMENT 'determine how difficult this type is\nscale: from 1 to 10\ndefault: 5',
  PRIMARY KEY (`relation_type_id`),
  UNIQUE KEY `relation_type_name_UNIQUE` (`relation_type_name`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `relation_types`
--

LOCK TABLES `relation_types` WRITE;
/*!40000 ALTER TABLE `relation_types` DISABLE KEYS */;
INSERT INTO `relation_types` VALUES (1,'<isCitizenOf>','A citizen of this nation: The %s',2),(2,'<isLocatedIn>','A place in this nation:  The %s',5),(3,'<happenedIn>','An event of this nation: The %s',8),(4,'<isPoliticianOf>','A politician in this nation: The %s',6),(5,'<participatedIn>','This nation participated in: The %s',7),(6,'<exports>','Main exported goods: %s',3),(7,'<hasOfficialLanguage>','Official language: %s',14),(8,'<imports>','Main imported goods: %s',4),(9,'<hasCapital>','Capital: The %s',7),(10,'<hasCurrency>','Currency: %s',8),(11,'<owns>','The %s is owned by this nation',2),(12,'<wasCreatedOnDate>','This nation was founded on: %s',7),(13,'<hasGini>','Ranked %s in Gini index (Inequality)',1),(14,'<hasInflation>','Inflation Rate: %s highest in the world',4),(15,'<hasRevenue>','Revenue: %s highest in the world',3),(16,'<hasUnemployment>','Unemplyment Ratio: %s highest in the world',4),(17,'<hasNumberOfPeople>','%s largest population in the world',2),(18,'<hasTLD>','national domain names: www . google%s',18),(19,'<hasPoverty>','%s poorest nation in the world',1),(20,'<hasGDP>','GDP: %s greatest in the world',4),(21,'<hasImport>','Total Export: %s highest  in the world',3),(22,'<hasExpenses>','Total Expenses: %s highest  in the world',3),(23,'<hasExport>','Total Export: %s highest  in the world',3),(24,'<hasMotto>','Motto: %s',6),(25,'<hasEconomicGrowth>','Ecomonical Growth: %s highest in the world',1);
/*!40000 ALTER TABLE `relation_types` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-06-18 15:15:40
