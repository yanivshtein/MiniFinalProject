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
  `ActionType` enum('Borrow','Return','Reservation','Lost') NOT NULL,
  `ActionDate` date NOT NULL,
  `additionalInfo` varchar(150) DEFAULT NULL,
  `deadline` date DEFAULT NULL,
  `ActionID` int NOT NULL AUTO_INCREMENT,
  `reminderSent` tinyint(1) DEFAULT '0',
  `hasReturned` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`ActionID`),
  KEY `SubscriberID` (`SubscriberID`),
  CONSTRAINT `activityhistory_ibfk_1` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`subscriber_id`),
  CONSTRAINT `subscriber_id` FOREIGN KEY (`SubscriberID`) REFERENCES `subscriber` (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=125 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activityhistory`
--

LOCK TABLES `activityhistory` WRITE;
/*!40000 ALTER TABLE `activityhistory` DISABLE KEYS */;
INSERT INTO `activityhistory` VALUES (2,'The Lord of the Rings','Return','2025-01-25','12 Days Late',NULL,85,0,0),(1,'Moby Dick','Borrow','2025-01-12','','2025-01-28',86,1,0),(3,'To Kill a Mockingbird','Borrow','2025-01-15','Extended by: Doroty Shamo , at: 2025-01-26','2025-01-30',87,0,1),(4,'To Kill a Mockingbird','Borrow','2025-01-10',NULL,'2025-01-24',88,0,1),(5,'To Kill a Mockingbird','Borrow','2025-01-01',NULL,'2025-01-14',89,0,1),(1,'The Hobbit','Borrow','2025-01-25',NULL,'2025-02-08',97,0,0),(1,'1984','Borrow','2025-01-25',NULL,'2025-02-08',98,0,1),(3,'To Kill a Mockingbird','Return','2025-01-25','Returned on time',NULL,99,0,0),(4,'To Kill a Mockingbird','Return','2025-01-25','1 Days Late',NULL,100,0,0),(5,'To Kill a Mockingbird','Return','2025-01-25','11 Days Late',NULL,101,0,0),(1,'1984','Reservation','2025-01-26',NULL,NULL,102,0,0),(3,'1984','Reservation','2025-01-26',NULL,NULL,103,0,0),(4,'1984','Borrow','2025-01-24',NULL,'2025-02-12',104,0,1),(4,'1984','Return','2025-01-26','Returned on time',NULL,105,0,0),(1,'1984','Reservation','2025-01-26',NULL,NULL,106,0,0),(4,'1984','Borrow','2025-01-20',NULL,'2025-02-12',107,0,1),(4,'1984','Return','2025-01-26','Returned on time',NULL,108,0,0),(1,'1984','Borrow','2025-01-25',NULL,'2025-02-02',109,0,1),(1,'1984','Borrow','2025-01-27',NULL,NULL,110,0,1),(1,'1984','Lost','2025-01-27',NULL,NULL,113,0,0),(1,'Moby Dick','Return','2025-02-01','Returned on time',NULL,114,1,0),(1,'The Lord of the Rings','Borrow','2025-01-16','','2025-01-30',117,0,0),(1,'War and Peace','Borrow','2025-01-16',NULL,'2025-01-30',118,0,0),(3,'War and Peace','Reservation','2025-01-27',NULL,NULL,119,0,0),(1,'1984','Reservation','2025-01-27',NULL,NULL,120,0,0),(7,'1984','Borrow','2025-01-27',NULL,'2025-02-10',121,0,1),(7,'1984','Return','2025-01-27','Returned on time',NULL,122,0,0),(7,'1984','Borrow','2025-01-16',NULL,'2025-01-30',123,0,0),(10,'The Great Gatsby','Borrow','2024-12-01',NULL,'2024-12-14',124,0,0);
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
INSERT INTO `books` VALUES ('1984','0','9','Science Fiction','George Orwell',1,'A'),('Harry Potter and the Sorcerer\'s Stone','8','25','	Fantasy','J.K.Rowling',2,'A'),('Moby Dick','0','6','	Adventure','Herman Melville',3,'A'),('Pride and Prejudice','1','12','Romantic Novel','Jane Austen',4,'A'),('The Catcher in the Rye','4','8','Fiction','J.D.Salinger',5,'A'),('The Great Gatsby','9','20','Drama','F.Scott Fitzgerald',6,'A'),('The Hobbit','6','18','	Fantasy','J.R.R.Tolkien',7,'A'),('The Lord of the Rings','7','14','	Fantasy','J.R.R.Tolkien',8,'A'),('To Kill a Mockingbird','11','10','Drama','	Harper Lee',9,'A'),('War and Peace','0','5','Drama','	Leo Tolstoy',10,'A');
/*!40000 ALTER TABLE `books` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `frozen_subs`
--

DROP TABLE IF EXISTS `frozen_subs`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `frozen_subs` (
  `subscriber_id` int DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `finish_date` date DEFAULT NULL,
  `freeze_id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`freeze_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `frozen_subs`
--

LOCK TABLES `frozen_subs` WRITE;
/*!40000 ALTER TABLE `frozen_subs` DISABLE KEYS */;
INSERT INTO `frozen_subs` VALUES (2,'2025-01-25','2025-02-25',1),(5,'2025-01-25','2025-02-25',2);
/*!40000 ALTER TABLE `frozen_subs` ENABLE KEYS */;
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
INSERT INTO `lib_messages` VALUES (NULL,'The subscriber 1, got Auto Extension for 14 more days. Action Date: 2025-01-27');
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
INSERT INTO `librarian` VALUES (1,'Doroty Shamo','doroty@gmail.com','doroty123'),(2,'Einat Kiper','einat@gmail.com','einat123'),(3,'Avi Sofer','avi@gmail.com','aviS123');
/*!40000 ALTER TABLE `librarian` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `librarian_messages`
--

DROP TABLE IF EXISTS `librarian_messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `librarian_messages` (
  `id` int NOT NULL AUTO_INCREMENT,
  `notes` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `librarian_messages`
--

LOCK TABLES `librarian_messages` WRITE;
/*!40000 ALTER TABLE `librarian_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `librarian_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `orders`
--

DROP TABLE IF EXISTS `orders`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orders` (
  `time` datetime DEFAULT NULL,
  `bookName` varchar(70) DEFAULT NULL,
  `subID` int DEFAULT NULL,
  `arrivalTime` date DEFAULT NULL,
  `order_id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `orders`
--

LOCK TABLES `orders` WRITE;
/*!40000 ALTER TABLE `orders` DISABLE KEYS */;
INSERT INTO `orders` VALUES ('2025-01-26 18:30:05','1984',3,NULL,21),('2025-01-27 15:23:17','War and Peace',3,NULL,23);
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
  `note` varchar(150) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=38 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_messages`
--

LOCK TABLES `sub_messages` WRITE;
/*!40000 ALTER TABLE `sub_messages` DISABLE KEYS */;
INSERT INTO `sub_messages` VALUES (1,'Reminder: The book \"Moby Dick\" must be returned by 2025-01-29.',35),(1,'Your order of the book: 1984 has arrived! Please take it in less than two days',36),(1,'Your order of the book: 1984 is canceled!',37);
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
  `join_date` date DEFAULT NULL,
  PRIMARY KEY (`subscriber_id`)
) ENGINE=InnoDB AUTO_INCREMENT=90711 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriber`
--

LOCK TABLES `subscriber` WRITE;
/*!40000 ALTER TABLE `subscriber` DISABLE KEYS */;
INSERT INTO `subscriber` VALUES (1,'Yakir Zisman','059-1234564','yakir@gmail.com','active','yakir123','2025-01-23'),(2,'Nehoray Kroitoro','051-1234568','kroitor@gmail.com','frozen','nk123','2025-01-23'),(3,'Yael Cohen','052-1236666','yaelc12@gmail.com','active','yc2025','2025-01-22'),(4,'Daniel Levi','054-1234570','daniel.l@gmail.com','active','dl_01','2025-01-20'),(5,'Avigail Stern','058-1234569','avigail.s@gmail.com','frozen','avs123','2025-01-15'),(6,'Moshe Peretz','053-1234561','moshep@gmail.com','active','mp2025','2025-01-10'),(7,'Noa Bar','050-1234563','noabar@gmail.com','active','noa_1','2025-01-05'),(8,'Shai Gold','055-1234562','shaig@gmail.com','active','sg123','2025-01-01'),(9,'Lior Azulay','058-9876543','lioraz@gmail.com','frozen','la2024','2024-12-30'),(10,'Rina Katz','050-8765432','rina.k@gmail.com','active','rk_2024','2024-11-25'),(11,'Itay Shachar','054-7654321','itays@gmail.com','active','itay2024','2024-10-15'),(12,'Dana Blum','052-6543210','dana.blum@gmail.com','active','db_24','2024-09-10'),(13,'Tomer Hadar','051-5432109','tomer.h@gmail.com','active','th2024','2024-08-20'),(14,'Elior Ben-David','059-4321098','eliorbd@gmail.com','active','ebd123','2024-07-15'),(15,'Meital Golan','053-3210987','meital.g@gmail.com','frozen','mg2024','2024-06-05'),(16,'Yossi Avraham','055-2109876','yossi.a@gmail.com','active','ya123','2024-05-01');
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

-- Dump completed on 2025-01-27 16:54:22
