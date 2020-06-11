-- --------------------------------------------------------
-- Host:                         tiw.nsanitygaming.it
-- Server version:               5.5.65-MariaDB - MariaDB Server
-- Server OS:                    Linux
-- HeidiSQL Version:             11.0.0.5919
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Dumping database structure for dbgroup2
CREATE DATABASE IF NOT EXISTS `dbgroup2` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;
USE `dbgroup2`;

-- Dumping structure for table dbgroup2.chosenoptional
CREATE TABLE IF NOT EXISTS `chosenoptional` (
  `estid` int(11) NOT NULL,
  `optid` int(11) NOT NULL,
  PRIMARY KEY (`estid`,`optid`) USING BTREE,
  KEY `FK_chosenoptional_optionaltoproduct` (`optid`) USING BTREE,
  CONSTRAINT `FK_chosenoptional_estimate` FOREIGN KEY (`estid`) REFERENCES `estimate` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_chosenoptional_optional` FOREIGN KEY (`optid`) REFERENCES `optional` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.chosenoptional: ~49 rows (approximately)
/*!40000 ALTER TABLE `chosenoptional` DISABLE KEYS */;
INSERT INTO `chosenoptional` (`estid`, `optid`) VALUES
	(14, 8),
	(15, 5),
	(16, 3),
	(16, 4),
	(17, 3),
	(17, 4),
	(19, 8),
	(20, 2),
	(21, 4),
	(21, 7),
	(22, 3),
	(23, 3),
	(24, 1),
	(24, 2),
	(25, 3),
	(25, 9),
	(26, 7),
	(27, 8),
	(28, 2),
	(29, 4),
	(29, 7),
	(30, 3),
	(31, 4),
	(31, 7),
	(32, 1),
	(32, 7),
	(32, 8),
	(33, 1),
	(34, 1),
	(34, 2),
	(35, 4),
	(35, 7),
	(36, 1),
	(36, 2),
	(37, 3),
	(38, 4),
	(39, 3),
	(40, 3),
	(41, 7),
	(42, 3),
	(42, 5),
	(42, 9),
	(42, 10),
	(43, 3),
	(43, 7),
	(44, 3),
	(44, 7),
	(45, 8),
	(46, 5);
/*!40000 ALTER TABLE `chosenoptional` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.estimate
CREATE TABLE IF NOT EXISTS `estimate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usrid` int(11) NOT NULL DEFAULT '0',
  `prdid` int(11) NOT NULL,
  `empid` int(11) DEFAULT NULL,
  `price` float DEFAULT NULL,
  PRIMARY KEY (`id`,`usrid`) USING BTREE,
  KEY `FK__user` (`usrid`),
  KEY `FK_estimate_product` (`prdid`),
  KEY `FK_order_user` (`empid`),
  CONSTRAINT `FK_estimate_product` FOREIGN KEY (`prdid`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_order_user` FOREIGN KEY (`empid`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=47 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.estimate: ~32 rows (approximately)
/*!40000 ALTER TABLE `estimate` DISABLE KEYS */;
INSERT INTO `estimate` (`id`, `usrid`, `prdid`, `empid`, `price`) VALUES
	(14, 2, 6, 1, 456789),
	(15, 2, 2, 1, 71),
	(16, 4, 1, 1, 85),
	(17, 4, 1, 1, 647384),
	(19, 3, 6, 1, 55),
	(20, 4, 3, 1, 999),
	(21, 4, 5, 1, 222),
	(22, 3, 4, 1, 50),
	(23, 3, 1, 1, 24.4444),
	(24, 3, 3, 1, 20.2),
	(25, 3, 2, 1, 896),
	(26, 3, 4, 1, 66),
	(27, 3, 6, 1, 88),
	(28, 3, 3, 1, 55),
	(29, 3, 5, 1, 88),
	(30, 2, 1, 1, 10),
	(31, 2, 5, 1, 22.455),
	(32, 2, 6, 1, 44),
	(33, 2, 3, 1, 5),
	(34, 2, 3, 1, 0),
	(35, 2, 5, 1, 24.4),
	(36, 2, 3, 1, 0.25),
	(37, 2, 1, 1, 44),
	(38, 2, 1, 1, 22.44),
	(39, 4, 2, NULL, NULL),
	(40, 4, 4, 1, 55),
	(41, 4, 5, NULL, NULL),
	(42, 4, 2, 1, 44.56),
	(43, 2, 4, 1, 0),
	(44, 2, 4, NULL, NULL),
	(45, 2, 6, 1, 33222),
	(46, 4, 2, NULL, NULL);
/*!40000 ALTER TABLE `estimate` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.optional
CREATE TABLE IF NOT EXISTS `optional` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('NORMAL','SALE') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'NORMAL',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.optional: ~9 rows (approximately)
/*!40000 ALTER TABLE `optional` DISABLE KEYS */;
INSERT INTO `optional` (`id`, `name`, `type`) VALUES
	(1, 'Aria condizionata', 'NORMAL'),
	(2, 'Riscaldamento', 'NORMAL'),
	(3, 'Portabiciclette', 'SALE'),
	(4, 'Tendine parasole', 'NORMAL'),
	(5, 'Sedili in ecopelle', 'SALE'),
	(7, 'Wi-Fi', 'NORMAL'),
	(8, 'Propulsione nucleare ad impulso', 'NORMAL'),
	(9, 'Vagone bar', 'SALE'),
	(10, 'Vetri fotocromatici', 'NORMAL');
/*!40000 ALTER TABLE `optional` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.optionaltoproduct
CREATE TABLE IF NOT EXISTS `optionaltoproduct` (
  `prdid` int(11) NOT NULL,
  `optid` int(11) NOT NULL,
  PRIMARY KEY (`prdid`,`optid`),
  KEY `FK_optionaltoproduct_optional` (`optid`),
  CONSTRAINT `FK_optionaltoproduct_optional` FOREIGN KEY (`optid`) REFERENCES `optional` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_optionaltoproduct_product` FOREIGN KEY (`prdid`) REFERENCES `product` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.optionaltoproduct: ~15 rows (approximately)
/*!40000 ALTER TABLE `optionaltoproduct` DISABLE KEYS */;
INSERT INTO `optionaltoproduct` (`prdid`, `optid`) VALUES
	(1, 3),
	(1, 4),
	(2, 3),
	(2, 5),
	(2, 9),
	(2, 10),
	(3, 1),
	(3, 2),
	(4, 3),
	(4, 7),
	(5, 4),
	(5, 7),
	(6, 1),
	(6, 7),
	(6, 8);
/*!40000 ALTER TABLE `optionaltoproduct` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.product
CREATE TABLE IF NOT EXISTS `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.product: ~6 rows (approximately)
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
INSERT INTO `product` (`id`, `name`, `image`) VALUES
	(1, 'ETR 425 CORADIA MERIDIAN', 'ETR_425_CORADIA_MERIDIAN.jpg'),
	(2, 'ETR 245 CORADIA MERIDIAN', 'ETR_245_CORADIA_MERIDIAN.jpg'),
	(3, 'Elettrotreno TSR', 'ELETTROTRENO_TSR.jpg'),
	(4, 'Elettrotreno TAF', 'ELETTROTRENO_TAF.jpg'),
	(5, 'Elettromotrice ALe 582', 'ELETTROMOTRICE_ALe_582.jpg'),
	(6, 'ETR 621 CARAVAGGIO', 'ETR_621_CARAVAGGIO.jpeg');
/*!40000 ALTER TABLE `product` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `surname` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `role` enum('customer','employee') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'customer',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `username` (`username`),
  UNIQUE KEY `email` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.user: ~6 rows (approximately)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `username`, `password`, `email`, `name`, `surname`, `role`) VALUES
	(1, 'employee1', 'employee1pass', 'mail2@example.org', 'Aldo', 'Plenzich', 'employee'),
	(2, 'customer1', 'bellanapoli123', 'LuigiDiMAyo@esteri.gov.it', 'Luigi', 'Di Maio', 'customer'),
	(3, 'mattiasala', '12345', 'mattiasala@polimi.it', 'Mattia', 'Sala', 'customer'),
	(4, 'snegrini', 'aassddff', 'negrinisamuele@outlook.com', 'Samuele', 'Negrini', 'customer'),
	(5, '@!u_', '@!u_', 'ws@dd.cca', '@!u_', '@!u_', 'customer'),
	(6, 'testuser', 'test123', 'testuser@testmail.com', 'testname', 'testsurname', 'customer');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
