package exceptions;

/**
 * Classe que lida com erros especificos da lógica da aplicação.
 * <p>
 * Lança unchecked exception. Usada pra capturar falhas que violam
 * as regras de negócio da aplicação, como entrada de dados inválidos. 
 */
public class RegraDeNegocioExcepetion extends RuntimeException {
	
	/**
	 * Lança a exceção
	 * @param mensagem Mensagem de erro
	 */
	public RegraDeNegocioExcepetion(String mensagem) {
		super(mensagem);
	}
}
