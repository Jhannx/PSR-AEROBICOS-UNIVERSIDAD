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
-- Table structure for table `historial_auditoria`
--

DROP TABLE IF EXISTS `historial_auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historial_auditoria` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tabla_afectada` varchar(50) NOT NULL,
  `registro_id` int NOT NULL,
  `campo` varchar(50) DEFAULT NULL,
  `valor_anterior` text,
  `valor_nuevo` text,
  `accion` enum('INSERT','UPDATE','DELETE') NOT NULL,
  `usuario_id` int DEFAULT NULL,
  `fecha_hora` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `historial_auditoria_ibfk_1` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historial_auditoria`
--

LOCK TABLES `historial_auditoria` WRITE;
/*!40000 ALTER TABLE `historial_auditoria` DISABLE KEYS */;
INSERT INTO `historial_auditoria` VALUES (1,'tarifa',1,'monto','4500.00','5000.00','UPDATE',1,'2026-08-01 18:00:00'),(2,'persona',4,'telefono','3000000000','3004567890','UPDATE',2,'2026-08-10 18:15:00'),(3,'persona',8,'observaciones_salud',NULL,'Lesion rodilla recuperada','UPDATE',2,'2026-09-05 18:30:00'),(4,'horario',6,'hora_fin','20:50:00','21:00:00','UPDATE',1,'2026-09-10 17:00:00'),(5,'sesion',3,'profesor_usuario_id',NULL,'3','UPDATE',1,'2026-10-04 18:00:00'),(6,'usuario',11,'activo','0','1','UPDATE',1,'2026-09-15 18:40:00'),(7,'persona',16,'telefono','3190000000','3196789012','UPDATE',2,'2026-09-28 17:45:00'),(8,'sesion',6,'hora_fin',NULL,'2026-10-06 21:00:00','UPDATE',2,'2026-10-06 21:05:00'),(9,'persona',14,'apellidos','Ospina','Ospina Castillo','UPDATE',2,'2026-09-22 17:10:00'),(10,'tarifa',1,'monto','4000.00','4500.00','UPDATE',1,'2026-07-01 18:00:00'),(11,'horario',2,'hora_inicio','18:50:00','19:00:00','UPDATE',1,'2026-09-01 17:00:00'),(12,'sesion',1,'total_asistencias','2','3','UPDATE',2,'2026-10-05 18:05:00'),(13,'sesion',5,'total_recaudo','5000.00','10000.00','UPDATE',2,'2026-10-06 20:05:00'),(14,'usuario',5,'correo','mateo@temp.com','mateo.ramos@gmail.com','UPDATE',1,'2026-08-16 17:00:00'),(15,'persona',11,'observaciones_salud',NULL,'Hipertension controlada','UPDATE',2,'2026-09-15 18:35:00');
/*!40000 ALTER TABLE `historial_auditoria` ENABLE KEYS */;
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
