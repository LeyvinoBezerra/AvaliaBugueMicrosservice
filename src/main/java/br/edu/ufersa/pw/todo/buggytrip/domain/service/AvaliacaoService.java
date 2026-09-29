package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Avaliacao.*;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.entities.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.AvaliacaoRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {
    final AvaliacaoRepository repo;
    final UsuarioService users;

    public AvaliacaoService(AvaliacaoRepository r, UsuarioService u) {
        repo = r;
        users = u;
    }

    @Transactional
    public AvaliacaoResponse criar(AvaliacaoRequest r, Long currentUserId, boolean admin) {
        if (!admin && !r.avaliadorId().equals(currentUserId))
            throw new br.edu.ufersa.pw.todo.buggytrip.api.exceptions.BusinessRuleException("O avaliador deve ser o usuário autenticado");
        validar(r);
        if (repo.existsByAvaliadorIdAndBugueiroId(r.avaliadorId(), r.bugueiroId()))
            throw new ConflictException("O avaliador já avaliou este bugueiro");
        var a = new Avaliacao();
        copy(r, a);
        a.setAvaliador(users.get(r.avaliadorId()));
        a.setBugueiro(users.get(r.bugueiroId()));
        return out(repo.save(a));
    }

    @Transactional
    public AvaliacaoResponse atualizar(Long id, AvaliacaoRequest r, Long currentUserId, boolean admin) {
        var a = get(id);
        if (!admin && !a.getAvaliador().getId().equals(currentUserId))
            throw new org.springframework.security.access.AccessDeniedException("Somente o avaliador ou ADMIN pode alterar");
        validar(r);
        if (!a.getAvaliador().getId().equals(r.avaliadorId()) || !a.getBugueiro().getId().equals(r.bugueiroId()))
            if (repo.existsByAvaliadorIdAndBugueiroId(r.avaliadorId(), r.bugueiroId()))
                throw new ConflictException("Já existe avaliação para este par");
        copy(r, a);
        a.setAvaliador(users.get(r.avaliadorId()));
        a.setBugueiro(users.get(r.bugueiroId()));
        return out(repo.save(a));
    }

    @Transactional
    public void remover(Long id, Long currentUserId, boolean admin) {
        var a = get(id);
        if (!admin && !a.getAvaliador().getId().equals(currentUserId))
            throw new org.springframework.security.access.AccessDeniedException("Somente o avaliador ou ADMIN pode remover");
        repo.delete(a);
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse buscar(Long id) {
        return out(get(id));
    }

    @Transactional(readOnly = true)
    public PageResponse<AvaliacaoResponse> listar(Long bugueiroId, Pageable p) {
        var x = (bugueiroId == null ? repo.findAll(p) : repo.findByBugueiroId(bugueiroId, p)).map(this::out);
        return new PageResponse<>(x.getContent(), x.getNumber(), x.getSize(), x.getTotalElements(), x.getTotalPages(), x.isFirst(), x.isLast());
    }

    private Avaliacao get(Long id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Avaliação não encontrada: " + id));
    }

    private void validar(AvaliacaoRequest r) {
        var a = users.get(r.avaliadorId());
        var b = users.get(r.bugueiroId());
        if (a.getId().equals(b.getId()))
            throw new BusinessRuleException("Avaliador e bugueiro devem ser usuários diferentes");
        if (a.getUsuarioTipo() != EnumUsuario.CLIENTE)
            throw new BusinessRuleException("O avaliador deve possuir tipo CLIENTE");
        if (b.getUsuarioTipo() != EnumUsuario.BUGUEIRO)
            throw new BusinessRuleException("O avaliado deve possuir tipo BUGUEIRO");
    }

    private void copy(AvaliacaoRequest r, Avaliacao a) {
        a.setSeguranca(r.seguranca());
        a.setConhecimentoRoteiro(r.conhecimentoRoteiro());
        a.setConfortoVeiculo(r.confortoVeiculo());
        a.setSimpatiaMotorista(r.simpatiaMotorista());
        a.setExperienciaGeral(r.experienciaGeral());
        a.setAdaptabilidade(r.adaptabilidade());
        a.setParadasInteressantes(r.paradasInteressantes());
        a.setDiferencial(r.diferencial());
        a.setFeedback(r.feedback());
    }

    private AvaliacaoResponse out(Avaliacao a) {
        double n = (a.getSeguranca() + a.getConhecimentoRoteiro() + a.getConfortoVeiculo() + a.getSimpatiaMotorista() + a.getExperienciaGeral() + a.getAdaptabilidade() + a.getParadasInteressantes()) / 7.0;
        return new AvaliacaoResponse(a.getId(), a.getSeguranca(), a.getConhecimentoRoteiro(), a.getConfortoVeiculo(), a.getSimpatiaMotorista(), a.getExperienciaGeral(), a.getAdaptabilidade(), a.getParadasInteressantes(), a.getDiferencial(), a.getFeedback(), a.getAvaliador().getId(), a.getBugueiro().getId(), a.getDataCriacao(), a.getDataAtualizacao(), Math.round(n * 100) / 100.0);
    }
}
