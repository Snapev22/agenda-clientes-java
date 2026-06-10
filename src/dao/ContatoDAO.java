package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import entities.Contato;
import entities.PessoaFisica;
import entities.PessoaJuridica;
import exceptions.RegraDeNegocioException;

/**
 * A classe {@code ContatoDAO} é responsável pelas operações de CRUD no banco de
 * dados relacionadas aos contatos cadastrados no sistema.
 * <p>
 * Suporta os tipos {@link PessoaFisica} e {@link PessoaJuridica}, ambos
 * persistidos na tabela {@code contatos} com estratégia single table (coluna
 * {@code tipo}).
 * </p>
 */
public class ContatoDAO {

	private ConexaoDb conexaoDb;

	/**
	 * Construtor padrão. Inicializa o objeto {@code ConexaoDb} para gerenciar a
	 * conexão com o banco de dados.
	 */
	public ContatoDAO() {
		this.conexaoDb = new ConexaoDb();
	}

	public void abrirCadastro(Contato contato) {
		String sqlQuery = """
				INSERT INTO contatos (nome, endereco, telefone, tipo, cpf, data_nascimento, cnpj, razao_social, ultimo_contato)
				VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
				""";

		try (Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sqlQuery)) {

			ps.setString(1, contato.getNome());
			ps.setString(2, contato.getEndereco());
			ps.setString(3, contato.getTelefone());
			
			if (contato instanceof PessoaFisica pf) {
				ps.setString(4, "PF");
				ps.setString(5, pf.getCpf());
				ps.setObject(6, pf.getDataNascimento(), java.sql.Types.DATE);
				ps.setNull(7, java.sql.Types.VARCHAR);
				ps.setNull(8, java.sql.Types.VARCHAR);
			} else if (contato instanceof PessoaJuridica pj) {
				ps.setString(4, "PJ");
				ps.setNull(5, java.sql.Types.VARCHAR);
				ps.setNull(6, java.sql.Types.VARCHAR);
				ps.setString(7, pj.getCnpj());
				ps.setString(8, pj.getRazaoSocial());
			}
			
			ps.setObject(9, contato.getUltimoContato(), java.sql.Types.DATE);
			ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao cadastrar pessoa", e);
		}
	}

	/**
	 * Retorna uma lista de contatos ordenada pelo critério informado. Aceita apenas
	 * colunas previamente validadas para evitar SQL Injection.
	 *
	 * @param criterio nome da coluna para ordenação.
	 * @return lista imutável de contatos ordenados.
	 * @throws RegraDeNegocioException se o critério não for permitido.
	 */
	private List<Contato> listarComOrdenacao(String criterio) {
		List<String> colunasValidas = List.of("id", "nome",  "telefone");

		if (!colunasValidas.contains(criterio.toLowerCase().trim())) {
			throw new RegraDeNegocioException("Tentativa de ordenação por campo inválido ou não permitido.");
		}
		List<Contato> cadastros = new ArrayList<>();
		String sqlQuery = String.format("""
				SELECT id, nome, endereco, telefone, tipo, cpf, data_nascimento, cnpj, razao_social, ultimo_contato
				FROM contatos
				ORDER BY %s
				""", criterio.trim());

		try (Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sqlQuery);
				ResultSet rs = ps.executeQuery()) {

			while (rs.next()) {
				cadastros.add(mapearContato(rs));
			}

		} catch (SQLException e) {
			throw new RuntimeException("Erro ao listar contatos.", e);
		}
		return Collections.unmodifiableList(cadastros);
	}

	public List<Contato> listarTodosCadastros() {
		return listarComOrdenacao("id");
	}

	public List<Contato> listarEmOrdemAlfabetica() {
		return listarComOrdenacao("nome");
	}

	public Optional<Contato> buscaPorId(Integer id) {
		String sqlQuery = """
				SELECT id, nome, endereco, telefone, tipo, cpf, data_nascimento, cnpj, razao_social, ultimo_contato
				FROM contatos
				WHERE id = ?
				""";

		try (Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sqlQuery)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(mapearContato(rs));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar contato com id: " + id, e);
		}
		return Optional.empty();
	}
	
	public List<Contato> buscarPorNome(String nome) {
		List<Contato> encontrados = new ArrayList<>();
		String sql = """
				SELECT id, nome, endereco, telefone, tipo, cpf, data_nascimento, cnpj, razao_social, ultimo_contato
				FROM contatos
				WHERE LOWER(nome) LIKE LOWER(?)
				""";

		try (Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sql)) {

			ps.setString(1, "%" + nome.trim() + "%");

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					encontrados.add(mapearContato(rs));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException("Erro ao buscar contatos por nome.", e);
		}

		return Collections.unmodifiableList(encontrados);
	}
	
	/**
     * Atualiza os dados de um contato existente.
     * Persiste campos comuns e campos específicos do subtipo (PF ou PJ).
     *
     * @param contato contato com dados atualizados.
     * @throws RuntimeException se ocorrer erro de SQL.
     */
	public int alteraContato(Contato contato) {
		String sqlQuery = """
				UPDATE contatos
				SET nome = ?, endereco = ?, telefone = ?, cpf = ?, data_nascimento = ?, cnpj = ?, razao_social = ?, ultimo_contato = ?
				WHERE id = ?
				""";
		try (Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sqlQuery)) {

			ps.setString(1, contato.getNome());
			ps.setString(2, contato.getEndereco());
			ps.setString(3, contato.getTelefone());
			
			if (contato instanceof PessoaFisica pf) {
				ps.setString(4, pf.getCpf());
				ps.setObject(5, pf.getDataNascimento(), java.sql.Types.DATE);
				ps.setNull(6, java.sql.Types.VARCHAR);
				ps.setNull(7, java.sql.Types.VARCHAR);
			} else if (contato instanceof PessoaJuridica pj) {
				ps.setNull(4, java.sql.Types.VARCHAR);
				ps.setNull(5, java.sql.Types.VARCHAR);
				ps.setString(6, pj.getCnpj());
				ps.setString(7, pj.getRazaoSocial());
			}

			ps.setObject(8, contato.getUltimoContato(), java.sql.Types.DATE);
			ps.setInt(9, contato.getId());
			return 	ps.executeUpdate();

		} catch (SQLException e) {
			throw new RuntimeException("Erro ao atualizar contato", e);
		}
	}

	public int removerContato(int id) {
		String sqlQuery = "DELETE FROM contatos WHERE id = ?";

		try (Connection connection = conexaoDb.recuperaConexao();
				PreparedStatement ps = connection.prepareStatement(sqlQuery)) {

			ps.setInt(1, id);
			return ps.executeUpdate();
		} catch (SQLException e) {
			throw new RuntimeException("Erro ao remover contato.", e);
		}
	}

	/**
	 * Converte um registro do {@code ResultSet} em um objeto {@code Contato}. Lê a
	 * coluna {@code tipo} para instanciar {@link PessoaFisica} ou
	 * {@link PessoaJuridica} e popula os campos específicos de cada subtipo.
	 *
	 * @param rs ResultSet posicionado em uma linha válida.
	 * @return instância de {@code PessoaFisica} ou {@code PessoaJuridica}.
	 * @throws SQLException se ocorrer erro ao acessar o ResultSet.
	 */
	private static Contato mapearContato(ResultSet rs) throws SQLException {

		String tipo = rs.getString("tipo");
		Date ultimoContato = rs.getDate("ultimo_contato");
		if ("PF".equals(tipo)) {
			Date data = rs.getDate("data_nascimento") ;
			PessoaFisica pessoaFisica = PessoaFisica.builder()
					.id(rs.getInt("id"))
					.nome(rs.getString("nome"))
					.endereco(rs.getString("endereco")).telefone(rs.getString("telefone"))
					.cpf(rs.getString("cpf"))
					.ultimoContato(ultimoContato != null ? ultimoContato.toLocalDate() : null)
					.dataNascimento(
							data != null
							? data.toLocalDate()
							:null)
					.build();
			return pessoaFisica;
		} else {
			return PessoaJuridica.builder()
					.id(rs.getInt("id"))
					.nome(rs.getString("nome"))
					.endereco(rs.getString("endereco"))
					.telefone(rs.getString("telefone"))
					.cnpj(rs.getString("cnpj"))
					.razaoSocial(rs.getString("razao_social"))
					.ultimoContato(ultimoContato != null ? ultimoContato.toLocalDate() : null)
					.build();
		}
	}
}