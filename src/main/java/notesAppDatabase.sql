-- phpMyAdmin SQL Dump
-- version 4.9.5
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 20, 2020 at 08:33 PM
-- Server version: 10.4.13-MariaDB
-- PHP Version: 7.2.29

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
SET AUTOCOMMIT = 0;
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `u287406969_quizup`
--

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` int(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `user_id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `user_id`) VALUES
(120, 'Sheridan', 44),
(118, 'cat2', 45),
(116, 'cat1', 45),
(115, 'baby', 43),
(114, 'full', 43),
(113, 'Lambton', 44),
(121, 'shopping', 44),
(122, 'shopping', 46),
(123, 'college', 46),
(124, 'trip', 46),
(125, 'android', 47),
(126, 'iOS', 47),
(127, 'shopping', 48),
(128, 'games', 48);

-- --------------------------------------------------------

--
-- Table structure for table `locations`
--

CREATE TABLE `locations` (
  `id` int(20) NOT NULL,
  `lat` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `lng` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `place` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `note_id` int(20) NOT NULL,
  `timestamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `locations`
--

INSERT INTO `locations` (`id`, `lat`, `lng`, `place`, `note_id`, `timestamp`) VALUES
(27, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 18:11:07'),
(28, '43.653225000000006', '-79.38318333333333', '', 71, '2020-07-20 18:18:38'),
(29, '43.653225000000006', '-79.38318333333333', '', 71, '2020-07-20 18:42:38'),
(30, '43.653225000000006', '-79.38318333333333', '', 70, '2020-07-20 19:01:09'),
(31, '37.4219907', '-122.0839942', '', 75, '2020-07-20 19:36:45'),
(32, '43.653225000000006', '-79.38318333333333', '', 75, '2020-07-20 19:38:28'),
(33, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 19:41:54'),
(34, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 19:44:05'),
(35, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 20:13:29'),
(36, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 20:15:39'),
(37, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 20:15:39'),
(38, '43.653225000000006', '-79.38318333333333', '', -1, '2020-07-20 20:20:16'),
(39, '43.653225000000006', '-79.38318333333333', '', 81, '2020-07-20 20:20:38'),
(40, '43.653225000000006', '-79.38318333333333', '', 78, '2020-07-20 20:31:17');

-- --------------------------------------------------------

--
-- Table structure for table `notes`
--

CREATE TABLE `notes` (
  `id` int(10) NOT NULL,
  `created_date` varchar(255) NOT NULL,
  `updated_date` varchar(255) NOT NULL,
  `title` varchar(255) NOT NULL,
  `category_id` int(10) DEFAULT NULL,
  `content` varchar(255) NOT NULL,
  `location` varchar(255) NOT NULL,
  `secured` varchar(255) NOT NULL,
  `image` text NOT NULL,
  `audio` varchar(255) NOT NULL,
  `video` varchar(255) NOT NULL,
  `user_id` int(10) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `notes`
--

INSERT INTO `notes` (`id`, `created_date`, `updated_date`, `title`, `category_id`, `content`, `location`, `secured`, `image`, `audio`, `video`, `user_id`) VALUES
(81, '2020-07-20 16:20:15', '2020-07-20 16:20:38', 'groceriesdfsdsf', 128, 'shopping groceriesdssadsaddsa', '', '', '', '', '', 48),
(80, '2020-07-20 16:15:32', '2020-07-20 16:15:32', 'Ios notes', 126, 'IOS demo note', '', '', '', 'http://alllinks.online/andproject/uploads/dd6d877e57e61cae532ee9c9f611f76c.3gp', '', 47),
(77, '2020-07-20 15:44:03', '2020-07-20 15:44:03', 'trip', 121, 'asagcfd my', '', '', '', '', '', 44),
(78, '2020-07-20 16:13:28', '2020-07-20 16:31:16', 'android Notes', 126, 'android demo', '', '', '', 'http://alllinks.online/andproject/uploads/63d659f6df9ffce45589637811e5090b.3gp', '', 47),
(79, '2020-07-20 16:15:34', '2020-07-20 16:15:34', 'Ios notes', 126, 'IOS demo note', '', '', '', 'http://alllinks.online/andproject/uploads/dd6d877e57e61cae532ee9c9f611f76c.3gp', '', 47),
(76, '2020-07-20 15:41:52', '2020-07-20 15:41:52', 'fdsfdsdfsdsf', 120, 'sheridan', '', '', '', '', '', 44),
(75, '2020-07-20 15:35:55', '2020-07-20 15:38:26', 'demo', 113, 'demo123', '', '', '', 'http://alllinks.online/andproject/uploads/4ad875d1218ffd32db68ba7249d42f4c.3gp', '', 44),
(74, '2020-07-20 15:20:01', '2020-07-20 15:20:01', 'atitle', 118, 'dfd', '', '', '', '', '', 45),
(72, '2020-07-20 15:18:03', '2020-07-20 15:18:03', 'title1', 116, 'note1', '', '', '', '', '', 45),
(73, '2020-07-20 15:19:38', '2020-07-20 15:19:38', 'zttitle', 116, 'title2', '', '', '', '', '', 45),
(71, '2020-07-20 14:11:06', '2020-07-20 14:42:38', 'fffff', 113, 'dfdsdfsdsf', '', '', '', 'http://alllinks.online/andproject/uploads/566a26e897fe1f2218e232b83689c366.3gp', '', 44),
(69, '2020-07-20 12:50:24', '2020-07-20 12:50:24', 'Pwer rangers SPD', -1, 'SPACE PATROL DELTA \nzooooooommm!', '', '', '', '', '', 43),
(70, '2020-07-20 13:05:32', '2020-07-20 15:01:08', 'demo', 113, '1234', '', '', '', 'http://alllinks.online/andproject/uploads/f7a42b9401c0eabf12d6fe529f84ad8d.3gp', '', 44),
(67, '2020-07-20 12:49:16', '2020-07-20 12:49:16', 'new', -1, 'old\n', '', '', '', '', '', 43),
(68, '2020-07-20 12:49:16', '2020-07-20 12:49:16', 'new', -1, 'old\n', '', '', '', '', '', 43);

-- --------------------------------------------------------

--
-- Table structure for table `participants`
--

CREATE TABLE `participants` (
  `id` int(11) NOT NULL,
  `email` varchar(40) NOT NULL,
  `name` varchar(40) NOT NULL,
  `added_on` timestamp NOT NULL DEFAULT current_timestamp(),
  `score` decimal(10,0) DEFAULT NULL,
  `score_updated_on` timestamp NULL DEFAULT NULL,
  `no_of_questions_answered` int(11) DEFAULT NULL,
  `correct_answers` int(11) DEFAULT NULL,
  `wrong_answers` int(11) DEFAULT NULL,
  `topic` varchar(30) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` int(10) NOT NULL,
  `email` varchar(30) NOT NULL,
  `password` varchar(30) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `email`, `password`) VALUES
(48, 'rakshith@gmail.com', '1234'),
(47, 'rvv@gmail.com', '1234'),
(46, 'varshini', 'Anna'),
(45, 'abc', '123'),
(44, 'rakshith', '1234'),
(43, 'val', '22');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `locations`
--
ALTER TABLE `locations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notes`
--
ALTER TABLE `notes`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `participants`
--
ALTER TABLE `participants`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email_topic` (`email`,`topic`) USING BTREE;

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=129;

--
-- AUTO_INCREMENT for table `locations`
--
ALTER TABLE `locations`
  MODIFY `id` int(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=41;

--
-- AUTO_INCREMENT for table `notes`
--
ALTER TABLE `notes`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=82;

--
-- AUTO_INCREMENT for table `participants`
--
ALTER TABLE `participants`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=53;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` int(10) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
