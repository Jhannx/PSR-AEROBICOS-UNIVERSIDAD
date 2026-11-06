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
-- Table structure for table `movimiento_cuenta`
--

DROP TABLE IF EXISTS `movimiento_cuenta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_cuenta` (
  `id` int NOT NULL AUTO_INCREMENT,
  `persona_id` int NOT NULL,
  `llegada_id` int DEFAULT NULL,
  `monto_recibido` decimal(10,2) NOT NULL,
  `monto_abonado` decimal(10,2) NOT NULL,
  `metodo_pago` enum('EFECTIVO','NEQUI','DAVIPLATA','TRANSFERENCIA') NOT NULL DEFAULT 'EFECTIVO',
  `tipo_movimiento` enum('ABONO','REEMBOLSO') NOT NULL DEFAULT 'ABONO',
  `vuelto_entregado` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_hora` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `usuario_id` int NOT NULL,
  `nota` text,
  PRIMARY KEY (`id`),
  KEY `persona_id` (`persona_id`),
  KEY `llegada_id` (`llegada_id`),
  KEY `usuario_id` (`usuario_id`),
  CONSTRAINT `movimiento_cuenta_ibfk_1` FOREIGN KEY (`persona_id`) REFERENCES `persona` (`id`) ON DELETE CASCADE,
  CONSTRAINT `movimiento_cuenta_ibfk_2` FOREIGN KEY (`llegada_id`) REFERENCES `llegada` (`id`) ON DELETE SET NULL,
  CONSTRAINT `movimiento_cuenta_ibfk_3` FOREIGN KEY (`usuario_id`) REFERENCES `usuario` (`id`) ON DELETE RESTRICT,
  CONSTRAINT `movimiento_cuenta_chk_1` CHECK (((`monto_recibido` >= 0.00) and (`monto_abonado` >= 0.00)))
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_cuenta`
--

LOCK TABLES `movimiento_cuenta` WRITE;
/*!40000 ALTER TABLE `movimiento_cuenta` DISABLE KEYS */;
INSERT INTO `movimiento_cuenta` VALUES (1,11,NULL,50000.00,50000.00,'TRANSFERENCIA','ABONO',1,'2026-09-15 18:30:00',2,'Abono anticipado paquete 10 clases'),(2,4,1,10000.00,5000.00,'EFECTIVO','ABONO',1,'2026-10-05 16:55:00',2,'Pago sesion 1'),(3,5,2,5000.00,5000.00,'NEQUI','ABONO',1,'2026-10-05 16:56:00',2,'Pago sesion 1'),(4,6,3,20000.00,7000.00,'DAVIPLATA','ABONO',1,'2026-10-05 16:58:00',2,'Pago sesion especial'),(5,7,4,10000.00,4000.00,'EFECTIVO','ABONO',1,'2026-10-05 18:50:00',2,'Pago sesion descuento'),(6,8,5,5000.00,5000.00,'NEQUI','ABONO',1,'2026-10-05 18:53:00',2,'Pago sesion 2'),(7,9,6,10000.00,5000.00,'EFECTIVO','ABONO',1,'2026-10-05 19:55:00',2,'Pago sesion 3'),(8,4,7,5000.00,5000.00,'NEQUI','ABONO',1,'2026-10-06 17:25:00',2,'Pago sesion 4'),(9,5,8,5000.00,4000.00,'TRANSFERENCIA','ABONO',1,'2026-10-06 17:27:00',2,'Pago sesion descuento'),(10,10,10,5000.00,5000.00,'EFECTIVO','ABONO',1,'2026-10-06 18:55:00',2,'Pago sesion 5'),(11,12,11,15000.00,5000.00,'NEQUI','ABONO',1,'2026-10-06 19:58:00',2,'Pago sesion 6'),(12,4,12,5000.00,5000.00,'EFECTIVO','ABONO',1,'2026-10-07 16:53:00',2,'Pago sesion 7'),(13,5,13,5000.00,5000.00,'DAVIPLATA','ABONO',1,'2026-10-07 18:48:00',2,'Pago sesion 8'),(14,4,16,5000.00,5000.00,'NEQUI','ABONO',1,'2026-10-08 17:25:00',2,'Pago sesion 10'),(15,5,17,5000.00,5000.00,'EFECTIVO','ABONO',1,'2026-10-08 18:52:00',2,'Pago sesion 11'),(16,7,NULL,5000.00,5000.00,'EFECTIVO','REEMBOLSO',1,'2026-10-08 19:30:00',2,'Devolucion por saldo a favor no usado');
/*!40000 ALTER TABLE `movimiento_cuenta` ENABLE KEYS */;
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
