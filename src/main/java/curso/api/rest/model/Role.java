package curso.api.rest.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.springframework.security.core.GrantedAuthority;

/**
 * @author Angela
 * Classe que representa a tabela no banco de dados. 
 * Role = Papéis
 *
 */

@Entity
@Table(name = "role")
@SequenceGenerator(name = "seq_role", sequenceName = "seq_role", allocationSize= 1, initialValue = 1)
public class Role implements GrantedAuthority ,Serializable{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_role")
	private Long id;
	private String nomeRole; /* Papel no sistema (ROLE_SECRETARIA, ROLE_GERENTE) */
	
	@Override
	public String getAuthority() {
		/* Retorna o nome do papel, acesso ou autorização, exemplo : ROLE_ADMIN, ROLE_GERENTE, ROLE_SECRETARIO*/
		return this.nomeRole;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNomeRole() {
		return nomeRole;
	}

	public void setNomeRole(String nomeRole) {
		this.nomeRole = nomeRole;
	}
	
}