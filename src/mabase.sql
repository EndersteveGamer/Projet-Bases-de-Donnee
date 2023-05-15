-- phpMyAdmin SQL Dump
-- version 4.7.1
-- https://www.phpmyadmin.net/
--
-- Hôte : sql7.freemysqlhosting.net
-- Généré le :  sam. 13 mai 2023 à 21:06
-- Version du serveur :  5.5.62-0ubuntu0.14.04.1
-- Version de PHP :  7.0.33-0ubuntu0.16.04.16

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de données :  `sql7615548`
--

-- --------------------------------------------------------

--
-- Structure de la table `APPROVISIONNER`
--

CREATE TABLE `APPROVISIONNER` (
  `IDMelange` int(6) NOT NULL,
  `SemaineAppro` int(2) NOT NULL,
  `QuantiteMelange` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Structure de la table `CLIENTE`
--

CREATE TABLE `CLIENTE` (
  `IDCli` int(6) NOT NULL,
  `NomCli` text NOT NULL,
  `AdrCli` text NOT NULL,
  `VilleCli` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `CLIENTE`
--

INSERT INTO `CLIENTE` (`IDCli`, `NomCli`, `AdrCli`, `VilleCli`) VALUES
(193418, 'Amar Di', '150 Boulevard du Pain', 'Besançon'),
(669471, 'Camille Onette', '10 Rue de la Fontaine', 'Besançon'),
(895447, 'Jean-Michel Hufflen', '10 Rue de la rue', 'Besançon'),
(895449, 'Cote Deboeuf', '9 Rue de la boucherie', 'Steak');

-- --------------------------------------------------------

--
-- Structure de la table `LIVRER`
--

CREATE TABLE `LIVRER` (
  `IDCli` int(6) NOT NULL,
  `IDPain` int(6) NOT NULL,
  `DateLivraison` date NOT NULL,
  `NombreDePains` int(3) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `LIVRER`
--

INSERT INTO `LIVRER` (`IDCli`, `IDPain`, `DateLivraison`, `NombreDePains`) VALUES
(193418, 794567, '2023-05-22', 3),
(669471, 173246, '2023-05-14', 5),
(895447, 123729, '2023-05-16', 10),
(895447, 794567, '2023-05-15', 4),
(895449, 173246, '2023-05-10', 2);

-- --------------------------------------------------------

--
-- Structure de la table `MELANGE`
--

CREATE TABLE `MELANGE` (
  `IDMelange` int(6) NOT NULL,
  `DescMelange` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `MELANGE`
--

INSERT INTO `MELANGE` (`IDMelange`, `DescMelange`) VALUES
(145679, 'Beaucoup de farine de blé et peu de levure'),
(545674, 'Peu de farine de blé et beaucoup de levure'),
(793486, 'Beaucoup de farine de blé et beaucoup de levure'),
(796321, 'Recette secrète');

-- --------------------------------------------------------

--
-- Structure de la table `PAIN`
--

CREATE TABLE `PAIN` (
  `IDPain` int(6) NOT NULL,
  `DescPain` text NOT NULL,
  `PrixPainHT` float DEFAULT NULL,
  `IDMelange` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

--
-- Déchargement des données de la table `PAIN`
--

INSERT INTO `PAIN` (`IDPain`, `DescPain`, `PrixPainHT`, `IDMelange`) VALUES
(123729, 'La spéciale', 2, 796321),
(124379, 'Baguette moyenne', 0.75, 545674),
(173246, 'La grosse baguette', 1, 793486),
(794567, 'Le petit pain', 0.5, 145679);

--
-- Index pour les tables déchargées
--

--
-- Index pour la table `APPROVISIONNER`
--
ALTER TABLE `APPROVISIONNER`
  ADD PRIMARY KEY (`IDMelange`,`SemaineAppro`);

--
-- Index pour la table `CLIENTE`
--
ALTER TABLE `CLIENTE`
  ADD PRIMARY KEY (`IDCli`);

--
-- Index pour la table `LIVRER`
--
ALTER TABLE `LIVRER`
  ADD PRIMARY KEY (`IDCli`,`IDPain`,`DateLivraison`);

--
-- Index pour la table `MELANGE`
--
ALTER TABLE `MELANGE`
  ADD PRIMARY KEY (`IDMelange`);

--
-- Index pour la table `PAIN`
--
ALTER TABLE `PAIN`
  ADD PRIMARY KEY (`IDPain`);

--
-- AUTO_INCREMENT pour les tables déchargées
--

--
-- AUTO_INCREMENT pour la table `CLIENTE`
--
ALTER TABLE `CLIENTE`
  MODIFY `IDCli` int(6) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=895450;COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
