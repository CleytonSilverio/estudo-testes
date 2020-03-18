package br.estudo.servico;

import static br.estudo.util.DataUtils.adicionarDias;

import java.util.Date;

import br.estudo.entidade.Filme;
import br.estudo.entidade.Locacao;
import br.estudo.entidade.Usuario;
import br.estudo.exception.FilmeSemEstoqueException;
import br.estudo.exception.LocadoraException;

public class LocacaoService {
	
	public Locacao alugarFilme(Usuario usuario, Filme filme) throws Exception {
		
		if (filme.getEstoque() == 0) {
			throw new FilmeSemEstoqueException(null);
		}
		if (usuario == null) {
			throw new LocadoraException("Usuario inexistente!");
		}
		
		Locacao locacao = new Locacao();
		locacao.setFilme(filme);
		locacao.setUsuario(usuario);
		locacao.setDataLocacao(new Date());
		locacao.setValor(filme.getPrecoLocacao());

		//Entrega no dia seguinte
		Date dataEntrega = new Date();
		dataEntrega = adicionarDias(dataEntrega, 1);
		locacao.setDataRetorno(dataEntrega);
		
		//Salvando a locacao...	
		
		return locacao;
	}
	
}