package curso.api.rest.controller;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import curso.api.rest.dto.UsuarioDTO;
import curso.api.rest.model.Usuario;
import curso.api.rest.repository.UsuarioRepository;

@SuppressWarnings({ "unchecked", "rawtypes" })

@RestController /* Arquitetura RESTful */
@RequestMapping(value = "/usuario")
public class IndexController {
	
	@Autowired /* Se fosse CDI seria @Inject */
	private UsuarioRepository usuarioRepository;

	/* Serviço RESTful
	 * Busca usuário por id */
	@GetMapping(value = "/{id}", produces = "application/json")
	@CacheEvict(value="cacheuser", allEntries = true)
	@CachePut("cacheuser")
	public ResponseEntity<Usuario> init(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/* Retorna os dados do usuário em formato Json */
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/* Serviço RESTful
	 * Busca usuário por id */
	@GetMapping(value = "/{id}", produces = "application/json", headers = "X-API-Version=v1")
	@CacheEvict(value="cacheuser", allEntries = true)
	@CachePut("cacheuser")
	public ResponseEntity<UsuarioDTO> initV1(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id); /* Consulta ao BD */
		
		/* Retorna os dados do usuário em formato Json */
		return new ResponseEntity<UsuarioDTO>(new UsuarioDTO(usuario.get()), HttpStatus.OK);
	}
	
