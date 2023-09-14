package br.com.medeirosecia.analyzemail.domain.service.gmail;

import br.com.medeirosecia.analyzemail.infra.filesystem.LocalFileSystem;
import javafx.concurrent.Task;

public abstract class AnalyzeInboxBase extends Task<Void>{
    private LocalFileSystem localFileSystem;
    protected AnalyzeInboxBase(LocalFileSystem localFileSystem) {
        this.localFileSystem = localFileSystem;
    }
}
