package main;

import controller.AgendaController;
import service.ContatoService;
import service.RelatorioAgendaService;
import service.ScoreRelacionamentoService;
import view.AgendaView;

/**
 * Classe principal da aplicação.
 * <p>
 * Responsável pela interação com o usuário através de caixas de diálogo,
 * captura de entradas e exibição das informações da agenda.
 * </p>
 */
public class AgendaApplication {

	public static void main(String[] args) {
		ContatoService contatoService = new ContatoService();
		RelatorioAgendaService agendaService = new RelatorioAgendaService();
		ScoreRelacionamentoService scoreService = new  ScoreRelacionamentoService();
		AgendaView agendaView =  new  AgendaView();
		
		AgendaController agendaController = new AgendaController(agendaView, contatoService, agendaService, scoreService);
		
		agendaController.iniciar();
	}

}
