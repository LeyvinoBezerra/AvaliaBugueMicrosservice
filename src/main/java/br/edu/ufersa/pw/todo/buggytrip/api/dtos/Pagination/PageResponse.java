package br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination;

import java.util.List;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages, boolean first,
                              boolean last) {
}
