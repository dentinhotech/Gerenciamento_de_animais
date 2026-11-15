CREATE DATABASE gerenciamento_animais
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE gerenciamento_animais;

CREATE TABLE cachorros (
  id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  raca VARCHAR(100) NOT NULL,
  idade INT UNSIGNED NOT NULL
);