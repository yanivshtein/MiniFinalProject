-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: localhost    Database: hw2-shitot
-- ------------------------------------------------------
-- Server version	8.0.40

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
-- Table structure for table `activityhistory`
--

DROP TABLE IF EXISTS `activityhistory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `activityhistory` (
  `SubscriberID` int NOT NULL,
  `BookName` varchar(255) DEFAULT NULL,
  `ActionType` enum('Borrow','Return','Reservation','UpdateDetails') NOT NULL,
  `ActionDate` date NOT NULL,
  `returned_late` tinyint DEFAULT NULL,
  `librarian_extend_id` int DEFAULT NULL,
  `deadline` date DEFAULT NULL,
  `ActionID` int NOT NULL,
  PRIMARY KEY (`ActionID`),
  KEY `SubscriberID` (`SubscriberID`),
  KEY `librarian_id_idx` (`librarian_extend_id`),
  CONSTRAINT `activityhistory_ibfk_1` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`subscriber_id`),
  CONSTRAINT `librarian_id` FOREIGN KEY (`librarian_extend_id`) REFERENCES `librarian` (`librarian_id`),
  CONSTRAINT `subscriber_id` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`subscriber_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activityhistory`
--

LOCK TABLES `activityhistory` WRITE;
/*!40000 ALTER TABLE `activityhistory` DISABLE KEYS */;
INSERT INTO `activityhistory` VALUES (1,'To Kill a Mockingbird','Borrow','2024-01-01',NULL,NULL,'2024-01-16',1),(1,'To Kill a Mockingbird','Return','2024-01-16',1,NULL,NULL,2),(3,'Jane Eyre','Borrow','2024-02-02',NULL,NULL,NULL,3);
/*!40000 ALTER TABLE `activityhistory` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-07 11:44:04
