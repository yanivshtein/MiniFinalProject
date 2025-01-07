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
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `time` date DEFAULT NULL,
  `bookName` varchar(45) DEFAULT NULL,
  `subID` varchar(45) DEFAULT NULL,
  `orderID` int NOT NULL,
  PRIMARY KEY (`orderID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('2025-01-02','123',NULL,1),('2025-01-02','Harry Potter',NULL,2),('2025-01-02','Harry 2',NULL,3),('2025-01-02','harry 3',NULL,4),('2025-01-02','harry 4',NULL,5),('2025-01-02','harry 5',NULL,6),('2025-01-02','Charley bit my finger',NULL,7),('2025-01-02','harry 123',NULL,8),('2025-01-02','harry?',NULL,9),('2025-01-02','haaarrrrrrrrryyy',NULL,10),('2025-01-02','hooho',NULL,11),('2025-01-02','charley bit my finger!',NULL,12),('2025-01-02','bottle of water',NULL,13),('2025-01-02','bottle of water',NULL,14),('2025-01-02','bottle of water',NULL,15),('2025-01-02','bottle of water',NULL,16),('2025-01-02','bottle of water',NULL,17),('2025-01-02','bottle of water',NULL,18),('2025-01-02','bottle of water',NULL,19),('2025-01-02','bottle of water',NULL,20),('2025-01-02','bottle',NULL,21),('2025-01-02','bottle',NULL,22),('2025-01-02','bottle of rum',NULL,23),('2025-01-02','bottle of rumi',NULL,24),('2025-01-02','dor shamo 2',NULL,25),('2025-01-03','War and Peace',NULL,26);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-07 11:54:07
