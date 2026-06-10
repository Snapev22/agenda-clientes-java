package service;

import entities.Contato;
import entities.PessoaJuridica;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Calcula o score de relacionamento de um contato (0 a 100).
 * <p>
 * O score representa a "saúde" do relacionamento com o cliente,
 * penalizando contatos negligenciados e premiando cadastros completos.
 * Fórmula:
 *   score = scoreTempo * pesotipo - penalidade
 * </p>
 */
public class ScoreRelacionamentoService {

	private static final int DIAS_IDEAL_PF = 30;
    private static final int DIAS_IDEAL_PJ = 15;
    private static final double PENALIDADE_SEM_ENDERECO = 10.0;
    private static final double SCORE_MAXIMO = 100.0;

    /**
     * Calcula o score de relacionamento de um contato.
     * Usa vetor interno para calcular o decaimento por faixa de tempo sem contato.
     *
     * @param contato      contato a ser avaliado.
     * @param ultimoContato data do último contato registrado.
     * @return score de 0.0 a 100.0.
     */
    public double calcularScore(Contato contato, LocalDate ultimoContato) {
        if (ultimoContato == null) return 0.0;

        long diasSemContato = ChronoUnit.DAYS.between(ultimoContato, LocalDate.now());
        int diasIdeal = contato instanceof PessoaJuridica ? DIAS_IDEAL_PJ : DIAS_IDEAL_PF;

        // Vetor de limiares de decaimento: cada faixa aplica um fator diferente
        // [0] = dentro do prazo ideal, [1] = até 2x, [2] = até 3x, [3] = acima
        double[] fatoresDecaimento = {1.0, 0.75, 0.40, 0.10};
        int[] limitesFaixa       = {diasIdeal, diasIdeal * 2, diasIdeal * 3};

        double fator = fatoresDecaimento[3]; // default: muito atrasado
        for (int i = 0; i < limitesFaixa.length; i++) {
            if (diasSemContato <= limitesFaixa[i]) {
                fator = fatoresDecaimento[i];
                break;
            }
        }

        double score = SCORE_MAXIMO * fator;

        // Penalidade por cadastro incompleto
        if (contato.getEndereco() == null || contato.getEndereco().isBlank()) {
            score -= PENALIDADE_SEM_ENDERECO;
        }

        return Math.max(0.0, score);
    }

    /**
     * Classifica o score em uma categoria textual.
     *
     * @param score valor calculado.
     * @return rótulo da categoria.
     */
    public String classificarScore(double score) {
        if (score >= 80) return "Ótimo";
        if (score >= 50) return "Regular";
        if (score >= 20) return "Atenção";
        return "Crítico";
    }
}
