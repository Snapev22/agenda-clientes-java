package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import entities.Pessoa;
import exceptions.RegraDeNegocioExcepetion;

/**
 * A classe {@code PessoaDAO} é responável por métodos correspondentes as operações de CRUD no banco de dados relacionado
 * as pessoas cadastradas no sistema.
 * <p>
 * Utiliza a classe {@code ConexaoDb} para obter conexão com banco de dados.
 * </p>
 * 
 * 
 */
public class PessoaDAO {
	
	private ConexaoDb conexaoDb;

	/**
	 * Construtor padrão.
	 * Inicializa o objeto {@code ConexaoDb} para gerenciar a conexão com o banco de dados.
	 */
	public PessoaDAO(){
		this.conexaoDb = new ConexaoDb();
	}
	
	
	public void abrirCadastro(Pessoa pessoa) {
		String sqlQuery = "INSERT INTO pessoas (nome, endereco, telefone, idade)" +
	                       "VALUES (?, ?, ?, ?)";
		
		try(Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sqlQuery)){
			
			ps.setString(1, pessoa.getNome());
			ps.setString(2, pessoa.getEndereco());
			ps.setString(3, pessoa.getTelefone());
			ps.setObject(4, pessoa.getIdade(), java.sql.Types.INTEGER);
			ps.execute();
		}catch (SQLException e) {
			throw new RuntimeException("Erro ao cadastrar pessoa", e);
		}			
	}
	
	/**
	 * Retorna uma lista de pessoas ordenada conforme o critério informado.
	 *
	 * @param criterio coluna utilizada para ordenação.
	 * @return uma lista imutável de pessoas ordenadas.
	 * @throws RegraDeNegocioExcepetion caso o critério informado não seja permitido.
	 * @throws RuntimeException caso ocorra erro durante a consulta SQL.
	 */
	private List<Pessoa> listarComOrdenacao(String criterio){
		List<String> colunasValidas = List.of("id", "nome", "idade", "telefone");
		
		if(!colunasValidas.contains(criterio.toLowerCase().trim())) {
			throw new RegraDeNegocioExcepetion("Tentativa de ordenação por campo inválido ou não permitido.");
		}
		List<Pessoa> cadastros = new ArrayList<Pessoa>();
		String sqlQuery = String.format("""
				SELECT id, nome, endereco, telefone, idade
				FROM  pessoas
				ORDER BY %s
				""", criterio.trim());
		
		try(Connection connection = conexaoDb.recuperaConexao();
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ResultSet rs = ps.executeQuery()){
			
			while(rs.next()) {
				 cadastros.add(mapearPessoa(rs));
			}
			
		}catch (SQLException e) {
			throw new RuntimeException("Erro ao listar pessoas", e);
		}
		return  Collections.unmodifiableList(cadastros);
	}
	
	
	public List<Pessoa> listarTodosCadastros(){
		return listarComOrdenacao("id");
	}
	
	
	public List<Pessoa> listarEmOrdemAlfabetica(){
		return listarComOrdenacao("nome");
	}
	
	
	public List<Pessoa> listarPorIdade(int idade){
		List<Pessoa> encontrados = new ArrayList<>(); 
		String sql = """
				SELECT id, nome, endereco, telefone, idade
				FROM  pessoas
				WHERE idade = ?
				""";
		try(Connection connection = conexaoDb.recuperaConexao();
			PreparedStatement ps = connection.prepareStatement(sql)){
			
			ps.setInt(1, idade);
			try(ResultSet rs = ps.executeQuery()){
				while(rs.next()) {
					encontrados.add(mapearPessoa(rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao ' pessoas por idade.",e);
		}
		return Collections.unmodifiableList(encontrados);
	}
	
	public Optional<Pessoa> buscaPorId(Integer id) {
		String sqlQuery = """ 
				SELECT id, nome, endereco, telefone, idade		
				FROM pessoas
				WHERE id = ?				
				""";
		try(Connection connection = conexaoDb.recuperaConexao();
			PreparedStatement ps = connection.prepareStatement(sqlQuery)){
			
			ps.setInt(1, id);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()) {
					Pessoa p = mapearPessoa(rs);
					return Optional.of(p);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar pessoa com id: " + id, e);
		}
		return Optional.empty();
	}
	
	/**
	 * Atualiza as informações de uma pessoa existente no banco de dados.
	 * 
	 * A atualização é baseada no ID contido no objeto {@code Pessoa}.
	 * * @param pessoa O objeto {@code Pessoa} com as informações atualizadas.
	 * @throws RuntimeException se ocorrer um erro de SQL durante a atualização.
	 */
	public void alteraPessoa(Pessoa pessoa) {
		String sqlQuery = """
				UPDATE pessoas 
				SET nome = ?, endereco = ?, telefone = ?, idade = ?
				WHERE ID = ?
				""";
		try(Connection connection = conexaoDb.recuperaConexao();
			PreparedStatement ps = connection.prepareStatement(sqlQuery)){
			
			ps.setString(1, pessoa.getNome());
			ps.setString(2, pessoa.getEndereco());
			ps.setString(3, pessoa.getTelefone());
			ps.setObject(4, pessoa.getIdade(), java.sql.Types.INTEGER);
			ps.setInt(5, pessoa.getId());
			
			ps.executeUpdate();
		}catch (SQLException e) {
			throw new RuntimeException("Erro ao atualizar pessoa", e);
		}
	}
	
	public int  removerPessoa(int id) {
		String sqlQuery = "DELETE FROM pessoas WHERE id = ?";
		
		try(Connection connection =  conexaoDb.recuperaConexao();
			PreparedStatement ps = connection.prepareStatement(sqlQuery)){
			
			ps.setInt(1, id);
			return  ps.executeUpdate();
		}catch (SQLException e) {
			throw new RuntimeException("Erro ao remover pessoa", e);
		}
	}
	
	/**
	 * Converte um registro do {@code ResultSet} em um objeto {@code Pessoa}.
	 *
	 * @param rs resultado da consulta SQL posicionado em uma linha válida.
	 * @return um objeto {@code Pessoa} preenchido com os dados do banco.
	 * @throws SQLException caso ocorra erro ao acessar os dados do ResultSet.
	 */
	private static Pessoa mapearPessoa(ResultSet rs) throws SQLException {
		return  Pessoa.builder()
				.id(rs.getInt("id"))
				.nome(rs.getString("nome"))
				.endereco(rs.getString("endereco"))
				.telefone(rs.getString("telefone"))
				.idade(rs.getObject("idade", Integer.class))
				.build();
	}
}