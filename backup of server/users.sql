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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(25) NOT NULL,
  `password` varchar(50) NOT NULL,
  `email` varchar(100) DEFAULT NULL,
  `top_score` int(11) DEFAULT NULL,
  `played_counter` int(11) DEFAULT NULL,
  `home_nation_id` int(11) NOT NULL,
  `last_login` datetime DEFAULT NULL,
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  KEY `fk_Users_Nations1_idx` (`home_nation_id`),
  CONSTRAINT `fk_Users_Nations1` FOREIGN KEY (`home_nation_id`) REFERENCES `nations` (`nation_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'alon','d8578edf8458ce06fbc5bb76a58c5ca4','alon/k@l.com',500,NULL,5,'2015-06-18 06:22:30','2015-06-09 16:22:28'),(2,'ogoun','30242c80ce0c971bc3a44d0cdff14d66','ogoun.d@gmail.com',1138,NULL,191,'2015-06-18 13:36:55','2015-06-12 13:31:43'),(4,'alonalon','d4b9967b6c3f9cdb6210cb408e82a5be','alon.elmaliah@gmail.com',0,NULL,43,'2015-06-16 11:59:00','2015-06-16 08:59:02'),(5,'the dude','a4589a60ea90b98f8f75780b4c829e9a','yuvalzuaretz@gmail.com',0,NULL,191,'2015-06-17 19:56:20','2015-06-17 15:52:08'),(6,'1','c4ca4238a0b923820dcc509a6f75849b','',0,NULL,191,'2015-06-18 06:19:29','2015-06-18 00:48:36'),(9,'player','827ccb0eea8a706c4c34a16891f84e7b','ludmeron@gmail.com',576,NULL,191,'2015-06-18 13:59:16','2015-06-18 03:29:26'),(10,'test','098f6bcd4621d373cade4e832627b4f6','ludmeron@gmail.com',0,NULL,191,'2015-06-18 13:59:58','2015-06-18 11:00:03');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-06-18 15:17:49