	/* Serviço RESTful
	 * Busca usuário por id */
	@GetMapping(value = "/{id}", produces = "application/json",  headers = "X-API-Version=v2")
	public ResponseEntity<Usuario> initV2(@PathVariable (value = "id") Long id) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/* Retorna os dados do usuário em formato Json */
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}
	
	/*Vamos supor que o carregamento de usuário seja um processo lento
	 * e queremos controlar ele com cache para agilizar o processo*/
	@GetMapping(value = "/", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuario () throws InterruptedException{
		
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findAll();
		
		/* Retorna os dados do usuário em formato Json */
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
	}
	
	/* END-POINT Consulta de usuário por nome */
	@GetMapping(value = "/usuarioPorNome/{nome}", produces = "application/json")
	@CacheEvict(value="cacheusuarios", allEntries = true)
	@CachePut("cacheusuarios")
	public ResponseEntity<List<Usuario>> usuarioPorNome (@PathVariable (value = "nome") String nome) throws InterruptedException{
		
		List<Usuario> lista = (List<Usuario>) usuarioRepository.findUserByNome(nome);
		
		/* Retorna os dados do usuário em formato Json */
		return new ResponseEntity<List<Usuario>>(lista, HttpStatus.OK);
	}
	
	/* Serviço RESTful */
	@GetMapping(value = "/{id}/codigovenda/{venda}", produces = "application/pdf")
	public ResponseEntity<Usuario> relatorio(@PathVariable (value = "id") Long id, @PathVariable (value = "venda") Long venda) {
		
		Optional<Usuario> usuario = usuarioRepository.findById(id);
		
		/*o retorno seria um relatorio*/
		return new ResponseEntity<Usuario>(usuario.get(), HttpStatus.OK);
	}

	/* Libera acesso para este End-Points apenas para requisições que vierem do computador local 
	@CrossOrigin(origins = "http://localhost:8080")*/
	@PostMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> cadastrar(@RequestBody Usuario usuario) {
	    try {
	        /* Ao persistir associa os telefones ao usuário */
	        for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
	            usuario.getTelefones().get(pos).setUsuario(usuario);
	        }
	        
	        // ------------------------ INICIO - Consumindo API Pública Externa ------------------------ //
	        URL url = new URL("https://viacep.com.br/ws/" + usuario.getCep() + "/json/");
	        URLConnection connection = url.openConnection();
	        InputStream is = connection.getInputStream();
	        BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

	        String cep = "";
	        StringBuffer jsonCep = new StringBuffer();
	        while ((cep = br.readLine()) != null) {
	            jsonCep.append(cep);
	        }
	        
	        // Convertendo os dados recebidos na variável jsonCep em um Json
	        Usuario usuarioAux = new Gson().fromJson(jsonCep.toString(), Usuario.class);
	        usuario.setCep(usuarioAux.getCep());
	        usuario.setLogradouro(usuarioAux.getLogradouro());
	        usuario.setComplemento(usuarioAux.getComplemento());
	        usuario.setBairro(usuarioAux.getBairro());
	        usuario.setLocalidade(usuarioAux.getLocalidade());
	        usuario.setUf(usuarioAux.getUf());
	        usuario.setEstado(usuarioAux.getEstado());
	        usuario.setDdd(usuarioAux.getDdd());
	        // ------------------------ FIM - Consumindo API Pública Externa ------------------------ //

	        /* Criptografando a senha do usuário */
	        String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
	        usuario.setSenha(senhaCriptografada);
	        
	        /* Salva o usuário */
	        Usuario usuarioSalvo = usuarioRepository.save(usuario);
	        return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED); // Return created response on success
	    } catch (Exception e) {
	    	  return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

	
	@PostMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity cadastrarvenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		
		/*Aqui seria o processo de venda*/
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity("id user :" + iduser + " idvenda :"+ idvenda, HttpStatus.OK);
		
	}
	
	@PutMapping(value = "/", produces = "application/json")
	public ResponseEntity<Usuario> atualizar(@RequestBody Usuario usuario) {
	    try {
	        /* Ao persistir associa os telefones ao usuário */
	        for (int pos = 0; pos < usuario.getTelefones().size(); pos++) {
	            usuario.getTelefones().get(pos).setUsuario(usuario);
	        }
	        
	        /* Criptografando a senha do usuário */
	        Usuario userTemporario = usuarioRepository.findById(usuario.getId()).orElseThrow(() -> new Exception("Usuário não encontrado"));

	        if (!userTemporario.getSenha().equals(usuario.getSenha())) { /* Senhas diferentes */
	            String senhaCriptografada = new BCryptPasswordEncoder().encode(usuario.getSenha());
	            usuario.setSenha(senhaCriptografada);
	        }

	        /* Outras rotinas antes de atualizar */
	        Usuario usuarioSalvo = usuarioRepository.save(usuario);
	        return new ResponseEntity<>(usuarioSalvo, HttpStatus.OK); // Return updated user on success
	    } catch (Exception e) {
	    	 return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	}

	
	/* Libera acesso para este End-Points para requisições que vierem de qualquer lugar */
	@CrossOrigin
	@DeleteMapping(value = "/{id}", produces = "application/text")
	public String delete(@PathVariable (value = "id") Long id){
		
		usuarioRepository.deleteById(id);
		
		return "ok";
		
	}
	
	/* Libera acesso para este End-Points apenas para requisições que vierem do www.sistemacliente1.com.br */
	@CrossOrigin(origins = "www.sistemacliente1.com.br")
	@DeleteMapping(value = "/{id}/venda", produces = "application/text")
	public String deleteVenda(@PathVariable (value = "id") Long id){
		
		usuarioRepository.deleteById(id);
		
		return "ok";
		
	}
	
	/* Libera acesso para este End-Points apenas para requisições que vierem dos clientes
	 * www.sistemacliente2.com.br e www.sistemacliente3.com.br */
	@CrossOrigin(origins = {"www.sistemacliente2.com.br", "www.sistemacliente3.com.br"})
	@PutMapping(value = "/{iduser}/idvenda/{idvenda}", produces = "application/json")
	public ResponseEntity updatevenda(@PathVariable Long iduser, @PathVariable Long idvenda) {
		
		/*Aqui seria o processo de venda*/
		//Usuario usuarioSalvo = usuarioRepository.save(usuario);
		
		return new ResponseEntity("Venda Atualizada", HttpStatus.OK);
		
	}
}