package exceptions;

/**
 * Classe que lida com erros especificos da lógica da aplicação.
 * <p>
 * Lança unchecked exception. Usada pra capturar falhas que violam
 * as regras de negócio da aplicação, como entrada de dados inválidos. 
 */
public class RegraDeNegocioException extends RuntimeException {
	
	public RegraDeNegocioException(String mensagem) {
		super(mensagem);
	}
}
