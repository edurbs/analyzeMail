package br.com.medeirosecia.analyzemail.domain.service.gmail;

import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;

public interface AnalyzeInbox {
    Void call() throws Exception;   
}
