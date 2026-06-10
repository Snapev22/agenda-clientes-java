package entities;

import java.time.LocalDate;

import interfaces.Validavel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * Representa uma pessoa cadastrada na agenda.
 * <p>
 * A classe armazena os dados básicos de contato de uma pessoa,
 * permitindo informações opcionais como idade e endereço.
 * </p>
 */
@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(of = "id")
public abstract  class Contato implements Validavel{
	
	@Setter(lombok.AccessLevel.NONE)
	private Integer id;
	private String nome;
	private String endereco;
	private String telefone;
	private LocalDate ultimoContato;
	
	@Override
	public abstract String toString();
	
	protected String formatarTelefone() { 
	    if (getTelefone().length() == 11) {
	        return getTelefone().replaceFirst("(\\d{2})(\\d{5})(\\d{4})", "($1) $2-$3");
	    }
	    if (getTelefone().length() == 10) {
	        return getTelefone().replaceFirst("(\\d{2})(\\d{4})(\\d{4})", "($1) $2-$3");
	    }
	    
	    return getTelefone();
	}
}
