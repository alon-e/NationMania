-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema DbMysql05
-- -----------------------------------------------------
-- DB for NationMania Proj.

-- -----------------------------------------------------
-- Schema DbMysql05
--
-- DB for NationMania Proj.
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `DbMysql05` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `DbMysql05` ;

-- -----------------------------------------------------
-- Table `DbMysql05`.`continents`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`continents` (
  `continent_id` INT NOT NULL AUTO_INCREMENT,
  `continent_name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`continent_id`),
  UNIQUE INDEX `continent_name_UNIQUE` (`continent_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql05`.`nations`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`nations` (
  `nation_id` INT NOT NULL AUTO_INCREMENT,
  `nation_name` VARCHAR(50) NULL,
  `nation_caption` VARCHAR(45) NULL,
  `nation_weight` INT NULL DEFAULT 5,
  `continent_id` INT NOT NULL,
  PRIMARY KEY (`nation_id`),
  INDEX `fk_Nations_Continents1_idx` (`continent_id` ASC),
  UNIQUE INDEX `nation_name_UNIQUE` (`nation_name` ASC),
  CONSTRAINT `fk_Nations_Continents1`
    FOREIGN KEY (`continent_id`)
    REFERENCES `DbMysql05`.`continents` (`continent_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql05`.`relation_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`relation_types` (
  `relation_type_id` INT NOT NULL AUTO_INCREMENT,
  `relation_type_name` VARCHAR(45) NOT NULL,
  `relation_type_caption` VARCHAR(45) NULL,
  `relation_type_weight` INT NOT NULL DEFAULT 5 COMMENT 'determine how difficult this type is\nscale: from 1 to 10\ndefault: 5',
  PRIMARY KEY (`relation_type_id`),
  UNIQUE INDEX `relation_type_name_UNIQUE` (`relation_type_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql05`.`fact_types`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`fact_types` (
  `fact_type_id` INT NOT NULL AUTO_INCREMENT,
  `fact_type_name` VARCHAR(45) NOT NULL,
  `fact_type_caption` VARCHAR(45) NULL,
  `fact_type_weight` INT NOT NULL DEFAULT 5 COMMENT 'determine how difficult this type is\nscale: from 1 to 10\ndefault: 5',
  PRIMARY KEY (`fact_type_id`),
  UNIQUE INDEX `fact_type_name_UNIQUE` (`fact_type_name` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql05`.`facts`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`facts` (
  `fact_id` INT NOT NULL AUTO_INCREMENT,
  `fact_name` VARCHAR(45) NOT NULL,
  `fact_caption` VARCHAR(250) NOT NULL,
  `fact_weight` INT NOT NULL DEFAULT 5,
  `nation_id` INT NOT NULL,
  `relation_type_id` INT NOT NULL,
  `fact_type_id` INT NOT NULL,
  `fact_reported` INT NOT NULL COMMENT 'dead giveaway',
  PRIMARY KEY (`fact_id`),
  INDEX `fk_Facts_Nations_idx` (`nation_id` ASC),
  INDEX `fk_Facts_Fact_Types1_idx` (`relation_type_id` ASC),
  INDEX `fk_Facts_Fact_Types2_idx` (`fact_type_id` ASC),
  UNIQUE INDEX `fact_name_UNIQUE` (`fact_name` ASC),
  CONSTRAINT `fk_Facts_Nations`
    FOREIGN KEY (`nation_id`)
    REFERENCES `DbMysql05`.`nations` (`nation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Facts_Fact_Types1`
    FOREIGN KEY (`relation_type_id`)
    REFERENCES `DbMysql05`.`relation_types` (`relation_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Facts_Fact_Types2`
    FOREIGN KEY (`fact_type_id`)
    REFERENCES `DbMysql05`.`fact_types` (`fact_type_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql05`.`users`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`users` (
  `user_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(25) NOT NULL,
  `password` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NULL,
  `top_score` INT NULL,
  `home_nation_id` INT NOT NULL,
  `last_login` DATETIME NULL,
  `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  INDEX `fk_Users_Nations1_idx` (`home_nation_id` ASC),
  CONSTRAINT `fk_Users_Nations1`
    FOREIGN KEY (`home_nation_id`)
    REFERENCES `DbMysql05`.`nations` (`nation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);


-- -----------------------------------------------------
-- Table `DbMysql05`.`games`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`games` (
  `game_id` INT NOT NULL AUTO_INCREMENT,
  `user_id` INT NOT NULL,
  `game_score` INT NULL,
  `nations_discovered` INT NULL,
  `time_stp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX `fk_History_Users1_idx` (`user_id` ASC),
  PRIMARY KEY (`game_id`),
  CONSTRAINT `fk_History_Users10`
    FOREIGN KEY (`user_id`)
    REFERENCES `DbMysql05`.`users` (`user_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `DbMysql05`.`game_info`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `DbMysql05`.`game_info` (
  `game_info_id` INT NOT NULL AUTO_INCREMENT,
  `game_id` INT NOT NULL,
  `nation_id` INT NOT NULL,
  `fact_id` INT NULL,
  INDEX `fk_History_Facts1_idx` (`fact_id` ASC),
  INDEX `fk_Game_Info_Nations1_idx` (`nation_id` ASC),
  INDEX `fk_Game_Info_Games1_idx` (`game_id` ASC),
  PRIMARY KEY (`game_info_id`),
  CONSTRAINT `fk_History_Facts1`
    FOREIGN KEY (`fact_id`)
    REFERENCES `DbMysql05`.`facts` (`fact_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Game_Info_Nations1`
    FOREIGN KEY (`nation_id`)
    REFERENCES `DbMysql05`.`nations` (`nation_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_Game_Info_Games1`
    FOREIGN KEY (`game_id`)
    REFERENCES `DbMysql05`.`games` (`game_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

