package biojobs;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BioJobResultDao extends JpaRepository<BioJobResult, Long> {

    BioJobResult findByResultFileName(String fileName);
}
