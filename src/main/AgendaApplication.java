package main;

import entities.Pessoa;
import exceptions.RegraDeNegocioExcepetion;
import service.PessoaService;

import java.util.List;

import javax.swing.JOptionPane;
/**
 * Classe responsável pela entrada e saída de dados
 * 
 * @au
 */
public class AgendaTest01 {
	
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
	 *Retorna o inteiro digitado pelo usuario, em caso de valor inválido 
	 *a exceção é capturada e retorna -1 caindo no default do switch. 
	 *
	 *@return inteiro digitado pelo usuário ou 7 em caso de exceção.
	 */
	private static int exibirOpcoes() {

		String opcaoStr = JOptionPane.showInputDialog(null, """
				---------Menu de agenda------
				1 - Cadastro
				2 - Pesquisa por idade
				3 - Ordenação alfabética
				4 - Altera cadastro
				5 - Remove cadastro
				6 - Listar cadastros
				7 - Sair do menu
				Escolha uma opção: """);
		try {
			int opcao = Integer.parseInt(opcaoStr);
			return opcao;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Erro: Digite apenas números inteiros para a opção.", "Erro de Entrada",
					JOptionPane.ERROR_MESSAGE);
			return -1;
		}
	}
	
	/** 
	 * exibe todos os cadastros do sistema.
	 */
	private static void listarCadastros() {
		if(pessoaService.isVazia()) {
			JOptionPane.showMessageDialog(null,"Erro: agenda vazia", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		List<Pessoa> cadastros = pessoaService.listaCadastros();
		
		mostrarPorPagina(cadastros, 3);
	}
	
	/**
	 * captura as informações para criação de um cadastro.
	 * Passa as informações para o o metodo responsavel pela criação do cadastro.
	 * <p>
	 * As informações são checadas em outros métodos.
	 */
	private static void cadastroPessoa() {

		String nome = JOptionPane.showInputDialog(null, "Digite o nome: ");
		String idadeStr = JOptionPane.showInputDialog(null, "Digite a idade: ");
		String endereco = JOptionPane.showInputDialog(null, "Digite o endereço: ");
		String telefone = JOptionPane.showInputDialog(null, "Digite o telefone: ");
		try {
			int idade = Integer.parseInt(idadeStr);
			Pessoa pessoa = new Pessoa(nome, endereco, telefone, idade);
			pessoaService.cadastraPessoa(pessoa);
			JOptionPane.showMessageDialog(null, "Pessoa cadastrada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Erro: idade deve ser um número inteiro.", "Erro de Entrada",
					JOptionPane.ERROR_MESSAGE);
		}
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
			
			 int opcaoSelecionada = JOptionPane.showOptionDialog(
		                null,
		                resultado.toString(),
		                "Página " + (paginaAtual + 1) + " / " + totalPaginas,
		                JOptionPane.DEFAULT_OPTION,
		                JOptionPane.INFORMATION_MESSAGE,
		                null,
		                opcoes,
		                opcoes[0]
		        );
			if(opcaoSelecionada == 0 && paginaAtual > 0) {
				paginaAtual--; // voltar;
			}else if(opcaoSelecionada == 1 && paginaAtual < totalPaginas - 1) {
				paginaAtual++;//avançar
			}else {
				break;  // se clicou no X, ou não pode mais avançar/voltar
			}
		}
	}
	
	
	/**
	 * Captura idade digitada pelo usuario.
	 * Passa como parâmetro para outro método que realiza a busca.
	 * Exibe cadastros que correspondem com a idade digitada.
	 * <p>
	 * Caso o usuário digite dado inválido, a exceção é capturada, exibindo mensagem de erro.
	 */
	private static void exibePessoasPorIdade() {

		if (pessoaService.isVazia()) {
			JOptionPane.showMessageDialog(null, "Erro: agenda vazia", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String idadePesquisaStr = JOptionPane.showInputDialog(null, "Digite a idade para pesquisa: ");
		try {
			int idadePesquisa = Integer.parseInt(idadePesquisaStr);
			List<Pessoa> encontrados = pessoaService.pesquisaPorIdade(idadePesquisa);
			if (encontrados.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Nenhuma pessoa encontrada com essa idade", "Informação",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			mostrarPorPagina(encontrados, 3);		
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Erro: Idade só pode ser um número inteiro.", "Erro de Entrada",
					JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Exibe todos os cadastros do sistema ordenados alfabeticamente.
	 */
	private static void exibePessoasAlfabeticamente() {
		if (pessoaService.isVazia()) {
			JOptionPane.showMessageDialog(null, "Erro: agenda vazia", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}
		mostrarPorPagina (pessoaService.cadastrosEmOrdemAlfabetica(), 3);
	}

	
	/**
	 * Captura ID digitado pelo usuário para alterar cadastro.
	 * Passa o ID para metodo que realiza a busca dentro do sistema.
	 * Captura as informações necessárias para alterar cadastro.
	 * Se o ID informado não for válido lança exceção.
	 * As outras validações são feitas por outros métodos.
	 */
	private static void alteraCadastro() {

		if (pessoaService.isVazia()) {
			JOptionPane.showMessageDialog(null, "Erro: agenda vazia", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String idBuscaStr = JOptionPane.showInputDialog(null, "Digite o ID para realizar busca: ");
		try {
			int idBusca = Integer.parseInt(idBuscaStr);
			Pessoa pessoaAlterar = pessoaService.buscaPorID(idBusca);
			
			int confirmar = JOptionPane.showConfirmDialog(null,
					"Pessoa encontrada. Deseja alterar cadastro?\n\n" + pessoaAlterar.toString(),
					"Confirmarção de Alteração", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

			if (confirmar != JOptionPane.YES_OPTION) {
				JOptionPane.showMessageDialog(null, "Operação cancelada.");
				return;
			}
			pessoaAlterar.setNome(JOptionPane.showInputDialog(null, "Pessoa encontrada: \n " + "Digite o nome: "));

			pessoaAlterar.setIdade(Integer.parseInt(JOptionPane.showInputDialog(null, "Digite a idade: ")));

			pessoaAlterar.setEndereco(JOptionPane.showInputDialog(null, "Digite o endereço: "));

			pessoaAlterar.setTelefone(JOptionPane.showInputDialog(null, "Digite o telefone: "));
			
			pessoaService.alteraCadastros(pessoaAlterar);
			JOptionPane.showMessageDialog(null, "Alteração bem sucedida.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Erro: ID deve ser um número inteiro positivo", "Erro de ID",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 * Captura as informações necessárias para remover usuário.
	 * Passa as informações para outro método.
	 */
	private static void removePessoa() {
		if (pessoaService.isVazia()) {
			JOptionPane.showMessageDialog(null, "Erro:  agenda vazia", "Erro", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String idStr = JOptionPane.showInputDialog(null, "Digite o id que deseja excluir: ");
		Pessoa pessoaRemover = null;

		try {
			int id = Integer.parseInt(idStr);
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
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(null, "Erro: Id deve ser um numero inteiro positivo", "Erro de ID",
					JOptionPane.ERROR_MESSAGE);
		}

	}
}
                               