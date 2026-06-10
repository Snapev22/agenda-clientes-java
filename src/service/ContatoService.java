package service;

import java.util.Collections;
import java.util.List;

import dao.ContatoDAO;
import entities.Contato;
import entities.PessoaFisica;
import exceptions.RegraDeNegocioException;

/**
 * A classe {@code ContatoService} fornece os serviços relacionados à manipulação
 * de cadastros na agenda de clientes.
 * <p>
 * Delega a validação para cada entidade via interface {@link interfaces.Validavel},
 * eliminando a necessidade de validações acopladas nesta camada.
 * </p>
 */
public class ContatoService {
	
	private ContatoDAO contatoDAO;
		
    /**
     * Construtor padrão. Inicializa o acesso aos dados de contatos.
     */
	public ContatoService() {
		this.contatoDAO = new ContatoDAO();
	}
	
	public void cadastrarContato(Contato novoContato) {
		novoContato.validar();
		contatoDAO.abrirCadastro(novoContato);
	}
		
	public void removerCadastro(int id) {
		int linhasAfetadas = contatoDAO.removerContato(id);	
		if(linhasAfetadas == 0) {
			throw new RegraDeNegocioException("Nenhum registro encontrado com ID: " + id + " para remoção.");
		}
	}
	
	public List<Contato> listaCadastros() {
		return contatoDAO.listarTodosCadastros();
	}
	
	public List<Contato> pesquisaPorIdade(Integer idade) {
		return contatoDAO.listarTodosCadastros()
	            .stream()
	            .filter(PessoaFisica.class::isInstance)
	            .map(PessoaFisica.class::cast)
	            .filter(pf -> pf.getIdade() != null)
	            .filter(pf -> pf.getIdade().equals(idade))
	            .map(pf -> (Contato) pf)
	            .toList();
	}
	
	public List<Contato> cadastrosEmOrdemAlfabetica() {
		return  contatoDAO.listarEmOrdemAlfabetica();
	}
	
	public Contato buscar(int id) {
		return contatoDAO.buscaPorId(id)
				.orElseThrow(() -> new RegraDeNegocioException("Pessoa não encontrada."));
	}
	
    public List<Contato> buscar(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new RegraDeNegocioException("O nome para busca não pode ser vazio.");
        }
        return contatoDAO.buscarPorNome(nome);
    }
	
	public void alteraCadastros(Contato contatoAlterar) {
		if(contatoAlterar == null)throw new RegraDeNegocioException("Pessoa não pode ser nula");
		contatoAlterar.validar();
		contatoDAO.alteraContato(contatoAlterar);
	}
	
	/**
     * Retorna uma sublista (página) de contatos a partir de uma lista completa.
     *
     * @param itens lista completa de contatos a ser paginada.
     * @param numeroPagina  número da página desejada (começa em 1).
     * @param tamanhoPagina quantidade máxima de contatos por página.
     * @return lista imutável com os contatos da página solicitada,
     *         ou lista vazia se o número de página for inválido.
     */
	public List<Contato> getPagina(List<Contato> itens, int numeroPagina, int tamanhoPagina){
		int inicio = (numeroPagina - 1) * tamanhoPagina;
		int fim = Math.min(inicio + tamanhoPagina, itens.size());
		
		if(inicio >= itens.size() || inicio < 0) return Collections.emptyList();
		
		return List.copyOf(itens.subList(inicio, fim));
	}
}
