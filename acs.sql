-- MySQL dump 10.11
--
-- Host: localhost    Database: acs
-- ------------------------------------------------------
-- Server version	5.0.45-community-nt-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `atmerrorsstatsbean`
--

DROP TABLE IF EXISTS `atmerrorsstatsbean`;
CREATE TABLE `atmerrorsstatsbean` (
  `hostid` int(11) NOT NULL,
  `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `type` int(11) NOT NULL,
  `intervalStart` timestamp NOT NULL default '0000-00-00 00:00:00',
  `ATUCCRCErrors` bigint(20) default NULL,
  `ATUCFECErrors` bigint(20) default NULL,
  `ATUCHECErrors` bigint(20) default NULL,
  `CellDelin` bigint(20) default NULL,
  `CRCErrors` bigint(20) default NULL,
  `ErroredSecs` bigint(20) default NULL,
  `FECErrors` bigint(20) default NULL,
  `HECErrors` bigint(20) default NULL,
  `InitErrors` bigint(20) default NULL,
  `InitTimeouts` bigint(20) default NULL,
  `LinkRetrain` bigint(20) default NULL,
  `LossOfFraming` bigint(20) default NULL,
  `ReceiveBlocks` bigint(20) default NULL,
  `SeverelyErroredSecs` bigint(20) default NULL,
  `TransmitBlocks` bigint(20) default NULL,
  `LossOfPower` bigint(20) default NULL,
  `LossOfSignal` bigint(20) default NULL,
  PRIMARY KEY  (`hostid`,`time`,`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `backupbean`
--

DROP TABLE IF EXISTS `backupbean`;
CREATE TABLE `backupbean` (
  `hostid` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `cfg` longblob,
  PRIMARY KEY  (`hostid`,`type`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `configurationbean`
--

DROP TABLE IF EXISTS `configurationbean`;
CREATE TABLE `configurationbean` (
  `name` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `hwid` int(11) NOT NULL default '0',
  `config` longblob,
  `filename` varchar(250) character set utf8 collate utf8_bin default NULL,
  `version` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`hwid`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `datamodelbean`
--

DROP TABLE IF EXISTS `datamodelbean`;
CREATE TABLE `datamodelbean` (
  `name` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `type` varchar(250) character set utf8 collate utf8_bin default NULL,
  `min` bigint(20) NOT NULL,
  `max` bigint(20) NOT NULL,
  `length` int(11) NOT NULL,
  `description` longblob,
  `version` varchar(250) character set utf8 collate utf8_bin default NULL,
  `defaultvalue` varchar(250) character set utf8 collate utf8_bin default NULL,
  `writable` tinyint(4) NOT NULL,
  `trname` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `deviceprofile2softwarebean`
--

DROP TABLE IF EXISTS `deviceprofile2softwarebean`;
CREATE TABLE `deviceprofile2softwarebean` (
  `profileName` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `hwid` int(11) NOT NULL,
  `version` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`profileName`,`hwid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `deviceprofilebean`
--

DROP TABLE IF EXISTS `deviceprofilebean`;
CREATE TABLE `deviceprofilebean` (
  `name` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `informinterval` int(11) default NULL,
  `dayskeepstats` int(11) default NULL,
  `savestats` tinyint(4) default NULL,
  `saveLog` tinyint(4) default NULL,
  `saveParamValues` tinyint(4) default NULL,
  `saveParamValuesInterval` int(11) default NULL,
  `saveParamValuesOnChange` tinyint(4) default NULL,
  `saveParamValuesOnBoot` tinyint(4) default NULL,
  `dp2f_profileName` varchar(250) character set utf8 collate utf8_bin default NULL,
  `dp2f_hwid` int(11) default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `dslstatsbean`
--

DROP TABLE IF EXISTS `dslstatsbean`;
CREATE TABLE `dslstatsbean` (
  `hostid` int(11) NOT NULL,
  `time` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `DownstreamAttenuation` int(11) default NULL,
  `DownstreamCurrRate` int(11) default NULL,
  `DownstreamMaxRate` int(11) default NULL,
  `DownstreamNoiseMargin` int(11) default NULL,
  `DownstreamPower` int(11) default NULL,
  `UpstreamAttenuation` int(11) default NULL,
  `UpstreamCurrRate` int(11) default NULL,
  `UpstreamMaxRate` int(11) default NULL,
  `UpstreamNoiseMargin` int(11) default NULL,
  `UpstreamPower` int(11) default NULL,
  PRIMARY KEY  (`hostid`,`time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `hardwaremodelbean`
--

DROP TABLE IF EXISTS `hardwaremodelbean`;
CREATE TABLE `hardwaremodelbean` (
  `id` int(11) NOT NULL auto_increment,
  `oui` varchar(250) character set utf8 collate utf8_bin default NULL,
  `hclass` varchar(250) character set utf8 collate utf8_bin default NULL,
  `DisplayName` varchar(250) character set utf8 collate utf8_bin default NULL,
  `manufacturer` varchar(250) character set utf8 collate utf8_bin default NULL,
  `version` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `kmodels` (`oui`,`hclass`,`version`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8;

--
-- Table structure for table `hostsbean`
--

DROP TABLE IF EXISTS `hostsbean`;
CREATE TABLE `hostsbean` (
  `id` int(11) NOT NULL auto_increment,
  `serialno` varchar(250) character set utf8 collate utf8_bin default NULL,
  `url` varchar(250) character set utf8 collate utf8_bin default NULL,
  `configname` varchar(250) character set utf8 collate utf8_bin default NULL,
  `currentsoftware` varchar(250) character set utf8 collate utf8_bin default NULL,
  `sfwupdtime` timestamp NOT NULL default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP,
  `sfwupdres` varchar(250) character set utf8 collate utf8_bin default NULL,
  `cfgupdres` varchar(250) character set utf8 collate utf8_bin default NULL,
  `lastcontact` timestamp NOT NULL default '0000-00-00 00:00:00',
  `cfgupdtime` timestamp NOT NULL default '0000-00-00 00:00:00',
  `hardware` varchar(250) character set utf8 collate utf8_bin default NULL,
  `cfgversion` varchar(250) character set utf8 collate utf8_bin default NULL,
  `props` longblob,
  `hwid` int(11) default NULL,
  `username` varchar(250) character set utf8 collate utf8_bin default NULL,
  `password` varchar(250) character set utf8 collate utf8_bin default NULL,
  `authtype` int(11) default NULL,
  `customerid` varchar(250) character set utf8 collate utf8_bin default NULL,
  `conrequser` varchar(250) character set utf8 collate utf8_bin default NULL,
  `conreqpass` varchar(250) character set utf8 collate utf8_bin default NULL,
  `cfgforce` tinyint(4) default NULL,
  `profileName` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`id`),
  UNIQUE KEY `khosts` (`hwid`,`serialno`),
  CONSTRAINT `hostsbean_ibfk_1` FOREIGN KEY (`hwid`) REFERENCES `hardwaremodelbean` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8;

--
-- Table structure for table `propertybean`
--

DROP TABLE IF EXISTS `propertybean`;
CREATE TABLE `propertybean` (
  `parentId` int(11) NOT NULL,
  `type` int(11) NOT NULL,
  `name` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `value` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`parentId`,`type`,`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `scriptbean`
--

DROP TABLE IF EXISTS `scriptbean`;
CREATE TABLE `scriptbean` (
  `name` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `script` longblob,
  `description` varchar(250) character set utf8 collate utf8_bin default NULL,
  PRIMARY KEY  (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `softwarebean`
--

DROP TABLE IF EXISTS `softwarebean`;
CREATE TABLE `softwarebean` (
  `version` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `minversion` varchar(250) character set utf8 collate utf8_bin default NULL,
  `url` varchar(250) character set utf8 collate utf8_bin default NULL,
  `size` bigint(20) NOT NULL,
  `filename` varchar(250) character set utf8 collate utf8_bin default NULL,
  `hwid` int(11) NOT NULL,
  PRIMARY KEY  (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Table structure for table `softwaredetailbean`
--

DROP TABLE IF EXISTS `softwaredetailbean`;
CREATE TABLE `softwaredetailbean` (
  `hwid` int(11) NOT NULL,
  `version` varchar(250) character set utf8 collate utf8_bin NOT NULL,
  `paramNames` longblob,
  `methods` longblob,
  PRIMARY KEY  (`hwid`,`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2009-10-08 10:09:37
