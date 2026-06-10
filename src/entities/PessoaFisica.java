package entities;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

import exceptions.RegraDeNegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PessoaFisica extends Contato {
	private String cpf;
	private LocalDate dataNascimento;

	@Override
	public void validar() throws RegraDeNegocioException {
		if (getNome() == null || getNome().isBlank()) {
			throw new RegraDeNegocioException("Nome é obrigatório.");
		}
		if (getTelefone() == null || getTelefone().isBlank()) {
			throw new RegraDeNegocioException("Telefone é obrigatório.");
		}
		if (getTelefone().length() < 10 || getTelefone().length() > 11) {
		    throw new RegraDeNegocioException("Telefone deve possuir 10 (fixo) ou 11 (celular) dígitos com DDD.");
		}
		if (dataNascimento != null && dataNascimento.isAfter(LocalDate.now())) {
			    throw new RegraDeNegocioException( "Data de nascimento não pode estar no futuro.");
		}
		if (cpf == null || cpf.isBlank()) {
		    throw new RegraDeNegocioException("CPF é obrigatório para Pessoa Física.");
		}
		
	    if (cpf.length() != 11) {
	        throw new RegraDeNegocioException("CPF deve possuir 11 dígitos.");
	    }
	}
	
	
	
	@Override
    public String toString() {
        String idadeSaida = (getIdade() == null) ? "Não informada" : getIdade().toString();
        String enderecoSaida = (getEndereco() == null || getEndereco().isBlank()) ? "Não informado" : getEndereco();
        DateTimeFormatter formatoIdade = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String nascSaida = dataNascimento == null ? "Não informada" : dataNascimento.format(formatoIdade);

        return String.format(
            "ID: %d\nTipo: Pessoa Física\nNome: %s\nCPF: %s\nData de Nascimento: %s\n" 
            		+"Idade: %s\nTelefone: %s\nEndereço: %s\n",    
            getId(), getNome(), formatarCpf(), nascSaida, idadeSaida, formatarTelefone(), enderecoSaida
        );
    }
	
	private String formatarCpf() {
		return cpf.replaceFirst( "(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
	            "$1.$2.$3-$4");
	}
	
	
	public Integer getIdade() {
		if(dataNascimento == null) {
			return null;
		}
		
		return Period.between(dataNascimento, LocalDate.now()).getYears();
	}
	
	
}
