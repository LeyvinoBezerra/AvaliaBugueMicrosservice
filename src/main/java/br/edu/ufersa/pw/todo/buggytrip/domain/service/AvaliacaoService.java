package br.edu.ufersa.pw.todo.buggytrip.domain.service;

import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Avaliacao.*;
import br.edu.ufersa.pw.todo.buggytrip.api.dtos.Pagination.PageResponse;
import br.edu.ufersa.pw.todo.buggytrip.api.exceptions.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.entities.*;
import br.edu.ufersa.pw.todo.buggytrip.domain.enuns.EnumUsuario;
import br.edu.ufersa.pw.todo.buggytrip.domain.repositories.AvaliacaoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AvaliacaoService {
    private static final Logger logger = LoggerFactory.getLogger(AvaliacaoService.class);

    private final AvaliacaoRepository avaliacaoRepository;
    private final UsuarioService usuarioService;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, UsuarioService usuarioService) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional
    public AvaliacaoResponse criar(
            AvaliacaoRequest avaliacaoRequest, Long authenticatedUserId, boolean isAdministrator) {
        if (!isAdministrator && !avaliacaoRequest.avaliadorId().equals(authenticatedUserId))
            throw new br.edu.ufersa.pw.todo.buggytrip.api.exceptions.BusinessRuleException("O avaliador deve ser o usuário autenticado");
        validarParticipantes(avaliacaoRequest);
        if (avaliacaoRepository.existsByAvaliadorIdAndBugueiroId(
                avaliacaoRequest.avaliadorId(), avaliacaoRequest.bugueiroId()))
            throw new ConflictException("O avaliador já avaliou este bugueiro");
        var avaliacao = new Avaliacao();
        copiarDados(avaliacaoRequest, avaliacao);
        avaliacao.setAvaliador(usuarioService.get(avaliacaoRequest.avaliadorId()));
        avaliacao.setBugueiro(usuarioService.get(avaliacaoRequest.bugueiroId()));
        var avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        logger.info("Review created reviewId={} reviewerId={} buggyDriverId={}",
                avaliacaoSalva.getId(), avaliacaoRequest.avaliadorId(), avaliacaoRequest.bugueiroId());
        return toResponse(avaliacaoSalva);
    }

    @Transactional
    public AvaliacaoResponse atualizar(
            Long avaliacaoId, AvaliacaoRequest avaliacaoRequest,
            Long authenticatedUserId, boolean isAdministrator) {
        var avaliacao = buscarAvaliacaoPorId(avaliacaoId);
        if (!isAdministrator && !avaliacao.getAvaliador().getId().equals(authenticatedUserId))
            throw new org.springframework.security.access.AccessDeniedException("Somente o avaliador ou ADMIN pode alterar");
        validarParticipantes(avaliacaoRequest);
        if (!avaliacao.getAvaliador().getId().equals(avaliacaoRequest.avaliadorId())
                || !avaliacao.getBugueiro().getId().equals(avaliacaoRequest.bugueiroId()))
            if (avaliacaoRepository.existsByAvaliadorIdAndBugueiroId(
                    avaliacaoRequest.avaliadorId(), avaliacaoRequest.bugueiroId()))
                throw new ConflictException("Já existe avaliação para este par");
        copiarDados(avaliacaoRequest, avaliacao);
        avaliacao.setAvaliador(usuarioService.get(avaliacaoRequest.avaliadorId()));
        avaliacao.setBugueiro(usuarioService.get(avaliacaoRequest.bugueiroId()));
        var avaliacaoSalva = avaliacaoRepository.save(avaliacao);
        logger.info("Review updated reviewId={} reviewerId={} buggyDriverId={}",
                avaliacaoSalva.getId(), avaliacaoRequest.avaliadorId(), avaliacaoRequest.bugueiroId());
        return toResponse(avaliacaoSalva);
    }

    @Transactional
    public void remover(Long avaliacaoId, Long authenticatedUserId, boolean isAdministrator) {
        var avaliacao = buscarAvaliacaoPorId(avaliacaoId);
        if (!isAdministrator && !avaliacao.getAvaliador().getId().equals(authenticatedUserId))
            throw new org.springframework.security.access.AccessDeniedException("Somente o avaliador ou ADMIN pode remover");
        avaliacaoRepository.delete(avaliacao);
        logger.info("Review deleted reviewId={} reviewerId={}", avaliacaoId, avaliacao.getAvaliador().getId());
    }

    @Transactional(readOnly = true)
    public AvaliacaoResponse buscar(Long avaliacaoId) {
        return toResponse(buscarAvaliacaoPorId(avaliacaoId));
    }

    @Transactional(readOnly = true)
    public PageResponse<AvaliacaoResponse> listar(Long bugueiroId, Pageable pageable) {
        var avaliacoesPage = (bugueiroId == null
                ? avaliacaoRepository.findAll(pageable)
                : avaliacaoRepository.findByBugueiroId(bugueiroId, pageable)).map(this::toResponse);
        return new PageResponse<>(avaliacoesPage.getContent(), avaliacoesPage.getNumber(), avaliacoesPage.getSize(),
                avaliacoesPage.getTotalElements(), avaliacoesPage.getTotalPages(),
                avaliacoesPage.isFirst(), avaliacoesPage.isLast());
    }

    private Avaliacao buscarAvaliacaoPorId(Long avaliacaoId) {
        return avaliacaoRepository.findById(avaliacaoId)
                .orElseThrow(() -> new NotFoundException("Avaliação não encontrada: " + avaliacaoId));
    }

    private void validarParticipantes(AvaliacaoRequest avaliacaoRequest) {
        var avaliador = usuarioService.get(avaliacaoRequest.avaliadorId());
        var bugueiro = usuarioService.get(avaliacaoRequest.bugueiroId());
        if (avaliador.getId().equals(bugueiro.getId()))
            throw new BusinessRuleException("Avaliador e bugueiro devem ser usuários diferentes");
        if (avaliador.getUsuarioTipo() != EnumUsuario.CLIENTE)
            throw new BusinessRuleException("O avaliador deve possuir tipo CLIENTE");
        if (bugueiro.getUsuarioTipo() != EnumUsuario.BUGUEIRO)
            throw new BusinessRuleException("O avaliado deve possuir tipo BUGUEIRO");
    }

    private void copiarDados(AvaliacaoRequest source, Avaliacao target) {
        target.setSeguranca(source.seguranca());
        target.setConhecimentoRoteiro(source.conhecimentoRoteiro());
        target.setConfortoVeiculo(source.confortoVeiculo());
        target.setSimpatiaMotorista(source.simpatiaMotorista());
        target.setExperienciaGeral(source.experienciaGeral());
        target.setAdaptabilidade(source.adaptabilidade());
        target.setParadasInteressantes(source.paradasInteressantes());
        target.setDiferencial(source.diferencial());
        target.setFeedback(source.feedback());
    }

    private AvaliacaoResponse toResponse(Avaliacao avaliacao) {
        double mediaNotas = (avaliacao.getSeguranca() + avaliacao.getConhecimentoRoteiro()
                + avaliacao.getConfortoVeiculo() + avaliacao.getSimpatiaMotorista()
                + avaliacao.getExperienciaGeral() + avaliacao.getAdaptabilidade()
                + avaliacao.getParadasInteressantes()) / 7.0;
        return new AvaliacaoResponse(
                avaliacao.getId(), avaliacao.getSeguranca(), avaliacao.getConhecimentoRoteiro(),
                avaliacao.getConfortoVeiculo(), avaliacao.getSimpatiaMotorista(), avaliacao.getExperienciaGeral(),
                avaliacao.getAdaptabilidade(), avaliacao.getParadasInteressantes(), avaliacao.getDiferencial(),
                avaliacao.getFeedback(), avaliacao.getAvaliador().getId(), avaliacao.getBugueiro().getId(),
                avaliacao.getDataCriacao(), avaliacao.getDataAtualizacao(), Math.round(mediaNotas * 100) / 100.0);
    }
}
