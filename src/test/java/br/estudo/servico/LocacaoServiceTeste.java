package br.estudo.servico;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import br.estudo.dao.LocacaoDao;
import br.estudo.entidade.Filme;
import br.estudo.entidade.Locacao;
import br.estudo.entidade.Usuario;
import br.estudo.exception.FilmeSemEstoqueException;
import br.estudo.exception.LocadoraException;
import br.estudo.matchers.MeusMatchers;
import br.estudo.util.DataUtils;

public class LocacaoServiceTeste {
	
private LocacaoService service;

	private SerasaService serasa;
	private LocacaoDao dao;
	private EmailService email;
	
	@Rule
	public ErrorCollector error = new ErrorCollector();
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Before
	public void setup(){
		service = new LocacaoService();
		dao = Mockito.mock(LocacaoDao.class);
		service.setLocacaoDao(dao);
		serasa = Mockito.mock(SerasaService.class);
		service.setSerasa(serasa);
		email = Mockito.mock(EmailService.class);
		service.setEmailService(email);
	}
	
	@Test
	public void deveAlugarFilme() throws Exception {
		Assume.assumeFalse(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		//acao
		Locacao locacao = service.alugarFilme(usuario, filmes);
			
		//verificacao
		error.checkThat(locacao.getValor(), is(equalTo(5.0)));
		error.checkThat(locacao.getDataLocacao(), MeusMatchers.eHoje());
		error.checkThat(locacao.getDataRetorno(), MeusMatchers.eHojeComDiferencaDias(1));
	}
	
	@Test(expected = FilmeSemEstoqueException.class)
	public void naoDeveAlugarFilmeSemEstoque() throws Exception{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 0, 4.0));
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void naoDeveAlugarFilmeSemUsuario() throws FilmeSemEstoqueException{
		//cenario
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		//acao
		try {
			service.alugarFilme(null, filmes);
			Assert.fail();
		} catch (LocadoraException e) {
			assertThat(e.getMessage(), is("Usuario vazio"));
		}
	}

	@Test
	public void naoDeveAlugarFilmeSemFilme() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Filme vazio");
		
		//acao
		service.alugarFilme(usuario, null);
	}
	
	@Test
	public void deveDevolverNaSegundaAoAlugarNoSabado() throws FilmeSemEstoqueException, LocadoraException{
		Assume.assumeTrue(DataUtils.verificarDiaSemana(new Date(), Calendar.SATURDAY));
		
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		//acao
		Locacao retorno = service.alugarFilme(usuario, filmes);
		
		//verificacao
		assertThat(retorno.getDataRetorno(), MeusMatchers.caiNumaSegunda());
		
	}
	
	@Test
	public void naoDeveAlugarFilmeNegativado() throws FilmeSemEstoqueException, LocadoraException{
		//cenario
		Usuario usuario = new Usuario("Usuario 1");
		List<Filme> filmes = Arrays.asList(new Filme("Filme 1", 1, 5.0));
		
		Mockito.when(serasa.possuiNegativacao(usuario)).thenReturn(true);
		
		exception.expect(LocadoraException.class);
		exception.expectMessage("Usuário negativado!");
		
		//acao
		service.alugarFilme(usuario, filmes);
	}
	
	@Test
	public void deveEnviarEmailAtrasado() {
		//Se criar uma builder este metodo funciona :)
		//cenario
		//List<Locacao> locacoes = Arrays.asList(locacaoBuilder().umaLocacao().comDataRetorno(obterDataComDiferencaDias(-2).agora());
		//Mockito.when(dao.obterLocacoesPendentes()).thenReturn(locacoes);
		//acao
		//service.notificarAtraso();
		//verificacao
		//Mockito.verify(email).notificarAtraso(usuario);
		//Mockito.verify(email, Mockito.never()).notificarAtraso(usuario2);
		//Mockito.verifyNoMoreInteractions(email);
		
		
	}
}
