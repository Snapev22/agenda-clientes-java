package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import javax.swing.JOptionPane;

import entities.Contato;
import entities.PessoaFisica;
import entities.PessoaJuridica;
import exceptions.RegraDeNegocioException;

public class AgendaView {

	public int exibirMenu() {
		String opcaoStr = JOptionPane.showInputDialog(null, """
				====== Agenda de Clientes ======
				1 - Cadastrar contato
				2 - Pesquisar por idade
				3 - Listar em ordem alfabética
				4 - Buscar por nome
				5 - Alterar cadastro
				6 - Remover contato
				7 - Listar todos os cadastros
				8 - Estatísticas da agenda
				9 - Exibir ranking dos relacionamentos
				10 - Sair
				================================
				Escolha uma opção:\s""");
		return lerInteiro("opcao", opcaoStr, true);
	}

	public Optional<Contato> cadastrarContato() {
		String[] tipos = { "Pessoa Física", "Pessoa Jurídica" };
		int tipo = JOptionPane.showOptionDialog(null, "Qual o tipo de contato?", "Tipo de Contato",
				JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

		if (tipo == JOptionPane.CLOSED_OPTION)
			return Optional.empty();

		String nome = JOptionPane.showInputDialog(null, "Nome:");
		String telefone = lerSomenteDigitos(JOptionPane.showInputDialog(null, "Telefone:"));
		String endereco = JOptionPane.showInputDialog(null, "Endereço (opcional):");
		String ucStr = JOptionPane.showInputDialog(null, "Data do último contato (dd/mm/aaaa, opcional):");
		LocalDate ultimoContato = (lerData("ultimo contato", ucStr, false));
	
		Contato contato = 
				tipo == 0 ?PessoaFisica.builder().nome(nome).telefone(telefone).endereco(endereco).ultimoContato(ultimoContato).build()
				: PessoaJuridica.builder().nome(nome).telefone(telefone).endereco(endereco).ultimoContato(ultimoContato).build();

		preencherPorTipoDeContato(contato);
		return Optional.ofNullable(contato);
	
	}
	
	public int obterIdadeParaBuscar() {
		String idadePesquisaStr = JOptionPane.showInputDialog(null, "Digite a idade para pesquisa: ");
		return lerInteiro("idade", idadePesquisaStr, false);
	
	}
	
	public  String obterNomeParaBusca() {
        return JOptionPane.showInputDialog(null, "Digite o nome (ou trecho) para busca:");
    }

	public int obterIdParaBusca() {
	    String idBuscaStr = JOptionPane.showInputDialog(null, "Digite o ID para realizar busca: ");
	    return lerInteiro("ID", idBuscaStr, true);
	}
	
	public boolean confirmarAlteracao(Contato contato) {
	    int confirmar = JOptionPane.showConfirmDialog(null,
	            "Contato encontrado. Deseja alterar?\n\n" + contato,
	            "Confirmação de Alteração", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	            
	    return confirmar == JOptionPane.YES_OPTION;
	}
	
	public boolean confirmarRemocao(Contato contato) {
		int confirmar = JOptionPane.showConfirmDialog(null,
                "Deseja remover o seguinte contato?\n\n" + contato,
                "Confirmação de Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		
		return confirmar == JOptionPane.YES_OPTION;
	}
	
	public void preencherNovoContato(Contato contato) {
		contato.setNome(JOptionPane.showInputDialog(null, "Contato encontrado: \n " + "Digite o nome: "));
		contato.setEndereco(JOptionPane.showInputDialog(null, "Digite o endereço: "));
		contato.setTelefone(lerSomenteDigitos(JOptionPane.showInputDialog(null, "Telefone:")));
		String ucStr = JOptionPane.showInputDialog(null, "Data do último contato (dd/mm/aaaa, opcional):");
		contato.setUltimoContato(lerData("ultimo contato", ucStr, false));
		
		preencherPorTipoDeContato(contato);
	}
	
	public void exibirMensagem(String mensagem, String titulo, int tipo) {
		JOptionPane.showMessageDialog(null, mensagem, titulo, tipo);
	}

	public int confirmar(String mensagem, String titulo) {
		return JOptionPane.showConfirmDialog(null, mensagem, titulo, JOptionPane.YES_NO_OPTION);
	}

	private void preencherPorTipoDeContato(Contato contato) {
		if (contato instanceof PessoaFisica pf) {
			pf.setCpf(lerSomenteDigitos(JOptionPane.showInputDialog(null, "CPF:")));
			String dataStr = JOptionPane.showInputDialog(null, "Data de nascimento (dd/mm/aaaa):");
	        pf.setDataNascimento(lerData("Data de nascimento", dataStr, false));
		} else if (contato instanceof PessoaJuridica pj) {
			pj.setCnpj(lerSomenteDigitos(JOptionPane.showInputDialog(null, "CNPJ:")));
			pj.setRazaoSocial(JOptionPane.showInputDialog(null, "Razão social:"));
		}
	}

	
	public int exibirPagina(String conteudoPagina, int paginaAtual, int totalPaginas) {
	    String[] opcoes = { "Voltar", "Avançar", "Fechar" }; // Adicionado "Fechar" explicitamente para ajudar o usuário
	    
	    return JOptionPane.showOptionDialog( null, conteudoPagina,
	            "Página " + paginaAtual + " / " + totalPaginas, 
	            JOptionPane.DEFAULT_OPTION,JOptionPane.INFORMATION_MESSAGE, 
	            null,  opcoes, opcoes[1] // Foca no "Avançar" por padrão
	    );
	}
	
	private static Integer lerInteiro(String nomeCampo, String valor, boolean obrigatorio) {
		if (valor == null || valor.isBlank()) {
			if (obrigatorio) {
				throw new RegraDeNegocioException("o campo '" + nomeCampo + "' não pode ser nulo.");
			}
			return null;
		}

		try {
			return Integer.parseInt(valor);
		} catch (NumberFormatException e) {
			throw new RegraDeNegocioException("O valor para '" + nomeCampo + "' deve ser um número inteiro válido");
		}
	}
	
	private static LocalDate lerData(String nomeCampo, String valor, boolean obrigatorio) {
	    if (valor == null || valor.isBlank()) {
	        if (obrigatorio) {
	            throw new RegraDeNegocioException("O campo '" + nomeCampo + "' não pode ser nulo.");
	        }
	        return null;
	    }

	    try {
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	        
	        return LocalDate.parse(valor, formatter);
	    } catch (java.time.format.DateTimeParseException e) {
	        throw new RegraDeNegocioException("O valor para '" + nomeCampo + "' deve ser uma data válida no formato dd/mm/aaaa");
	    }
	}
	
	private String lerSomenteDigitos(String valor) {
	    return valor == null ? null : valor.replaceAll("\\D", "");
	}
}
