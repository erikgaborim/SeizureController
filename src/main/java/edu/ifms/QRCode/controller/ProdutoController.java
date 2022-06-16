package edu.ifms.QRCode.controller;

import java.util.Optional;


import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.ifms.QRCode.modelo.Apreensao;
import edu.ifms.QRCode.modelo.Auxiliar;
import edu.ifms.QRCode.modelo.Produto;
import edu.ifms.QRCode.repository.ApreensaoRepository;
import edu.ifms.QRCode.repository.ProdutoRepository;

@Controller
@RequestMapping("/produto")
public class ProdutoController {

	@Autowired
	private ProdutoRepository produtoRepository;
	
	@Autowired
	private ApreensaoRepository apreensaoRepository;

	@GetMapping("/novo")
	public String adicionarProduto(Model model) {
		model.addAttribute("produto", new Produto());
		model.addAttribute("auxiliar", new Auxiliar());
		return "forms/form-produto";
	}
	
	private void vincularProdutoApreensao(Produto produto, Apreensao apreensao) {
		apreensao = apreensaoRepository.findById(apreensao.getId()).get();
		System.err.println(apreensao.getId());
		apreensao.addItem(produto);
		apreensaoRepository.save(apreensao);
	}	

	@PostMapping("/salvar")
	public String salvarProduto(Produto produto, Auxiliar auxiliar, BindingResult result, Model model,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return "forms/form-produto";
		}
				
		Apreensao apreensao = apreensaoRepository.findById(auxiliar.getIdAux()).get();
		produto.setApreensao(apreensao);
		produtoRepository.save(produto);
		vincularProdutoApreensao(produto, apreensao);
		
		attributes.addFlashAttribute("mensagem", "Produto salvo com sucesso!");
		return "redirect:/";
	}

	@GetMapping("/apagar/{id}")
	public String apagarProduto(@PathVariable("id") long id, Model model) {
		Produto produto = produtoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Id inválido:" + id));
		produtoRepository.delete(produto);
		return "redirect:/";
	}

	@GetMapping("/editar/{id}")
	public String editarProduto(@PathVariable("id") long id, Model model) {
		Optional<Produto> produtoVelho = produtoRepository.findById(id);
		if (!produtoVelho.isPresent()) {
			throw new IllegalArgumentException("Produto inválido: " + id);
		}
		Produto produto = produtoVelho.get();
		
		Auxiliar auxiliar = new Auxiliar();
		auxiliar.setIdAux(produto.getApreensao().getId());
		
		model.addAttribute("auxiliar", auxiliar);
		model.addAttribute("produto", produto);
		return "forms/form-produto-edition";
	}

	@PostMapping("/editar/{id}")
	public String salvarEditProduto(@PathVariable("id") long id, @Valid Produto produto, @Valid Auxiliar auxiliar, BindingResult result) {
		if (result.hasErrors()) {
			produto.setId(id);
			return "forms/form-produto";
		}
		
		Apreensao apreensaoNova = apreensaoRepository.findById(auxiliar.getIdAux()).get();
		Produto produtoVelho = produtoRepository.findById(produto.getId()).get();
		
		Long idApreensaoVelho = produtoVelho.getApreensao().getId();
		Apreensao apreensaoVelha = apreensaoRepository.findById(idApreensaoVelho).get();
		
		produto.setApreensao(apreensaoNova);
		produtoRepository.save(produto);
		
		if(idApreensaoVelho != apreensaoNova.getId()) {
			apreensaoVelha.removeItem(produtoVelho);
			apreensaoRepository.save(apreensaoVelha);
			vincularProdutoApreensao(produto, apreensaoNova);
			
		}

		return "redirect:/";
	}
	
}
