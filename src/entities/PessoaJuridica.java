package entities;

import exceptions.RegraDeNegocioException;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class PessoaJuridica extends Contato {
	private String cnpj;
	private String razaoSocial;

	@Override
	public void validar() throws RegraDeNegocioException {
		if (getNome() == null || getNome().isBlank()) {
			throw new RegraDeNegocioException("Nome fantasia é obrigatório.");
		}
		if (getTelefone() == null || getTelefone().isBlank()) {
			throw new RegraDeNegocioException("Telefone é obrigatório.");
		}
		if (getTelefone().length() < 10 || getTelefone().length() > 11) {
		    throw new RegraDeNegocioException("Telefone deve possuir 10 (fixo) ou 11 (celular) dígitos com DDD.");
		}
		if (cnpj == null || cnpj.isBlank()) {
			throw new RegraDeNegocioException("CNPJ é obrigatório para Pessoa Jurídica.");
		}		
	    if (cnpj.length() != 14) {
	        throw new RegraDeNegocioException("CNPJ deve possuir 14 dígitos.");
	    }
		if (razaoSocial == null || razaoSocial.isBlank()) {
			throw new RegraDeNegocioException("Razão social é obrigatória para Pessoa Jurídica.");
		}
	}

	public String toString() {
		String enderecoSaida = (getEndereco() == null || getEndereco().isBlank()) ? "Não informado" : getEndereco();

		return String.format(
				"ID: %d\nTipo: Pessoa Jurídica\nNome Fantasia: %s\nRazão Social: %s\nCNPJ: %s\n"
						+ "Telefone: %s\nEndereço: %s\n",
				getId(), getNome(), razaoSocial, formataCnpj(), formatarTelefone(), enderecoSaida);
	}
	
	private String formataCnpj() {
		return cnpj.replaceFirst(
				"(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})",
	            "$1.$2.$3/$4-$5");
	}
}
