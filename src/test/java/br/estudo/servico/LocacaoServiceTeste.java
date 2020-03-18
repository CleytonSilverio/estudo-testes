package br.estudo.servico;

import static org.junit.Assert.fail;

import java.util.Date;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;

import br.estudo.entidade.Filme;
import br.estudo.entidade.Locacao;
import br.estudo.entidade.Usuario;
import br.estudo.exception.FilmeSemEstoqueException;
import br.estudo.exception.LocadoraException;
import br.estudo.util.DataUtils;

public class LocacaoServiceTeste {
	
	private LocacaoService service;
	private Usuario usuario;
	private Filme filme;
	private Locacao locacao;

	@Rule
	public ErrorCollector error = new ErrorCollector();
	@Rule
	public ExpectedException exception = ExpectedException.none();

	@Before
	public void before() {
		// cenário
		filme = new Filme("filme 1.", 2, 4.00);
		service = new LocacaoService();
		usuario = new Usuario("usuario 1");
	}

	@Test
	public void testLocacao() throws Exception {
		// ação
		locacao = service.alugarFilme(usuario, filme);

		// verificação
		error.checkThat(locacao.getValor(), CoreMatchers.is(CoreMatchers.equalTo(4.00)));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataLocacao(), new Date()), CoreMatchers.is(true));
		error.checkThat(DataUtils.isMesmaData(locacao.getDataRetorno(), DataUtils.obterDataComDiferencaDias(1)), CoreMatchers.is(true));

	}
	@Test(expected = FilmeSemEstoqueException.class)
	public void testLocacao_filmeSemEstouqe_1() throws Exception {
		filme = new Filme("filme 1", 0, 4.00);
		service.alugarFilme(usuario, filme);
	}
	@Test
	public void testLocacao_filmeSemEstouqe_2() {
		filme = new Filme("filme 1", 0, 4.00);
		try {
			service.alugarFilme(usuario, filme);
		} catch (Exception e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Filme sem estoque!"));
		}
	}
	@Test
	public void testLocacao_filmeSemEstouqe_3() throws Exception {
		// cenário
		filme = new Filme("filme 1", 0, 4.00);
		exception.expect(Exception.class);
		exception.expectMessage("Filme sem estoque!");
		// ação
		service.alugarFilme(usuario, filme);

	}

	@Test
	public void testLocacao_usuarioVazio() throws Exception {
		try {
			service.alugarFilme(null, filme);
			fail();
		} catch (LocadoraException e) {
			Assert.assertThat(e.getMessage(), CoreMatchers.is("Usuário vazio!"));
		}
		System.out.println("Forma robusta");
	}

	@Test
	public void testLocacao_filmeVazio() throws Exception {
		exception.expect(Exception.class);
		exception.expectMessage("Filme vazio!");
		service.alugarFilme(usuario, null);
	}
}
