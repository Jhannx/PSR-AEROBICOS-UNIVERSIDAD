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
-- Table structure for table `persona`
--

DROP TABLE IF EXISTS `persona`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `persona` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` enum('CC','TI','CE','PA') NOT NULL,
  `numero_documento` varchar(20) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `telefono` varchar(20) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `observaciones_salud` text,
  `fuente_registro` enum('ADMIN','AUTOREGISTRO') NOT NULL DEFAULT 'AUTOREGISTRO',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `persona`
--

LOCK TABLES `persona` WRITE;
/*!40000 ALTER TABLE `persona` DISABLE KEYS */;
INSERT INTO `persona` VALUES (1,'CC','1102812001','Jhan Paul','Corena Segura','3001234567','2002-05-14',NULL,'ADMIN','2026-08-01 08:00:00',1),(2,'CC','1102812002','Laura Vanessa','Mercado Hoyos','3012345678','1998-09-20',NULL,'ADMIN','2026-08-01 08:30:00',1),(3,'CC','1102812003','Carlos Andres','Mendoza Perez','3023456789','1995-03-12',NULL,'ADMIN','2026-08-05 09:00:00',1),(4,'CC','1102812004','Sofia Elena','Martinez Lopez','3004567890','1999-11-05','Asma leve','AUTOREGISTRO','2026-08-10 10:00:00',1),(5,'CC','1102812005','Mateo David','Ramos Gomez','3015678901','2001-07-22',NULL,'AUTOREGISTRO','2026-08-15 14:00:00',1),(6,'CC','1102812006','Valentina','Gomez Hoyos','3046789012','2003-01-30',NULL,'AUTOREGISTRO','2026-08-20 16:30:00',1),(7,'CC','1102812007','Andres Felipe','Petro Hernandez','3107890123','1997-12-18',NULL,'AUTOREGISTRO','2026-09-01 08:15:00',1),(8,'CC','1102812008','Mariana Lucia','Morales Vega','3118901234','2000-04-25','Lesion rodilla recuperada','AUTOREGISTRO','2026-09-05 11:00:00',1),(9,'CC','1102812009','Diego Alejandro','Soto Durango','3129012345','1996-08-14',NULL,'AUTOREGISTRO','2026-09-10 15:00:00',1),(10,'CC','1102812010','Isabella','Diaz Paternina','3130123456','2004-06-08',NULL,'AUTOREGISTRO','2026-09-12 17:00:00',1),(11,'CC','1102812011','Camilo Jose','Vergara Sierra','3141234567','1994-10-03','Hipertension controlada','ADMIN','2026-09-15 09:30:00',1),(12,'CC','1102812012','Gabriela','Mejia Benitez','3152345678','2002-02-17',NULL,'AUTOREGISTRO','2026-09-18 10:45:00',1),(13,'TI','1067912013','Samuel David','Polo Arrieta','3163456789','2008-11-29',NULL,'AUTOREGISTRO','2026-09-20 14:20:00',1),(14,'CC','1102812014','Daniela','Ospina Castillo','3174567890','1999-07-07',NULL,'AUTOREGISTRO','2026-09-22 16:00:00',1),(15,'CC','1102812015','Juan Pablo','Berrocal Ruiz','3185678901','2001-03-21',NULL,'AUTOREGISTRO','2026-09-25 18:00:00',1),(16,'CC','1102812016','Natalia Andrea','Conde Madera','3196789012','2000-09-15','Alergia al polvo','AUTOREGISTRO','2026-09-28 08:30:00',1),(17,'CC','1102812017','Esteban Manuel','Ruiz Montes','3207890123','1995-05-19',NULL,'AUTOREGISTRO','2026-10-01 07:45:00',1),(18,'CC','1102812018','Sara Victoria','Fuentes Luna','3218901234','2003-12-02',NULL,'AUTOREGISTRO','2026-10-02 09:15:00',1);
/*!40000 ALTER TABLE `persona` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-06 14:57:54
