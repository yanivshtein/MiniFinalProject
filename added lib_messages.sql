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
  `additionalInfo` varchar(150) DEFAULT NULL,
  `deadline` date DEFAULT NULL,
  `ActionID` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`ActionID`),
  KEY `SubscriberID` (`SubscriberID`),
  CONSTRAINT `activityhistory_ibfk_1` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`subscriber_id`),
  CONSTRAINT `subscriber_id` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activityhistory`
--

LOCK TABLES `activityhistory` WRITE;
/*!40000 ALTER TABLE `activityhistory` DISABLE KEYS */;
INSERT INTO `activityhistory` VALUES (1,'To Kill a Mockingbird','Borrow','2024-01-01','Extended by: doroty , at: 2025-01-14','2024-01-20',1),(1,'To Kill a Mockingbird','Return','2024-01-16','1',NULL,2),(3,'Jane Eyre','Borrow','2024-02-02',NULL,NULL,3),(2,'1984','Borrow','2025-01-07','autoExtended','2025-01-28',4),(2,'The Hobbit','Borrow','2025-01-07',NULL,'2025-01-01',5),(2,'1984','Borrow','2025-01-10','autoExtended','2025-01-31',6),(5,'1984','Reservation','2025-01-13',NULL,NULL,7),(5,'War and Peace','Reservation','2025-01-13',NULL,NULL,12),(2,'War and Peace','Borrow','2025-01-10',NULL,'2025-01-24',13),(2,'War and Peace','Return','2025-01-13','Returned on time',NULL,14),(5,'War and Peace','Borrow','2025-01-13','autoExtended','2025-01-22',15),(5,'1984','Reservation','2025-01-14',NULL,NULL,16),(5,'War and Peace','Reservation','2025-01-14',NULL,NULL,17),(5,'1984','Reservation','2025-01-14',NULL,NULL,18),(2,'Harry Potter and the Sorcerer\'s Stone','Borrow','2025-01-14',NULL,'2025-01-28',19),(2,'The Hobbit','Return','2025-01-14','13 Days Late',NULL,21);
/*!40000 ALTER TABLE `activityhistory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `books`
--

DROP TABLE IF EXISTS `books`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `books` (
  `bookName` varchar(50) NOT NULL,
  `copysAvailable` varchar(45) DEFAULT NULL,
  `totalCopys` varchar(45) DEFAULT NULL,
  `Genre` varchar(45) DEFAULT NULL,
  `description` varchar(200) DEFAULT NULL,
  `author` varchar(45) DEFAULT NULL,
  `BookId` int DEFAULT NULL,
  `location` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`bookName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `books`
--

LOCK TABLES `books` WRITE;
/*!40000 ALTER TABLE `books` DISABLE KEYS */;
INSERT INTO `books` VALUES ('1984','0','15','Science Fiction',NULL,'George Orwell',1,'A'),('Harry Potter and the Sorcerer\'s Stone','9','25','	Fantasy',NULL,'J. K. Rowling',2,'A'),('Moby Dick','3','6','	Adventure',NULL,'Herman Melville',3,'A'),('Pride and Prejudice','6','12','Romantic Novel',NULL,'Jane Austen',4,'A'),('The Catcher in the Rye','4','8','Fiction',NULL,'J.D. Salinger',5,'A'),('The Great Gatsby','8','20','Drama',NULL,'	F. Scott Fitzgerald',6,'A'),('The Hobbit','11','18','	Fantasy',NULL,'J. R. R. Tolkien',7,'A'),('The Lord of the Rings','7','14','	Fantasy',NULL,'J. R. R. Tolkien',8,'A'),('To Kill a Mockingbird','5','10','Drama',NULL,'	Harper Lee',9,'A'),('War and Peace','0','5','Drama',NULL,'	Leo Tolstoy',10,'A');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lib_messages`
--

DROP TABLE IF EXISTS `lib_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lib_messages` (
  `libID` int DEFAULT NULL,
  `note` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lib_messages`
--

LOCK TABLES `lib_messages` WRITE;
/*!40000 ALTER TABLE `lib_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `lib_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `librarian`
--

DROP TABLE IF EXISTS `librarian`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `librarian` (
  `librarian_id` int NOT NULL,
  `librarian_name` varchar(70) DEFAULT NULL,
  `librarian_email` varchar(70) DEFAULT NULL,
  `librarian_password` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`librarian_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `librarian`
--

LOCK TABLES `librarian` WRITE;
/*!40000 ALTER TABLE `librarian` DISABLE KEYS */;
INSERT INTO `librarian` VALUES (1,'doroty','1','1');
/*!40000 ALTER TABLE `librarian` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `time` date DEFAULT NULL,
  `bookName` varchar(70) DEFAULT NULL,
  `subID` int DEFAULT NULL,
  `arrivalTime` date DEFAULT NULL,
  `order_id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('2025-01-14','War and Peace',5,NULL,3);
/*!40000 ALTER TABLE `orders` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_messages`
--

DROP TABLE IF EXISTS `sub_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sub_messages` (
  `subID` int DEFAULT NULL,
  `note` varchar(150) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_messages`
--

LOCK TABLES `sub_messages` WRITE;
/*!40000 ALTER TABLE `sub_messages` DISABLE KEYS */;
INSERT INTO `sub_messages` VALUES (0,'Your order of the book: 1984 has arrived! Please take it in less than two days'),(0,'Your order of the book: 1984 has arrived! Please take it in less than two days'),(5,'Your order of the book: 1984 is canceled!'),(5,'Your order of the book: 1984 is canceled!');
/*!40000 ALTER TABLE `sub_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriber`
--

DROP TABLE IF EXISTS `subscriber`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriber` (
  `subscriber_id` int NOT NULL AUTO_INCREMENT,
  `subscriber_name` varchar(255) NOT NULL,
  `subscriber_phone_number` varchar(15) DEFAULT NULL,
  `subscriber_email` varchar(255) DEFAULT NULL,
  `subscription_status` varchar(30) DEFAULT NULL,
  `password` varchar(70) DEFAULT NULL,
  PRIMARY KEY (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=14070 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES (1,'dor','1','1','frozen',NULL),(2,'John Doe','123-456-7890','john.doe@example.com','Frozen','123'),(3,'Jane Smith','987-654-3210','jane.smith@example.com','active',NULL),(4,'Alice Johnson','555-666-7777','alice.johnson@example.com','active',NULL),(5,'1','1','1','active','1'),(14069,'beri','','','active','1');
/*!40000 ALTER TABLE `subscriber` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-14 19:50:33
