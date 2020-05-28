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
  CONSTRAINT `FK_chosenoptional_estimate` FOREIGN KEY (`estid`) REFERENCES `estimate` (`id`),
  CONSTRAINT `FK_chosenoptional_optional` FOREIGN KEY (`optid`) REFERENCES `optional` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.chosenoptional: ~0 rows (approximately)
/*!40000 ALTER TABLE `chosenoptional` DISABLE KEYS */;
/*!40000 ALTER TABLE `chosenoptional` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.estimate
CREATE TABLE IF NOT EXISTS `estimate` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `usrid` int(11) NOT NULL DEFAULT '0',
  `empid` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `prdid` int(11) NOT NULL,
  PRIMARY KEY (`id`,`usrid`) USING BTREE,
  KEY `FK__user` (`usrid`),
  KEY `FK_order_user` (`empid`),
  KEY `FK_estimate_product` (`prdid`),
  CONSTRAINT `FK_estimate_product` FOREIGN KEY (`prdid`) REFERENCES `product` (`id`),
  CONSTRAINT `FK_order_user` FOREIGN KEY (`empid`) REFERENCES `user` (`id`),
  CONSTRAINT `FK__user` FOREIGN KEY (`usrid`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.estimate: ~0 rows (approximately)
/*!40000 ALTER TABLE `estimate` DISABLE KEYS */;
/*!40000 ALTER TABLE `estimate` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.optional
CREATE TABLE IF NOT EXISTS `optional` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` enum('normal','sale') COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'normal',
  `prdid` int(11) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.optional: ~0 rows (approximately)
/*!40000 ALTER TABLE `optional` DISABLE KEYS */;
/*!40000 ALTER TABLE `optional` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.optionaltoproduct
CREATE TABLE IF NOT EXISTS `optionaltoproduct` (
  `prdid` int(11) NOT NULL,
  `optid` int(11) NOT NULL,
  PRIMARY KEY (`prdid`,`optid`),
  KEY `FK_optionaltoproduct_optional` (`optid`),
  CONSTRAINT `FK_optionaltoproduct_optional` FOREIGN KEY (`optid`) REFERENCES `optional` (`id`),
  CONSTRAINT `FK_optionaltoproduct_product` FOREIGN KEY (`prdid`) REFERENCES `product` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.optionaltoproduct: ~0 rows (approximately)
/*!40000 ALTER TABLE `optionaltoproduct` DISABLE KEYS */;
/*!40000 ALTER TABLE `optionaltoproduct` ENABLE KEYS */;

-- Dumping structure for table dbgroup2.product
CREATE TABLE IF NOT EXISTS `product` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.product: ~0 rows (approximately)
/*!40000 ALTER TABLE `product` DISABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Dumping data for table dbgroup2.user: ~1 rows (approximately)
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` (`id`, `username`, `password`, `email`, `name`, `surname`, `role`) VALUES
	(1, 'customer1', 'customer1pass', 'mail@example.org', 'Samuele', 'Negrini', 'customer');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
