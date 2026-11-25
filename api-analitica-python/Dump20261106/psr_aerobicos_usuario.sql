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
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id` int NOT NULL AUTO_INCREMENT,
  `numero_documento` varchar(20) DEFAULT NULL,
  `correo` varchar(150) DEFAULT NULL,
  `password_hash` varchar(255) NOT NULL,
  `persona_id` int DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_creacion` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `numero_documento` (`numero_documento`),
  UNIQUE KEY `correo` (`correo`),
  KEY `persona_id` (`persona_id`),
  CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE,
  CONSTRAINT `usuario_chk_1` CHECK (((`numero_documento` is not null) or (`correo` is not null)))
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'1102812001','admin@psr.com','$2a$10$abcdefghijklmnopqrstuv1',1,1,'2026-08-01 08:00:00'),(2,'1102812002','recepcion@psr.com','$2a$10$abcdefghijklmnopqrstuv2',2,1,'2026-08-01 08:30:00'),(3,'1102812003','carlos.mendoza@psr.com','$2a$10$abcdefghijklmnopqrstuv3',3,1,'2026-08-05 09:00:00'),(4,'1102812004','sofia.martinez@gmail.com','$2a$10$abcdefghijklmnopqrstuv4',4,1,'2026-08-10 10:00:00'),(5,'1102812005','mateo.ramos@gmail.com','$2a$10$abcdefghijklmnopqrstuv5',5,1,'2026-08-15 14:00:00'),(6,'1102812006','valentina.gomez@gmail.com','$2a$10$abcdefghijklmnopqrstuv6',6,1,'2026-08-20 16:30:00'),(7,'1102812007','andres.petro@gmail.com','$2a$10$abcdefghijklmnopqrstuv7',7,1,'2026-09-01 08:15:00'),(8,'1102812008','mariana.morales@gmail.com','$2a$10$abcdefghijklmnopqrstuv8',8,1,'2026-09-05 11:00:00'),(9,'1102812009','diego.soto@gmail.com','$2a$10$abcdefghijklmnopqrstuv9',9,1,'2026-09-10 15:00:00'),(10,'1102812010','isabella.diaz@gmail.com','$2a$10$abcdefghijklmnopqrstuv0',10,1,'2026-09-12 17:00:00'),(11,'1102812011','camilo.vergara@gmail.com','$2a$10$abcdefghijklmnopqrstua1',11,1,'2026-09-15 09:30:00'),(12,'1102812012','gabriela.mejia@gmail.com','$2a$10$abcdefghijklmnopqrstua2',12,1,'2026-09-18 10:45:00'),(13,'1067912013','samuel.polo@gmail.com','$2a$10$abcdefghijklmnopqrstua3',13,1,'2026-09-20 14:20:00'),(14,'1102812014','daniela.ospina@gmail.com','$2a$10$abcdefghijklmnopqrstua4',14,1,'2026-09-22 16:00:00'),(15,'1102812015','juan.berrocal@gmail.com','$2a$10$abcdefghijklmnopqrstua5',15,1,'2026-09-25 18:00:00');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-11-06 14:57:55
