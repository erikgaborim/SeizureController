package edu.ifms.QRCode.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.ifms.QRCode.modelo.Apreensao;
import edu.ifms.QRCode.modelo.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
	Iterable<Produto> findByApreensao(Optional<Apreensao> apreensao);
	List<Produto> findByApreensao(Apreensao apreensao);
	
}
