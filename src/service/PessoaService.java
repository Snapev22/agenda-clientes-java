package service;

import java.util.Collections;
import java.util.List;
import dao.PessoaDAO;
import entities.Pessoa;
import exceptions.RegraDeNegocioExcepetion;

/**
 * A classe({@code PessoaService} fornece os serviços relacionados a manipulação de cadastros na agenda.
 * Ela permite criar cadastro, listar, ordernar alfabeticamente, listar com filto de idade, e remoção de cadastro.
 * <p> 
 */
public class PessoaService {
	
	private PessoaDAO pessoaDAO;
		
	/**
	 * Construtor padrão da classe {@code PessoaService}.
	 * Inicializa o acesso aos dados de pessoas. .
	 */
	public PessoaService() {
		this.pessoaDAO = new PessoaDAO();
	}
	
	public void cadastraPessoa(Pessoa novaPessoa) {
		validarDadosPessoa(novaPessoa);
		pessoaDAO.abrirCadastro(novaPessoa);
	}
	
	/**
	 * Realiza as validações das informações obrigatórias da pessoa.
	 * <p>
	 * Nome e telefone são obrigatórios.
	 * A idade, quando informada, deve possuir valor positivo.
	 * </p>
	 *
	 * @param p pessoa a ser validada.
	 * @throws RegraDeNegocioExcepetion caso algum dado obrigatório seja inválido.
	 */
	private void validarDadosPessoa(Pessoa p) {
		if(p.getNome() == null || p.getNome().isBlank()) {
			throw new RegraDeNegocioExcepetion("Nome é obrigatório para o cadastro.");
		}
		
		if (p.getTelefone() == null || p.getTelefone().isBlank()) {
	        throw new RegraDeNegocioExcepetion("Telefone é obrigatório para o cadastro.");
	    }
		
		if (p.getIdade() != null && p.getIdade() <= 0) {
	        throw new RegraDeNegocioExcepetion("A idade, se informada, deve ser um número positivo.");
	    }
	}
	
	public void removerCadastro(int id) {
		int linhasAfetadas = pessoaDAO.removerPessoa(id);
		
		if(linhasAfetadas == 0) {
			throw new RegraDeNegocioExcepetion("Nenhum registro encontrado com ID: " + id + " para remoção.");
		}
	}
	
	public List<Pessoa> listaCadastros() {
		return pessoaDAO.listarTodosCadastros();
	}
	
	public List<Pessoa> pesquisaPorIdade(int idade) {
		return pessoaDAO.listarPorIdade(idade); 
	}
	
	public List<Pessoa> cadastrosEmOrdemAlfabetica() {
		return  pessoaDAO.listarEmOrdemAlfabetica();
	}
	

	public Pessoa buscaPorID(int id) {
		return pessoaDAO.buscaPorId(id)
				.orElseThrow(() -> new RegraDeNegocioExcepetion("Pessoa não encontrada."));
	}
	
	public void alteraCadastros(Pessoa pessoaAlterar) {
		if(pessoaAlterar == null)throw new RegraDeNegocioExcepetion("Pessoa não pode ser nula");
		validarDadosPessoa(pessoaAlterar);
		pessoaDAO.alteraPessoa(pessoaAlterar);
	}
	
	/**
	 * Retorna uma sublista (página) de pessoas a partir de uma lista completa.
	 * * @param pessoas  A lista completa de pessoas a ser paginada.
	 * @param numeroPagina   O número da página que se deseja obter (começando de 1).
	 * @param tamanhoPagina  O número máximo de pessoas por página.
	 * @return  Uma lista imutável contendo as pessoas da página solicitada. 
	 * Retorna uma lista vazia caso o número da página seja inválido ou 
	 * a lista de entrada esteja vazia.
	 */
	public List<Pessoa> getPagina(List<Pessoa> pessoas, int numeroPagina, int tamanhoPagina){
		int inicio = (numeroPagina - 1) * tamanhoPagina;
		int fim = Math.min(inicio + tamanhoPagina, pessoas.size());
		
		if(inicio >= pessoas.size() || inicio < 0) return Collections.emptyList();
		
		return pessoas.subList(inicio, fim);
	}
}
