package controller;

import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import entities.Contato;
import exceptions.RegraDeNegocioException;
import lombok.RequiredArgsConstructor;
import service.ContatoService;
import service.RelatorioAgendaService;
import service.ScoreRelacionamentoService;
import view.AgendaView;

@RequiredArgsConstructor
public class AgendaController {
	private final AgendaView view;
	private final ContatoService contatoService;
	private final RelatorioAgendaService relatorioService;
	private final ScoreRelacionamentoService scoreService;
	
	public void iniciar() {
        
        int opcao;
		do {
			try {
				opcao = view.exibirMenu();
				switch (opcao) {
					case 1 -> cadastrarContato();
					case 2 -> buscaPorIdade();
					case 3 -> exibeContatosAlfabeticamente();
					case 4 -> buscarPorNome();
					case 5 -> alterarCadastro();
					case 6 -> removerContato();
					case 7 -> listarCadastros();
					case 8 -> exibirRelatorioAgenda();
					case 9 -> exibirRankingRelacionamento();
					case 10 -> view.exibirMensagem("Programa encerrado.", "Fim", JOptionPane.INFORMATION_MESSAGE);
					default -> JOptionPane.showMessageDialog(null, "Opção inválida.", "Erro", JOptionPane.ERROR_MESSAGE);
				}
			} catch (RegraDeNegocioException e) {
				view.exibirMensagem(e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
				opcao = 0;
			}
		}while (opcao != 10);
	}

	private  void cadastrarContato() {	
		view.cadastrarContato().ifPresentOrElse(
				contato ->{
					contatoService.cadastrarContato(contato);
		            view.exibirMensagem("Contato cadastrado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
				},
				() -> view.exibirMensagem("Operação cancelada", "Aviso", JOptionPane.INFORMATION_MESSAGE)
			);
	}

	private void  buscaPorIdade() {
		agendaIsVazia();
		List<Contato> encontrados = contatoService.pesquisaPorIdade(view.obterIdadeParaBuscar());
		
		if (encontrados.isEmpty()) {
			view.exibirMensagem("Nenhuma pessoa encontrada com essa idade", "Informação",
					JOptionPane.INFORMATION_MESSAGE); 
			return;
		}
		mostrarPorPagina(encontrados);
	}

	
	private void exibeContatosAlfabeticamente() {
		agendaIsVazia();
		mostrarPorPagina(contatoService.cadastrosEmOrdemAlfabetica());
	}
	
	private void buscarPorNome() {
		agendaIsVazia();
		List<Contato> encontrados = contatoService.buscar(view.obterNomeParaBusca());

	    if (encontrados.isEmpty()) {
	        JOptionPane.showMessageDialog(null,
	                "Nenhum contato encontrado com esse nome.", "Informação", JOptionPane.INFORMATION_MESSAGE);
	        return;
	    }
	    mostrarPorPagina(encontrados);
	}
	
	private void alterarCadastro() {
		agendaIsVazia();
		
		int id = view.obterIdParaBusca();
		Contato contato = contatoService.buscar(id);
		
		if (!view.confirmarAlteracao(contato)) {
			view.exibirMensagem("Operação cancelada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		view.preencherNovoContato(contato);
		contatoService.alteraCadastros(contato);
		view.exibirMensagem("Alteração bem sucedida.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	private void removerContato() {
		agendaIsVazia();

		int id = view.obterIdParaBusca();
		Contato contato = contatoService.buscar(id);

		if (!view.confirmarRemocao(contato)) {
			view.exibirMensagem("Operação cancelada.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		contatoService.removerCadastro(id);
		view.exibirMensagem("Contato removido com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	private void exibirRelatorioAgenda() {
		agendaIsVazia();
		List<Contato> todos = contatoService.listaCadastros();
		String relatorio = relatorioService.gerarRelatorio(todos);
		view.exibirMensagem(relatorio, "Estatísticas da Agenda",  JOptionPane.INFORMATION_MESSAGE);
	}

	private void listarCadastros() {
		agendaIsVazia();
		List<Contato> cadastros = contatoService.listaCadastros();

		mostrarPorPagina(cadastros);
	}

	private void exibirRankingRelacionamento() {
	    agendaIsVazia();
	    List<Contato> contatos = contatoService.listaCadastros();

	    StringBuilder sb = new StringBuilder("=== RANKING DE RELACIONAMENTO ===\n");
	    sb.append("(ordem: mais crítico → melhor)\n\n");

	    contatos.stream()
        .map(c -> Map.entry(c, scoreService.calcularScore(c, c.getUltimoContato()))) 
        .sorted(Map.Entry.comparingByValue()) 
        .forEach(entry -> {
            Contato contato = entry.getKey();
            double score = entry.getValue();
            
            sb.append(String.format("%-30s | %5.1f pts | %s\n",
                contato.getNome(),
                score,
                scoreService.classificarScore(score)));
        });

	    view.exibirMensagem(sb.toString(), "Ranking de Relacionamento", JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void mostrarPorPagina(List<Contato> contatos) {
	    final int POR_PAGINA = 2;
	    int totalPessoas = contatos.size();
	    int paginaAtual = 0;
	    int totalPaginas = (int) Math.ceil((double) totalPessoas / POR_PAGINA);

	    while (true) {
	        List<Contato> pagina = contatoService.getPagina(contatos, paginaAtual + 1, POR_PAGINA);
	        
	        StringBuilder resultado = new StringBuilder();
	        for (Contato c : pagina) {
	            resultado.append(c).append("\n");
	        }

	        int opcaoSelecionada = view.exibirPagina(resultado.toString(), paginaAtual + 1, totalPaginas);
	        
	        if (opcaoSelecionada == 0 && paginaAtual > 0) {
	            paginaAtual--; // Voltar
	        } else if (opcaoSelecionada == 1 && paginaAtual < totalPaginas - 1) {
	            paginaAtual++; // Avançar
	        } else {
	            break; // Clicou em "Fechar", no 'X' ou chegou nos limites
	        }
	    }
	}
	private void agendaIsVazia() {
		if (contatoService.listaCadastros().isEmpty()) {
			throw new RegraDeNegocioException("A agenda está vazia.");
		}
	}
	
	
}
