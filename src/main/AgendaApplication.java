package main;

import entities.Pessoa;
import exceptions.RegraDeNegocioExcepetion;
import service.PessoaService;

import java.util.List;

import javax.swing.JOptionPane;

/**
 * Classe principal da aplicação.
 * <p>
 * Responsável pela interação com o usuário através de caixas de diálogo,
 * captura de entradas e exibição das informações da agenda.
 * </p>
 */
public class AgendaApplication {
	private static final PessoaService pessoaService = new PessoaService();
	
	public static void main(String[] args) {
		int opcao = exibirOpcoes();
		
		while (opcao != 7) {
			try { 
				switch (opcao) {
					case 1:
						cadastroPessoa();
						break;
					case 2:
						exibePessoasPorIdade();
						break;
					case 3:
						exibePessoasAlfabeticamente();
						break;
	
					case 4:
						alteraCadastro(); 
						break;
					case 5:
						removePessoa();
						break;
					case 6:
						listarCadastros();
						break;
					default:
					JOptionPane.showMessageDialog(null, "Erro: Opção inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
					break;
				}
			}catch (RegraDeNegocioExcepetion e) {
				JOptionPane.showMessageDialog(null, "Erro: " + e.getMessage(), "Erro de entrada",
						JOptionPane.ERROR_MESSAGE);
			}
			opcao = exibirOpcoes();
		}
		JOptionPane.showMessageDialog(null, "Programa encerrado.", "Fim", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Exibe o menu principal da aplicação e retorna a opção escolhida pelo usuário.
	 *
	 * @return a opção digitada pelo usuário.
	 * @throws RegraDeNegocioExcepetion caso o valor informado não seja um número válido.
	 */
	private static int exibirOpcoes() {
		String opcaoStr = JOptionPane.showInputDialog
				(null, 
				"""
				---------Menu de agenda------
				1 - Cadastrar
				2 - Pesquisar por idade
				3 - Agenda em ordem afabética
				4 - Alterar cadastro
				5 - Remover cadastro
				6 - Listar cadastros
				7 - Sair do menu
				Escolha uma opção: """);
		int opcao = lerInteiro("opcao", opcaoStr, false);
		return opcao;
	}
	
	private static void listarCadastros() {
		agendaIsVazia();
		List<Pessoa> cadastros = pessoaService.listaCadastros();
		
		mostrarPorPagina(cadastros, 3);
	}
	
	private static void cadastroPessoa() {
		String nome = JOptionPane.showInputDialog(null, "Digite o nome: ");
		String idadeStr = JOptionPane.showInputDialog(null, "Digite a idade(opcional): ");
		String endereco = JOptionPane.showInputDialog(null, "Digite o endereço: ");
		String telefone = JOptionPane.showInputDialog(null, "Digite o telefone: ");
		Integer idade = lerInteiro("idade", idadeStr, false);
		Pessoa pessoa = Pessoa.builder()
				.nome(nome)
				.endereco(endereco)
				.telefone(telefone)
				.idade(idade)
				.build();
		pessoaService.cadastraPessoa(pessoa);
		JOptionPane.showMessageDialog(null, "Pessoa cadastrada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Exibe uma lista de pessoas em paginas utilizando JOptionPane.
	 * Mostra uma página por vez (com quantidade definida em pessoasPorPagina),
	 * permitindo ao usuário navegar entre elas pelos botões "Voltar" e "Avançar".
	 * O loop continua até que não seja possível avançar/voltar ou o usuário feche a janela.
	 */
	private static void mostrarPorPagina(List<Pessoa> pessoas, int pessoasPorPagina) {
		String [] opcoes = {"Voltar", "Avançar"};
		int totalPessoas = pessoas.size();
		int paginaAtual = 0;
		int totalPaginas = (int)Math.ceil((double)totalPessoas / pessoasPorPagina);
		
		while(true) {
			List<Pessoa> pagina = pessoaService.getPagina(pessoas, paginaAtual + 1, pessoasPorPagina);
			
			StringBuilder resultado = new StringBuilder();
			for(Pessoa p : pagina) {
				resultado.append(p).append("\n");
			}
			int opcaoSelecionada = JOptionPane.showOptionDialog(null, resultado.toString(),
					"Página " + (paginaAtual + 1) + " / " + totalPaginas, JOptionPane.DEFAULT_OPTION,
					JOptionPane.INFORMATION_MESSAGE, null, opcoes, opcoes[0]);
			if(opcaoSelecionada == 0 && paginaAtual > 0) {
				paginaAtual--; // voltar;
			}else if(opcaoSelecionada == 1 && paginaAtual < totalPaginas - 1) {
				paginaAtual++;//avançar
			}else {
				break;  // se clicou no X, ou não pode mais avançar/voltar
			}
		}
	}
	
	private static void exibePessoasPorIdade() {
		agendaIsVazia();
		String idadePesquisaStr = JOptionPane.showInputDialog(null, "Digite a idade para pesquisa: ");
		int idadePesquisa = lerInteiro("idade", idadePesquisaStr, false);
		List<Pessoa> encontrados = pessoaService.pesquisaPorIdade(idadePesquisa);
		if (encontrados.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Nenhuma pessoa encontrada com essa idade", "Informação",
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		mostrarPorPagina(encontrados, 3);
	}
	
	private static void exibePessoasAlfabeticamente() {
		agendaIsVazia();		
		mostrarPorPagina (pessoaService.cadastrosEmOrdemAlfabetica(), 3);
	}
	
	private static void alteraCadastro() {
		agendaIsVazia();
		String idBuscaStr = JOptionPane.showInputDialog(null, "Digite o ID para realizar busca: ");
		int idBusca = lerInteiro("ID", idBuscaStr, true);
		Pessoa pessoaAlterar = pessoaService.buscaPorID(idBusca);
		int confirmar = JOptionPane.showConfirmDialog(null,
				"Pessoa encontrada. Deseja alterar cadastro?\n\n" + pessoaAlterar.toString(),
				"Confirmarção de Alteração", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	
		if (confirmar != JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(null, "Operação cancelada.");
			return;
		}
		pessoaAlterar.setNome(JOptionPane.showInputDialog(null, "Pessoa encontrada: \n " + "Digite o nome: "));
		String novaIdadeStr = JOptionPane.showInputDialog(null, "Digite a idade: ");
		Integer novaIdade = lerInteiro("idade", novaIdadeStr, false);
		pessoaAlterar.setIdade(novaIdade);
		pessoaAlterar.setEndereco(JOptionPane.showInputDialog(null, "Digite o endereço: "));
		pessoaAlterar.setTelefone(JOptionPane.showInputDialog(null, "Digite o telefone: "));
		
		pessoaService.alteraCadastros(pessoaAlterar);
		JOptionPane.showMessageDialog(null, "Alteração bem sucedida.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}

	private static void removePessoa() {
		agendaIsVazia();
		Pessoa pessoaRemover = null;
		String idStr = JOptionPane.showInputDialog(null, "Digite o id que deseja excluir: ");
		int id = lerInteiro("ID", idStr, true);
		
		pessoaRemover = pessoaService.buscaPorID(id);
		String mensagem = "Deseja remover a seguinte pessoa?\n\n" + pessoaRemover.toString();
		int confirmacao = JOptionPane.showConfirmDialog(null, mensagem, "Confirmação de remoção",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
	
		if (confirmacao != JOptionPane.YES_OPTION) {
			JOptionPane.showMessageDialog(null, "Operação cancelada.");
			return;
		}
		pessoaService.removerCadastro(id);
		JOptionPane.showMessageDialog(null, "Pessoa removida com sucesso.", "Sucesso",
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Converte uma string para inteiro.
	 * <p>
	 * Caso o campo seja obrigatório e o valor esteja vazio,
	 * uma exceção é lançada.
	 * </p>
	 *
	 * @param nomeCampo nome lógico do campo utilizado na mensagem de erro.
	 * @param valor valor digitado pelo usuário.
	 * @param obrigatorio define se o campo aceita valor nulo.
	 * @return o inteiro convertido ou {@code null} caso permitido.
	 * @throws RegraDeNegocioExcepetion caso o valor seja inválido.
	 */
	private static Integer lerInteiro(String nomeCampo, String valor, boolean obrigatorio) {
		if (valor == null || valor.isBlank()) {
			if (obrigatorio) {
				throw new RegraDeNegocioExcepetion("o campo '" + nomeCampo + "' não pode ser nulo.");
			}
			return null;
		}
		
		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException e) {
			throw new RegraDeNegocioExcepetion("O valor para '" + nomeCampo + "' deve ser um número inteiro válido");
		}
	}
	
	private static void agendaIsVazia() {
		List<Pessoa> pessoas = pessoaService.listaCadastros();
		if (pessoas.isEmpty()) {
			throw new RegraDeNegocioExcepetion("A agenda está vazia.");
		}
	}
}
                               