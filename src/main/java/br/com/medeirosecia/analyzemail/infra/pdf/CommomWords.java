package br.com.medeirosecia.analyzemail.infra.pdf;

public enum CommomWords {
    PORTUGUES(new String[] { "Não", "Para", "Uma", "Está", "Com", "Por", "Ele", "Mais", "Mas", "Como", "Dos", "Seu",
            "Sua", "Muito", "Nos", "Quando", "Também", "Ainda", "Mesmo", "Bem", "Sem", "Ser", "Ter",
            "Fazer", "Dizer", "Vida", "Tempo", "Ano", "Dia", "Parte", "Lugar", "Pessoa", "Coisa", "Amor",
            "que", "Palavra", "Exemplo", "Coisa", "Temporada", "Momento", "Importante",
            "Informa", "Sistema", "Govern", "Desenvolv", "Comunica", "Traba", "Situa",
            "Dificul", "Condição", "Sociedade", "Econômico", "Necessi", "Relação",
            "Problema", "História", "Formação", "Produção", "Diferente", "Política", "Atividade", "Direção",
            "Muda", "Educa", "Atend", "Segurança", "Comercial", "Construção", "Tecnologia",
            "Cultura", "Mercado", "Criança", "Companhia", "Experiência", "Complemento",
            "Instituição", "Aprend", "Particip", "População",
            "Organiza", "Aplica", "Desenvolv", "Tecnológico", "Preferência",
            "Informática", "Realiza", "Atualiza", "Eletrônico", "Resolução",
            "Relaciona", "Estratégia", "Compreen", "Documento", "Funciona", "Institucional",
            "Inova", "Sustent", "Ambiente", "Respeit", "Conhe", "Opera", "Personalidade",
            "Ação", "Integra", "Responsabilidade", "Resulta", "Acessibilidade", "Satisf",
            "Aperfeiçoa", "Benefício", "Colaboração", "Exploração", "Profissional",
            "Administra", "Solução", "Competência", "Processa",
            "Produtiv", "Implementa", "Oportunidade", "Configura",
            "Disponi", "Abord", "Excelência", "Benefí", "Cartei", "valor", "Banco", "Venci", "Númer", "qualquer",
            "cobra", "data", "forma", "nosso", "atras", "descont", "multa", "paga"});

    private String[] words;

    private CommomWords(String[] words) {
        this.words = words;
    }

    public String[] getCommomWords() {
        return words;
    }
}
