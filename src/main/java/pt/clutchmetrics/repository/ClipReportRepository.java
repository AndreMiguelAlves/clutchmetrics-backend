
package pt.clutchmetrics.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.clutchmetrics.entity.Clip;
import pt.clutchmetrics.entity.ClipReport;
import pt.clutchmetrics.entity.User;

import java.util.List;

public interface ClipReportRepository
        extends JpaRepository<ClipReport, Long> {

    // Verificar se o utilizador já denunciou este clip
    boolean existsByClipAndReporter(
            Clip clip,
            User reporter
    );

    // Listar denúncias da mais recente para a mais antiga
    List<ClipReport> findAllByOrderByCreatedAtDesc();

    // Encontrar denúncias associadas a um clip
    List<ClipReport> findByClip(Clip clip);
}
