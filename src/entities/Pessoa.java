package entities;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Representa uma pessoa cadastrada na agenda.
 * <p>
 * A classe armazena os dados básicos de contato de uma pessoa,
 * permitindo informações opcionais como idade e endereço.
 * </p>
 */
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = "id")
public class Pessoa{
	
	@Setter(lombok.AccessLevel.NONE)
	private Integer id;
	private String nome;
	private String endereco;
	private String telefone;
	private Integer idade;
	
	@Override
	public String toString() {
		String idadeSaida = (this.idade == null) ? "Não informada" : this.idade.toString();
	    String enderecoSaida = (this.endereco == null || this.endereco.isBlank()) ? "Não informado" : this.endereco;
	    
	    return String.format("ID: %d\nNome: %s\nIdade: %s\nTelefone: %s\nEndereço: %s\n",
	    		id, nome, idadeSaida, telefone, enderecoSaida);
	}
}
