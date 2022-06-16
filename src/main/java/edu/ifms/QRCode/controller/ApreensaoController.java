package edu.ifms.QRCode.controller;

import java.io.IOException;
import java.util.Base64;
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

import com.google.zxing.WriterException;

import edu.ifms.QRCode.assets.QRCodeGenerator;
import edu.ifms.QRCode.modelo.Apreensao;
import edu.ifms.QRCode.modelo.Produto;
import edu.ifms.QRCode.repository.ApreensaoRepository;
import edu.ifms.QRCode.repository.ProdutoRepository;

@Controller
@RequestMapping("/apreensao")
public class ApreensaoController {
	
	@Autowired
	private ApreensaoRepository apreensaoRepository;
	
	@Autowired
	private ProdutoRepository ProdutoRepository;
	
	@GetMapping("/novo")
	public String adicionarApreensao(Model model) {
		model.addAttribute("apreensao", new Apreensao());
		return "forms/form-apreensao";
	}
	
	@PostMapping("/salvar")
	public String salvarApreensao(@Valid Apreensao apreensao, BindingResult result, 
				Model model, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return "forms/form-apreensao";
		}
		
		apreensaoRepository.save(apreensao);
		attributes.addFlashAttribute("mensagem", "Apreensão registrada com sucesso!");
		return "redirect:/apreensao/novo";
	}
	
	@GetMapping("/apagar/{id}")
	public String apagarApreensao(@PathVariable("id") long id, Model model) {
		Apreensao apreensao = apreensaoRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("Id inválido: " + id));
		apreensaoRepository.delete(apreensao);
	    return "redirect:/";
	}
	
	@GetMapping("/editar/{id}")
	public String editarProduto(@PathVariable("id") long id, Model model) {
		Optional<Apreensao> apreensaoVelha = apreensaoRepository.findById(id);
		if (!apreensaoVelha.isPresent()) {
            throw new IllegalArgumentException("Apreensão inválida:" + id);
        } 
		Apreensao apreensao = apreensaoVelha.get();
	    model.addAttribute("apreensao", apreensao);
	    return "forms/form-apreensao-edition";
	}
	
	@PostMapping("/editar/{id}")
	public String editarApreensao(@PathVariable("id") long id, 
			@Valid Apreensao apreensao, BindingResult result) {
		if (result.hasErrors()) {
	    	apreensao.setId(id);
	        return "forms/form-apreensao";
	    }
	    apreensaoRepository.save(apreensao);
	    return "redirect:/";
	}
	
	@GetMapping("/listar/{id}")
	public String listarProdutosApreensao(@PathVariable("id") long id, Model model) {
		Optional<Apreensao> apreensao = apreensaoRepository.findById(id);
		Iterable<Produto> produtos = ProdutoRepository.findByApreensao(apreensao);


		model.addAttribute("message", "Lista de produtos da apreensão - "+ Long.toString(id));
		model.addAttribute("identificador", "" + Long.toString(id));
		model.addAttribute("produtos", produtos);
		
		return "listar-apreensao-produtos";
	}
	
	@GetMapping("/qrcode/{id}")
	public String mostrarQRCode(@PathVariable("id") long id, Model model, RedirectAttributes attributes) {
		String data = Long.toString(id);

		byte[] qrcodeValue = null;

		try {
			qrcodeValue = QRCodeGenerator.getQRCodeImage(data, 500, 500);
		} catch (WriterException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		model.addAttribute("image", Base64.getEncoder().encodeToString(qrcodeValue));

		return "mostrar-qrcode";
	}
	
}
