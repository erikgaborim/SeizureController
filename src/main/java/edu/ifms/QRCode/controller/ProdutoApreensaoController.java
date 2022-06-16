package edu.ifms.QRCode.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ifms.QRCode.modelo.Apreensao;
import edu.ifms.QRCode.modelo.Auxiliar;
import edu.ifms.QRCode.modelo.Produto;
import edu.ifms.QRCode.modelo.ValueScanned;
import edu.ifms.QRCode.repository.ApreensaoRepository;
import edu.ifms.QRCode.repository.ProdutoRepository;

@RestController
@RequestMapping("/produto-apreensao")
public class ProdutoApreensaoController {
	
	@Autowired
	private ApreensaoRepository apreensaoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	private String[] nomeItens;
	
	@GetMapping("/novo")
	public String novoVinculo(Model model) {
		model.addAttribute("auxiliar", new Auxiliar());
		model.addAttribute("produto", new Produto());
		return "produto-apreensao";
	}
	
	
	
	//Método que recebe o ID escaneado
	@PostMapping("/receber-id")
	public Apreensao idVinculado(@RequestBody ValueScanned valueScanned, Model model) {
		Long idSent = Long.parseLong(valueScanned.getValueScanned());
		Optional<Apreensao> apreensaoExistente = apreensaoRepository.findById(idSent);
		if (!apreensaoExistente.isPresent()) {
			throw new IllegalArgumentException("Produto inválido: " + idSent);
		}
		Apreensao apreensao = apreensaoExistente.get();
		System.out.println(apreensao.getData());
		
		int count = 0;
		this.nomeItens = new String[apreensao.getItens().size()];
		for(Produto item : apreensao.getItens()) {
			this.nomeItens[count] = item.getNome();
			count++;
		}
		
		apreensao.setItens(null);
		 
		return apreensao;
	}
	
	@GetMapping("receber-itens")
	public String[] recebeItens() {
		for(String nome : this.nomeItens) {
			System.err.println(nome);
		}
		return this.nomeItens;
	}
	
	
	@GetMapping("/chama-formulario/{id}")
	public String chamarFormulario(Long id, Model model) {
		model.addAttribute("apreensao", new Apreensao());
		model.addAttribute("produto", new Produto());
		return "produto-apreensao";
	}
}
