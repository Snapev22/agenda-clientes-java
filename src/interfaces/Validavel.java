package interfaces;

import exceptions.RegraDeNegocioException;

public interface Validavel {
	void validar() throws RegraDeNegocioException;
}
