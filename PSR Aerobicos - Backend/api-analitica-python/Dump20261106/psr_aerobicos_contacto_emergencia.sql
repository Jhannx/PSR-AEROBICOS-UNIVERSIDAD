CREATE DATABASE  IF NOT EXISTS `psr_aerobicos` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `psr_aerobicos`;
-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: psr_aerobicos
-- ------------------------------------------------------
-- Server version	8.0.42

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
-- Table structure for table `contacto_emergencia`
--

DROP TABLE IF EXISTS `contacto_emergencia`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `contacto_emergencia` (
  `id` int NOT NULL AUTO_INCREMENT,
  `persona_id` int NOT NULL,
  `nombre` varchar(150) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `observaciones` text,
  `actualizado_en` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `persona_id` (`persona_id`),
  CONSTRAINT `contacto_emergencia_ibfk_1` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE,
  CONSTRAINT `contacto_emergencia_chk_1` CHECK (((`nombre` is not null) or (`telefono` is not null) or (`observaciones` is not null)))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contacto_emergencia`
--

LOCK TABLES `contacto_emergencia` WRITE;
/*!40000 ALTER TABLE `contacto_emergencia` DISABLE KEYS */;
INSERT INTO `contacto_emergencia` VALUES (1,1,'Marta Segura','3009991111','Madre','2026-08-01 08:00:00'),(2,2,'Pedro Mercado','3019992222','Padre','2026-08-01 08:30:00'),(3,3,'Lucia Mendoza','3029993333','Hermana','2026-08-05 09:00:00'),(4,4,'Jorge Martinez','3009994444','Padre','2026-08-10 10:00:00'),(5,5,'Claudia Ramos','3019995555','Madre','2026-08-15 14:00:00'),(6,6,'Hernan Gomez','3049996666','Tio','2026-08-20 16:30:00'),(7,7,'Gloria Petro','3109997777','Esposa','2026-09-01 08:15:00'),(8,8,'Felipe Morales','3119998888','Hermano','2026-09-05 11:00:00'),(9,9,'Sandra Soto','3129999999','Madre','2026-09-10 15:00:00'),(10,10,'Marcos Diaz','3139990000','Padre','2026-09-12 17:00:00'),(11,11,'Carmen Vergara','3149991122','Hermana','2026-09-15 09:30:00'),(12,12,'Patricia Benitez','3159992233','Madre','2026-09-18 10:45:00'),(13,13,'Ricardo Polo','3169993344','Padre acudiente','2026-09-20 14:20:00'),(14,14,'Alvaro Ospina','3179994455','Padre','2026-09-22 16:00:00'),(15,15,'Elena Berrocal','3189995566','Hermana','2026-09-25 18:00:00');
/*!40000 ALTER TABLE `contacto_emergencia` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-06 14:57:53
