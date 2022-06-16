package edu.ifms.QRCode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import edu.ifms.QRCode.modelo.Apreensao;

@Repository
public interface ApreensaoRepository extends JpaRepository<Apreensao, Long> {
	
}
