package edu.ifms.QRCode.controller;

import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.ifms.QRCode.repository.ApreensaoRepository;
import edu.ifms.QRCode.repository.ProdutoRepository;

@Controller
public class HomeController {
	
	@Autowired
	private ApreensaoRepository apreensaoRepository;
	
	@Autowired
	private ProdutoRepository produtoRepository;
	
	@RequestMapping("/") 
	public String index(Model model) {
		model.addAttribute("message", "Controle de apreensões com QRCode");
		model.addAttribute("apreensoes", apreensaoRepository.findAll());
		model.addAttribute("produtos", produtoRepository.findAll());
		return "index";
	}
}
