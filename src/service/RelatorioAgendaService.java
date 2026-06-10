package service;

import entities.Contato;
import entities.PessoaFisica;
import entities.PessoaJuridica;

import java.util.List;

/**
 * Classe responsável pelos cálculos e estatísticas da agenda de clientes.
 * <p>
 * Aplica lógica matemática sobre a coleção de contatos, utilizando vetores para
 * distribuição por faixa etária e operadores aritméticos para médias e
 * percentuais. Separada do {@code ContatoService} seguindo o Princípio da
 * Responsabilidade Única (SRP).
 * </p>
 */
public class RelatorioAgendaService {

	public static final String[] LABELS_FAIXAS = { "Até 25 anos", "26 a 40 anos", "41 a 60 anos", "Acima de 60" };

	/**
	 * Gera um relatório completo de estatísticas da agenda.
	 * <p>
	 * Percorre a lista de contatos uma única vez, acumulando:
	 * <ul>
	 *	<li>Contagem de Pessoa Física e Pessoa Jurídica</li>
	 * 	<li>Soma de idades para cálculo de média (apenas PF)</li>
	 * 	<li>Distribuição por faixa etária em vetor {@code int[4]}</li>
	 * 	<li>Contagem de cadastros com endereço preenchido</li>
	 * </ul>
	 * </p>
	 *
	 * @param contatos lista de contatos da agenda.
	 * @return String formatada com o relatório completo.
	 */
	public String gerarRelatorio(List<Contato> contatos) {
		int totalContatos = contatos.size();
		int totalPF = 0;
		int totalPJ = 0;
		int somaIdades = 0;
		int qtdIdadeInformada = 0;
		int qtdEnderecoInformado = 0;
		
		int[] faixasEtarias = new int[4];

		for (Contato c : contatos) {
			if (c instanceof PessoaFisica pf) {
				totalPF++;
				if (pf.getIdade() != null) {
					int idade = pf.getIdade();
					somaIdades += idade;
					qtdIdadeInformada++;
					faixasEtarias[classificarFaixa(idade)]++;
				}
			} else if (c instanceof PessoaJuridica) {
				totalPJ++;
			}

			if (c.getEndereco() != null && !c.getEndereco().isBlank()) {
				qtdEnderecoInformado++;
			}

		}
		double mediaIdade = qtdIdadeInformada > 0 ? (double) somaIdades / qtdIdadeInformada : 0;
		double taxaPreenchimentoTotal = totalContatos > 0 ? (double) qtdEnderecoInformado / totalContatos * 100 : 0;
		
		return formatarRelatorio(totalContatos, totalPF, totalPJ,
                mediaIdade, qtdIdadeInformada, faixasEtarias, qtdEnderecoInformado, taxaPreenchimentoTotal);
	}
	
	/**
     * Classifica uma idade no índice correspondente do vetor de faixas etárias.
     *
     * @param idade idade a ser classificada.
     * @return índice de 0 a 3 correspondente à faixa etária.
     */
    private int classificarFaixa(int idade) {
        if (idade <= 25) return 0;
        if (idade <= 40) return 1;
        if (idade <= 60) return 2;
        return 3;
    }
    

    /**
    * Formata os dados calculados em um relatório legível para exibição.
    */
   private String formatarRelatorio(int totalContatos, int totalPF, int totalPJ,  double mediaIdade, int qtdIdadeInformada,
                                    int[] faixas, int comEndereco, double percentualCompletos) {
		StringBuilder sb = new StringBuilder();
		sb.append("======= ESTATÍSTICAS DA AGENDA =======\n\n");
		sb.append(String.format("Total de contatos    : %d\n", totalContatos));
		sb.append(String.format("Pessoas Física  (PF)  : %d\n", totalPF));
		sb.append(String.format("Pessoas Jurídica (PJ) : %d\n\n", totalPJ));

		if (qtdIdadeInformada > 0) {
			sb.append(String.format("Média de idade (PF)  : %.1f anos\n\n", mediaIdade));
			sb.append("Distribuição por faixa etária (PF):\n");
			for (int i = 0; i < faixas.length; i++) {
				sb.append(String.format("  %-15s : %d contato(s)\n", LABELS_FAIXAS[i], faixas[i]));
			}
		} else {
			sb.append("Nenhuma idade registrada para estatísticas de faixa etária.\n");
		}

		sb.append(String.format("\nCadastros com endereço: %d (%.1f%%)\n", comEndereco, percentualCompletos));
		sb.append("======================================");
		return sb.toString();
	}
}
