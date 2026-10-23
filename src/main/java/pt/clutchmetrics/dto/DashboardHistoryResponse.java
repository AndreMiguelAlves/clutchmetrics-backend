
package pt.clutchmetrics.dto;

import java.util.List;

public record DashboardHistoryResponse(
        List<MatchResponse> matches,
        long totalMatches,
        int page,
        int pageSize,
        boolean hasMore
) {
}
