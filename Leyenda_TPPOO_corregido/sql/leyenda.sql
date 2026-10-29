-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 29-10-2026 a las 22:40:59
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `leyenda`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `equipos`
--

CREATE TABLE `equipos` (
  `id` int(11) NOT NULL,
  `nombre` varchar(50) DEFAULT NULL,
  `pais` varchar(50) DEFAULT NULL,
  `tier` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `jugadores`
--

CREATE TABLE `jugadores` (
  `id` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `edad` int(11) NOT NULL,
  `ovr` int(11) NOT NULL,
  `precio` double NOT NULL,
  `resistencia` int(11) NOT NULL,
  `equipo` varchar(100) DEFAULT NULL,
  `dorsal` int(11) NOT NULL,
  `posicion` varchar(50) NOT NULL,
  `velocidad` int(11) DEFAULT NULL,
  `remate` int(11) DEFAULT NULL,
  `fuerza` int(11) DEFAULT NULL,
  `pase` int(11) DEFAULT NULL,
  `regate` int(11) DEFAULT NULL,
  `centros` int(11) DEFAULT NULL,
  `marcaje` int(11) DEFAULT NULL,
  `definicion` int(11) DEFAULT NULL,
  `control` int(11) DEFAULT NULL,
  `entradas` int(11) DEFAULT NULL,
  `tipo` varchar(30) NOT NULL,
  `cabezazo` int(11) DEFAULT NULL,
  `vision` int(11) DEFAULT NULL,
  `recuperacion` int(11) DEFAULT NULL,
  `reflejos` int(11) DEFAULT NULL,
  `atajada` int(11) DEFAULT NULL,
  `salida` int(11) DEFAULT NULL,
  `juego_con_los_pies` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `jugadores`
--

INSERT INTO `jugadores` (`id`, `nombre`, `edad`, `ovr`, `precio`, `resistencia`, `equipo`, `dorsal`, `posicion`, `velocidad`, `remate`, `fuerza`, `pase`, `regate`, `centros`, `marcaje`, `definicion`, `control`, `entradas`, `tipo`, `cabezazo`, `vision`, `recuperacion`, `reflejos`, `atajada`, `salida`, `juego_con_los_pies`) VALUES
(1, 'Nacho Gutierrez', 16, 50, 500000, 50, 'Boca Juniors', 77, 'Extremo Izquierdo', 50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 'DELANTERO', 50, NULL, NULL, NULL, NULL, NULL, NULL),
(2, 'Kiki Mpape', 40, 62, 500000, 50, 'Talleres', 12, 'Delantero Centro', 50, 53, 50, 50, 50, 50, 50, 53, 50, 50, 'DELANTERO', 53, NULL, NULL, NULL, NULL, NULL, NULL);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `equipos`
--
ALTER TABLE `equipos`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `jugadores`
--
ALTER TABLE `jugadores`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `equipos`
--
ALTER TABLE `equipos`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `jugadores`
--
ALTER TABLE `jugadores`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
