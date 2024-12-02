package curso.api.rest.dto;

import java.io.Serializable;

import curso.api.rest.model.Usuario;

/** 
 * 
 * DTO (Data Transfer Object), é um padrão de projeto utilizado para transferir dados entre diferentes camadas de uma aplicação,
 * geralmente entre o backend e o frontend. Ele permite encapsular dados em um objeto simples, 
 * que não contém lógica de negócios, e é utilizado para melhorar a eficiência da comunicação 
 * e reduzir o acoplamento entre as diferentes partes do sistema.
 * Um DTO é geralmente uma classe que possui atributos que correspondem aos dados que se deseja transferir.
 * 
 * Com DTO carrega-se no frontend apenas os atributos necessários ocultando os demais por questão de segurança.
 * 
 * */

public class UsuarioDTO implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String userLogin;
	private String userNome;
	private String userCpf;
	
	private String cep;
	private String logradouro;
	private String bairro;
	private String localidade;
	private String uf;
	private String estado;
	
	/* Construtor */
	public UsuarioDTO(Usuario usuario) {
		
		this.userLogin = usuario.getLogin();
		this.userNome = usuario.getNome();
		this.userCpf = usuario.getCpf();
		this.cep = usuario.getCep();
		this.logradouro = usuario.getLogradouro();
		this.bairro = usuario.getBairro();
		this.localidade = usuario.getLocalidade();
		this.uf = usuario.getUf();
		this.estado = usuario.getEstado();
	}
	
	public String getUserLogin() {
		return userLogin;
	}
	
	public void setUserLogin(String userLogin) {
		this.userLogin = userLogin;
	}
	
	public String getUserNome() {
		return userNome;
	}
	
	public void setUserNome(String userNome) {
		this.userNome = userNome;
	}
	
	public String getUserCpf() {
		return userCpf;
	}
	
	public void setUserCpf(String userCpf) {
		this.userCpf = userCpf;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getBairro() {
		return bairro;
	}

	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getLocalidade() {
		return localidade;
	}

	public void setLocalidade(String localidade) {
		this.localidade = localidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}
	
}