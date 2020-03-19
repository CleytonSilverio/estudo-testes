package br.estudo.dao;

import java.util.List;

import br.estudo.entidade.Locacao;

public interface LocacaoDao {
	
	public void salvar(Locacao locacao);

	public List<Locacao> obterLocacoesPendentes();

}
