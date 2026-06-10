package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe responsável por fornecer conexões com o banco de dados.
 * <p>
 * Neste projeto é utilizado o {@link DriverManager} pela simplicidade didática.
 */
public class ConexaoDb {

	/**
	 * Estabelece e retorna uma nova conexão com o banco de dados.
	 *
	 * @return uma conexão ativa com o banco de dados.
	 * @throws RuntimeException se a conexão falhar.
	 */
	public Connection recuperaConexao() {
		try {
			String password = "root123";
			String user = "root";
			String database = "agenda_db";

			String url = "jdbc:mysql://localhost:3306/" + database;
			return DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao conectar com o banco de dados");
		}
	}
}
